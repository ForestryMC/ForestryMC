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

import forestry.api.apiculture.IArmorApiarist;
import forestry.api.apiculture.IArmorApiaristHelper;

public class ArmorApiaristHelper implements IArmorApiaristHelper {

	@Override
	public boolean isArmorApiarist(ItemStack stack, EntityLivingBase entity, String cause, boolean doProtect) {
		if (stack == null) {
			return false;
		}

		Item item = stack.getItem();
		if (!(item instanceof IArmorApiarist)) {
			return false;
		}

		IArmorApiarist armorApiarist = (IArmorApiarist) item;
		return armorApiarist.protectEntity(entity, stack, cause, doProtect);
	}

	@Override
	public int wearsItems(EntityLivingBase entity, String cause, boolean doProtect) {
		int count = 0;

		for (int i = 1; i <= 4; i++) {
			ItemStack armorItem = entity.getEquipmentInSlot(i);
			if (isArmorApiarist(armorItem, entity, cause, doProtect)) {
				count++;
			}
		}

		return count;
	}
}
