package forestry.apiculture.genetics;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.core.GuiElementAlignment;
import forestry.api.core.IGuiElementHelper;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumDatabaseTab;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.render.ColourProperties;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

@SideOnly(Side.CLIENT)
public class BeeDatabaseTab implements IDatabaseTab<IBee> {
	private final EnumDatabaseTab tab;

	BeeDatabaseTab(EnumDatabaseTab tab) {
		this.tab = tab;
	}

	@Override
	public void createElements(IGuiElementHelper elementHelper, IBee bee, ItemStack itemStack) {
		EnumBeeType type = BeeManager.beeRoot.getType(itemStack);
		if (type == null) {
			return;
		}
		IAlleleSpecies primarySpecies = bee.getGenome().getPrimary();
		boolean active = tab == EnumDatabaseTab.ACTIVE_SPECIES;

		elementHelper.addText(Translator.translateToLocal("for.gui.database.tab." + tab.name().toLowerCase() + ".name"), GuiElementAlignment.CENTER, 0xcfb53b);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.species"), bee, EnumBeeChromosome.SPECIES, active);

		IAlleleTolerance tempTolerance = (IAlleleTolerance) (active ? bee.getGenome().getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE) : bee.getGenome().getInactiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE));

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.climate"), GuiElementAlignment.CENTER);
		elementHelper.addToleranceInfo(tempTolerance, primarySpecies, AlleleManager.climateHelper.toDisplay(primarySpecies.getTemperature()));

		IAlleleTolerance humidTolerance = (IAlleleTolerance) (active ? bee.getGenome().getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE) : bee.getGenome().getInactiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE));

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.humidity"), GuiElementAlignment.CENTER);
		elementHelper.addToleranceInfo(humidTolerance, primarySpecies, AlleleManager.climateHelper.toDisplay(primarySpecies.getHumidity()));

		elementHelper.addAllele(Translator.translateToLocal("for.gui.lifespan"), bee, EnumBeeChromosome.LIFESPAN, active);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.speed"), bee, EnumBeeChromosome.SPEED, active);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.pollination"), bee, EnumBeeChromosome.FLOWERING, active);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.flowers"), bee, EnumBeeChromosome.FLOWER_PROVIDER, active);

		IAlleleInteger primaryFertility = (IAlleleInteger) (active ? bee.getGenome().getActiveAllele(EnumBeeChromosome.FERTILITY) : bee.getGenome().getInactiveAllele(EnumBeeChromosome.FERTILITY));

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.fertility"), GuiElementAlignment.CENTER);

		elementHelper.addFertilityInfo(primaryFertility, 0, 0);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.area"), bee, EnumBeeChromosome.TERRITORY, active);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.effect"), bee, EnumBeeChromosome.EFFECT, active);

		String yes = Translator.translateToLocal("for.yes");
		String no = Translator.translateToLocal("for.no");

		String diurnal, nocturnal;
		if(active) {
			if (bee.getGenome().getNeverSleeps()) {
				nocturnal = diurnal = yes;
			} else {
				nocturnal = bee.getGenome().getPrimary().isNocturnal() ? yes : no;
				diurnal = !bee.getGenome().getPrimary().isNocturnal() ? yes : no;
			}
		}else {
			if (((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumButterflyChromosome.NOCTURNAL)).getValue()) {
				nocturnal = diurnal = yes;
			} else {
				nocturnal = bee.getGenome().getSecondary().isNocturnal() ? yes : no;
				diurnal = !bee.getGenome().getSecondary().isNocturnal() ? yes : no;
			}
		}

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.diurnal"), GuiElementAlignment.CENTER);
		elementHelper.addText(diurnal, GuiElementAlignment.CENTER, elementHelper.factory().getColorCoding(false));

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.nocturnal"), GuiElementAlignment.CENTER);
		elementHelper.addText(nocturnal, GuiElementAlignment.CENTER, elementHelper.factory().getColorCoding(false));

		String flyer = StringUtil.readableBoolean(active ? bee.getGenome().getToleratesRain() : ((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TOLERATES_RAIN)).getValue(), yes, no);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.flyer"), (a) -> flyer, bee, EnumButterflyChromosome.TOLERANT_FLYER, active);

		String cave = StringUtil.readableBoolean(active ? bee.getGenome().getCaveDwelling() : ((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.CAVE_DWELLING)).getValue(), yes, no);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.fireresist"), (a) -> cave, bee, EnumButterflyChromosome.FIRE_RESIST, active);

		String displayText;
		if (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN) {
			String displayTextKey = "for.bees.stock.pristine";
			if (!bee.isNatural()) {
				displayTextKey = "for.bees.stock.ignoble";
			}
			displayText = Translator.translateToLocal(displayTextKey);
			elementHelper.addText(displayText, GuiElementAlignment.CENTER, ColourProperties.INSTANCE.get("gui.beealyzer.binomial"));
		}
	}

	@Override
	public EnumDatabaseTab getTab() {
		return tab;
	}
}
