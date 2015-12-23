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
package forestry.core.recipes.nei;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.AlleleManager;

/**
 * @author bdew
 */
public class ItemFilterOther extends ItemFilterForestry {

	public ItemFilterOther(Boolean items) {
		super(items);
	}

	@Override
	public boolean matches(ItemStack item) {
		return super.matches(item) && !AlleleManager.alleleRegistry.isIndividual(item);
	}
}
