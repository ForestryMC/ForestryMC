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

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.world.ITreeGenData;
import forestry.arboriculture.gadgets.TileTreeContainer;
import forestry.core.utils.Utils;
import forestry.core.worldgen.BlockType;
import forestry.core.worldgen.BlockTypeVoid;
import forestry.core.worldgen.WorldGenBase;

public abstract class WorldGenArboriculture extends WorldGenBase {

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
		if (forced || canGrow(world, pos.getX(), pos.getY(), pos.getZ())) {
			generate(world);
			return true;
		}

		return false;
	}

	private static GameProfile getOwner(World world, BlockPos pos) {
		TileTreeContainer tile = Utils.getTile(world, pos, TileTreeContainer.class);
		if (tile == null) {
			return null;
		}
		return tile.getOwner();
	}

	public void preGenerate(World world, BlockPos statePos) {
		this.startX = statePos.getX();
		this.startY = statePos.getY();
		this.startZ = statePos.getZ();
	}

	protected abstract void generate(World world);

	public abstract boolean canGrow(World world, int x, int y, int z);

	protected abstract TreeBlockTypeLeaf getLeaf(GameProfile owner);

	protected abstract ITreeBlockType getWood();

	protected void generateTreeTrunk(World world, int height, int girth) {
		generateTreeTrunk(world, height, girth, 0);
	}

	protected void generateTreeTrunk(World world, int height, int girth, float vines) {
		int offset = (girth - 1) / 2;
		for (int x = 0; x < girth; x++) {
			for (int z = 0; z < girth; z++) {
				for (int y = 0; y < height; y++) {
					addWood(world, x - offset, y, z - offset, EnumReplaceMode.ALL);
					addVines(world, x - offset, y, z - offset, vines);
				}
			}
		}

		if (!spawnPods) {
			return;
		}

		generatePods(world, height, girth);

	}

	protected void generatePods(World world, int height, int girth) {
		for (int y = minPodHeight; y < height; y++) {
			for (int x = 0; x < girth; x++) {
				for (int z = 0; z < girth; z++) {

					if ((x > 0 && x < girth) && (z > 0 && z < girth)) {
						continue;
					}

					tree.trySpawnFruitBlock(world, new BlockPos(startX + x + 1, startY + y, startZ + z));
					tree.trySpawnFruitBlock(world, new BlockPos(startX + x - 1, startY + y, startZ + z));
					tree.trySpawnFruitBlock(world, new BlockPos(startX + x, startY + y, startZ + z + 1));
					tree.trySpawnFruitBlock(world, new BlockPos(startX + x, startY + y, startZ + z - 1));
				}
			}
		}
	}

	protected void generateSupportStems(World world, int height, int girth, float chance, float maxHeight) {

		int offset = 1;

		for (int x = -offset; x < girth + offset; x++) {
			for (int z = -offset; z < girth + offset; z++) {

				if (x == -offset && z == -offset) {
					continue;
				}
				if (x == girth + offset && z == girth + offset) {
					continue;
				}
				if (x == -offset && z == girth + offset) {
					continue;
				}
				if (x == girth + offset && z == -offset) {
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

	@Override
	protected void addBlock(World world, int x, int y, int z, ITreeBlockType type, EnumReplaceMode replace) {
		if (replace == EnumReplaceMode.ALL
				|| (replace == EnumReplaceMode.SOFT && Utils.isReplaceableBlock(world, new BlockPos(startX + x, startY + y, startZ + z)))
				|| world.isAirBlock(new BlockPos(startX + x, startY + y, startZ + z))) {
			type.setBlock(world, tree, new BlockPos(startX + x, startY + y, startZ + z));
		}
	}

	protected final void clearBlock(World world, int x, int y, int z) {
		air.setBlock(world, new BlockPos(startX + x, startY + y, startZ + z));
	}

	protected final void addWood(World world, int x, int y, int z, EnumReplaceMode replace) {
		addBlock(world, x, y, z, wood, replace);
	}
	
	protected final void addLeaf(World world, int x, int y, int z, EnumReplaceMode replace) {
		addBlock(world, x, y, z, leaf, replace);
	}

	protected final void addVine(World world, int x, int y, int z, ITreeBlockType vine) {
		addBlock(world, x, y, z, vine, EnumReplaceMode.NONE);
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
