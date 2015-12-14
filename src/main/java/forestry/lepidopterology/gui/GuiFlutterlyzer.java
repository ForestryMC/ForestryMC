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
package forestry.lepidopterology.gui;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.genetics.GenericRatings;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.gui.GuiAlyzer;
import forestry.core.utils.StringUtil;
import forestry.lepidopterology.genetics.ButterflyGenome;
import forestry.lepidopterology.inventory.ItemInventoryFlutterlyzer;
import forestry.plugins.PluginLepidopterology;

public class GuiFlutterlyzer extends GuiAlyzer {

	public GuiFlutterlyzer(EntityPlayer player, ItemInventoryFlutterlyzer inventory) {
		super(ButterflyManager.butterflyRoot, player, new ContainerAlyzer(inventory, player), inventory, "gui.flutterlyzer");

		ArrayList<ItemStack> butterflyList = new ArrayList<>();
		PluginLepidopterology.items.butterflyGE.addCreativeItems(butterflyList, false);
		for (ItemStack butterflyStack : butterflyList) {
			IAlleleButterflySpecies species = ButterflyGenome.getSpecies(butterflyStack);
			if (species != null) {
				iconStacks.put(species.getUID(), butterflyStack);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int page = 0;
		IButterfly butterfly = null;
		for (int k = 1; k < ItemInventoryFlutterlyzer.SLOT_ANALYZE_5 + 1; k++) {
			if (k == ItemInventoryFlutterlyzer.SLOT_ENERGY) {
				continue;
			}

			if (inventory.getStackInSlot(k) == null) {
				continue;
			}
			butterfly = ButterflyManager.butterflyRoot.getMember(inventory.getStackInSlot(k));
			if (butterfly == null || !butterfly.isAnalyzed()) {
				continue;
			}

			page = k;
			break;
		}

		switch (page) {
			case 1:
				drawAnalyticsPage1(butterfly);
				break;
			case 2:
				drawAnalyticsPage2(butterfly);
				break;
			case 3:
				drawAnalyticsPage3(butterfly);
				break;
			case 4:
				drawAnalyticsPageMutations(butterfly);
				break;
			case 6:
				drawAnalyticsPageClassification(butterfly);
				break;
			default:
				drawAnalyticsOverview();
		}

	}

	private void drawAnalyticsPage1(IButterfly butterfly) {
		textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		textLayout.drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		textLayout.drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		textLayout.newLine();
		textLayout.newLine();

		drawSpeciesRow(StringUtil.localize("gui.species"), butterfly, EnumButterflyChromosome.SPECIES, null, null);
		textLayout.newLine();

		drawRow(StringUtil.localize("gui.size"), butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.SIZE).getName(),
				butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.SIZE).getName(), butterfly, EnumButterflyChromosome.SPEED);
		textLayout.newLine();

		drawRow(StringUtil.localize("gui.lifespan"), butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.LIFESPAN).getName(),
				butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.LIFESPAN).getName(), butterfly,
				EnumButterflyChromosome.LIFESPAN);
		textLayout.newLine();

		drawRow(StringUtil.localize("gui.speed"), butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.SPEED).getName(),
				butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.SPEED).getName(), butterfly, EnumButterflyChromosome.SPEED);
		textLayout.newLine();

		drawRow(StringUtil.localize("gui.metabolism"), GenericRatings.rateMetabolism(butterfly.getGenome().getMetabolism()),
				GenericRatings.rateMetabolism(((IAlleleInteger) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.METABOLISM)).getValue()), butterfly, EnumButterflyChromosome.METABOLISM);
		textLayout.newLine();

		textLayout.drawLine(StringUtil.localize("gui.fertility"), COLUMN_0);
		drawFertilityInfo(butterfly.getGenome().getFertility(), COLUMN_1, getColorCoding(butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.FERTILITY)
				.isDominant()), 8);
		drawFertilityInfo(((IAlleleInteger) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.FERTILITY)).getValue(), COLUMN_2, getColorCoding(butterfly
				.getGenome().getInactiveAllele(EnumButterflyChromosome.FERTILITY).isDominant()), 8);

		textLayout.newLine();

		drawRow(StringUtil.localize("gui.flowers"), butterfly.getGenome().getFlowerProvider().getDescription(),
				((IAlleleFlowers) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.FLOWER_PROVIDER)).getProvider()
						.getDescription(), butterfly, EnumButterflyChromosome.FLOWER_PROVIDER);
		textLayout.newLine();

		drawRow(StringUtil.localize("gui.effect"), butterfly.getGenome().getEffect().getName(),
				butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.EFFECT).getName(), butterfly,
				EnumButterflyChromosome.EFFECT);

		textLayout.newLine();

		textLayout.endPage();
	}

	private void drawAnalyticsPage2(IButterfly butterfly) {
		textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		textLayout.drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		textLayout.drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		textLayout.newLine();
		textLayout.newLine();

		drawRow(StringUtil.localize("gui.climate"), AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getTemperature()),
				AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getTemperature()), butterfly, EnumButterflyChromosome.SPECIES);
		textLayout.newLine();

		IAlleleTolerance tempToleranceActive = (IAlleleTolerance) butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE);
		IAlleleTolerance tempToleranceInactive = (IAlleleTolerance) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE);
		textLayout.drawLine("  " + StringUtil.localize("gui.tolerance"), COLUMN_0);
		drawToleranceInfo(tempToleranceActive, COLUMN_1);
		drawToleranceInfo(tempToleranceInactive, COLUMN_2);

		textLayout.newLine();

		drawRow(StringUtil.localize("gui.humidity"), AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getHumidity()),
				AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getHumidity()), butterfly, EnumButterflyChromosome.SPECIES);
		textLayout.newLine();

		IAlleleTolerance humidToleranceActive = (IAlleleTolerance) butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE);
		IAlleleTolerance humidToleranceInactive = (IAlleleTolerance) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE);
		textLayout.drawLine("  " + StringUtil.localize("gui.tolerance"), COLUMN_0);
		drawToleranceInfo(humidToleranceActive, COLUMN_1);
		drawToleranceInfo(humidToleranceInactive, COLUMN_2);

		textLayout.newLine();
		textLayout.newLine();

		String yes = StringUtil.localize("yes");
		String no = StringUtil.localize("no");

		String diurnal0, diurnal1, nocturnal0, nocturnal1;
		if (butterfly.getGenome().getNocturnal()) {
			nocturnal0 = diurnal0 = yes;
		} else {
			nocturnal0 = butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
			diurnal0 = !butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
		}
		if (((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.NOCTURNAL)).getValue()) {
			nocturnal1 = diurnal1 = yes;
		} else {
			nocturnal1 = butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
			diurnal1 = !butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
		}

		textLayout.drawLine(StringUtil.localize("gui.diurnal"), COLUMN_0);
		textLayout.drawLine(diurnal0, COLUMN_1, getColorCoding(false));
		textLayout.drawLine(diurnal1, COLUMN_2, getColorCoding(false));
		textLayout.newLine();

		textLayout.drawLine(StringUtil.localize("gui.nocturnal"), COLUMN_0);
		textLayout.drawLine(nocturnal0, COLUMN_1, getColorCoding(false));
		textLayout.drawLine(nocturnal1, COLUMN_2, getColorCoding(false));
		textLayout.newLine();

		String primary = StringUtil.readableBoolean(butterfly.getGenome().getTolerantFlyer(), yes, no);
		String secondary = StringUtil.readableBoolean(((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.TOLERANT_FLYER)).getValue(), yes,
				no);

		drawRow(StringUtil.localize("gui.flyer"), primary, secondary, butterfly, EnumButterflyChromosome.TOLERANT_FLYER);
		textLayout.newLine();

		primary = StringUtil.readableBoolean(butterfly.getGenome().getFireResist(), yes, no);
		secondary = StringUtil.readableBoolean(((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.FIRE_RESIST)).getValue(), yes,
				no);

		drawRow(StringUtil.localize("gui.fireresist"), primary, secondary, butterfly, EnumButterflyChromosome.FIRE_RESIST);

		textLayout.endPage();
	}

	private void drawAnalyticsPage3(IButterfly butterfly) {

		textLayout.startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		textLayout.drawLine(StringUtil.localize("gui.loot.butterfly") + ":", COLUMN_0);
		textLayout.newLine();

		int x = COLUMN_0;
		for (ItemStack stack : butterfly.getGenome().getPrimary().getButterflyLoot().keySet()) {
			itemRender.renderItemIntoGUI(fontRendererObj, mc.renderEngine, stack, guiLeft + x, guiTop + textLayout.getLineY());
			x += 18;
			if (x > 148) {
				x = COLUMN_0;
				textLayout.newLine();
			}
		}

		textLayout.newLine();
		textLayout.newLine();

		textLayout.drawLine(StringUtil.localize("gui.loot.caterpillar") + ":", COLUMN_0);
		textLayout.newLine();

		x = COLUMN_0;
		for (ItemStack stack : butterfly.getGenome().getPrimary().getCaterpillarLoot().keySet()) {
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
