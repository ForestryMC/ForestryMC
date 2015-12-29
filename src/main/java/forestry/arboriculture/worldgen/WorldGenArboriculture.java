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
package forestry.arboriculture.worldgen;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.world.ITreeGenData;
import forestry.arboriculture.tiles.TileTreeContainer;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.worldgen.BlockType;
import forestry.core.worldgen.BlockTypeVoid;
import forestry.core.worldgen.WorldGenBase;

public abstract class WorldGenArboriculture extends WorldGenBase {

	enum Direction {
		NORTH(ForgeDirection.NORTH),
		SOUTH(ForgeDirection.SOUTH),
		WEST(ForgeDirection.WEST),
		EAST(ForgeDirection.EAST);

		public final ForgeDirection forgeDirection;

		Direction(ForgeDirection forgeDirection) {
			this.forgeDirection = forgeDirection;
		}

		public static Direction getRandom(Random random) {
			return values()[random.nextInt(values().length)];
		}

		public static Direction getRandomOther(Random random, Direction direction) {
			EnumSet<Direction> directions = EnumSet.allOf(Direction.class);
			directions.remove(direction);
			int size = directions.size();
			return directions.toArray(new Direction[size])[random.nextInt(size)];
		}
	}

	private static final ITreeBlockType vineNorth = new TreeBlockType(Blocks.vine, 1);
	private static final ITreeBlockType vineSouth = new TreeBlockType(Blocks.vine, 4);
	private static final ITreeBlockType vineWest = new TreeBlockType(Blocks.vine, 8);
	private static final ITreeBlockType vineEast = new TreeBlockType(Blocks.vine, 2);
	private static final int minPodHeight = 3;

	private static final BlockType air = new BlockTypeVoid();

	protected final ITreeGenData tree;
	private int startX;
	private int startY;
	private int startZ;

	protected TreeBlockTypeLeaf leaf;
	protected ITreeBlockType wood;

	private boolean spawnPods = false;

	protected WorldGenArboriculture(ITreeGenData tree) {
		this.tree = tree;
	}

	@Override
	public boolean generate(World world, int x, int y, int z, boolean forced) {
		this.spawnPods = tree.allowsFruitBlocks();
		this.leaf = getLeaf(getOwner(world, x, y, z));
		this.wood = getWood();

		preGenerate(world, x, y, z);
		if (forced || canGrow(world, x, y, z)) {
			generate(world);
			return true;
		}

		return false;
	}

	private static GameProfile getOwner(World world, int x, int y, int z) {
		TileTreeContainer tile = TileUtil.getTile(world, x, y, z, TileTreeContainer.class);
		if (tile == null) {
			return null;
		}
		return tile.getOwner();
	}

	public void preGenerate(World world, int startX, int startY, int startZ) {
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
	}

	protected abstract void generate(World world);

	public abstract boolean canGrow(World world, int x, int y, int z);

	protected abstract TreeBlockTypeLeaf getLeaf(GameProfile owner);

	protected abstract ITreeBlockType getWood();

	protected List<ChunkCoordinates> generateTreeTrunk(World world, int height, int girth) {
		return generateTreeTrunk(world, height, girth, 0);
	}

	protected List<ChunkCoordinates> generateTreeTrunk(World world, int height, int girth, float vinesChance) {
		return generateTreeTrunk(world, height, girth, vinesChance, ForgeDirection.UNKNOWN, 0);
	}

	protected List<ChunkCoordinates> generateTreeTrunk(World world, int height, int girth, float vinesChance, ForgeDirection leanDirection, float leanAmount) {
		return generateTreeTrunk(world, height, girth, 0, vinesChance, leanDirection, leanAmount);
	}

	/** Returns a list of trunk top coordinates */
	protected List<ChunkCoordinates> generateTreeTrunk(World world, int height, int girth, int yStart, float vinesChance, ForgeDirection leanDirection, float leanAmount) {
		List<ChunkCoordinates> treeTops = new ArrayList<>();

		final int leanStartY = (int) Math.floor(height * 0.33f);
		int prevXOffset = 0;
		int prevZOffset = 0;

		int offset = (girth - 1) / 2;
		for (int x = 0; x < girth; x++) {
			for (int z = 0; z < girth; z++) {
				for (int y = yStart; y < height; y++) {
					float lean;
					if (y < leanStartY) {
						lean = 0;
					} else {
						lean = leanAmount * (y - leanStartY) / (height - leanStartY);
					}
					int xOffset = (int) Math.floor(leanDirection.offsetX * lean - offset);
					int zOffset = (int) Math.floor(leanDirection.offsetZ * lean - offset);

					if (xOffset != prevXOffset || zOffset != prevZOffset) {
						prevXOffset = xOffset;
						prevZOffset = zOffset;
						if (y > 0) {
							wood.setDirection(leanDirection);
							addWood(world, x + xOffset, y - 1, z + zOffset, EnumReplaceMode.ALL);
							wood.setDirection(ForgeDirection.UP);
						}
					}

					addWood(world, x + xOffset, y, z + zOffset, EnumReplaceMode.ALL);
					addVines(world, x + xOffset, y, z + zOffset, vinesChance);

					if (y + 1 == height) {
						treeTops.add(new ChunkCoordinates(x + xOffset, y, z + zOffset));
					}
				}
			}
		}

		if (spawnPods) {
			generatePods(world, height, girth);
		}

		return treeTops;
	}

