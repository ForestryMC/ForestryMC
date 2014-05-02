/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
		} else
			return false;
	}
}
