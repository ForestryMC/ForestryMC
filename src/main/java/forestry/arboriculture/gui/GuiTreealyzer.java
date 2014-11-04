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

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.ITree;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleEffect;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.genetics.TreeGenome;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemTreealyzer.TreealyzerInventory;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.Allele;
import forestry.core.genetics.AlleleBoolean;
import forestry.core.genetics.AllelePlantType;
import forestry.core.gui.GuiAlyzer;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginArboriculture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumPlantType;

import java.util.ArrayList;
import java.util.Locale;

public class GuiTreealyzer extends GuiAlyzer {

	private ItemStack[] tempProductList;

	public GuiTreealyzer(EntityPlayer player, TreealyzerInventory inventory) {
		super(AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees"), player,
				new ContainerTreealyzer(player.inventory, inventory), inventory, 1, inventory.getSizeInventory());

		guiName = "gui.treealyzer";

		ArrayList<ItemStack> treeList = new ArrayList<ItemStack>();
		((ItemGermlingGE) ForestryItem.sapling.item()).addCreativeItems(treeList, false);
		for (ItemStack treeStack : treeList)
			iconStacks.put(TreeGenome.getSpecies(treeStack).getUID(), treeStack);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int page = 0;
		ITree tree = null;
		EnumGermlingType treeType = EnumGermlingType.SAPLING;
		for (int k = 1; k < TreealyzerInventory.SLOT_ANALYZE_5 + 1; k++) {
			if (k == TreealyzerInventory.SLOT_ENERGY)
				continue;

			if (inventory.getStackInSlot(k) == null)
				continue;
			tree = PluginArboriculture.treeInterface.getMember(inventory.getStackInSlot(k));
			treeType = PluginArboriculture.treeInterface.getType(inventory.getStackInSlot(k));
			if (tree == null || !tree.isAnalyzed())
				continue;

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
			drawAnalyticsPage4(tree);
			break;
		case 6:
			drawAnalyticsPageClassification(tree);
			break;
		default:
			drawAnalyticsOverview();
		}

	}

	private void drawAnalyticsPage1(ITree tree, EnumGermlingType type) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		newLine();
		newLine();

		/*
		IAlleleTreeSpecies primary = tree.getGenome().getPrimaryAsTree();
		IAlleleTreeSpecies secondary = tree.getGenome().getSecondaryAsTree();

		drawLine(StringUtil.localize("gui.species"), COLUMN_0);
		drawSplitLine(primary.getName(), COLUMN_1, COLUMN_2 - COLUMN_1 - 4, tree, EnumTreeChromosome.SPECIES, false);
		drawSplitLine(secondary.getName(), COLUMN_2, COLUMN_2 - COLUMN_1 - 4, tree, EnumTreeChromosome.SPECIES, true);

		newLine();
		newLine();*/

		{
			String customPrimaryTreeKey = "trees.custom.treealyzer." + type.getName() + "." + tree.getGenome().getPrimary().getUnlocalizedName().replace("trees.species.", "");
			String customSecondaryTreeKey = "trees.custom.treealyzer." + type.getName() + "." + tree.getGenome().getSecondary().getUnlocalizedName().replace("trees.species.", "");

			drawSpeciesRow(StringUtil.localize("gui.species"), tree, EnumTreeChromosome.SPECIES, checkCustomName(customPrimaryTreeKey), checkCustomName(customSecondaryTreeKey));
		}
		newLine();

		drawLine(StringUtil.localize("gui.saplings"), COLUMN_0);
		drawLine(tree.getGenome().getActiveAllele(EnumTreeChromosome.FERTILITY.ordinal()).getName(), COLUMN_1, tree, EnumTreeChromosome.FERTILITY, false);
		drawLine(tree.getGenome().getInactiveAllele(EnumTreeChromosome.FERTILITY.ordinal()).getName(), COLUMN_2, tree,
				EnumTreeChromosome.FERTILITY, true);

		newLine();

		drawRow(StringUtil.localize("gui.maturity"), tree.getGenome().getActiveAllele(EnumTreeChromosome.MATURATION.ordinal()).getName(),
				tree.getGenome().getInactiveAllele(EnumTreeChromosome.MATURATION.ordinal()).getName(), tree,
				EnumTreeChromosome.MATURATION);

		drawLine(StringUtil.localize("gui.height"), COLUMN_0);
		drawLine(tree.getGenome().getActiveAllele(EnumTreeChromosome.HEIGHT.ordinal()).getName(), COLUMN_1, tree, EnumTreeChromosome.HEIGHT, false);
		drawLine(tree.getGenome().getInactiveAllele(EnumTreeChromosome.HEIGHT.ordinal()).getName(), COLUMN_2, tree,
				EnumTreeChromosome.HEIGHT, true);

