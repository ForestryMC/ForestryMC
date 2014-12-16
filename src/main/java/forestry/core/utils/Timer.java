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

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Timer {

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
