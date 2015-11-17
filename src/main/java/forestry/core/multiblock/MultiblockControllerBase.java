package forestry.core.multiblock;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.utils.Log;
import forestry.core.utils.StringUtil;

/**
 * This class contains the base logic for "multiblock controllers". Conceptually, they are
 * meta-TileEntities. They govern the logic for an associated group of TileEntities.
 *
 * Subordinate TileEntities implement the IMultiblockComponent class and, generally, should not have an update() loop.
 */
public abstract class MultiblockControllerBase implements IMultiblockControllerInternal {
	// Multiblock stuff - do not mess with
	protected World worldObj;

	// Ticks
	private static final Random rand = new Random();
	private int tickCount = rand.nextInt(256);
	
	// Disassembled -> Assembled; Assembled -> Disassembled OR Paused; Paused -> Assembled
	protected enum AssemblyState {
		Disassembled, Assembled, Paused
	}

	protected AssemblyState assemblyState;

	protected HashSet<IMultiblockComponent> connectedParts;
	
	/** This is a deterministically-picked coordinate that identifies this
	 * multiblock uniquely in its dimension.
	 * Currently, this is the coord with the lowest X, Y and Z coordinates, in that order of evaluation.
	 * i.e. If something has a lower X but higher Y/Z coordinates, it will still be the reference.
	 * If something has the same X but a lower Y coordinate, it will be the reference. Etc.
	 */
	private ChunkCoordinates referenceCoord;

	/**
	 * Minimum bounding box coordinate. Blocks do not necessarily exist at this coord if your machine
	 * is not a cube/rectangular prism.
	 */
	private ChunkCoordinates minimumCoord;

	/**
	 * Maximum bounding box coordinate. Blocks do not necessarily exist at this coord if your machine
	 * is not a cube/rectangular prism.
	 */
	private ChunkCoordinates maximumCoord;
	
	/**
	 * Set to true whenever a part is removed from this controller.
	 */
	private boolean shouldCheckForDisconnections;
	
	/**
	 * Set whenever we validate the multiblock
	 */
	private MultiblockValidationException lastValidationException;

	protected MultiblockControllerBase(World world) {
		this.worldObj = world;
		this.connectedParts = new HashSet<>();

		this.referenceCoord = null;
		this.assemblyState = AssemblyState.Disassembled;

		this.minimumCoord = null;
		this.maximumCoord = null;

		this.shouldCheckForDisconnections = true;
		this.lastValidationException = null;
	}

	@Override
	@Nonnull
	public Collection<IMultiblockComponent> getComponents() {
		return Collections.unmodifiableCollection(connectedParts);
	}
	
	/**
	 * Call when a block with cached save-delegate data is added to the multiblock.
	 * The part will be notified that the data has been used after this call completes.
	 * @param part The NBT tag containing this controller's data.
	 */
	protected abstract void onAttachedPartWithMultiblockData(IMultiblockComponent part, NBTTagCompound data);

	@Override
	public void attachBlock(IMultiblockComponent part) {
		ChunkCoordinates coord = part.getCoordinates();

		if (!connectedParts.add(part)) {
			Log.warning("[%s] Controller %s is double-adding part %d @ %s. This is unusual. " +
							"If you encounter odd behavior, please tear down the machine and rebuild it.",
					(worldObj.isRemote ? "CLIENT" : "SERVER"), hashCode(), part.hashCode(), coord);
		}

		MultiblockLogic logic = (MultiblockLogic) part.getMultiblockLogic();
		
		logic.setController(this);
		this.onBlockAdded(part);

		if (logic.hasMultiblockSaveData()) {
			NBTTagCompound savedData = logic.getMultiblockSaveData();
			onAttachedPartWithMultiblockData(part, savedData);
			logic.onMultiblockDataAssimilated();
		}
		
		if (this.referenceCoord == null) {
			referenceCoord = coord;
			logic.becomeMultiblockSaveDelegate();
		} else if (coord.compareTo(referenceCoord) < 0) {
			TileEntity te = this.worldObj.getTileEntity(referenceCoord.posX, referenceCoord.posY, referenceCoord.posZ);
			if (te instanceof IMultiblockComponent) {
				IMultiblockComponent tePart = (IMultiblockComponent) te;
				MultiblockLogic teLogic = (MultiblockLogic) tePart.getMultiblockLogic();
				teLogic.forfeitMultiblockSaveDelegate();
			}
			
			referenceCoord = coord;
			logic.becomeMultiblockSaveDelegate();
		} else {
			logic.forfeitMultiblockSaveDelegate();
		}

		if (minimumCoord != null) {
			if (coord.posX < minimumCoord.posX) {
				minimumCoord.posX = coord.posX;
			}
			if (coord.posY < minimumCoord.posY) {
				minimumCoord.posY = coord.posY;
			}
			if (coord.posZ < minimumCoord.posZ) {
				minimumCoord.posZ = coord.posZ;
			}
		}
		
		if (maximumCoord != null) {
			if (coord.posX > maximumCoord.posX) {
				maximumCoord.posX = coord.posX;
			}
			if (coord.posY > maximumCoord.posY) {
				maximumCoord.posY = coord.posY;
			}
			if (coord.posZ > maximumCoord.posZ) {
				maximumCoord.posZ = coord.posZ;
			}
		}
		
		MultiblockRegistry.addDirtyController(worldObj, this);
	}

