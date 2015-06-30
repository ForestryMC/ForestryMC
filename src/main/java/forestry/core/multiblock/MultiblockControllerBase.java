package forestry.core.multiblock;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.mojang.authlib.GameProfile;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.core.delegates.AccessHandler;
import forestry.core.interfaces.IAccessHandler;
import forestry.core.interfaces.IOwnable;
import forestry.core.interfaces.IRestrictedAccessTile;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

/**
 * This class contains the base logic for "multiblock controllers". Conceptually, they are
 * meta-TileEntities. They govern the logic for an associated group of TileEntities.
 *
 * Subordinate TileEntities implement the IMultiblockPart class and, generally, should not have an update() loop.
 */
public abstract class MultiblockControllerBase implements ISidedInventory, IRestrictedAccessTile, IStreamableGui, IErrorLogicSource {
	public static final short DIMENSION_UNBOUNDED = -1;

	// Multiblock stuff - do not mess with
	protected World worldObj;
	
	// Disassembled -> Assembled; Assembled -> Disassembled OR Paused; Paused -> Assembled
	protected enum AssemblyState {
		Disassembled, Assembled, Paused
	}

	protected AssemblyState assemblyState;

	protected HashSet<IMultiblockPart> connectedParts;
	
	/** This is a deterministically-picked coordinate that identifies this
	 * multiblock uniquely in its dimension.
	 * Currently, this is the coord with the lowest X, Y and Z coordinates, in that order of evaluation.
	 * i.e. If something has a lower X but higher Y/Z coordinates, it will still be the reference.
	 * If something has the same X but a lower Y coordinate, it will be the reference. Etc.
	 */
	private CoordTriplet referenceCoord;

	/**
	 * Minimum bounding box coordinate. Blocks do not necessarily exist at this coord if your machine
	 * is not a cube/rectangular prism.
	 */
	private CoordTriplet minimumCoord;

	/**
	 * Maximum bounding box coordinate. Blocks do not necessarily exist at this coord if your machine
	 * is not a cube/rectangular prism.
	 */
	private CoordTriplet maximumCoord;
	
	/**
	 * Set to true whenever a part is removed from this controller.
	 */
	private boolean shouldCheckForDisconnections;
	
	/**
	 * Set whenever we validate the multiblock
	 */
	private MultiblockValidationException lastValidationException;

	// Ticks
	private static final Random rand = new Random();
	private int tickCount = rand.nextInt(256);

	private final AccessHandler accessHandler;
	private final IErrorLogic errorLogic;
	
	protected boolean debugMode;
	
	protected MultiblockControllerBase(World world) {
		// Multiblock stuff
		worldObj = world;
		connectedParts = new HashSet<IMultiblockPart>();

		referenceCoord = null;
		assemblyState = AssemblyState.Disassembled;

		minimumCoord = null;
		maximumCoord = null;

		shouldCheckForDisconnections = true;
		lastValidationException = null;

		accessHandler = new AccessHandler(this);
		errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();

		debugMode = false;
	}

	@Override
	public IAccessHandler getAccessHandler() {
		return accessHandler;
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	public void setDebugMode(boolean active) {
		debugMode = active;
	}
	
	public boolean isDebugMode() {
		return debugMode;
	}
	
	/**
	 * Call when a block with cached save-delegate data is added to the multiblock.
	 * The part will be notified that the data has been used after this call completes.
	 * @param part The NBT tag containing this controller's data.
	 */
	public abstract void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data);
	
