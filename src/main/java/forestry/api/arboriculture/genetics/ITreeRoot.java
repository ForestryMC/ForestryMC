/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import genetics.api.individual.IGenome;

import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.ISpeciesRootPollinatable;

public interface ITreeRoot extends ISpeciesRootPollinatable<ITree> {

	/**
	 * @return {@link IArboristTracker} associated with the passed world.
	 */
	@Override
	IArboristTracker getBreedingTracker(IWorld world, @Nullable GameProfile player);

	/* TREE SPECIFIC */

	/**
	 * Register a leaf tick handler.
	 *
	 * @param handler the {@link ILeafTickHandler} to register.
	 */
	void registerLeafTickHandler(ILeafTickHandler handler);

	Collection<ILeafTickHandler> getLeafTickHandlers();

	@Nullable
	ITree getTree(World world, BlockPos pos);

	ITree getTree(World world, IGenome genome);

	boolean plantSapling(World world, ITree tree, GameProfile owner, BlockPos pos);

	boolean setFruitBlock(IWorld world, IGenome genome, IAlleleFruit allele, float yield, BlockPos pos);

	/* GAME MODE */
	List<ITreekeepingMode> getTreekeepingModes();

	ITreekeepingMode getTreekeepingMode(IWorld world);

	@Nullable
	ITreekeepingMode getTreekeepingMode(String name);

	void registerTreekeepingMode(ITreekeepingMode mode);

	void setTreekeepingMode(IWorld world, ITreekeepingMode mode);

	Collection<IFruitProvider> getFruitProvidersForFruitFamily(IFruitFamily fruitFamily);
}
