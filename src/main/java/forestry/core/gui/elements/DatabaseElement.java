package forestry.core.gui.elements;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.gatgets.DatabaseMode;
import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.gui.elements.layouts.FlexLayout;
import forestry.core.utils.Translator;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;

public class DatabaseElement extends ContainerElement {
	private DatabaseMode mode = DatabaseMode.ACTIVE;
	@Nullable
	private IIndividual individual;
	private int secondColumn = 0;
	private int thirdColumn = 0;

	public DatabaseElement(int width) {
		setSize(width, UNKNOWN_HEIGHT);
		setLayout(FlexLayout.vertical(0));
		this.secondColumn = width / 2;
	}

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

	public void addFertilityLine(String chromosomeName, IChromosomeType chromosome, int texOffset) {
		IGenome genome = getGenome();
		IAllele activeAllele = genome.getActiveAllele(chromosome);
		IAllele inactiveAllele = genome.getInactiveAllele(chromosome);
		if (mode == DatabaseMode.BOTH) {
			if (!(activeAllele instanceof IAlleleValue) || !(inactiveAllele instanceof IAlleleValue)) {
				return;
			}
			addLine(chromosomeName, GuiElementFactory.INSTANCE.createFertilityInfo((IAlleleValue<Integer>) activeAllele, texOffset), GuiElementFactory.INSTANCE.createFertilityInfo((IAlleleValue<Integer>) inactiveAllele, texOffset));
		} else {
			boolean active = mode == DatabaseMode.ACTIVE;
			IAllele allele = active ? activeAllele : inactiveAllele;
			if (!(allele instanceof IAlleleValue)) {
				return;
			}
			addLine(chromosomeName, GuiElementFactory.INSTANCE.createFertilityInfo((IAlleleValue<Integer>) allele, texOffset));
		}
	}

	public void addToleranceLine(IChromosomeType chromosome) {
		IAllele allele = getGenome().getActiveAllele(chromosome);
		if (!(allele instanceof IAlleleValue)) {
			return;
		}
		addLine("  " + Translator.translateToLocal("for.gui.tolerance"), GuiElementFactory.INSTANCE.createToleranceInfo((IAlleleValue<EnumTolerance>) allele));
	}

	public void addMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker) {
		GuiElement element = GuiElementFactory.INSTANCE.createMutation(x, y, width, height, mutation, species, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	public void addMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker) {
		GuiElement element = GuiElementFactory.INSTANCE.createMutationResultant(x, y, width, height, mutation, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	public void addLine(String firstText, String secondText, boolean dominant) {
		addLine(firstText, secondText, GuiElementFactory.INSTANCE.guiStyle, GuiElementFactory.INSTANCE.getStateStyle(dominant));
	}

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

	public final void addLine(String chromosomeName, IChromosomeType chromosome) {
		addLine(chromosomeName, (allele, b) -> allele.getDisplayName().getString(), chromosome);
	}

	public void addLine(String firstText, String secondText, Style firstStyle, Style secondStyle) {
		ContainerElement first = addSplitText(preferredSize.width, firstText, firstStyle);
		ContainerElement second = addSplitText(preferredSize.width, secondText, secondStyle);
		addLine(first, second);
	}

	private ContainerElement addSplitText(int width, String text, Style style) {
		FontRenderer fontRenderer = Minecraft.getInstance().font;
		ContainerElement vertical = GuiElementFactory.vertical(width, 0);
		for (IReorderingProcessor splitText : fontRenderer.split(new StringTextComponent(text), width)) {
			vertical.label(splitText).setStyle(style);
		}
		return vertical;
	}

	private void addLine(String chromosomeName, GuiElement right) {
		int center = preferredSize.width / 2;
		GuiElement first = addSplitText(center, chromosomeName, GuiElementFactory.INSTANCE.guiStyle);
		addLine(first, right);
	}

	private void addLine(String chromosomeName, GuiElement second, GuiElement third) {
		int center = preferredSize.width / 2;
		GuiElement first = addSplitText(center, chromosomeName, GuiElementFactory.INSTANCE.guiStyle);
		addLine(first, second, third);
	}

	private void addLine(GuiElement first, GuiElement second, GuiElement third) {
		ContainerElement panel = pane(preferredSize.width, UNKNOWN_HEIGHT);
		first.setAlign(Alignment.MIDDLE_LEFT);
		second.setAlign(Alignment.MIDDLE_LEFT);
		third.setAlign(Alignment.MIDDLE_LEFT);
		panel.add(first);
		panel.add(second);
		panel.add(third);
		second.setXPosition(secondColumn);
		third.setXPosition(thirdColumn);
	}

	private void addLine(GuiElement first, GuiElement second) {
		ContainerElement panel = pane(preferredSize.width, UNKNOWN_HEIGHT);
		first.setAlign(Alignment.MIDDLE_LEFT);
		second.setAlign(Alignment.MIDDLE_LEFT);
		panel.add(first);
		panel.add(second);
		second.setXPosition(secondColumn);
	}

	public <A extends IAllele> void addLine(String chromosomeName, BiFunction<A, Boolean, String> toText, IChromosomeType chromosome) {
		addAlleleRow(chromosomeName, toText, chromosome, null);
	}

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
