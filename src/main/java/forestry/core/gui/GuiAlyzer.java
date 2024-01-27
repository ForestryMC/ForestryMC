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
import java.util.Optional;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.alleles.IAlleleValue;
import genetics.api.classification.IClassification;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.ComponentKeys;
import genetics.utils.AlleleUtils;
import genetics.utils.RootUtils;

public class GuiAlyzer extends GuiForestry<ContainerAlyzer> {

	public static final int COLUMN_0 = 12;
	public static final int COLUMN_1 = 90;
	public static final int COLUMN_2 = 155;

	private final ItemInventoryAlyzer itemInventory;

	public GuiAlyzer(ContainerAlyzer container, Inventory playerInv, Component name) {
		super(Constants.TEXTURE_PATH_GUI + "/portablealyzer.png", container, playerInv, Component.literal("GUI_ALYZER_TEST_TITLE"));

		this.itemInventory = container.inventory;
		this.imageWidth = 246;
		this.imageHeight = 238;
	}

	public final int getColorCoding(boolean dominant) {
		if (dominant) {
			return ColourProperties.INSTANCE.get("gui.beealyzer.dominant");
		} else {
			return ColourProperties.INSTANCE.get("gui.beealyzer.recessive");
		}
	}

	public final void drawLine(PoseStack transform, String text, int x, IIndividual individual, IChromosomeType chromosome, boolean inactive) {
		if (!inactive) {
			textLayout.drawLine(transform, text, x, getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()));
		} else {
			textLayout.drawLine(transform, text, x, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
		}
	}

