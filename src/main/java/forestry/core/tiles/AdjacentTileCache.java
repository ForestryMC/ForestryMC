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
package forestry.core.tiles;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * A helper class that caches adjacent tiles for a given tile entity.
 * <p>
 * Listeners can be added to listen for adjacent tile changes.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class AdjacentTileCache {

	private static final int DELAY_MIN = 20;
	private static final int DELAY_MAX = 2400;
	private static final int DELAY_STEP = 2;
	private final Timer[] timer = new Timer[6];
	private final BlockEntity[] cache = new BlockEntity[6];
	private final int[] delay = new int[6];
	private final BlockEntity source;
	private final Set<ICacheListener> listeners = new LinkedHashSet<>();

	/**
	 * Listener that listens for adjacent tile changes.
	 */
	public interface ICacheListener {

		/**
		 * Called at the moment the tile registers an adjacent tile change.
		 */
		void changed();

		/**
		 * Calle if the tile entity gets removed to clean the cached data.
		 */
		void purge();

	}

	public AdjacentTileCache(BlockEntity tile) {
		this.source = tile;
		Arrays.fill(delay, DELAY_MIN);
		for (int i = 0; i < timer.length; i++) {
			timer[i] = new Timer();
		}
	}

	public void addListener(ICacheListener listener) {
		listeners.add(listener);
	}

	@Nullable
	private BlockEntity searchSide(Direction side) {
		Level world = source.getLevel();
		BlockPos pos = source.getBlockPos().relative(side);
		if (world.hasChunkAt(pos) && !world.isEmptyBlock(pos)) {
			return TileUtil.getTile(world, pos);
		}
		return null;
	}

	public void refresh() {
		for (Direction side : Direction.values()) {
			getTileOnSide(side);
		}
	}

	public void purge() {
		Arrays.fill(cache, null);
		Arrays.fill(delay, DELAY_MIN);
		for (Timer t : timer) {
			t.reset();
		}
		changed();
		for (ICacheListener listener : listeners) {
			listener.purge();
		}
	}

	public void onNeighborChange() {
		Arrays.fill(delay, DELAY_MIN);
	}

	protected void setTile(int side, @Nullable BlockEntity tile) {
		if (cache[side] != tile) {
			cache[side] = tile;
			changed();
		}
	}

	private void changed() {
		for (ICacheListener listener : listeners) {
			listener.changed();
		}
	}

	private boolean areCoordinatesOnSide(Direction side, BlockEntity target) {
		return source.getBlockPos().getX() + side.getStepX() == target.getBlockPos().getX() && source.getBlockPos().getY() + side.getStepY() == target.getBlockPos().getY() && source.getBlockPos().getZ() + side.getStepZ() == target.getBlockPos().getZ();
	}

	@Nullable
	public BlockEntity getTileOnSide(Direction side) {
		int s = side.ordinal();
		if (cache[s] != null) {
			if (cache[s].isRemoved() || !areCoordinatesOnSide(side, cache[s])) {
				setTile(s, null);
			} else {
				return cache[s];
			}
		}

		if (timer[s].hasTriggered(source.getLevel(), delay[s])) {
			setTile(s, searchSide(side));
			if (cache[s] == null) {
				incrementDelay(s);
			} else {
				delay[s] = DELAY_MIN;
			}
		}

		return cache[s];
	}

	private void incrementDelay(int side) {
		delay[side] += DELAY_STEP;
		if (delay[side] > DELAY_MAX) {
			delay[side] = DELAY_MAX;
		}
	}

	public BlockEntity getSource() {
		return source;
	}

	private static class Timer {

		private long startTime = Long.MIN_VALUE;

		public boolean hasTriggered(Level world, int ticks) {
			long currentTime = world.getGameTime();
			if (currentTime >= ticks + startTime || startTime > currentTime) {
				startTime = currentTime;
				return true;
			}
			return false;
		}

		public void reset() {
			startTime = Long.MIN_VALUE;
		}

	}
}
