package forestry.lepidopterology.genetics;

import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.api.genetics.gatgets.DatabaseMode;
import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.genetics.GenericRatings;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.lib.IDatabaseElement;
import forestry.core.utils.StringUtil;
import genetics.api.alleles.IAlleleValue;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
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
        IAlleleButterflySpecies primarySpecies = butterfly.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES);
        IAlleleButterflySpecies secondarySpecies = butterfly.getGenome()
                                                            .getInactiveAllele(ButterflyChromosomes.SPECIES);

        database.label(
                new TranslationTextComponent(
                        "for.gui.database.tab." + (mode == DatabaseMode.ACTIVE ? "active" : "inactive") +
                        "_species.name"),
                GuiElementAlignment.TOP_CENTER,
                GuiElementFactory.INSTANCE.databaseTitle
        );

        database.addLine(new TranslationTextComponent("for.gui.species"), ButterflyChromosomes.SPECIES);

        database.addLine(new TranslationTextComponent("for.gui.size"), ButterflyChromosomes.SIZE);

        database.addLine(new TranslationTextComponent("for.gui.lifespan"), ButterflyChromosomes.LIFESPAN);

        database.addLine(new TranslationTextComponent("for.gui.speed"), ButterflyChromosomes.SPEED);

        database.addLine(
                new TranslationTextComponent("for.gui.metabolism"),
                (IAlleleValue<Integer> allele, Boolean a)
                        -> GenericRatings.rateMetabolism(allele.getValue()), ButterflyChromosomes.METABOLISM
        );

        database.addFertilityLine(new TranslationTextComponent("for.gui.fertility"), ButterflyChromosomes.FERTILITY, 8);

        database.addLine(new TranslationTextComponent("for.gui.flowers"), ButterflyChromosomes.FLOWER_PROVIDER);
        database.addLine(new TranslationTextComponent("for.gui.effect"), ButterflyChromosomes.EFFECT);

        Function<Boolean, ITextComponent> toleranceText = a -> {
            IAlleleForestrySpecies species = a ? primarySpecies : secondarySpecies;
            return AlleleManager.climateHelper.toDisplay(species.getTemperature());
        };
        database.addLine(
                new TranslationTextComponent("for.gui.climate"),
                toleranceText,
                ButterflyChromosomes.TEMPERATURE_TOLERANCE
        );
        database.addToleranceLine(ButterflyChromosomes.TEMPERATURE_TOLERANCE);

        database.addLine(
                new TranslationTextComponent("for.gui.humidity"),
                toleranceText,
                ButterflyChromosomes.HUMIDITY_TOLERANCE
        );
        database.addToleranceLine(ButterflyChromosomes.HUMIDITY_TOLERANCE);

        ITextComponent yes = new TranslationTextComponent("for.yes");
        ITextComponent no = new TranslationTextComponent("for.no");

        {
            ITextComponent diurnalFirst, diurnalSecond, nocturnalFirst, nocturnalSecond;
            if (butterfly.getGenome().getActiveValue(ButterflyChromosomes.NOCTURNAL)) {
                nocturnalFirst = diurnalFirst = yes;
            } else {
                nocturnalFirst = primarySpecies.isNocturnal() ? yes : no;
                diurnalFirst = !primarySpecies.isNocturnal() ? yes : no;
            }
            if (butterfly.getGenome().getInactiveValue(ButterflyChromosomes.NOCTURNAL)) {
                nocturnalSecond = diurnalSecond = yes;
            } else {
                nocturnalSecond = secondarySpecies.isNocturnal() ? yes : no;
                diurnalSecond = !secondarySpecies.isNocturnal() ? yes : no;
            }

            database.addLine(
                    new TranslationTextComponent("for.gui.diurnal"),
                    (Boolean a) -> a ? diurnalFirst : diurnalSecond,
                    false
            );
            database.addLine(
                    new TranslationTextComponent("for.gui.nocturnal"),
                    (Boolean a) -> a ? nocturnalFirst : nocturnalSecond,
                    false
            );
        }

        Function<Boolean, ITextComponent> flyer = active -> StringUtil.readableBoolean(
                active ? butterfly.getGenome()
                                  .getActiveValue(ButterflyChromosomes.TOLERANT_FLYER) : butterfly.getGenome()
                                                                                                  .getInactiveValue(
                                                                                                          ButterflyChromosomes.TOLERANT_FLYER),
                yes,
                no
        );
        database.addLine(new TranslationTextComponent("for.gui.flyer"), flyer, ButterflyChromosomes.TOLERANT_FLYER);

        Function<Boolean, ITextComponent> fireresist = active -> StringUtil.readableBoolean(
                active ? butterfly.getGenome()
                                  .getActiveValue(ButterflyChromosomes.FIRE_RESIST) : butterfly.getGenome()
                                                                                               .getInactiveValue(
                                                                                                       ButterflyChromosomes.FIRE_RESIST),
                yes,
                no
        );
        database.addLine(
                new TranslationTextComponent("for.gui.fireresist"),
                fireresist,
                ButterflyChromosomes.FIRE_RESIST
        );
    }

    @Override
    public ItemStack getIconStack() {
        return ButterflyDefinition.BlueWing.getMemberStack(
                mode == DatabaseMode.ACTIVE ? EnumFlutterType.BUTTERFLY : EnumFlutterType.CATERPILLAR);
    }
}