	public final void drawSplitLine(String text, int x, int maxWidth, IIndividual individual, IChromosomeType chromosome, boolean inactive) {
		if (!inactive) {
			textLayout.drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()));
		} else {
			textLayout.drawSplitLine(text, x, maxWidth, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
		}
	}

	public final void drawSplitLine(Component component, int x, int maxWidth, IIndividual individual, IChromosomeType chromosome, boolean inactive) {
		if (!inactive) {
			textLayout.drawSplitLine(component, x, maxWidth, getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()));
		} else {
			textLayout.drawSplitLine(component, x, maxWidth, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
		}
	}

	public final void drawRow(PoseStack transform, String text0, String text1, String text2, IIndividual individual, IChromosomeType chromosome) {
		textLayout.drawRow(transform, text0, text1, text2, ColourProperties.INSTANCE.get("gui.screen"), getColorCoding(individual.getGenome().getActiveAllele(chromosome).isDominant()),
				getColorCoding(individual.getGenome().getInactiveAllele(chromosome).isDominant()));
	}

	public final void drawChromosomeRow(PoseStack transform, String chromosomeName, IIndividual individual, IChromosomeType chromosome) {
		IAllele active = individual.getGenome().getActiveAllele(chromosome);
		IAllele inactive = individual.getGenome().getInactiveAllele(chromosome);
		textLayout.drawRow(transform, chromosomeName, active.getDisplayName().getString(), inactive.getDisplayName().getString(),
				ColourProperties.INSTANCE.get("gui.screen"), getColorCoding(active.isDominant()),
				getColorCoding(inactive.isDominant()));
	}

	public final void drawSpeciesRow(PoseStack transform, String text0, IIndividual individual, IChromosomeType chromosome, IOrganismType type) {
		IAlleleForestrySpecies primary = (IAlleleForestrySpecies) individual.getGenome().getPrimary();
		IAlleleForestrySpecies secondary = (IAlleleForestrySpecies) individual.getGenome().getSecondary();

		textLayout.drawLine(transform, text0, textLayout.column0);
		int columnwidth = textLayout.column2 - textLayout.column1 - 2;

		Map<ResourceLocation, ItemStack> iconStacks = ((IForestrySpeciesRoot<?>) chromosome.getRoot()).getAlyzerPlugin().getIconStacks();

		GuiUtil.drawItemStack(this, iconStacks.get(primary.getRegistryName()), leftPos + textLayout.column1 + columnwidth - 20, topPos + 10);
		GuiUtil.drawItemStack(this, iconStacks.get(secondary.getRegistryName()), leftPos + textLayout.column2 + columnwidth - 20, topPos + 10);

		Component primaryName = primary.getAlyzerName(type);
		Component secondaryName = primary.getAlyzerName(type);

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
	protected void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(transform, partialTicks, mouseX, mouseY);
		widgetManager.clear();

		int specimenSlot = getSpecimenSlot();
		if (specimenSlot < ItemInventoryAlyzer.SLOT_ANALYZE_1) {
			drawAnalyticsOverview(transform);
			return;
		}

		ItemStack stackInSlot = itemInventory.getItem(specimenSlot);
		IRootDefinition<IForestrySpeciesRoot<IIndividual>> definition = RootUtils.getRoot(stackInSlot);
		if (!definition.isPresent()) {
			return;
		}
		IForestrySpeciesRoot<IIndividual> speciesRoot = definition.get();
		switch (specimenSlot) {
			case ItemInventoryAlyzer.SLOT_ANALYZE_1 -> {
				speciesRoot.getAlyzerPlugin().drawAnalyticsPage1(transform, this, stackInSlot);
				break;
			}
			case ItemInventoryAlyzer.SLOT_ANALYZE_2 -> {
				speciesRoot.getAlyzerPlugin().drawAnalyticsPage2(transform, this, stackInSlot);
				break;
			}
			case ItemInventoryAlyzer.SLOT_ANALYZE_3 -> {
				speciesRoot.getAlyzerPlugin().drawAnalyticsPage3(transform, stackInSlot, this);
				break;
			}
			case ItemInventoryAlyzer.SLOT_ANALYZE_4 -> {
				speciesRoot.create(stackInSlot).ifPresent((value) -> drawAnalyticsPageMutations(transform, value));
				break;
			}
			case ItemInventoryAlyzer.SLOT_ANALYZE_5 -> {
				speciesRoot.create(stackInSlot).ifPresent((value) -> drawAnalyticsPageClassification(transform, value));
				break;
			}
			default -> drawAnalyticsOverview(transform);
		}

	}

	private int getSpecimenSlot() {
		for (int k = ItemInventoryAlyzer.SLOT_SPECIMEN; k <= ItemInventoryAlyzer.SLOT_ANALYZE_5; k++) {
			ItemStack stackInSlot = itemInventory.getItem(k);
			if (stackInSlot.isEmpty()) {
				continue;
			}

			IRootDefinition<IForestrySpeciesRoot<IIndividual>> definition = RootUtils.getRoot(stackInSlot);
			if (!definition.isPresent()) {
				continue;
			}
			IForestrySpeciesRoot<IIndividual> speciesRoot = definition.get();

			Optional<IIndividual> optionalIndividual = speciesRoot.create(stackInSlot);
			if (optionalIndividual.filter(individual -> !individual.isAnalyzed()).isPresent()) {
				continue;
			}

			return k;
		}
		return -1;
	}

	public void drawAnalyticsOverview(PoseStack transform) {

		textLayout.startPage(transform);

		textLayout.newLine();
		String title = Translator.translateToLocal("for.gui.portablealyzer").toUpperCase(Locale.ENGLISH);
		textLayout.drawCenteredLine(transform, title, 8, 208, ColourProperties.INSTANCE.get("gui.screen"));
		textLayout.newLine();

		getFontRenderer().drawWordWrap(Component.translatable("for.gui.portablealyzer.help"), leftPos + COLUMN_0 + 4, topPos + 42, 200, ColourProperties.INSTANCE.get("gui.screen"));
		textLayout.newLine();
		textLayout.newLine();
		textLayout.newLine();
		textLayout.newLine();

		textLayout.drawLine(transform, Translator.translateToLocal("for.gui.alyzer.overview") + ":", COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine(transform, "I  : " + Translator.translateToLocal("for.gui.general"), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine(transform, "II : " + Translator.translateToLocal("for.gui.environment"), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine(transform, "III: " + Translator.translateToLocal("for.gui.produce"), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine(transform, "IV : " + Translator.translateToLocal("for.gui.evolution"), COLUMN_0 + 4);

		textLayout.endPage(transform);
	}

	public final void drawAnalyticsPageClassification(PoseStack transform, IIndividual individual) {

		textLayout.startPage(transform);

		textLayout.drawLine(transform, Translator.translateToLocal("for.gui.alyzer.classification") + ":", 12);
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

			textLayout.drawLine(transform, group.getScientific(), x, group.getLevel().getColour());
			textLayout.drawLine(transform, group.getLevel().name(), 170, group.getLevel().getColour());
			textLayout.newLineCompressed();
			x += 12;
		}

		// Add the species name
		String binomial = individual.getGenome().getPrimary().getBinomial();
		if (group != null && group.getLevel() == IClassification.EnumClassLevel.GENUS) {
			binomial = group.getScientific().substring(0, 1) + ". " + binomial.toLowerCase(Locale.ENGLISH);
		}

		textLayout.drawLine(transform, binomial, x, 0xebae85);
		textLayout.drawLine(transform, "SPECIES", 170, 0xebae85);

		textLayout.newLine();
		textLayout.drawLine(transform, Translator.translateToLocal("for.gui.alyzer.authority") + ": " + individual.getGenome().getPrimary().getAuthority(), 12);
		if (AlleleUtils.isBlacklisted(individual.getIdentifier())) {
			String extinct = ">> " + Translator.translateToLocal("for.gui.alyzer.extinct").toUpperCase(Locale.ENGLISH) + " <<";
			getFontRenderer().drawShadow(transform, extinct, leftPos + 200 - getFontRenderer().width(extinct),
					topPos + textLayout.getLineY(), ColourProperties.INSTANCE.get("gui.beealyzer.dominant"));
		}

		textLayout.newLine();
		String description = individual.getGenome().getPrimary().getDescription().getString();
		if (StringUtils.isBlank(description) || description.startsWith("for.description.")) {
			textLayout.drawSplitLine(Translator.translateToLocal("for.gui.alyzer.nodescription"), 12, 200, 0x666666);
		} else {
			String[] tokens = description.split("\\|");
			textLayout.drawSplitLine(tokens[0], 12, 200, 0x666666);
			if (tokens.length > 1) {
				String signature = "- " + tokens[1];
				getFontRenderer().drawShadow(transform, signature, leftPos + 210 - getFontRenderer().width(signature), topPos + 145 - 14, 0x99cc32);
			}
		}

		textLayout.endPage(transform);
	}

	@SuppressWarnings("unchecked")
	public void drawAnalyticsPageMutations(PoseStack transform, IIndividual individual) {
		textLayout.startPage(transform, COLUMN_0, COLUMN_1, COLUMN_2);
		textLayout.drawLine(transform, Translator.translateToLocal("for.gui.beealyzer.mutations") + ":", COLUMN_0);
		textLayout.newLine();

		//RenderHelper.enableGUIStandardItemLighting(); TODO Gui Light

		IGenome genome = individual.getGenome();
		IForestrySpeciesRoot<IIndividual> speciesRoot = (IForestrySpeciesRoot) individual.getRoot();
		IAlleleSpecies species = genome.getPrimary();

		int columnWidth = 50;
		int x = 0;

		Player player = Minecraft.getInstance().player;
		//TODO world cast
		IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.level, player.getGameProfile());

		IMutationContainer<IIndividual, ? extends IMutation> container = speciesRoot.getComponent(ComponentKeys.MUTATIONS);
		for (IMutation mutation : container.getCombinations(species)) {
			if (breedingTracker.isDiscovered(mutation)) {
				drawMutationInfo(transform, mutation, species, COLUMN_0 + x, breedingTracker);
			} else {
				// Do not display secret undiscovered mutations.
				if (mutation.isSecret()) {
					continue;
				}

				drawUnknownMutation(transform, mutation, COLUMN_0 + x, breedingTracker);
			}

			x += columnWidth;
			if (x >= columnWidth * 4) {
				x = 0;
				textLayout.newLine(16);
			}
		}

		textLayout.endPage(transform);
	}

	public void drawMutationInfo(PoseStack transform, IMutation combination, IAllele species, int x, IBreedingTracker breedingTracker) {
		Map<ResourceLocation, ItemStack> iconStacks = ((IForestrySpeciesRoot) combination.getRoot()).getAlyzerPlugin().getIconStacks();

		ItemStack partnerBee = iconStacks.get(combination.getPartner(species).getRegistryName());
		widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), partnerBee));

		drawProbabilityArrow(transform, combination, leftPos + x + 18, topPos + textLayout.getLineY() + 4, breedingTracker);

		IAllele result = combination.getTemplate()[BeeChromosomes.SPECIES.ordinal()];
		ItemStack resultBee = iconStacks.get(result.getRegistryName());
		widgetManager.add(new ItemStackWidget(widgetManager, x + 33, textLayout.getLineY(), resultBee));
	}

	private void drawUnknownMutation(PoseStack transform, IMutation combination, int x, IBreedingTracker breedingTracker) {

		drawQuestionMark(transform, leftPos + x, topPos + textLayout.getLineY());

		drawProbabilityArrow(transform, combination, leftPos + x + 18, topPos + textLayout.getLineY() + 4, breedingTracker);

		drawQuestionMark(transform, leftPos + x + 32, topPos + textLayout.getLineY());
	}

	private void drawQuestionMark(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, x, y, 78, 240, 16, 16);
	}

	private void drawProbabilityArrow(PoseStack transform, IMutation combination, int x, int y, IBreedingTracker breedingTracker) {
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
		blit(transform, x, y, column, line, 15, 9);

		boolean researched = breedingTracker.isResearched(combination);
		if (researched) {
			getFontRenderer().draw(transform, "+", x + 9, y + 1, 0);
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}

	public void drawToleranceInfo(PoseStack transform, IAlleleValue<EnumTolerance> toleranceAllele, int x) {
		int textColor = getColorCoding(toleranceAllele.isDominant());
		EnumTolerance tolerance = toleranceAllele.getValue();
		String text = "(" + toleranceAllele.getDisplayName().getString() + ")";

		// Enable correct lighting.
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		switch (tolerance) {
			case BOTH_1, BOTH_2, BOTH_3, BOTH_4, BOTH_5 -> {
				drawBothSymbol(transform, x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(transform, text, x + 14, textColor);
			}
			case DOWN_1, DOWN_2, DOWN_3, DOWN_4, DOWN_5 -> {
				drawDownSymbol(transform, x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(transform, text, x + 14, textColor);
			}
			case UP_1, UP_2, UP_3, UP_4, UP_5 -> {
				drawUpSymbol(transform, x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(transform, text, x + 14, textColor);
			}
			default -> {
				drawNoneSymbol(transform, x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(transform, "(0)", x + 14, textColor);
			}
		}
	}

	private void drawDownSymbol(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, leftPos + x, topPos + y, 0, 247, 15, 9);
	}

	private void drawUpSymbol(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, leftPos + x, topPos + y, 15, 247, 15, 9);
	}

	private void drawBothSymbol(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, leftPos + x, topPos + y, 30, 247, 15, 9);
	}

	private void drawNoneSymbol(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, leftPos + x, topPos + y, 45, 247, 15, 9);
	}

	public void drawFertilityInfo(PoseStack transform, int fertility, int x, int textColor, int texOffset) {
		// Enable correct lighting.
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		String fertilityString = fertility + " x";

		int stringWidth = getFontRenderer().width(fertilityString);

		bindTexture(textureFile);
		blit(transform, leftPos + x + stringWidth + 2, topPos + textLayout.getLineY() - 1, 60, 240 + texOffset, 12, 8);

		textLayout.drawLine(transform, fertilityString, x, textColor);
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
			IRootDefinition<IForestrySpeciesRoot> definition = RootUtils.getRoot(specimen);
			if (definition.isPresent()) {
				IAlyzerPlugin alyzerPlugin = definition.get().getAlyzerPlugin();
				return alyzerPlugin.getHints();
			}
		}
		return Collections.emptyList();
	}
}
