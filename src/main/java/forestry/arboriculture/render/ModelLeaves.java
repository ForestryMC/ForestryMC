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
package forestry.arboriculture.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.models.ModelBlockOverlay;
import forestry.core.proxy.Proxies;

public class ModelLeaves extends ModelBlockOverlay<BlockForestryLeaves> {

	public ModelLeaves() {
		super(BlockForestryLeaves.class);
	}

	@Override
	public void renderInventory(BlockForestryLeaves block, ItemStack itemStack, IModelBaker baker) {
		if (!(itemStack.getItem() instanceof ItemBlockLeaves) || block == null) {
			return;
		}

		TileLeaves leaves = new TileLeaves();
		if (itemStack.hasTagCompound()) {
			leaves.readFromNBT(itemStack.getTagCompound());
		} else {
			leaves.setTree(TreeHelper.treeTemplates.get(0));
		}

		TextureAtlasSprite leavesIcon = leaves.getLeaveSprite(Proxies.render.fancyGraphicsEnabled());
		if (leavesIcon == null) {
			return;
		}
		baker.setColorIndex(0);

		baker.renderFaceYNeg(null, leavesIcon);
		baker.renderFaceYPos(null, leavesIcon);
		baker.renderFaceZNeg(null, leavesIcon);
		baker.renderFaceZPos(null, leavesIcon);
		baker.renderFaceXNeg(null, leavesIcon);
		baker.renderFaceXPos(null, leavesIcon);

		// add fruit
		if (!leaves.hasFruit()) {
			return;
		}

		TextureAtlasSprite fruitTexture = leaves.getFruitSprite();
		if (fruitTexture == null) {
			return;
		}
		baker.setColorIndex(1);

		baker.renderFaceYNeg(null, fruitTexture);
		baker.renderFaceYPos(null, fruitTexture);
		baker.renderFaceZNeg(null, fruitTexture);
		baker.renderFaceZPos(null, fruitTexture);
		baker.renderFaceXNeg(null, fruitTexture);
		baker.renderFaceXPos(null, fruitTexture);
	}

	@Override
	public boolean renderInWorld(BlockForestryLeaves block, IBlockAccess world, BlockPos pos, IModelBaker baker) {
		TileLeaves tile = BlockForestryLeaves.getLeafTile(world, pos);
		if (tile == null) {
			return false;
		}

		// Render the plain leaf block.
		baker.renderStandardBlock(block, pos, tile.getLeaveSprite(Proxies.render.fancyGraphicsEnabled()), 0);

		// Render overlay for fruit leaves.
		TextureAtlasSprite fruitSprite = tile.getFruitSprite();

		if (fruitSprite != null) {
			renderFruitOverlay(world, block, pos, baker, fruitSprite, 1);
		}

		return true;
	}

	private boolean renderFruitOverlay(IBlockAccess world, BlockForestryLeaves block, BlockPos pos, IModelBaker baker, TextureAtlasSprite sprite, int colorIndex) {
		// Bottom
		renderBottomFace(world, block, pos, baker, sprite, colorIndex);
		renderTopFace(world, block, pos, baker, sprite, colorIndex);
		renderEastFace(world, block, pos, baker, sprite, colorIndex);
		renderWestFace(world, block, pos, baker, sprite, colorIndex);
		renderNorthFace(world, block, pos, baker, sprite, colorIndex);
		renderSouthFace(world, block, pos, baker, sprite, colorIndex);

		return true;
	}
}
