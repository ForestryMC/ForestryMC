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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public abstract class GuiAlyzer extends GuiForestry<ContainerAlyzer, IInventory> {

	protected static final int COLUMN_0 = 12;
	protected static final int COLUMN_1 = 90;
	protected static final int COLUMN_2 = 155;

	private final ISpeciesRoot speciesRoot;
	private final IBreedingTracker breedingTracker;

	private final String guiName;

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	protected GuiAlyzer(ISpeciesRoot speciesRoot, EntityPlayer player, ContainerAlyzer container, IInventory inventory, String guiName) {
		super(Constants.TEXTURE_PATH_GUI + "/beealyzer2.png", container, inventory);

		this.xSize = 246;
		this.ySize = 238;

		this.guiName = guiName;

		this.speciesRoot = speciesRoot;
		this.breedingTracker = this.speciesRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
	}

	protected final int getColorCoding(boolean dominant) {
		if (dominant) {
			return fontColor.get("gui.beealyzer.dominant");
		} else {
			return fontColor.get("gui.beealyzer.recessive");
		}
	}

	protected final void drawLine(String text, int x, IIndividual individual, IChromosomeType chromosome, boolean inactive) {
		if (!inactive) {
			drawLine(text, x, getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()));
		} else {
			drawLine(text, x, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
		}
	}

	protected final void drawSplitLine(String text, int x, int maxWidth, IIndividual individual, IChromosomeType chromosome, boolean inactive) {
		if (!inactive) {
			drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()));
		} else {
			drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
		}
	}

	protected final void drawRow(String text0, String text1, String text2, IIndividual individual, IChromosomeType chromosome) {
		drawRow(text0, text1, text2, fontColor.get("gui.screen"), getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()),
				getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
	}

	protected final void drawChromosomeRow(String chromosomeName, IIndividual individual, IChromosomeType chromosome) {
		IAllele active = individual.getGenome().getActiveAllele(chromosome);
		IAllele inactive = individual.getGenome().getInactiveAllele(chromosome);
		drawRow(chromosomeName, active.getName(), inactive.getName(),
				fontColor.get("gui.screen"), getColorCoding(active.isDominant()),
				getColorCoding(inactive.isDominant()));
	}

	protected final void drawSpeciesRow(String text0, IIndividual individual, IChromosomeType chromosome, String customPrimaryName, String customSecondaryName) {
		IAlleleSpecies primary = individual.getGenome().getPrimary();
		IAlleleSpecies secondary = individual.getGenome().getSecondary();

		drawLine(text0, column0);
		int columnwidth = column2 - column1 - 2;

		RenderHelper.enableStandardItemLighting();
		drawItemStack(iconStacks.get(primary.getUID()), guiLeft + column1 + (columnwidth - 20), guiTop + 10);
		drawItemStack(iconStacks.get(secondary.getUID()), guiLeft + column2 + (columnwidth - 20), guiTop + 10);
		RenderHelper.disableStandardItemLighting();

		String primaryName = customPrimaryName == null ? primary.getName() : customPrimaryName;
		String secondaryName = customSecondaryName == null ? secondary.getName() : customSecondaryName;

		drawSplitLine(primaryName, column1, columnwidth, individual, chromosome, false);
		drawSplitLine(secondaryName, column2, columnwidth, individual, chromosome, true);

		newLine();
	}

	protected static String checkCustomName(String key) {
		if (StringUtil.canTranslate(key)) {
			return StringUtil.localize(key);
		} else {
			return null;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
		widgetManager.clear();
	}

	protected void drawAnalyticsOverview() {

		startPage();

		newLine();
		String title = StringUtil.localize(guiName).toUpperCase();
		drawCenteredLine(title, 8, 208);
		newLine();

		fontRendererObj.drawSplitString(StringUtil.localize(guiName + ".help"), guiLeft + COLUMN_0 + 4, guiTop + 42, 200, fontColor.get("gui.screen"));
		newLine();
		newLine();
		newLine();

		drawLine(StringUtil.localize("gui.alyzer.overview") + ":", COLUMN_0 + 4);
		newLine();
		drawLine("I  : " + StringUtil.localize("gui.general"), COLUMN_0 + 4);
		newLine();
		drawLine("II : " + StringUtil.localize("gui.environment"), COLUMN_0 + 4);
		newLine();
		drawLine("III: " + StringUtil.localize("gui.produce"), COLUMN_0 + 4);
		newLine();
		drawLine("IV : " + StringUtil.localize("gui.evolution"), COLUMN_0 + 4);

		newLine();

		String mode = breedingTracker.getModeName();
		if (mode != null && !mode.isEmpty()) {
			String rules = StringUtil.localize(guiName + ".behaviour") + ": " + WordUtils.capitalize(mode);
			drawCenteredLine(rules, 8, 208, fontColor.get(guiName + ".binomial"));
		}

		endPage();
	}

	protected final void drawAnalyticsPageClassification(IIndividual individual) {

		startPage();

		drawLine(StringUtil.localize("gui.alyzer.classification") + ":", 12);
		newLine();

		Stack<IClassification> hierarchy = new Stack<>();
		IClassification classification = individual.getGenome().getPrimary().getBranch();
		while (classification != null) {

			if (classification.getScientific() != null && !classification.getScientific().isEmpty()) {
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

			drawLine(group.getScientific(), x, group.getLevel().getColour());
			drawLine(group.getLevel().name(), 170, group.getLevel().getColour());
			newLineCompressed();
			x += 12;
		}

		// Add the species name
		String binomial = individual.getGenome().getPrimary().getBinomial();
		if (group != null && group.getLevel() == EnumClassLevel.GENUS) {
			binomial = group.getScientific().substring(0, 1) + ". " + binomial.toLowerCase(Locale.ENGLISH);
		}

		drawLine(binomial, x, 0xebae85);
		drawLine("SPECIES", 170, 0xebae85);

		newLine();
		drawLine(StringUtil.localize("gui.alyzer.authority") + ": " + individual.getGenome().getPrimary().getAuthority(), 12);
		if (AlleleManager.alleleRegistry.isBlacklisted(individual.getIdent())) {
			String extinct = ">> " + StringUtil.localize("gui.alyzer.extinct").toUpperCase() + " <<";
			fontRendererObj.drawStringWithShadow(extinct, guiLeft + 200 - fontRendererObj.getStringWidth(extinct),
					guiTop + getLineY(), fontColor.get("gui.beealyzer.dominant"));
		}

		newLine();
		String description = individual.getGenome().getPrimary().getDescription();
		if (StringUtils.isBlank(description) || description.startsWith("for.description.")) {
			drawSplitLine(StringUtil.localize("gui.alyzer.nodescription"), 12, 200, 0x666666);
		} else {
			String tokens[] = description.split("\\|");
			drawSplitLine(tokens[0], 12, 200, 0x666666);
			if (tokens.length > 1) {
				String signature = "- " + tokens[1];
				fontRendererObj.drawStringWithShadow(signature, (guiLeft + 210) - fontRendererObj.getStringWidth(signature), guiTop + 145 - 14, 0x99cc32);
			}
		}

		endPage();

	}

	protected void drawAnalyticsPageMutations(IIndividual individual) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		drawLine(StringUtil.localize("gui.beealyzer.mutations") + ":", COLUMN_0);
		newLine();

		RenderHelper.enableGUIStandardItemLighting();

		HashMap<IMutation, IAllele> combinations = new HashMap<>();

		for (IMutation mutation : speciesRoot.getCombinations(individual.getGenome().getPrimary())) {
			combinations.put(mutation, individual.getGenome().getPrimary());
		}

		for (IMutation mutation : speciesRoot.getCombinations(individual.getGenome().getSecondary())) {
			combinations.put(mutation, individual.getGenome().getSecondary());
		}

		int columnWidth = 50;
		int x = 0;

		for (Map.Entry<IMutation, IAllele> mutation : combinations.entrySet()) {

			if (breedingTracker.isDiscovered(mutation.getKey())) {
				drawMutationInfo(mutation.getKey(), mutation.getValue(), COLUMN_0 + x);
			} else {
				// Do not display secret undiscovered mutations.
				if (mutation.getKey().isSecret()) {
					continue;
				}

				drawUnknownMutation(mutation.getKey(), COLUMN_0 + x);
			}

			x += columnWidth;
			if (x >= columnWidth * 4) {
				x = 0;
				newLine();
			}
		}

		endPage();
	}

	protected void drawMutationInfo(IMutation combination, IAllele species, int x) {

		ItemStack partnerBee = iconStacks.get(combination.getPartner(species).getUID());
		widgetManager.add(new ItemStackWidget(x, getLineY(), partnerBee));

		drawProbabilityArrow(combination.getBaseChance(), guiLeft + x + 18, guiTop + getLineY() + 4);

		IAllele result = combination.getTemplate()[EnumBeeChromosome.SPECIES.ordinal()];
		ItemStack resultBee = iconStacks.get(result.getUID());
		widgetManager.add(new ItemStackWidget(x + 33, getLineY(), resultBee));
	}

	private void drawUnknownMutation(IMutation combination, int x) {

		drawQuestionMark(guiLeft + x, guiTop + getLineY());

		drawProbabilityArrow(combination.getBaseChance(), guiLeft + x + 18, guiTop + getLineY() + 4);

		drawQuestionMark(guiLeft + x + 32, guiTop + getLineY());
	}

	private void drawQuestionMark(int x, int y) {
		Proxies.render.bindTexture(textureFile);
		drawTexturedModalRect(x, y, 78, 240, 16, 16);
	}

	private void drawProbabilityArrow(float chance, int x, int y) {
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
		Proxies.render.bindTexture(textureFile);
		drawTexturedModalRect(x, y, column, line, 15, 9);
	}

	protected void drawToleranceInfo(IAlleleTolerance toleranceAllele, int x) {
		int textColor = getColorCoding(toleranceAllele.isDominant());
		EnumTolerance tolerance = toleranceAllele.getValue();
		String text = "(" + toleranceAllele.getName() + ")";

		// Enable correct lighting.
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		switch (tolerance) {
			case BOTH_1:
			case BOTH_2:
			case BOTH_3:
			case BOTH_4:
			case BOTH_5:
				drawBothSymbol(x - 2, getLineY() - 1);
				drawLine(text, x + 14, textColor);
				break;
			case DOWN_1:
			case DOWN_2:
			case DOWN_3:
			case DOWN_4:
			case DOWN_5:
				drawDownSymbol(x - 2, getLineY() - 1);
				drawLine(text, x + 14, textColor);
				break;
			case UP_1:
			case UP_2:
			case UP_3:
			case UP_4:
			case UP_5:
				drawUpSymbol(x - 2, getLineY() - 1);
				drawLine(text, x + 14, textColor);
				break;
			default:
				drawNoneSymbol(x - 2, getLineY() - 1);
				drawLine("(0)", x + 14, textColor);
				break;
		}
	}

	private void drawDownSymbol(int x, int y) {
		Proxies.render.bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + y, 0, 247, 15, 9);
	}

	private void drawUpSymbol(int x, int y) {
		Proxies.render.bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + y, 15, 247, 15, 9);
	}

	private void drawBothSymbol(int x, int y) {
		Proxies.render.bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + y, 30, 247, 15, 9);
	}

	private void drawNoneSymbol(int x, int y) {
		Proxies.render.bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + y, 45, 247, 15, 9);
	}

	protected void drawFertilityInfo(int fertility, int x, int textColor, int texOffset) {
		// Enable correct lighting.
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		String fertilityString = Integer.toString(fertility) + " x";

		int stringWidth = fontRendererObj.getStringWidth(fertilityString);

		Proxies.render.bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x + stringWidth + 2, guiTop + getLineY() - 1, 60, 240 + texOffset, 12, 8);

		drawLine(fertilityString, x, textColor);
	}

}
