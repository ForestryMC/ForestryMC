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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.world.ITreeGenData;
import forestry.arboriculture.tiles.TileTreeContainer;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.worldgen.BlockType;
import forestry.core.worldgen.BlockTypeVoid;
import forestry.core.worldgen.WorldGenBase;

public abstract class WorldGenArboriculture extends WorldGenBase {

	enum Direction {
		NORTH(EnumFacing.NORTH),
		SOUTH(EnumFacing.SOUTH),
		WEST(EnumFacing.WEST),
		EAST(EnumFacing.EAST);

		public final EnumFacing facing;

		Direction(EnumFacing forgeDirection) {
			this.facing = forgeDirection;
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
	public boolean generate(World world, BlockPos pos, boolean forced) {
		this.spawnPods = tree.allowsFruitBlocks();
		this.leaf = getLeaf(getOwner(world, pos));
		this.wood = getWood();

		preGenerate(world, pos);
		if (forced || canGrow(world, pos)) {
			generate(world);
			return true;
		}

		return false;
	}

	private static GameProfile getOwner(World world, BlockPos pos) {
		TileTreeContainer tile = TileUtil.getTile(world, pos, TileTreeContainer.class);
		if (tile == null) {
			return null;
		}
		return tile.getOwner();
	}

	public void preGenerate(World world, BlockPos pos) {
		this.startX = pos.getX();
		this.startY = pos.getY();
		this.startZ = pos.getZ();
	}

	protected abstract void generate(World world);
	
	public abstract boolean canGrow(World world, BlockPos pos);

	protected abstract TreeBlockTypeLeaf getLeaf(GameProfile owner);

	protected abstract ITreeBlockType getWood();

	protected List<BlockPos> generateTreeTrunk(World world, int height, int girth) {
		return generateTreeTrunk(world, height, girth, 0);
	}

	protected List<BlockPos> generateTreeTrunk(World world, int height, int girth, float vinesChance) {
		return generateTreeTrunk(world, height, girth, vinesChance, null, 0);
	}

	protected List<BlockPos> generateTreeTrunk(World world, int height, int girth, float vinesChance, EnumFacing leanDirection, float leanAmount) {
		return generateTreeTrunk(world, height, girth, 0, vinesChance, leanDirection, leanAmount);
	}

	/** Returns a list of trunk top coordinates */
	protected List<BlockPos> generateTreeTrunk(World world, int height, int girth, int yStart, float vinesChance, EnumFacing leanDirection, float leanAmount) {
		List<BlockPos> treeTops = new ArrayList<>();

		final int leanStartY = (int) Math.floor(height * 0.33f);
		int prevXOffset = 0;
		int prevZOffset = 0;
		
		int offsetX = 0;
		int offsetZ = 0;
		
		if(leanDirection != null){
			offsetX = leanDirection.getFrontOffsetX();
			offsetZ = leanDirection.getFrontOffsetZ();
		}
		
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
					int xOffset = (int) Math.floor(offsetX * lean - offset);
					int zOffset = (int) Math.floor(offsetZ * lean - offset);

					if (xOffset != prevXOffset || zOffset != prevZOffset) {
						prevXOffset = xOffset;
						prevZOffset = zOffset;
						if (y > 0) {
							wood.setDirection(leanDirection);
							addWood(world, new BlockPos(x + xOffset, y - 1, z + zOffset), EnumReplaceMode.ALL);
							wood.setDirection(EnumFacing.UP);
						}
					}

					addWood(world, new BlockPos(x + xOffset, y, z + zOffset), EnumReplaceMode.ALL);
					addVines(world, new BlockPos(x + xOffset, y, z + zOffset), vinesChance);

					if (y + 1 == height) {
						treeTops.add(new BlockPos(x + xOffset, y, z + zOffset));
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
		if (BlockUtil.isReplaceableBlock(world, new BlockPos(x, y, z)) || world.isAirBlock(new BlockPos(x, y, z))) {
			tree.trySpawnFruitBlock(world, new BlockPos(x, y, z));
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
						addWood(world, new BlockPos(x, i, z), EnumReplaceMode.SOFT);
					}
				}
			}
		}
	}

	protected List<BlockPos> generateBranches(World world, final int startY, int xOffset, int zOffset, float spreadY, float spreadXZ, int radius, int count) {
		return generateBranches(world, startY, xOffset, zOffset, spreadY, spreadXZ, radius, count, 1.0f);
	}

	protected List<BlockPos> generateBranches(World world, final int startY, final int xOffset, final int zOffset, final float spreadY, final float spreadXZ, int radius, final int count, final float chance) {
		List<BlockPos> branchEnds = new ArrayList<>();
		if (radius < 1) {
			radius = 1;
		}

		for (Direction cardinalDirection : Direction.values()) {
			EnumFacing branchDirection = cardinalDirection.facing;
			
			int offsetX = 0;
			int offsetZ = 0;
			
			if(branchDirection != null){
				offsetX = branchDirection.getFrontOffsetX();
				offsetZ = branchDirection.getFrontOffsetZ();
			}
			
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
							if (offsetX == 0) {
								if (world.rand.nextBoolean()) {
									x++;
								} else {
									x--;
								}
								wood.setDirection(EnumFacing.EAST);
							} else if (offsetZ == 0) {
								if (world.rand.nextBoolean()) {
									z++;
								} else {
									z--;
								}
								wood.setDirection(EnumFacing.SOUTH);
							}
						} else {
							x += offsetX;
							z += offsetZ;
							wood.setDirection(branchDirection);
						}
					}
					addWood(world, new BlockPos(x, y, z), EnumReplaceMode.SOFT);
					branchEnds.add(new BlockPos(x, y, z));
				}
			}
		}

		return branchEnds;
	}
	
	@Override
	protected void addBlock(World world, BlockPos pos, ITreeBlockType type, EnumReplaceMode replace) {
		BlockPos posN = getPos(pos);
		if (replace == EnumReplaceMode.ALL
				|| (replace == EnumReplaceMode.SOFT && BlockUtil.isReplaceableBlock(world, posN))
				|| world.isAirBlock(posN)) {
			type.setBlock(world, tree, posN);
		}
	}

	protected final void clearBlock(World world, BlockPos pos) {
		air.setBlock(world, getPos(pos));
	}

	protected final void addWood(World world, BlockPos pos, EnumReplaceMode replace) {
		addBlock(world, pos, wood, replace);
	}
	
	protected final void addLeaf(World world, BlockPos pos, EnumReplaceMode replace) {
		addBlock(world, pos, leaf, replace);
	}

	protected final void addVine(World world, int x, int y, int z, ITreeBlockType vine) {
		addBlock(world, new BlockPos(x, y, z), vine, EnumReplaceMode.NONE);
	}

	protected final void addVines(World world, BlockPos pos, float chance) {
		if (chance <= 0) {
			return;
		}

		if (world.rand.nextFloat() < chance) {
			addVine(world, pos.getX() - 1, pos.getY(), pos.getZ(), vineWest);
		}
		if (world.rand.nextFloat() < chance) {
			addVine(world, pos.getX() + 1, pos.getY(), pos.getZ(), vineEast);
		}
		if (world.rand.nextFloat() < chance) {
			addVine(world, pos.getX(), pos.getY(), pos.getZ() - 1, vineNorth);
		}
		if (world.rand.nextFloat() < chance) {
			addVine(world, pos.getX(), pos.getY(), pos.getZ() + 1, vineSouth);
		}
	}
	
	protected BlockPos getPos(BlockPos pos){
		return new BlockPos(startX + pos.getX(), startY + pos.getX(), startZ + pos.getZ());
	}

}
