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
package forestry.greenhouse.multiblock.blocks.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import forestry.api.climate.GreenhouseState;
import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateHousing;
import forestry.api.greenhouse.IBlankBlock;
import forestry.api.greenhouse.IGreenhouseBlock;
import forestry.api.greenhouse.IGreenhouseBlockHandler;
import forestry.api.greenhouse.IGreenhouseLimits;
import forestry.api.greenhouse.Position2D;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;
import forestry.greenhouse.multiblock.GreenhouseLimits;
import forestry.greenhouse.multiblock.GreenhouseLimitsBuilder;
import forestry.greenhouse.multiblock.blocks.GreenhouseException;
import forestry.greenhouse.multiblock.blocks.blank.BlankBlockHandler;
import forestry.greenhouse.multiblock.blocks.wall.WallBlockHandler;
import forestry.greenhouse.multiblock.blocks.world.GreenhouseBlockManager;
import forestry.greenhouse.network.packets.PacketGreenhouseData;

public class GreenhouseProviderServer extends GreenhouseProvider {
	private static final List<IGreenhouseBlockHandler> HANDLERS = new ArrayList<>();
	private static final int TIME_BETWEEN_UPDATES = 100;
	private static final int UPDATE_DELAY = 35;

	private final Set<Long> unloadedChunks;

	private Position2D maxSize;
	private Position2D minSize;
	private boolean needReload;
	private boolean needUpdate;
	private long previousUpdateTick;

	public GreenhouseProviderServer(World world, IClimateContainer container) {
		super(world, container);
		this.unloadedChunks = new HashSet<>();
		this.maxSize = Position2D.NULL_POSITION;
		this.minSize = Position2D.NULL_POSITION;
	}

	public void create() {
		if (ready) {
			GreenhouseState oldState = this.state;

			storage.removeProviderFromChunks();
			storage.clearBlocks(false);
			createBlocks();
			storage.addProviderToChunks();
			if (!world.isRemote) {
				IClimateHousing region = container.getParent();
				BlockPos pos = region.getCoordinates();
				if (pos != null) {
					needReload = oldState != state;
					NetworkUtil.sendNetworkPacket(new PacketGreenhouseData(pos, this), pos, world);
				}
			}
		}
	}

	@Override
	public void init(BlockPos centerPos, IGreenhouseLimits limits) {
		super.init(centerPos, limits);
	}

	@Override
	public void clear(boolean chunkUnloading) {
		super.clear(chunkUnloading);
		this.maxSize = Position2D.NULL_POSITION;
		this.minSize = Position2D.NULL_POSITION;
		this.unloadedChunks.clear();
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		storage.writeData(data);
		data.writeByte(state.ordinal());
		data.writeBoolean(needReload);
		data.writeInt(size);
		data.writeBlockPos(centerPos);
		if (limits != null && limits instanceof GreenhouseLimits) {
			data.writeBoolean(true);
			((GreenhouseLimits) limits).writeData(data);
		} else {
			data.writeBoolean(false);
		}
		if (usedLimits != null && usedLimits instanceof GreenhouseLimits) {
			data.writeBoolean(true);
			((GreenhouseLimits) usedLimits).writeData(data);
		} else {
			data.writeBoolean(false);
		}
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		// Only read on the client side
	}

	@Override
	public synchronized void recreate() {
		create();
	}

	/* CHUNK LOADING */
	@Override
	public void onUnloadChunk(long chunkPos) {
		unloadedChunks.add(chunkPos);
		lastNotClosedException = new GreenhouseException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.space.unloaded"));
		state = GreenhouseState.UNLOADED_CHUNK;
	}

	@Override
	public void onLoadChunk(long chunkPos) {
		unloadedChunks.remove(chunkPos);
		if (unloadedChunks.isEmpty() && hasUnloadedChunks()) {
			state = GreenhouseState.OPEN;
			create();
		}
	}

	public void checkPosition(BlockPos position) throws GreenhouseException {
		if (maxSize.getX() < position.getX()
			|| maxSize.getZ() < position.getZ()
			|| minSize.getX() > position.getX()
			|| minSize.getZ() > position.getZ()) {
			throw new GreenhouseException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.space.notclosed", position.getX(), position.getZ())).setPos(position);
		}

		if (!world.isBlockLoaded(position)) {
			unloadedChunks.add(ChunkPos.asLong(position.getX() >> 4, position.getZ() >> 4));
			throw new GreenhouseException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.space.unloaded", position.getX(), position.getZ()));
		}
	}

	@Override
	public Collection<IGreenhouseBlockHandler> getHandlers() {
		if (HANDLERS.isEmpty()) {
			HANDLERS.add(WallBlockHandler.getInstance());
			HANDLERS.add(BlankBlockHandler.getInstance());
		}
		return HANDLERS;
	}

	@Override
	public void onBlockChange() {
		long totalWorldTime = world.getTotalWorldTime();
		if (totalWorldTime >= previousUpdateTick + TIME_BETWEEN_UPDATES) {
			GreenhouseBlockManager.getInstance().scheduleUpdate(world, container.getParent().getCoordinates(), this, UPDATE_DELAY);
			previousUpdateTick = totalWorldTime;
		}
	}

