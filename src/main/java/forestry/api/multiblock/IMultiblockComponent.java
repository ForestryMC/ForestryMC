/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import net.minecraft.util.ChunkCoordinates;

import com.mojang.authlib.GameProfile;

/**
 * Basic interface for a multiblock machine component.
 * Implemented by TileEntities.
 */
public interface IMultiblockComponent {
	
	/**
	 * Returns the location of this tile entity in the world.
	 * @return ChunkCoordinates set to the location of this tile entity in the world.
	 */
	ChunkCoordinates getCoordinates();

	/**
	 * @return the gameProfile of the player who owns this single component (not the entire multiblock)
	 */
	GameProfile getOwner();

	/**
	 * @return the multiblock logic for this part
	 */
	IMultiblockLogic getMultiblockLogic();

	/**
	 * Called when a machine is fully assembled from the disassembled state, meaning
	 * it was constructed by a player/entity action, not by chunks loading.
	 * Note that, for non-square machines, the min/max coordinates may not actually be part
	 * of the machine! They form an outer bounding box for the whole machine itself.
	 * @param multiblockController The controller to which this part is being assembled.
	 */
	void onMachineAssembled(IMultiblockController multiblockController, ChunkCoordinates minCoord, ChunkCoordinates maxCoord);

	/**
	 * Called when the machine is broken for game reasons, e.g. a player removed a block
	 * or an explosion occurred.
	 */
	void onMachineBroken();
}
