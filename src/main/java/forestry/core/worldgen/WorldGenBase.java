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
package forestry.core.worldgen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.arboriculture.worldgen.ITreeBlockType;

public abstract class WorldGenBase extends WorldGenerator {

	protected enum EnumReplaceMode {

		NONE, ALL, SOFT
	}

	public static class Vector {

		public Vector(float f, float h, float g) {
			this.x = f;
			this.y = h;
			this.z = g;
		}

		public final float x;
		public final float y;
		public final float z;

		public static double distance(Vector a, Vector b) {
			return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
		}

		public static ForgeDirection direction(Vector a, Vector b) {
			int x = (int) Math.abs(a.x - b.x);
			int y = (int) Math.abs(a.y - b.y);
			int z = (int) Math.abs(a.z - b.z);
			int max = Math.max(x, Math.max(y, z));
			if (max == x) {
				return ForgeDirection.EAST;
			} else if (max == z) {
				return ForgeDirection.SOUTH;
			} else {
				return ForgeDirection.UP;
			}
		}
	}

	@Override
	public final boolean generate(World world, Random random, int x, int y, int z) {
		return generate(world, x, y, z, false);
	}

	public boolean generate(World world, int x, int y, int z, boolean forced) {
		return false;
	}

	protected abstract boolean addBlock(World world, int x, int y, int z, ITreeBlockType type, EnumReplaceMode replace);

	protected final void generateCuboid(World world, Vector start, Vector area, ITreeBlockType block, EnumReplaceMode replace) {
		for (int x = (int) start.x; x < (int) start.x + area.x; x++) {
			for (int y = (int) start.y; y < (int) start.y + area.y; y++) {
				for (int z = (int) start.z; z < (int) start.z + area.z; z++) {
					addBlock(world, x, y, z, block, replace);
				}
			}
		}
	}

	/*
	 * Center is the bottom middle of the cylinder
	 */
	protected final void generateCylinder(World world, Vector center, float radius, int height, ITreeBlockType block, EnumReplaceMode replace) {
		Vector start = new Vector(center.x - radius, center.y, center.z - radius);
		Vector area = new Vector(radius * 2 + 1, height, radius * 2 + 1);
		for (int x = (int) start.x; x < (int) start.x + area.x; x++) {
			for (int y = (int) start.y; y < (int) start.y + area.y; y++) {
				for (int z = (int) start.z; z < (int) start.z + area.z; z++) {
					Vector position = new Vector(x, y, z);
					Vector treeCenter = new Vector(center.x, y, center.z);
					if (Vector.distance(position, treeCenter) <= (radius) + 0.01) {
						ForgeDirection direction = Vector.direction(position, treeCenter);
						block.setDirection(direction);
						addBlock(world, x, y, z, block, replace);
					}
				}
			}
		}
	}

	protected final void generateCircle(World world, Vector center, float radius, int width, int height, ITreeBlockType block, EnumReplaceMode replace) {
		generateCircle(world, center, radius, width, height, block, 1.0f, replace);
	}

	protected final void generateCircle(World world, Vector center, float radius, int width, int height, ITreeBlockType block, float chance, EnumReplaceMode replace) {
		Vector start = new Vector(center.x - radius, center.y, center.z - radius);
		Vector area = new Vector(radius * 2 + 1, height, radius * 2 + 1);

		for (int x = (int) start.x; x < (int) start.x + area.x; x++) {
			for (int y = (int) start.y; y < (int) start.y + area.y; y++) {
				for (int z = (int) start.z; z < (int) start.z + area.z; z++) {

					if (world.rand.nextFloat() > chance) {
						continue;
					}

					double distance = Vector.distance(new Vector(x, y, z), new Vector(center.x, y, center.z));
					if ((radius - width - 0.01 < distance && distance <= (radius) + 0.01)) {
						addBlock(world, x, y, z, block, replace);
					}
				}
			}
		}
	}

	protected final void generateSphere(World world, Vector center, int radius, ITreeBlockType block, EnumReplaceMode replace) {
		Vector start = new Vector(center.x - radius, center.y - radius, center.z - radius);
		Vector area = new Vector(radius * 2 + 1, radius * 2 + 1, radius * 2 + 1);
		for (int x = (int) start.x; x < (int) start.x + area.x; x++) {
			for (int y = (int) start.y; y < (int) start.y + area.y; y++) {
				for (int z = (int) start.z; z < (int) start.z + area.z; z++) {
					if (Vector.distance(new Vector(x, y, z), new Vector(center.x, center.y, center.z)) <= (radius) + 0.01) {
						addBlock(world, x, y, z, block, replace);
					}
				}
			}
		}
	}
}
