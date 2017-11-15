package forestry.lepidopterology.genetics;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterflyGenomeWrapper;
import forestry.core.genetics.GenomeWrapper;

public class ButterflyGenomeWrapper extends GenomeWrapper<EnumButterflyChromosome> implements IButterflyGenomeWrapper {
	public ButterflyGenomeWrapper(IGenome genome) {
		super(genome);
	}

	/* SPECIES */
	@Override
	public IAlleleButterflySpecies getPrimary() {
		return getActiveAllele(EnumButterflyChromosome.SPECIES, IAlleleButterflySpecies.class);
	}

	@Override
	public IAlleleButterflySpecies getSecondary() {
		return getInactiveAllele(EnumButterflyChromosome.SPECIES, IAlleleButterflySpecies.class);
	}

	@Override
	public float getSize() {
		return getActiveAllele(EnumButterflyChromosome.SIZE, IAlleleFloat.class).getValue();
	}

	@Override
	public int getLifespan() {
		return getActiveAllele(EnumButterflyChromosome.LIFESPAN, IAlleleInteger.class).getValue();
	}

	@Override
	public float getSpeed() {
		return getActiveAllele(EnumButterflyChromosome.SPEED, IAlleleFloat.class).getValue();
	}

	@Override
	public int getMetabolism() {
		return getActiveAllele(EnumButterflyChromosome.METABOLISM, IAlleleInteger.class).getValue();
	}

	@Override
	public int getFertility() {
		return getActiveAllele(EnumButterflyChromosome.FERTILITY, IAlleleInteger.class).getValue();
	}

	@Override
	public EnumTolerance getToleranceTemp() {
		return getActiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE, IAlleleTolerance.class).getValue();
	}

	@Override
	public EnumTolerance getToleranceHumid() {
		return getActiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE, IAlleleTolerance.class).getValue();
	}

	@Override
	public boolean getNocturnal() {
		return getActiveAllele(EnumButterflyChromosome.NOCTURNAL, IAlleleBoolean.class).getValue();
	}

	@Override
	public boolean getTolerantFlyer() {
		return getActiveAllele(EnumButterflyChromosome.TOLERANT_FLYER, IAlleleBoolean.class).getValue();
	}

	@Override
	public boolean getFireResist() {
		return getActiveAllele(EnumButterflyChromosome.FIRE_RESIST, IAlleleBoolean.class).getValue();
	}

	@Override
	public IFlowerProvider getFlowerProvider() {
		return getActiveAllele(EnumButterflyChromosome.FLOWER_PROVIDER, IAlleleFlowers.class).getProvider();
	}

	@Override
	public IAlleleButterflyEffect getEffect() {
		return getActiveAllele(EnumButterflyChromosome.EFFECT, IAlleleButterflyEffect.class);
	}

	@Override
	public IAlleleButterflyCocoon getCocoon() {
		return getActiveAllele(EnumButterflyChromosome.COCOON, IAlleleButterflyCocoon.class);
	}
}
