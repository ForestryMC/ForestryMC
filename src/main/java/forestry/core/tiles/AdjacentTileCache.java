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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class AdjacentTileCache {

	private static final int DELAY_MIN = 20;
	private static final int DELAY_MAX = 2400;
	private static final int DELAY_STEP = 2;
	private final Timer[] timer = new Timer[6];
	private final TileEntity[] cache = new TileEntity[6];
	private final int[] delay = new int[6];
	private final TileEntity source;
	private final Set<ICacheListener> listeners = new LinkedHashSet<>();

	public interface ICacheListener {

		void changed();

		void purge();

	}

	public AdjacentTileCache(TileEntity tile) {
		this.source = tile;
		Arrays.fill(delay, DELAY_MIN);
		for (int i = 0; i < timer.length; i++) {
			timer[i] = new Timer();
		}
	}

	public void addListener(ICacheListener listener) {
		listeners.add(listener);
	}

	private TileEntity searchSide(ForgeDirection side) {
		World world = source.getWorldObj();
		int sx = source.xCoord + side.offsetX;
		int sy = source.yCoord + side.offsetY;
		int sz = source.zCoord + side.offsetZ;
		if (world.blockExists(sx, sy, sz) && world.getBlock(sx, sy, sz) != Blocks.air) {
			return world.getTileEntity(sx, sy, sz);
		}
		return null;
	}

	public void refresh() {
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
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

	protected void setTile(int side, TileEntity tile) {
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

	private boolean areCoordinatesOnSide(ForgeDirection side, TileEntity target) {
		return source.xCoord + side.offsetX == target.xCoord && source.yCoord + side.offsetY == target.yCoord && source.zCoord + side.offsetZ == target.zCoord;
	}

	public TileEntity getTileOnSide(ForgeDirection side) {
		int s = side.ordinal();
		if (cache[s] != null) {
			if (cache[s].isInvalid() || !areCoordinatesOnSide(side, cache[s])) {
				setTile(s, null);
			} else {
				return cache[s];
			}
		}

		if (timer[s].hasTriggered(source.getWorldObj(), delay[s])) {
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

	public TileEntity getSource() {
		return source;
	}

	private static class Timer {

		private long startTime = Long.MIN_VALUE;

		public boolean hasTriggered(World world, int ticks) {
			long currentTime = world.getTotalWorldTime();
			if (currentTime >= (ticks + startTime) || startTime > currentTime) {
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
