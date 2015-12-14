package forestry.core.multiblock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.core.utils.Log;

/**
 * This class manages all the multiblock controllers that exist in a given world,
 * either client- or server-side.
 * You must create different registries for server and client worlds.
 *
 * @author Erogenous Beef
 */
public class MultiblockWorldRegistry {

	private World worldObj;
	
	private final Set<IMultiblockControllerInternal> controllers;        // Active controllers
	private final Set<IMultiblockControllerInternal> dirtyControllers;    // Controllers whose parts lists have changed
	private final Set<IMultiblockControllerInternal> deadControllers;    // Controllers which are empty

	// A list of orphan parts - parts which currently have no master, but should seek one this tick
	// Indexed by the hashed chunk coordinate
	// This can be added-to asynchronously via chunk loads!
	private Set<IMultiblockComponent> orphanedParts;

	// A list of parts which have been detached during internal operations
	private final Set<IMultiblockComponent> detachedParts;
	
	// A list of parts whose chunks have not yet finished loading
	// They will be added to the orphan list when they are finished loading.
	// Indexed by the hashed chunk coordinate
	// This can be added-to asynchronously via chunk loads!
	private final HashMap<Long, Set<IMultiblockComponent>> partsAwaitingChunkLoad;
	
	// Mutexes to protect lists which may be changed due to asynchronous events, such as chunk loads
	private final Object partsAwaitingChunkLoadMutex;
	private final Object orphanedPartsMutex;
	
	public MultiblockWorldRegistry(World world) {
		worldObj = world;
		
		controllers = new HashSet<>();
		deadControllers = new HashSet<>();
		dirtyControllers = new HashSet<>();
		
		detachedParts = new HashSet<>();
		orphanedParts = new HashSet<>();

		partsAwaitingChunkLoad = new HashMap<>();
		partsAwaitingChunkLoadMutex = new Object();
		orphanedPartsMutex = new Object();
	}
	
	/**
	 * Called before Tile Entities are ticked in the world. Run game logic.
	 */
	public void tickStart() {
		if (controllers.size() > 0) {
			for (IMultiblockControllerInternal controller : controllers) {
				if (controller.getWorld() == worldObj && controller.getWorld().isRemote == worldObj.isRemote) {
					if (controller.isEmpty()) {
						// This happens on the server when the user breaks the last block. It's fine.
						// Mark 'er dead and move on.
						deadControllers.add(controller);
					} else {
						// Run the game logic for this world
						controller.updateMultiblockEntity();
					}
				}
			}
		}
	}
	
