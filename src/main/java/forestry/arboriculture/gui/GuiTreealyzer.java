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
package forestry.arboriculture.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.genetics.TreeGenome;
import forestry.arboriculture.genetics.alleles.AlleleFruit;
import forestry.arboriculture.inventory.ItemInventoryTreealyzer;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.genetics.alleles.AllelePlantType;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.gui.GuiAlyzer;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginArboriculture;

public class GuiTreealyzer extends GuiAlyzer {

	public GuiTreealyzer(EntityPlayer player, ItemInventoryTreealyzer inventory) {
		super(TreeManager.treeRoot, player, new ContainerAlyzer(inventory, player), inventory, "gui.treealyzer");

		ArrayList<ItemStack> treeList = new ArrayList<>();
		PluginArboriculture.items.sapling.addCreativeItems(treeList, false);
		for (ItemStack treeStack : treeList) {
			IAlleleTreeSpecies species = TreeGenome.getSpecies(treeStack);
			if (species != null) {
				iconStacks.put(species.getUID(), treeStack);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int page = 0;
		ITree tree = null;
		EnumGermlingType treeType = EnumGermlingType.SAPLING;
		for (int k = 1; k < ItemInventoryTreealyzer.SLOT_ANALYZE_5 + 1; k++) {
			if (k == ItemInventoryTreealyzer.SLOT_ENERGY) {
				continue;
			}

			if (inventory.getStackInSlot(k) == null) {
				continue;
			}
			tree = TreeManager.treeRoot.getMember(inventory.getStackInSlot(k));
			treeType = TreeManager.treeRoot.getType(inventory.getStackInSlot(k));
			if (tree == null || !tree.isAnalyzed()) {
				continue;
			}

			page = k;
			break;
		}

		switch (page) {
			case 1:
				drawAnalyticsPage1(tree, treeType);
				break;
			case 2:
				drawAnalyticsPage2(tree);
				break;
			case 3:
				drawAnalyticsPage3(tree);
				break;
			case 4:
				drawAnalyticsPageMutations(tree);
				break;
			case 6:
				drawAnalyticsPageClassification(tree);
				break;
			default:
				drawAnalyticsOverview();
		}

	}

	private void drawAnalyticsPage1(ITree tree, EnumGermlingType type) {

		textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		textLayout.drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		textLayout.drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		textLayout.newLine();
		textLayout.newLine();

		{
			String customPrimaryTreeKey = "trees.custom.treealyzer." + type.getName() + "." + tree.getGenome().getPrimary().getUnlocalizedName().replace("trees.species.", "");
			String customSecondaryTreeKey = "trees.custom.treealyzer." + type.getName() + "." + tree.getGenome().getSecondary().getUnlocalizedName().replace("trees.species.", "");

			drawSpeciesRow(StringUtil.localize("gui.species"), tree, EnumTreeChromosome.SPECIES, checkCustomName(customPrimaryTreeKey), checkCustomName(customSecondaryTreeKey));
			textLayout.newLine();
		}

		drawChromosomeRow(StringUtil.localize("gui.saplings"), tree, EnumTreeChromosome.FERTILITY);
		textLayout.newLineCompressed();
		drawChromosomeRow(StringUtil.localize("gui.maturity"), tree, EnumTreeChromosome.MATURATION);
		textLayout.newLineCompressed();
		drawChromosomeRow(StringUtil.localize("gui.height"), tree, EnumTreeChromosome.HEIGHT);
		textLayout.newLineCompressed();

		IAlleleInteger activeGirth = (IAlleleInteger) tree.getGenome().getActiveAllele(EnumTreeChromosome.GIRTH);
		IAlleleInteger inactiveGirth = (IAlleleInteger) tree.getGenome().getInactiveAllele(EnumTreeChromosome.GIRTH);
		textLayout.drawLine(StringUtil.localize("gui.girth"), COLUMN_0);
		drawLine(String.format("%sx%s", activeGirth.getValue(), activeGirth.getValue()), COLUMN_1, tree, EnumTreeChromosome.GIRTH, false);
		drawLine(String.format("%sx%s", inactiveGirth.getValue(), inactiveGirth.getValue()), COLUMN_2, tree, EnumTreeChromosome.GIRTH, true);

		textLayout.newLineCompressed();

		drawChromosomeRow(StringUtil.localize("gui.yield"), tree, EnumTreeChromosome.YIELD);
		textLayout.newLineCompressed();
		drawChromosomeRow(StringUtil.localize("gui.sappiness"), tree, EnumTreeChromosome.SAPPINESS);
		textLayout.newLineCompressed();

		String yes = StringUtil.localize("yes");
		String no = StringUtil.localize("no");

		AlleleBoolean primaryFireproof = (AlleleBoolean) tree.getGenome().getActiveAllele(EnumTreeChromosome.FIREPROOF);
		AlleleBoolean secondaryFireproof = (AlleleBoolean) tree.getGenome().getInactiveAllele(EnumTreeChromosome.FIREPROOF);

		textLayout.drawLine(StringUtil.localize("gui.fireproof"), COLUMN_0);
		drawLine(StringUtil.readableBoolean(primaryFireproof.getValue(), yes, no), COLUMN_1, tree, EnumTreeChromosome.FIREPROOF, false);
		drawLine(StringUtil.readableBoolean(secondaryFireproof.getValue(), yes, no), COLUMN_2, tree, EnumTreeChromosome.FIREPROOF, false);

		textLayout.newLineCompressed();

		drawChromosomeRow(StringUtil.localize("gui.effect"), tree, EnumTreeChromosome.EFFECT);

		textLayout.endPage();
	}

	private void drawAnalyticsPage2(ITree tree) {

		textLayout.startPage();

		int speciesDominance0 = getColorCoding(tree.getGenome().getPrimary().isDominant());
		int speciesDominance1 = getColorCoding(tree.getGenome().getSecondary().isDominant());

		textLayout.drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		textLayout.drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		textLayout.newLine();
		textLayout.newLine();

		textLayout.drawLine(StringUtil.localize("gui.growth"), COLUMN_0);
		drawLine(tree.getGenome().getGrowthProvider().getDescription(), COLUMN_1, tree, EnumTreeChromosome.GROWTH, false);
		drawLine(((IAlleleGrowth) tree.getGenome().getInactiveAllele(EnumTreeChromosome.GROWTH)).getProvider().getDescription(), COLUMN_2, tree,
				EnumTreeChromosome.GROWTH, true);

		textLayout.newLine();

		textLayout.drawLine(StringUtil.localize("gui.native"), COLUMN_0);
		textLayout.drawLine(StringUtil.localize("gui." + tree.getGenome().getPrimary().getPlantType().toString().toLowerCase(Locale.ENGLISH)), COLUMN_1,
				speciesDominance0);
		textLayout.drawLine(StringUtil.localize("gui." + tree.getGenome().getSecondary().getPlantType().toString().toLowerCase(Locale.ENGLISH)), COLUMN_2,
				speciesDominance1);

		textLayout.newLine();

		textLayout.drawLine(StringUtil.localize("gui.tolerated"), COLUMN_0);

		List<EnumPlantType> activeTolerated = new ArrayList<>(tree.getGenome().getPlantTypes());
		List<EnumPlantType> inactiveTolerated = Collections.emptyList();

		IAllele inactiveAllelePlant = tree.getGenome().getInactiveAllele(EnumTreeChromosome.PLANT);
		if (inactiveAllelePlant instanceof AllelePlantType) {
			inactiveTolerated = new ArrayList<>(((AllelePlantType) inactiveAllelePlant).getPlantTypes());
		}

		int max = Math.max(activeTolerated.size(), inactiveTolerated.size());
		for (int i = 0; i < max; i++) {
			if (i > 0) {
				textLayout.newLine();
			}
			if (activeTolerated.size() > i) {
				drawLine(StringUtil.localize("gui." + activeTolerated.get(i).toString().toLowerCase(Locale.ENGLISH)), COLUMN_1, tree, EnumTreeChromosome.PLANT, false);
			}
			if (inactiveTolerated.size() > i) {
				drawLine(StringUtil.localize("gui." + inactiveTolerated.get(i).toString().toLowerCase(Locale.ENGLISH)), COLUMN_2, tree, EnumTreeChromosome.PLANT, true);
			}
		}
		textLayout.newLine();

		// FRUITS
		textLayout.drawLine(StringUtil.localize("gui.supports"), COLUMN_0);
		List<IFruitFamily> families0 = new ArrayList<>(tree.getGenome().getPrimary().getSuitableFruit());
		List<IFruitFamily> families1 = new ArrayList<>(tree.getGenome().getSecondary().getSuitableFruit());

		max = Math.max(families0.size(), families1.size());
		for (int i = 0; i < max; i++) {
			if (i > 0) {
				textLayout.newLine();
			}

			if (families0.size() > i) {
				textLayout.drawLine(families0.get(i).getName(), COLUMN_1, speciesDominance0);
			}
			if (families1.size() > i) {
				textLayout.drawLine(families1.get(i).getName(), COLUMN_2, speciesDominance1);
			}

		}

		textLayout.newLine();
		textLayout.newLine();

		int fruitDominance0 = getColorCoding(tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS).isDominant());
		int fruitDominance1 = getColorCoding(tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS).isDominant());

		textLayout.drawLine(StringUtil.localize("gui.fruits"), COLUMN_0);
		String strike = "";
		IAllele fruit0 = tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS);
		if (!tree.canBearFruit() && fruit0 != AlleleFruit.fruitNone) {
			strike = EnumChatFormatting.STRIKETHROUGH.toString();
		}
		textLayout.drawLine(strike + StringUtil.localize(tree.getGenome().getFruitProvider().getDescription()), COLUMN_1, fruitDominance0);

