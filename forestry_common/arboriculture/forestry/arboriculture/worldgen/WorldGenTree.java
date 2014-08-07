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

import com.mojang.authlib.GameProfile;

import forestry.api.world.ITreeGenData;
import forestry.core.worldgen.BlockType;
import forestry.plugins.PluginArboriculture;

public abstract class WorldGenTree extends WorldGenArboriculture {

	protected int girth;
	protected int height;

	protected int minHeight = 4;
	protected int maxHeight = 80;

	public WorldGenTree(ITreeGenData tree) {
		super(tree);
	}

	@Override
	public void generate() {
		generateTreeTrunk(height, girth);

		int leafSpawn = height + 1;

		generateAdjustedCylinder(leafSpawn--, 0, 1, leaf, EnumReplaceMode.NONE);
		generateAdjustedCylinder(leafSpawn--, 0.5f, 1, leaf, EnumReplaceMode.NONE);

		generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf, EnumReplaceMode.NONE);
		generateAdjustedCylinder(leafSpawn--, 1.9f, 1, leaf, EnumReplaceMode.NONE);

	}

	protected Vector getCenteredAt(int yCenter, int offset) {
		float cent = girth % 2 == 0 ? 0.5f : 0f;
		return new Vector(cent + offset, yCenter, cent + offset);
	}

	protected void generateAdjustedCylinder(int yCenter, float radius, int height, BlockType block) {
		generateAdjustedCylinder(yCenter, 0, radius, height, block, EnumReplaceMode.NONE);
	}

	protected void generateAdjustedCylinder(int yCenter, float radius, int height, BlockType block, EnumReplaceMode replace) {
		generateAdjustedCylinder(yCenter, 0, radius, height, block, replace);
	}

	protected void generateAdjustedCylinder(int yCenter, int offset, float radius, int height, BlockType block, EnumReplaceMode replace) {
		generateCylinder(getCenteredAt(yCenter, offset), radius + girth, height, block, replace);
	}

	@Override
	public boolean canGrow() {
		return tree.canGrow(world, startX, startY, startZ, tree.getGirth(world, startX, startY, startZ), height);

	}

	@Override
	public void preGenerate() {
		height = determineHeight(5, 2);
		girth = determineGirth(tree.getGirth(world, startX, startY, startZ));
	}

	protected int determineGirth(int base) {
		return base;
	}

	protected int modifyByHeight(int val, int min, int max) {
		int determined = Math.round(val * tree.getHeightModifier() * PluginArboriculture.treeInterface.getTreekeepingMode(world).getHeightModifier(null, 1f));
		return determined < min ? min : determined > max ? max : determined;
	}

	// TODO: Get access to the tree genome for the treekeeping mode
	protected int determineHeight(int required, int variation) {
		int determined = Math.round((required + rand.nextInt(variation)) * tree.getHeightModifier()
				* PluginArboriculture.treeInterface.getTreekeepingMode(world).getHeightModifier(null, 1f));
		return determined < minHeight ? minHeight : determined > maxHeight ? maxHeight : determined;
	}

	@Override
	public BlockType getLeaf(GameProfile owner) {
		return new BlockTypeLeaf(owner);
	}

	@Override
	public abstract BlockType getWood();

}
