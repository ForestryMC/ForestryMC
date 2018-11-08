package forestry.apiculture.genetics;

import java.util.function.Function;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.DatabaseMode;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IDatabaseElement;
import forestry.api.gui.style.ITextStyle;
import forestry.api.gui.style.TextStyleBuilder;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.render.ColourProperties;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

@SideOnly(Side.CLIENT)
public class BeeDatabaseTab implements IDatabaseTab<IBee> {
	private static final ITextStyle BINOMIAL = new TextStyleBuilder().color(() -> ColourProperties.INSTANCE.get("gui.beealyzer.binomial")).build();

	private final DatabaseMode mode;

	BeeDatabaseTab(DatabaseMode mode) {
		this.mode = mode;
	}

	@Override
	public DatabaseMode getMode() {
		return mode;
	}

	@Override
	public void createElements(IDatabaseElement container, IBee bee, ItemStack itemStack) {
		EnumBeeType type = BeeManager.beeRoot.getType(itemStack);
		if (type == null) {
			return;
		}
		IAlleleSpecies primarySpecies = bee.getGenome().getPrimary();
		IAlleleSpecies secondarySpecies = bee.getGenome().getSecondary();

		container.label(Translator.translateToLocal("for.gui.database.tab." + (mode == DatabaseMode.ACTIVE ? "active" : "inactive") + "_species.name"), GuiElementAlignment.TOP_CENTER, GuiElementFactory.DATABASE_TITLE);

		container.addLine(Translator.translateToLocal("for.gui.species"), EnumBeeChromosome.SPECIES);

		Function<Boolean, String> toleranceText = a -> {
			IAlleleSpecies species = a ? primarySpecies : secondarySpecies;
			return AlleleManager.climateHelper.toDisplay(species.getTemperature());
		};
		container.addLine(Translator.translateToLocal("for.gui.climate"), toleranceText, EnumBeeChromosome.TEMPERATURE_TOLERANCE);
		container.addToleranceLine(EnumBeeChromosome.TEMPERATURE_TOLERANCE);

		container.addLine(Translator.translateToLocal("for.gui.humidity"), toleranceText, EnumBeeChromosome.HUMIDITY_TOLERANCE);
		container.addToleranceLine(EnumBeeChromosome.HUMIDITY_TOLERANCE);

		container.addLine(Translator.translateToLocal("for.gui.lifespan"), EnumBeeChromosome.LIFESPAN);

		container.addLine(Translator.translateToLocal("for.gui.speed"), EnumBeeChromosome.SPEED);
		container.addLine(Translator.translateToLocal("for.gui.pollination"), EnumBeeChromosome.FLOWERING);
		container.addLine(Translator.translateToLocal("for.gui.flowers"), EnumBeeChromosome.FLOWER_PROVIDER);

		container.addFertilityLine(Translator.translateToLocal("for.gui.fertility"), EnumBeeChromosome.FERTILITY, 0);

		container.addLine(Translator.translateToLocal("for.gui.area"), EnumBeeChromosome.TERRITORY);
		container.addLine(Translator.translateToLocal("for.gui.effect"), EnumBeeChromosome.EFFECT);

		String yes = Translator.translateToLocal("for.yes");
		String no = Translator.translateToLocal("for.no");

		String diurnal, nocturnal;
		if (mode == DatabaseMode.ACTIVE) {
			if (bee.getGenome().getNeverSleeps()) {
				nocturnal = diurnal = yes;
			} else {
				nocturnal = bee.getGenome().getPrimary().isNocturnal() ? yes : no;
				diurnal = !bee.getGenome().getPrimary().isNocturnal() ? yes : no;
			}
		} else {
			if (((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumButterflyChromosome.NOCTURNAL)).getValue()) {
				nocturnal = diurnal = yes;
			} else {
				nocturnal = bee.getGenome().getSecondary().isNocturnal() ? yes : no;
				diurnal = !bee.getGenome().getSecondary().isNocturnal() ? yes : no;
			}
		}

		container.addLine(Translator.translateToLocal("for.gui.diurnal"), diurnal, false);

		container.addLine(Translator.translateToLocal("for.gui.nocturnal"), nocturnal, false);

		Function<Boolean, String> flyer = active -> StringUtil.readableBoolean(active ? bee.getGenome().getToleratesRain() : ((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.TOLERATES_RAIN)).getValue(), yes, no);
		container.addLine(Translator.translateToLocal("for.gui.flyer"), flyer, EnumBeeChromosome.TOLERATES_RAIN);

		Function<Boolean, String> cave = active -> StringUtil.readableBoolean(active ? bee.getGenome().getCaveDwelling() : ((AlleleBoolean) bee.getGenome().getInactiveAllele(EnumBeeChromosome.CAVE_DWELLING)).getValue(), yes, no);
		container.addLine(Translator.translateToLocal("for.gui.cave"), cave, EnumBeeChromosome.CAVE_DWELLING);

		String displayText;
		if (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN) {
			String displayTextKey = "for.bees.stock.pristine";
			if (!bee.isNatural()) {
				displayTextKey = "for.bees.stock.ignoble";
			}
			displayText = Translator.translateToLocal(displayTextKey);
			container.label(displayText, GuiElementAlignment.TOP_CENTER, BINOMIAL);
		}
	}

	@Override
	public ItemStack getIconStack() {
		return BeeDefinition.MEADOWS.getMemberStack(mode == DatabaseMode.ACTIVE ? EnumBeeType.PRINCESS : EnumBeeType.DRONE);
	}
}
