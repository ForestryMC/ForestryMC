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

import net.minecraft.item.ItemStack;

import forestry.api.core.Tabs;
import forestry.core.items.ItemOverlay;

public class ItemPropolis extends ItemOverlay {
	public ItemPropolis() {
		super(Tabs.tabApiculture, EnumPropolis.VALUES);
	}

	public ItemStack get(EnumPropolis propolis, int amount) {
		return new ItemStack(this, amount, propolis.ordinal());
	}
}
