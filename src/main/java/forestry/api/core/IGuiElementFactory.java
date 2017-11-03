/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IMutation;

/**
 * A helper interface to create gui elements.
 */
@SideOnly(Side.CLIENT)
public interface IGuiElementFactory {

	IGuiElementHelper createHelper(IGuiElementLayout element);

	/* GENETIC*/
	/**
	 * @param  dominant True if you want the dominant color and false if you want the recessive color.
	 *
	 * @return The color code that forestry uses to show if a allele is dominant (true) or recessive (false).
	 */
	int getColorCoding(boolean dominant);

	/**
	 * @return Null if the mutation is secret and undiscovered. {@link IMutation#isSecret()}
	 */
	IGuiElement createMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker);

	/**
	 * @return Null if the mutation is secret and undiscovered. {@link IMutation#isSecret()}
	 */
	IGuiElement createMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker);

	IGuiElement createFertilityInfo(IAlleleInteger fertilityAllele, int x, int texOffset);

	IGuiElement createToleranceInfo(IAlleleTolerance toleranceAllele, IAlleleSpecies species, String text);

	/* LAYOUTS */
	IGuiElementLayout createHorizontal(int xPos, int yPos, int height);

	IGuiElementLayout createVertical(int xPos, int yPos, int width);

	IGuiElementLayout createPanel(int xPos, int yPos, int width, int height);
}