		newLine();

		drawLine(StringUtil.localize("gui.girth"), COLUMN_0);
		drawLine(String.format("%sx%s", tree.getGenome().getGirth(), tree.getGenome().getGirth()), COLUMN_1, tree, EnumTreeChromosome.FERTILITY, false);
		int secondGirth = ((IAlleleInteger) tree.getGenome().getInactiveAllele(EnumTreeChromosome.GIRTH.ordinal())).getValue();
		drawLine(String.format("%sx%s", secondGirth, secondGirth), COLUMN_2, tree, EnumTreeChromosome.FERTILITY, true);

		newLine();

		drawLine(StringUtil.localize("gui.yield"), COLUMN_0);
		drawLine(tree.getGenome().getActiveAllele(EnumTreeChromosome.YIELD.ordinal()).getName(), COLUMN_1, tree, EnumTreeChromosome.YIELD, false);
		drawLine(tree.getGenome().getInactiveAllele(EnumTreeChromosome.YIELD.ordinal()).getName(), COLUMN_2, tree,
				EnumTreeChromosome.YIELD, true);

		newLine();

		drawLine(StringUtil.localize("gui.sappiness"), COLUMN_0);
		drawLine(tree.getGenome().getActiveAllele(EnumTreeChromosome.SAPPINESS.ordinal()).getName(), COLUMN_1, tree, EnumTreeChromosome.SAPPINESS, false);

		// FIXME: Legacy handling
		IAllele sappiness = tree.getGenome().getInactiveAllele(EnumTreeChromosome.SAPPINESS.ordinal());
		String sap;
		if (sappiness instanceof IAlleleFloat)
			sap = sappiness.getName();
		else
			sap = Allele.saplingsLowest.getName();

		drawLine(sap, COLUMN_2, tree, EnumTreeChromosome.SAPPINESS, true);

		newLine();

		String yes = StringUtil.localize("yes");
		String no = StringUtil.localize("no");

		AlleleBoolean primaryFireproof = (AlleleBoolean)tree.getGenome().getActiveAllele(EnumTreeChromosome.FIREPROOF.ordinal());
		AlleleBoolean secondaryFireproof = (AlleleBoolean)tree.getGenome().getInactiveAllele(EnumTreeChromosome.FIREPROOF.ordinal());

		drawLine(StringUtil.localize("gui.fireproof"), COLUMN_0);
		drawLine(StringUtil.readableBoolean(primaryFireproof.getValue(), yes, no), COLUMN_1, tree, EnumTreeChromosome.FIREPROOF, false);
		drawLine(StringUtil.readableBoolean(secondaryFireproof.getValue(), yes, no), COLUMN_2, tree, EnumTreeChromosome.FIREPROOF, false);

		newLine();

		drawRow(StringUtil.localize("gui.effect"), tree.getGenome().getEffect().getName(),
				((IAlleleEffect) tree.getGenome().getInactiveAllele(EnumTreeChromosome.EFFECT.ordinal())).getName(), tree,
				EnumTreeChromosome.EFFECT);

		newLine();
		newLine();

