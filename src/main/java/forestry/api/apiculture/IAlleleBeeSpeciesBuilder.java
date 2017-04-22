/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.IAlleleSpeciesBuilder;
import net.minecraft.item.ItemStack;

public interface IAlleleBeeSpeciesBuilder extends IAlleleSpeciesBuilder {

	@Override
	IAlleleBeeSpecies build();

	/**
	 * Add a product for this bee species.
	 * Chance is between 0 and 1.
	 */
	IAlleleBeeSpeciesBuilder addProduct(ItemStack product, Float chance);

	/**
	 * Add a specialty product for this bee species.
	 * Bees only produce their specialty when they are Jubilant (see IJubilanceProvider)
	 * Chance is between 0 and 1.
	 */
	IAlleleBeeSpeciesBuilder addSpecialty(ItemStack specialty, Float chance);

	/**
	 * Set the Jubilance Provider for this bee species.
	 * Bees only produce their specialty when they are Jubilant (see IJubilanceProvider)
	 */
	IAlleleBeeSpeciesBuilder setJubilanceProvider(IJubilanceProvider provider);

	/**
	 * Make this species only active at night.
	 */
	IAlleleBeeSpeciesBuilder setNocturnal();

	/**
	 * Use this if you have custom icons for bees.
	 */
	IAlleleBeeSpeciesBuilder setCustomBeeModelProvider(IBeeModelProvider beeIconProvider);

	/**
	 * Use this if you have custom icon colours for bees (other than the default static primary + secondary colours).
	 */
	IAlleleBeeSpeciesBuilder setCustomBeeSpriteColourProvider(IBeeSpriteColourProvider beeIconColourProvider);
}
