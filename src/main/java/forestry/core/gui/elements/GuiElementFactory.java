package forestry.core.gui.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.api.core.IGuiElement;
import forestry.api.core.IGuiElementFactory;
import forestry.api.core.IGuiElementHelper;
import forestry.api.core.IGuiElementLayout;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesPlugin;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.gui.Drawable;
import forestry.core.render.ColourProperties;

public class GuiElementFactory implements IGuiElementFactory {
	public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/database_mutation_screen.png");

	public static final Drawable QUESTION_MARK = new Drawable(TEXTURE, 78, 240, 16, 16);
	public static final Drawable DOWN_SYMBOL = new Drawable(TEXTURE, 0, 247, 15, 9);
	public static final Drawable UP_SYMBOL = new Drawable(TEXTURE, 15, 247, 15, 9);
	public static final Drawable BOOTH_SYMBOL = new Drawable(TEXTURE, 30, 247, 15, 9);
	public static final Drawable NONE_SYMBOL = new Drawable(TEXTURE, 45, 247, 15, 9);

	public static final GuiElementFactory INSTANCE = new GuiElementFactory();

	private GuiElementFactory() {
	}


	@Override
	public IGuiElementHelper createHelper(IGuiElementLayout element) {
		return new GuiElementHelper(element);
	}

	@Override
	public IGuiElementLayout createVertical(int xPos, int yPos, int width) {
		return new GuiElementVertical(xPos, yPos, width);
	}

	@Override
	public IGuiElementLayout createHorizontal(int xPos, int yPos, int height) {
		return new GuiElementHorizontal(xPos, yPos, height);
	}

	@Override
	public IGuiElementLayout createPanel(int xPos, int yPos, int width, int height) {
		return new GuiElementPanel(xPos, yPos, width, height);
	}

	public final int getColorCoding(boolean dominant) {
		if (dominant) {
			return ColourProperties.INSTANCE.get("gui.beealyzer.dominant");
		} else {
			return ColourProperties.INSTANCE.get("gui.beealyzer.recessive");
		}
	}

	public IGuiElement createFertilityInfo(IAlleleInteger fertilityAllele, int x, int texOffset) {
		String fertilityString = Integer.toString(fertilityAllele.getValue()) + " x";

		IGuiElementLayout layout = createHorizontal(x, 0, 0).setDistance(2);
		layout.addElement(new GuiElementText(0, 0, 12, fertilityString, getColorCoding(fertilityAllele.isDominant())));
		layout.addElement(new GuiElementDrawable(0, -1, new Drawable(TEXTURE, 60, 240 + texOffset, 12, 8)));
		return layout;
	}

	public IGuiElement createToleranceInfo(IAlleleTolerance toleranceAllele, IAlleleSpecies species, String text) {
		IGuiElementLayout layout = createHorizontal(0, 0, 0).setDistance(0);
		layout.addElement(new GuiElementText(0, 0, 12, text, getColorCoding(species.isDominant())));
		layout.addElement(createToleranceInfo(toleranceAllele, 0));
		return layout;
	}

	private IGuiElementLayout createToleranceInfo(IAlleleTolerance toleranceAllele, int x) {
		int textColor = getColorCoding(toleranceAllele.isDominant());
		EnumTolerance tolerance = toleranceAllele.getValue();
		String text = "(" + toleranceAllele.getAlleleName() + ")";

		IGuiElementLayout layout = createHorizontal(x, 0, 0).setDistance(2);

		switch (tolerance) {
			case BOTH_1:
			case BOTH_2:
			case BOTH_3:
			case BOTH_4:
			case BOTH_5:
				layout.addElement(createBothSymbol(0, -1));
				layout.addElement(new GuiElementText(0, 0, 12, text, textColor));
				break;
			case DOWN_1:
			case DOWN_2:
			case DOWN_3:
			case DOWN_4:
			case DOWN_5:
				layout.addElement(createDownSymbol(0, -1));
				layout.addElement(new GuiElementText(0, 0, 12, text, textColor));
				break;
			case UP_1:
			case UP_2:
			case UP_3:
			case UP_4:
			case UP_5:
				layout.addElement(createUpSymbol(0, -1));
				layout.addElement(new GuiElementText(0, 0, 12, text, textColor));
				break;
			default:
				layout.addElement(createNoneSymbol(0, -1));
				layout.addElement(new GuiElementText(0, 0, 12, "(0)", textColor));
				break;
		}
		return layout;
	}

