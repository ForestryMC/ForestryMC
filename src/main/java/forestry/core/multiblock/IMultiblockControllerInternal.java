/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.multiblock;

import javax.annotation.Nonnull;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.api.core.IErrorLogicSource;
import forestry.api.core.INBTTagable;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.access.IRestrictedAccess;
import forestry.core.network.IStreamableGui;
import forestry.core.tiles.IClimatised;

// internal implementation of IMultiblockController
public interface IMultiblockControllerInternal extends IMultiblockController, INBTTagable, IRestrictedAccess, IErrorLogicSource, IClimatised, IStreamableGui {
	/**
	 * Attach a new part to this machine.
	 * @param part The part to add.
	 */
	void attachBlock(IMultiblockComponent part);

	/**
	 * Call to detach a block from this machine. Generally, this should be called
	 * when the tile entity is being released, e.g. on block destruction.
	 * @param part The part to detach from this machine.
	 * @param chunkUnloading Is this entity detaching due to the chunk unloading? If true, the multiblock will be paused instead of broken.
	 */
	void detachBlock(IMultiblockComponent part, boolean chunkUnloading);

	/**
	 * Check if the machine is whole or not.
	 * If the machine was not whole, but now is, assemble the machine.
	 * If the machine was whole, but no longer is, disassemble the machine.
	 */
	void checkIfMachineIsWhole();

	/**
	 * Assimilate another controller into this controller.
	 * Acquire all of the other controller's blocks and attach them
	 * to this one.
	 *
	 * @param other The controller to merge into this one.
	 */
	void assimilate(IMultiblockControllerInternal other);

	/**
	 * Called when this machine is consumed by another controller.
	 * Essentially, forcibly tear down this object.
	 * @param otherController The controller consuming this controller.
	 */
	void _onAssimilated(IMultiblockControllerInternal otherController);

	/**
	 * Callback. Called after this controller is assimilated into another controller.
	 * All blocks have been stripped out of this object and handed over to the
	 * other controller.
	 * This is intended primarily for cleanup.
	 * @param assimilator The controller which has assimilated this controller.
	 */
	void onAssimilated(IMultiblockControllerInternal assimilator);

	/**
	 * Driver for the update loop. If the machine is assembled, runs
	 * the game logic update method.
	 */
	void updateMultiblockEntity();

	/**
	 * @return The reference coordinate, the block with the lowest x, y, z coordinates, evaluated in that order.
	 */
	ChunkCoordinates getReferenceCoord();

	/**
	 * Force this multiblock to recalculate its minimum and maximum coordinates
	 * from the list of connected parts.
	 */
	void recalculateMinMaxCoords();

	/**
	 * Called when the save delegate's tile entity is being asked for its description packet
	 * @param data A fresh compound tag to write your multiblock data into
	 */
	void formatDescriptionPacket(NBTTagCompound data);

	/**
	 * Called when the save delegate's tile entity receiving a description packet
	 * @param data A compound tag containing multiblock data to import
	 */
	void decodeDescriptionPacket(NBTTagCompound data);

	World getWorld();

	/**
	 * @return True if this controller has no associated blocks, false otherwise
	 */
	boolean isEmpty();

	/**
	 * Tests whether this multiblock should consume the other multiblock
	 * and become the new multiblock master when the two multiblocks
	 * are adjacent. Assumes both multiblocks are the same type.
	 * @param otherController The other multiblock controller.
	 * @return True if this multiblock should consume the other, false otherwise.
	 */
	boolean shouldConsume(IMultiblockControllerInternal otherController);

	String getPartsListString();

	/**
	 * Checks all of the parts in the controller. If any are dead or do not exist in the world, they are removed.
	 */
	void auditParts();

	/**
	 * Called when this machine may need to check for blocks that are no
	 * longer physically connected to the reference coordinate.
	 * @return the parts that were disconnected and removed
	 */
	@Nonnull
	Set<IMultiblockComponent> checkForDisconnections();

	/**
	 * Detach all parts. Return a set of all parts which still
	 * have a valid tile entity. Chunk-safe.
	 * @return A set of all parts which still have a valid tile entity.
	 */
	@Nonnull
	Set<IMultiblockComponent> detachAllBlocks();
}
