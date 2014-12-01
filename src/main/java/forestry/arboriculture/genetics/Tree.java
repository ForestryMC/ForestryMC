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

import com.mojang.authlib.GameProfile;
import forestry.api.arboriculture.EnumGrowthConditions;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IGenome;
import forestry.api.world.ITreeGenData;
import forestry.core.genetics.Allele;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.Individual;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginArboriculture;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class Tree extends Individual implements ITree, ITreeGenData, IPlantable {

	private ITreeGenome genome;
	private ITreeGenome mate;

	/* CONSTRUCTOR */
	public Tree(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	public Tree(ITreeGenome genome) {
		this.genome = genome;
	}

	public Tree(World world, ITreeGenome genome) {
		this.genome = genome;

	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("Genome"))
			genome = new TreeGenome(nbttagcompound.getCompoundTag("Genome"));
		else
			genome = PluginArboriculture.treeInterface.templateAsGenome(TreeTemplates.getOakTemplate());
		if (nbttagcompound.hasKey("Mate"))
			mate = new TreeGenome(nbttagcompound.getCompoundTag("Mate"));

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		if (genome != null) {
			NBTTagCompound NBTmachine = new NBTTagCompound();
			genome.writeToNBT(NBTmachine);
			nbttagcompound.setTag("Genome", NBTmachine);
		}
		if (mate != null) {
			NBTTagCompound NBTmachine = new NBTTagCompound();
			mate.writeToNBT(NBTmachine);
			nbttagcompound.setTag("Mate", NBTmachine);
		}

	}

	/* INTERACTION */
	@Override
	public void mate(ITree other) {
		mate = new TreeGenome(other.getGenome().getChromosomes());
	}

	/* EFFECTS */
	@Override
	public IEffectData[] doEffect(IEffectData[] storedData, World world, int biomeid, int x, int y, int z) {
		IAlleleLeafEffect effect = (IAlleleLeafEffect) getGenome().getActiveAllele(EnumTreeChromosome.EFFECT);

		if (effect == null)
			return null;

		storedData[0] = doEffect(effect, storedData[0], world, biomeid, x, y, z);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable())
			return storedData;

		IAlleleLeafEffect secondary = (IAlleleLeafEffect) getGenome().getInactiveAllele(EnumTreeChromosome.EFFECT);
		if (!secondary.isCombinable())
			return storedData;

		storedData[1] = doEffect(secondary, storedData[1], world, biomeid, x, y, z);

		return storedData;
	}

	private IEffectData doEffect(IAlleleLeafEffect effect, IEffectData storedData, World world, int biomeid, int x, int y, int z) {
		storedData = effect.validateStorage(storedData);
		return effect.doEffect(getGenome(), storedData, world, x, y, z);
	}

	@Override
	public IEffectData[] doFX(IEffectData[] storedData, World world, int biomeid, int x, int y, int z) {
		return null;
	}

	/* GROWTH */
	@Override
	public WorldGenerator getTreeGenerator(World world, int x, int y, int z, boolean wasBonemealed) {
		return genome.getPrimary().getGenerator(this, world, x, y, z);
	}

	@Override
	public boolean canStay(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		if (block == null)
			return false;

		for (EnumPlantType type : getPlantTypes()) {
			this.plantType = type;
			if (block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this))
				return true;
		}

		return false;
	}

	private EnumPlantType plantType;

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return plantType;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return null;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return 0;
	}

	@Override
	public boolean canGrow(World world, int x, int y, int z, int expectedGirth, int expectedHeight) {
		return genome.getGrowthProvider().canGrow(genome, world, x, y, z, expectedGirth, expectedHeight);
	}

	@Override
	public int getRequiredMaturity() {
		return genome.getMaturationTime();
	}

	@Override
	public EnumGrowthConditions getGrowthCondition(World world, int x, int y, int z) {
		return genome.getGrowthProvider().getGrowthConditions(getGenome(), world, x, y, z);
	}

	@Override
	public int getGirth(World world, int x, int y, int z) {
		return genome.getGirth();
	}

	@Override
	public int getResilience() {
		int base = (int)(getGenome().getFertility() * getGenome().getSappiness() * 100);
		return (base > 1 ? base : 1) * 10;
	}

	@Override
	public float getHeightModifier() {
		return genome.getHeight();
	}

	@Override
	public void setLeaves(World world, GameProfile owner, int x, int y, int z) {
		PluginArboriculture.treeInterface.setLeaves(world, this, owner, x, y, z);
	}

	@Override
	public boolean allowsFruitBlocks() {
		IFruitProvider provider = getGenome().getFruitProvider();
		if (!provider.requiresFruitBlocks())
			return false;

		Collection<IFruitFamily> suitable = genome.getPrimary().getSuitableFruit();
		if (!suitable.contains(provider.getFamily()))
			return false;

		return true;
	}

	@Override
	public boolean trySpawnFruitBlock(World world, int x, int y, int z) {
		IFruitProvider provider = getGenome().getFruitProvider();
		Collection<IFruitFamily> suitable = genome.getPrimary().getSuitableFruit();
		if (!suitable.contains(provider.getFamily()))
			return false;

		return provider.trySpawnFruitBlock(getGenome(), world, x, y, z);
	}

	/* INFORMATION */
	@Override
	public ITreeGenome getGenome() {
		return this.genome;
	}

	@Override
	public ITree copy() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new Tree(nbttagcompound);
	}

	@Override
	public ITreeGenome getMate() {
		return this.mate;
	}

	@Override
	public boolean isPureBred(EnumTreeChromosome chromosome) {
		return genome.getActiveAllele(chromosome).getUID().equals(genome.getInactiveAllele(chromosome).getUID());
	}

	@Override
	public EnumSet<EnumPlantType> getPlantTypes() {
		EnumSet<EnumPlantType> tolerated = genome.getPlantTypes();
		tolerated.add(genome.getPrimary().getPlantType());
		return tolerated;
	}

	@Override
	public void addTooltip(List<String> list) {

		// No info 4 u!
		if (!isAnalyzed) {
			list.add("<" + StringUtil.localize("gui.unknown") + ">");
			return;
		}

		// You analyzed it? Juicy tooltip coming up!
		IAlleleTreeSpecies primary = genome.getPrimary();
		IAlleleTreeSpecies secondary = genome.getSecondary();
		if (!isPureBred(EnumTreeChromosome.SPECIES))
			list.add("\u00A79" + StringUtil.localize("trees.hybrid").replaceAll("%PRIMARY",primary.getName()).replaceAll("%SECONDARY",secondary.getName()));
		list.add(String.format("\u00A76S: %s, \u00A7cM: %s", genome.getActiveAllele(EnumTreeChromosome.SAPPINESS).getName(), genome.getActiveAllele(EnumTreeChromosome.MATURATION).getName()));
		list.add(String.format("\u00A7dH: %s, \u00A7bG: %sx%s", genome.getActiveAllele(EnumTreeChromosome.HEIGHT).getName(), genome.getGirth(), genome.getGirth()));

		list.add(String.format("\u00A7eS: %s, \u00A7fY: %s", genome.getActiveAllele(EnumTreeChromosome.FERTILITY).getName(), genome.getActiveAllele(EnumTreeChromosome.YIELD).getName()));

		IAlleleBoolean primaryFireproof = (IAlleleBoolean)genome.getActiveAllele(EnumTreeChromosome.FIREPROOF);
		if (primaryFireproof.getValue())
			list.add(String.format("\u00A7c%s", StatCollector.translateToLocal("for.gui.fireresist")));

		IAllele fruit = getGenome().getActiveAllele(EnumTreeChromosome.FRUITS);
		if(fruit != Allele.fruitNone) {
			String strike = "";
			if (!canBearFruit())
				strike = "\u00A7m";
			list.add(strike + "\u00A7aF: " + StringUtil.localize(genome.getFruitProvider().getDescription()));
		}

	}

	/* REPRODUCTION */
	@Override
	public ITree[] getSaplings(World world, int x, int y, int z, float modifier) {
		ArrayList<ITree> prod = new ArrayList<ITree>();

		float chance = genome.getFertility() * modifier;

		if (world.rand.nextFloat() <= chance)
			if (this.getMate() == null)
				prod.add(PluginArboriculture.treeInterface.getTree(world, new TreeGenome(genome.getChromosomes())));
			else
				prod.add(createOffspring(world, x, y, z));

		return prod.toArray(new ITree[prod.size()]);
	}

	private ITree createOffspring(World world, int x, int y, int z) {

		IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
		IChromosome[] parent1 = genome.getChromosomes();
		IChromosome[] parent2 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation
		// template if mutation occured.
		IChromosome[] mutated = mutateSpecies(world, x, y, z, genome, mate);
		if (mutated == null)
			mutated = mutateSpecies(world, x, y, z, mate, genome);

		if (mutated != null)
			return new Tree(world, new TreeGenome(mutated));

		for (int i = 0; i < parent1.length; i++)
			if (parent1[i] != null && parent2[i] != null)
				chromosomes[i] = Chromosome.inheritChromosome(world.rand, parent1[i], parent2[i]);

		return new Tree(world, new TreeGenome(chromosomes));
	}

	private IChromosome[] mutateSpecies(World world, int x, int y, int z, IGenome genomeOne, IGenome genomeTwo) {

		IChromosome[] parent1 = genomeOne.getChromosomes();
		IChromosome[] parent2 = genomeTwo.getChromosomes();

		IGenome genome0;
		IGenome genome1;
		IAllele allele0;
		IAllele allele1;

		if (world.rand.nextBoolean()) {
			allele0 = parent1[EnumTreeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent2[EnumTreeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = parent2[EnumTreeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = parent1[EnumTreeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		for (ITreeMutation mutation : PluginArboriculture.treeInterface.getMutations(true)) {
			float chance = 0;

			// Stop blacklisted species.
			// if (BeeManager.breedingManager.isBlacklisted(mutation.getTemplate()[0].getUID())) {
			// continue;
			// }

			if ((chance = mutation.getChance(world, x, y, z, allele0, allele1, genome0, genome1)) > 0)
				if (world.rand.nextFloat()*100 < chance)
					// IApiaristTracker breedingTracker = BeeManager.breedingManager.getApiaristTracker(world);
					// breedingTracker.registerMutation(mutation);
					return PluginArboriculture.treeInterface.templateAsChromosomes(mutation.getTemplate());
		}

		return null;
	}

	/* PRODUCTION */
	@Override
	public boolean canBearFruit() {
		return genome.getPrimary().getSuitableFruit().contains(genome.getFruitProvider().getFamily());
	}

	@Override
	public ItemStack[] getProduceList() {
		return genome.getFruitProvider().getProducts();
	}

	@Override
	public ItemStack[] getSpecialtyList() {
		return genome.getFruitProvider().getSpecialty();
	}

	@Override
	public ItemStack[] produceStacks(World world, int x, int y, int z, int ripeningTime) {
		return genome.getFruitProvider().getFruits(genome, world, x, y, z, ripeningTime);
	}

}
