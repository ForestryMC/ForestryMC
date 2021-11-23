/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IIndividual;
import forestry.api.world.ITreeGenData;

public interface ITree extends IIndividual, ITreeGenData {

	void mate(ITree other);

	IEffectData[] doEffect(IEffectData[] storedData, World world, int x, int y, int z);

	IEffectData[] doFX(IEffectData[] storedData, World world, int x, int y, int z);

	ITreeGenome getGenome();

	ITreeGenome getMate();

	EnumSet<EnumPlantType> getPlantTypes();

	/**
	 * @since Forestry 4.0
	 */
	ITree[] getSaplings(World world, GameProfile playerProfile, int x, int y, int z, float modifier);

	ItemStack[] getProduceList();

	ItemStack[] getSpecialtyList();

	ItemStack[] produceStacks(World world, int x, int y, int z, int ripeningTime);

	/**
	 * @return Boolean indicating whether a sapling can stay planted at the given position.
	 */
	boolean canStay(World world, int x, int y, int z);

	/**
	 * @return Boolean indicating whether a sapling at the given position can grow into a tree.
	 */
	boolean canGrow(World world, int x, int y, int z, int expectedGirth, int expectedHeight);

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
	int getGirth(World world, int x, int y, int z);

	/**
	 * @return Growth conditions at the given position.
	 */
	EnumGrowthConditions getGrowthCondition(World world, int x, int y, int z);

	WorldGenerator getTreeGenerator(World world, int x, int y, int z, boolean wasBonemealed);

	ITree copy();

	boolean isPureBred(EnumTreeChromosome chromosome);

	boolean canBearFruit();
}