	/**
	 * Attach a new part to this machine.
	 * @param part The part to add.
	 */
	public void attachBlock(IMultiblockPart part) {
		CoordTriplet coord = part.getWorldLocation();

		if (!connectedParts.add(part)) {
			Proxies.log.warning("[%s] Controller %s is double-adding part %d @ %s. This is unusual. If you encounter odd behavior, please tear down the machine and rebuild it.", (worldObj.isRemote ? "CLIENT" : "SERVER"), hashCode(), part.hashCode(), coord);
		}
		
		part.onAttached(this);
		this.onBlockAdded(part);

		if (part.hasMultiblockSaveData()) {
			NBTTagCompound savedData = part.getMultiblockSaveData();
			onAttachedPartWithMultiblockData(part, savedData);
			part.onMultiblockDataAssimilated();
		}
		
		if (this.referenceCoord == null) {
			referenceCoord = coord;
			part.becomeMultiblockSaveDelegate();
		} else if (coord.compareTo(referenceCoord) < 0) {
			TileEntity te = this.worldObj.getTileEntity(referenceCoord.x, referenceCoord.y, referenceCoord.z);
			((IMultiblockPart) te).forfeitMultiblockSaveDelegate();
			
			referenceCoord = coord;
			part.becomeMultiblockSaveDelegate();
		} else {
			part.forfeitMultiblockSaveDelegate();
		}
		
		if (minimumCoord != null) {
			if (part.xCoord < minimumCoord.x) {
				minimumCoord.x = part.xCoord;
			}
			if (part.yCoord < minimumCoord.y) {
				minimumCoord.y = part.yCoord;
			}
			if (part.zCoord < minimumCoord.z) {
				minimumCoord.z = part.zCoord;
			}
		}
		
		if (maximumCoord != null) {
			if (part.xCoord > maximumCoord.x) {
				maximumCoord.x = part.xCoord;
			}
			if (part.yCoord > maximumCoord.y) {
				maximumCoord.y = part.yCoord;
			}
			if (part.zCoord > maximumCoord.z) {
				maximumCoord.z = part.zCoord;
			}
		}
		
		MultiblockRegistry.addDirtyController(worldObj, this);
	}

	/**
	 * Called when a new part is added to the machine. Good time to register things into lists.
	 * @param newPart The part being added.
	 */
	protected abstract void onBlockAdded(IMultiblockPart newPart);

	/**
	 * Called when a part is removed from the machine. Good time to clean up lists.
	 * @param oldPart The part being removed.
	 */
	protected abstract void onBlockRemoved(IMultiblockPart oldPart);
	
	/**
	 * Called when a machine is assembled from a disassembled state.
	 */
	protected void onMachineAssembled() {
		if (worldObj.isRemote) {
			return;
		}

		// Figure out who owns the multiblock, by majority

		Multiset<GameProfile> owners = HashMultiset.create();
		for (IMultiblockPart part : connectedParts) {
			if (part instanceof IRestrictedAccessTile) {
				IAccessHandler accessHandler = ((IRestrictedAccessTile) part).getAccessHandler();
				GameProfile owner = accessHandler.getOwner();
				if (owner != null) {
					owners.add(owner);
				}
			} else if (part instanceof IOwnable) {
				IOwnable ownable = (IOwnable) part;
				GameProfile owner = ownable.getOwner();
				if (owner != null) {
					owners.add(owner);
				}
			}
		}

		GameProfile owner = null;
		int max = 0;
		for (Multiset.Entry<GameProfile> entry : owners.entrySet()) {
			int count = entry.getCount();
			if (count > max) {
				max = count;
				owner = entry.getElement();
			}
		}

		getAccessHandler().setOwner(owner);
	}
	
	/**
	 * Called when a machine is restored to the assembled state from a paused state.
	 */
	protected void onMachineRestored() {

	}

	/**
	 * Called when a machine is paused from an assembled state
	 * This generally only happens due to chunk-loads and other "system" events.
	 */
	protected void onMachinePaused() {

	}
	
	/**
	 * Called when a machine is disassembled from an assembled state.
	 * This happens due to user or in-game actions (e.g. explosions)
	 */
	protected void onMachineDisassembled() {

	}
	
	/**
	 * Callback whenever a part is removed (or will very shortly be removed) from a controller.
	 * Do housekeeping/callbacks, also nulls min/max coords.
	 * @param part The part being removed.
	 */
	private void onDetachBlock(IMultiblockPart part) {
		// Strip out this part
		part.onDetached(this);
		this.onBlockRemoved(part);
		part.forfeitMultiblockSaveDelegate();

		minimumCoord = maximumCoord = null;
		
		if (referenceCoord != null && referenceCoord.equals(part.xCoord, part.yCoord, part.zCoord)) {
			referenceCoord = null;
		}
		
		shouldCheckForDisconnections = true;
	}
	
