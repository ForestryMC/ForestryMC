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

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import forestry.core.utils.ItemStackUtil;
import forestry.farming.blocks.BlockFarm;

public class RenderFarmItem implements IItemRenderer {

	private static void renderFarmBlock(RenderBlocks render, ItemStack item, float translateX, float translateY, float translateZ) {
		Tessellator tessellator = Tessellator.instance;
		BlockFarm block = (BlockFarm) ItemStackUtil.getBlock(item);
		if (block == null) {
			return;
		}

		block.setBlockBoundsForItemRender();
		render.setRenderBoundsFromBlock(block);

		EnumFarmBlockTexture type = EnumFarmBlockTexture.getFromCompound(item.getTagCompound());

		GL11.glTranslatef(translateX, translateY, translateZ);

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		render.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, BlockFarm.getBlockTextureForSide(type, 0));
		render.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, BlockFarm.getOverlayTextureForBlock(0, item.getItemDamage()));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		render.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, BlockFarm.getBlockTextureForSide(type, 1));
		render.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, BlockFarm.getOverlayTextureForBlock(1, item.getItemDamage()));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		render.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, BlockFarm.getBlockTextureForSide(type, 2));
		render.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, BlockFarm.getOverlayTextureForBlock(2, item.getItemDamage()));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		render.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, BlockFarm.getBlockTextureForSide(type, 3));
		render.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, BlockFarm.getOverlayTextureForBlock(3, item.getItemDamage()));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		render.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, BlockFarm.getBlockTextureForSide(type, 4));
		render.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, BlockFarm.getOverlayTextureForBlock(4, item.getItemDamage()));
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		render.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, BlockFarm.getBlockTextureForSide(type, 5));
		render.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, BlockFarm.getOverlayTextureForBlock(5, item.getItemDamage()));
		tessellator.draw();

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		switch (type) {
			case ENTITY:
				renderFarmBlock((RenderBlocks) data[0], item, -0.5f, -0.5f, -0.5f);
				break;
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
				renderFarmBlock((RenderBlocks) data[0], item, 0f, 0f, 0f);
				break;
			case INVENTORY:
				renderFarmBlock((RenderBlocks) data[0], item, -0.5f, -0.5f, -0.5f);
				break;
			default:
		}

		GL11.glPopAttrib();
	}

}
