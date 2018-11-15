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
package forestry.apiculture;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.apiculture.IArmorApiaristHelper;

public class ArmorApiaristHelper implements IArmorApiaristHelper {

	@Override
	public boolean isArmorApiarist(ItemStack stack, EntityLivingBase entity, String cause, boolean doProtect) {
		if (stack.isEmpty()) {
			return false;
		}

		final Item item = stack.getItem();
		final IArmorApiarist armorApiarist;
		if (item instanceof IArmorApiarist) { // legacy
			armorApiarist = (IArmorApiarist) item;
		} else if (stack.hasCapability(ApicultureCapabilities.ARMOR_APIARIST, null)) {
			armorApiarist = stack.getCapability(ApicultureCapabilities.ARMOR_APIARIST, null);
		} else {
			return false;
		}

		return armorApiarist != null && armorApiarist.protectEntity(entity, stack, cause, doProtect);
	}

	@Override
	public int wearsItems(EntityLivingBase entity, String cause, boolean doProtect) {
		int count = 0;

		for (ItemStack armorItem : entity.getEquipmentAndArmor()) {
			if (isArmorApiarist(armorItem, entity, cause, doProtect)) {
				count++;
			}
		}

		return count;
	}
}
