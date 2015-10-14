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

import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.ITreeModifier;
import forestry.api.arboriculture.TreeManager;
import forestry.api.world.ITreeGenData;

public abstract class WorldGenTree extends WorldGenArboriculture {
	private static final int minHeight = 4;
	private static final int maxHeight = 80;

	private final int baseHeight;
	private final int heightVariation;

	protected int girth;
	protected int height;

	protected WorldGenTree(ITreeGenData tree, int baseHeight, int heightVariation) {
		super(tree);
		this.baseHeight = baseHeight;
		this.heightVariation = heightVariation;
	}

	@Override
	public void generate(World world) {
		generateTreeTrunk(world, height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(world, leafSpawn--, 0, 1, leaf, EnumReplaceMode.NONE);
		generateAdjustedCylinder(world, leafSpawn--, 0.5f, 1, leaf, EnumReplaceMode.NONE);

		generateAdjustedCylinder(world, leafSpawn--, 1.9f, 1, leaf, EnumReplaceMode.NONE);
		generateAdjustedCylinder(world, leafSpawn--, 1.9f, 1, leaf, EnumReplaceMode.NONE);
	}

	protected Vector getCenteredAt(int yCenter, int xOffset, int zOffset) {
		float cent = girth % 2 == 0 ? 0.5f : 0f;
		return new Vector(cent + xOffset, yCenter, cent + zOffset);
	}

	protected void generateAdjustedCylinder(World world, int yCenter, float radius, int height, ITreeBlockType block) {
		generateAdjustedCylinder(world, yCenter, 0, 0, radius, height, block, EnumReplaceMode.SOFT);
	}

	protected void generateAdjustedCylinder(World world, int yCenter, float radius, int height, ITreeBlockType block, EnumReplaceMode replace) {
		generateAdjustedCylinder(world, yCenter, 0, 0, radius, height, block, replace);
	}

	protected void generateAdjustedCylinder(World world, int yCenter, int xOffset, int zOffset, float radius, int height, ITreeBlockType block, EnumReplaceMode replace) {
		Vector center = getCenteredAt(yCenter, xOffset, zOffset);
		generateCylinder(world, center, radius + girth, height, block, replace);
	}

	protected void generateAdjustedCircle(World world, int yCenter, int xOffset, int zOffset, float radius, int width, int height, ITreeBlockType block, float chance, EnumReplaceMode replace) {
		Vector center = getCenteredAt(yCenter, xOffset, zOffset);
		generateCircle(world, center, radius, width, height, block, chance, replace);
	}

	@Override
	public boolean canGrow(World world, int x, int y, int z) {
		return tree.canGrow(world, x, y, z, girth, height);
	}

	@Override
	public final void preGenerate(World world, int startX, int startY, int startZ) {
		super.preGenerate(world, startX, startY, startZ);
		height = determineHeight(world, baseHeight, heightVariation);
		girth = tree.getGirth(world, startX, startY, startZ);
	}

	protected int modifyByHeight(World world, int val, int min, int max) {
		ITreeModifier treeModifier = TreeManager.treeRoot.getTreekeepingMode(world);
		int determined = Math.round(val * tree.getHeightModifier() * treeModifier.getHeightModifier(tree.getGenome(), 1f));
		return determined < min ? min : determined > max ? max : determined;
	}

	private int determineHeight(World world, int required, int variation) {
		ITreeModifier treeModifier = TreeManager.treeRoot.getTreekeepingMode(world);
		int baseHeight = required + world.rand.nextInt(variation);
		int height = Math.round(baseHeight * tree.getHeightModifier() * treeModifier.getHeightModifier(tree.getGenome(), 1f));
		return height < minHeight ? minHeight : height > maxHeight ? maxHeight : height;
	}

	@Override
	public TreeBlockTypeLeaf getLeaf(GameProfile owner) {
		return new TreeBlockTypeLeaf(owner);
	}

	@Override
	public ITreeBlockType getWood() {
		return new TreeBlockTypeLog();
	}

}
