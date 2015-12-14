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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;

public class RenderLeavesItem implements IItemRenderer {
	protected static final double OVERLAY_SHIFT = 0.001;

	@Override
	public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
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
	public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		switch (type) {
			case ENTITY:
				renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f);
				break;
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
				renderItem((RenderBlocks) data[0], item, 0.5f, 0.5f, 0.5f);
				break;
			case INVENTORY:
				renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f);
				break;
			default:
		}

		GL11.glPopAttrib();
	}

	private static void renderItem(RenderBlocks renderer, ItemStack itemStack, float x, float y, float z) {
		Tessellator tessellator = Tessellator.instance;
		Block block = ItemStackUtil.getBlock(itemStack);

		if (!(itemStack.getItem() instanceof ItemBlockLeaves) || block == null) {
			return;
		}

		TileLeaves leaves = new TileLeaves();
		if (itemStack.hasTagCompound()) {
			leaves.readFromNBT(itemStack.getTagCompound());
		} else {
			leaves.setTree(TreeHelper.treeTemplates.get(0));
		}

		IIcon leavesIcon = leaves.getIcon(Proxies.render.fancyGraphicsEnabled());
		if (leavesIcon == null) {
			return;
		}
		int color = leaves.getFoliageColour(Proxies.common.getPlayer());

		float r1 = (float) (color >> 16 & 255) / 255.0F;
		float g1 = (float) (color >> 8 & 255) / 255.0F;
		float b1 = (float) (color & 255) / 255.0F;
		GL11.glColor4f(r1, g1, b1, 1.0F);

		GL11.glTranslatef(x, y, z);

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, leavesIcon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, leavesIcon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, leavesIcon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, leavesIcon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, leavesIcon);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, leavesIcon);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);


		// add fruit
		if (!leaves.hasFruit()) {
			return;
		}

		IIcon fruitTexture = leaves.getFruitTexture();
		if (fruitTexture == null) {
			return;
		}
		int fruitColor = leaves.getFruitColour();

		float r2 = (float) (fruitColor >> 16 & 255) / 255.0F;
		float g2 = (float) (fruitColor >> 8 & 255) / 255.0F;
		float b2 = (float) (fruitColor & 255) / 255.0F;
		GL11.glColor4f(r2, g2, b2, 1.0F);

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D - OVERLAY_SHIFT, 0.0D, fruitTexture);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D + OVERLAY_SHIFT, 0.0D, fruitTexture);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D - OVERLAY_SHIFT, fruitTexture);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D + OVERLAY_SHIFT, fruitTexture);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D - OVERLAY_SHIFT, 0.0D, 0.0D, fruitTexture);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D + OVERLAY_SHIFT, 0.0D, 0.0D, fruitTexture);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
}