	/**
	 * Call to detach a block from this machine. Generally, this should be called
	 * when the tile entity is being released, e.g. on block destruction.
	 * @param part The part to detach from this machine.
	 * @param chunkUnloading Is this entity detaching due to the chunk unloading? If true, the multiblock will be paused instead of broken.
	 */
	public void detachBlock(IMultiblockPart part, boolean chunkUnloading) {
		if (chunkUnloading && this.assemblyState == AssemblyState.Assembled) {
			this.assemblyState = AssemblyState.Paused;
			this.onMachinePaused();
		}

		// Strip out this part
		onDetachBlock(part);
		if (!connectedParts.remove(part)) {
			Proxies.log.warning("[%s] Double-removing part (%d) @ %d, %d, %d, this is unexpected and may cause problems. If you encounter anomalies, please tear down the reactor and rebuild it.", worldObj.isRemote ? "CLIENT" : "SERVER", part.hashCode(), part.xCoord, part.yCoord, part.zCoord);
		}

		if (connectedParts.isEmpty()) {
			// Destroy/unregister
			MultiblockRegistry.addDeadController(this.worldObj, this);
			return;
		}

		MultiblockRegistry.addDirtyController(this.worldObj, this);

		// Find new save delegate if we need to.
		if (referenceCoord == null) {
			selectNewReferenceCoord();
		}
	}

	/**
	 * Helper method so we don't check for a whole machine until we have enough blocks
	 * to actually assemble it. This isn't as simple as xmax*ymax*zmax for non-cubic machines
	 * or for machines with hollow/complex interiors.
	 * @return The minimum number of blocks connected to the machine for it to be assembled.
	 */
	protected abstract int getMinimumNumberOfBlocksForAssembledMachine();

	/**
	 * Returns the maximum X dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
	 * dimension checking in X. (This is not recommended.)
	 * @return The maximum X dimension size of the machine, or -1 
	 */
	protected abstract int getMaximumXSize();

	/**
	 * Returns the maximum Z dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
	 * dimension checking in X. (This is not recommended.)
	 * @return The maximum Z dimension size of the machine, or -1 
	 */
	protected abstract int getMaximumZSize();

	/**
	 * Returns the maximum Y dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
	 * dimension checking in X. (This is not recommended.)
	 * @return The maximum Y dimension size of the machine, or -1 
	 */
	protected abstract int getMaximumYSize();
	
	/**
	 * Returns the minimum X dimension size of the machine. Must be at least 1, because nothing else makes sense.
	 * @return The minimum X dimension size of the machine
	 */
	protected int getMinimumXSize() {
		return 1;
	}

	/**
	 * Returns the minimum Y dimension size of the machine. Must be at least 1, because nothing else makes sense.
	 * @return The minimum Y dimension size of the machine
	 */
	protected int getMinimumYSize() {
		return 1;
	}

	/**
	 * Returns the minimum Z dimension size of the machine. Must be at least 1, because nothing else makes sense.
	 * @return The minimum Z dimension size of the machine
	 */
	protected int getMinimumZSize() {
		return 1;
	}
	
	
	/**
	 * @return An exception representing the last error encountered when trying to assemble this
	 * multiblock, or null if there is no error.
	 */
	public MultiblockValidationException getLastValidationException() {
		return lastValidationException;
	}
	
	/**
	 * Checks if a machine is whole. If not, throws an exception with the reason why.
	 */
	protected abstract void isMachineWhole() throws MultiblockValidationException;
	
	/**
	 * Check if the machine is whole or not.
	 * If the machine was not whole, but now is, assemble the machine.
	 * If the machine was whole, but no longer is, disassemble the machine.
	 * @return
	 */
	public void checkIfMachineIsWhole() {
		AssemblyState oldState = this.assemblyState;
		boolean isWhole;
		lastValidationException = null;
		try {
			isMachineWhole();
			isWhole = true;
		} catch (MultiblockValidationException e) {
			lastValidationException = e;
			isWhole = false;
		}
		
		if (isWhole) {
			// This will alter assembly state
			assembleMachine(oldState);
		} else if (oldState == AssemblyState.Assembled) {
			// This will alter assembly state
			disassembleMachine();
		}
		// Else Paused, do nothing
	}
	
	/**
	 * Called when a machine becomes "whole" and should begin
	 * functioning as a game-logically finished machine.
	 * Calls onMachineAssembled on all attached parts.
	 */
	private void assembleMachine(AssemblyState oldState) {
		for (IMultiblockPart part : connectedParts) {
			part.onMachineAssembled(this);
		}
		
		this.assemblyState = AssemblyState.Assembled;
		if (oldState == AssemblyState.Paused) {
			onMachineRestored();
		} else {
			onMachineAssembled();
		}
	}
	
	/**
	 * Called when the machine needs to be disassembled.
	 * It is not longer "whole" and should not be functional, usually
	 * as a result of a block being removed.
	 * Calls onMachineBroken on all attached parts.
	 */
	private void disassembleMachine() {
		for (IMultiblockPart part : connectedParts) {
			part.onMachineBroken();
		}
		
		this.assemblyState = AssemblyState.Disassembled;
		onMachineDisassembled();
	}
	
