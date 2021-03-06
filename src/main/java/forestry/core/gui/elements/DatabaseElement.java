package forestry.core.gui.elements;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import forestry.api.genetics.DatabaseMode;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IDatabaseElement;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IElementLayout;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.style.ITextStyle;
import forestry.core.gui.elements.layouts.PaneLayout;
import forestry.core.gui.elements.layouts.VerticalLayout;
import forestry.core.utils.Translator;

public class DatabaseElement extends VerticalLayout implements IDatabaseElement {
	private DatabaseMode mode = DatabaseMode.ACTIVE;
	@Nullable
	private IIndividual individual;
	private int secondColumn = 0;
	private int thirdColumn = 0;

	public DatabaseElement(int width) {
		super(0, 0, width);
		this.secondColumn = width / 2;
	}

	@Override
	public void init(DatabaseMode mode, IIndividual individual, int secondColumn, int thirdColumn) {
		this.mode = mode;
		this.individual = individual;
		this.secondColumn = secondColumn;
		this.thirdColumn = thirdColumn;
	}

	@Nullable
	public IIndividual getIndividual() {
		return individual;
	}

	public IGenome getGenome() {
		Preconditions.checkNotNull(individual, "Database Element has not been initialised.");
		return individual.getGenome();
	}

	@Override
	public void addFertilityLine(String chromosomeName, IChromosomeType chromosome, int texOffset) {
		IGenome genome = getGenome();
		IAllele activeAllele = genome.getActiveAllele(chromosome);
		IAllele inactiveAllele = genome.getInactiveAllele(chromosome);
		if (mode == DatabaseMode.BOTH) {
			if (!(activeAllele instanceof IAlleleInteger) || !(inactiveAllele instanceof IAlleleInteger)) {
				return;
			}
			addLine(chromosomeName, GuiElementFactory.INSTANCE.createFertilityInfo((IAlleleInteger) activeAllele, texOffset), GuiElementFactory.INSTANCE.createFertilityInfo((IAlleleInteger) inactiveAllele, texOffset));
		} else {
			boolean active = mode == DatabaseMode.ACTIVE;
			IAllele allele = active ? activeAllele : inactiveAllele;
			if (!(allele instanceof IAlleleInteger)) {
				return;
			}
			addLine(chromosomeName, GuiElementFactory.INSTANCE.createFertilityInfo((IAlleleInteger) allele, texOffset));
		}
	}

	@Override
	public void addToleranceLine(IChromosomeType chromosome) {
		IAllele allele = getGenome().getActiveAllele(chromosome);
		if (!(allele instanceof IAlleleTolerance)) {
			return;
		}
		addLine("  " + Translator.translateToLocal("for.gui.tolerance"), GuiElementFactory.INSTANCE.createToleranceInfo((IAlleleTolerance) allele));
	}

