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
package forestry.core.inventory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.tiles.AdjacentTileCache;
import forestry.core.tiles.TileUtil;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public final class AdjacentInventoryCache implements AdjacentTileCache.ICacheListener {

	public interface ITileFilter {
		boolean matches(TileEntity tile);
	}

	private final AdjacentTileCache cache;
	private boolean changed = true;
	private final List<IInventory> invs = new LinkedList<>();
	private final IInventory[] sides = new IInventory[6];
	private final Comparator<IInventory> sorter;
	private final ITileFilter filter;

	public AdjacentInventoryCache(TileEntity tile, AdjacentTileCache cache) {
		this(tile, cache, null, null);
	}

	public AdjacentInventoryCache(TileEntity tile, AdjacentTileCache cache, ITileFilter filter) {
		this(tile, cache, filter, null);
	}

	public AdjacentInventoryCache(TileEntity tile, AdjacentTileCache cache, ITileFilter filter, Comparator<IInventory> sorter) {
		this.cache = cache;
		this.filter = filter;
		this.sorter = sorter;
		cache.addListener(this);
	}

	public IInventory getAdjacentInventory(ForgeDirection side) {
		checkChanged();
		return sides[side.ordinal()];
	}

	public Collection<IInventory> getAdjacentInventories() {
		checkChanged();
		return invs;
	}

	@Override
	public void changed() {
		changed = true;
	}

	@Override
	public void purge() {
		invs.clear();
		Arrays.fill(sides, null);
	}

	private void checkChanged() {
		cache.refresh();
		if (changed) {
			changed = false;
			purge();
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
				TileEntity tile = cache.getTileOnSide(side);
				if (tile != null && (filter == null || filter.matches(tile))) {
					IInventory inv = TileUtil.getInventoryFromTile(tile, side.getOpposite());
					if (inv != null) {
						sides[side.ordinal()] = inv;
						invs.add(inv);
					}
				}
			}
			if (sorter != null) {
				Collections.sort(invs, sorter);
			}
		}
	}

}
