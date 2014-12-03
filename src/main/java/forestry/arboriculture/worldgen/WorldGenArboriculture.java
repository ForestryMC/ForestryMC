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
import net.minecraft.tileentity.TileEntity;

import com.mojang.authlib.GameProfile;

import forestry.api.world.ITreeGenData;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;
import forestry.core.worldgen.BlockType;
import forestry.core.worldgen.WorldGenBase;

public abstract class WorldGenArboriculture extends WorldGenBase {

	private static final BlockType vine = new BlockType(Blocks.vine, 0);
	private static final BlockType air = new BlockTypeVoid();

	protected final ITreeGenData tree;
	protected int startX;
	protected int startY;
	protected int startZ;

	protected BlockType leaf;
	protected BlockType wood;

	protected boolean spawnPods = false;
	protected int minPodHeight = 3;

	public WorldGenArboriculture(ITreeGenData tree) {
		this.tree = tree;
	}

	@Override
	public boolean subGenerate(int x, int y, int z) {
		this.startX = x;
		this.startY = y;
		this.startZ = z;

		this.spawnPods = tree.allowsFruitBlocks();
		this.leaf = getLeaf(getOwner());
		this.wood = getWood();

		preGenerate();
		if (!canGrow())
			return false;
		else {
			generate();
			return true;
		}

	}

	private GameProfile getOwner() {
		TileEntity tile = world.getTileEntity(startX, startY, startZ);
		if (tile instanceof TileSapling)
			return ((TileSapling) tile).getOwnerProfile();
		else
			return null;
	}

	public abstract void preGenerate();

	public abstract void generate();

	public abstract boolean canGrow();

	public abstract BlockType getLeaf(GameProfile owner);

	public abstract BlockType getWood();

	public final Vect getStartVector() {
		return new Vect(startX, startY, startZ);
	}

	protected void generateTreeTrunk(int height, int girth) {
		generateTreeTrunk(height, girth, 0);
	}

	protected void generateTreeTrunk(int height, int girth, float vines) {
		int offset = (girth - 1) / 2;
		for (int x = 0; x < girth; x++)
			for (int y = 0; y < girth; y++)
				for (int i = 0; i < height; i++) {
					addWood(x - offset, i, y - offset, EnumReplaceMode.ALL);

					if (rand.nextFloat() < vines)
						addVine(x - offset - 1, i, y - offset);
					if (rand.nextFloat() < vines)
						addVine(x - offset + 1, i, y - offset);
					if (rand.nextFloat() < vines)
						addVine(x - offset, i, y - offset - 1);
					if (rand.nextFloat() < vines)
						addVine(x - offset, i, y - offset + 1);
				}

		if (!spawnPods)
			return;

		for (int y = minPodHeight; y < height; y++)
			for (int x = 0; x < girth; x++)
				for (int z = 0; z < girth; z++) {

					if ((x > 0 && x < girth) && (z > 0 && z < girth))
						continue;

					tree.trySpawnFruitBlock(world, startX + x + 1, startY + y, startZ + z);
					tree.trySpawnFruitBlock(world, startX + x - 1, startY + y, startZ + z);
					tree.trySpawnFruitBlock(world, startX + x, startY + y, startZ + z + 1);
					tree.trySpawnFruitBlock(world, startX + x, startY + y, startZ + z - 1);
				}

	}

	protected void generateSupportStems(int height, int girth, float chance, float maxHeight) {

		int offset = 1;

		for (int x = -offset; x < girth + offset; x++)
			for (int z = -offset; z < girth + offset; z++) {

				if (x == -offset && z == -offset)
					continue;
				if (x == girth + offset && z == girth + offset)
					continue;
				if (x == -offset && z == girth + offset)
					continue;
				if (x == girth + offset && z == -offset)
					continue;

				int stemHeight = rand.nextInt(Math.round(height * maxHeight));
				if (rand.nextFloat() < chance)
					for (int i = 0; i < stemHeight; i++)
						addWood(x, i, z, EnumReplaceMode.SOFT);
			}

	}

	@Override
	protected void addBlock(int x, int y, int z, BlockType type, EnumReplaceMode replace) {
		if (replace == EnumReplaceMode.ALL
				|| replace == EnumReplaceMode.SOFT && Utils.isReplaceableBlock(world, startX + x, startY + y, startZ + z)
				|| world.isAirBlock(startX + x, startY + y, startZ + z))
			type.setBlock(world, tree, startX + x, startY + y, startZ + z);
	}

	protected final void clearBlock(int x, int y, int z) {
		air.setBlock(world, tree, startX + x, startY + y, startZ + z);
	}

	protected final void addWood(int x, int y, int z, EnumReplaceMode replace) {
		addBlock(x, y, z, wood, replace);
	}

	protected final void addLeaf(int x, int y, int z, EnumReplaceMode replace) {
		addBlock(x, y, z, leaf, replace);
	}

	protected final void addVine(int x, int y, int z) {
		addBlock(x, y, z, vine, EnumReplaceMode.NONE);
	}

}