	/**
	 * Assimilate another controller into this controller.
	 * Acquire all of the other controller's blocks and attach them
	 * to this one.
	 *
	 * @param other The controller to merge into this one.
	 */
	public void assimilate(MultiblockControllerBase other) {
		CoordTriplet otherReferenceCoord = other.getReferenceCoord();
		if (otherReferenceCoord != null && getReferenceCoord().compareTo(otherReferenceCoord) >= 0) {
			throw new IllegalArgumentException("The controller with the lowest minimum-coord value must consume the one with the higher coords");
		}

		TileEntity te;
		Set<IMultiblockPart> partsToAcquire = new HashSet<IMultiblockPart>(other.connectedParts);

		// releases all blocks and references gently so they can be incorporated into another multiblock
		other._onAssimilated(this);
		
		for (IMultiblockPart acquiredPart : partsToAcquire) {
			// By definition, none of these can be the minimum block.
			if (acquiredPart.isInvalid()) {
				continue;
			}
			
			connectedParts.add(acquiredPart);
			acquiredPart.onAssimilated(this);
			this.onBlockAdded(acquiredPart);
		}

		this.onAssimilate(other);
		other.onAssimilated(this);
	}
	
	/**
	 * Called when this machine is consumed by another controller.
	 * Essentially, forcibly tear down this object.
	 * @param otherController The controller consuming this controller.
	 */
	private void _onAssimilated(MultiblockControllerBase otherController) {
		if (referenceCoord != null) {
			if (worldObj.getChunkProvider().chunkExists(referenceCoord.getChunkX(), referenceCoord.getChunkZ())) {
				TileEntity te = this.worldObj.getTileEntity(referenceCoord.x, referenceCoord.y, referenceCoord.z);
				if (te instanceof IMultiblockPart) {
					((IMultiblockPart) te).forfeitMultiblockSaveDelegate();
				}
			}
			this.referenceCoord = null;
		}

		connectedParts.clear();
	}
	
	/**
	 * Callback. Called after this controller assimilates all the blocks
	 * from another controller.
	 * Use this to absorb that controller's game data.
	 * @param assimilated The controller whose uniqueness was added to our own.
	 */
	protected abstract void onAssimilate(MultiblockControllerBase assimilated);
	
	/**
	 * Callback. Called after this controller is assimilated into another controller.
	 * All blocks have been stripped out of this object and handed over to the
	 * other controller.
	 * This is intended primarily for cleanup.
	 * @param assimilator The controller which has assimilated this controller.
	 */
	protected abstract void onAssimilated(MultiblockControllerBase assimilator);
	
	/**
	 * Driver for the update loop. If the machine is assembled, runs
	 * the game logic update method.
	 */
	public final void updateMultiblockEntity() {
		tickCount++;

		if (connectedParts.isEmpty()) {
			// This shouldn't happen, but just in case...
			MultiblockRegistry.addDeadController(this.worldObj, this);
			return;
		}

		if (this.assemblyState != AssemblyState.Assembled) {
			// Not assembled - don't run game logic
			return;
		}

		if (worldObj.isRemote) {
			updateClient(tickCount);
		} else if (updateServer(tickCount)) {
			// If this returns true, the server has changed its internal data. 
			// If our chunks are loaded (they should be), we must mark our chunks as dirty.
			if (minimumCoord != null && maximumCoord != null &&
					this.worldObj.checkChunksExist(minimumCoord.x, minimumCoord.y, minimumCoord.z,
							maximumCoord.x, maximumCoord.y, maximumCoord.z)) {
				int minChunkX = minimumCoord.x >> 4;
				int minChunkZ = minimumCoord.z >> 4;
				int maxChunkX = maximumCoord.x >> 4;
				int maxChunkZ = maximumCoord.z >> 4;
				
				for (int x = minChunkX; x <= maxChunkX; x++) {
					for (int z = minChunkZ; z <= maxChunkZ; z++) {
						// Ensure that we save our data, even if the our save delegate is in has no TEs.
						Chunk chunkToSave = this.worldObj.getChunkFromChunkCoords(x, z);
						chunkToSave.setChunkModified();
					}
				}
			}
		}
		// Else: Server, but no need to save data.
	}
	
