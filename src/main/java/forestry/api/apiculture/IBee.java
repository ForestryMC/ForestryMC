/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IErrorState;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IIndividualLiving;

/**
 * Other implementations than Forestry's default one are not supported.
 *
 * @author SirSengir
 */
public interface IBee extends IIndividualLiving {

	/**
	 * @return Bee's genetic information.
	 */
	@Override
	IBeeGenome getGenome();

	/**
	 * @return Genetic information of the bee's mate, null if unmated.
	 */
	@Nullable
	@Override
	IBeeGenome getMate();

	/**
	 * @return true if the individual is originally of natural origin.
	 */
	boolean isNatural();

	/**
	 * @return generation this individual is removed from the original individual.
	 */
	int getGeneration();

	/**
	 * Set the natural flag on this bee.
	 */
	void setIsNatural(boolean flag);

	IEffectData[] doEffect(IEffectData[] storedData, IBeeHousing housing);

	@SideOnly(Side.CLIENT)
	IEffectData[] doFX(IEffectData[] storedData, IBeeHousing housing);

	/**
	 * @return true if the bee may spawn offspring
	 */
	boolean canSpawn();

	/**
	 * Determines whether the queen can work.
	 *
	 * @param housing the {@link IBeeHousing} the bee currently resides in.
	 * @return an empty set if the queen can work, a set of error states if the queen can not work
	 */
	Set<IErrorState> getCanWork(IBeeHousing housing);

	List<Biome> getSuitableBiomes();

	NonNullList<ItemStack> getProduceList();

	NonNullList<ItemStack> getSpecialtyList();

	NonNullList<ItemStack> produceStacks(IBeeHousing housing);

	@Nullable
	IBee spawnPrincess(IBeeHousing housing);

	List<IBee> spawnDrones(IBeeHousing housing);

	/**
	 * @deprecated since Forestry 5.5.4. Use {@link #plantFlowerRandom(IBeeHousing, List)}
	 */
	@Deprecated
	void plantFlowerRandom(IBeeHousing housing);

	/**
	 * Plants a random flower from the given list near the housing
	 *
	 * @param housing          the location the bee is living
	 * @param potentialFlowers the flowers to choose from
	 * @return the position of the planted flower, or null of none were planted
	 * @since Forestry 5.5.4
	 */
	@Nullable
	default BlockPos plantFlowerRandom(IBeeHousing housing, List<IBlockState> potentialFlowers) {
		//noinspection deprecation
		plantFlowerRandom(housing);
		return null;
	}

	@Nullable
	IIndividual retrievePollen(IBeeHousing housing);

	boolean pollinateRandom(IBeeHousing housing, IIndividual pollen);

}
