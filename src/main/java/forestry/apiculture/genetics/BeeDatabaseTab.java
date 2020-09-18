package forestry.apiculture.genetics;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.api.genetics.gatgets.DatabaseMode;
import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.lib.IDatabaseElement;
import forestry.core.utils.StringUtil;
import genetics.api.organism.IOrganismType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class BeeDatabaseTab implements IDatabaseTab<IBee> {

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
        Optional<IOrganismType> optionalType = BeeManager.beeRoot.getTypes().getType(itemStack);
        if (!optionalType.isPresent()) {
            return;
        }
        IOrganismType type = optionalType.get();
        IAlleleBeeSpecies primarySpecies = bee.getGenome().getActiveAllele(BeeChromosomes.SPECIES);
        IAlleleBeeSpecies secondarySpecies = bee.getGenome().getInactiveAllele(BeeChromosomes.SPECIES);

        container.label(
                new TranslationTextComponent("for.gui.database.tab." + (mode == DatabaseMode.ACTIVE ? "active" : "inactive") + "_species.name"),
                GuiElementAlignment.TOP_CENTER,
                GuiElementFactory.INSTANCE.databaseTitle
        );

        container.addLine(
                new TranslationTextComponent("for.gui.species"), BeeChromosomes.SPECIES);

        Function<Boolean, ITextProperties> toleranceText = a -> {
            IAlleleForestrySpecies species = a ? primarySpecies : secondarySpecies;
            return AlleleManager.climateHelper.toDisplay(species.getTemperature());
        };
        container.addLine(new TranslationTextComponent("for.gui.climate"), toleranceText, BeeChromosomes.TEMPERATURE_TOLERANCE);
        container.addToleranceLine(BeeChromosomes.TEMPERATURE_TOLERANCE);

        container.addLine(new TranslationTextComponent("for.gui.humidity"), toleranceText, BeeChromosomes.HUMIDITY_TOLERANCE);
        container.addToleranceLine(BeeChromosomes.HUMIDITY_TOLERANCE);

        container.addLine(new TranslationTextComponent("for.gui.lifespan"), BeeChromosomes.LIFESPAN);

        container.addLine(new TranslationTextComponent("for.gui.speed"), BeeChromosomes.SPEED);
        container.addLine(new TranslationTextComponent("for.gui.pollination"), BeeChromosomes.FLOWERING);
        container.addLine(new TranslationTextComponent("for.gui.flowers"), BeeChromosomes.FLOWER_PROVIDER);

        container.addFertilityLine(new TranslationTextComponent("for.gui.fertility"), BeeChromosomes.FERTILITY, 0);

        container.addLine(new TranslationTextComponent("for.gui.area"), BeeChromosomes.TERRITORY);
        container.addLine(new TranslationTextComponent("for.gui.effect"), BeeChromosomes.EFFECT);

        ITextProperties yes = new TranslationTextComponent("for.yes");
        ITextProperties no = new TranslationTextComponent("for.no");

        ITextProperties diurnal, nocturnal;
        if (mode == DatabaseMode.ACTIVE) {
            if (bee.getGenome().getActiveValue(BeeChromosomes.NEVER_SLEEPS)) {
                nocturnal = diurnal = yes;
            } else {
                nocturnal = primarySpecies.isNocturnal() ? yes : no;
                diurnal = !primarySpecies.isNocturnal() ? yes : no;
            }
        } else {
            if (bee.getGenome().getInactiveValue(ButterflyChromosomes.NOCTURNAL)) {
                nocturnal = diurnal = yes;
            } else {
                nocturnal = secondarySpecies.isNocturnal() ? yes : no;
                diurnal = !secondarySpecies.isNocturnal() ? yes : no;
            }
        }

        container.addLine(new TranslationTextComponent("for.gui.diurnal"), diurnal, false);

        container.addLine(new TranslationTextComponent("for.gui.nocturnal"), nocturnal, false);

        Function<Boolean, ITextProperties> flyer = active ->
                StringUtil.readableBoolean(
                        active
                                ? bee.getGenome().getActiveValue(BeeChromosomes.TOLERATES_RAIN)
                                : bee.getGenome().getInactiveValue(BeeChromosomes.TOLERATES_RAIN),
                        yes,
                        no
                );
        container.addLine(new TranslationTextComponent("for.gui.flyer"), flyer, BeeChromosomes.TOLERATES_RAIN);

        Function<Boolean, ITextProperties> cave = active ->
                StringUtil.readableBoolean(
                        active
                                ? bee.getGenome().getActiveValue(BeeChromosomes.CAVE_DWELLING)
                                : bee.getGenome().getInactiveValue(BeeChromosomes.CAVE_DWELLING),
                        yes,
                        no
                );
        container.addLine(new TranslationTextComponent("for.gui.cave"), cave, BeeChromosomes.CAVE_DWELLING);

        if (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN) {
            String displayTextKey = "for.bees.stock.pristine";
            if (!bee.isNatural()) {
                displayTextKey = "for.bees.stock.ignoble";
            }

            container.label(new TranslationTextComponent(displayTextKey), GuiElementAlignment.TOP_CENTER, GuiElementFactory.INSTANCE.binomial);
        }
    }

    @Override
    public ItemStack getIconStack() {
        return BeeDefinition.MEADOWS.getMemberStack(mode == DatabaseMode.ACTIVE ? EnumBeeType.PRINCESS : EnumBeeType.DRONE);
    }
}
