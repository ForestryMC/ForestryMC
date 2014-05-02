/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.render;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import forestry.core.ForestryClient;
import forestry.core.interfaces.IBlockRenderer;

public class BlockRenderingHandler implements ISimpleBlockRenderingHandler {

	public static HashMap<TileRendererIndex, IBlockRenderer> byBlockRenderer = new HashMap<TileRendererIndex, IBlockRenderer>();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		if (block.getRenderType() == ForestryClient.byBlockModelId) {
			TileRendererIndex index = new TileRendererIndex(block, metadata);
			if (byBlockRenderer.containsKey(index))
				byBlockRenderer.get(index).inventoryRender(-0.5, -0.5, -0.5, 0, 0);
		}

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return ForestryClient.byBlockModelId;
	}

}
