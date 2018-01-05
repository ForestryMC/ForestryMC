package forestry.apiculture.genetics;

import net.minecraft.util.math.Vec3i;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenomeWrapper;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;
import forestry.core.genetics.GenomeWrapper;

public class BeeGenomeWrapper extends GenomeWrapper<EnumBeeChromosome> implements IBeeGenomeWrapper {
	public BeeGenomeWrapper(IGenome genome) {
		super(genome);
	}

	@Override
	public IAlleleBeeSpecies getPrimary(){
		return getActiveAllele(EnumBeeChromosome.SPECIES, IAlleleBeeSpecies.class);
	}

	@Override
	public IAlleleBeeSpecies getSecondary(){
		return getInactiveAllele(EnumBeeChromosome.SPECIES, IAlleleBeeSpecies.class);
	}

	@Override
	public float getSpeed() {
		return getActiveAllele(EnumBeeChromosome.SPEED, IAlleleFloat.class).getValue();
	}

	@Override
	public int getLifespan() {
		return getActiveAllele(EnumBeeChromosome.LIFESPAN, IAlleleInteger.class).getValue();
	}

	@Override
	public int getFertility() {
		return getActiveAllele(EnumBeeChromosome.FERTILITY, IAlleleInteger.class).getValue();
	}

	@Override
	public EnumTolerance getToleranceTemp() {
		return getActiveAllele(EnumBeeChromosome.TEMPERATURE_TOLERANCE, IAlleleTolerance.class).getValue();
	}

	@Override
	public boolean getNeverSleeps() {
		return getActiveAllele(EnumBeeChromosome.NEVER_SLEEPS, IAlleleBoolean.class).getValue();
	}

	@Override
	public EnumTolerance getToleranceHumid() {
		return getActiveAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE, IAlleleTolerance.class).getValue();
	}

	@Override
	public boolean getToleratesRain() {
		return getActiveAllele(EnumBeeChromosome.TOLERATES_RAIN, IAlleleBoolean.class).getValue();
	}

	@Override
	public boolean getCaveDwelling() {
		return getActiveAllele(EnumBeeChromosome.CAVE_DWELLING, IAlleleBoolean.class).getValue();
	}

	@Override
	public IFlowerProvider getFlowerProvider() {
		return getActiveAllele(EnumBeeChromosome.FLOWER_PROVIDER, IAlleleFlowers.class).getProvider();
	}

	@Override
	public int getFlowering() {
		return getActiveAllele(EnumBeeChromosome.FLOWERING, IAlleleInteger.class).getValue();
	}

	@Override
	public Vec3i getTerritory() {
		return getActiveAllele(EnumBeeChromosome.TERRITORY, IAlleleArea.class).getValue();
	}

	@Override
	public IAlleleBeeEffect getEffect() {
		return getActiveAllele(EnumBeeChromosome.EFFECT, IAlleleBeeEffect.class);
	}
}
