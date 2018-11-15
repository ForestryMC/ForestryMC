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
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.IFruitProvider;
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
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.config.Config;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.Individual;
import forestry.core.utils.Translator;

public class Tree extends Individual implements ITree, IPlantable {
	private final ITreeGenome genome;
	@Nullable
	private ITreeGenome mate;

	public Tree(ITreeGenome genome) {
		this.genome = genome;
	}

	public Tree(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);

		if (nbttagcompound.hasKey("Genome")) {
			this.genome = new TreeGenome(nbttagcompound.getCompoundTag("Genome"));
		} else {
			throw new IllegalArgumentException("Nbt has no Genome " + nbttagcompound);
		}

		if (nbttagcompound.hasKey("Mate")) {
			mate = new TreeGenome(nbttagcompound.getCompoundTag("Mate"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		NBTTagCompound nbtGenome = new NBTTagCompound();
		genome.writeToNBT(nbtGenome);
		nbttagcompound.setTag("Genome", nbtGenome);

		if (mate != null) {
			NBTTagCompound nbtMate = new NBTTagCompound();
			mate.writeToNBT(nbtMate);
			nbttagcompound.setTag("Mate", nbtMate);
		}
		return nbttagcompound;
	}

	/* INTERACTION */
	@Override
	public void mate(ITree other) {
		mate = new TreeGenome(other.getGenome().getChromosomes());
	}

	/* EFFECTS */
	@Override
	public IEffectData[] doEffect(IEffectData[] storedData, World world, BlockPos pos) {
		IAlleleLeafEffect effect = (IAlleleLeafEffect) getGenome().getActiveAllele(EnumTreeChromosome.EFFECT);

		storedData[0] = doEffect(effect, storedData[0], world, pos);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable()) {
			return storedData;
		}

		IAlleleLeafEffect secondary = (IAlleleLeafEffect) getGenome().getInactiveAllele(EnumTreeChromosome.EFFECT);
		if (!secondary.isCombinable()) {
			return storedData;
		}

		storedData[1] = doEffect(secondary, storedData[1], world, pos);

		return storedData;
	}

	private IEffectData doEffect(IAlleleLeafEffect effect, IEffectData storedData, World world, BlockPos pos) {
		storedData = effect.validateStorage(storedData);
		return effect.doEffect(getGenome(), storedData, world, pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IEffectData[] doFX(IEffectData[] storedData, World world, BlockPos pos) {
		return storedData;
	}

	/* GROWTH */
	@Override
	public WorldGenerator getTreeGenerator(World world, BlockPos pos, boolean wasBonemealed) {
		return genome.getPrimary().getGenerator().getWorldGenerator(this);
	}

	@Override
	public boolean canStay(IBlockAccess world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		IBlockState blockState = world.getBlockState(blockPos);

		Block block = blockState.getBlock();
		return block.canSustainPlant(blockState, world, blockPos, EnumFacing.UP, this);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return genome.getPrimary().getPlantType();
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return world.getBlockState(pos);
	}

	@Override
	@Nullable
	public BlockPos canGrow(World world, BlockPos pos, int expectedGirth, int expectedHeight) {
		return TreeGrowthHelper.canGrow(world, genome, pos, expectedGirth, expectedHeight);
	}

	@Override
	public int getRequiredMaturity() {
		return genome.getMaturationTime();
	}

	@Override
	public int getGirth() {
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
	public boolean setLeaves(World world, @Nullable GameProfile owner, BlockPos pos, Random rand) {
		return genome.getPrimary().getGenerator().setLeaves(genome, world, owner, pos, rand);
	}

	@Override
	public boolean setLeaves(World world, @Nullable GameProfile owner, BlockPos pos) {
		return setLeaves(world, owner, pos, world.rand);
	}

	@Override
	public boolean setLogBlock(World world, BlockPos pos, EnumFacing facing) {
		return genome.getPrimary().getGenerator().setLogBlock(genome, world, pos, facing);
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
	public boolean trySpawnFruitBlock(World world, Random rand, BlockPos pos) {
		IFruitProvider provider = getGenome().getFruitProvider();
		Collection<IFruitFamily> suitable = genome.getPrimary().getSuitableFruit();
		return suitable.contains(provider.getFamily()) &&
			provider.trySpawnFruitBlock(getGenome(), world, rand, pos);
	}

	/* INFORMATION */
	@Override
	public ITreeGenome getGenome() {
		return genome;
	}

	@Override
	public ITree copy() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new Tree(nbttagcompound);
	}

	@Nullable
	@Override
	public ITreeGenome getMate() {
		return this.mate;
	}

	@Override
	public boolean isPureBred(EnumTreeChromosome chromosome) {
		return genome.getActiveAllele(chromosome).getUID().equals(genome.getInactiveAllele(chromosome).getUID());
	}

	@Override
	public void addTooltip(List<String> list) {

		// No info 4 u!
		if (!isAnalyzed) {
			list.add("<" + Translator.translateToLocal("for.gui.unknown") + ">");
			return;
		}

		// You analyzed it? Juicy tooltip coming up!
		IAlleleTreeSpecies primary = genome.getPrimary();
		IAlleleTreeSpecies secondary = genome.getSecondary();
		if (!isPureBred(EnumTreeChromosome.SPECIES)) {
			list.add(TextFormatting.BLUE + Translator.translateToLocal("for.trees.hybrid").replaceAll("%PRIMARY", primary.getAlleleName()).replaceAll("%SECONDARY", secondary.getAlleleName()));
		}

		String sappiness = TextFormatting.GOLD + "S: " + genome.getActiveAllele(EnumTreeChromosome.SAPPINESS).getAlleleName();
		String maturation = TextFormatting.RED + "M: " + genome.getActiveAllele(EnumTreeChromosome.MATURATION).getAlleleName();
		String height = TextFormatting.LIGHT_PURPLE + "H: " + genome.getActiveAllele(EnumTreeChromosome.HEIGHT).getAlleleName();
		String girth = TextFormatting.AQUA + "G: " + String.format("%sx%s", genome.getGirth(), genome.getGirth());
		String saplings = TextFormatting.YELLOW + "S: " + genome.getActiveAllele(EnumTreeChromosome.FERTILITY).getAlleleName();
		String yield = TextFormatting.WHITE + "Y: " + genome.getActiveAllele(EnumTreeChromosome.YIELD).getAlleleName();
		list.add(String.format("%s, %s", saplings, maturation));
		list.add(String.format("%s, %s", height, girth));
		list.add(String.format("%s, %s", yield, sappiness));

		IAlleleBoolean primaryFireproof = (IAlleleBoolean) genome.getActiveAllele(EnumTreeChromosome.FIREPROOF);
		if (primaryFireproof.getValue()) {
			list.add(TextFormatting.RED + Translator.translateToLocal("for.gui.fireresist"));
		}

		IAllele fruit = getGenome().getActiveAllele(EnumTreeChromosome.FRUITS);
		if (fruit != AlleleFruits.fruitNone) {
			String strike = "";
			if (!canBearFruit()) {
				strike = TextFormatting.STRIKETHROUGH.toString();
			}
			list.add(strike + TextFormatting.GREEN + "F: " + genome.getFruitProvider().getDescription());
		}
	}

	/* REPRODUCTION */
	@Override
	public List<ITree> getSaplings(World world, @Nullable GameProfile playerProfile, BlockPos pos, float modifier) {
		List<ITree> prod = new ArrayList<>();

		float chance = genome.getFertility() * modifier;

		if (world.rand.nextFloat() <= chance) {
			if (mate == null) {
				prod.add(TreeManager.treeRoot.getTree(world, new TreeGenome(genome.getChromosomes())));
			} else {
				prod.add(createOffspring(world, mate, playerProfile, pos));
			}
		}

		return prod;
	}

	private ITree createOffspring(World world, ITreeGenome mate, @Nullable GameProfile playerProfile, BlockPos pos) {
		IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
		IChromosome[] parent1 = genome.getChromosomes();
		IChromosome[] parent2 = mate.getChromosomes();

		// Check for mutation. Replace one of the parents with the mutation
		// template if mutation occured.
		IChromosome[] mutated = mutateSpecies(world, playerProfile, pos, genome, mate);
		if (mutated == null) {
			mutated = mutateSpecies(world, playerProfile, pos, mate, genome);
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

	@Nullable
	private static IChromosome[] mutateSpecies(World world, @Nullable GameProfile playerProfile, BlockPos pos, ITreeGenome genomeOne, ITreeGenome genomeTwo) {
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

			float chance = treeMutation.getChance(world, pos, allele0, allele1, genome0, genome1);
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
	public Map<ItemStack, Float> getProducts() {
		return genome.getFruitProvider().getProducts();
	}

	@Override
	public Map<ItemStack, Float> getSpecialties() {
		return genome.getFruitProvider().getSpecialty();
	}

	@Override
	public NonNullList<ItemStack> produceStacks(World world, BlockPos pos, int ripeningTime) {
		return genome.getFruitProvider().getFruits(genome, world, pos, ripeningTime);
	}
}
