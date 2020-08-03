package genetics.individual;

import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ChromosomeInfo {
    public final IChromosomeType chromosomeType;
    @Nullable
    public IChromosome chromosome;
    @Nullable
    public ResourceLocation activeSpeciesUid;
    @Nullable
    public ResourceLocation inactiveSpeciesUid;

    public ChromosomeInfo(IChromosomeType chromosomeType) {
        this.chromosomeType = chromosomeType;
    }

    public ChromosomeInfo setChromosome(@Nullable IChromosome chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public void setSpeciesInfo(@Nullable ResourceLocation activeSpeciesUid, @Nullable ResourceLocation inactiveSpeciesUid) {
        this.activeSpeciesUid = activeSpeciesUid;
        this.inactiveSpeciesUid = inactiveSpeciesUid;
    }
}
