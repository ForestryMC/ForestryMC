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
import java.util.Collection;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.IErrorState;
import forestry.api.greenhouse.IClimateHousing;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.network.PacketBufferForestry;
import forestry.greenhouse.api.climate.GreenhouseState;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlockHandler;
import forestry.greenhouse.api.greenhouse.Position2D;
import forestry.greenhouse.multiblock.GreenhouseLimits;
import forestry.greenhouse.multiblock.blocks.client.ClientBlockHandler;

public class GreenhouseProviderClient extends GreenhouseProvider {
	boolean reloadChunks;

	public GreenhouseProviderClient(World world, IClimateContainer container) {
		super(world, container);
	}

	@Override
	public void create() {
		//Server side only
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		// Only write on the server side
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		storage.readData(data);
		storage.createChunksFromCache();
		state = GreenhouseState.values()[data.readByte()];
		reloadChunks = data.readBoolean();
		size = data.readInt();
		centerPos = data.readBlockPos();
		if (data.readBoolean()) {
			limits = new GreenhouseLimits(data);
		}
		if (data.readBoolean()) {
			usedLimits = new GreenhouseLimits(data);
		}
		getErrorLogic().readData(data);
	}

	@Override
	public synchronized void recreate() {
		storage.clearBlocks(false);
		storage.createBlocksFromCache();
		Minecraft mc = Minecraft.getMinecraft();
		mc.addScheduledTask(this::markBlockForRenderUpdate);
	}

	/* CHUNK LOADING */
	@Override
	public void onUnloadChunk(long chunkPos) {
		//Server side only
	}

	@Override
	public void onLoadChunk(long chunkPos) {
		//Server side only
	}

	@Override
	public IErrorState checkPosition(BlockPos position) {
		return null;
	}

	@Override
	public Collection<IGreenhouseBlockHandler> getHandlers() {
		return Collections.singleton(ClientBlockHandler.getInstance());
	}

	@Override
	public void onBlockChange() {
		//Server side only
	}

	@Override
	public void scheduledUpdate() {
		//Server side only
	}

	public void markBlockForRenderUpdate() {
		IClimateHousing housing = container.getParent();
		if (housing instanceof IGreenhouseController) {
			IGreenhouseController controller = (IGreenhouseController) housing;
			if (!controller.isAssembled()) {
				return;
			}
			BlockPos position = controller.getCenterCoordinates();
			Position2D minEdge = limits.getMinimumCoordinates();
			Position2D maxEdge = limits.getMaximumCoordinates();
			BlockPos minPos = new BlockPos(minEdge.getX(), -limits.getDepth(), minEdge.getZ());
			BlockPos maxPos = new BlockPos(maxEdge.getX() + 1, limits.getHeight(), maxEdge.getZ() + 1);
			minPos = minPos.add(position);
			maxPos = maxPos.add(position);
			world.markBlockRangeForRenderUpdate(minPos, maxPos);
		}
	}
}
