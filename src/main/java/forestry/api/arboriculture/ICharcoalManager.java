/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

/**
 * Provides functions that are related to the forestry charcoal pile.
 */
public interface ICharcoalManager {
	/**
	 * Registers the given block as a valid block for the charcoal pile wall and adds the given charcoal amount to it.
	 * <p>
	 * This method unlike {@link #registerWall(BlockState, int)} ignores the metadata of the actual block and
	 * only compares the world block with the given block.
	 */
	void registerWall(Block block, int amount);

	/**
	 * Registers the given block as a valid block for the charcoal pile wall and adds the given charcoal amount to it.
	 * <p>
	 * This method unlike {@link #registerWall(Block, int)} compares the world state with the given state and not
	 * only the block.
	 */
	void registerWall(BlockState blockState, int amount);

	/**
	 * Registers your implementation of the {@link ICharcoalPileWall} interface.
	 */
	void registerWall(ICharcoalPileWall wall);

	/**
	 * Remove a wall associated with the given block. Not guaranteed to work depending on how {@link ICharcoalPileWall} is implemented.
	 * {@link #removeWall(BlockState)} is preferred.
	 *
	 * @return true if the wall was removed.
	 */
	boolean removeWall(Block block);

	/**
	 * Remove a wall that {@link ICharcoalPileWall#matches(BlockState)} the given blockstate.
	 *
	 * @param state the blockstate to remove.
	 * @return true if the wall was removed.
	 */
	boolean removeWall(BlockState state);

	/**
	 * @return A collection with all registered charcoal pile walls.
	 */
	Collection<ICharcoalPileWall> getWalls();
}