		endPage();
	}

	private void drawAnalyticsPage2(ITree tree) {

		startPage();

		int speciesDominance0 = getColorCoding(tree.getGenome().getPrimary().isDominant());
		int speciesDominance1 = getColorCoding(tree.getGenome().getSecondary().isDominant());

		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		newLine();
		newLine();

		drawLine(StringUtil.localize("gui.growth"), COLUMN_0);
		drawLine(tree.getGenome().getGrowthProvider().getDescription(), COLUMN_1, tree, EnumTreeChromosome.GROWTH, false);
		drawLine(((IAlleleGrowth) tree.getGenome().getInactiveAllele(EnumTreeChromosome.GROWTH.ordinal())).getProvider().getDescription(), COLUMN_2, tree,
				EnumTreeChromosome.GROWTH, true);

		newLine();

		drawLine(StringUtil.localize("gui.native"), COLUMN_0);
		drawLine(StringUtil.localize("gui." + tree.getGenome().getPrimary().getPlantType().toString().toLowerCase(Locale.ENGLISH)), COLUMN_1,
				speciesDominance0);
		drawLine(StringUtil.localize("gui." + tree.getGenome().getSecondary().getPlantType().toString().toLowerCase(Locale.ENGLISH)), COLUMN_2,
				speciesDominance1);

		newLine();

		drawLine(StringUtil.localize("gui.tolerated"), COLUMN_0);

		EnumPlantType[] tolerated0 = tree.getGenome().getPlantTypes().toArray(new EnumPlantType[0]);
		EnumPlantType[] tolerated1 = new EnumPlantType[0];
		IAllele allele1 = tree.getGenome().getInactiveAllele(EnumTreeChromosome.PLANT.ordinal());
		if (allele1 instanceof AllelePlantType)
			tolerated1 = ((AllelePlantType) allele1).getPlantTypes().toArray(new EnumPlantType[0]);

		int max = tolerated0.length > tolerated1.length ? tolerated0.length : tolerated1.length;
		for (int i = 0; i < max; i++) {
			if (i > 0)
				newLine();
			if(tolerated0.length > i)
				drawLine(StringUtil.localize("gui." + tolerated0[i].toString().toLowerCase(Locale.ENGLISH)), COLUMN_1, tree, EnumTreeChromosome.PLANT, false);
			if(tolerated1.length > i)
				drawLine(StringUtil.localize("gui." + tolerated1[i].toString().toLowerCase(Locale.ENGLISH)), COLUMN_2, tree, EnumTreeChromosome.PLANT, true);
		}
		newLine();

		// FRUITS
		drawLine(StringUtil.localize("gui.supports"), COLUMN_0);
		IFruitFamily[] families0 = tree.getGenome().getPrimary().getSuitableFruit().toArray(new IFruitFamily[0]);
		IFruitFamily[] families1 = tree.getGenome().getPrimary().getSuitableFruit().toArray(new IFruitFamily[0]);

		max = families0.length > families1.length ? families0.length : families1.length;
		for (int i = 0; i < max; i++) {
			if (i > 0)
				newLine();

			if (families0.length > i)
				drawLine(families0[i].getName(), COLUMN_1, speciesDominance0);
			if (families1.length > i)
				drawLine(families1[i].getName(), COLUMN_2, speciesDominance1);

		}

		newLine();
		newLine();

		int fruitDominance0 = getColorCoding(tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS.ordinal()).isDominant());
		int fruitDominance1 = getColorCoding(tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS.ordinal()).isDominant());

		drawLine(StringUtil.localize("gui.fruits"), COLUMN_0);
		String strike = "";
		IAllele fruit0 = tree.getGenome().getActiveAllele(EnumTreeChromosome.FRUITS.ordinal());
		if (!tree.canBearFruit() && fruit0 != Allele.fruitNone)
			strike = "\u00A7m";
		drawLine(strike + StringUtil.localize(tree.getGenome().getFruitProvider().getDescription()), COLUMN_1, fruitDominance0);

		strike = "";
		IAllele fruit1 = tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS.ordinal());
		if (!tree.getGenome().getSecondary().getSuitableFruit().contains(((IAlleleFruit) fruit1).getProvider().getFamily()) && fruit1 != Allele.fruitNone)
			strike = "\u00A7m";
		drawLine(strike + StringUtil.localize(((IAlleleFruit) fruit1).getProvider().getDescription()), COLUMN_2, fruitDominance1);

		newLine();

		drawLine(StringUtil.localize("gui.family"), COLUMN_0);
		IFruitFamily primary = tree.getGenome().getFruitProvider().getFamily();
		IFruitFamily secondary = ((IAlleleFruit) tree.getGenome().getInactiveAllele(EnumTreeChromosome.FRUITS.ordinal())).getProvider().getFamily();

		if (primary != null)
			drawLine(primary.getName(), COLUMN_1, fruitDominance0);
		if (secondary != null)
			drawLine(secondary.getName(), COLUMN_2, fruitDominance1);

		endPage();
	}

	private void drawAnalyticsPage3(ITree tree) {

		tempProductList = tree.getProduceList();

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		drawLine(StringUtil.localize("gui.beealyzer.produce") + ":", COLUMN_0);
		newLine();

		int x = COLUMN_0;
		for (ItemStack stack : tempProductList) {
			itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, stack, (int) ((guiLeft + x) * (1 / factor)),
					(int) ((guiTop + getLineY()) * (1 / factor)));
			x += 18;
			if (x > adjustToFactor(148)) {
				x = COLUMN_0;
				newLine();
			}
		}

		newLine();
		newLine();

		drawLine(StringUtil.localize("gui.beealyzer.specialty") + ":", COLUMN_0);
		newLine();

		x = COLUMN_0;
		for (ItemStack stack : tree.getSpecialtyList()) {
			itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, stack, (int) ((guiLeft + x) * (1 / factor)),
					(int) ((guiTop + getLineY()) * (1 / factor)));
			x += 18;
			if (x > adjustToFactor(148)) {
				x = COLUMN_0;
				newLine();
			}
		}

		endPage();
	}

}