	/**
	 * The server-side update loop! Use this similarly to a TileEntity's update loop.
	 * You do not need to call your superclass' update() if you're directly
	 * derived from MultiblockControllerBase. This is a callback.
	 * Note that this will only be called when the machine is assembled.
	 * @return True if the multiblock should save data, i.e. its internal game state has changed. False otherwise.
	 */
	protected abstract boolean updateServer(int tickCount);
	
	/**
	 * Client-side update loop. Generally, this shouldn't do anything, but if you want
	 * to do some interpolation or something, do it here.
	 */
	protected abstract void updateClient(int tickCount);

	protected final boolean updateOnInterval(int tickInterval) {
		return tickCount % tickInterval == 0;
	}
	
	// Validation helpers

	/**
	 * @param level the level of the block on the multiblock, starting at 0 for the bottom.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 * @throws MultiblockValidationException if the tested block is not allowed on the machine's side faces
	 */
	protected void isBlockGoodForExteriorLevel(int level, World world, int x, int y, int z) throws MultiblockValidationException {
		Block block = world.getBlock(x, y, z);
		throw new MultiblockValidationException(StringUtil.localizeAndFormatRaw("for.multiblock.error.invalid.interior", x, y, z, block.getLocalizedName()));
	}
	
	/**
	 * The interior is any block that does not touch blocks outside the machine.
	 * @param world World object for the world in which this controller is located.
	 * @param x X coordinate of the block being tested
	 * @param y Y coordinate of the block being tested
	 * @param z Z coordinate of the block being tested
	 * @throws MultiblockValidationException if the tested block is not allowed in the machine's interior
	 */
	protected void isBlockGoodForInterior(World world, int x, int y, int z) throws MultiblockValidationException {
		Block block = world.getBlock(x, y, z);
		throw new MultiblockValidationException(StringUtil.localizeAndFormatRaw("for.multiblock.error.invalid.interior", x, y, z, block.getLocalizedName()));
	}
	
	/**
	 * @return The reference coordinate, the block with the lowest x, y, z coordinates, evaluated in that order.
	 */
	public CoordTriplet getReferenceCoord() {
		if (referenceCoord == null) {
			selectNewReferenceCoord();
		}
		return referenceCoord;
	}
	
	/**
	 * @return The number of blocks connected to this controller.
	 */
	public int getNumConnectedBlocks() {
		return connectedParts.size();
	}

	public void writeToNBT(NBTTagCompound data) {
		accessHandler.writeToNBT(data);
	}

	public void readFromNBT(NBTTagCompound data) {
		accessHandler.readFromNBT(data);
	}

