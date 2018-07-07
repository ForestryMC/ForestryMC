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
package forestry.greenhouse.multiblock.blocks.world;

import net.minecraft.util.math.BlockPos;

import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;

class NextTickEntry implements Comparable<NextTickEntry> {
	private final IGreenhouseProvider provider;
	private final BlockPos position;
	public long scheduledTime;

	public NextTickEntry(BlockPos position, IGreenhouseProvider provider) {
		this.position = position;
		this.provider = provider;
	}

	@Override
	public int hashCode() {
		return this.position.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NextTickEntry)) {
			return false;
		} else {
			NextTickEntry nextticklistentry = (NextTickEntry) o;
			return this.position.equals(nextticklistentry.position);
		}
	}

	@Override
	public String toString() {
		return provider + ": " + this.position + ", " + this.scheduledTime;
	}

	public NextTickEntry setScheduledTime(long scheduledTime) {
		this.scheduledTime = scheduledTime;
		return this;
	}

	@Override
	public int compareTo(NextTickEntry o) {
		return Long.compare(this.scheduledTime, o.scheduledTime);
	}

	public BlockPos getPosition() {
		return position;
	}

	public IGreenhouseProvider getProvider() {
		return provider;
	}
}
