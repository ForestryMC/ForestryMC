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


import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyGenomeWrapper;
import forestry.core.genetics.Genome;

public class ButterflyGenome extends Genome implements IButterflyGenome {

	private IButterflyGenomeWrapper wrapper;

	/* CONSTRUCTOR */
	public ButterflyGenome(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
		this.wrapper = ButterflyManager.butterflyRoot.getWrapper(this);
	}

	public ButterflyGenome(IChromosome[] chromosomes) {
		super(chromosomes);
		this.wrapper = ButterflyManager.butterflyRoot.getWrapper(this);
	}

	// NBT RETRIEVAL
	public static IAlleleButterflySpecies getSpecies(ItemStack itemStack) {
		Preconditions.checkArgument(ButterflyManager.butterflyRoot.isMember(itemStack), "Must be a butterfly");

		IAlleleSpecies species = getSpeciesDirectly(ButterflyManager.butterflyRoot, itemStack);
		if (species instanceof IAlleleButterflySpecies) {
			return (IAlleleButterflySpecies) species;
		}

		return (IAlleleButterflySpecies) getAllele(itemStack, EnumButterflyChromosome.SPECIES, true);
	}

	/* SPECIES */
	@Override
	public IAlleleButterflySpecies getPrimary() {
		return wrapper.getPrimary();
	}

	@Override
	public IAlleleButterflySpecies getSecondary() {
		return wrapper.getSecondary();
	}

	@Override
	public float getSize() {
		return wrapper.getSize();
	}

	@Override
	public int getLifespan() {
		return wrapper.getLifespan();
	}

	@Override
	public float getSpeed() {
		return wrapper.getSpeed();
	}

	@Override
	public int getMetabolism() {
		return wrapper.getMetabolism();
	}

	@Override
	public int getFertility() {
		return wrapper.getFertility();
	}

	@Override
	public EnumTolerance getToleranceTemp() {
		return wrapper.getToleranceTemp();
	}

	@Override
	public EnumTolerance getToleranceHumid() {
		return wrapper.getToleranceHumid();
	}

	@Override
	public boolean getNocturnal() {
		return wrapper.getNocturnal();
	}

	@Override
	public boolean getTolerantFlyer() {
		return wrapper.getTolerantFlyer();
	}

	@Override
	public boolean getFireResist() {
		return wrapper.getFireResist();
	}

	@Override
	public IFlowerProvider getFlowerProvider() {
		return wrapper.getFlowerProvider();
	}

	@Override
	public IAlleleButterflyEffect getEffect() {
		return wrapper.getEffect();
	}

	@Override
	public IAlleleButterflyCocoon getCocoon() {
		return wrapper.getCocoon();
	}

	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return ButterflyManager.butterflyRoot;
	}

	@Override
	public IGenome getGenome() {
		return this;
	}

	@Override
	public <A extends IAllele> A getActiveAllele(EnumButterflyChromosome chromosomeType, Class<A> alleleClass) {
		return wrapper.getActiveAllele(chromosomeType, alleleClass);
	}

	@Override
	public <A extends IAllele> A getInactiveAllele(EnumButterflyChromosome chromosomeType, Class<A> alleleClass) {
		return wrapper.getInactiveAllele(chromosomeType, alleleClass);
	}
}