		strike = "";
		IAllele fruit1 = tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS);
		if (!tree.getGenome().getSecondary().getSuitableFruit().contains(((IAlleleFruit) fruit1).getProvider().getFamily()) && fruit1 != AlleleFruit.fruitNone) {
			strike = EnumChatFormatting.STRIKETHROUGH.toString();
		}
		textLayout.drawLine(strike + StringUtil.localize(((IAlleleFruit) fruit1).getProvider().getDescription()), COLUMN_2, fruitDominance1);

		textLayout.newLine();

		textLayout.drawLine(StringUtil.localize("gui.family"), COLUMN_0);
		IFruitFamily primary = tree.getGenome().getFruitProvider().getFamily();
		IFruitFamily secondary = ((IAlleleFruit) tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS)).getProvider().getFamily();

		if (primary != null) {
			textLayout.drawLine(primary.getName(), COLUMN_1, fruitDominance0);
		}
		if (secondary != null) {
			textLayout.drawLine(secondary.getName(), COLUMN_2, fruitDominance1);
		}

		textLayout.endPage();
	}

	private void drawAnalyticsPage3(ITree tree) {

		textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		textLayout.drawLine(StringUtil.localize("gui.beealyzer.produce") + ":", COLUMN_0);
		textLayout.newLine();

		int x = COLUMN_0;
		for (ItemStack stack : tree.getProduceList()) {
			itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, stack, guiLeft + x, guiTop + textLayout.getLineY());
			x += 18;
			if (x > 148) {
				x = COLUMN_0;
				textLayout.newLine();
			}
		}

		textLayout.newLine();
		textLayout.newLine();
		textLayout.newLine();
		textLayout.newLine();

		textLayout.drawLine(StringUtil.localize("gui.beealyzer.specialty") + ":", COLUMN_0);
		textLayout.newLine();

		x = COLUMN_0;
		for (ItemStack stack : tree.getSpecialtyList()) {
			itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, stack, guiLeft + x, guiTop + textLayout.getLineY());
			x += 18;
			if (x > 148) {
				x = COLUMN_0;
				textLayout.newLine();
			}
		}

		textLayout.endPage();
	}

}
