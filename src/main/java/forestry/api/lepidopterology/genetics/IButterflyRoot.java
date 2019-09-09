/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology.genetics;

import javax.annotation.Nullable;

import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.lepidopterology.ILepidopteristTracker;

public interface IButterflyRoot extends IForestrySpeciesRoot<IButterfly> {

	/* BUTTERFLY SPECIFIC */
	@Override
	ILepidopteristTracker getBreedingTracker(IWorld world, @Nullable GameProfile player);

	/**
	 * Spawns the given butterfly in the world.
	 *
	 * @return butterfly entity on success, null otherwise.
	 */
	MobEntity spawnButterflyInWorld(World world, IButterfly butterfly, double x, double y, double z);

	BlockPos plantCocoon(IWorld world, BlockPos pos, IButterfly caterpillar, GameProfile owner, int age, boolean createNursery);

	/**
	 * @return true if passed item is mated.
	 */
	boolean isMated(ItemStack stack);

}
