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

import java.util.List;
import java.util.Locale;

import forestry.api.core.EnumContainerType;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.Translator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.Translator;

public class ItemFluidContainerForestry extends ItemForestry {
	private final EnumContainerType type;

	public ItemFluidContainerForestry(EnumContainerType type) {
		super(CreativeTabForestry.tabForestry);
		this.type = type;
	}

	/* Models */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		String identifier = "liquids/" + type.toString().toLowerCase(Locale.ENGLISH);
		manager.registerItemModel(item, 0, identifier + "_empty");
		ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, identifier), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		// empty
		subItems.add(new ItemStack(itemIn));

		// filled
		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			ItemStack itemStack = new ItemStack(itemIn);
			IFluidHandler fluidHandler = new FluidHandlerItemForestry(itemStack, type);
			if (fluidHandler.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true) == Fluid.BUCKET_VOLUME) {
				subItems.add(itemStack);
			}
		}
	}

	public EnumContainerType getType() {
		return type;
	}

	protected FluidStack getContained(ItemStack itemStack) {
		if (itemStack.stackSize != 1) {
			itemStack = itemStack.copy();
			itemStack.stackSize = 1;
		}
		IFluidHandler fluidHandler = new FluidHandlerItemForestry(itemStack, type);
		return fluidHandler.drain(Integer.MAX_VALUE, false);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemFluidContainerForestry) {
			FluidStack fluid = getContained(stack);
			if (fluid != null) {
				String exactTranslationKey = "item.for." + type.getName() + '.' + fluid.getFluid().getName() + ".name";
				if (Translator.canTranslateToLocal(exactTranslationKey)) {
					return Translator.translateToLocal(exactTranslationKey);
				} else {
					String grammarKey = "item.for." + type.getName() + ".grammar";
					return Translator.translateToLocalFormatted(grammarKey, fluid.getLocalizedName());
				}
			} else {
				String unlocalizedname = "item.for." + type.getName() + ".empty.name";
				return Translator.translateToLocal(unlocalizedname);
			}
		}
		return super.getItemStackDisplayName(stack);
	}

	/** DRINKS */
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		DrinkProperties drinkProperties = getDrinkProperties(stack);
		if (drinkProperties != null) {
			if (entityLiving instanceof EntityPlayer && !((EntityPlayer) entityLiving).capabilities.isCreativeMode) {
				EntityPlayer player = (EntityPlayer) entityLiving;
				if (!player.capabilities.isCreativeMode) {
					--stack.stackSize;
				}

				if (!worldIn.isRemote) {
					FoodStats foodStats = player.getFoodStats();
					foodStats.addStats(drinkProperties.getHealAmount(), drinkProperties.getSaturationModifier());
					worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
				}

				player.addStat(StatList.getObjectUseStats(this));
			}
		}
		return stack;
	}

	protected DrinkProperties getDrinkProperties(ItemStack itemStack) {
		FluidStack contained = getContained(itemStack);
		if (contained != null) {
			Fluids definition = Fluids.getFluidDefinition(contained);
			if (definition != null) {
				return definition.getDrinkProperties();
			}
		}
		return null;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		DrinkProperties drinkProperties = getDrinkProperties(itemstack);
		if (drinkProperties != null) {
			return drinkProperties.getMaxItemUseDuration();
		} else {
			return super.getMaxItemUseDuration(itemstack);
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		DrinkProperties drinkProperties = getDrinkProperties(itemstack);
		if (drinkProperties != null) {
			return EnumAction.DRINK;
		} else {
			return EnumAction.NONE;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		DrinkProperties drinkProperties = getDrinkProperties(itemStackIn);
		if (drinkProperties != null) {
			if (playerIn.canEat(false)) {
				playerIn.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
			} else {
				return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
			}
		} else {
			return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		return new FluidHandlerItemForestry(stack, type);
	}
}
