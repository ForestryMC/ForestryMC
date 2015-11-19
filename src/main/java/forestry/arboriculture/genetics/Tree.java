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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.arboriculture.EnumGrowthConditions;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutation;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IMutation;
import forestry.arboriculture.genetics.alleles.AlleleFruit;
import forestry.core.config.Config;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.Individual;
import forestry.core.utils.StringUtil;

public class Tree extends Individual implements ITree, IPlantable {

	private ITreeGenome genome;
	private ITreeGenome mate;

	private EnumSet<EnumPlantType> plantTypes;
	private EnumPlantType plantType;

	/* CONSTRUCTOR */
	public Tree(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	public Tree(ITreeGenome genome) {
		setGenome(genome);
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("Genome")) {
			setGenome(new TreeGenome(nbttagcompound.getCompoundTag("Genome")));
		} else {
			setGenome(TreeDefinition.Oak.getGenome());
		}

		if (nbttagcompound.hasKey("Mate")) {
			mate = new TreeGenome(nbttagcompound.getCompoundTag("Mate"));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		if (genome != null) {
			NBTTagCompound nbtGenome = new NBTTagCompound();
			genome.writeToNBT(nbtGenome);
			nbttagcompound.setTag("Genome", nbtGenome);
		}
		if (mate != null) {
			NBTTagCompound nbtMate = new NBTTagCompound();
			mate.writeToNBT(nbtMate);
			nbttagcompound.setTag("Mate", nbtMate);
		}

	}

	/* INTERACTION */
	@Override
	public void mate(ITree other) {
		mate = new TreeGenome(other.getGenome().getChromosomes());
	}

	/* EFFECTS */
	@Override
	public IEffectData[] doEffect(IEffectData[] storedData, World world, int x, int y, int z) {
		IAlleleLeafEffect effect = (IAlleleLeafEffect) getGenome().getActiveAllele(EnumTreeChromosome.EFFECT);

		if (effect == null) {
			return null;
		}

		storedData[0] = doEffect(effect, storedData[0], world, x, y, z);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable()) {
			return storedData;
		}

		IAlleleLeafEffect secondary = (IAlleleLeafEffect) getGenome().getInactiveAllele(EnumTreeChromosome.EFFECT);
		if (!secondary.isCombinable()) {
			return storedData;
		}

		storedData[1] = doEffect(secondary, storedData[1], world, x, y, z);

		return storedData;
	}

	private IEffectData doEffect(IAlleleLeafEffect effect, IEffectData storedData, World world, int x, int y, int z) {
		storedData = effect.validateStorage(storedData);
		return effect.doEffect(getGenome(), storedData, world, x, y, z);
	}

	@Override
	public IEffectData[] doFX(IEffectData[] storedData, World world, int x, int y, int z) {
		return null;
	}

	/* GROWTH */
	@Override
	public WorldGenerator getTreeGenerator(World world, int x, int y, int z, boolean wasBonemealed) {
		return genome.getPrimary().getGenerator().getWorldGenerator(this);
	}

	@Override
	public boolean canStay(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		if (block == null) {
			return false;
		}

		for (EnumPlantType type : getPlantTypes()) {
			this.plantType = type;
			if (block.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this)) {
				return true;
			}
		}

