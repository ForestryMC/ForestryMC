package forestry.apiculture.genetics;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.tooltips.ToolTip;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.genetics.alleles.IAlleleFlowers;
import forestry.api.genetics.alyzer.IAlleleDisplayHandler;
import forestry.api.genetics.alyzer.IAlleleDisplayHelper;
import forestry.api.genetics.alyzer.IAlyzerHelper;
import forestry.core.genetics.GenericRatings;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.ResourceUtil;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IChromosomeValue;
import genetics.api.individual.IGenome;
import genetics.api.organism.IOrganismType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public enum BeeDisplayHandler implements IAlleleDisplayHandler<IBee> {
    SPECIES(BeeChromosomes.SPECIES, 0) {
        @Override
        public void drawAlyzer(
                IAlyzerHelper helper,
                IGenome genome,
                double mouseX,
                double mouseY,
                MatrixStack transform
        ) {
            IOrganismType organismType = helper.getOrganismType();
            ITextComponent primaryName = GeneticsUtil.getSpeciesName(
                    organismType,
                    genome.getActiveAllele(BeeChromosomes.SPECIES)
            );
            ITextComponent secondaryName = GeneticsUtil.getSpeciesName(
                    organismType,
                    genome.getActiveAllele(BeeChromosomes.SPECIES)
            );

            helper.drawSpeciesRow(
                    new TranslationTextComponent("for.gui.species").getString(),
                    BeeChromosomes.SPECIES,
                    primaryName,
                    secondaryName
            );
        }
    },
    SPEED(BeeChromosomes.SPEED, -1, 1) {
        @Override
        public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
            IAllele speedAllele = getActiveAllele(genome);
            TranslationTextComponent customSpeed = new TranslationTextComponent(
                    "for.tooltip.worker." +
                    speedAllele.getLocalisationKey().replaceAll("(.*)\\.", "")
            );
            if (ResourceUtil.canTranslate(customSpeed)) {
                toolTip.singleLine()
                       .add(customSpeed)
                       .style(TextFormatting.GRAY)
                       .create();
            } else {
                toolTip.singleLine()
                       .add(speedAllele.getDisplayName())
                       .text(new StringTextComponent(" "))
                       .translated("for.gui.worker")
                       .style(TextFormatting.GRAY)
                       .create();
            }
        }
    },
    LIFESPAN(BeeChromosomes.LIFESPAN, -1, 0) {
        @Override
        public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
            toolTip.singleLine()
                   .add(genome.getActiveAllele(BeeChromosomes.LIFESPAN).getDisplayName())
                   .text(new StringTextComponent(" "))
                   .translated("for.gui.life")
                   .style(TextFormatting.GRAY)
                   .create();
        }
    },
    FERTILITY(BeeChromosomes.FERTILITY, -1, "fertility") {
        @Override
        public void drawAlyzer(
                IAlyzerHelper helper,
                IGenome genome,
                double mouseX,
                double mouseY,
                MatrixStack transform
        ) {
            super.drawAlyzer(helper, genome, mouseX, mouseY, transform);
            IAlleleValue<Integer> primaryFertility = getActiveValue(genome);
            IAlleleValue<Integer> secondaryFertility = getInactiveValue(genome);
            helper.nextColumn();
            helper.drawFertilityInfo(primaryFertility);
            helper.nextColumn();
            helper.drawFertilityInfo(secondaryFertility);
        }
    },
    TEMPERATURE_TOLERANCE(BeeChromosomes.TEMPERATURE_TOLERANCE, -1, 2) {
        @Override
        public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
            IAlleleBeeSpecies primary = genome.getActiveAllele(BeeChromosomes.SPECIES);
            IAlleleValue<EnumTolerance> tempToleranceAllele = getActiveAllele(genome);
            ITextComponent caption = AlleleManager.climateHelper.toDisplay(primary.getTemperature());
            toolTip.singleLine()
                   .text(new StringTextComponent("T:  "))
                   .add(caption)
                   .text(new StringTextComponent(" / "))
                   .add(tempToleranceAllele.getDisplayName())
                   .style(TextFormatting.GREEN)
                   .create();
        }
    },
    HUMIDITY_TOLERANCE(BeeChromosomes.HUMIDITY_TOLERANCE, -1, 3) {
        @Override
        public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
            IAlleleBeeSpecies primary = genome.getActiveAllele(BeeChromosomes.SPECIES);
            IAlleleValue<EnumTolerance> humidToleranceAllele = getActiveAllele(genome);
            ITextComponent caption = AlleleManager.climateHelper.toDisplay(primary.getHumidity());
            toolTip.singleLine()
                   .text(new StringTextComponent("H: "))
                   .add(caption)
                   .text(new StringTextComponent(" / "))
                   .add(humidToleranceAllele.getDisplayName())
                   .style(TextFormatting.GREEN)
                   .create();
        }
    },
    FLOWER_PROVIDER(BeeChromosomes.FLOWER_PROVIDER, -1, 4, "flowers") {
        @Override
        public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
            IAlleleFlowers flowers = getActiveAllele(genome);
            toolTip.add(flowers.getProvider().getDescription(), TextFormatting.GRAY);
        }
    },
    FLOWERING(BeeChromosomes.FLOWERING, -1, -1, "pollination"),
    NEVER_SLEEPS(BeeChromosomes.NEVER_SLEEPS, -1, 5) {
        @Override
        public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
            if (getActiveValue(genome)) {
                toolTip.text(GenericRatings.rateActivityTime(true, false)).style(TextFormatting.RED);
            }
        }
    },
    TOLERATES_RAIN(BeeChromosomes.TOLERATES_RAIN, -1, 6) {
        @Override
        public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
            if (getActiveValue(genome)) {
                toolTip.translated("for.gui.flyer.tooltip").style(TextFormatting.WHITE);
            }
        }
    };


    final IChromosomeType type;
    @Nullable
    final String alyzerCaption;
    final int alyzerIndex;
    final int tooltipIndex;

    BeeDisplayHandler(IChromosomeType type, int alyzerIndex) {
        this(type, alyzerIndex, -1, null);
    }

    BeeDisplayHandler(IChromosomeType type, int alyzerIndex, int tooltipIndex) {
        this(type, alyzerIndex, tooltipIndex, null);
    }

    BeeDisplayHandler(IChromosomeType type, int alyzerIndex, @Nullable String alyzerCaption) {
        this(type, alyzerIndex, -1, alyzerCaption);
    }

    BeeDisplayHandler(IChromosomeType type, int alyzerIndex, int tooltipIndex, @Nullable String alyzerCaption) {
        this.type = type;
        this.alyzerCaption = alyzerCaption;
        this.alyzerIndex = alyzerIndex;
        this.tooltipIndex = tooltipIndex;
    }

    public static void init(IAlleleDisplayHelper helper) {
        for (BeeDisplayHandler handler : values()) {
            int tooltipIndex = handler.tooltipIndex;
            if (tooltipIndex >= 0) {
                helper.addTooltip(handler, BeeRoot.UID, tooltipIndex * 10);
            }
        }
    }

    @Override
    public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
        //Default Implementation
    }

    @Override
    public void drawAlyzer(IAlyzerHelper helper, IGenome genome, double mouseX, double mouseY, MatrixStack transform) {
        if (alyzerCaption != null) {
            helper.drawChromosomeRow("for.gui." + alyzerCaption, type, true);
        }
    }

    <V> IAlleleValue<V> getActive(IGenome genome) {
        //noinspection unchecked
        return genome.getActiveAllele((IChromosomeValue<V>) type);
    }

    <V> IAlleleValue<V> getInactive(IGenome genome) {
        //noinspection unchecked
        return genome.getInactiveAllele((IChromosomeValue<V>) type);
    }

    <A extends IAllele> A getActiveAllele(IGenome genome) {
        //noinspection unchecked
        return genome.getActiveAllele((IChromosomeAllele<A>) type);
    }

    <A extends IAllele> A getInactiveAllele(IGenome genome) {
        //noinspection unchecked
        return genome.getInactiveAllele((IChromosomeAllele<A>) type);
    }

    <V> V getActiveValue(IGenome genome) {
        //noinspection unchecked
        return genome.getActiveValue((IChromosomeValue<V>) type);
    }

    <V> V getInactiveValue(IGenome genome) {
        //noinspection unchecked
        return genome.getInactiveValue((IChromosomeValue<V>) type);
    }
}
