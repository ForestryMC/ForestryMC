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

import forestry.api.arboriculture.EnumWoodType;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.core.utils.ItemStackUtil;

public class RenderStairItem implements IItemRenderer {

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
				renderStairBlock((RenderBlocks) data[0], item, 0f, 0f, 0f);
				break;
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
				renderStairBlock((RenderBlocks) data[0], item, 0.5f, 0.5f, 0.5f);
				break;
			case INVENTORY:
				renderStairBlock((RenderBlocks) data[0], item, 0f, 0f, 0f);
				break;
			default:
		}
	}

	private static void renderStairBlock(RenderBlocks renderBlocks, ItemStack itemStack, float x, float y, float z) {

		Tessellator tessellator = Tessellator.instance;
		Block block = ItemStackUtil.getBlock(itemStack);

		if (!(itemStack.getItem() instanceof ItemBlockWood) || block == null) {
			return;
		}

		EnumWoodType woodType = ItemBlockWood.getWoodType(itemStack);
		IIcon texture = IconProviderWood.getPlankIcon(woodType);

		for (int i = 0; i < 2; ++i) {
			if (i == 0) {
				renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
			}

			if (i == 1) {
				renderBlocks.setRenderBounds(0.0D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
			}

			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderBlocks.renderFaceYNeg(block, x, y, z, texture);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderBlocks.renderFaceYPos(block, x, y, z, texture);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderBlocks.renderFaceZNeg(block, x, y, z, texture);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderBlocks.renderFaceZPos(block, x, y, z, texture);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderBlocks.renderFaceXNeg(block, x, y, z, texture);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderBlocks.renderFaceXPos(block, x, y, z, texture);
			tessellator.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}
	}

}