	protected void generatePods(World world, int height, int girth) {
		for (int y = minPodHeight; y < height; y++) {
			for (int x = 0; x < girth; x++) {
				for (int z = 0; z < girth; z++) {

					if ((x > 0 && x < girth) && (z > 0 && z < girth)) {
						continue;
					}

					trySpawnFruitBlock(world, x + 1, y, z);
					trySpawnFruitBlock(world, x - 1, y, z);
					trySpawnFruitBlock(world, x, y, z + 1);
					trySpawnFruitBlock(world, x, y, z - 1);
				}
			}
		}
	}

	private void trySpawnFruitBlock(World world, int x, int y, int z) {
		x += startX;
		y += startY;
		z += startZ;
		if (BlockUtil.isReplaceableBlock(world, x, y, z) || world.isAirBlock(x, y, z)) {
			tree.trySpawnFruitBlock(world, x, y, z);
		}
	}

	protected void generateSupportStems(World world, int height, int girth, float chance, float maxHeight) {

		int offset = (int) Math.ceil(girth / 2.0f);
		int max = (int) Math.floor(girth / 2.0f) + offset;
		if (girth % 2 == 0) {
			max++;
		}

		for (int x = -offset; x < max; x++) {
			for (int z = -offset; z < max; z++) {

				if (x == -offset && z == -offset) {
					continue;
				}
				if (x == max && z == max) {
					continue;
				}
				if (x == -offset && z == max) {
					continue;
				}
				if (x == max && z == -offset) {
					continue;
				}

				int stemHeight = world.rand.nextInt(Math.round(height * maxHeight));
				if (world.rand.nextFloat() < chance) {
					for (int i = 0; i < stemHeight; i++) {
						addWood(world, x, i, z, EnumReplaceMode.SOFT);
					}
				}
			}
		}
	}

	protected List<ChunkCoordinates> generateBranches(World world, final int startY, int xOffset, int zOffset, float spreadY, float spreadXZ, int radius, int count) {
		return generateBranches(world, startY, xOffset, zOffset, spreadY, spreadXZ, radius, count, 1.0f);
	}

	protected List<ChunkCoordinates> generateBranches(World world, final int startY, final int xOffset, final int zOffset, final float spreadY, final float spreadXZ, int radius, final int count, final float chance) {
		List<ChunkCoordinates> branchEnds = new ArrayList<>();
		if (radius < 1) {
			radius = 1;
		}

		for (Direction cardinalDirection : Direction.values()) {
			ForgeDirection branchDirection = cardinalDirection.forgeDirection;
			wood.setDirection(branchDirection);
			for (int i = 0; i < count; i++) {
				if (world.rand.nextFloat() > chance) {
					continue;
				}
				int y = startY;
				int x = xOffset;
				int z = zOffset;

				for (int r = 0; r < radius; r++) {
					if (world.rand.nextFloat() < spreadY) {
						// make branches only spread up, not down
						y++;
					} else {
						if (world.rand.nextFloat() < spreadXZ) {
							if (branchDirection.offsetX == 0) {
								if (world.rand.nextBoolean()) {
									x++;
								} else {
									x--;
								}
								wood.setDirection(ForgeDirection.EAST);
							} else if (branchDirection.offsetZ == 0) {
								if (world.rand.nextBoolean()) {
									z++;
								} else {
									z--;
								}
								wood.setDirection(ForgeDirection.SOUTH);
							}
						} else {
							x += branchDirection.offsetX;
							z += branchDirection.offsetZ;
							wood.setDirection(branchDirection);
						}
					}
					if (addWood(world, x, y, z, EnumReplaceMode.SOFT)) {
						branchEnds.add(new ChunkCoordinates(x, y, z));
					} else {
						break;
					}
				}
			}
		}

		return branchEnds;
	}

	@Override
	protected boolean addBlock(World world, int x, int y, int z, ITreeBlockType type, EnumReplaceMode replace) {
		if (replace == EnumReplaceMode.ALL
				|| (replace == EnumReplaceMode.SOFT && BlockUtil.isReplaceableBlock(world, startX + x, startY + y, startZ + z))
				|| world.isAirBlock(startX + x, startY + y, startZ + z)) {
			type.setBlock(world, tree, startX + x, startY + y, startZ + z);
			return true;
		}
		return false;
	}

	protected final void clearBlock(World world, int x, int y, int z) {
		air.setBlock(world, startX + x, startY + y, startZ + z);
	}

	protected final boolean addWood(World world, int x, int y, int z, EnumReplaceMode replace) {
		return addBlock(world, x, y, z, wood, replace);
	}
	
	protected final boolean addLeaf(World world, int x, int y, int z, EnumReplaceMode replace) {
		return addBlock(world, x, y, z, leaf, replace);
	}

	protected final boolean addVine(World world, int x, int y, int z, ITreeBlockType vine) {
		return addBlock(world, x, y, z, vine, EnumReplaceMode.NONE);
	}

	protected final void addVines(World world, int x, int y, int z, float chance) {
		if (chance <= 0) {
			return;
		}

		if (world.rand.nextFloat() < chance) {
			addVine(world, x - 1, y, z, vineWest);
		}
		if (world.rand.nextFloat() < chance) {
			addVine(world, x + 1, y, z, vineEast);
		}
		if (world.rand.nextFloat() < chance) {
			addVine(world, x, y, z - 1, vineNorth);
		}
		if (world.rand.nextFloat() < chance) {
			addVine(world, x, y, z + 1, vineSouth);
		}
	}

}
