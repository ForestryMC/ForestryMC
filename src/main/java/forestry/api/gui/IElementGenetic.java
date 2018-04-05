package forestry.api.gui;

import java.util.function.Function;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;

public interface IElementGenetic extends IElementGroup {

	/**
	 * Adds the chromosomeName and the name of the active/not active allele, of the chromosome, with {@link #text}.
	 */
	void addAlleleRow(String chromosomeName, IIndividual individual, IChromosomeType chromosome, boolean active);

	/**
	 * Adds the chromosomeName and the result of toString with {@link #text}.
	 */
	<A extends IAllele> void addAlleleRow(String chromosomeName, Function<A, String> toString, IIndividual individual, IChromosomeType chromosome, boolean active);

	void addFertilityInfo(String chromosomeName, IAlleleInteger fertilityAllele, int texOffset);

	void addToleranceInfo(String chromosomeName, IAlleleTolerance toleranceAllele, IAlleleSpecies species, String text);

	void addMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker);

	void addMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker);

	void addRow(String firstText, String secondText, int firstColor, int secondColor);

	void addRow(String leftText, String rightText, boolean dominant);
}
