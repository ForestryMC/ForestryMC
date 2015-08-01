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

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.Genome;
import forestry.core.genetics.alleles.AllelePlantType;

public class TreeGenome extends Genome implements ITreeGenome {

	public TreeGenome(IChromosome[] chromosomes) {
		super(chromosomes);
	}

	public TreeGenome(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
	}

	// NBT RETRIEVAL
	public static IAlleleTreeSpecies getSpecies(ItemStack itemStack) {
		if (!TreeManager.treeRoot.isMember(itemStack)) {
			return null;
		}

		IAlleleSpecies species = getSpeciesDirectly(itemStack);
		if (species instanceof IAlleleTreeSpecies) {
			return (IAlleleTreeSpecies) species;
		}

		return (IAlleleTreeSpecies) getActiveAllele(itemStack, EnumTreeChromosome.SPECIES, TreeManager.treeRoot);
	}

	@Override
	public IAlleleTreeSpecies getPrimary() {
		return (IAlleleTreeSpecies) getActiveAllele(EnumTreeChromosome.SPECIES);
	}

	@Override
	public IAlleleTreeSpecies getSecondary() {
		return (IAlleleTreeSpecies) getInactiveAllele(EnumTreeChromosome.SPECIES);
	}

	@Override
	public IFruitProvider getFruitProvider() {
		return ((IAlleleFruit) getActiveAllele(EnumTreeChromosome.FRUITS)).getProvider();
	}

	@Override
	public IGrowthProvider getGrowthProvider() {
		return ((IAlleleGrowth) getActiveAllele(EnumTreeChromosome.GROWTH)).getProvider();
	}

	@Override
	public float getHeight() {
		return ((IAlleleFloat) getActiveAllele(EnumTreeChromosome.HEIGHT)).getValue();
	}

	@Override
	public float getFertility() {
		return ((IAlleleFloat) getActiveAllele(EnumTreeChromosome.FERTILITY)).getValue();
	}

	@Override
	public float getYield() {
		return ((IAlleleFloat) getActiveAllele(EnumTreeChromosome.YIELD)).getValue();
	}

	@Override
	public float getSappiness() {
		return ((IAlleleFloat) getActiveAllele(EnumTreeChromosome.SAPPINESS)).getValue();
	}

	@Override
	public EnumSet<EnumPlantType> getPlantTypes() {
		return ((AllelePlantType) getActiveAllele(EnumTreeChromosome.PLANT)).getPlantTypes();
	}

	@Override
	public int getMaturationTime() {
		return ((IAlleleInteger) getActiveAllele(EnumTreeChromosome.MATURATION)).getValue();
	}

	@Override
	public int getGirth() {
		return ((IAlleleInteger) getActiveAllele(EnumTreeChromosome.GIRTH)).getValue();
	}
	
	@Override
	public IAlleleLeafEffect getEffect() {
		return (IAlleleLeafEffect) getActiveAllele(EnumTreeChromosome.EFFECT);
	}

	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return TreeManager.treeRoot;
	}

}