	public IGuiElementLayout createMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker) {
		if (breedingTracker.isDiscovered(mutation)) {
			IGuiElementLayout element = new GuiElementPanel(x, y, width, height);
			IAlyzerPlugin plugin = mutation.getRoot().getAlyzerPlugin();
			Map<String, ItemStack> iconStacks = plugin.getIconStacks();

			ItemStack firstPartner = iconStacks.get(mutation.getAllele0().getUID());
			ItemStack secondPartner = iconStacks.get(mutation.getAllele1().getUID());
			element.addElements(new GuiElementItemStack(0, 0, firstPartner), createProbabilityAdd(mutation, 21, 4), new GuiElementItemStack(33, 0, secondPartner));
			return element;
		}
		// Do not display secret undiscovered mutations.
		if (mutation.isSecret()) {
			return null;
		}

		return createUnknownMutationGroup(x, y, width, height, mutation);
	}

	public IGuiElementLayout createMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker) {
		if (breedingTracker.isDiscovered(mutation)) {
			GuiElementPanel element = new GuiElementPanel(x, y, width, height);
			ISpeciesRoot speciesRoot = mutation.getRoot();
			int speciesIndex = speciesRoot.getSpeciesChromosomeType().ordinal();
			ISpeciesPlugin plugin = mutation.getRoot().getSpeciesPlugin();
			Map<String, ItemStack> iconStacks = plugin.getIndividualStacks();

			ItemStack partner = iconStacks.get(mutation.getPartner(species).getUID());
			IAllele resultAllele = mutation.getTemplate()[speciesIndex];
			ItemStack result = iconStacks.get(resultAllele.getUID());
			element.addElements(new GuiElementItemStack(0, 0, partner), new GuiElementItemStack(33, 0, result));
			element.addElements(createProbabilityArrow(mutation, 18, 4, breedingTracker));
			return element;
		}
		// Do not display secret undiscovered mutations.
		if (mutation.isSecret()) {
			return null;
		}

		return createUnknownMutationGroup(x, y, width, height, mutation, breedingTracker);
	}

	private static IGuiElementLayout createUnknownMutationGroup(int x, int y, int width, int height, IMutation mutation) {
		GuiElementPanel element = new GuiElementPanel(x, y, width, height);

		element.addElements(createQuestionMark(0, 0), createProbabilityAdd(mutation, 21, 4), createQuestionMark(32, 0));
		return element;
	}

	private static IGuiElementLayout createUnknownMutationGroup(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker) {
		GuiElementPanel element = new GuiElementPanel(x, y, width, height);

		element.addElements(createQuestionMark(0, 0), createQuestionMark(32, 0));
		element.addElements(createProbabilityArrow(mutation, 18, 4, breedingTracker));
		return element;
	}

	private static Collection<IGuiElement> createProbabilityArrow(IMutation combination, int x, int y, IBreedingTracker breedingTracker) {
		float chance = combination.getBaseChance();
		int line = 247;
		int column = 100;
		switch (EnumMutateChance.rateChance(chance)) {
			case HIGHEST:
				column = 100;
				break;
			case HIGHER:
				column = 100 + 15;
				break;
			case HIGH:
				column = 100 + 15 * 2;
				break;
			case NORMAL:
				column = 100 + 15 * 3;
				break;
			case LOW:
				column = 100 + 15 * 4;
				break;
			case LOWEST:
				column = 100 + 15 * 5;
			default:
				break;
		}

		List<IGuiElement> elements = new ArrayList<>();
		// Probability arrow
		elements.add(new GuiElementDrawable(x, y, new Drawable(TEXTURE, column, line, 15, 9)));

		boolean researched = breedingTracker.isResearched(combination);
		if (researched) {
			elements.add(new GuiElementText(x + 9, y + 1, 10, 10, "+"));
		}

		return elements;
	}

	private static GuiElementDrawable createProbabilityAdd(IMutation mutation, int x, int y) {
		float chance = mutation.getBaseChance();
		int line = 247;
		int column = 190;
		switch (EnumMutateChance.rateChance(chance)) {
			case HIGHEST:
				column = 190;
				break;
			case HIGHER:
				column = 190 + 9;
				break;
			case HIGH:
				column = 190 + 9 * 2;
				break;
			case NORMAL:
				column = 190 + 9 * 3;
				break;
			case LOW:
				column = 190 + 9 * 4;
				break;
			case LOWEST:
				column = 190 + 9 * 5;
			default:
				break;
		}

		// Probability add
		return new GuiElementDrawable(x, y, new Drawable(TEXTURE, column, line, 9, 9));
	}

	private static GuiElementDrawable createQuestionMark(int x, int y) {
		return new GuiElementDrawable(x, y, QUESTION_MARK);
	}

	private static GuiElementDrawable createDownSymbol(int x, int y) {
		return new GuiElementDrawable(x, y, DOWN_SYMBOL);
	}

	private static GuiElementDrawable createUpSymbol(int x, int y) {
		return new GuiElementDrawable(x, y, UP_SYMBOL);
	}

	private static GuiElementDrawable createBothSymbol(int x, int y) {
		return new GuiElementDrawable(x, y, BOOTH_SYMBOL);
	}

	private static GuiElementDrawable createNoneSymbol(int x, int y) {
		return new GuiElementDrawable(x, y, NONE_SYMBOL);
	}
}