	/**
	 * Force this multiblock to recalculate its minimum and maximum coordinates
	 * from the list of connected parts.
	 */
	public void recalculateMinMaxCoords() {
		minimumCoord = new CoordTriplet(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		maximumCoord = new CoordTriplet(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

		for (IMultiblockPart part : connectedParts) {
			if (part.xCoord < minimumCoord.x) {
				minimumCoord.x = part.xCoord;
			}
			if (part.xCoord > maximumCoord.x) {
				maximumCoord.x = part.xCoord;
			}
			if (part.yCoord < minimumCoord.y) {
				minimumCoord.y = part.yCoord;
			}
			if (part.yCoord > maximumCoord.y) {
				maximumCoord.y = part.yCoord;
			}
			if (part.zCoord < minimumCoord.z) {
				minimumCoord.z = part.zCoord;
			}
			if (part.zCoord > maximumCoord.z) {
				maximumCoord.z = part.zCoord;
			}
		}
	}
	
	/**
	 * @return The minimum bounding-box coordinate containing this machine's blocks.
	 */
	public CoordTriplet getMinimumCoord() {
		if (minimumCoord == null) {
			recalculateMinMaxCoords();
		}
		return minimumCoord.copy();
	}

	/**
	 * @return The maximum bounding-box coordinate containing this machine's blocks.
	 */
	public CoordTriplet getMaximumCoord() {
		if (maximumCoord == null) {
			recalculateMinMaxCoords();
		}
		return maximumCoord.copy();
	}

	public final CoordTriplet getCenterCoord() {
		CoordTriplet minCoord = getMinimumCoord();
		CoordTriplet maxCoord = getMaximumCoord();

		return new CoordTriplet(
				(minCoord.x + maxCoord.x) / 2,
				(minCoord.y + maxCoord.y) / 2,
				(minCoord.z + maxCoord.z) / 2
		);
	}

	public final CoordTriplet getTopCenterCoord() {
		CoordTriplet minCoord = getMinimumCoord();
		CoordTriplet maxCoord = getMaximumCoord();

		return new CoordTriplet(
				(minCoord.x + maxCoord.x) / 2,
				maxCoord.y,
				(minCoord.z + maxCoord.z) / 2
		);
	}

	public final boolean isCoordInMultiblock(int x, int y, int z) {
		return (x >= minimumCoord.x && x <= maximumCoord.x) && (y >= minimumCoord.y && y <= maximumCoord.y) && (z >= minimumCoord.z && z <= maximumCoord.z);
	}

	/**
	 * Called when the save delegate's tile entity is being asked for its description packet
	 * @param data A fresh compound tag to write your multiblock data into
	 */
	public abstract void formatDescriptionPacket(NBTTagCompound data);

	/**
	 * Called when the save delegate's tile entity receiving a description packet
	 * @param data A compound tag containing multiblock data to import
	 */
	public abstract void decodeDescriptionPacket(NBTTagCompound data);

	/**
	 * @return True if this controller has no associated blocks, false otherwise
	 */
	public boolean isEmpty() {
		return connectedParts.isEmpty();
	}

	/**
	 * Tests whether this multiblock should consume the other multiblock
	 * and become the new multiblock master when the two multiblocks
	 * are adjacent. Assumes both multiblocks are the same type.
	 * @param otherController The other multiblock controller.
	 * @return True if this multiblock should consume the other, false otherwise.
	 */
	public boolean shouldConsume(MultiblockControllerBase otherController) {
		if (!otherController.getClass().equals(getClass())) {
			throw new IllegalArgumentException("Attempting to merge two multiblocks with different master classes - this should never happen!");
		}
		
		if (otherController == this) {
			return false;
		} // Don't be silly, don't eat yourself.
		
		int res = _shouldConsume(otherController);
		if (res < 0) {
			return true;
		} else if (res > 0) {
			return false;
		} else {
			// Strip dead parts from both and retry
			Proxies.log.warning("[%s] Encountered two controllers with the same reference coordinate. Auditing connected parts and retrying.", worldObj.isRemote ? "CLIENT" : "SERVER");
			auditParts();
			otherController.auditParts();
			
			res = _shouldConsume(otherController);
			if (res < 0) {
				return true;
			} else if (res > 0) {
				return false;
			} else {
				Proxies.log.severe("My Controller (%d): size (%d), parts: %s", hashCode(), connectedParts.size(), getPartsListString());
				Proxies.log.severe("Other Controller (%d): size (%d), coords: %s", otherController.hashCode(), otherController.connectedParts.size(), otherController.getPartsListString());
				throw new IllegalArgumentException("[" + (worldObj.isRemote ? "CLIENT" : "SERVER") + "] Two controllers with the same reference coord that somehow both have valid parts - this should never happen!");
			}

		}
	}
	
	private int _shouldConsume(MultiblockControllerBase otherController) {
		CoordTriplet myCoord = getReferenceCoord();
		CoordTriplet theirCoord = otherController.getReferenceCoord();
		
		// Always consume other controllers if their reference coordinate is null - this means they're empty and can be assimilated on the cheap
		if (theirCoord == null) {
			return -1;
		} else {
			return myCoord.compareTo(theirCoord);
		}
	}
	
	private String getPartsListString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (IMultiblockPart part : connectedParts) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(String.format("(%d: %d, %d, %d)", part.hashCode(), part.xCoord, part.yCoord, part.zCoord));
			first = false;
		}
		
		return sb.toString();
	}
	
	/**
	 * Checks all of the parts in the controller. If any are dead or do not exist in the world, they are removed.
	 */
	private void auditParts() {
		HashSet<IMultiblockPart> deadParts = new HashSet<IMultiblockPart>();
		for (IMultiblockPart part : connectedParts) {
			if (part.isInvalid() || worldObj.getTileEntity(part.xCoord, part.yCoord, part.zCoord) != part) {
				onDetachBlock(part);
				deadParts.add(part);
			}
		}
		
		connectedParts.removeAll(deadParts);
		Proxies.log.warning("[%s] Controller found %d dead parts during an audit, %d parts remain attached", worldObj.isRemote ? "CLIENT" : "SERVER", deadParts.size(), connectedParts.size());
	}

