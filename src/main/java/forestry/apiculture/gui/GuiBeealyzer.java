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

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.apiculture.genetics.BeeGenome;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemBeealyzer.BeealyzerInventory;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.AlleleArea;
import forestry.core.genetics.AlleleBoolean;
import forestry.core.gui.GuiAlyzer;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Vect;
import forestry.plugins.PluginApiculture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class GuiBeealyzer extends GuiAlyzer {

	private ItemStack[] tempProductList;

	public GuiBeealyzer(EntityPlayer player, BeealyzerInventory inventory) {
		super(AlleleManager.alleleRegistry.getSpeciesRoot("rootBees"), player,
				new ContainerBeealyzer(player.inventory, inventory), inventory, 1, inventory.getSizeInventory());

		guiName = "gui.beealyzer";

		ArrayList<ItemStack> beeList = new ArrayList<ItemStack>();
		((ItemBeeGE) ForestryItem.beeDroneGE.item()).addCreativeItems(beeList, false);
		for (ItemStack beeStack : beeList)
			iconStacks.put(BeeGenome.getSpecies(beeStack).getUID(), beeStack);

		breedingTracker = PluginApiculture.beeInterface.getBreedingTracker(player.worldObj, player.getGameProfile());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int page = 0;
		IBee bee = null;
		EnumBeeType beeType = EnumBeeType.DRONE;
		for (int k = 1; k < BeealyzerInventory.SLOT_ANALYZE_5 + 1; k++) {
			if (k == BeealyzerInventory.SLOT_ENERGY)
				continue;

			if (inventory.getStackInSlot(k) == null)
				continue;
			bee = PluginApiculture.beeInterface.getMember(inventory.getStackInSlot(k));
			beeType = PluginApiculture.beeInterface.getType(inventory.getStackInSlot(k));
			if (bee == null || !bee.isAnalyzed())
				continue;

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
			drawAnalyticsPage3(bee, beeType);
			break;
		case 4:
			drawAnalyticsPage4(bee);
			break;
		case 6:
			drawAnalyticsPageClassification(bee);
			break;
		default:
			drawAnalyticsOverview();
		}

	}

	private void drawAnalyticsPage1(IBee bee, EnumBeeType type) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		newLine();
		newLine();

		{
			String customPrimaryBeeKey = "bees.custom.beealyzer." + type.getName() + "." + bee.getGenome().getPrimary().getUnlocalizedName().replace("bees.species.", "");
			String customSecondaryBeeKey = "bees.custom.beealyzer." + type.getName() + "." + bee.getGenome().getSecondary().getUnlocalizedName().replace("bees.species.", "");

			drawSpeciesRow(StringUtil.localize("gui.species"), bee, EnumBeeChromosome.SPECIES, checkCustomName(customPrimaryBeeKey), checkCustomName(customSecondaryBeeKey));
		}
		drawRow(StringUtil.localize("gui.lifespan"), bee.getGenome().getActiveAllele(EnumBeeChromosome.LIFESPAN.ordinal()).getName(),
				bee.getGenome().getInactiveAllele(EnumBeeChromosome.LIFESPAN.ordinal()).getName(), bee,
				EnumBeeChromosome.LIFESPAN);

		drawRow(StringUtil.localize("gui.speed"), bee.getGenome().getActiveAllele(EnumBeeChromosome.SPEED.ordinal()).getName(),
				bee.getGenome().getInactiveAllele(EnumBeeChromosome.SPEED.ordinal()).getName(), bee, EnumBeeChromosome.SPEED);

		drawRow(StringUtil.localize("gui.pollination"), bee.getGenome().getActiveAllele(EnumBeeChromosome.FLOWERING.ordinal()).getName(),
				bee.getGenome().getInactiveAllele(EnumBeeChromosome.FLOWERING.ordinal()).getName(), bee,
				EnumBeeChromosome.FLOWERING);

		drawRow(StringUtil.localize("gui.flowers"), bee.getGenome().getFlowerProvider().getDescription(),
				((IAlleleFlowers) bee.getGenome().getInactiveAllele(EnumBeeChromosome.FLOWER_PROVIDER.ordinal())).getProvider()
						.getDescription(), bee, EnumBeeChromosome.FLOWER_PROVIDER);

		drawLine(StringUtil.localize("gui.fertility"), COLUMN_0);
		drawFertilityInfo(bee.getGenome().getFertility(), COLUMN_1, getColorCoding(bee.getGenome().getActiveAllele(EnumBeeChromosome.FERTILITY.ordinal())
				.isDominant()), 0);
		drawFertilityInfo(((IAlleleInteger) bee.getGenome().getInactiveAllele(EnumBeeChromosome.FERTILITY.ordinal())).getValue(), COLUMN_2, getColorCoding(bee
				.getGenome().getInactiveAllele(EnumBeeChromosome.FERTILITY.ordinal()).isDominant()), 0);

		newLine();

		int[] areaAr = bee.getGenome().getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]);
		drawRow(StringUtil.localize("gui.area"), area.toString(), ((AlleleArea) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TERRITORY.ordinal()))
				.getArea().toString(), bee, EnumBeeChromosome.TERRITORY);

		drawRow(StringUtil.localize("gui.effect"), bee.getGenome().getEffect().getName(),
				bee.getGenome().getInactiveAllele(EnumBeeChromosome.EFFECT.ordinal()).getName(), bee,
				EnumBeeChromosome.EFFECT);

		newLine();

		endPage();
	}

	private void drawAnalyticsPage2(IBee bee, EnumBeeType type) {

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		drawLine(StringUtil.localize("gui.active"), COLUMN_1);
		drawLine(StringUtil.localize("gui.inactive"), COLUMN_2);

		newLine();
		newLine();

		drawRow(StringUtil.localize("gui.climate"), AlleleManager.climateHelper.toDisplay(bee.getGenome().getPrimary().getTemperature()),
				AlleleManager.climateHelper.toDisplay(bee.getGenome().getSecondary().getTemperature()), bee, EnumBeeChromosome.SPECIES);

		IAlleleTolerance tempToleranceActive = (IAlleleTolerance)bee.getGenome().getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal());
		IAlleleTolerance tempToleranceInactive = (IAlleleTolerance)bee.getGenome().getInactiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal());
		drawLine(StringUtil.localize("gui.temptol"), COLUMN_0);
		drawToleranceInfo(tempToleranceActive, COLUMN_1);
		drawToleranceInfo(tempToleranceInactive, COLUMN_2);

		newLine();

		drawRow(StringUtil.localize("gui.humidity"), AlleleManager.climateHelper.toDisplay(bee.getGenome().getPrimary().getHumidity()),
				AlleleManager.climateHelper.toDisplay(bee.getGenome().getSecondary().getHumidity()), bee, EnumBeeChromosome.SPECIES);

		IAlleleTolerance humidToleranceActive = (IAlleleTolerance)bee.getGenome().getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal());
		IAlleleTolerance humidToleranceInactive = (IAlleleTolerance)bee.getGenome().getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal());
		drawLine(StringUtil.localize("gui.humidtol"), COLUMN_0);
		drawToleranceInfo(humidToleranceActive, COLUMN_1);
		drawToleranceInfo(humidToleranceInactive, COLUMN_2);

		newLine();
		newLine();

		String yes = StringUtil.localize("yes");
		String no = StringUtil.localize("no");

		String diurnal0, diurnal1, nocturnal0, nocturnal1;
		if(bee.getGenome().getNocturnal()) {
			nocturnal0 = diurnal0 = yes;
		} else {
			nocturnal0 = bee.getGenome().getPrimary().isNocturnal() ? yes : no;
			diurnal0 = !bee.getGenome().getPrimary().isNocturnal() ? yes : no;
		}
		if(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.NOCTURNAL.ordinal())).getValue()) {
			nocturnal1 = diurnal1 = yes;
		} else {
			nocturnal1 = bee.getGenome().getSecondary().isNocturnal() ? yes : no;
			diurnal1 = !bee.getGenome().getSecondary().isNocturnal() ? yes : no;
		}

		drawLine(StringUtil.localize("gui.diurnal"), COLUMN_0);
		drawLine(diurnal0, COLUMN_1, getColorCoding(false));
		drawLine(diurnal1, COLUMN_2, getColorCoding(false));
		newLine();

		drawLine(StringUtil.localize("gui.nocturnal"), COLUMN_0);
		drawLine(nocturnal0, COLUMN_1, getColorCoding(false));
		drawLine(nocturnal1, COLUMN_2, getColorCoding(false));
		newLine();

		String primary = StringUtil.readableBoolean(bee.getGenome().getTolerantFlyer(), yes, no);
		String secondary = StringUtil.readableBoolean(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TOLERANT_FLYER.ordinal())).getValue(), yes,
				no);

		drawRow(StringUtil.localize("gui.flyer"), primary, secondary, bee, EnumBeeChromosome.TOLERANT_FLYER);

		primary = StringUtil.readableBoolean(bee.getGenome().getCaveDwelling(), yes, no);
		secondary = StringUtil.readableBoolean(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.CAVE_DWELLING.ordinal())).getValue(), yes,
				no);

		drawRow(StringUtil.localize("gui.cave"), primary, secondary, bee, EnumBeeChromosome.CAVE_DWELLING);

		newLine();

		String displayText;
		if (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN) {
			displayText = "bees.stock.pristine";
			if (!bee.isNatural())
				displayText = "bees.stock.ignoble";
			displayText = StringUtil.localize(displayText);
			drawCenteredLine(displayText, 8, 208, fontColor.get("gui.beealyzer.binomial"));
		}

		if (bee.getGeneration() >= 0) {
			newLine();

			displayText = StringUtil.localizeAndFormat("gui.beealyzer.generations", bee.getGeneration());
			drawCenteredLine(displayText, 8, 208, fontColor.get("gui.beealyzer.binomial"));
		}

		endPage();
	}

	private void drawAnalyticsPage3(IBee bee, EnumBeeType type) {

		float factor = this.factor;
		this.setFactor(1.0f);

		tempProductList = bee.getProduceList();

		startPage(COLUMN_0, COLUMN_1, COLUMN_2);

		drawLine(StringUtil.localize("gui.beealyzer.produce") + ":", COLUMN_0);

		newLine();

		int x = COLUMN_0;
		for (ItemStack stack : tempProductList) {
			widgetManager.add(new ItemStackWidget(adjustToFactor(x), adjustToFactor(getLineY()), stack));

			x += 18;
			if (x > adjustToFactor(148)) {
				x = COLUMN_0;
				newLine();
			}
		}

		newLine();
		newLine();
		newLine();

		drawLine(StringUtil.localize("gui.beealyzer.specialty") + ":", COLUMN_0);

		x = COLUMN_0;
		for (ItemStack stack : bee.getSpecialtyList()) {
			widgetManager.add(new ItemStackWidget(adjustToFactor(x), adjustToFactor(getLineY()), stack));

			x += 18;
			if (x > adjustToFactor(148)) {
				x = COLUMN_0;
				newLine();
			}
		}

		endPage();

		this.setFactor(factor);
	}

}
