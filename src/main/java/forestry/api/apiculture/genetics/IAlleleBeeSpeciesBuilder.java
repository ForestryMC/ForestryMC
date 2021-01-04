/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.apiculture.genetics;

import forestry.api.apiculture.IBeeModelProvider;
import forestry.api.apiculture.IBeeSpriteColourProvider;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.api.genetics.alleles.IAlleleSpeciesBuilder;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public interface IAlleleBeeSpeciesBuilder extends IAlleleSpeciesBuilder<IAlleleBeeSpeciesBuilder> {

    @Override
    IAlleleBeeSpecies build();

    IAlleleBeeSpeciesBuilder setColour(IBeeSpriteColourProvider colourProvider);

    /**
     * @param primaryColor   The outline color of this species
     * @param secondaryColor The body color of this species
     * @return
     */
    IAlleleBeeSpeciesBuilder setColour(int primaryColor, int secondaryColor);

    /**
     * Add a product for this bee species.
     * Chance is between 0 and 1.
     */
    IAlleleBeeSpeciesBuilder addProduct(Supplier<ItemStack> product, Float chance);

    /**
     * Add a specialty product for this bee species.
     * Bees only produce their specialty when they are Jubilant (see IJubilanceProvider)
     * Chance is between 0 and 1.
     */
    IAlleleBeeSpeciesBuilder addSpecialty(Supplier<ItemStack> specialty, Float chance);

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
