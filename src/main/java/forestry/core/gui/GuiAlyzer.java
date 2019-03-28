/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;

public class GuiAlyzer extends GuiForestry<ContainerAlyzer> {

	public static final int COLUMN_0 = 12;
	public static final int COLUMN_1 = 90;
	public static final int COLUMN_2 = 155;

	private final ItemInventoryAlyzer itemInventory;

	public GuiAlyzer(EntityPlayer player, ItemInventoryAlyzer itemInventory) {
		super(Constants.TEXTURE_PATH_GUI + "/portablealyzer.png", new ContainerAlyzer(itemInventory, player));

		this.itemInventory = itemInventory;
		this.xSize = 246;
		this.ySize = 238;
	}

	public final int getColorCoding(boolean dominant) {
		if (dominant) {
			return ColourProperties.INSTANCE.get("gui.beealyzer.dominant");
		} else {
			return ColourProperties.INSTANCE.get("gui.beealyzer.recessive");
		}
	}

	public final void drawLine(String text, int x, IIndividual individual, IChromosomeType chromosome, boolean inactive) {
		if (!inactive) {
			textLayout.drawLine(text, x, getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()));
		} else {
			textLayout.drawLine(text, x, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
		}
	}

	public final void drawSplitLine(String text, int x, int maxWidth, IIndividual individual, IChromosomeType chromosome, boolean inactive) {
		if (!inactive) {
			textLayout.drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()));
		} else {
			textLayout.drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
		}
	}

	public final void drawRow(String text0, String text1, String text2, IIndividual individual, IChromosomeType chromosome) {
		textLayout.drawRow(text0, text1, text2, ColourProperties.INSTANCE.get("gui.screen"), getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()),
			getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
	}

	public final void drawChromosomeRow(String chromosomeName, IIndividual individual, IChromosomeType chromosome) {
		IAllele active = individual.getGenome().getActiveAllele(chromosome);
		IAllele inactive = individual.getGenome().getInactiveAllele(chromosome);
		textLayout.drawRow(chromosomeName, active.getAlleleName(), inactive.getAlleleName(),
			ColourProperties.INSTANCE.get("gui.screen"), getColorCoding(active.isDominant()),
			getColorCoding(inactive.isDominant()));
	}

	public final void drawSpeciesRow(String text0, IIndividual individual, IChromosomeType chromosome, @Nullable String customPrimaryName, @Nullable String customSecondaryName) {
		IAlleleSpecies primary = individual.getGenome().getPrimary();
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

		textLayout.newLine();
	}

	@Nullable
	public static String checkCustomName(String key) {
		if (Translator.canTranslateToLocal(key)) {
			return Translator.translateToLocal(key);
		} else {
			return null;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		widgetManager.clear();

		int specimenSlot = getSpecimenSlot();
		if (specimenSlot < ItemInventoryAlyzer.SLOT_ANALYZE_1) {
			drawAnalyticsOverview();
			return;
		}

		ItemStack stackInSlot = itemInventory.getStackInSlot(specimenSlot);
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(stackInSlot);
		if (speciesRoot == null) {
			return;
		}

		switch (specimenSlot) {
			case ItemInventoryAlyzer.SLOT_ANALYZE_1: {
				speciesRoot.getAlyzerPlugin().drawAnalyticsPage1(this, stackInSlot);
				break;
			}
			case ItemInventoryAlyzer.SLOT_ANALYZE_2: {
				speciesRoot.getAlyzerPlugin().drawAnalyticsPage2(this, stackInSlot);
				break;
			}
			case ItemInventoryAlyzer.SLOT_ANALYZE_3: {
				speciesRoot.getAlyzerPlugin().drawAnalyticsPage3(this, stackInSlot);
				break;
			}
			case ItemInventoryAlyzer.SLOT_ANALYZE_4: {
				IIndividual individual = speciesRoot.getMember(stackInSlot);
				drawAnalyticsPageMutations(individual);
				break;
			}
			case ItemInventoryAlyzer.SLOT_ANALYZE_5: {
				IIndividual individual = speciesRoot.getMember(stackInSlot);
				drawAnalyticsPageClassification(individual);
				break;
			}
			default:
				drawAnalyticsOverview();
		}

	}

	private int getSpecimenSlot() {
		for (int k = ItemInventoryAlyzer.SLOT_SPECIMEN; k <= ItemInventoryAlyzer.SLOT_ANALYZE_5; k++) {
			ItemStack stackInSlot = itemInventory.getStackInSlot(k);
			if (stackInSlot.isEmpty()) {
				continue;
			}

			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(stackInSlot);
			if (speciesRoot == null) {
				continue;
			}

			IIndividual individual = speciesRoot.getMember(stackInSlot);
			if (!individual.isAnalyzed()) {
				continue;
			}

			return k;
		}
		return -1;
	}

	public void drawAnalyticsOverview() {

		textLayout.startPage();

		textLayout.newLine();
		String title = Translator.translateToLocal("for.gui.portablealyzer").toUpperCase(Locale.ENGLISH);
		textLayout.drawCenteredLine(title, 8, 208, ColourProperties.INSTANCE.get("gui.screen"));
		textLayout.newLine();

		fontRenderer.drawSplitString(Translator.translateToLocal("for.gui.portablealyzer.help"), guiLeft + COLUMN_0 + 4, guiTop + 42, 200, ColourProperties.INSTANCE.get("gui.screen"));
		textLayout.newLine();
		textLayout.newLine();
		textLayout.newLine();
		textLayout.newLine();

		textLayout.drawLine(Translator.translateToLocal("for.gui.alyzer.overview") + ":", COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine("I  : " + Translator.translateToLocal("for.gui.general"), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine("II : " + Translator.translateToLocal("for.gui.environment"), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine("III: " + Translator.translateToLocal("for.gui.produce"), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine("IV : " + Translator.translateToLocal("for.gui.evolution"), COLUMN_0 + 4);

		textLayout.endPage();
	}

	public final void drawAnalyticsPageClassification(IIndividual individual) {

		textLayout.startPage();

		textLayout.drawLine(Translator.translateToLocal("for.gui.alyzer.classification") + ":", 12);
		textLayout.newLine();

		Stack<IClassification> hierarchy = new Stack<>();
		IClassification classification = individual.getGenome().getPrimary().getBranch();
		while (classification != null) {

			if (!classification.getScientific().isEmpty()) {
				hierarchy.push(classification);
			}
			classification = classification.getParent();
		}

		boolean overcrowded = hierarchy.size() > 5;
		int x = 12;
		IClassification group = null;

		while (!hierarchy.isEmpty()) {

			group = hierarchy.pop();
			if (overcrowded && group.getLevel().isDroppable()) {
				continue;
			}

			textLayout.drawLine(group.getScientific(), x, group.getLevel().getColour());
			textLayout.drawLine(group.getLevel().name(), 170, group.getLevel().getColour());
			textLayout.newLineCompressed();
			x += 12;
		}

		// Add the species name
		String binomial = individual.getGenome().getPrimary().getBinomial();
		if (group != null && group.getLevel() == EnumClassLevel.GENUS) {
			binomial = group.getScientific().substring(0, 1) + ". " + binomial.toLowerCase(Locale.ENGLISH);
		}

		textLayout.drawLine(binomial, x, 0xebae85);
		textLayout.drawLine("SPECIES", 170, 0xebae85);

		textLayout.newLine();
		textLayout.drawLine(Translator.translateToLocal("for.gui.alyzer.authority") + ": " + individual.getGenome().getPrimary().getAuthority(), 12);
		if (AlleleManager.alleleRegistry.isBlacklisted(individual.getIdent())) {
			String extinct = ">> " + Translator.translateToLocal("for.gui.alyzer.extinct").toUpperCase(Locale.ENGLISH) + " <<";
			fontRenderer.drawStringWithShadow(extinct, guiLeft + 200 - fontRenderer.getStringWidth(extinct),
				guiTop + textLayout.getLineY(), ColourProperties.INSTANCE.get("gui.beealyzer.dominant"));
		}

		textLayout.newLine();
		String description = individual.getGenome().getPrimary().getDescription();
		if (StringUtils.isBlank(description) || description.startsWith("for.description.")) {
			textLayout.drawSplitLine(Translator.translateToLocal("for.gui.alyzer.nodescription"), 12, 200, 0x666666);
		} else {
			String tokens[] = description.split("\\|");
			textLayout.drawSplitLine(tokens[0], 12, 200, 0x666666);
			if (tokens.length > 1) {
				String signature = "- " + tokens[1];
				fontRenderer.drawStringWithShadow(signature, guiLeft + 210 - fontRenderer.getStringWidth(signature), guiTop + 145 - 14, 0x99cc32);
			}
		}

		textLayout.endPage();
	}

	public void drawAnalyticsPageMutations(IIndividual individual) {

		textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		textLayout.drawLine(Translator.translateToLocal("for.gui.beealyzer.mutations") + ":", COLUMN_0);
		textLayout.newLine();

		RenderHelper.enableGUIStandardItemLighting();

		IGenome genome = individual.getGenome();
		ISpeciesRoot speciesRoot = genome.getSpeciesRoot();
		IAlleleSpecies species = genome.getPrimary();

		int columnWidth = 50;
		int x = 0;

		EntityPlayer player = Minecraft.getMinecraft().player;
		IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.world, player.getGameProfile());

		for (IMutation mutation : speciesRoot.getCombinations(species)) {
			if (breedingTracker.isDiscovered(mutation)) {
				drawMutationInfo(mutation, species, COLUMN_0 + x, breedingTracker);
			} else {
				// Do not display secret undiscovered mutations.
				if (mutation.isSecret()) {
					continue;
				}

				drawUnknownMutation(mutation, COLUMN_0 + x, breedingTracker);
			}

			x += columnWidth;
			if (x >= columnWidth * 4) {
				x = 0;
				textLayout.newLine(16);
			}
		}

		textLayout.endPage();
	}

	public void drawMutationInfo(IMutation combination, IAllele species, int x, IBreedingTracker breedingTracker) {

		Map<String, ItemStack> iconStacks = combination.getRoot().getAlyzerPlugin().getIconStacks();

		ItemStack partnerBee = iconStacks.get(combination.getPartner(species).getUID());
		widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), partnerBee));

		drawProbabilityArrow(combination, guiLeft + x + 18, guiTop + textLayout.getLineY() + 4, breedingTracker);

		IAllele result = combination.getTemplate()[EnumBeeChromosome.SPECIES.ordinal()];
		ItemStack resultBee = iconStacks.get(result.getUID());
		widgetManager.add(new ItemStackWidget(widgetManager, x + 33, textLayout.getLineY(), resultBee));
	}

	private void drawUnknownMutation(IMutation combination, int x, IBreedingTracker breedingTracker) {

		drawQuestionMark(guiLeft + x, guiTop + textLayout.getLineY());

		drawProbabilityArrow(combination, guiLeft + x + 18, guiTop + textLayout.getLineY() + 4, breedingTracker);

		drawQuestionMark(guiLeft + x + 32, guiTop + textLayout.getLineY());
	}

	private void drawQuestionMark(int x, int y) {
		bindTexture(textureFile);
		drawTexturedModalRect(x, y, 78, 240, 16, 16);
	}

	private void drawProbabilityArrow(IMutation combination, int x, int y, IBreedingTracker breedingTracker) {
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

		// Probability arrow
		bindTexture(textureFile);
		drawTexturedModalRect(x, y, column, line, 15, 9);

		boolean researched = breedingTracker.isResearched(combination);
		if (researched) {
			fontRenderer.drawString("+", x + 9, y + 1, 0);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}

	public void drawToleranceInfo(IAlleleTolerance toleranceAllele, int x) {
		int textColor = getColorCoding(toleranceAllele.isDominant());
		EnumTolerance tolerance = toleranceAllele.getValue();
		String text = "(" + toleranceAllele.getAlleleName() + ")";

		// Enable correct lighting.
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		switch (tolerance) {
			case BOTH_1:
			case BOTH_2:
			case BOTH_3:
			case BOTH_4:
			case BOTH_5:
				drawBothSymbol(x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(text, x + 14, textColor);
				break;
			case DOWN_1:
			case DOWN_2:
			case DOWN_3:
			case DOWN_4:
			case DOWN_5:
				drawDownSymbol(x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(text, x + 14, textColor);
				break;
			case UP_1:
			case UP_2:
			case UP_3:
			case UP_4:
			case UP_5:
				drawUpSymbol(x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(text, x + 14, textColor);
				break;
			default:
				drawNoneSymbol(x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine("(0)", x + 14, textColor);
				break;
		}
	}

	private void drawDownSymbol(int x, int y) {
		bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + y, 0, 247, 15, 9);
	}

	private void drawUpSymbol(int x, int y) {
		bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + y, 15, 247, 15, 9);
	}

	private void drawBothSymbol(int x, int y) {
		bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + y, 30, 247, 15, 9);
	}

	private void drawNoneSymbol(int x, int y) {
		bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + y, 45, 247, 15, 9);
	}

	public void drawFertilityInfo(int fertility, int x, int textColor, int texOffset) {
		// Enable correct lighting.
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		String fertilityString = Integer.toString(fertility) + " x";

		int stringWidth = fontRenderer.getStringWidth(fertilityString);

		bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x + stringWidth + 2, guiTop + textLayout.getLineY() - 1, 60, 240 + texOffset, 12, 8);

		textLayout.drawLine(fertilityString, x, textColor);
	}

	public TextLayoutHelper getTextLayout() {
		return textLayout;
	}

	public WidgetManager getWidgetManager() {
		return widgetManager;
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(itemInventory);
		addHintLedger(getHints());
	}

	public List<String> getHints() {
		ItemStack specimen = itemInventory.getSpecimen();
		if (!specimen.isEmpty()) {
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(specimen);
			if (speciesRoot != null) {
				IAlyzerPlugin alyzerPlugin = speciesRoot.getAlyzerPlugin();
				return alyzerPlugin.getHints();
			}
		}
		return Collections.emptyList();
	}
}