	/**
	 * Called when this machine may need to check for blocks that are no
	 * longer physically connected to the reference coordinate.
	 * @return
	 */
	public Set<IMultiblockPart> checkForDisconnections() {
		if (!this.shouldCheckForDisconnections) {
			return null;
		}
		
		if (this.isEmpty()) {
			MultiblockRegistry.addDeadController(worldObj, this);
			return null;
		}
		
		TileEntity te;
		IChunkProvider chunkProvider = worldObj.getChunkProvider();

		// Invalidate our reference coord, we'll recalculate it shortly
		referenceCoord = null;
		
		// Reset visitations and find the minimum coordinate
		Set<IMultiblockPart> deadParts = new HashSet<IMultiblockPart>();
		CoordTriplet c;
		IMultiblockPart referencePart = null;

		int originalSize = connectedParts.size();

		for (IMultiblockPart part : connectedParts) {
			// This happens during chunk unload.
			if (!chunkProvider.chunkExists(part.xCoord >> 4, part.zCoord >> 4) || part.isInvalid()) {
				deadParts.add(part);
				onDetachBlock(part);
				continue;
			}
			
			if (worldObj.getTileEntity(part.xCoord, part.yCoord, part.zCoord) != part) {
				deadParts.add(part);
				onDetachBlock(part);
				continue;
			}

			part.setUnvisited();
			part.forfeitMultiblockSaveDelegate();
			
			c = part.getWorldLocation();
			if (referenceCoord == null) {
				referenceCoord = c;
				referencePart = part;
			} else if (c.compareTo(referenceCoord) < 0) {
				referenceCoord = c;
				referencePart = part;
			}
		}
		
		connectedParts.removeAll(deadParts);
		deadParts.clear();
		
		if (referencePart == null || isEmpty()) {
			// There are no valid parts remaining. The entire multiblock was unloaded during a chunk unload. Halt.
			shouldCheckForDisconnections = false;
			MultiblockRegistry.addDeadController(worldObj, this);
			return null;
		} else {
			referencePart.becomeMultiblockSaveDelegate();
		}

		// Now visit all connected parts, breadth-first, starting from reference coord's part
		IMultiblockPart part;
		LinkedList<IMultiblockPart> partsToCheck = new LinkedList<IMultiblockPart>();
		IMultiblockPart[] nearbyParts = null;
		int visitedParts = 0;

		partsToCheck.add(referencePart);
		
		while (!partsToCheck.isEmpty()) {
			part = partsToCheck.removeFirst();
			part.setVisited();
			visitedParts++;

			nearbyParts = part.getNeighboringParts(); // Chunk-safe on server, but not on client
			for (IMultiblockPart nearbyPart : nearbyParts) {
				// Ignore different machines
				if (nearbyPart.getMultiblockController() != this) {
					continue;
				}

				if (!nearbyPart.isVisited()) {
					nearbyPart.setVisited();
					partsToCheck.add(nearbyPart);
				}
			}
		}
		
		// Finally, remove all parts that remain disconnected.
		Set<IMultiblockPart> removedParts = new HashSet<IMultiblockPart>();
		for (IMultiblockPart orphanCandidate : connectedParts) {
			if (!orphanCandidate.isVisited()) {
				deadParts.add(orphanCandidate);
				orphanCandidate.onOrphaned(this, originalSize, visitedParts);
				onDetachBlock(orphanCandidate);
				removedParts.add(orphanCandidate);
			}
		}

		// Trim any blocks that were invalid, or were removed.
		connectedParts.removeAll(deadParts);
		
		// Cleanup. Not necessary, really.
		deadParts.clear();
		
		// Juuuust in case.
		if (referenceCoord == null) {
			selectNewReferenceCoord();
		}
		
		// We've run the checks from here on out.
		shouldCheckForDisconnections = false;
		
		return removedParts;
	}

	/**
	 * Detach all parts. Return a set of all parts which still
	 * have a valid tile entity. Chunk-safe.
	 * @return A set of all parts which still have a valid tile entity.
	 */
	public Set<IMultiblockPart> detachAllBlocks() {
		if (worldObj == null) {
			return new HashSet<IMultiblockPart>();
		}
		
		IChunkProvider chunkProvider = worldObj.getChunkProvider();
		for (IMultiblockPart part : connectedParts) {
			if (chunkProvider.chunkExists(part.xCoord >> 4, part.zCoord >> 4)) {
				onDetachBlock(part);
			}
		}

		Set<IMultiblockPart> detachedParts = connectedParts;
		connectedParts = new HashSet<IMultiblockPart>();
		return detachedParts;
	}

	/**
	 * @return True if this multiblock machine is considered assembled and ready to go.
	 */
	public boolean isAssembled() {
		return this.assemblyState == AssemblyState.Assembled;
	}
	
