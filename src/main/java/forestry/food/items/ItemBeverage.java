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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

		void registerIcons(IIconRegister register);

		IIcon getIconBottle();

		IIcon getIconContents();
	}

	public final IBeverageInfo[] beverages;

	public ItemBeverage() {
		super(1, 0.2f);
		setMaxStackSize(8);
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
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		List<IBeverageEffect> effects = BeverageEffect.loadEffects(itemstack);
		ItemStack container = new ItemStack(Items.glass_bottle, 1);

		itemstack.stackSize--;
		if (!entityplayer.inventory.addItemStackToInventory(container)) {
				entityplayer.dropPlayerItemWithRandomChoice(container, false);
		} else {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, container);
		}
		entityplayer.inventory.markDirty();

		entityplayer.getFoodStats().func_151686_a(this, itemstack);
		world.playSoundAtEntity(entityplayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		if (world.isRemote) {
			return itemstack;
		}

		for (IBeverageEffect effect : effects) {
			effect.doEffect(world, entityplayer);
		}

		return itemstack;
	}

	@Override
	public int func_150905_g(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		IBeverageInfo beverage = beverages[meta];
		return beverage.getHeal();
	}

	@Override
	public float func_150906_h(ItemStack itemstack) {
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
		return EnumAction.drink;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		int meta = itemstack.getItemDamage();
		IBeverageInfo beverage = beverages[meta];

		if (entityplayer.canEat(beverage.isAlwaysEdible())) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < beverages.length; i++) {
			if (Config.isDebug || !beverages[i].isSecret()) {
				itemList.add(new ItemStack(this, 1, i));
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		List<IBeverageEffect> effects = BeverageEffect.loadEffects(itemstack);

		for (IBeverageEffect effect : effects) {
			if (effect.getDescription() != null) {
				list.add(effect.getDescription());
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + beverages[stack.getItemDamage()].getName();
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		for (IBeverageInfo info : beverages) {
			info.registerIcons(register);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int j) {
		if (j > 0 && beverages[i].getSecondaryColor() != 0) {
			return beverages[i].getIconBottle();
		} else {
			return beverages[i].getIconContents();
		}
	}

	// Return true to enable color overlay
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {

		if (j == 0 || beverages[itemstack.getItemDamage()].getSecondaryColor() == 0) {
			return beverages[itemstack.getItemDamage()].getPrimaryColor();
		} else {
			return beverages[itemstack.getItemDamage()].getSecondaryColor();
		}
	}

	public ItemStack get(EnumBeverage beverage, int amount) {
		return new ItemStack(this, amount, beverage.ordinal());
	}
}
