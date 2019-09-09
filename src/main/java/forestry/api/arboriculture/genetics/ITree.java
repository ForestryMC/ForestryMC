/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;

import forestry.api.genetics.IEffectData;
import forestry.api.world.ITreeGenData;

public interface ITree extends IIndividual, ITreeGenData {

	@Override
	ITreeRoot getRoot();

	boolean matchesTemplateGenome();

	IEffectData[] doEffect(IEffectData[] storedData, World world, BlockPos pos);

	@OnlyIn(Dist.CLIENT)
	IEffectData[] doFX(IEffectData[] storedData, World world, BlockPos pos);

	/**
	 * @since Forestry 4.0
	 */
	List<ITree> getSaplings(World world, @Nullable GameProfile playerProfile, BlockPos pos, float modifier);

	// Products, Chance
	Map<ItemStack, Float> getProducts();

	// Specialties, Chance
	Map<ItemStack, Float> getSpecialties();

	NonNullList<ItemStack> produceStacks(World world, BlockPos pos, int ripeningTime);

	/**
	 * @return Boolean indicating whether a sapling can stay planted at the given position.
	 */
	boolean canStay(IBlockReader world, BlockPos pos);

	/**
	 * @return Position that this tree can grow. May be different from pos if there are multiple saplings.
	 * Returns null if a sapling at the given position can not grow into a tree.
	 */
	@Override
	@Nullable
	BlockPos canGrow(IWorld world, BlockPos pos, int expectedGirth, int expectedHeight);

	/**
	 * @return Integer denoting the maturity (block ticks) required for a sapling to attempt to grow into a tree.
	 */
	int getRequiredMaturity();

	/**
	 * @return Integer denoting how resilient leaf blocks are against adverse influences (i.e. caterpillars).
	 */
	int getResilience();

	/**
	 * @return Integer denoting the size of the tree trunk.
	 */
	@Override
	int getGirth();

	Feature<NoFeatureConfig> getTreeGenerator(World world, BlockPos pos, boolean wasBonemealed);

	@Override
	ITree copy();

	boolean isPureBred(IChromosomeType chromosome);

	boolean canBearFruit();

	default boolean hasEffect() {
		return getGenome().getActiveAllele(TreeChromosomes.SPECIES).hasEffect();
	}

	default boolean isSecret() {
		return getGenome().getActiveAllele(TreeChromosomes.SPECIES).isSecret();
	}
}
