/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.multiblock;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import forestry.api.core.INbtWritable;

/**
 * Multiblock Logic implements the basic logic for IMultiblockComponent tile entities.
 * Instances must come from MultiblockManager.logicFactory, most of the implementation is hidden.
 * <p>
 * IMultiblockComponent tile entities must wire up the methods in the "Updating and Synchronization" section.
 * As a starting point, you can use MultiblockTileEntityBase.
 */
public interface IMultiblockLogic extends INbtWritable {

	/**
	 * @return True if this block is connected to a multiblock controller. False otherwise.
	 */
	boolean isConnected();

	/**
	 * @return the multiblock controller for this logic
	 */
	IMultiblockController getController();

	/* Updating and Synchronization */

	/**
	 * call on Tile.validate()
	 **/
	void validate(Level world, IMultiblockComponent part);

	/**
	 * call on Tile.invalidate()
	 **/
	void invalidate(Level world, IMultiblockComponent part);

	/**
	 * call on Tile.onChunkUnload()
	 **/
	void onChunkUnload(Level world, IMultiblockComponent part);

	/**
	 * Writes data for client synchronization.
	 * Use this in Tile.getDescriptionPacket()
	 */
	void encodeDescriptionPacket(CompoundTag packetData);

	/**
	 * Reads data for client synchronization.
	 * Use this in Tile.onDataPacket()
	 */
	void decodeDescriptionPacket(CompoundTag packetData);

	/**
	 * Read the logic's data from file.
	 * Use this in Tile.read()
	 */
	void readFromNBT(CompoundTag CompoundNBT);

	/**
	 * Write the logic's data to file.
	 * Use this in Tile.write()
	 */
	@Override
	CompoundTag write(CompoundTag CompoundNBT);
}
