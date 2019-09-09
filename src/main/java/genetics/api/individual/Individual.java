package genetics.api.individual;

import javax.annotation.Nullable;
import java.util.Optional;

import net.minecraft.nbt.CompoundNBT;

import genetics.api.GeneticsAPI;

/**
 * A simple abstract default implementation of {@link IIndividual}.
 */
public abstract class Individual implements IIndividual {
	private static final String NBT_GENOME = "Genome";
	private static final String NBT_MATE = "Mate";
	private static final String NBT_ANALYZED = "IsAnalyzed";

	protected final IGenome genome;
	protected boolean isAnalyzed = false;
	@Nullable
	protected IGenome mate;

	public Individual(IGenome genome) {
		this.genome = genome;
	}

	public Individual(IGenome genome, @Nullable IGenome mate) {
		this.genome = genome;
		this.mate = mate;
	}

	public Individual(CompoundNBT compound) {
		IKaryotype karyotype = getRoot().getKaryotype();
		if (compound.contains(NBT_GENOME)) {
			genome = GeneticsAPI.apiInstance.getGeneticFactory().createGenome(karyotype, compound.getCompound(NBT_GENOME));
		} else {
			genome = karyotype.getDefaultGenome();
		}

		if (compound.contains(NBT_MATE)) {
			mate = GeneticsAPI.apiInstance.getGeneticFactory().createGenome(karyotype, compound.getCompound(NBT_MATE));
		}

		isAnalyzed = compound.getBoolean(NBT_ANALYZED);
	}

	@Override
	public String getIdentifier() {
		return genome.getActiveAllele(getRoot().getKaryotype().getSpeciesType()).getRegistryName().toString();
	}

	@Override
	public IGenome getGenome() {
		return genome;
	}

	@Override
	public boolean mate(@Nullable IGenome mate) {
		if (mate != null && mate.getKaryotype() != genome.getKaryotype()) {
			return false;
		}
		this.mate = mate;
		return true;
	}

	@Override
	public Optional<IGenome> getMate() {
		return Optional.ofNullable(mate);
	}

	@Override
	public boolean isPureBred(IChromosomeType geneType) {
		return genome.isPureBred(geneType);
	}

	@Override
	public boolean isGeneticEqual(IIndividual other) {
		return genome.isGeneticEqual(other.getGenome());
	}

	@Override
	public void onBuild(IIndividual otherIndividual) {
		if (otherIndividual.isAnalyzed()) {
			analyze();
		}
		Optional<IGenome> otherMate = otherIndividual.getMate();
		otherMate.ifPresent(this::mate);
	}

	@Override
	public IIndividualBuilder toBuilder() {
		return GeneticsAPI.apiInstance.getGeneticFactory().createIndividualBuilder(this);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put(NBT_GENOME, genome.writeToNBT(new CompoundNBT()));
		if (mate != null) {
			compound.put(NBT_MATE, mate.writeToNBT(new CompoundNBT()));
		}
		compound.putBoolean(NBT_ANALYZED, isAnalyzed);
		return compound;
	}

	@Override
	public boolean analyze() {
		if (isAnalyzed) {
			return false;
		}

		isAnalyzed = true;
		return true;
	}

	@Override
	public boolean isAnalyzed() {
		return isAnalyzed;
	}
}
