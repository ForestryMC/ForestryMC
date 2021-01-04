/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.core.gui.elements.lib;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.api.genetics.gatgets.IGeneticAnalyzer;
import forestry.api.genetics.gatgets.IGeneticAnalyzerProvider;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.mutation.IMutation;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * A helper interface to create gui elements.
 */
@OnlyIn(Dist.CLIENT)
public interface IGuiElementFactory {

    IGeneticAnalyzer createAnalyzer(IWindowElement window, int xPos, int yPos, boolean rightBoarder, IGeneticAnalyzerProvider provider);

    /* GENETIC*/

    /**
     * @param dominant True if you want the dominant color and false if you want the recessive color.
     * @return The color code that forestry uses to show if a allele is dominant (true) or recessive (false).
     */
    int getColorCoding(boolean dominant);

    Style getStateStyle(boolean dominant);

    Style getGuiStyle();

    /**
     * @return Null if the mutation is secret and undiscovered. {@link IMutation#isSecret()}
     */
    @Nullable
    IGuiElement createMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker);

    /**
     * @return Null if the mutation is secret and undiscovered. {@link IMutation#isSecret()}
     */
    @Nullable
    IGuiElement createMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker);

    IGuiElement createFertilityInfo(IAlleleValue<Integer> fertilityAllele, int texOffset);

    IGuiElement createToleranceInfo(IAlleleValue<EnumTolerance> toleranceAllele, IAlleleForestrySpecies species, String text);

    IGuiElement createToleranceInfo(IAlleleValue<EnumTolerance> toleranceAllele);

    /* LAYOUTS */
    IElementLayout createHorizontal(int xPos, int yPos, int height);

    IElementLayout createVertical(int xPos, int yPos, int width);

    IElementGroup createPane(int xPos, int yPos, int width, int height);
}
