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
package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.config.Config;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.TextLayoutHelper;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

import genetics.api.GeneticHelper;
import genetics.api.individual.IGenome;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismType;

public class TreeAlyzerPlugin implements IAlyzerPlugin {
	public static final TreeAlyzerPlugin INSTANCE = new TreeAlyzerPlugin();

	protected final Map<ResourceLocation, ItemStack> iconStacks = new HashMap<>();

	private TreeAlyzerPlugin() {
		NonNullList<ItemStack> treeList = NonNullList.create();
		ArboricultureItems.SAPLING.item().addCreativeItems(treeList, false);
		for (ItemStack treeStack : treeList) {
			IOrganism<?> organism = GeneticHelper.getOrganism(treeStack);
			if (organism.isEmpty()) {
				continue;
			}
			IAlleleTreeSpecies species = organism.getAllele(TreeChromosomes.SPECIES, true);
			iconStacks.put(species.getRegistryName(), treeStack);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawAnalyticsPage1(MatrixStack transform, Screen gui, ItemStack itemStack) {
		if (gui instanceof GuiAlyzer) {
			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
			Optional<ITree> optional = TreeManager.treeRoot.create(itemStack);
			if (!optional.isPresent()) {
				return;
			}
			ITree tree = optional.get();
			Optional<IOrganismType> typeOptional = TreeManager.treeRoot.getTypes().getType(itemStack);
			if (!typeOptional.isPresent()) {
				return;
			}
			IOrganismType type = typeOptional.get();
			IGenome genome = tree.getGenome();

			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

			textLayout.drawLine(transform, Translator.translateToLocal("for.gui.active"), GuiAlyzer.COLUMN_1);
			textLayout.drawLine(transform, Translator.translateToLocal("for.gui.inactive"), GuiAlyzer.COLUMN_2);

			textLayout.newLine();
			textLayout.newLine();

			guiAlyzer.drawSpeciesRow(transform, Translator.translateToLocal("for.gui.species"), tree, TreeChromosomes.SPECIES, type);
			textLayout.newLine();

			guiAlyzer.drawChromosomeRow(transform, Translator.translateToLocal("for.gui.saplings"), tree, TreeChromosomes.FERTILITY);
			textLayout.newLineCompressed();
			guiAlyzer.drawChromosomeRow(transform, Translator.translateToLocal("for.gui.maturity"), tree, TreeChromosomes.MATURATION);
			textLayout.newLineCompressed();
			guiAlyzer.drawChromosomeRow(transform, Translator.translateToLocal("for.gui.height"), tree, TreeChromosomes.HEIGHT);
			textLayout.newLineCompressed();

			int activeGirth = genome.getActiveValue(TreeChromosomes.GIRTH);
			int inactiveGirth = genome.getInactiveValue(TreeChromosomes.GIRTH);
			textLayout.drawLine(transform, Translator.translateToLocal("for.gui.girth"), GuiAlyzer.COLUMN_0);
			guiAlyzer.drawLine(transform, String.format("%sx%s", activeGirth, activeGirth), GuiAlyzer.COLUMN_1, tree, TreeChromosomes.GIRTH, false);
			guiAlyzer.drawLine(transform, String.format("%sx%s", inactiveGirth, inactiveGirth), GuiAlyzer.COLUMN_2, tree, TreeChromosomes.GIRTH, true);

			textLayout.newLineCompressed();

			guiAlyzer.drawChromosomeRow(transform, Translator.translateToLocal("for.gui.yield"), tree, TreeChromosomes.YIELD);
			textLayout.newLineCompressed();
			guiAlyzer.drawChromosomeRow(transform, Translator.translateToLocal("for.gui.sappiness"), tree, TreeChromosomes.SAPPINESS);
			textLayout.newLineCompressed();

			guiAlyzer.drawChromosomeRow(transform, Translator.translateToLocal("for.gui.effect"), tree, TreeChromosomes.EFFECT);

			textLayout.endPage();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawAnalyticsPage2(MatrixStack transform, Screen gui, ItemStack itemStack) {
		if (gui instanceof GuiAlyzer) {
			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
			Optional<ITree> optional = TreeManager.treeRoot.create(itemStack);
			if (!optional.isPresent()) {
				return;
			}
			ITree tree = optional.get();
			IGenome genome = tree.getGenome();
			IAlleleTreeSpecies primary = genome.getActiveAllele(TreeChromosomes.SPECIES);
			IAlleleTreeSpecies secondary = genome.getInactiveAllele(TreeChromosomes.SPECIES);
			IAlleleFruit activeFruit = tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS);
			IAlleleFruit inactiveFruit = tree.getGenome().getInactiveAllele(TreeChromosomes.FRUITS);
			IFruitFamily primaryFamily = activeFruit.getProvider().getFamily();
			IFruitFamily secondaryFamily = inactiveFruit.getProvider().getFamily();

			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

			int speciesDominance0 = guiAlyzer.getColorCoding(primary.isDominant());
			int speciesDominance1 = guiAlyzer.getColorCoding(genome.getSecondary().isDominant());

			textLayout.drawLine(transform, Translator.translateToLocal("for.gui.active"), GuiAlyzer.COLUMN_1);
			textLayout.drawLine(transform, Translator.translateToLocal("for.gui.inactive"), GuiAlyzer.COLUMN_2);

			textLayout.newLine();
			textLayout.newLine();

			String yes = Translator.translateToLocal("for.yes");
			String no = Translator.translateToLocal("for.no");

			String fireproofActive = StringUtil.readableBoolean(genome.getActiveValue(TreeChromosomes.FIREPROOF), yes, no);
			String fireproofInactive = StringUtil.readableBoolean(genome.getInactiveValue(TreeChromosomes.FIREPROOF), yes, no);

			guiAlyzer.drawRow(transform, Translator.translateToLocal("for.gui.fireproof"), fireproofActive, fireproofInactive, tree, TreeChromosomes.FIREPROOF);

			textLayout.newLine();

			textLayout.drawLine(transform, Translator.translateToLocal("for.gui.native"), GuiAlyzer.COLUMN_0);
			textLayout.drawLine(transform, Translator.translateToLocal("for.gui." + primary.getPlantType().getName()), GuiAlyzer.COLUMN_1,
					speciesDominance0);
			textLayout.drawLine(transform, Translator.translateToLocal("for.gui." + secondary.getPlantType().getName()), GuiAlyzer.COLUMN_2,
					speciesDominance1);

			textLayout.newLine();

			// FRUITS
			textLayout.drawTranslatedLine(transform, "for.gui.supports", GuiAlyzer.COLUMN_0);
			List<IFruitFamily> families0 = new ArrayList<>(primary.getSuitableFruit());
			List<IFruitFamily> families1 = new ArrayList<>(secondary.getSuitableFruit());

			int max = Math.max(families0.size(), families1.size());
			for (int i = 0; i < max; i++) {
				if (i > 0) {
					textLayout.newLineCompressed();
				}

				if (families0.size() > i) {
					textLayout.drawLine(transform, families0.get(i).getName(), GuiAlyzer.COLUMN_1, speciesDominance0);
				}
				if (families1.size() > i) {
					textLayout.drawLine(transform, families1.get(i).getName(), GuiAlyzer.COLUMN_2, speciesDominance1);
				}

			}

			textLayout.newLine();

			int fruitDominance0 = guiAlyzer.getColorCoding(activeFruit.isDominant());
			int fruitDominance1 = guiAlyzer.getColorCoding(inactiveFruit.isDominant());

			textLayout.drawTranslatedLine(transform, "for.gui.fruits", GuiAlyzer.COLUMN_0);
			TextFormatting strike = TextFormatting.RESET;
			if (!tree.canBearFruit() && activeFruit != AlleleFruits.fruitNone) {
				strike = TextFormatting.STRIKETHROUGH;
			}
			textLayout.drawLine(transform, activeFruit.getProvider().getDescription().withStyle(strike), GuiAlyzer.COLUMN_1, fruitDominance0);

			strike = TextFormatting.RESET;
			if (!secondary.getSuitableFruit().contains(inactiveFruit.getProvider().getFamily()) && inactiveFruit != AlleleFruits.fruitNone) {
				strike = TextFormatting.STRIKETHROUGH;
			}
			textLayout.drawLine(transform, inactiveFruit.getProvider().getDescription().withStyle(strike), GuiAlyzer.COLUMN_2, fruitDominance1);

			textLayout.newLine();

			textLayout.drawTranslatedLine(transform, "for.gui.family", GuiAlyzer.COLUMN_0);

			if (!primaryFamily.getUID().equals(EnumFruitFamily.NONE.getUID())) {
				textLayout.drawLine(transform, primaryFamily.getName(), GuiAlyzer.COLUMN_1, fruitDominance0);
			}
			if (!secondaryFamily.getUID().equals(EnumFruitFamily.NONE.getUID())) {
				textLayout.drawLine(transform, secondaryFamily.getName(), GuiAlyzer.COLUMN_2, fruitDominance1);
			}

			textLayout.endPage();
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawAnalyticsPage3(MatrixStack transform, ItemStack itemStack, Screen gui) {
		if (gui instanceof GuiAlyzer) {
			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
			Optional<ITree> optional = TreeManager.treeRoot.create(itemStack);
			if (!optional.isPresent()) {
				return;
			}
			ITree tree = optional.get();

			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();
			WidgetManager widgetManager = guiAlyzer.getWidgetManager();

			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

			textLayout.drawLine(transform, Translator.translateToLocal("for.gui.beealyzer.produce") + ":", GuiAlyzer.COLUMN_0);
			textLayout.newLine();

			int x = GuiAlyzer.COLUMN_0;
			for (ItemStack stack : tree.getProducts().getPossibleStacks()) {
				widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), stack));
				x += 18;
				if (x > 148) {
					x = GuiAlyzer.COLUMN_0;
					textLayout.newLine();
				}
			}

			textLayout.newLine();
			textLayout.newLine();
			textLayout.newLine();
			textLayout.newLine();

			textLayout.drawLine(transform, Translator.translateToLocal("for.gui.beealyzer.specialty") + ":", GuiAlyzer.COLUMN_0);
			textLayout.newLine();

			x = GuiAlyzer.COLUMN_0;
			for (ItemStack stack : tree.getSpecialties().getPossibleStacks()) {
				Minecraft.getInstance().getItemRenderer().renderGuiItem(stack, guiAlyzer.getGuiLeft() + x, guiAlyzer.getGuiTop() + textLayout.getLineY());
				x += 18;
				if (x > 148) {
					x = GuiAlyzer.COLUMN_0;
					textLayout.newLine();
				}
			}

			textLayout.endPage();
		}
	}

	@Override
	public Map<ResourceLocation, ItemStack> getIconStacks() {
		return iconStacks;
	}

	@Override
	public List<String> getHints() {
		return Config.hints.get("treealyzer");
	}

}
