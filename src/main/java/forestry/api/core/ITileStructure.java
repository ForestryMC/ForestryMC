/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;

/**
 * The basis for multiblock components. 
 */
public interface ITileStructure {

	/**
	 * @return String unique to the type of structure controlled by this structure logic. Should map to {@link IStructureLogic}
	 */
	String getTypeUID();

	/**
	 * Should map to {@link IStructureLogic}
	 */
	void validateStructure();

	/**
	 * Called when the structure resets.
	 */
	void onStructureReset();

	/**
	 * @return TileEntity that is the master in this structure, null if no structure exists.
	 */
	ITileStructure getCentralTE();

	/**
	 * Called to set the master TileEntity. Implementing TileEntity should keep track of the master's coordinates, not refer to the TE object itself.
	 * 
	 * @param tile
	 */
	void setCentralTE(TileEntity tile);

	/**
	 * @return IInventory representing the inventory accessed from this block.
	 */
	@Deprecated
	IInventory getInventory();

	/**
	 * @return ISidedInventory representing the inventory accessed from this block.
	 */
	ISidedInventory getStructureInventory();

	/**
	 * Only called on Forestry's own blocks.
	 */
	void makeMaster();

	/**
	 * @return true if this TE is the master in a structure, false otherwise.
	 */
	boolean isMaster();

	/**
	 * @return true if the TE is master or has a master.
	 */
	boolean isIntegratedIntoStructure();

}
