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
import net.minecraft.entity.player.EntityPlayer;
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
		try {
			return armorApiarist.protectEntity(entity, stack, cause, doProtect);
		} catch (Throwable ignored) { // protectEntity is new to the API and may not be implemented by the armor
			if (entity instanceof EntityPlayer) {
				return armorApiarist.protectPlayer((EntityPlayer) entity, stack, cause, doProtect);
			} else {
				return true; // fallback on assuming IArmorApiarist will protect the entity
			}
		}
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

	@Override
	public boolean isArmorApiarist(ItemStack stack, EntityPlayer player, String cause, boolean doProtect) {
		return isArmorApiarist(stack, (EntityLivingBase) player, cause, doProtect);
	}

	@Override
	public int wearsItems(EntityPlayer player, String cause, boolean doProtect) {
		return wearsItems((EntityLivingBase) player, cause, doProtect);
	}
}
