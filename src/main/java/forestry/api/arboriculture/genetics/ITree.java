/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.ITreeGenData;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.products.IProductList;

import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;

public interface ITree extends IIndividual, ITreeGenData {

	@Override
	ITreeRoot getRoot();

	boolean matchesTemplateGenome();

	IEffectData[] doEffect(IEffectData[] storedData, Level world, BlockPos pos);

	@OnlyIn(Dist.CLIENT)
	IEffectData[] doFX(IEffectData[] storedData, Level world, BlockPos pos);

	/**
	 * @since Forestry 4.0
	 */
	List<ITree> getSaplings(Level world, @Nullable GameProfile playerProfile, BlockPos pos, float modifier);

	// Products, Chance
	IProductList getProducts();

	// Specialties, Chance
	IProductList getSpecialties();

	NonNullList<ItemStack> produceStacks(Level world, BlockPos pos, int ripeningTime);

	/**
	 * @return Boolean indicating whether a sapling can stay planted at the given position.
	 */
	boolean canStay(BlockGetter world, BlockPos pos);

	/**
	 * @return Position that this tree can grow. May be different from pos if there are multiple saplings.
	 * Returns null if a sapling at the given position can not grow into a tree.
	 */
	@Override
	@Nullable
	BlockPos canGrow(LevelAccessor world, BlockPos pos, int expectedGirth, int expectedHeight);

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

	Feature<NoneFeatureConfiguration> getTreeGenerator(WorldGenLevel world, BlockPos pos, boolean wasBonemealed);

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
