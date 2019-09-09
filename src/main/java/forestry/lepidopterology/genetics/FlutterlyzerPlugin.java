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
package forestry.lepidopterology.genetics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.genetics.IAlyzerPlugin;
import forestry.core.config.Config;

//TODO: Port plugin
public class FlutterlyzerPlugin implements IAlyzerPlugin {
	public static final FlutterlyzerPlugin INSTANCE = new FlutterlyzerPlugin();

	protected final Map<ResourceLocation, ItemStack> iconStacks = new HashMap<>();

	private FlutterlyzerPlugin() {
		//		NonNullList<ItemStack> butterflyList = NonNullList.create();
		//		ModuleLepidopterology.getItems().butterflyGE.addCreativeItems(butterflyList, false);
		//		for (ItemStack butterflyStack : butterflyList) {
		//			IAlleleButterflySpecies species = ButterflyGenome.getSpecies(butterflyStack);
		//			iconStacks.put(species.getUID(), butterflyStack);
		//		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawAnalyticsPage1(Screen gui, ItemStack itemStack) {
		//		if (gui instanceof GuiAlyzer) {
		//			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
		//			IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(itemStack);
		//			if (butterfly == null) {
		//				return;
		//			}
		//
		//			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();
		//
		//			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);
		//
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.active"), GuiAlyzer.COLUMN_1);
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.inactive"), GuiAlyzer.COLUMN_2);
		//
		//			textLayout.newLine();
		//			textLayout.newLine();
		//
		//			guiAlyzer.drawSpeciesRow(Translator.translateToLocal("for.gui.species"), butterfly, ButterflyChromosomes.SPECIES, null, null);
		//			textLayout.newLine();
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.size"), butterfly.getGenome().getActiveAllele(ButterflyChromosomes.SIZE).getAlleleName(),
		//				butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.SIZE).getAlleleName(), butterfly, ButterflyChromosomes.SPEED);
		//			textLayout.newLine();
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.lifespan"), butterfly.getGenome().getActiveAllele(ButterflyChromosomes.LIFESPAN).getAlleleName(),
		//				butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.LIFESPAN).getAlleleName(), butterfly,
		//				ButterflyChromosomes.LIFESPAN);
		//			textLayout.newLine();
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.speed"), butterfly.getGenome().getActiveAllele(ButterflyChromosomes.SPEED).getAlleleName(),
		//				butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.SPEED).getAlleleName(), butterfly, ButterflyChromosomes.SPEED);
		//			textLayout.newLine();
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.metabolism"), GenericRatings.rateMetabolism(butterfly.getGenome().getMetabolism()),
		//				GenericRatings.rateMetabolism(((IAlleleInteger) butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.METABOLISM)).getValue()), butterfly, ButterflyChromosomes.METABOLISM);
		//			textLayout.newLine();
		//
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.fertility"), GuiAlyzer.COLUMN_0);
		//			guiAlyzer.drawFertilityInfo(butterfly.getGenome().getFertility(), GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(butterfly.getGenome().getActiveAllele(ButterflyChromosomes.FERTILITY)
		//				.isDominant()), 8);
		//			guiAlyzer.drawFertilityInfo(((IAlleleInteger) butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.FERTILITY)).getValue(), GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(butterfly
		//				.getGenome().getInactiveAllele(ButterflyChromosomes.FERTILITY).isDominant()), 8);
		//
		//			textLayout.newLine();
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.flowers"), butterfly.getGenome().getFlowerProvider().getDescription(),
		//				((IAlleleFlowers) butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.FLOWER_PROVIDER)).getProvider()
		//					.getDescription(), butterfly, ButterflyChromosomes.FLOWER_PROVIDER);
		//			textLayout.newLine();
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.effect"), butterfly.getGenome().getEffect().getAlleleName(),
		//				butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.EFFECT).getAlleleName(), butterfly,
		//				ButterflyChromosomes.EFFECT);
		//
		//			textLayout.newLine();
		//
		//			textLayout.endPage();
		//		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawAnalyticsPage2(Screen gui, ItemStack itemStack) {
		//		if (gui instanceof GuiAlyzer) {
		//			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
		//			IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(itemStack);
		//			if (butterfly == null) {
		//				return;
		//			}
		//
		//			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();
		//
		//			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);
		//
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.active"), GuiAlyzer.COLUMN_1);
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.inactive"), GuiAlyzer.COLUMN_2);
		//
		//			textLayout.newLine();
		//			textLayout.newLine();
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.climate"), AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getTemperature()).getUnformattedComponentText(),	//TODO ITextComponent
		//				AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getTemperature()).getUnformattedComponentText(), butterfly, ButterflyChromosomes.SPECIES);
		//			textLayout.newLine();
		//
		//			IAlleleTolerance tempToleranceActive = (IAlleleTolerance) butterfly.getGenome().getActiveAllele(ButterflyChromosomes.TEMPERATURE_TOLERANCE);
		//			IAlleleTolerance tempToleranceInactive = (IAlleleTolerance) butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.TEMPERATURE_TOLERANCE);
		//			textLayout.drawLine("  " + Translator.translateToLocal("for.gui.tolerance"), GuiAlyzer.COLUMN_0);
		//			guiAlyzer.drawToleranceInfo(tempToleranceActive, GuiAlyzer.COLUMN_1);
		//			guiAlyzer.drawToleranceInfo(tempToleranceInactive, GuiAlyzer.COLUMN_2);
		//
		//			textLayout.newLine();
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.humidity"), AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getHumidity()).getUnformattedComponentText(),	//TODO ITextComponent
		//				AlleleManager.climateHelper.toDisplay(butterfly.getGenome().getPrimary().getHumidity()).getUnformattedComponentText(), butterfly, ButterflyChromosomes.SPECIES);
		//			textLayout.newLine();
		//
		//			IAlleleTolerance humidToleranceActive = (IAlleleTolerance) butterfly.getGenome().getActiveAllele(ButterflyChromosomes.HUMIDITY_TOLERANCE);
		//			IAlleleTolerance humidToleranceInactive = (IAlleleTolerance) butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.HUMIDITY_TOLERANCE);
		//			textLayout.drawLine("  " + Translator.translateToLocal("for.gui.tolerance"), GuiAlyzer.COLUMN_0);
		//			guiAlyzer.drawToleranceInfo(humidToleranceActive, GuiAlyzer.COLUMN_1);
		//			guiAlyzer.drawToleranceInfo(humidToleranceInactive, GuiAlyzer.COLUMN_2);
		//
		//			textLayout.newLine();
		//			textLayout.newLine();
		//
		//			String yes = Translator.translateToLocal("for.yes");
		//			String no = Translator.translateToLocal("for.no");
		//
		//			String diurnal0, diurnal1, nocturnal0, nocturnal1;
		//			if (butterfly.getGenome().getNocturnal()) {
		//				nocturnal0 = diurnal0 = yes;
		//			} else {
		//				nocturnal0 = butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
		//				diurnal0 = !butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
		//			}
		//			if (((AlleleBoolean) butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.NOCTURNAL)).getValue()) {
		//				nocturnal1 = diurnal1 = yes;
		//			} else {
		//				nocturnal1 = butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
		//				diurnal1 = !butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
		//			}
		//
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.diurnal"), GuiAlyzer.COLUMN_0);
		//			textLayout.drawLine(diurnal0, GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(false));
		//			textLayout.drawLine(diurnal1, GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(false));
		//			textLayout.newLine();
		//
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.nocturnal"), GuiAlyzer.COLUMN_0);
		//			textLayout.drawLine(nocturnal0, GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(false));
		//			textLayout.drawLine(nocturnal1, GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(false));
		//			textLayout.newLine();
		//
		//			String primary = StringUtil.readableBoolean(butterfly.getGenome().getTolerantFlyer(), yes, no);
		//			String secondary = StringUtil.readableBoolean(((AlleleBoolean) butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.TOLERANT_FLYER)).getValue(), yes,
		//				no);
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.flyer"), primary, secondary, butterfly, ButterflyChromosomes.TOLERANT_FLYER);
		//			textLayout.newLine();
		//
		//			primary = StringUtil.readableBoolean(butterfly.getGenome().getFireResist(), yes, no);
		//			secondary = StringUtil.readableBoolean(((AlleleBoolean) butterfly.getGenome().getInactiveAllele(ButterflyChromosomes.FIRE_RESIST)).getValue(), yes,
		//				no);
		//
		//			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.fireresist"), primary, secondary, butterfly, ButterflyChromosomes.FIRE_RESIST);
		//
		//			textLayout.endPage();
		//		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawAnalyticsPage3(Screen gui, ItemStack itemStack) {
		//		if (gui instanceof GuiAlyzer) {
		//			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
		//			IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(itemStack);
		//			if (butterfly == null) {
		//				return;
		//			}
		//
		//			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();
		//
		//			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);
		//
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.loot.butterfly") + ":", GuiAlyzer.COLUMN_0);
		//			textLayout.newLine();
		//
		//			int x = GuiAlyzer.COLUMN_0;
		//			for (ItemStack stack : butterfly.getGenome().getPrimary().getButterflyLoot().keySet()) {
		//				Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, guiAlyzer.getGuiLeft() + x, guiAlyzer.getGuiTop() + textLayout.getLineY());
		//				x += 18;
		//				if (x > 148) {
		//					x = GuiAlyzer.COLUMN_0;
		//					textLayout.newLine();
		//				}
		//			}
		//
		//			textLayout.newLine();
		//			textLayout.newLine();
		//
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.loot.caterpillar") + ":", GuiAlyzer.COLUMN_0);
		//			textLayout.newLine();
		//
		//			x = GuiAlyzer.COLUMN_0;
		//			for (ItemStack stack : butterfly.getGenome().getPrimary().getCaterpillarLoot().keySet()) {
		//				Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, guiAlyzer.getGuiLeft() + x, guiAlyzer.getGuiTop() + textLayout.getLineY());
		//				x += 18;
		//				if (x > 148) {
		//					x = GuiAlyzer.COLUMN_0;
		//					textLayout.newLine();
		//				}
		//			}
		//
		//			textLayout.newLine();
		//			textLayout.newLine();
		//
		//			textLayout.drawLine(Translator.translateToLocal("for.gui.loot.cocoon") + ":", GuiAlyzer.COLUMN_0);
		//			textLayout.newLine();
		//
		//			x = GuiAlyzer.COLUMN_0;
		//			for (ItemStack stack : butterfly.getGenome().getCocoon().getCocoonLoot().keySet()) {
		//				Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, guiAlyzer.getGuiLeft() + x, guiAlyzer.getGuiTop() + textLayout.getLineY());
		//				x += 18;
		//				if (x > 148) {
		//					x = GuiAlyzer.COLUMN_0;
		//					textLayout.newLine();
		//				}
		//			}
		//
		//			textLayout.endPage();
		//		}
	}

	@Override
	public Map<ResourceLocation, ItemStack> getIconStacks() {
		return iconStacks;
	}

	@Override
	public List<String> getHints() {
		return Config.hints.get("flutterlyzer");
	}
}
