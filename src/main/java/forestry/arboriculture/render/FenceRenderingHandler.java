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
import net.minecraft.block.BlockFence;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import org.lwjgl.opengl.GL11;

import forestry.plugins.PluginArboriculture;

public class FenceRenderingHandler implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

		Tessellator tess = Tessellator.instance;

		for (int i = 0; i < 4; ++i) {

			float thickness = 0.125F;

			if (i == 0) {
				block.setBlockBounds(0.5F - thickness, 0.0F, 0.0F, 0.5F + thickness, 1.0F, thickness * 2.0F);
			}

			if (i == 1) {
				block.setBlockBounds(0.5F - thickness, 0.0F, 1.0F - thickness * 2.0F, 0.5F + thickness, 1.0F, 1.0F);
			}

			thickness = 0.0625F;

			if (i == 2) {
				block.setBlockBounds(0.5F - thickness, 1.0F - thickness * 3.0F, -thickness * 2.0F, 0.5F + thickness, 1.0F - thickness, 1.0F + thickness * 2.0F);
			}

			if (i == 3) {
				block.setBlockBounds(0.5F - thickness, 0.5F - thickness * 3.0F, -thickness * 2.0F, 0.5F + thickness, 0.5F - thickness, 1.0F + thickness * 2.0F);
			}

			renderer.setRenderBoundsFromBlock(block);

			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			tess.startDrawingQuads();
			tess.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));
			tess.draw();
			tess.startDrawingQuads();
			tess.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));
			tess.draw();
			tess.startDrawingQuads();
			tess.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));
			tess.draw();
			tess.startDrawingQuads();
			tess.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));
			tess.draw();
			tess.startDrawingQuads();
			tess.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));
			tess.draw();
			tess.startDrawingQuads();
			tess.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
			tess.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}

		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderer.setRenderBoundsFromBlock(block);

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return renderer.renderBlockFence((BlockFence) block, x, y, z);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return PluginArboriculture.modelIdFences;
	}

}
