/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.lepidopterology.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleEffect;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IMutation;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.AlleleBoolean;
import forestry.core.genetics.AlleleTolerance;
import forestry.core.genetics.GenericRatings;
import forestry.core.gui.GuiAlyzer;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.items.ItemFlutterlyzer.FlutterlyzerInventory;
import forestry.plugins.PluginLepidopterology;

public class GuiFlutterlyzer extends GuiAlyzer {

	private ItemStack[] tempProductList;

	public GuiFlutterlyzer(EntityPlayer player, FlutterlyzerInventory inventory) {
		super(AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies"), player,
				new ContainerFlutterlyzer(player.inventory, inventory), inventory, 1, inventory.getSizeInventory());

		xSize = 196;
		ySize = 238;

		ArrayList<ItemStack> butterflyList = new ArrayList<ItemStack>();
		((ItemButterflyGE) ForestryItem.butterflyGE.item()).addCreativeItems(butterflyList, false);
		for (ItemStack butterflyStack : butterflyList)
			iconStacks.put(PluginLepidopterology.butterflyInterface.getMember(butterflyStack).getIdent(), butterflyStack);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int page = 0;
		IButterfly butterfly = null;
		for (int k = 1; k < FlutterlyzerInventory.SLOT_ANALYZE_5 + 1; k++) {
			if (k == FlutterlyzerInventory.SLOT_ENERGY)
				continue;

			if (inventory.getStackInSlot(k) == null)
				continue;
			butterfly = PluginLepidopterology.butterflyInterface.getMember(inventory.getStackInSlot(k));
			if (butterfly == null || !butterfly.isAnalyzed())
				continue;

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
			drawAnalyticsPage4(butterfly);
			break;
		case 6:
			drawAnalyticsPageClassification(butterfly);
			break;
		default:
			drawAnalyticsOverview();
		}

	}

	private void drawAnalyticsOverview() {

		startPage();

		newLine();
		String title = StringUtil.localize("item.flutterlyzer").toUpperCase();
		drawCenteredLine(title, 8, 158);
		newLine();

		fontRendererObj.drawSplitString(StringUtil.localize("gui.flutterlyzer.help"), (int) ((guiLeft + COLUMN_0 + 4) * (1 / factor)),
				(int) ((guiTop + 42) * (1 / factor)), (int) (158 * (1 / factor)), fontColor.get("gui.screen"));
		newLine();
		newLine();
		newLine();
		newLine();

		drawLine(StringUtil.localize("gui.beealyzer.overview") + ":", COLUMN_0 + 4);
		newLine();
		drawLine("I  : " + StringUtil.localize("gui.general"), COLUMN_0 + 4);
		newLine();
		drawLine("II : " + StringUtil.localize("gui.environment"), COLUMN_0 + 4);
		newLine();
		drawLine("III: " + StringUtil.localize("gui.produce"), COLUMN_0 + 4);
		newLine();
		drawLine("IV : " + StringUtil.localize("gui.evolution"), COLUMN_0 + 4);

		endPage();
	}

