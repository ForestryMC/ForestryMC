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
package forestry.arboriculture.genetics;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeGenomeWrapper;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.Genome;

public class TreeGenome extends Genome implements ITreeGenome {
	private ITreeGenomeWrapper wrapper;

	/* CONSTRUCTOR */
	public TreeGenome(IChromosome[] chromosomes) {
		super(chromosomes);
		this.wrapper = TreeManager.treeRoot.getWrapper(this);
	}

	public TreeGenome(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
		this.wrapper = TreeManager.treeRoot.getWrapper(this);
	}

	// NBT RETRIEVAL
	public static IAlleleTreeSpecies getSpecies(ItemStack itemStack) {
		Preconditions.checkArgument(TreeManager.treeRoot.isMember(itemStack), "ItemStack must be a tree");

		IAlleleSpecies species = getSpeciesDirectly(TreeManager.treeRoot, itemStack);
		if (species instanceof IAlleleTreeSpecies) {
			return (IAlleleTreeSpecies) species;
		}

		return (IAlleleTreeSpecies) getAllele(itemStack, EnumTreeChromosome.SPECIES, true);
	}

	@Override
	public IAlleleTreeSpecies getPrimary() {
		return wrapper.getPrimary();
	}

	@Override
	public IAlleleTreeSpecies getSecondary() {
		return wrapper.getSecondary();
	}

	@Override
	public IFruitProvider getFruitProvider() {
		return wrapper.getFruitProvider();
	}

	@Override
	public float getHeight() {
		return wrapper.getHeight();
	}

	@Override
	public float getFertility() {
		return wrapper.getFertility();
	}

	@Override
	public float getYield() {
		return wrapper.getYield();
	}

	@Override
	public float getSappiness() {
		return wrapper.getSappiness();
	}

	@Override
	public int getMaturationTime() {
		return wrapper.getMaturationTime();
	}

	@Override
	public int getGirth() {
		return wrapper.getGirth();
	}

	@Override
	public IAlleleLeafEffect getEffect() {
		return wrapper.getEffect();
	}
	
	@Override
	public ItemStack getDecorativeLeaves() {
		return wrapper.getDecorativeLeaves();
	}

	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return TreeManager.treeRoot;
	}

	@Override
	public boolean matchesTemplateGenome() {
		return wrapper.matchesTemplateGenome();
	}

	@Override
	public IGenome getGenome() {
		return this;
	}

	@Override
	public <A extends IAllele> A getActiveAllele(EnumTreeChromosome chromosomeType, Class<A> alleleClass) {
		return wrapper.getActiveAllele(chromosomeType, alleleClass);
	}

	@Override
	public <A extends IAllele> A getInactiveAllele(EnumTreeChromosome chromosomeType, Class<A> alleleClass) {
		return wrapper.getInactiveAllele(chromosomeType, alleleClass);
	}
}
