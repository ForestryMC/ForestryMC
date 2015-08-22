/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IIndividual;

public interface ITree extends IIndividual {

	void mate(ITree other);

	IEffectData[] doEffect(IEffectData[] storedData, World world, int biomeid, BlockPos pos);

	IEffectData[] doFX(IEffectData[] storedData, World world, int biomeid, BlockPos pos);

	ITreeGenome getGenome();

	ITreeGenome getMate();

	EnumSet<EnumPlantType> getPlantTypes();

	ITree[] getSaplings(World world, BlockPos pos, float modifier);

	ItemStack[] getProduceList();

	ItemStack[] getSpecialtyList();

	ItemStack[] produceStacks(World world, BlockPos pos, int ripeningTime);

	/**
	 * 
	 * @param world
	 * @param pos
	 * @return Boolean indicating whether a sapling can stay planted at the given position.
	 */
	boolean canStay(World world, BlockPos pos);

	/**
	 * 
	 * @param world
	 * @param pos
	 * @return Boolean indicating whether a sapling at the given position can grow into a tree.
	 */
	boolean canGrow(World world, BlockPos pos, int expectedGirth, int expectedHeight);

	/**
	 * @return Integer denoting the maturity (block ticks) required for a sapling to attempt to grow into a tree.
	 */
	int getRequiredMaturity();

	/**
	 * @return Integer denoting how resilient leaf blocks are against adverse influences (i.e. caterpillars).
	 */
	int getResilience();
	
	/**
	 * @param world
	 * @param pos
	 * @return Integer denoting the size of the tree trunk.
	 */
	int getGirth(World world, BlockPos pos);

	
	
	/**
	 * 
	 * @param world
	 * @param pos
	 * @return Growth conditions at the given position.
	 */
	EnumGrowthConditions getGrowthCondition(World world, BlockPos pos);

	WorldGenerator getTreeGenerator(World world, BlockPos pos, boolean wasBonemealed);

	ITree copy();

	boolean isPureBred(EnumTreeChromosome chromosome);

	boolean canBearFruit();
}
