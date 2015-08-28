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
package forestry.apiculture.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;


import forestry.api.apiculture.IArmorApiarist;
import forestry.api.core.IArmorNaturalist;
import forestry.api.core.Tabs;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemArmorApiarist extends ItemArmor implements IArmorApiarist, IArmorNaturalist {

	public ItemArmorApiarist(int slot) {
		super(ArmorMaterial.LEATHER, 0, slot);
		this.setMaxDamage(100);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (ForestryItem.apiaristLegs.isItemEqual(stack)) {
			return Defaults.ID + ":" + Defaults.TEXTURE_APIARIST_ARMOR_SECONDARY;
		} else {
			return Defaults.ID + ":" + Defaults.TEXTURE_APIARIST_ARMOR_PRIMARY;
		}
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		return 0xffffff;
	}

	@Override
	public boolean hasColor(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean protectPlayer(EntityPlayer player, ItemStack armor, String cause, boolean doProtect) {
		return true;
	}

	@Override
	public boolean canSeePollination(EntityPlayer player, ItemStack armor, boolean doSee) {
		return armorType == 0;
	}

	private static boolean isArmorApiarist(ItemStack itemStack, EntityPlayer player, String cause, boolean protect) {
		if (itemStack == null) {
			return false;
		}

		Item item = itemStack.getItem();
		if (!(item instanceof IArmorApiarist)) {
			return false;
		}

		return ((IArmorApiarist) item).protectPlayer(player, itemStack, cause, protect);
	}

	public static int wearsItems(EntityPlayer player, String cause, boolean protect) {
		int count = 0;

		for (ItemStack armorItem : player.inventory.armorInventory) {
			if (isArmorApiarist(armorItem, player, cause, protect)) {
				count++;
			}
		}

		return count;
	}

}