	/**
	 * Called prior to processing multiblock controllers. Do bookkeeping.
	 */
	public void processMultiblockChanges() {
		IChunkProvider chunkProvider = worldObj.getChunkProvider();
		ChunkCoordinates coord;

		// Merge pools - sets of adjacent machines which should be merged later on in processing
		List<Set<IMultiblockControllerInternal>> mergePools = null;
		if (orphanedParts.size() > 0) {
			Set<IMultiblockComponent> orphansToProcess = null;
			
			// Keep the synchronized block small. We can't iterate over orphanedParts directly
			// because the client does not know which chunks are actually loaded, so attachToNeighbors()
			// is not chunk-safe on the client, because Minecraft is stupid.
			// It's possible to polyfill this, but the polyfill is too slow for comfort.
			synchronized (orphanedPartsMutex) {
				if (orphanedParts.size() > 0) {
					orphansToProcess = orphanedParts;
					orphanedParts = new HashSet<>();
				}
			}
			
			if (orphansToProcess != null && orphansToProcess.size() > 0) {
				Set<IMultiblockControllerInternal> compatibleControllers;
				
				// Process orphaned blocks
				// These are blocks that exist in a valid chunk and require a controller
				for (IMultiblockComponent orphan : orphansToProcess) {
					coord = orphan.getCoordinates();
					if (!chunkProvider.chunkExists(coord.posX >> 4, coord.posZ >> 4)) {
						continue;
					}

					// This can occur on slow machines.
					if (orphan instanceof TileEntity && ((TileEntity) orphan).isInvalid()) {
						continue;
					}

					if (worldObj.getTileEntity(coord.posX, coord.posY, coord.posZ) != orphan) {
						// This block has been replaced by another.
						continue;
					}
					
					// THIS IS THE ONLY PLACE WHERE PARTS ATTACH TO MACHINES
					// Try to attach to a neighbor's master controller
					compatibleControllers = attachToNeighbors(orphan);
					if (compatibleControllers.size() == 0) {
						// FOREVER ALONE! Create and register a new controller.
						// THIS IS THE ONLY PLACE WHERE NEW CONTROLLERS ARE CREATED.
						MultiblockLogic logic = (MultiblockLogic) orphan.getMultiblockLogic();
						IMultiblockControllerInternal newController = logic.createNewController(worldObj);
						newController.attachBlock(orphan);
						this.controllers.add(newController);
					} else if (compatibleControllers.size() > 1) {
						if (mergePools == null) {
							mergePools = new ArrayList<>();
						}

						// THIS IS THE ONLY PLACE WHERE MERGES ARE DETECTED
						// Multiple compatible controllers indicates an impending merge.
						// Locate the appropriate merge pool(s)
						List<Set<IMultiblockControllerInternal>> candidatePools = new ArrayList<>();
						for (Set<IMultiblockControllerInternal> candidatePool : mergePools) {
							if (!Collections.disjoint(candidatePool, compatibleControllers)) {
								// They share at least one element, so that means they will all touch after the merge
								candidatePools.add(candidatePool);
							}
						}
						
						if (candidatePools.size() <= 0) {
							// No pools nearby, create a new merge pool
							mergePools.add(compatibleControllers);
						} else if (candidatePools.size() == 1) {
							// Only one pool nearby, simply add to that one
							candidatePools.get(0).addAll(compatibleControllers);
						} else {
							// Multiple pools- merge into one, then add the compatible controllers
							Set<IMultiblockControllerInternal> masterPool = candidatePools.get(0);
							Set<IMultiblockControllerInternal> consumedPool;
							for (int i = 1; i < candidatePools.size(); i++) {
								consumedPool = candidatePools.get(i);
								masterPool.addAll(consumedPool);
								mergePools.remove(consumedPool);
							}
							masterPool.addAll(compatibleControllers);
						}
					}
				}
			}
		}

		if (mergePools != null && mergePools.size() > 0) {
			// Process merges - any machines that have been marked for merge should be merged
			// into the "master" machine.
			// To do this, we combine lists of machines that are touching one another and therefore
			// should voltron the fuck up.
			for (Set<IMultiblockControllerInternal> mergePool : mergePools) {
				// Search for the new master machine, which will take over all the blocks contained in the other machines
				IMultiblockControllerInternal newMaster = null;
				for (IMultiblockControllerInternal controller : mergePool) {
					if (newMaster == null || controller.shouldConsume(newMaster)) {
						newMaster = controller;
					}
				}
				
				if (newMaster == null) {
					Log.severe("Multiblock system checked a merge pool of size %d, found no master candidates. This should never happen.", mergePool.size());
				} else {
					// Merge all the other machines into the master machine, then unregister them
					addDirtyController(newMaster);
					for (IMultiblockControllerInternal controller : mergePool) {
						if (controller != newMaster) {
							newMaster.assimilate(controller);
							addDeadController(controller);
							addDirtyController(newMaster);
						}
					}
				}
			}
		}

		// Process splits and assembly
		// Any controllers which have had parts removed must be checked to see if some parts are no longer
		// physically connected to their master.
		if (dirtyControllers.size() > 0) {
			for (IMultiblockControllerInternal controller : dirtyControllers) {
				if (controller == null) {
					continue;
				}
				// Tell the machine to check if any parts are disconnected.
				// It should return a set of parts which are no longer connected.
				// POSTCONDITION: The controller must have informed those parts that
				// they are no longer connected to this machine.
				Set<IMultiblockComponent> newlyDetachedParts = controller.checkForDisconnections();
				
				if (!controller.isEmpty()) {
					controller.recalculateMinMaxCoords();
					controller.checkIfMachineIsWhole();
				} else {
					addDeadController(controller);
				}
				
				if (newlyDetachedParts.size() > 0) {
					// Controller has shed some parts - add them to the detached list for delayed processing
					detachedParts.addAll(newlyDetachedParts);
				}
			}
			
			dirtyControllers.clear();
		}
		
		// Unregister dead controllers
		if (deadControllers.size() > 0) {
			for (IMultiblockControllerInternal controller : deadControllers) {
				// Go through any controllers which have marked themselves as potentially dead.
				// Validate that they are empty/dead, then unregister them.
				if (!controller.isEmpty()) {
					Log.severe("Found a non-empty controller. Forcing it to shed its blocks and die. This should never happen!");
					detachedParts.addAll(controller.detachAllBlocks());
				}

				// THIS IS THE ONLY PLACE WHERE CONTROLLERS ARE UNREGISTERED.
				this.controllers.remove(controller);
			}
			
			deadControllers.clear();
		}
		
		// Process detached blocks
		// Any blocks which have been detached this tick should be moved to the orphaned
		// list, and will be checked next tick to see if their chunk is still loaded.
		for (IMultiblockComponent part : detachedParts) {
			// Ensure parts know they're detached
			MultiblockLogic logic = (MultiblockLogic) part.getMultiblockLogic();
			logic.assertDetached(part);
		}
		
		addAllOrphanedPartsThreadsafe(detachedParts);
		detachedParts.clear();
	}

