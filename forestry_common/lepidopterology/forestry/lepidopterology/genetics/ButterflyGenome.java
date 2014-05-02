/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.lepidopterology.genetics;

import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.core.genetics.AlleleBoolean;
import forestry.core.genetics.AlleleTolerance;
import forestry.core.genetics.Genome;
import forestry.plugins.PluginLepidopterology;

public class ButterflyGenome extends Genome implements IButterflyGenome {

	/* CONSTRUCTOR */
	public ButterflyGenome(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
	}

	public ButterflyGenome(IChromosome[] chromosomes) {
		super(chromosomes);
	}

	/* SPECIES */
	@Override
	public IAlleleButterflySpecies getPrimary() {
		return (IAlleleButterflySpecies) getActiveAllele(EnumButterflyChromosome.SPECIES.ordinal());
	}

	@Override
	public IAlleleButterflySpecies getSecondary() {
		return (IAlleleButterflySpecies) getInactiveAllele(EnumButterflyChromosome.SPECIES.ordinal());
	}

	@Override
	public float getSize() {
		return ((IAlleleFloat) getActiveAllele(EnumButterflyChromosome.SIZE.ordinal())).getValue();
	}
	
	@Override
	public int getLifespan() {
		return ((IAlleleInteger) getActiveAllele(EnumButterflyChromosome.LIFESPAN.ordinal())).getValue();
	}
	
	@Override
	public float getSpeed() {
		return ((IAlleleFloat) getActiveAllele(EnumButterflyChromosome.SPEED.ordinal())).getValue();
	}

	@Override
	public int getMetabolism() {
		return ((IAlleleInteger) getActiveAllele(EnumButterflyChromosome.METABOLISM.ordinal())).getValue();
	}

	@Override
	public int getFertility() {
		return ((IAlleleInteger) getActiveAllele(EnumButterflyChromosome.FERTILITY.ordinal())).getValue();
	}

	@Override
	public EnumTolerance getToleranceTemp() {
		return ((AlleleTolerance) getActiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal())).getValue();
	}

	@Override
	public EnumTolerance getToleranceHumid() {
		return ((AlleleTolerance) getActiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal())).getValue();
	}

	@Override
	public boolean getNocturnal() {
		return ((AlleleBoolean) getActiveAllele(EnumButterflyChromosome.NOCTURNAL.ordinal())).getValue();
	}

	@Override
	public boolean getTolerantFlyer() {
		return ((AlleleBoolean) getActiveAllele(EnumButterflyChromosome.TOLERANT_FLYER.ordinal())).getValue();
	}

	@Override
	public boolean getFireResist() {
		return ((AlleleBoolean) getActiveAllele(EnumButterflyChromosome.FIRE_RESIST.ordinal())).getValue();
	}

	@Override
	public IFlowerProvider getFlowerProvider() {
		return ((IAlleleFlowers) getActiveAllele(EnumButterflyChromosome.FLOWER_PROVIDER.ordinal())).getProvider();
	}

	@Override
	public IAlleleButterflyEffect getEffect() {
		return (IAlleleButterflyEffect) getActiveAllele(EnumButterflyChromosome.EFFECT.ordinal());
	}

	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return PluginLepidopterology.butterflyInterface;
	}

}
