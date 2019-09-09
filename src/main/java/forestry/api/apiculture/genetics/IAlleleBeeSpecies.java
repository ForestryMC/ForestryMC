/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.genetics;

import java.util.Map;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.IModelManager;
import forestry.api.genetics.IAlleleForestrySpecies;

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
	 * @return Map of possible products with the chance for drop each bee cycle. (0 - 1]
	 */
	Map<ItemStack, Float> getProductChances();

	/**
	 * @return Map of possible specialities with the chance for drop each bee cycle. (0 - 1]
	 */
	Map<ItemStack, Float> getSpecialtyChances();

	/**
	 * Only jubilant bees produce specialities.
	 *
	 * @return true if the bee is jubilant, false otherwise.
	 */
	boolean isJubilant(IGenome genome, IBeeHousing housing);

	@OnlyIn(Dist.CLIENT)
	ModelResourceLocation getModel(EnumBeeType type);

	@OnlyIn(Dist.CLIENT)
	void registerModels(Item item, IModelManager manager);
}
