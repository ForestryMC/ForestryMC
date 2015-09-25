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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.render.RenderOverlayBlock;
import forestry.plugins.PluginArboriculture;

/**
 * Ugly but serviceable renderer for leaves, taking fruits into account.
 */
public class RenderLeavesBlock extends RenderOverlayBlock {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		// uses IItemRenderer
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileLeaves tile = BlockForestryLeaves.getLeafTile(world, x, y, z);
		if (tile == null) {
			return false;
		}

		// Render the plain leaf block.
		renderer.renderStandardBlock(block, x, y, z);

		// Render overlay for fruit leaves.
		IIcon fruitIcon = tile.getFruitTexture();

		if (fruitIcon != null) {
			int fruitColor = tile.getFruitColour();
			renderFruitOverlay(world, block, x, y, z, renderer, fruitIcon, fruitColor);
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return PluginArboriculture.modelIdLeaves;
	}

	private static boolean renderFruitOverlay(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, IIcon texture, int multiplier) {

		float mR = (multiplier >> 16 & 255) / 255.0F;
		float mG = (multiplier >> 8 & 255) / 255.0F;
		float mB = (multiplier & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable) {
			mR = (mR * 30.0F + mG * 59.0F + mB * 11.0F) / 100.0F;
			mG = (mR * 30.0F + mG * 70.0F) / 100.0F;
			mB = (mR * 30.0F + mB * 70.0F) / 100.0F;
		}

		return renderFruitOverlayWithColorMultiplier(world, block, x, y, z, mR, mG, mB, renderer, texture);
	}

	private static boolean renderFruitOverlayWithColorMultiplier(IBlockAccess world, Block block, int x, int y, int z, float r, float g, float b,
			RenderBlocks renderer, IIcon texture) {

		int mixedBrightness = block.getMixedBrightnessForBlock(world, x, y, z);

		float adjR = 0.5f * r;
		float adjG = 0.5f * g;
		float adjB = 0.5f * b;

		// Bottom
		renderBottomFace(world, block, x, y, z, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderTopFace(world, block, x, y, z, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderEastFace(world, block, x, y, z, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderWestFace(world, block, x, y, z, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderNorthFace(world, block, x, y, z, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderSouthFace(world, block, x, y, z, renderer, texture, mixedBrightness, adjR, adjG, adjB);

		return true;
	}
}
