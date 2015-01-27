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
package forestry.core.render;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;

import forestry.core.ForestryClient;
import forestry.core.interfaces.IBlockRenderer;

public class BlockRenderingHandler implements ISimpleBlockRenderingHandler {

	public static final HashMap<TileRendererIndex, IBlockRenderer> byBlockRenderer = new HashMap<TileRendererIndex, IBlockRenderer>();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		if (block.getRenderType() == ForestryClient.byBlockModelId) {
			TileRendererIndex index = new TileRendererIndex(block, metadata);
			if (byBlockRenderer.containsKey(index)) {
				byBlockRenderer.get(index).inventoryRender(-0.5, -0.5, -0.5, 0, 0);
			}
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
