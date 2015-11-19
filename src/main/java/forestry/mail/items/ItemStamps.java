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
package forestry.mail.items;

import net.minecraft.item.ItemStack;

import forestry.api.mail.EnumPostage;
import forestry.api.mail.IStamps;
import forestry.core.CreativeTabForestry;
import forestry.core.items.ItemOverlay;

public class ItemStamps extends ItemOverlay implements IStamps {
	public ItemStamps() {
		super(CreativeTabForestry.tabForestry, EnumStampDefinition.VALUES);
	}

	@Override
	public EnumPostage getPostage(ItemStack itemstack) {
		if (itemstack.getItem() != this) {
			return EnumPostage.P_0;
		}

		if (itemstack.getItemDamage() < 0 || itemstack.getItemDamage() >= EnumStampDefinition.VALUES.length) {
			return EnumPostage.P_0;
		}

		return EnumStampDefinition.VALUES[itemstack.getItemDamage()].getPostage();
	}

	public ItemStack get(EnumStampDefinition stampInfo, int amount) {
		return new ItemStack(this, amount, stampInfo.ordinal());
	}
}
