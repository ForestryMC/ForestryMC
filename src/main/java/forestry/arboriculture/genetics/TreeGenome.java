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

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.TreeChromosome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.core.genetics.Genome;
import forestry.core.genetics.alleles.AllelePlantType;

public class TreeGenome extends Genome<TreeChromosome> implements ITreeGenome {

	public TreeGenome(@Nonnull ImmutableMap<TreeChromosome, IChromosome> chromosomes) {
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

		return (IAlleleTreeSpecies) getActiveAllele(itemStack, TreeChromosome.SPECIES, TreeManager.treeRoot);
	}

	@Nonnull
	@Override
	public IAlleleTreeSpecies getPrimary() {
		return (IAlleleTreeSpecies) getActiveAllele(TreeChromosome.SPECIES);
	}

	@Nonnull
	@Override
	public IAlleleTreeSpecies getSecondary() {
		return (IAlleleTreeSpecies) getInactiveAllele(TreeChromosome.SPECIES);
	}

	@Override
	public IFruitProvider getFruitProvider() {
		return ((IAlleleFruit) getActiveAllele(TreeChromosome.FRUITS)).getProvider();
	}

	@Override
	public IGrowthProvider getGrowthProvider() {
		return ((IAlleleGrowth) getActiveAllele(TreeChromosome.GROWTH)).getProvider();
	}

	@Override
	public float getHeight() {
		return ((IAlleleFloat) getActiveAllele(TreeChromosome.HEIGHT)).getValue();
	}

	@Override
	public float getFertility() {
		return ((IAlleleFloat) getActiveAllele(TreeChromosome.FERTILITY)).getValue();
	}

	@Override
	public float getYield() {
		return ((IAlleleFloat) getActiveAllele(TreeChromosome.YIELD)).getValue();
	}

	@Override
	public float getSappiness() {
		return ((IAlleleFloat) getActiveAllele(TreeChromosome.SAPPINESS)).getValue();
	}

	@Override
	public EnumSet<EnumPlantType> getPlantTypes() {
		return ((AllelePlantType) getActiveAllele(TreeChromosome.PLANT)).getPlantTypes();
	}

	@Override
	public int getMaturationTime() {
		return ((IAlleleInteger) getActiveAllele(TreeChromosome.MATURATION)).getValue();
	}

	@Override
	public int getGirth() {
		return ((IAlleleInteger) getActiveAllele(TreeChromosome.GIRTH)).getValue();
	}
	
	@Override
	public IAlleleLeafEffect getEffect() {
		return (IAlleleLeafEffect) getActiveAllele(TreeChromosome.EFFECT);
	}

	@Nonnull
	@Override
	public ITreeRoot getSpeciesRoot() {
		return TreeManager.treeRoot;
	}

}
