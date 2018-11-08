package forestry.apiculture.genetics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.apiculture.ModuleApiculture;
import forestry.core.config.Config;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.TextLayoutHelper;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

public class BeeAlyzerPlugin implements IAlyzerPlugin {
	public static final BeeAlyzerPlugin INSTANCE = new BeeAlyzerPlugin();

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	private BeeAlyzerPlugin() {
		NonNullList<ItemStack> beeList = NonNullList.create();
		ModuleApiculture.getItems().beeDroneGE.addCreativeItems(beeList, false);
		for (ItemStack beeStack : beeList) {
			IAlleleBeeSpecies species = BeeGenome.getSpecies(beeStack);
			iconStacks.put(species.getUID(), beeStack);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawAnalyticsPage1(GuiScreen gui, ItemStack itemStack) {
		if (gui instanceof GuiAlyzer) {
			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
			IBee bee = BeeManager.beeRoot.getMember(itemStack);
			if (bee == null) {
				return;
			}
			EnumBeeType type = BeeManager.beeRoot.getType(itemStack);
			if (type == null) {
				return;
			}

			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

			textLayout.drawLine(Translator.translateToLocal("for.gui.active"), GuiAlyzer.COLUMN_1);
			textLayout.drawLine(Translator.translateToLocal("for.gui.inactive"), GuiAlyzer.COLUMN_2);

			textLayout.newLine();
			textLayout.newLine();

			{
				String customPrimaryBeeKey = "for.bees.custom.beealyzer." + type.getName() + "." + bee.getGenome().getPrimary().getUnlocalizedName().replace("bees.species.", "");
				String customSecondaryBeeKey = "for.bees.custom.beealyzer." + type.getName() + "." + bee.getGenome().getSecondary().getUnlocalizedName().replace("bees.species.", "");

				guiAlyzer.drawSpeciesRow(Translator.translateToLocal("for.gui.species"), bee, EnumBeeChromosome.SPECIES, GuiAlyzer.checkCustomName(customPrimaryBeeKey), GuiAlyzer.checkCustomName(customSecondaryBeeKey));
				textLayout.newLine();
			}

			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.lifespan"), bee, EnumBeeChromosome.LIFESPAN);
			textLayout.newLine();
			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.speed"), bee, EnumBeeChromosome.SPEED);
			textLayout.newLine();
			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.pollination"), bee, EnumBeeChromosome.FLOWERING);
			textLayout.newLine();
			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.flowers"), bee, EnumBeeChromosome.FLOWER_PROVIDER);
			textLayout.newLine();

			textLayout.drawLine(Translator.translateToLocal("for.gui.fertility"), GuiAlyzer.COLUMN_0);
			IAlleleInteger primaryFertility = (IAlleleInteger) bee.getGenome().getActiveAllele(EnumBeeChromosome.FERTILITY);
			IAlleleInteger secondaryFertility = (IAlleleInteger) bee.getGenome().getInactiveAllele(EnumBeeChromosome.FERTILITY);
			guiAlyzer.drawFertilityInfo(primaryFertility.getValue(), GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(primaryFertility.isDominant()), 0);
			guiAlyzer.drawFertilityInfo(secondaryFertility.getValue(), GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(secondaryFertility.isDominant()), 0);
			textLayout.newLine();

			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.area"), bee, EnumBeeChromosome.TERRITORY);
			textLayout.newLine();

			guiAlyzer.drawChromosomeRow(Translator.translateToLocal("for.gui.effect"), bee, EnumBeeChromosome.EFFECT);
			textLayout.newLine();

			textLayout.endPage();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawAnalyticsPage2(GuiScreen gui, ItemStack itemStack) {
		if (gui instanceof GuiAlyzer) {
			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
			IBee bee = BeeManager.beeRoot.getMember(itemStack);
			if (bee == null) {
				return;
			}
			EnumBeeType type = BeeManager.beeRoot.getType(itemStack);

			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();

			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

			textLayout.drawLine(Translator.translateToLocal("for.gui.active"), GuiAlyzer.COLUMN_1);
			textLayout.drawLine(Translator.translateToLocal("for.gui.inactive"), GuiAlyzer.COLUMN_2);

			textLayout.newLine();

			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.climate"), AlleleManager.climateHelper.toDisplay(bee.getGenome().getPrimary().getTemperature()),
				AlleleManager.climateHelper.toDisplay(bee.getGenome().getSecondary().getTemperature()), bee, EnumBeeChromosome.SPECIES);

			textLayout.newLine();

			IAlleleTolerance tempToleranceActive = (IAlleleTolerance) bee.getGenome().getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE);
			IAlleleTolerance tempToleranceInactive = (IAlleleTolerance) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE);
			textLayout.drawLine("  " + Translator.translateToLocal("for.gui.tolerance"), GuiAlyzer.COLUMN_0);
			guiAlyzer.drawToleranceInfo(tempToleranceActive, GuiAlyzer.COLUMN_1);
			guiAlyzer.drawToleranceInfo(tempToleranceInactive, GuiAlyzer.COLUMN_2);

			textLayout.newLine(16);

			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.humidity"), AlleleManager.climateHelper.toDisplay(bee.getGenome().getPrimary().getHumidity()),
				AlleleManager.climateHelper.toDisplay(bee.getGenome().getSecondary().getHumidity()), bee, EnumBeeChromosome.SPECIES);

			textLayout.newLine();

			IAlleleTolerance humidToleranceActive = (IAlleleTolerance) bee.getGenome().getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE);
			IAlleleTolerance humidToleranceInactive = (IAlleleTolerance) bee.getGenome().getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE);
			textLayout.drawLine("  " + Translator.translateToLocal("for.gui.tolerance"), GuiAlyzer.COLUMN_0);
			guiAlyzer.drawToleranceInfo(humidToleranceActive, GuiAlyzer.COLUMN_1);
			guiAlyzer.drawToleranceInfo(humidToleranceInactive, GuiAlyzer.COLUMN_2);

