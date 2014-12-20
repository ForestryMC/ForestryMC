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
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileForestry;
import forestry.core.genetics.EnumMutateChance;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

public abstract class GuiAlyzer extends GuiForestry<TileForestry> {

	protected static final int COLUMN_0 = 12;
	protected static final int COLUMN_1 = 85;
	protected static final int COLUMN_2 = 150;

	protected final IInventory inventory;
	protected final ISpeciesRoot speciesRoot;
	protected final IBreedingTracker breedingTracker;

	protected final String guiName;

	protected HashMap<String, ItemStack> iconStacks = new HashMap<String, ItemStack>();

	public GuiAlyzer(String speciesRoot, EntityPlayer player, ContainerForestry container, IInventory inventory, String guiName) {
		super(Defaults.TEXTURE_PATH_GUI + "/beealyzer2.png", container);

		this.xSize = 246;
		this.ySize = 238;

		this.guiName = guiName;

		this.inventory = inventory;
		this.speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(speciesRoot);
		this.breedingTracker = this.speciesRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
	}

	protected final int getColorCoding(boolean dominant) {
		if (dominant)
			return fontColor.get("gui.beealyzer.dominant");
		else
			return fontColor.get("gui.beealyzer.recessive");
	}

	protected final void drawLine(String text, int x, IIndividual individual, IChromosomeType chromosome, boolean inactive) {
		if (!inactive)
			drawLine(text, x, getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()));
		else
			drawLine(text, x, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
	}

