package forestry.lepidopterology.genetics;

import java.util.function.Function;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.DatabaseMode;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IDatabaseElement;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.genetics.GenericRatings;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

@SideOnly(Side.CLIENT)
public class ButterflyDatabaseTab implements IDatabaseTab<IButterfly> {
	private final DatabaseMode mode;

	ButterflyDatabaseTab(DatabaseMode mode) {
		this.mode = mode;
	}

	@Override
	public DatabaseMode getMode() {
		return mode;
	}

	@Override
	public void createElements(IDatabaseElement database, IButterfly butterfly, ItemStack itemStack) {
		IAlleleButterflySpecies primarySpecies = butterfly.getGenome().getPrimary();
		IAlleleButterflySpecies secondarySpecies = butterfly.getGenome().getSecondary();

		database.label(Translator.translateToLocal("for.gui.database.tab." + (mode == DatabaseMode.ACTIVE ? "active" : "inactive") + "_species.name"), GuiElementAlignment.TOP_CENTER, GuiElementFactory.DATABASE_TITLE);

		database.addLine(Translator.translateToLocal("for.gui.species"), EnumButterflyChromosome.SPECIES);

		database.addLine(Translator.translateToLocal("for.gui.size"), EnumButterflyChromosome.SIZE);

		database.addLine(Translator.translateToLocal("for.gui.lifespan"), EnumButterflyChromosome.LIFESPAN);

		database.addLine(Translator.translateToLocal("for.gui.speed"), EnumButterflyChromosome.SPEED);

		database.addLine(Translator.translateToLocal("for.gui.metabolism"), (IAlleleInteger allele, Boolean a) -> GenericRatings.rateMetabolism(allele.getValue()), EnumButterflyChromosome.METABOLISM);

		database.addFertilityLine(Translator.translateToLocal("for.gui.fertility"), EnumButterflyChromosome.FERTILITY, 8);

		database.addLine(Translator.translateToLocal("for.gui.flowers"), EnumButterflyChromosome.FLOWER_PROVIDER);
		database.addLine(Translator.translateToLocal("for.gui.effect"), EnumButterflyChromosome.EFFECT);

		Function<Boolean, String> toleranceText = a -> {
			IAlleleSpecies species = a ? primarySpecies : secondarySpecies;
			return AlleleManager.climateHelper.toDisplay(species.getTemperature());
		};
		database.addLine(Translator.translateToLocal("for.gui.climate"), toleranceText, EnumButterflyChromosome.TEMPERATURE_TOLERANCE);
		database.addToleranceLine(EnumButterflyChromosome.TEMPERATURE_TOLERANCE);

		database.addLine(Translator.translateToLocal("for.gui.humidity"), toleranceText, EnumButterflyChromosome.HUMIDITY_TOLERANCE);
		database.addToleranceLine(EnumButterflyChromosome.HUMIDITY_TOLERANCE);

		String yes = Translator.translateToLocal("for.yes");
		String no = Translator.translateToLocal("for.no");

		{
			String diurnalFirst;
			String diurnalSecond;
			String nocturnalFirst;
			String nocturnalSecond;
			if (butterfly.getGenome().getNocturnal()) {
				nocturnalFirst = diurnalFirst = yes;
			} else {
				nocturnalFirst = butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
				diurnalFirst = !butterfly.getGenome().getPrimary().isNocturnal() ? yes : no;
			}
			if (((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.NOCTURNAL)).getValue()) {
				nocturnalSecond = diurnalSecond = yes;
			} else {
				nocturnalSecond = butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
				diurnalSecond = !butterfly.getGenome().getSecondary().isNocturnal() ? yes : no;
			}

			database.addLine(Translator.translateToLocal("for.gui.diurnal"), (Boolean a) -> a ? diurnalFirst : diurnalSecond, false);
			database.addLine(Translator.translateToLocal("for.gui.nocturnal"), (Boolean a) -> a ? nocturnalFirst : nocturnalSecond, false);
		}

		Function<Boolean, String> flyer = active -> StringUtil.readableBoolean(active ? butterfly.getGenome().getTolerantFlyer() : ((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.TOLERANT_FLYER)).getValue(), yes, no);
		database.addLine(Translator.translateToLocal("for.gui.flyer"), flyer, EnumButterflyChromosome.TOLERANT_FLYER);

		Function<Boolean, String> fireresist = active -> StringUtil.readableBoolean(active ? butterfly.getGenome().getFireResist() : ((AlleleBoolean) butterfly.getGenome().getInactiveAllele(EnumButterflyChromosome.FIRE_RESIST)).getValue(), yes, no);
		database.addLine(Translator.translateToLocal("for.gui.fireresist"), fireresist, EnumButterflyChromosome.FIRE_RESIST);
	}

	@Override
	public ItemStack getIconStack() {
		return ButterflyDefinition.BlueWing.getMemberStack(mode == DatabaseMode.ACTIVE ? EnumFlutterType.BUTTERFLY : EnumFlutterType.CATERPILLAR);
	}
}
