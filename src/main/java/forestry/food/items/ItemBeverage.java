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
package forestry.food.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.api.food.IBeverageEffect;
import forestry.core.config.Config;
import forestry.core.items.ItemForestryFood;
import forestry.core.items.ItemOverlay;
import forestry.food.BeverageEffect;

public class ItemBeverage extends ItemForestryFood {
	public interface IBeverageInfo extends ItemOverlay.IOverlayInfo {
		int getHeal();

		float getSaturation();

		boolean isAlwaysEdible();

		void registerModels(Item item, IModelManager manager);
	}

	public final IBeverageInfo[] beverages;

	public ItemBeverage() {
		super(1, 0.2f);
		setMaxStackSize(1);
		this.beverages = EnumBeverage.VALUES;
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		List<IBeverageEffect> effects = BeverageEffect.loadEffects(stack);

		stack.stackSize--;

		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			entityplayer.getFoodStats().addStats(this, stack);
			worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);

			if (worldIn.isRemote) {
				return stack;
			}

			for (IBeverageEffect effect : effects) {
				effect.doEffect(worldIn, entityplayer);
			}

			entityplayer.addStat(StatList.getObjectUseStats(this));
		}

		return stack;
	}

	@Override
	public int getHealAmount(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		IBeverageInfo beverage = beverages[meta];
		return beverage.getHeal();
	}

	@Override
	public float getSaturationModifier(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		IBeverageInfo beverage = beverages[meta];
		return beverage.getSaturation();
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		int meta = itemStackIn.getItemDamage();
		IBeverageInfo beverage = beverages[meta];
		if (playerIn.canEat(beverage.isAlwaysEdible())) {
			playerIn.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
		} else {
			return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		for (int i = 0; i < beverages.length; i++) {
			if (Config.isDebug || !beverages[i].isSecret()) {
				itemList.add(new ItemStack(this, 1, i));
			}
		}
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
		List<IBeverageEffect> effects = BeverageEffect.loadEffects(itemstack);

		for (IBeverageEffect effect : effects) {
			if (effect.getDescription() != null) {
				list.add(effect.getDescription());
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + beverages[stack.getItemDamage()].getUid();
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (IBeverageInfo info : beverages) {
			info.registerModels(item, manager);
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		if (j == 1 || beverages[itemstack.getItemDamage()].getSecondaryColor() == 0) {
			return beverages[itemstack.getItemDamage()].getPrimaryColor();
		} else {
			return beverages[itemstack.getItemDamage()].getSecondaryColor();
		}
	}

	public ItemStack get(EnumBeverage beverage, int amount) {
		return new ItemStack(this, amount, beverage.ordinal());
	}
}