	private void drawAnalyticsPage1(IButterfly butterfly) {
		startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		newLine();
		newLine();

		drawSpeciesRow(StringUtil.localize("gui.species"), butterfly, EnumButterflyChromosome.SPECIES);
		newLine();

		drawRow(StringUtil.localize("gui.size"), butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.SIZE.ordinal()).getName(),
				butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.SIZE.ordinal()).getName(), butterfly, EnumButterflyChromosome.SPEED);

		drawRow(StringUtil.localize("gui.lifespan"), butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.LIFESPAN.ordinal()).getName(),
				butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.LIFESPAN.ordinal()).getName(), butterfly,
				EnumButterflyChromosome.LIFESPAN);

		drawRow(StringUtil.localize("gui.speed"), butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.SPEED.ordinal()).getName(),
				butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.SPEED.ordinal()).getName(), butterfly, EnumButterflyChromosome.SPEED);

		drawRow(StringUtil.localize("gui.metabolism"), GenericRatings.rateMetabolism(butterfly.getGenome().getMetabolism()),
				GenericRatings.rateMetabolism(((IAlleleInteger) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.METABOLISM.ordinal())).getValue()), butterfly, EnumButterflyChromosome.METABOLISM);

		drawLine(StringUtil.localize("gui.fertility"), COLUMN_0);
		drawFertilityInfo(butterfly.getGenome().getFertility(), COLUMN_1, getColorCoding(butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.FERTILITY.ordinal())
				.isDominant()), 8);
		drawFertilityInfo(((IAlleleInteger) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.FERTILITY.ordinal())).getValue(), COLUMN_2, getColorCoding(butterfly
				.getGenome().getInactiveAllele(EnumButterflyChromosome.FERTILITY.ordinal()).isDominant()), 8);

		newLine();

		drawRow(StringUtil.localize("gui.flowers"), StringUtil.localize(butterfly.getGenome().getFlowerProvider().getDescription()),
				StringUtil.localize(((IAlleleFlowers) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.FLOWER_PROVIDER.ordinal())).getProvider()
						.getDescription()), butterfly, EnumButterflyChromosome.FLOWER_PROVIDER);

		drawRow(StringUtil.localize("gui.effect"), StringUtil.localize(butterfly.getGenome().getEffect().getName()),
				StringUtil.localize(((IAlleleEffect) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.EFFECT.ordinal())).getName()), butterfly,
				EnumButterflyChromosome.EFFECT);

		newLine();


		endPage();

	}
	private void drawAnalyticsPage2(IButterfly butterfly) {
		startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		newLine();
		newLine();

		drawRow(StringUtil.localize("gui.climate"), AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getTemperature()),
				AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getTemperature()), butterfly, EnumButterflyChromosome.SPECIES);

		drawLine(StringUtil.localize("gui.temptol"), COLUMN_0);
		drawToleranceInfo(butterfly.getGenome().getToleranceTemp(), COLUMN_1,
				getColorCoding(butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()).isDominant()));
		drawToleranceInfo(((AlleleTolerance) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal())).getValue(), COLUMN_2,
				getColorCoding(butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()).isDominant()));

		newLine();

		drawRow(StringUtil.localize("gui.humidity"), AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getHumidity()),
				AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getHumidity()), butterfly, EnumButterflyChromosome.SPECIES);

		drawLine(StringUtil.localize("gui.humidtol"), COLUMN_0);
		drawToleranceInfo(butterfly.getGenome().getToleranceHumid(), COLUMN_1,
				getColorCoding(butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal()).isDominant()));
		drawToleranceInfo(((AlleleTolerance) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal())).getValue(), COLUMN_2,
				getColorCoding(butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal()).isDominant()));

		newLine();
		newLine();

		String yes = StringUtil.localize("yes");
		String no = StringUtil.localize("no");

		String diurnal0, diurnal1, nocturnal0, nocturnal1;
		if(butterfly.getGenome().getNocturnal()) {
			nocturnal0 = diurnal0 = yes;
		} else {
			nocturnal0 = butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
			diurnal0 = !butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
		}
		if(((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.NOCTURNAL.ordinal())).getValue()) {
			nocturnal1 = diurnal1 = yes;
		} else {
			nocturnal1 = butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
			diurnal1 = !butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
		}

		drawLine(StringUtil.localize("gui.diurnal"), COLUMN_0);
		drawLine(diurnal0, COLUMN_1, getColorCoding(false));
		drawLine(diurnal1, COLUMN_2, getColorCoding(false));
		newLine();

		drawLine(StringUtil.localize("gui.nocturnal"), COLUMN_0);
		drawLine(nocturnal0, COLUMN_1, getColorCoding(false));
		drawLine(nocturnal1, COLUMN_2, getColorCoding(false));
		newLine();

		String primary = StringUtil.readableBoolean(butterfly.getGenome().getTolerantFlyer(), yes, no);
		String secondary = StringUtil.readableBoolean(((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.TOLERANT_FLYER.ordinal())).getValue(), yes,
				no);

		drawRow(StringUtil.localize("gui.flyer"), primary, secondary, butterfly, EnumButterflyChromosome.TOLERANT_FLYER);

		primary = StringUtil.readableBoolean(butterfly.getGenome().getFireResist(), yes, no);
		secondary = StringUtil.readableBoolean(((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.FIRE_RESIST.ordinal())).getValue(), yes,
				no);

		drawRow(StringUtil.localize("gui.fireresist"), primary, secondary, butterfly, EnumButterflyChromosome.FIRE_RESIST);

		endPage();

	}
	private void drawAnalyticsPage3(IButterfly butterfly) {

		tempProductList = butterfly.getGenome().getPrimary().getButterflyLoot().keySet().toArray(StackUtils.EMPTY_STACK_ARRAY);

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		drawLine(StringUtil.localize("gui.loot.butterfly") + ":", COLUMN_0);
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

		drawLine(StringUtil.localize("gui.loot.caterpillar") + ":", COLUMN_0);
		newLine();

		x = COLUMN_0;
		for (ItemStack stack : butterfly.getGenome().getPrimary().getCaterpillarLoot().keySet().toArray(StackUtils.EMPTY_STACK_ARRAY)) {
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
	private void drawAnalyticsPage4(IButterfly butterfly) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);
		drawLine(StringUtil.localize("gui.beealyzer.mutations") + ":", COLUMN_0);
		newLine();
		newLine();

		RenderHelper.enableGUIStandardItemLighting();

		HashMap<IMutation, IAllele> combinations = new HashMap<IMutation, IAllele>();

		/*
		for (IMutation mutation : TreeTemplates.getCombinations(butterfly.getGenome().getPrimary()))
			combinations.put(mutation, butterfly.getGenome().getPrimary());

		for (IMutation mutation : TreeTemplates.getCombinations(butterfly.getGenome().getSecondary()))
			combinations.put(mutation, butterfly.getGenome().getSecondary());
		 */

		int columnWidth = 50;
		int x = 0;

		for (Map.Entry<IMutation, IAllele> mutation : combinations.entrySet()) {

			//if (breedingTracker.isDiscovered(mutation.getKey()))
			drawMutationInfo(mutation.getKey(), mutation.getValue(), COLUMN_0 + x);
			/*else {
				// Do not display secret undiscovered mutations.
				if (mutation.getKey().isSecret())
					continue;

				drawUnknownMutation(mutation.getKey(), mutation.getValue(), COLUMN_0 + x);
			} */

			x += columnWidth;
			if (x > 150) {
				x = 0;
				newLine();
				newLine();
			}
		}

		endPage();

	}
}