			textLayout.newLine(16);

			String yes = Translator.translateToLocal("for.yes");
			String no = Translator.translateToLocal("for.no");

			String diurnal0, diurnal1, nocturnal0, nocturnal1;
			if (bee.getGenome().getNeverSleeps()) {
				nocturnal0 = diurnal0 = yes;
			} else {
				nocturnal0 = bee.getGenome().getPrimary().isNocturnal() ? yes : no;
				diurnal0 = !bee.getGenome().getPrimary().isNocturnal() ? yes : no;
			}
			if (((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.NEVER_SLEEPS)).getValue()) {
				nocturnal1 = diurnal1 = yes;
			} else {
				nocturnal1 = bee.getGenome().getSecondary().isNocturnal() ? yes : no;
				diurnal1 = !bee.getGenome().getSecondary().isNocturnal() ? yes : no;
			}

			textLayout.drawLine(Translator.translateToLocal("for.gui.diurnal"), GuiAlyzer.COLUMN_0);
			textLayout.drawLine(diurnal0, GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(false));
			textLayout.drawLine(diurnal1, GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(false));
			textLayout.newLineCompressed();

			textLayout.drawLine(Translator.translateToLocal("for.gui.nocturnal"), GuiAlyzer.COLUMN_0);
			textLayout.drawLine(nocturnal0, GuiAlyzer.COLUMN_1, guiAlyzer.getColorCoding(false));
			textLayout.drawLine(nocturnal1, GuiAlyzer.COLUMN_2, guiAlyzer.getColorCoding(false));
			textLayout.newLineCompressed();

			String primary = StringUtil.readableBoolean(bee.getGenome().getToleratesRain(), yes, no);
			String secondary = StringUtil.readableBoolean(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TOLERATES_RAIN)).getValue(), yes, no);

			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.flyer"), primary, secondary, bee, EnumBeeChromosome.TOLERATES_RAIN);

			textLayout.newLineCompressed();

			primary = StringUtil.readableBoolean(bee.getGenome().getCaveDwelling(), yes, no);
			secondary = StringUtil.readableBoolean(((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.CAVE_DWELLING)).getValue(), yes, no);

			guiAlyzer.drawRow(Translator.translateToLocal("for.gui.cave"), primary, secondary, bee, EnumBeeChromosome.CAVE_DWELLING);

			textLayout.newLine();

			String displayText;
			if (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN) {
				String displayTextKey = "for.bees.stock.pristine";
				if (!bee.isNatural()) {
					displayTextKey = "for.bees.stock.ignoble";
				}
				displayText = Translator.translateToLocal(displayTextKey);
				textLayout.drawCenteredLine(displayText, 8, 208, guiAlyzer.getFontColor().get("gui.beealyzer.binomial"));
			}

			if (bee.getGeneration() >= 0) {
				textLayout.newLineCompressed();
				displayText = Translator.translateToLocalFormatted("for.gui.beealyzer.generations", bee.getGeneration());
				textLayout.drawCenteredLine(displayText, 8, 208, guiAlyzer.getFontColor().get("gui.beealyzer.binomial"));
			}

			textLayout.endPage();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void drawAnalyticsPage3(GuiScreen gui, ItemStack itemStack) {
		if (gui instanceof GuiAlyzer) {
			GuiAlyzer guiAlyzer = (GuiAlyzer) gui;
			IBee bee = BeeManager.beeRoot.getMember(itemStack);
			if (bee == null) {
				return;
			}

			TextLayoutHelper textLayout = guiAlyzer.getTextLayout();
			WidgetManager widgetManager = guiAlyzer.getWidgetManager();

			textLayout.startPage(GuiAlyzer.COLUMN_0, GuiAlyzer.COLUMN_1, GuiAlyzer.COLUMN_2);

			textLayout.drawLine(Translator.translateToLocal("for.gui.beealyzer.produce") + ":", GuiAlyzer.COLUMN_0);

			textLayout.newLine();

			int x = GuiAlyzer.COLUMN_0;
			for (ItemStack stack : bee.getProduceList()) {
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

			textLayout.drawLine(Translator.translateToLocal("for.gui.beealyzer.specialty") + ":", GuiAlyzer.COLUMN_0);
			textLayout.newLine();

			x = GuiAlyzer.COLUMN_0;
			for (ItemStack stack : bee.getSpecialtyList()) {
				widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), stack));

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
	public Map<String, ItemStack> getIconStacks() {
		return iconStacks;
	}

	@Override
	public List<String> getHints() {
		return Config.hints.get("beealyzer");
	}

}