		return false;
	}

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
		IGrowthProvider growthProvider = genome.getGrowthProvider();
		return growthProvider.canGrow(genome, world, x, y, z, expectedGirth, expectedHeight);
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
		int base = (int) (getGenome().getFertility() * getGenome().getSappiness() * 100);
		return (base > 1 ? base : 1) * 10;
	}

	@Override
	public float getHeightModifier() {
		return genome.getHeight();
	}

	@Override
	public void setLeaves(World world, GameProfile owner, int x, int y, int z) {
		try {
			genome.getPrimary().getGenerator().setLeaves(genome, world, owner, x, y, z, false);
		} catch (Throwable ignored) {
			genome.getPrimary().getGenerator().setLeaves(world, owner, x, y, z, false);
		}
	}

	@Override
	public void setLeavesDecorative(World world, GameProfile owner, int x, int y, int z) {
		try {
			genome.getPrimary().getGenerator().setLeaves(genome, world, owner, x, y, z, true);
		} catch (Throwable ignored) {
			genome.getPrimary().getGenerator().setLeaves(world, owner, x, y, z, true);
		}
	}

	@Override
	public void setLogBlock(World world, int x, int y, int z, ForgeDirection facing) {
		try {
			genome.getPrimary().getGenerator().setLogBlock(genome, world, x, y, z, facing);
		} catch (Throwable ignored) {
			genome.getPrimary().getGenerator().setLogBlock(world, x, y, z, facing);
		}
	}

	@Override
	public boolean allowsFruitBlocks() {
		IFruitProvider provider = getGenome().getFruitProvider();
		if (!provider.requiresFruitBlocks()) {
			return false;
		}

		Collection<IFruitFamily> suitable = genome.getPrimary().getSuitableFruit();
		return suitable.contains(provider.getFamily());
	}

	@Override
	public boolean trySpawnFruitBlock(World world, int x, int y, int z) {
		IFruitProvider provider = getGenome().getFruitProvider();
		Collection<IFruitFamily> suitable = genome.getPrimary().getSuitableFruit();
		if (!suitable.contains(provider.getFamily())) {
			return false;
		}

		return provider.trySpawnFruitBlock(getGenome(), world, x, y, z);
	}

	/* INFORMATION */
	@Override
	public ITreeGenome getGenome() {
		return genome;
	}

	private void setGenome(ITreeGenome genome) {
		this.genome = genome;
		plantTypes = genome.getPlantTypes();
		plantTypes.add(genome.getPrimary().getPlantType());
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
		return plantTypes;
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
		if (!isPureBred(EnumTreeChromosome.SPECIES)) {
			list.add(EnumChatFormatting.BLUE + StringUtil.localize("trees.hybrid").replaceAll("%PRIMARY", primary.getName()).replaceAll("%SECONDARY", secondary.getName()));
		}

		String sappiness = EnumChatFormatting.GOLD + "S: " + genome.getActiveAllele(EnumTreeChromosome.SAPPINESS).getName();
		String maturation = EnumChatFormatting.RED + "M: " + genome.getActiveAllele(EnumTreeChromosome.MATURATION).getName();
		String height = EnumChatFormatting.LIGHT_PURPLE + "H: " + genome.getActiveAllele(EnumTreeChromosome.HEIGHT).getName();
		String girth = EnumChatFormatting.AQUA + "G: " + String.format("%sx%s", genome.getGirth(), genome.getGirth());
		String saplings = EnumChatFormatting.YELLOW + "S: " + genome.getActiveAllele(EnumTreeChromosome.FERTILITY).getName();
		String yield = EnumChatFormatting.WHITE + "Y: " + genome.getActiveAllele(EnumTreeChromosome.YIELD).getName();
		list.add(String.format("%s, %s", saplings, maturation));
		list.add(String.format("%s, %s", height, girth));
		list.add(String.format("%s, %s", yield, sappiness));

		IAlleleBoolean primaryFireproof = (IAlleleBoolean) genome.getActiveAllele(EnumTreeChromosome.FIREPROOF);
		if (primaryFireproof.getValue()) {
			list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("for.gui.fireresist"));
		}

		IAllele fruit = getGenome().getActiveAllele(EnumTreeChromosome.FRUITS);
		if (fruit != AlleleFruit.fruitNone) {
			String strike = "";
			if (!canBearFruit()) {
				strike = EnumChatFormatting.STRIKETHROUGH.toString();
			}
			list.add(strike + EnumChatFormatting.GREEN + "F: " + StringUtil.localize(genome.getFruitProvider().getDescription()));
		}

	}

	/* REPRODUCTION */
	@Override
	public ITree[] getSaplings(World world, GameProfile playerProfile, int x, int y, int z, float modifier) {
		ArrayList<ITree> prod = new ArrayList<>();

		float chance = genome.getFertility() * modifier;

		if (world.rand.nextFloat() <= chance) {
			if (this.getMate() == null) {
				prod.add(TreeManager.treeRoot.getTree(world, new TreeGenome(genome.getChromosomes())));
			} else {
				prod.add(createOffspring(world, playerProfile, x, y, z));
			}
		}

		return prod.toArray(new ITree[prod.size()]);
	}

	private ITree createOffspring(World world, GameProfile playerProfile, int x, int y, int z) {

		IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
		IChromosome[] parent1 = genome.getChromosomes();
		IChromosome[] parent2 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation
		// template if mutation occured.
		IChromosome[] mutated = mutateSpecies(world, playerProfile, x, y, z, genome, mate);
		if (mutated == null) {
			mutated = mutateSpecies(world, playerProfile, x, y, z, mate, genome);
		}

		if (mutated != null) {
			return new Tree(new TreeGenome(mutated));
		}

		for (int i = 0; i < parent1.length; i++) {
			if (parent1[i] != null && parent2[i] != null) {
				chromosomes[i] = Chromosome.inheritChromosome(world.rand, parent1[i], parent2[i]);
			}
		}

		return new Tree(new TreeGenome(chromosomes));
	}

	private static IChromosome[] mutateSpecies(World world, @Nullable GameProfile playerProfile, int x, int y, int z, ITreeGenome genomeOne, ITreeGenome genomeTwo) {

		IChromosome[] parent1 = genomeOne.getChromosomes();
		IChromosome[] parent2 = genomeTwo.getChromosomes();

		ITreeGenome genome0;
		ITreeGenome genome1;
		IAlleleTreeSpecies allele0;
		IAlleleTreeSpecies allele1;

		if (world.rand.nextBoolean()) {
			allele0 = (IAlleleTreeSpecies) parent1[EnumTreeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = (IAlleleTreeSpecies) parent2[EnumTreeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeOne;
			genome1 = genomeTwo;
		} else {
			allele0 = (IAlleleTreeSpecies) parent2[EnumTreeChromosome.SPECIES.ordinal()].getPrimaryAllele();
			allele1 = (IAlleleTreeSpecies) parent1[EnumTreeChromosome.SPECIES.ordinal()].getSecondaryAllele();

			genome0 = genomeTwo;
			genome1 = genomeOne;
		}

		IArboristTracker breedingTracker = null;
		if (playerProfile != null) {
			breedingTracker = TreeManager.treeRoot.getBreedingTracker(world, playerProfile);
		}

		List<IMutation> combinations = TreeManager.treeRoot.getCombinations(allele0, allele1, true);
		for (IMutation mutation : combinations) {
			ITreeMutation treeMutation = (ITreeMutation) mutation;
			// Stop blacklisted species.
			// if (BeeManager.breedingManager.isBlacklisted(mutation.getTemplate()[0].getUID())) {
			// continue;
			// }

			float chance = treeMutation.getChance(world, x, y, z, allele0, allele1, genome0, genome1);
			if (chance <= 0) {
				continue;
			}

			// boost chance for researched mutations
			if (breedingTracker != null && breedingTracker.isResearched(treeMutation)) {
				float mutationBoost = chance * (Config.researchMutationBoostMultiplier - 1.0f);
				mutationBoost = Math.min(Config.maxResearchMutationBoostPercent, mutationBoost);
				chance += mutationBoost;
			}

			if (chance > world.rand.nextFloat() * 100) {
				return TreeManager.treeRoot.templateAsChromosomes(treeMutation.getTemplate());
			}
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
