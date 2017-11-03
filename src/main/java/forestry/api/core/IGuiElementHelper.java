/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.util.function.Function;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IGuiElementLayoutHelper.LayoutFactory;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;

/**
 * A helper interface to add gui elements.
 */
@SideOnly(Side.CLIENT)
public interface IGuiElementHelper {
	/**
	 * Adds a {@link IGuiElement} to the parent of this.
	 */
	void add(IGuiElement element);

	/**
	 * Centers a element to the center of the x axis of the parent element.
	 *
	 * @return the element
	 */
	IGuiElement centerElement(IGuiElement element);

	/**
	 * Creates and adds an item gui element.
	 */
	void addItem(int x, ItemStack itemStack);

	IGuiElementLayoutHelper layoutHelper(LayoutFactory layoutFactory, int width, int height);

	/**
	 * Adds the chromosomeName and the name of the active/not active allele, of the chromosome, with {@link #addText}.
	 */
	void addAllele(String chromosomeName, IIndividual individual, IChromosomeType chromosome, boolean active);

	/**
	 * Adds the chromosomeName and the result of toString with {@link #addText}.
	 */
	<A extends IAllele> void addAllele(String chromosomeName, Function<A, String> toString, IIndividual individual, IChromosomeType chromosome, boolean active);

	void addFertilityInfo(IAlleleInteger fertilityAllele, int x, int texOffset);

	void addToleranceInfo(IAlleleTolerance toleranceAllele, IAlleleSpecies species, String text);

	void addMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker);

	void addMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker);

	/**
	 * Adds a text element with the default color,the align {@link GuiElementAlignment#LEFT} and the height 12.
	 */
	void addText(String text);

	/**
	 * Adds a text element with the align {@link GuiElementAlignment#LEFT} and the height 12.
	 */
	void addText(String text, int color);

	/**
	 * Adds a text element with the default color and the height 12.
	 */
	void addText(String text, GuiElementAlignment align);

	/**
	 * Adds a text element with the height 12.
	 */
	void addText(String text,GuiElementAlignment align, int color);

	/**
	 * Adds a text element.
	 */
	void addText(int x, int height, String text, GuiElementAlignment align, int color);

	/**
	 * @return The element to which all elements are added.
	 */
	IGuiElement getParent();

	IGuiElementFactory factory();
}
