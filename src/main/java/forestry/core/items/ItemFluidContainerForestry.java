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

import deleteme.RegistryNameFinder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
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
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> subItems) {
		if (this.allowedIn(tab)) {
			// empty
			subItems.add(new ItemStack(this));

			// filled
			for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
				if (fluid instanceof FlowingFluid && ((FlowingFluid) fluid).getSource() != fluid) {
					continue;
				}
				ItemStack itemStack = new ItemStack(this);
				IFluidHandlerItem fluidHandler = new FluidHandlerItemForestry(itemStack, type);
				if (fluidHandler.fill(new FluidStack(fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE) == FluidType.BUCKET_VOLUME) {
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
	public Component getName(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemFluidContainerForestry) {
			FluidStack fluid = getContained(stack);
			if (!fluid.isEmpty()) {
				String exactTranslationKey = Constants.TRANSLATION_KEY_ITEM + type.getSerializedName() + '.' + RegistryNameFinder.getRegistryName(fluid.getFluid());
				return Translator.tryTranslate(exactTranslationKey, () -> {
							String grammarKey = Constants.TRANSLATION_KEY_ITEM + type.getSerializedName() + ".grammar";
							return Component.translatable(grammarKey, fluid.getDisplayName());
						});
			} else {
				String unlocalizedname = Constants.TRANSLATION_KEY_ITEM + type.getSerializedName() + ".empty";
				return Component.translatable(unlocalizedname);
			}
		}
		return super.getName(stack);
	}

	/**
	 * DRINKS
	 */
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		DrinkProperties drinkProperties = getDrinkProperties(stack);
		if (drinkProperties != null) {
			if (entityLiving instanceof Player player && !player.isCreative()) {
				if (!player.getAbilities().instabuild) {
					stack.shrink(1);
				}

				if (!worldIn.isClientSide) {
					FoodData foodStats = player.getFoodData();
					foodStats.eat(drinkProperties.getHealAmount(), drinkProperties.getSaturationModifier());
					worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
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
	public UseAnim getUseAnimation(ItemStack itemstack) {
		DrinkProperties drinkProperties = getDrinkProperties(itemstack);
		if (drinkProperties != null) {
			return UseAnim.DRINK;
		} else {
			return UseAnim.NONE;
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand handIn) {
		ItemStack heldItem = player.getItemInHand(handIn);
		DrinkProperties drinkProperties = getDrinkProperties(heldItem);
		if (drinkProperties != null) {
			if (player.canEat(false)) {
				player.startUsingItem(handIn);
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
			} else {
				return new InteractionResultHolder<>(InteractionResult.FAIL, heldItem);
			}
		} else {
			if (Config.CapsuleFluidPickup) {
				BlockHitResult target = getPlayerPOVHitResult(world, player, ClipContext.Fluid.SOURCE_ONLY);
				if (target.getType() != HitResult.Type.BLOCK) {
					return InteractionResultHolder.pass(heldItem);
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

					return InteractionResultHolder.success(heldItem);
				}
			}
			return super.use(world, player, handIn);
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new FluidHandlerItemForestry(stack, type);
	}
}
