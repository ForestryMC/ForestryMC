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
package forestry.core.utils;

import net.minecraft.world.World;

public class DelayTimer {
	private long markedTime;

	public boolean delayPassed(World world, long delay) {
		long currentTime = world.getTotalWorldTime();

		if (currentTime < markedTime) {
			markedTime = currentTime;
			return false;
		} else if (markedTime + delay <= currentTime) {
			markedTime = currentTime;
			return true;
		} else {
			return false;
		}
	}
}
