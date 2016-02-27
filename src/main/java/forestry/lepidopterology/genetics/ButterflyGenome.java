/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.lepidopterology.genetics;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.lepidopterology.ButterflyChromosome;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.core.genetics.Genome;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.genetics.alleles.AlleleTolerance;

public class ButterflyGenome extends Genome<ButterflyChromosome> implements IButterflyGenome {

	/* CONSTRUCTOR */
	public ButterflyGenome(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
	}

	public ButterflyGenome(ImmutableMap<ButterflyChromosome, IChromosome> chromosomes) {
		super(chromosomes);
	}

	// NBT RETRIEVAL
	public static IAlleleButterflySpecies getSpecies(ItemStack itemStack) {
		if (!ButterflyManager.butterflyRoot.isMember(itemStack)) {
			return null;
		}
		
		IAlleleSpecies species = getSpeciesDirectly(itemStack);
		if (species instanceof IAlleleButterflySpecies) {
			return (IAlleleButterflySpecies) species;
		}

		return (IAlleleButterflySpecies) getActiveAllele(itemStack, ButterflyChromosome.SPECIES, ButterflyManager.butterflyRoot);
	}

	/* SPECIES */
	@Nonnull
	@Override
	public IAlleleButterflySpecies getPrimary() {
		return (IAlleleButterflySpecies) getActiveAllele(ButterflyChromosome.SPECIES);
	}

	@Nonnull
	@Override
	public IAlleleButterflySpecies getSecondary() {
		return (IAlleleButterflySpecies) getInactiveAllele(ButterflyChromosome.SPECIES);
	}

	@Override
	public float getSize() {
		return ((IAlleleFloat) getActiveAllele(ButterflyChromosome.SIZE)).getValue();
	}
	
	@Override
	public int getLifespan() {
		return ((IAlleleInteger) getActiveAllele(ButterflyChromosome.LIFESPAN)).getValue();
	}
	
	@Override
	public float getSpeed() {
		return ((IAlleleFloat) getActiveAllele(ButterflyChromosome.SPEED)).getValue();
	}

	@Override
	public int getMetabolism() {
		return ((IAlleleInteger) getActiveAllele(ButterflyChromosome.METABOLISM)).getValue();
	}

	@Override
	public int getFertility() {
		return ((IAlleleInteger) getActiveAllele(ButterflyChromosome.FERTILITY)).getValue();
	}

	@Override
	public EnumTolerance getToleranceTemp() {
		return ((AlleleTolerance) getActiveAllele(ButterflyChromosome.TEMPERATURE_TOLERANCE)).getValue();
	}

	@Override
	public EnumTolerance getToleranceHumid() {
		return ((AlleleTolerance) getActiveAllele(ButterflyChromosome.HUMIDITY_TOLERANCE)).getValue();
	}

	@Override
	public boolean getNeverSleeps() {
		return ((AlleleBoolean) getActiveAllele(ButterflyChromosome.NOCTURNAL)).getValue();
	}

	@Override
	public boolean getTolerantFlyer() {
		return ((AlleleBoolean) getActiveAllele(ButterflyChromosome.TOLERANT_FLYER)).getValue();
	}

	@Override
	public boolean getFireResist() {
		return ((AlleleBoolean) getActiveAllele(ButterflyChromosome.FIRE_RESIST)).getValue();
	}

	@Override
	public IFlowerProvider getFlowerProvider() {
		return ((IAlleleFlowers) getActiveAllele(ButterflyChromosome.FLOWER_PROVIDER)).getProvider();
	}

	@Override
	public IAlleleButterflyEffect getEffect() {
		return (IAlleleButterflyEffect) getActiveAllele(ButterflyChromosome.EFFECT);
	}

	@Nonnull
	@Override
	public IButterflyRoot getSpeciesRoot() {
		return ButterflyManager.butterflyRoot;
	}

}
