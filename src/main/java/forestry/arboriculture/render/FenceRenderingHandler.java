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
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.client.IItemRenderer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import org.lwjgl.opengl.GL11;

import forestry.arboriculture.WoodType;
import forestry.plugins.PluginArboriculture;

public class FenceRenderingHandler implements ISimpleBlockRenderingHandler, IItemRenderer {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		// uses IItemRenderer
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

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch (type) {
			case ENTITY:
			case EQUIPPED_FIRST_PERSON:
			case EQUIPPED:
			case INVENTORY:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
			case ENTITY:
				renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f, true);
				break;
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
				renderItem((RenderBlocks) data[0], item, 0.5f, 0.5f, 0.5f, true);
				break;
			case INVENTORY:
				renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f, false);
				break;
			default:
		}
	}

	private static void renderItem(RenderBlocks renderer, ItemStack itemStack, float x, float y, float z, boolean fullBlock) {
		Tessellator tess = Tessellator.instance;

		WoodType woodType = WoodType.getFromCompound(itemStack.getTagCompound());
		IIcon plankIcon = woodType.getPlankIcon();
		Block block = Block.getBlockFromItem(itemStack.getItem());

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
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, plankIcon);
			tess.draw();

			tess.startDrawingQuads();
			tess.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, plankIcon);
			tess.draw();

			tess.startDrawingQuads();
			tess.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, plankIcon);
			tess.draw();

			tess.startDrawingQuads();
			tess.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, plankIcon);
			tess.draw();

			tess.startDrawingQuads();
			tess.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, plankIcon);
			tess.draw();

			tess.startDrawingQuads();
			tess.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, plankIcon);
			tess.draw();

			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}

		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderer.setRenderBoundsFromBlock(block);
	}
}
