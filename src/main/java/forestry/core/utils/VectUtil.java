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

import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public final class VectUtil {
	public static BlockPos getRandomPositionInArea(Random random, Vec3i area) {
		int x = random.nextInt(area.getX());
		int y = random.nextInt(area.getY());
		int z = random.nextInt(area.getZ());
		return new BlockPos(x, y, z);
	}

	public static BlockPos add(Vec3i... vects) {
		int x = 0;
		int y = 0;
		int z = 0;
		for (Vec3i vect : vects) {
			x += vect.getX();
			y += vect.getY();
			z += vect.getZ();
		}
		return new BlockPos(x, y, z);
	}

	public static BlockPos scale(Vec3i vect, float factor) {
		return new BlockPos(vect.getX() * factor, vect.getY() * factor, vect.getZ() * factor);
	}

	public static EnumFacing direction(Vec3i a, Vec3i b) {
		int x = Math.abs(a.getX() - b.getX());
		int y = Math.abs(a.getY() - b.getY());
		int z = Math.abs(a.getZ() - b.getZ());
		int max = Math.max(x, Math.max(y, z));
		if (max == x) {
			return EnumFacing.EAST;
		} else if (max == z) {
			return EnumFacing.SOUTH;
		} else {
			return EnumFacing.UP;
		}
	}
}
