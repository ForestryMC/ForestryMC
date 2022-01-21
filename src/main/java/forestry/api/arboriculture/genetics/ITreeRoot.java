/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafTickHandler;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.ISpeciesRootPollinatable;

import genetics.api.individual.IGenome;

public interface ITreeRoot extends ISpeciesRootPollinatable<ITree> {

	/**
	 * @return {@link IArboristTracker} associated with the passed world.
	 */
	@Override
	IArboristTracker getBreedingTracker(LevelAccessor world, @Nullable GameProfile player);

	/* TREE SPECIFIC */

	/**
	 * Register a leaf tick handler.
	 *
	 * @param handler the {@link ILeafTickHandler} to register.
	 */
	void registerLeafTickHandler(ILeafTickHandler handler);

	Collection<ILeafTickHandler> getLeafTickHandlers();

	@Nullable
	ITree getTree(Level world, BlockPos pos);

	//TODO: Why is there a world ?
	ITree getTree(Level world, IGenome genome);

	@Nullable
	ITree getTree(BlockEntity tileEntity);

	boolean plantSapling(Level world, ITree tree, GameProfile owner, BlockPos pos);

	boolean setFruitBlock(LevelAccessor world, IGenome genome, IAlleleFruit allele, float yield, BlockPos pos);

	/* GAME MODE */
	List<ITreekeepingMode> getTreekeepingModes();

	ITreekeepingMode getTreekeepingMode(LevelAccessor world);

	@Nullable
	ITreekeepingMode getTreekeepingMode(String name);

	void registerTreekeepingMode(ITreekeepingMode mode);

	void setTreekeepingMode(LevelAccessor world, ITreekeepingMode mode);

	Collection<IFruitProvider> getFruitProvidersForFruitFamily(IFruitFamily fruitFamily);
}
