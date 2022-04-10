/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.items;

import javax.annotation.Nullable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import forestry.api.core.ItemGroups;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.definitions.DrinkProperties;
import forestry.core.items.definitions.EnumContainerType;
import forestry.core.items.definitions.FluidHandlerItemForestry;
import forestry.core.utils.Translator;

public class ItemFluidContainerForestry extends ItemForestry {
	private final EnumContainerType type;

	public ItemFluidContainerForestry(EnumContainerType type) {
		super(new Item.Properties().tab(ItemGroups.tabStorage));
		this.type = type;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.allowdedIn(tab)) {
			// empty
			subItems.add(new ItemStack(this));

			// filled
			for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
				if (fluid instanceof FlowingFluid && ((FlowingFluid) fluid).getSource() != fluid) {
					continue;
				}
				ItemStack itemStack = new ItemStack(this);
				IFluidHandlerItem fluidHandler = new FluidHandlerItemForestry(itemStack, type);
				if (fluidHandler.fill(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE) == FluidAttributes.BUCKET_VOLUME) {
					ItemStack filled = fluidHandler.getContainer();
					subItems.add(filled);
				}
			}
		}
	}

	public EnumContainerType getType() {
		return type;
	}

	protected FluidStack getContained(ItemStack itemStack) {
		if (itemStack.getCount() != 1) {
			itemStack = itemStack.copy();
			itemStack.setCount(1);
		}
		IFluidHandler fluidHandler = new FluidHandlerItemForestry(itemStack, type);
		return fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemFluidContainerForestry) {
			FluidStack fluid = getContained(stack);
			if (!fluid.isEmpty()) {
				String exactTranslationKey = Constants.TRANSLATION_KEY_ITEM + type.getSerializedName() + '.' + fluid.getFluid().getRegistryName();
				return Translator.tryTranslate(exactTranslationKey, () -> {
							String grammarKey = Constants.TRANSLATION_KEY_ITEM + type.getSerializedName() + ".grammar";
							return new TranslationTextComponent(grammarKey, fluid.getDisplayName());
						});
			} else {
				String unlocalizedname = Constants.TRANSLATION_KEY_ITEM + type.getSerializedName() + ".empty";
				return new TranslationTextComponent(unlocalizedname);
			}
		}
		return super.getName(stack);
	}

	/**
	 * DRINKS
	 */
	@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		DrinkProperties drinkProperties = getDrinkProperties(stack);
		if (drinkProperties != null) {
			if (entityLiving instanceof PlayerEntity && !((PlayerEntity) entityLiving).isCreative()) {
				PlayerEntity player = (PlayerEntity) entityLiving;
				if (!player.abilities.instabuild) {
					stack.shrink(1);
				}

				if (!worldIn.isClientSide) {
					FoodStats foodStats = player.getFoodData();
					foodStats.eat(drinkProperties.getHealAmount(), drinkProperties.getSaturationModifier());
					worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
				}

				player.awardStat(Stats.ITEM_USED.get(this));
			}
		}
		return stack;
	}

	@Nullable
	protected DrinkProperties getDrinkProperties(ItemStack itemStack) {
		FluidStack contained = getContained(itemStack);
		if (!contained.isEmpty()) {
			ForestryFluids definition = ForestryFluids.getFluidDefinition(contained);
			if (definition != null) {
				return definition.getDrinkProperties();
			}
		}
		return null;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		DrinkProperties drinkProperties = getDrinkProperties(itemstack);
		if (drinkProperties != null) {
			return drinkProperties.getMaxItemUseDuration();
		} else {
			return super.getUseDuration(itemstack);
		}
	}

	@Override
	public UseAction getUseAnimation(ItemStack itemstack) {
		DrinkProperties drinkProperties = getDrinkProperties(itemstack);
		if (drinkProperties != null) {
			return UseAction.DRINK;
		} else {
			return UseAction.NONE;
		}
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand handIn) {
		ItemStack heldItem = player.getItemInHand(handIn);
		DrinkProperties drinkProperties = getDrinkProperties(heldItem);
		if (drinkProperties != null) {
			if (player.canEat(false)) {
				player.startUsingItem(handIn);
				return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
			} else {
				return new ActionResult<>(ActionResultType.FAIL, heldItem);
			}
		} else {
			if (Config.CapsuleFluidPickup) {
				BlockRayTraceResult target = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
				if (target.getType() != RayTraceResult.Type.BLOCK) {
					return ActionResult.pass(heldItem);
				}
				ItemStack singleBucket = heldItem.copy();
				singleBucket.setCount(1);

				FluidActionResult filledResult = FluidUtil.tryPickUpFluid(singleBucket, player, world, target.getBlockPos(), target.getDirection());
				if (filledResult.isSuccess()) {
					ItemHandlerHelper.giveItemToPlayer(player, filledResult.result);

					if (!player.isCreative()) {
						// Remove consumed empty container
						heldItem.shrink(1);
					}

					return ActionResult.success(heldItem);
				}
			}
			return super.use(world, player, handIn);
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new FluidHandlerItemForestry(stack, type);
	}
}
