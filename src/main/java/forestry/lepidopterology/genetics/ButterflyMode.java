package forestry.lepidopterology.genetics;

import javax.annotation.Nonnull;

import forestry.api.genetics.IGenome;
import forestry.api.lepidopterology.ButterflyChromosome;
import forestry.api.lepidopterology.IButterflyMode;

public class ButterflyMode implements IButterflyMode {
	public static final IButterflyMode normal = new ButterflyMode("NORMAL", 1f);

	@Nonnull
	private final String name;
	private final float mutationModifier;

	private ButterflyMode(@Nonnull String name, float mutationModifier) {
		this.name = name;
		this.mutationModifier = mutationModifier;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public float getMutationModifier(IGenome<ButterflyChromosome> genome0, IGenome<ButterflyChromosome> genome1) {
		return mutationModifier;
	}
}
