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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import forestry.core.render.RenderOverlayBlock;
import forestry.farming.blocks.BlockFarm;
import forestry.plugins.PluginFarming;

public class RenderFarmBlock extends RenderOverlayBlock {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		// Render the plain block.
		renderer.renderStandardBlock(block, x, y, z);
		renderFarmOverlay(world, PluginFarming.blocks.farm, x, y, z, renderer);

		return true;
	}

	private static void renderFarmOverlay(IBlockAccess world, BlockFarm block, int x, int y, int z, RenderBlocks renderer) {
		int mixedBrightness = block.getMixedBrightnessForBlock(world, x, y, z);
		int metadata = world.getBlockMetadata(x, y, z);

		float adjR = 0.5f;
		float adjG = 0.5f;
		float adjB = 0.5f;

		renderBottomFace(world, block, x, y, z, renderer, BlockFarm.getOverlayTextureForBlock(0, metadata), mixedBrightness, adjR, adjG, adjB);
		renderTopFace(world, block, x, y, z, renderer, BlockFarm.getOverlayTextureForBlock(1, metadata), mixedBrightness, adjR, adjG, adjB);
		renderEastFace(world, block, x, y, z, renderer, BlockFarm.getOverlayTextureForBlock(2, metadata), mixedBrightness, adjR, adjG, adjB);
		renderWestFace(world, block, x, y, z, renderer, BlockFarm.getOverlayTextureForBlock(3, metadata), mixedBrightness, adjR, adjG, adjB);
		renderNorthFace(world, block, x, y, z, renderer, BlockFarm.getOverlayTextureForBlock(4, metadata), mixedBrightness, adjR, adjG, adjB);
		renderSouthFace(world, block, x, y, z, renderer, BlockFarm.getOverlayTextureForBlock(5, metadata), mixedBrightness, adjR, adjG, adjB);
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
