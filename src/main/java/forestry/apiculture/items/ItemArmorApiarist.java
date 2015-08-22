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

import forestry.api.apiculture.IArmorApiarist;
import forestry.api.core.IArmorNaturalist;
import forestry.api.core.IModelObject;
import forestry.api.core.Tabs;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemArmorApiarist extends ItemArmor implements IArmorApiarist, IArmorNaturalist, IModelObject {

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

	public static boolean wearsHelmet(EntityPlayer player, String cause, boolean protect) {
		ItemStack armorItem = player.inventory.armorInventory[3];
		return armorItem != null && armorItem.getItem() instanceof IArmorApiarist
				&& ((IArmorApiarist) armorItem.getItem()).protectPlayer(player, armorItem, cause, protect);
	}

	public static boolean wearsChest(EntityPlayer player, String cause, boolean protect) {
		ItemStack armorItem = player.inventory.armorInventory[2];
		return armorItem != null && armorItem.getItem() instanceof IArmorApiarist
				&& ((IArmorApiarist) armorItem.getItem()).protectPlayer(player, armorItem, cause, protect);
	}

	public static boolean wearsLegs(EntityPlayer player, String cause, boolean protect) {
		ItemStack armorItem = player.inventory.armorInventory[1];
		return armorItem != null && armorItem.getItem() instanceof IArmorApiarist
				&& ((IArmorApiarist) armorItem.getItem()).protectPlayer(player, armorItem, cause, protect);
	}

	public static boolean wearsBoots(EntityPlayer player, String cause, boolean protect) {
		ItemStack armorItem = player.inventory.armorInventory[0];
		return armorItem != null && armorItem.getItem() instanceof IArmorApiarist
				&& ((IArmorApiarist) armorItem.getItem()).protectPlayer(player, armorItem, cause, protect);
	}

	public static int wearsItems(EntityPlayer player, String cause, boolean protect) {
		int count = 0;

		if (wearsHelmet(player, cause, protect)) {
			count++;
		}
		if (wearsChest(player, cause, protect)) {
			count++;
		}
		if (wearsLegs(player, cause, protect)) {
			count++;
		}
		if (wearsBoots(player, cause, protect)) {
			count++;
		}

		return count;
	}

	@Override
	public ModelType getModelType() {
		return ModelType.DEFAULT;
	}

}
