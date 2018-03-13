package forestry.core.gui.elements;

import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import forestry.api.core.GuiElementAlignment;
import forestry.api.core.IGuiElement;
import forestry.api.core.IGuiElementFactory;
import forestry.api.core.IGuiElementHelper;
import forestry.api.core.IGuiElementLayout;
import forestry.api.core.IGuiElementLayoutHelper;
import forestry.api.core.IGuiElementLayoutHelper.LayoutFactory;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.core.render.ColourProperties;

public class GuiElementHelper implements IGuiElementHelper {
	private final IGuiElementLayout parent;
	private final int defaultColor;

	public GuiElementHelper(IGuiElementLayout parent) {
		this.parent = parent;
		this.defaultColor = ColourProperties.INSTANCE.get("gui.screen");
	}

	public GuiElementLayoutHelper layoutHelper(LayoutFactory layoutFactory, int width, int height) {
		return new GuiElementLayoutHelper(layoutFactory, width, height, this);
	}

	public void add(IGuiElementLayoutHelper groupHelper) {
		groupHelper.layouts().forEach(element -> add(element));
	}

	public void add(IGuiElement element) {
		parent.addElement(element);
	}

	public void addItem(int x, ItemStack itemStack) {
		parent.addElement(new GuiElementItemStack(x, 0, itemStack));
	}

	@Override
	public final void addAllele(String chromosomeName, IIndividual individual, IChromosomeType chromosome, boolean active) {
		addAllele(chromosomeName, allele -> allele.getAlleleName(), individual, chromosome, active);
	}

	@Override
	public final <A extends IAllele> void addAllele(String chromosomeName, Function<A, String> toString, IIndividual individual, IChromosomeType chromosome, boolean active) {
		A allele;
		if (active) {
			allele = (A) individual.getGenome().getActiveAllele(chromosome);
		} else {
			allele = (A) individual.getGenome().getInactiveAllele(chromosome);
		}
		addText(TextFormatting.UNDERLINE + chromosomeName, GuiElementAlignment.CENTER);
		addText(toString.apply(allele), GuiElementAlignment.CENTER, factory().getColorCoding(allele.isDominant()));
	}

	@Override
	public void addMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker) {
		IGuiElement element = factory().createMutation(x, y, width, height, mutation, species, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	@Override
	public void addMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker) {
		IGuiElement element = factory().createMutationResultant(x, y, width, height, mutation, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	public void addFertilityInfo(IAlleleInteger fertilityAllele, int x, int texOffset) {
		add(centerElement(factory().createFertilityInfo(fertilityAllele, x, texOffset)));
	}

	public void addToleranceInfo(IAlleleTolerance toleranceAllele, IAlleleSpecies species, String text) {
		add(centerElement(factory().createToleranceInfo(toleranceAllele, species, text)));
	}

	public void addText(String text) {
		addText(0, text, defaultColor);
	}

	public void addText(String text, int color) {
		addText(0, text, color);
	}

	@Override
	public void addText(String text, GuiElementAlignment align) {
		addText(text, align, defaultColor);
	}

	public void addText(String text, GuiElementAlignment align, int color) {
		addText(0, text, align, color);
	}

	public void addText(int x, String text, int color) {
		addText(x, text, GuiElementAlignment.LEFT, color);
	}

	public void addText(int x, String text, GuiElementAlignment align, int color) {
		addText(x, 12, text, align, color);
	}

	public void addText(int x, int height, String text, GuiElementAlignment align, int color) {
		parent.addElement(new GuiElementText(x, 0, parent.getWidth(), height, text, align, color));
	}

	@Override
	public IGuiElement centerElement(IGuiElement element) {
		element.setXOffset((parent.getWidth() - element.getWidth()) / 2);
		return element;
	}

	@Override
	public IGuiElementLayout getParent() {
		return parent;
	}

	@Override
	public IGuiElementFactory factory() {
		return GuiElementFactory.INSTANCE;
	}
}