	/**
	 * Called when a new part is added to the machine. Good time to register things into lists.
	 * @param newPart The part being added.
	 */
	protected abstract void onBlockAdded(IMultiblockComponent newPart);

	/**
	 * Called when a part is removed from the machine. Good time to clean up lists.
	 * @param oldPart The part being removed.
	 */
	protected abstract void onBlockRemoved(IMultiblockComponent oldPart);
	
	/**
	 * Called when a machine is assembled from a disassembled state.
	 */
	protected void onMachineAssembled() {

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
	private void onDetachBlock(IMultiblockComponent part) {
		// Strip out this part
		MultiblockLogic logic = (MultiblockLogic) part.getMultiblockLogic();
		logic.setController(null);
		this.onBlockRemoved(part);
		logic.forfeitMultiblockSaveDelegate();

		minimumCoord = maximumCoord = null;
		
		if (referenceCoord != null && referenceCoord.equals(part.getCoordinates())) {
			referenceCoord = null;
		}
		
		shouldCheckForDisconnections = true;
	}

	@Override
	public void detachBlock(IMultiblockComponent part, boolean chunkUnloading) {
		if (chunkUnloading && this.assemblyState == AssemblyState.Assembled) {
			this.assemblyState = AssemblyState.Paused;
			this.onMachinePaused();
		}

		// Strip out this part
		onDetachBlock(part);
		if (!connectedParts.remove(part)) {
			ChunkCoordinates partCoords = part.getCoordinates();
			Log.warning("[%s] Double-removing part (%d) @ %d, %d, %d, this is unexpected and may cause problems. " +
							"If you encounter anomalies, please tear down the reactor and rebuild it.",
					worldObj.isRemote ? "CLIENT" : "SERVER", part.hashCode(), partCoords.posX, partCoords.posY, partCoords.posZ);
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

	@Override
	public String getLastValidationError() {
		if (lastValidationException == null) {
			return null;
		}
		return lastValidationException.getMessage();
	}

	@Override
	public void reassemble() {
		MultiblockRegistry.addDirtyController(worldObj, this);
	}

	/**
	 * Checks if a machine is whole. If not, throws an exception with the reason why.
	 */
	protected abstract void isMachineWhole() throws MultiblockValidationException;

	@Override
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
		this.assemblyState = AssemblyState.Assembled;

		for (IMultiblockComponent part : connectedParts) {
			part.onMachineAssembled(this, getMinimumCoord(), getMaximumCoord());
		}

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
		this.assemblyState = AssemblyState.Disassembled;

		for (IMultiblockComponent part : connectedParts) {
			part.onMachineBroken();
		}

		onMachineDisassembled();
	}
	
	@Override
	public void assimilate(IMultiblockControllerInternal other) {
		ChunkCoordinates otherReferenceCoord = other.getReferenceCoord();
		if (otherReferenceCoord != null && getReferenceCoord().compareTo(otherReferenceCoord) >= 0) {
			throw new IllegalArgumentException("The controller with the lowest minimum-coord value must consume the one with the higher coords");
		}

		Set<IMultiblockComponent> partsToAcquire = new HashSet<>(other.getComponents());

		// releases all blocks and references gently so they can be incorporated into another multiblock
		other._onAssimilated(this);
		
		for (IMultiblockComponent acquiredPart : partsToAcquire) {
			// By definition, none of these can be the minimum block.
			if (isInvalid(acquiredPart)) {
				continue;
			}
			
			connectedParts.add(acquiredPart);
			MultiblockLogic logic = (MultiblockLogic) acquiredPart.getMultiblockLogic();
			logic.setController(this);
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
	@Override
	public void _onAssimilated(IMultiblockControllerInternal otherController) {
		if (referenceCoord != null) {
			if (worldObj.getChunkProvider().chunkExists(referenceCoord.posX >> 4, referenceCoord.posZ >> 4)) {
				TileEntity te = this.worldObj.getTileEntity(referenceCoord.posX, referenceCoord.posY, referenceCoord.posZ);
				if (te instanceof IMultiblockComponent) {
					IMultiblockComponent part = (IMultiblockComponent) te;
					MultiblockLogic logic = (MultiblockLogic) part.getMultiblockLogic();
					logic.forfeitMultiblockSaveDelegate();
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
	protected abstract void onAssimilate(IMultiblockControllerInternal assimilated);
	
	/**
	 * Callback. Called after this controller is assimilated into another controller.
	 * All blocks have been stripped out of this object and handed over to the
	 * other controller.
	 * This is intended primarily for cleanup.
	 * @param assimilator The controller which has assimilated this controller.
	 */
	@Override
	public abstract void onAssimilated(IMultiblockControllerInternal assimilator);
	
	@Override
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
					this.worldObj.checkChunksExist(minimumCoord.posX, minimumCoord.posY, minimumCoord.posZ,
							maximumCoord.posX, maximumCoord.posY, maximumCoord.posZ)) {
				int minChunkX = minimumCoord.posX >> 4;
				int minChunkZ = minimumCoord.posZ >> 4;
				int maxChunkX = maximumCoord.posX >> 4;
				int maxChunkZ = maximumCoord.posZ >> 4;
				
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
	
	@Override
	public ChunkCoordinates getReferenceCoord() {
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

	@Override
	public void recalculateMinMaxCoords() {
		minimumCoord = new ChunkCoordinates(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		maximumCoord = new ChunkCoordinates(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

		for (IMultiblockComponent part : connectedParts) {
			ChunkCoordinates partCoords = part.getCoordinates();
			if (partCoords.posX < minimumCoord.posX) {
				minimumCoord.posX = partCoords.posX;
			}
			if (partCoords.posX > maximumCoord.posX) {
				maximumCoord.posX = partCoords.posX;
			}
			if (partCoords.posY < minimumCoord.posY) {
				minimumCoord.posY = partCoords.posY;
			}
			if (partCoords.posY > maximumCoord.posY) {
				maximumCoord.posY = partCoords.posY;
			}
			if (partCoords.posZ < minimumCoord.posZ) {
				minimumCoord.posZ = partCoords.posZ;
			}
			if (partCoords.posZ > maximumCoord.posZ) {
				maximumCoord.posZ = partCoords.posZ;
			}
		}
	}
	
	/**
	 * @return The minimum bounding-box coordinate containing this machine's blocks.
	 */
	protected ChunkCoordinates getMinimumCoord() {
		if (minimumCoord == null) {
			recalculateMinMaxCoords();
		}
		return new ChunkCoordinates(minimumCoord);
	}

	/**
	 * @return The maximum bounding-box coordinate containing this machine's blocks.
	 */
	protected ChunkCoordinates getMaximumCoord() {
		if (maximumCoord == null) {
			recalculateMinMaxCoords();
		}
		return new ChunkCoordinates(maximumCoord);
	}

	protected final ChunkCoordinates getCenterCoord() {
		ChunkCoordinates minCoord = getMinimumCoord();
		ChunkCoordinates maxCoord = getMaximumCoord();

		return new ChunkCoordinates(
				(minCoord.posX + maxCoord.posX) / 2,
				(minCoord.posY + maxCoord.posY) / 2,
				(minCoord.posZ + maxCoord.posZ) / 2
		);
	}

	protected final ChunkCoordinates getTopCenterCoord() {
		ChunkCoordinates minCoord = getMinimumCoord();
		ChunkCoordinates maxCoord = getMaximumCoord();

		return new ChunkCoordinates(
				(minCoord.posX + maxCoord.posX) / 2,
				maxCoord.posY,
				(minCoord.posZ + maxCoord.posZ) / 2
		);
	}

	protected final boolean isCoordInMultiblock(int x, int y, int z) {
		return (x >= minimumCoord.posX && x <= maximumCoord.posX) && (y >= minimumCoord.posY && y <= maximumCoord.posY) && (z >= minimumCoord.posZ && z <= maximumCoord.posZ);
	}

	@Override
	public boolean isEmpty() {
		return connectedParts.isEmpty();
	}

	@Override
	public boolean shouldConsume(IMultiblockControllerInternal otherController) {
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
			Log.warning("[%s] Encountered two controllers with the same reference coordinate. Auditing connected parts and retrying.", worldObj.isRemote ? "CLIENT" : "SERVER");
			auditParts();
			otherController.auditParts();
			
			res = _shouldConsume(otherController);
			if (res < 0) {
				return true;
			} else if (res > 0) {
				return false;
			} else {
				Log.severe("My Controller (%d): size (%d), parts: %s", hashCode(), connectedParts.size(), getPartsListString());
				Log.severe("Other Controller (%d): size (%d), coords: %s", otherController.hashCode(), otherController.getComponents().size(), otherController.getPartsListString());
				throw new IllegalArgumentException("[" + (worldObj.isRemote ? "CLIENT" : "SERVER") + "] " +
						"Two controllers with the same reference coord that somehow both have valid parts - this should never happen!");
			}

		}
	}
	
	private int _shouldConsume(IMultiblockControllerInternal otherController) {
		ChunkCoordinates myCoord = getReferenceCoord();
		ChunkCoordinates theirCoord = otherController.getReferenceCoord();
		
		// Always consume other controllers if their reference coordinate is null - this means they're empty and can be assimilated on the cheap
		if (theirCoord == null) {
			return -1;
		} else {
			return myCoord.compareTo(theirCoord);
		}
	}

	@Override
	public String getPartsListString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (IMultiblockComponent part : connectedParts) {
			if (!first) {
				sb.append(", ");
			}
			ChunkCoordinates partCoord = part.getCoordinates();
			sb.append(String.format("(%d: %d, %d, %d)", part.hashCode(), partCoord.posX, partCoord.posY, partCoord.posZ));
			first = false;
		}
		
		return sb.toString();
	}
	
	@Override
	public void auditParts() {
		HashSet<IMultiblockComponent> deadParts = new HashSet<>();
		for (IMultiblockComponent part : connectedParts) {
			ChunkCoordinates partCoord = part.getCoordinates();
			if (isInvalid(part) || worldObj.getTileEntity(partCoord.posX, partCoord.posY, partCoord.posZ) != part) {
				onDetachBlock(part);
				deadParts.add(part);
			}
		}
		
		connectedParts.removeAll(deadParts);
		Log.warning("[%s] Controller found %d dead parts during an audit, %d parts remain attached", worldObj.isRemote ? "CLIENT" : "SERVER", deadParts.size(), connectedParts.size());
	}

	@Override
	@Nonnull
	public Set<IMultiblockComponent> checkForDisconnections() {
		if (!this.shouldCheckForDisconnections) {
			return Collections.emptySet();
		}
		
		if (this.isEmpty()) {
			MultiblockRegistry.addDeadController(worldObj, this);
			return Collections.emptySet();
		}
		
		IChunkProvider chunkProvider = worldObj.getChunkProvider();

		// Invalidate our reference coord, we'll recalculate it shortly
		referenceCoord = null;
		
		// Reset visitations and find the minimum coordinate
		Set<IMultiblockComponent> deadParts = new HashSet<>();
		ChunkCoordinates c;
		IMultiblockComponent referencePart = null;

		int originalSize = connectedParts.size();

		for (IMultiblockComponent part : connectedParts) {
			// This happens during chunk unload.
			ChunkCoordinates partCoord = part.getCoordinates();
			if (!chunkProvider.chunkExists(partCoord.posX >> 4, partCoord.posZ >> 4) || isInvalid(part)) {
				deadParts.add(part);
				onDetachBlock(part);
				continue;
			}
			
			if (worldObj.getTileEntity(partCoord.posX, partCoord.posY, partCoord.posZ) != part) {
				deadParts.add(part);
				onDetachBlock(part);
				continue;
			}

			MultiblockLogic logic = (MultiblockLogic) part.getMultiblockLogic();

			logic.setUnvisited();
			logic.forfeitMultiblockSaveDelegate();
			
			c = part.getCoordinates();
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
			return Collections.emptySet();
		} else {
			MultiblockLogic logic = (MultiblockLogic) referencePart.getMultiblockLogic();
			logic.becomeMultiblockSaveDelegate();
		}

		// Now visit all connected parts, breadth-first, starting from reference coord's part
		IMultiblockComponent part;
		LinkedList<IMultiblockComponent> partsToCheck = new LinkedList<>();

		partsToCheck.add(referencePart);
		
		while (!partsToCheck.isEmpty()) {
			part = partsToCheck.removeFirst();
			MultiblockLogic partLogic = (MultiblockLogic) part.getMultiblockLogic();
			partLogic.setVisited();

			List<IMultiblockComponent> nearbyParts = MultiblockUtil.getNeighboringParts(worldObj, part); // Chunk-safe on server, but not on client
			for (IMultiblockComponent nearbyPart : nearbyParts) {
				// Ignore different machines
				MultiblockLogic nearbyPartLogic = (MultiblockLogic) nearbyPart.getMultiblockLogic();
				if (nearbyPartLogic.getController() != this) {
					continue;
				}

				if (!nearbyPartLogic.isVisited()) {
					nearbyPartLogic.setVisited();
					partsToCheck.add(nearbyPart);
				}
			}
		}
		
		// Finally, remove all parts that remain disconnected.
		Set<IMultiblockComponent> removedParts = new HashSet<>();
		for (IMultiblockComponent orphanCandidate : connectedParts) {
			MultiblockLogic logic = (MultiblockLogic) orphanCandidate.getMultiblockLogic();
			if (!logic.isVisited()) {
				deadParts.add(orphanCandidate);
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

	@Override
	@Nonnull
	public Set<IMultiblockComponent> detachAllBlocks() {
		if (worldObj == null) {
			return new HashSet<>();
		}
		
		IChunkProvider chunkProvider = worldObj.getChunkProvider();
		for (IMultiblockComponent part : connectedParts) {
			ChunkCoordinates partCoord = part.getCoordinates();
			if (chunkProvider.chunkExists(partCoord.posX >> 4, partCoord.posZ >> 4)) {
				onDetachBlock(part);
			}
		}

		Set<IMultiblockComponent> detachedParts = connectedParts;
		connectedParts = new HashSet<>();
		return detachedParts;
	}

	/**
	 * @return True if this multiblock machine is considered assembled and ready to go.
	 */
	@Override
	public boolean isAssembled() {
		return this.assemblyState == AssemblyState.Assembled;
	}
	
	private void selectNewReferenceCoord() {
		IChunkProvider chunkProvider = worldObj.getChunkProvider();
		IMultiblockComponent theChosenOne = null;
		referenceCoord = null;

		for (IMultiblockComponent part : connectedParts) {
			ChunkCoordinates partCoord = part.getCoordinates();
			if (isInvalid(part) || !chunkProvider.chunkExists(partCoord.posX >> 4, partCoord.posZ >> 4)) {
				// Chunk is unloading, skip this coord to prevent chunk thrashing
				continue;
			}

			if (referenceCoord == null || referenceCoord.compareTo(partCoord) > 0) {
				referenceCoord = part.getCoordinates();
				theChosenOne = part;
			}
		}

		if (theChosenOne != null) {
			MultiblockLogic logic = (MultiblockLogic) theChosenOne.getMultiblockLogic();
			logic.becomeMultiblockSaveDelegate();
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
		ChunkCoordinates rc = getReferenceCoord();
		if (worldObj != null && rc != null) {
			worldObj.markBlockForUpdate(rc.posX, rc.posY, rc.posZ);
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

		ChunkCoordinates referenceCoord = getReferenceCoord();
		if (referenceCoord == null) {
			return;
		}

		TileEntity saveTe = worldObj.getTileEntity(referenceCoord.posX, referenceCoord.posY, referenceCoord.posZ);
		worldObj.markTileEntityChunkModified(referenceCoord.posX, referenceCoord.posY, referenceCoord.posZ, saveTe);
	}

	private static boolean isInvalid(IMultiblockComponent part) {
		return part instanceof TileEntity && ((TileEntity) part).isInvalid();
	}
}