	///// Multiblock Connection Base Logic
	private Set<IMultiblockControllerInternal> attachToNeighbors(IMultiblockComponent part) {
		Set<IMultiblockControllerInternal> controllers = new HashSet<>();
		IMultiblockControllerInternal bestController = null;

		MultiblockLogic logic = (MultiblockLogic) part.getMultiblockLogic();
		Class<?> controllerClass = logic.getControllerClass();
		// Look for a compatible controller in our neighboring parts.
		List<IMultiblockComponent> partsToCheck = MultiblockUtil.getNeighboringParts(worldObj, part);
		for (IMultiblockComponent neighborPart : partsToCheck) {
			IMultiblockLogic neighborLogic = neighborPart.getMultiblockLogic();
			if (neighborLogic.isConnected()) {
				IMultiblockControllerInternal candidate = (IMultiblockControllerInternal) neighborLogic.getController();
				if (!controllerClass.isAssignableFrom(candidate.getClass())) {
					// Skip multiblocks with incompatible types
					continue;
				}

				if (!controllers.contains(candidate) && (bestController == null || candidate.shouldConsume(bestController))) {
					bestController = candidate;
				}

				controllers.add(candidate);
			}
		}

		// If we've located a valid neighboring controller, attach to it.
		if (bestController != null) {
			// attachBlock will call onAttached, which will set the controller.
			bestController.attachBlock(part);
		}

		return controllers;
	}

	/**
	 * Called when a multiblock part is added to the world, either via chunk-load or user action.
	 * If its chunk is loaded, it will be processed during the next tick.
	 * If the chunk is not loaded, it will be added to a list of objects waiting for a chunkload.
	 * @param part The part which is being added to this world.
	 */
	public void onPartAdded(IMultiblockComponent part) {
		ChunkCoordinates worldLocation = part.getCoordinates();
		
		if (!worldObj.getChunkProvider().chunkExists(worldLocation.posX >> 4, worldLocation.posZ >> 4)) {
			// Part goes into the waiting-for-chunk-load list
			Set<IMultiblockComponent> partSet;
			long chunkHash = ChunkCoordIntPair.chunkXZ2Int(worldLocation.posX >> 4, worldLocation.posZ >> 4);
			synchronized (partsAwaitingChunkLoadMutex) {
				if (!partsAwaitingChunkLoad.containsKey(chunkHash)) {
					partSet = new HashSet<>();
					partsAwaitingChunkLoad.put(chunkHash, partSet);
				} else {
					partSet = partsAwaitingChunkLoad.get(chunkHash);
				}
				
				partSet.add(part);
			}
		} else {
			// Part goes into the orphan queue, to be checked this tick
			addOrphanedPartThreadsafe(part);
		}
	}
	
