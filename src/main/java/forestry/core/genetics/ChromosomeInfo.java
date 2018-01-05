package forestry.core.genetics;

import javax.annotation.Nullable;

import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;

public class ChromosomeInfo {
	@Nullable
	public IChromosome chromosome;
	@Nullable
	public String activeSpeciesUid;
	@Nullable
	public String inactiveSpeciesUid;

	public final IChromosomeType chromosomeType;

	public ChromosomeInfo(IChromosomeType chromosomeType) {
		this.chromosomeType = chromosomeType;
	}

	public ChromosomeInfo setChromosome(@Nullable IChromosome chromosome) {
		this.chromosome = chromosome;
		return this;
	}

	public void setSpeciesInfo(@Nullable String activeSpeciesUid, @Nullable String inactiveSpeciesUid) {
		this.activeSpeciesUid = activeSpeciesUid;
		this.inactiveSpeciesUid = inactiveSpeciesUid;
	}
}