	private void selectNewReferenceCoord() {
		IChunkProvider chunkProvider = worldObj.getChunkProvider();
		TileEntity theChosenOne = null;
		referenceCoord = null;

		for (IMultiblockPart part : connectedParts) {
			if (part.isInvalid() || !chunkProvider.chunkExists(part.xCoord >> 4, part.zCoord >> 4)) {
				// Chunk is unloading, skip this coord to prevent chunk thrashing
				continue;
			}

			if (referenceCoord == null || referenceCoord.compareTo(part.xCoord, part.yCoord, part.zCoord) > 0) {
				referenceCoord = part.getWorldLocation();
				theChosenOne = part;
			}
		}

		if (theChosenOne != null) {
			((IMultiblockPart) theChosenOne).becomeMultiblockSaveDelegate();
		}
	}
	
	/**
	 * Marks the reference coord dirty & updateable.
	 *
	 * On the server, this will mark the for a data-update, so that
	 * nearby clients will receive an updated description packet from the server
	 * after a short time. The block's chunk will also be marked dirty and the
	 * block's chunk will be saved to disk the next time chunks are saved.
	 *
	 * On the client, this will mark the block for a rendering update.
	 */
	protected void markReferenceCoordForUpdate() {
		CoordTriplet rc = getReferenceCoord();
		if (worldObj != null && rc != null) {
			worldObj.markBlockForUpdate(rc.x, rc.y, rc.z);
		}
	}
	
	/**
	 * Marks the reference coord dirty.
	 *
	 * On the server, this marks the reference coord's chunk as dirty; the block (and chunk)
	 * will be saved to disk the next time chunks are saved. This does NOT mark it dirty for
	 * a description-packet update.
	 *
	 * On the client, does nothing.
	 * @see MultiblockControllerBase#markReferenceCoordForUpdate()
	 */
	protected void markReferenceCoordDirty() {
		if (worldObj == null || worldObj.isRemote) {
			return;
		}

		CoordTriplet referenceCoord = getReferenceCoord();
		if (referenceCoord == null) {
			return;
		}

		TileEntity saveTe = worldObj.getTileEntity(referenceCoord.x, referenceCoord.y, referenceCoord.z);
		worldObj.markTileEntityChunkModified(referenceCoord.x, referenceCoord.y, referenceCoord.z, saveTe);
	}

	/* INVENTORY */
	public IInventoryAdapter getInternalInventory() {
		return FakeInventoryAdapter.instance();
	}

	@Override
	public void markDirty() {
		getInternalInventory().markDirty();
	}

	@Override
	public final int getSizeInventory() {
		return getInternalInventory().getSizeInventory();
	}

	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		return getInternalInventory().getStackInSlot(slotIndex);
	}

	@Override
	public final ItemStack decrStackSize(int slotIndex, int amount) {
		return getInternalInventory().decrStackSize(slotIndex, amount);
	}

	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		return getInternalInventory().getStackInSlotOnClosing(slotIndex);
	}

	@Override
	public final void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		getInternalInventory().setInventorySlotContents(slotIndex, itemstack);
	}

	@Override
	public final int getInventoryStackLimit() {
		return getInternalInventory().getInventoryStackLimit();
	}

	@Override
	public final void openInventory() {
		getInternalInventory().openInventory();
	}

	@Override
	public final void closeInventory() {
		getInternalInventory().closeInventory();
	}

	@Override
	public final String getInventoryName() {
		return getInternalInventory().getInventoryName();
	}

	@Override
	public final boolean isUseableByPlayer(EntityPlayer player) {
		return getInternalInventory().isUseableByPlayer(player);
	}

	@Override
	public final boolean hasCustomInventoryName() {
		return getInternalInventory().hasCustomInventoryName();
	}

	@Override
	public final boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().isItemValidForSlot(slotIndex, itemStack);
	}

	@Override
	public final int[] getAccessibleSlotsFromSide(int side) {
		return getInternalInventory().getAccessibleSlotsFromSide(side);
	}

	@Override
	public final boolean canInsertItem(int slotIndex, ItemStack itemStack, int side) {
		return getInternalInventory().canInsertItem(slotIndex, itemStack, side);
	}

	@Override
	public final boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		return getInternalInventory().canExtractItem(slotIndex, itemStack, side);
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		accessHandler.writeData(data);
		errorLogic.writeData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		accessHandler.readData(data);
		errorLogic.readData(data);
	}
}
