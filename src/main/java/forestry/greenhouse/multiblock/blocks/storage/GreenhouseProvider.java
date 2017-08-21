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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.core.network.IStreamable;
import forestry.greenhouse.api.climate.GreenhouseState;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.greenhouse.IGreenhouseLimits;
import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;
import forestry.greenhouse.api.greenhouse.IGreenhouseProviderListener;

public abstract class GreenhouseProvider implements IGreenhouseProvider, IStreamable {

	protected final Set<IGreenhouseProviderListener> listeners;
	protected final GreenhouseBlockStorage storage;
	protected final World world;
	protected final IClimateContainer container;
	private final IErrorLogic errorLogic;

	protected BlockPos centerPos;
	protected boolean ready;
	protected GreenhouseState state;
	@Nullable
	protected IGreenhouseLimits limits;
	@Nullable
	protected IGreenhouseLimits usedLimits;
	protected int size;

	public GreenhouseProvider(World world, IClimateContainer container) {
		this.listeners = new HashSet<>();
		this.centerPos = BlockPos.ORIGIN;
		this.world = world;
		this.container = container;
		this.state = GreenhouseState.UNREADY;
		this.storage = new GreenhouseBlockStorage(this, world);
		this.errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
	}

	public abstract void create();

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public IClimateContainer getClimateContainer() {
		return container;
	}

	@Override
	public void addListener(IGreenhouseProviderListener listener) {
		listeners.add(listener);
	}

	public Collection<IGreenhouseProviderListener> getListeners() {
		return listeners;
	}

	@Override
	public void init(BlockPos centerPos, IGreenhouseLimits limits) {
		this.centerPos = centerPos;
		this.limits = limits;
		ready = true;
		state = GreenhouseState.UNLOADED;
	}

	@Override
	public void clear(boolean chunkUnloading) {
		storage.clearBlocks(chunkUnloading);
		this.centerPos = BlockPos.ORIGIN;
		ready = false;
	}

	@Override
	public boolean hasUnloadedChunks() {
		return state == GreenhouseState.UNLOADED_CHUNK;
	}

	@Override
	public BlockPos getCenterPos() {
		return centerPos;
	}

	@Override
	public IGreenhouseLimits getLimits() {
		return limits;
	}

	@Nullable
	@Override
	public IGreenhouseLimits getUsedLimits() {
		return usedLimits;
	}

	@Override
	public GreenhouseState getState() {
		return state;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public boolean isClosed() {
		return state == GreenhouseState.CLOSED;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public GreenhouseBlockStorage getStorage() {
		return storage;
	}

	@Override
	public int hashCode() {
		return container.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GreenhouseProvider)) {
			return false;
		}
		GreenhouseProvider provider = (GreenhouseProvider) obj;
		return provider.getClimateContainer().equals(container);
	}
}