	/**
	 * Called when a part is removed from the world, via user action or via chunk unloads.
	 * This part is removed from any lists in which it may be, and its machine is marked for recalculation.
	 * @param part The part which is being removed.
	 */
	public void onPartRemovedFromWorld(IMultiblockComponent part) {
		ChunkCoordinates coord = part.getCoordinates();
		if (coord != null) {
			long hash = ChunkCoordIntPair.chunkXZ2Int(coord.posX >> 4, coord.posZ >> 4);
			
			if (partsAwaitingChunkLoad.containsKey(hash)) {
				synchronized (partsAwaitingChunkLoadMutex) {
					if (partsAwaitingChunkLoad.containsKey(hash)) {
						partsAwaitingChunkLoad.get(hash).remove(part);
						if (partsAwaitingChunkLoad.get(hash).size() <= 0) {
							partsAwaitingChunkLoad.remove(hash);
						}
					}
				}
			}
		}

		detachedParts.remove(part);
		if (orphanedParts.contains(part)) {
			synchronized (orphanedPartsMutex) {
				orphanedParts.remove(part);
			}
		}

		MultiblockLogic logic = (MultiblockLogic) part.getMultiblockLogic();
		logic.assertDetached(part);
	}

	/**
	 * Called when the world which this World Registry represents is fully unloaded from the system.
	 * Does some housekeeping just to be nice.
	 */
	public void onWorldUnloaded() {
		controllers.clear();
		deadControllers.clear();
		dirtyControllers.clear();
		
		detachedParts.clear();
		
		synchronized (partsAwaitingChunkLoadMutex) {
			partsAwaitingChunkLoad.clear();
		}
		
		synchronized (orphanedPartsMutex) {
			orphanedParts.clear();
		}
		
		worldObj = null;
	}

	/**
	 * Called when a chunk has finished loading. Adds all of the parts which are awaiting
	 * load to the list of parts which are orphans and therefore will be added to machines
	 * after the next world tick.
	 *
	 * @param chunkX Chunk X coordinate (world coordate >> 4) of the chunk that was loaded
	 * @param chunkZ Chunk Z coordinate (world coordate >> 4) of the chunk that was loaded
	 */
	public void onChunkLoaded(int chunkX, int chunkZ) {
		long chunkHash = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
		if (partsAwaitingChunkLoad.containsKey(chunkHash)) {
			synchronized (partsAwaitingChunkLoadMutex) {
				if (partsAwaitingChunkLoad.containsKey(chunkHash)) {
					addAllOrphanedPartsThreadsafe(partsAwaitingChunkLoad.get(chunkHash));
					partsAwaitingChunkLoad.remove(chunkHash);
				}
			}
		}
	}

	/**
	 * Registers a controller as dead. It will be cleaned up at the end of the next world tick.
	 * Note that a controller must shed all of its blocks before being marked as dead, or the system
	 * will complain at you.
	 *
	 * @param deadController The controller which is dead.
	 */
	public void addDeadController(IMultiblockControllerInternal deadController) {
		this.deadControllers.add(deadController);
	}

	/**
	 * Registers a controller as dirty - its list of attached blocks has changed, and it
	 * must be re-checked for assembly and, possibly, for orphans.
	 *
	 * @param dirtyController The dirty controller.
	 */
	public void addDirtyController(IMultiblockControllerInternal dirtyController) {
		this.dirtyControllers.add(dirtyController);
	}
	
	/**
	 * Use this only if you know what you're doing. You should rarely need to iterate
	 * over all controllers in a world!
	 *
	 * @return An (unmodifiable) set of controllers which are active in this world.
	 */
	public Set<IMultiblockControllerInternal> getControllers() {
		return Collections.unmodifiableSet(controllers);
	}

	/* *** PRIVATE HELPERS *** */
	
	private void addOrphanedPartThreadsafe(IMultiblockComponent part) {
		synchronized (orphanedPartsMutex) {
			orphanedParts.add(part);
		}
	}
	
	private void addAllOrphanedPartsThreadsafe(Collection<? extends IMultiblockComponent> parts) {
		synchronized (orphanedPartsMutex) {
			orphanedParts.addAll(parts);
		}
	}
}