	@Override
	public void scheduledUpdate() {
		create();
	}

	/**
	 * Check all internal blocks.
	 */
	private void createBlocks() {
		Stack<IGreenhouseBlock> blocksToCheck = new Stack();
		blocksToCheck.add(BlankBlockHandler.getInstance().createBlock(storage, null, null, centerPos));
		checkState(checkBlocks(blocksToCheck));
		EntityPlayer player = world.getClosestPlayer(centerPos.getX(), centerPos.getY(), centerPos.getZ(), 100000.0D, false);
		if (!world.isRemote && lastNotClosedException != null && player != null) {
			player.sendMessage(new TextComponentString(lastNotClosedException.getMessage()));
		}
	}

	private void checkState(GreenhouseState state) {
		this.state = state;
	}

	/**
	 * Check all internal blocks.
	 */
	private GreenhouseState checkBlocks(Collection<IGreenhouseBlock> blocks) {
		try {
			if (minSize == null || maxSize == null || minSize == Position2D.NULL_POSITION || maxSize == Position2D.NULL_POSITION) {
				checkMinMax();
			}
			int greenhouseHeight = centerPos.getY();
			int greenhouseDepth = centerPos.getY();
			int height = 0;
			int depth = 0;
			int maximalHeight = ((IGreenhouseController) container.getParent()).getCenterCoordinates().getY() + limits.getHeight();
			GreenhouseLimitsBuilder builder = new GreenhouseLimitsBuilder();
			Stack<IGreenhouseBlock> blocksToCheck = new Stack();
			blocksToCheck.addAll(blocks);
			while (!blocksToCheck.isEmpty()) {
				IGreenhouseBlock blockToCheck = blocksToCheck.pop();
				if (blockToCheck != null) {
					BlockPos position = blockToCheck.getPos();
					IGreenhouseBlockHandler handler = blockToCheck.getHandler();
					builder.recalculate(position);
					List<IGreenhouseBlock> newBlocksToCheck = handler.checkNeighborBlocks(storage, blockToCheck);
					blocksToCheck.addAll(newBlocksToCheck);
					if (blockToCheck instanceof IBlankBlock) {
						int positionHeight = getHeight(position, maximalHeight);
						int positionDepth = getDepth(position);
						if (positionHeight == -1) {
							throw new GreenhouseException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.roof.notclosed", position.getX(), position.getY(), position.getZ())).setPos(position);
						}
						if (positionHeight > greenhouseHeight) {
							greenhouseHeight = positionHeight;
						}
						height += positionHeight - centerPos.getY();
						if (positionDepth < greenhouseDepth) {
							greenhouseDepth = positionDepth;
						}
						depth += centerPos.getY() - positionDepth;
					}
				}
			}
			this.size = height + depth + storage.getBlockCount();
			lastNotClosedException = null;
			usedLimits = builder.build(greenhouseHeight, greenhouseDepth);
			return GreenhouseState.CLOSED;
		} catch (GreenhouseException exception) {
			lastNotClosedException = exception;
			if (!unloadedChunks.isEmpty()) {
				return GreenhouseState.UNLOADED_CHUNK;
			}
			return GreenhouseState.OPEN;
		}
	}

	private void checkMinMax() throws GreenhouseException {
		if (limits == null) {
			minSize = maxSize = null;
			throw new GreenhouseException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.edges"));
		}
		if (limits.getMaximumCoordinates() != null) {
			Position2D edge = limits.getMaximumCoordinates();
			maxSize = edge.add(1, 1).add(centerPos.getX(), centerPos.getZ());
		} else {
			maxSize = null;
			throw new GreenhouseException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.edge"));
		}
		if (limits.getMinimumCoordinates() != null) {
			Position2D edge = limits.getMinimumCoordinates();
			minSize = edge.add(-1, -1).add(centerPos.getX(), centerPos.getZ());
		} else {
			minSize = null;
			throw new GreenhouseException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.edge"));
		}
	}

	public int getHeight(BlockPos pos, int maximalHeight) {
		int worldHeight = world.getHeight();
		BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(pos);
		int height = -1;

		while (position.getY() <= worldHeight && position.getY() <= maximalHeight && height == -1) {
			IBlockState blockState = world.getBlockState(position);
			AxisAlignedBB collisionBB = blockState.getCollisionBoundingBox(world, pos);

			if (collisionBB == null || collisionBB.equals(Block.NULL_AABB) || blockState.getBlock().isLeaves(blockState, world, pos)) {
				position.move(EnumFacing.UP);
			} else {
				height = position.down().getY();
			}
		}

		return height;
	}

	public int getDepth(BlockPos pos) {
		int worldDepth = 0;
		BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(pos);
		int depth = -1;

		while (position.getY() > worldDepth && depth == -1) {
			IBlockState blockState = world.getBlockState(position);
			AxisAlignedBB collisionBB = blockState.getCollisionBoundingBox(world, pos);

			if (collisionBB == null || collisionBB.equals(Block.NULL_AABB) || blockState.getBlock().isLeaves(blockState, world, pos)) {
				position.move(EnumFacing.DOWN);
			} else {
				depth = position.up().getY();
			}
		}

		return depth;
	}
}
