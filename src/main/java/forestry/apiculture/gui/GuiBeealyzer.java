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
package forestry.apiculture.gui;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.apiculture.genetics.BeeGenome;
import forestry.apiculture.inventory.ItemInventoryBeealyzer;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginApiculture;

public class GuiBeealyzer extends GuiAlyzer {

	public GuiBeealyzer(EntityPlayer player, ItemInventoryBeealyzer inventory) {
		super(BeeManager.beeRoot, player, new ContainerAlyzer(inventory, player), inventory, "gui.beealyzer");

		ArrayList<ItemStack> beeList = new ArrayList<>();
		PluginApiculture.items.beeDroneGE.addCreativeItems(beeList, false);
		for (ItemStack beeStack : beeList) {
			IAlleleBeeSpecies species = BeeGenome.getSpecies(beeStack);
			if (species != null) {
				iconStacks.put(species.getUID(), beeStack);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int page = 0;
		IBee bee = null;
		EnumBeeType beeType = EnumBeeType.DRONE;

		for (int k = ItemInventoryBeealyzer.SLOT_SPECIMEN; k <= ItemInventoryBeealyzer.SLOT_ANALYZE_5; k++) {
			if (k == ItemInventoryBeealyzer.SLOT_ENERGY) {
				continue;
			}

			if (inventory.getStackInSlot(k) == null) {
				continue;
			}
			bee = BeeManager.beeRoot.getMember(inventory.getStackInSlot(k));
			beeType = BeeManager.beeRoot.getType(inventory.getStackInSlot(k));
			if (bee == null || !bee.isAnalyzed()) {
				continue;
			}

			page = k;
			break;
		}

		switch (page) {
			case 1:
				drawAnalyticsPage1(bee, beeType);
				break;
			case 2:
				drawAnalyticsPage2(bee, beeType);
				break;
			case 3:
				drawAnalyticsPage3(bee);
				break;
			case 4:
				drawAnalyticsPageMutations(bee);
				break;
			case 6:
				drawAnalyticsPageClassification(bee);
				break;
			default:
				drawAnalyticsOverview();
		}

	}

	private void drawAnalyticsPage1(IBee bee, EnumBeeType type) {

		textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		textLayout.drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		textLayout.drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		textLayout.newLine();
		textLayout.newLine();

		{
			String customPrimaryBeeKey = "bees.custom.beealyzer." + type.getName() + "." + bee.getGenome().getPrimary().getUnlocalizedName().replace("bees.species.", "");
			String customSecondaryBeeKey = "bees.custom.beealyzer." + type.getName() + "." + bee.getGenome().getSecondary().getUnlocalizedName().replace("bees.species.", "");

			drawSpeciesRow(StringUtil.localize("gui.species"), bee, EnumBeeChromosome.SPECIES, checkCustomName(customPrimaryBeeKey), checkCustomName(customSecondaryBeeKey));
			textLayout.newLine();
		}

		drawChromosomeRow(StringUtil.localize("gui.lifespan"), bee, EnumBeeChromosome.LIFESPAN);
		textLayout.newLine();
		drawChromosomeRow(StringUtil.localize("gui.speed"), bee, EnumBeeChromosome.SPEED);
		textLayout.newLine();
		drawChromosomeRow(StringUtil.localize("gui.pollination"), bee, EnumBeeChromosome.FLOWERING);
		textLayout.newLine();
		drawChromosomeRow(StringUtil.localize("gui.flowers"), bee, EnumBeeChromosome.FLOWER_PROVIDER);
		textLayout.newLine();

		textLayout.drawLine(StringUtil.localize("gui.fertility"), COLUMN_0);
		IAlleleInteger primaryFertility = (IAlleleInteger) bee.getGenome().getActiveAllele(EnumBeeChromosome.FERTILITY);
		IAlleleInteger secondaryFertility = (IAlleleInteger) bee.getGenome().getInactiveAllele(EnumBeeChromosome.FERTILITY);
		drawFertilityInfo(primaryFertility.getValue(), COLUMN_1, getColorCoding(primaryFertility.isDominant()), 0);
		drawFertilityInfo(secondaryFertility.getValue(), COLUMN_2, getColorCoding(secondaryFertility.isDominant()), 0);
		textLayout.newLine();

		drawChromosomeRow(StringUtil.localize("gui.area"), bee, EnumBeeChromosome.TERRITORY);
		textLayout.newLine();

		drawChromosomeRow(StringUtil.localize("gui.effect"), bee, EnumBeeChromosome.EFFECT);
		textLayout.newLine();

		textLayout.endPage();
	}

	private void drawAnalyticsPage2(IBee bee, EnumBeeType type) {

		textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		textLayout.drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		textLayout.drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		textLayout.newLine();

		drawRow(StringUtil.localize("gui.climate"), AlleleManager.climateHelper.toDisplay(bee.getGenome().getPrimary().getTemperature()),
				AlleleManager.climateHelper.toDisplay(bee.getGenome().getSecondary().getTemperature()), bee, EnumBeeChromosome.SPECIES);

		textLayout.newLine();

		IAlleleTolerance tempToleranceActive = (IAlleleTolerance) bee.getGenome().getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE);
		IAlleleTolerance tempToleranceInactive = (IAlleleTolerance) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE);
		textLayout.drawLine("  " + StringUtil.localize("gui.tolerance"), COLUMN_0);
		drawToleranceInfo(tempToleranceActive, COLUMN_1);
		drawToleranceInfo(tempToleranceInactive, COLUMN_2);

		textLayout.newLine(16);

		drawRow(StringUtil.localize("gui.humidity"), AlleleManager.climateHelper.toDisplay(bee.getGenome().getPrimary().getHumidity()),
				AlleleManager.climateHelper.toDisplay(bee.getGenome().getSecondary().getHumidity()), bee, EnumBeeChromosome.SPECIES);

		textLayout.newLine();

		IAlleleTolerance humidToleranceActive = (IAlleleTolerance) bee.getGenome().getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE);
		IAlleleTolerance humidToleranceInactive = (IAlleleTolerance) bee.getGenome().getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE);
		textLayout.drawLine("  " + StringUtil.localize("gui.tolerance"), COLUMN_0);
		drawToleranceInfo(humidToleranceActive, COLUMN_1);
		drawToleranceInfo(humidToleranceInactive, COLUMN_2);

		textLayout.newLine(16);

		String yes = StringUtil.localize("yes");
		String no = StringUtil.localize("no");

		String diurnal0, diurnal1, nocturnal0, nocturnal1;
		if (bee.getGenome().getNocturnal()) {
			nocturnal0 = diurnal0 = yes;
		} else {
			nocturnal0 = bee.getGenome().getPrimary().isNocturnal() ? yes : no;
			diurnal0 = !bee.getGenome().getPrimary().isNocturnal() ? yes : no;
		}
		if (((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.NOCTURNAL)).getValue()) {
			nocturnal1 = diurnal1 = yes;
		} else {
			nocturnal1 = bee.getGenome().getSecondary().isNocturnal() ? yes : no;
			diurnal1 = !bee.getGenome().getSecondary().isNocturnal() ? yes : no;
		}

		textLayout.drawLine(StringUtil.localize("gui.diurnal"), COLUMN_0);
		textLayout.drawLine(diurnal0, COLUMN_1, getColorCoding(false));
		textLayout.drawLine(diurnal1, COLUMN_2, getColorCoding(false));
		textLayout.newLineCompressed();

		textLayout.drawLine(StringUtil.localize("gui.nocturnal"), COLUMN_0);
		textLayout.drawLine(nocturnal0, COLUMN_1, getColorCoding(false));
		textLayout.drawLine(nocturnal1, COLUMN_2, getColorCoding(false));
		textLayout.newLineCompressed();

		String primary = StringUtil.readableBoolean(bee.getGenome().getTolerantFlyer(), yes, no);
		String secondary = StringUtil.readableBoolean(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TOLERANT_FLYER)).getValue(), yes, no);

		drawRow(StringUtil.localize("gui.flyer"), primary, secondary, bee, EnumBeeChromosome.TOLERANT_FLYER);

		textLayout.newLineCompressed();

		primary = StringUtil.readableBoolean(bee.getGenome().getCaveDwelling(), yes, no);
		secondary = StringUtil.readableBoolean(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.CAVE_DWELLING)).getValue(), yes, no);

		drawRow(StringUtil.localize("gui.cave"), primary, secondary, bee, EnumBeeChromosome.CAVE_DWELLING);

		textLayout.newLine();

		String displayText;
		if (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN) {
			displayText = "bees.stock.pristine";
			if (!bee.isNatural()) {
				displayText = "bees.stock.ignoble";
			}
			displayText = StringUtil.localize(displayText);
			textLayout.drawCenteredLine(displayText, 8, 208, fontColor.get("gui.beealyzer.binomial"));
		}

		if (bee.getGeneration() >= 0) {
			textLayout.newLineCompressed();
			displayText = StringUtil.localizeAndFormat("gui.beealyzer.generations", bee.getGeneration());
			textLayout.drawCenteredLine(displayText, 8, 208, fontColor.get("gui.beealyzer.binomial"));
		}

		textLayout.endPage();
	}

	private void drawAnalyticsPage3(IBee bee) {

		textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		textLayout.drawLine(StringUtil.localize("gui.beealyzer.produce") + ":", COLUMN_0);

		textLayout.newLine();

		int x = COLUMN_0;
		for (ItemStack stack : bee.getProduceList()) {
			widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), stack));

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
		for (ItemStack stack : bee.getSpecialtyList()) {
			widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), stack));

			x += 18;
			if (x > 148) {
				x = COLUMN_0;
				textLayout.newLine();
			}
		}

		textLayout.endPage();
	}

}
