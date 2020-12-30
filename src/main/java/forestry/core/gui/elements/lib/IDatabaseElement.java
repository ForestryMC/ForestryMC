package forestry.core.gui.elements.lib;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.gatgets.DatabaseMode;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IDatabaseElement extends IElementLayout {

    /**
     * Adds the chromosomeName and the name of the active/not active allele, of the chromosome, with {@link #label}.
     */
    void addLine(ITextComponent chromosomeName, IChromosomeType chromosome);

    /**
     * Adds the chromosomeName and the result of toString with {@link #label}.
     */
    <A extends IAllele> void addLine(
            ITextComponent chromosomeName,
            BiFunction<A, Boolean, ITextComponent> toText,
            IChromosomeType chromosome
    );

    <A extends IAllele> void addLine(
            ITextComponent chromosomeName,
            BiFunction<A, Boolean, ITextComponent> toText,
            IChromosomeType chromosome,
            boolean dominant
    );

    void addLine(ITextComponent leftText, Function<Boolean, ITextComponent> toText, IChromosomeType chromosome);

    void addLine(ITextComponent firstText, ITextComponent secondText, Style firstStyle, Style secondStyle);

    void addLine(ITextComponent leftText, ITextComponent rightText, boolean dominant);

    void addLine(ITextComponent leftText, Function<Boolean, ITextComponent> toText, boolean dominant);

    void addFertilityLine(ITextComponent chromosomeName, IChromosomeType chromosome, int texOffset);

    void addToleranceLine(IChromosomeType chromosome);

    void addMutation(
            int x,
            int y,
            int width,
            int height,
            IMutation mutation,
            IAllele species,
            IBreedingTracker breedingTracker
    );

    void addMutationResultant(
            int x,
            int y,
            int width,
            int height,
            IMutation mutation,
            IBreedingTracker breedingTracker
    );

    //void addRow(String firstText, String secondText, String thirdText, ITextStyle firstStyle, ITextStyle secondStyle, ITextStyle thirdStyle);

    //void addRow(String firstText, String secondText, String thirdText, boolean secondDominant, boolean thirdDominant);

    //void addRow(String firstText, String secondText, String thirdText, IIndividual individual, IChromosomeType chromosome);

    void addSpeciesLine(ITextComponent firstText, @Nullable ITextComponent secondText, IChromosomeType chromosome);

    void init(DatabaseMode mode, IIndividual individual, int secondColumn, int thirdColumn);

    @Nullable
    IIndividual getIndividual();

    IGenome getGenome();

    //void addSpeciesLine(String firstText, @Nullable String secondText, @Nullable String thirdText, IIndividual individual, IChromosomeType chromosome);
}
