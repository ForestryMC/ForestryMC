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

import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;

import codechicken.nei.api.ItemFilter;

/**
 * @author bdew
 */
public class ItemFilterGenetic implements ItemFilter {
	private final ISpeciesRoot root;
	private final int type;
	private final boolean analyzedOnly;

	public ItemFilterGenetic(ISpeciesRoot root, int type, boolean analyzedOnly) {
		this.root = root;
		this.type = type;
		this.analyzedOnly = analyzedOnly;
	}

	private boolean isMember(ItemStack item) {
		if (type >= 0) {
			return root.isMember(item, type);
		} else {
			return root.isMember(item);
		}
	}

	@Override
	public boolean matches(ItemStack item) {
		if (analyzedOnly) {
			if (!isMember(item)) {
				return false;
			}
			IIndividual individual = root.getMember(item);
			return individual.isAnalyzed() || !individual.isSecret();
		} else {
			return isMember(item);
		}
	}
}