	protected final void drawSplitLine(String text, int x, int maxWidth, IIndividual individual, IChromosomeType chromosome, boolean inactive) {
		if (!inactive)
			drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()));
		else
			drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
	}

	protected final void drawRow(String text0, String text1, String text2, IIndividual individual, IChromosomeType chromosome) {
		drawRow(text0, text1, text2, fontColor.get("gui.screen"), getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()),
				getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
	}

	protected final void drawSpeciesRow(String text0, IIndividual individual, IChromosomeType chromosome, String customPrimaryName, String customSecondaryName) {
		IAlleleSpecies primary = individual.getGenome().getPrimary();
		IAlleleSpecies secondary = individual.getGenome().getSecondary();

		drawLine(text0, column0);
		int columnwidth = column2 - column1 - 16;

		RenderHelper.enableStandardItemLighting();
		drawItemStack(iconStacks.get(primary.getUID()), adjustToFactor(guiLeft + column1 + columnwidth - 4), adjustToFactor(guiTop + getLineY()));
		drawItemStack(iconStacks.get(secondary.getUID()), adjustToFactor(guiLeft + column2 + columnwidth - 2), adjustToFactor(guiTop + getLineY()));
		RenderHelper.disableStandardItemLighting();

		String primaryName;
		String secondaryName;

		primaryName = customPrimaryName == null ? primary.getName() : customPrimaryName;
		secondaryName = customSecondaryName == null ? secondary.getName() : customSecondaryName;

		drawSplitLine(primaryName, column1, columnwidth, individual, chromosome, false);
		drawSplitLine(secondaryName, column2, columnwidth, individual, chromosome, true);

		newLine();
		newLine();

	}

	protected final void drawSpeciesRow(String text0, IIndividual individual, IChromosomeType chromosome){
		drawSpeciesRow(text0, individual, chromosome, null, null);
	}

	protected final String checkCustomName(String key){
		if(StringUtil.canTranslate(key)){
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

		fontRendererObj.drawSplitString(StringUtil.localize(guiName + ".help"), (int) ((guiLeft + COLUMN_0 + 4) * (1 / factor)),
				(int) ((guiTop + 42) * (1 / factor)), (int) (195 * (1 / factor)), fontColor.get("gui.screen"));
		newLine();
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
			newLine();
			String rules = StringUtil.localize(guiName + ".behaviour") + ": " + StringUtil.capitalize(mode);
			drawCenteredLine(rules, 8, 208, fontColor.get(guiName + ".binomial"));
		}

		endPage();
	}

	protected final void drawAnalyticsPageClassification(IIndividual individual) {

		startPage();

		drawLine(StringUtil.localize("gui.alyzer.classification") + ":", 12);
		newLine();

		Stack<IClassification> hierarchy = new Stack<IClassification>();
		IClassification classification = individual.getGenome().getPrimary().getBranch();
		while (classification != null) {

			if (classification.getScientific() != null && !classification.getScientific().isEmpty())
				hierarchy.push(classification);
			classification = classification.getParent();
		}

		boolean overcrowded = hierarchy.size() > 5;
		int x = 12;
		IClassification group = null;

		while (!hierarchy.isEmpty()) {

			group = hierarchy.pop();
			if (overcrowded && group.getLevel().isDroppable())
				continue;

			drawLine(group.getScientific(), x, group.getLevel().getColour());
			drawLine(group.getLevel().name(), 155, group.getLevel().getColour());
			newLine();
			x += 10;
		}

		// Add the species name
		String binomial = individual.getGenome().getPrimary().getBinomial();
		if (group != null && group.getLevel() == EnumClassLevel.GENUS)
			binomial = group.getScientific().substring(0, 1) + ". " + binomial.toLowerCase(Locale.ENGLISH);

		drawLine(binomial, x, 0xebae85);
		drawLine("SPECIES", 155, 0xebae85);

		newLine();
		newLine();
		drawLine(StringUtil.localize("gui.alyzer.authority") + ": " + individual.getGenome().getPrimary().getAuthority(), 12);
		if (AlleleManager.alleleRegistry.isBlacklisted(individual.getIdent())) {
			String extinct = ">> " + StringUtil.localize("gui.alyzer.extinct").toUpperCase() + " <<";
			fontRendererObj.drawStringWithShadow(extinct, adjustToFactor(guiLeft + 208) - fontRendererObj.getStringWidth(extinct),
					adjustToFactor(guiTop + getLineY()), fontColor.get("gui.beealyzer.dominant"));
		}

		newLine();
		String description = individual.getGenome().getPrimary().getDescription();
		if (StringUtils.isBlank(description) || description.startsWith("for.description."))
			drawSplitLine(StringUtil.localize("gui.alyzer.nodescription"), 12, 208, 0x666666);
		else {
			String tokens[] = description.split("\\|");
			drawSplitLine(tokens[0], 12, 208, 0x666666);
			if (tokens.length > 1)
				fontRendererObj.drawStringWithShadow("- " + tokens[1], adjustToFactor(guiLeft + 208) - fontRendererObj.getStringWidth("- " + tokens[1]),
						adjustToFactor(guiTop + 145 - 14), 0x99cc32);
		}

		endPage();

	}

	protected void drawAnalyticsPage4(IIndividual individual) {

		float factor = this.factor;
		this.setFactor(1.0f);

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		drawLine(StringUtil.localize("gui.beealyzer.mutations") + ":", COLUMN_0);
		newLine();

		RenderHelper.enableGUIStandardItemLighting();

		HashMap<IMutation, IAllele> combinations = new HashMap<IMutation, IAllele>();

		for (IMutation mutation : speciesRoot.getCombinations(individual.getGenome().getPrimary()))
			combinations.put(mutation, individual.getGenome().getPrimary());

		for (IMutation mutation : speciesRoot.getCombinations(individual.getGenome().getSecondary()))
			combinations.put(mutation, individual.getGenome().getSecondary());

		int columnWidth = 50;
		int x = 0;

		for (Map.Entry<IMutation, IAllele> mutation : combinations.entrySet()) {

			if (breedingTracker.isDiscovered(mutation.getKey()))
				drawMutationInfo(mutation.getKey(), mutation.getValue(), COLUMN_0 + x);
			else {
				// Do not display secret undiscovered mutations.
				if (mutation.getKey().isSecret())
					continue;

				drawUnknownMutation(mutation.getKey(), mutation.getValue(), COLUMN_0 + x);
			}

			x += columnWidth;
			if (x >= columnWidth * 4) {
				x = 0;
				newLine();
				newLine();
			}
		}

		endPage();

		this.setFactor(factor);
	}

	protected void drawMutationInfo(IMutation combination, IAllele species, int x) {

		ItemStack partnerBee = iconStacks.get(combination.getPartner(species).getUID());
		widgetManager.add(new ItemStackWidget(x, getLineY(), partnerBee));

		drawProbabilityArrow(combination.getBaseChance(), adjustToFactor(guiLeft) + x + 18, adjustToFactor(guiTop) + getLineY() + 4);

		IAllele result = combination.getTemplate()[EnumBeeChromosome.SPECIES.ordinal()];
		ItemStack resultBee = iconStacks.get(result.getUID());
		widgetManager.add(new ItemStackWidget(x + 33, getLineY(), resultBee));
	}

	protected void drawUnknownMutation(IMutation combination, IAllele species, int x) {

		drawQuestionMark(adjustToFactor(guiLeft) + x, adjustToFactor(guiTop) + getLineY());

		drawProbabilityArrow(combination.getBaseChance(), adjustToFactor(guiLeft) + x + 18, adjustToFactor(guiTop) + getLineY() + 4);

		drawQuestionMark(adjustToFactor(guiLeft) + x + 32, adjustToFactor(guiTop) + getLineY());
	}

	protected void drawQuestionMark(int x, int y) {
		Proxies.common.bindTexture(textureFile);
		drawTexturedModalRect(x, y, 78, 240, 16, 16);
	}

	protected void drawProbabilityArrow(float chance, int x, int y) {
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
		Proxies.common.bindTexture(textureFile);
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
			drawBothSymbol(x, getLineY() - 1);
			drawLine(text, x + (int) (20 * factor), textColor);
			break;
		case DOWN_1:
		case DOWN_2:
		case DOWN_3:
		case DOWN_4:
		case DOWN_5:
			drawDownSymbol(x, getLineY() - 1);
			drawLine(text, x + (int) (20 * factor), textColor);
			break;
		case UP_1:
		case UP_2:
		case UP_3:
		case UP_4:
		case UP_5:
			drawUpSymbol(x, getLineY() - 1);
			drawLine(text, x + (int) (20 * factor), textColor);
			break;
		default:
			drawNoneSymbol(x, getLineY() - 1);
			drawLine("(0)", x + (int) (20 * factor), textColor);
			break;
		}
	}

	private void drawDownSymbol(int x, int y) {
		Proxies.common.bindTexture(textureFile);
		drawTexturedModalRect((int) ((guiLeft + x) * (1 / factor)), (int) ((guiTop + getLineY()) * (1 / factor)), 0, 247, 15, 9);
	}

	private void drawUpSymbol(int x, int y) {
		Proxies.common.bindTexture(textureFile);
		drawTexturedModalRect((int) ((guiLeft + x) * (1 / factor)), (int) ((guiTop + getLineY()) * (1 / factor)), 15, 247, 15, 9);
	}

	private void drawBothSymbol(int x, int y) {
		Proxies.common.bindTexture(textureFile);
		drawTexturedModalRect((int) ((guiLeft + x) * (1 / factor)), (int) ((guiTop + getLineY()) * (1 / factor)), 30, 247, 15, 9);
	}

	private void drawNoneSymbol(int x, int y) {
		Proxies.common.bindTexture(textureFile);
		drawTexturedModalRect((int) ((guiLeft + x) * (1 / factor)), (int) ((guiTop + getLineY()) * (1 / factor)), 45, 247, 15, 9);
	}

	protected void drawFertilityInfo(int fertility, int x, int textColor, int texOffset) {
		// Enable correct lighting.
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		Proxies.common.bindTexture(textureFile);
		drawTexturedModalRect((int) ((guiLeft + x + 14) * (1 / factor)), (int) ((guiTop + getLineY()) * (1 / factor)), 60, 240 + texOffset, 12, 8);

		drawLine(Integer.toString(fertility) + " x", x, textColor);
	}

}
