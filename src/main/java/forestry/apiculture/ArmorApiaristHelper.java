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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.apiculture.IArmorApiaristHelper;

public class ArmorApiaristHelper implements IArmorApiaristHelper {

	@Override
	public boolean isArmorApiarist(ItemStack stack, LivingEntity entity, ResourceLocation cause, boolean doProtect) {
		if (stack.isEmpty()) {
			return false;
		}

		return stack.getCapability(ApicultureCapabilities.ARMOR_APIARIST)
				.map(armorApiarist -> armorApiarist.protectEntity(entity, stack, cause, doProtect))
				.orElse(false);
	}

	@Override
	public int wearsItems(LivingEntity entity, ResourceLocation cause, boolean doProtect) {
		int count = 0;

		for (ItemStack armorItem : entity.getAllSlots()) {
			if (isArmorApiarist(armorItem, entity, cause, doProtect)) {
				count++;
			}
		}

		return count;
	}
}
