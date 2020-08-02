/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.genetics;

import net.minecraft.client.renderer.model.ModelResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.api.genetics.products.IDynamicProductList;

public interface IAlleleBeeSpecies extends IAlleleForestrySpecies {

    /**
     * @return the IBeeRoot
     */
    @Override
    IBeeRoot getRoot();

    /**
     * @return true if this species is only active at night.
     */
    boolean isNocturnal();

    /**
     * @return List of possible products with the chance for drop each bee cycle. (0 - 1]
     */
    IDynamicProductList getProducts();

    /**
     * @return List of possible specialities with the chance for drop each bee cycle. (0 - 1]
     */
    IDynamicProductList getSpecialties();

    /**
     * Only jubilant bees produce specialities.
     *
     * @return true if the bee is jubilant, false otherwise.
     */
    boolean isJubilant(IGenome genome, IBeeHousing housing);

    @OnlyIn(Dist.CLIENT)
    ModelResourceLocation getModel(EnumBeeType type);
}
