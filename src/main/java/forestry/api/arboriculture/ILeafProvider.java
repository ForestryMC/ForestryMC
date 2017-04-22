/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.item.ItemStack;

public interface ILeafProvider {
	
	void init(IAlleleTreeSpecies treeSpecies);

	ItemStack getDecorativeLeaves();
	
}