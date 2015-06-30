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
package forestry.farming.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import forestry.core.config.ForestryBlock;
import forestry.core.render.OverlayRenderingHandler;
import forestry.farming.multiblock.BlockFarm;
import forestry.plugins.PluginFarming;

public class FarmRenderingHandler extends OverlayRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		// Render the plain block.
		renderer.renderStandardBlock(block, x, y, z);
		renderFarmOverlay(world, (BlockFarm) ForestryBlock.farm.block(), x, y, z, renderer, 0xffffff);

		return true;
	}

	private boolean renderFarmOverlay(IBlockAccess world, BlockFarm block, int x, int y, int z, RenderBlocks renderer, int multiplier) {

		float mR = (multiplier >> 16 & 255) / 255.0F;
		float mG = (multiplier >> 8 & 255) / 255.0F;
		float mB = (multiplier & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable) {
			mR = (mR * 30.0F + mG * 59.0F + mB * 11.0F) / 100.0F;
			mG = (mR * 30.0F + mG * 70.0F) / 100.0F;
			mB = (mR * 30.0F + mB * 70.0F) / 100.0F;
		}

		return renderFarmOverlayWithColourMultiplier(world, block, x, y, z, mR, mG, mB, renderer);
	}

	private boolean renderFarmOverlayWithColourMultiplier(IBlockAccess world, BlockFarm block, int x, int y, int z, float r, float g, float b,
			RenderBlocks renderer) {

		int mixedBrightness = block.getMixedBrightnessForBlock(world, x, y, z);
		int metadata = world.getBlockMetadata(x, y, z);

		float adjR = 0.5f * r;
		float adjG = 0.5f * g;
		float adjB = 0.5f * b;

		// Bottom
		renderBottomFace(world, block, x, y, z, renderer, block.getOverlayTextureForBlock(0, metadata), mixedBrightness, adjR, adjG, adjB);
		renderTopFace(world, block, x, y, z, renderer, block.getOverlayTextureForBlock(1, metadata), mixedBrightness, adjR, adjG, adjB);
		renderEastFace(world, block, x, y, z, renderer, block.getOverlayTextureForBlock(2, metadata), mixedBrightness, adjR, adjG, adjB);
		renderWestFace(world, block, x, y, z, renderer, block.getOverlayTextureForBlock(3, metadata), mixedBrightness, adjR, adjG, adjB);
		renderNorthFace(world, block, x, y, z, renderer, block.getOverlayTextureForBlock(4, metadata), mixedBrightness, adjR, adjG, adjB);
		renderSouthFace(world, block, x, y, z, renderer, block.getOverlayTextureForBlock(5, metadata), mixedBrightness, adjR, adjG, adjB);

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return PluginFarming.modelIdFarmBlock;
	}

}
