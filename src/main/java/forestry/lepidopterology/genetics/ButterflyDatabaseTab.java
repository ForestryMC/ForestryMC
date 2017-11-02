package forestry.lepidopterology.genetics;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.GuiElementAlignment;
import forestry.api.core.IGuiElementHelper;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumDatabaseTab;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.genetics.GenericRatings;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

@SideOnly(Side.CLIENT)
public class ButterflyDatabaseTab implements IDatabaseTab<IButterfly> {
	private final EnumDatabaseTab tab;

	ButterflyDatabaseTab(EnumDatabaseTab tab) {
		this.tab = tab;
	}

	@Override
	public void createElements(IGuiElementHelper elementHelper, IButterfly butterfly, ItemStack itemStack) {
		boolean active = tab == EnumDatabaseTab.ACTIVE_SPECIES;
		IAlleleButterflySpecies primarySpecies = butterfly.getGenome().getPrimary();

		elementHelper.addText(Translator.translateToLocal("for.gui.database.tab." + tab.name().toLowerCase() + ".name"), GuiElementAlignment.CENTER, 0xcfb53b);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.species"), butterfly, EnumButterflyChromosome.SPECIES, active);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.size"), butterfly, EnumButterflyChromosome.SIZE, active);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.lifespan"), butterfly, EnumButterflyChromosome.LIFESPAN, active);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.speed"), butterfly,EnumButterflyChromosome.SPEED, active);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.metabolism"), (IAlleleInteger a)-> GenericRatings.rateMetabolism(a.getValue()), butterfly, EnumButterflyChromosome.METABOLISM, active);

		IAlleleInteger fertility = (IAlleleInteger) (active ? butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.FERTILITY) : butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.FERTILITY));
		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.fertility"), GuiElementAlignment.CENTER);
		elementHelper.addFertilityInfo(fertility, 0, 8);

		elementHelper.addAllele(Translator.translateToLocal("for.gui.flowers"), butterfly, EnumButterflyChromosome.FLOWER_PROVIDER, active);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.effect"), butterfly, EnumButterflyChromosome.EFFECT, active);

		IAlleleTolerance tempTolerance = (IAlleleTolerance) (active ? butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE) : butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE));

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.climate"), GuiElementAlignment.CENTER);
		elementHelper.addToleranceInfo(tempTolerance, primarySpecies, AlleleManager.climateHelper.toDisplay(primarySpecies.getTemperature()));

		IAlleleTolerance humidTolerance = (IAlleleTolerance) (active ? butterfly.getGenome().getActiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE) : butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE));

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.humidity"), GuiElementAlignment.CENTER);
		elementHelper.addToleranceInfo(humidTolerance, primarySpecies, AlleleManager.climateHelper.toDisplay(primarySpecies.getHumidity()));

		String yes = Translator.translateToLocal("for.yes");
		String no = Translator.translateToLocal("for.no");

		String diurnal, nocturnal;
		if(active) {
			if (butterfly.getGenome().getNocturnal()) {
				nocturnal = diurnal = yes;
			} else {
				nocturnal = butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
				diurnal = !butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
			}
		}else {
			if (((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.NOCTURNAL)).getValue()) {
				nocturnal = diurnal = yes;
			} else {
				nocturnal = butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
				diurnal = !butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
			}
		}

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.diurnal"), GuiElementAlignment.CENTER);
		elementHelper.addText(diurnal, GuiElementAlignment.CENTER, elementHelper.factory().getColorCoding(false));

		elementHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.nocturnal"), GuiElementAlignment.CENTER);
		elementHelper.addText(nocturnal, GuiElementAlignment.CENTER, elementHelper.factory().getColorCoding(false));

		String flyer = StringUtil.readableBoolean(active ? butterfly.getGenome().getTolerantFlyer() : ((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.TOLERANT_FLYER)).getValue(), yes, no);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.flyer"), (a) -> flyer, butterfly, EnumButterflyChromosome.TOLERANT_FLYER, active);

		String fireresist = StringUtil.readableBoolean(active ? butterfly.getGenome().getFireResist() : ((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.FIRE_RESIST)).getValue(), yes, no);
		elementHelper.addAllele(Translator.translateToLocal("for.gui.fireresist"), (a) -> fireresist, butterfly, EnumButterflyChromosome.FIRE_RESIST, active);
	}

	@Override
	public EnumDatabaseTab getTab() {
		return tab;
	}
}
