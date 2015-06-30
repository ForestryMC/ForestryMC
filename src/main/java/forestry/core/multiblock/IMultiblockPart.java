package forestry.core.multiblock;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Basic interface for a multiblock machine part. This is defined as an abstract class
 * as we need the basic functionality of a TileEntity as well.
 * Preferably, you should derive from MultiblockTileEntityBase,
 * which does all the hard work for you.
 *
 * {@link forestry.core.multiblock.MultiblockTileEntityBase}
 */
public abstract class IMultiblockPart extends TileEntity {
	public static final int INVALID_DISTANCE = Integer.MAX_VALUE;
	
	/**
	 * @return True if this block is connected to a multiblock controller. False otherwise.
	 */
	public abstract boolean isConnected();
	
	/**
	 * @return The attached multiblock controller for this tile entity. 
	 */
	public abstract MultiblockControllerBase getMultiblockController();
	
	/**
	 * Returns the location of this tile entity in the world, in CoordTriplet form.
	 * @return A CoordTriplet with its x,y,z members set to the location of this tile entity in the world.
	 */
	public abstract CoordTriplet getWorldLocation();
	
	// Multiblock connection-logic callbacks
	
	/**
	 * Called after this block has been attached to a new multiblock controller.
	 * @param newController The new multiblock controller to which this tile entity is attached.
	 */
	public abstract void onAttached(MultiblockControllerBase newController);

	/**
	 * Called after this block has been detached from a multiblock controller.
	 * @param multiblockController The multiblock controller that no longer controls this tile entity.
	 */
	public abstract void onDetached(MultiblockControllerBase multiblockController);

	/**
	 * Called when this block is being orphaned. Use this to copy game-data values that
	 * should persist despite a machine being broken.
	 * This should NOT mark the part as disconnected. onDetached will be called immediately afterwards.
	 * @see #onDetached(MultiblockControllerBase)
	 * @param oldController The controller which is orphaning this block.
	 * @param oldControllerSize The number of connected blocks in the controller prior to shedding orphans.
	 * @param newControllerSize The number of connected blocks in the controller after shedding orphans.
	 */
	public abstract void onOrphaned(MultiblockControllerBase oldController, int oldControllerSize, int newControllerSize);

	// Multiblock fuse/split helper methods. Here there be dragons.

	/**
	 * Factory method. Creates a new multiblock controller and returns it.
	 * Does not attach this tile entity to it.
	 * Override this in your game code!
	 * @return A new Multiblock Controller, derived from MultiblockControllerBase.
	 */
	public abstract MultiblockControllerBase createNewMultiblock();

	/**
	 * Retrieve the type of multiblock controller which governs this part.
	 * Used to ensure that incompatible multiblocks are not merged.
	 * @return The class/type of the multiblock controller which governs this type of part.
	 */
	public abstract Class<? extends MultiblockControllerBase> getMultiblockControllerType();

	/**
	 * Called when this block is moved from its current controller into a new controller.
	 * A special case of attach/detach, done here for efficiency to avoid triggering
	 * lots of recalculation logic.
	 * @param newController The new controller into which this tile entity is being merged.
	 */
	public abstract void onAssimilated(MultiblockControllerBase newController);

	// Multiblock connection data access.
	// You generally shouldn't toy with these!
	// They're for use by Multiblock Controllers.

	/**
	 * Set that this block has been visited by your validation algorithms.
	 */
	public abstract void setVisited();

	/**
	 * Set that this block has not been visited by your validation algorithms;
	 */
	public abstract void setUnvisited();

	/**
	 * @return True if this block has been visited by your validation algorithms since the last reset.
	 */
	public abstract boolean isVisited();

	/**
	 * Called when this block becomes the designated block for saving data and
	 * transmitting data across the wire.
	 */
	public abstract void becomeMultiblockSaveDelegate();

	/**
	 * Called when this block is no longer the designated block for saving data
	 * and transmitting data across the wire.
	 */
	public abstract void forfeitMultiblockSaveDelegate();

	/**
	 * Is this block the designated save/load & network delegate?
	 */
	public abstract boolean isMultiblockSaveDelegate();

	/**
	 * Returns an array containing references to neighboring IMultiblockPart tile entities.
	 * Primarily a utility method. Only works after tileentity construction, so it cannot be used in
	 * MultiblockControllerBase::attachBlock.
	 *
	 * This method is chunk-safe on the server; it will not query for parts in chunks that are unloaded.
	 * Note that no method is chunk-safe on the client, because ChunkProviderClient is stupid.
	 * @return An array of references to neighboring IMultiblockPart tile entities.
	 */
	public abstract IMultiblockPart[] getNeighboringParts();

	// Multiblock business-logic callbacks - implement these!

	/**
	 * Called when a machine is fully assembled from the disassembled state, meaning
	 * it was broken by a player/entity action, not by chunk unloads.
	 * Note that, for non-square machines, the min/max coordinates may not actually be part
	 * of the machine! They form an outer bounding box for the whole machine itself.
	 * @param multiblockControllerBase The controller to which this part is being assembled.
	 */
	public abstract void onMachineAssembled(MultiblockControllerBase multiblockControllerBase);

	/**
	 * Called when the machine is broken for game reasons, e.g. a player removed a block
	 * or an explosion occurred.
	 */
	public abstract void onMachineBroken();

	/**
	 * Called when the user activates the machine. This is not called by default, but is included
	 * as most machines have this game-logical concept.
	 */
	public abstract void onMachineActivated();

	/**
	 * Called when the user deactivates the machine. This is not called by default, but is included
	 * as most machines have this game-logical concept.
	 */
	public abstract void onMachineDeactivated();

	// Block events

	/**
	 * Called when this part should check its neighbors.
	 * This method MUST NOT cause additional chunks to load.
	 * ALWAYS check to see if a chunk is loaded before querying for its tile entity
	 * This part should inform the controller that it is attaching at this time.
	 * @return A Set of multiblock controllers to which this object would like to attach. It should have attached to one of the controllers in this list. Return null if there are no compatible controllers nearby.
	 */
	public abstract Set<MultiblockControllerBase> attachToNeighbors();

	/**
	 * Assert that this part is detached. If not, log a warning and set the part's controller to null.
	 * Do NOT fire the full disconnection logic.
	 */
	public abstract void assertDetached();

	/**
	 * @return True if a part has multiblock game-data saved inside it.
	 */
	public abstract boolean hasMultiblockSaveData();

	/**
	 * @return The part's saved multiblock game-data in NBT format, or null if there isn't any.
	 */
	public abstract NBTTagCompound getMultiblockSaveData();

	/**
	 * Called after a block is added and the controller has incorporated the part's saved
	 * multiblock game-data into itself. Generally, you should clear the saved data here.
	 */
	public abstract void onMultiblockDataAssimilated();
}