	@Override
	public void addMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker) {
		IGuiElement element = GuiElementFactory.INSTANCE.createMutation(x, y, width, height, mutation, species, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	@Override
	public void addMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker) {
		IGuiElement element = GuiElementFactory.INSTANCE.createMutationResultant(x, y, width, height, mutation, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	@Override
	public void addLine(String firstText, String secondText, boolean dominant) {
		addLine(firstText, secondText, GuiElementFactory.GUI_STYLE, GuiElementFactory.INSTANCE.getStateStyle(dominant));
	}

	@Override
	public void addLine(String leftText, Function<Boolean, String> toText, boolean dominant) {
		if (mode == DatabaseMode.BOTH) {
			addLine(leftText, toText.apply(true), toText.apply(false), dominant, dominant);
		} else {
			addLine(leftText, toText.apply(mode == DatabaseMode.ACTIVE), dominant);
		}
	}

	/*@Override
	public void addRow(String firstText, String secondText, String thirdText, IIndividual individual, IChromosomeType chromosome) {
		addRow(firstText, secondText, thirdText, GuiElementFactory.GUI_STYLE,
			GuiElementFactory.INSTANCE.getStateStyle(individual.getGenome().getActiveAllele(chromosome).isDominant()),
			GuiElementFactory.INSTANCE.getStateStyle(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
	}*/

	@Override
	public void addLine(String leftText, Function<Boolean, String> toText, IChromosomeType chromosome) {
		IGenome genome = getGenome();
		IAllele activeAllele = genome.getActiveAllele(chromosome);
		IAllele inactiveAllele = genome.getInactiveAllele(chromosome);
		if (mode == DatabaseMode.BOTH) {
			addLine(leftText, toText.apply(true), toText.apply(false), activeAllele.isDominant(), inactiveAllele.isDominant());
		} else {
			boolean active = mode == DatabaseMode.ACTIVE;
			IAllele allele = active ? activeAllele : inactiveAllele;
			addLine(leftText, toText.apply(active), allele.isDominant());
		}
	}

	public void addLine(String firstText, String secondText, String thirdText, boolean secondDominant, boolean thirdDominant) {

	}

	@Override
	public final void addLine(String chromosomeName, IChromosomeType chromosome) {
		addLine(chromosomeName, (allele, b) -> allele.getAlleleName(), chromosome);
	}

	@Override
	public void addLine(String firstText, String secondText, ITextStyle firstStyle, ITextStyle secondStyle) {
		IElementLayout first = addSplitText(width, firstText, GuiElementAlignment.TOP_LEFT, firstStyle);
		IElementLayout second = addSplitText(width, secondText, GuiElementAlignment.TOP_LEFT, secondStyle);
		addLine(first, second);
	}

	private IElementLayout addSplitText(int width, String text, GuiElementAlignment alignment, ITextStyle style) {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		IElementLayout vertical = new VerticalLayout(width);
		for (String splitText : fontRenderer.listFormattedStringToWidth(text, 70)) {
			vertical.label(splitText, alignment, style);
		}
		return vertical;
	}

	private void addLine(String chromosomeName, IGuiElement right) {
		int center = width / 2;
		IGuiElement first = addSplitText(center, chromosomeName, GuiElementAlignment.TOP_LEFT, GuiElementFactory.GUI_STYLE);
		addLine(first, right);
	}

	private void addLine(String chromosomeName, IGuiElement second, IGuiElement third) {
		int center = width / 2;
		IGuiElement first = addSplitText(center, chromosomeName, GuiElementAlignment.TOP_LEFT, GuiElementFactory.GUI_STYLE);
		addLine(first, second, third);
	}

	private void addLine(IGuiElement first, IGuiElement second, IGuiElement third) {
		IElementGroup panel = new PaneLayout(width, 0);
		first.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		second.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		third.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		panel.add(first);
		panel.add(second);
		panel.add(third);
		second.setXPosition(secondColumn);
		third.setXPosition(thirdColumn);
		add(panel);
	}

	private void addLine(IGuiElement first, IGuiElement second) {
		IElementGroup panel = new PaneLayout(width, 0);
		first.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		second.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		panel.add(first);
		panel.add(second);
		second.setXPosition(secondColumn);
		add(panel);
	}

	@Override
	public <A extends IAllele> void addLine(String chromosomeName, BiFunction<A, Boolean, String> toText, IChromosomeType chromosome) {
		addAlleleRow(chromosomeName, toText, chromosome, null);
	}

	@Override
	public <A extends IAllele> void addLine(String chromosomeName, BiFunction<A, Boolean, String> toText, IChromosomeType chromosome, boolean dominant) {
		addAlleleRow(chromosomeName, toText, chromosome, dominant);
	}

	@SuppressWarnings("unchecked")
	private <A extends IAllele> void addAlleleRow(String chromosomeName, BiFunction<A, Boolean, String> toString, IChromosomeType chromosome, @Nullable Boolean dominant) {
		IGenome genome = getGenome();
		A activeAllele = (A) genome.getActiveAllele(chromosome);
		A inactiveAllele = (A) genome.getInactiveAllele(chromosome);
		if (mode == DatabaseMode.BOTH) {
			addLine(chromosomeName, toString.apply(activeAllele, true), toString.apply(inactiveAllele, false), dominant != null ? dominant : activeAllele.isDominant(), dominant != null ? dominant : inactiveAllele.isDominant());
		} else {
			boolean active = mode == DatabaseMode.ACTIVE;
			A allele = active ? activeAllele : inactiveAllele;
			addLine(chromosomeName, toString.apply(allele, active), dominant != null ? dominant : allele.isDominant());
		}
	}

	@Override
	public void addSpeciesLine(String firstText, @Nullable String secondText, IChromosomeType chromosome) {
		/*IAlleleSpecies primary = individual.getGenome().getPrimary();
		IAlleleSpecies secondary = individual.getGenome().getSecondary();

		textLayout.drawLine(text0, textLayout.column0);
		int columnwidth = textLayout.column2 - textLayout.column1 - 2;

		Map<String, ItemStack> iconStacks = chromosome.getSpeciesRoot().getAlyzerPlugin().getIconStacks();

		GuiUtil.drawItemStack(this, iconStacks.get(primary.getUID()), guiLeft + textLayout.column1 + columnwidth - 20, guiTop + 10);
		GuiUtil.drawItemStack(this, iconStacks.get(secondary.getUID()), guiLeft + textLayout.column2 + columnwidth - 20, guiTop + 10);

		String primaryName = customPrimaryName == null ? primary.getAlleleName() : customPrimaryName;
		String secondaryName = customSecondaryName == null ? secondary.getAlleleName() : customSecondaryName;

		drawSplitLine(primaryName, textLayout.column1, columnwidth, individual, chromosome, false);
		drawSplitLine(secondaryName, textLayout.column2, columnwidth, individual, chromosome, true);

		textLayout.newLine();*/
	}
}
