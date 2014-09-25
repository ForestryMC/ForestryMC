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

import forestry.api.arboriculture.ITree;
import forestry.arboriculture.gadgets.ForestryBlockLeaves;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.items.ItemLeavesBlock;
import forestry.core.proxy.Proxies;
import forestry.core.render.OverlayRenderingHandler;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginArboriculture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Ugly but serviceable renderer for leaves, taking fruits into account.
 */
public class LeavesRenderingHandler extends OverlayRenderingHandler implements IItemRenderer {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		// see IItemRenderer section below for inventory rendering
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		TileLeaves tile = ForestryBlockLeaves.getLeafTile(world, x, y, z);
		if (tile == null || tile.getTree() == null)
			return false;

		// Render the plain leaf block.
		renderer.renderStandardBlock(block, x, y, z);

		// Render overlay for fruit leaves.
		IIcon fruitIcon = null;
		int fruitColor = 0xffffff;
		if (tile != null) {
			fruitIcon = tile.getFruitTexture();
			fruitColor = tile.getFruitColour();
		}

		if (fruitIcon != null)
			renderFruitOverlay(world, block, x, y, z, renderer, fruitIcon, fruitColor);

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

	private boolean renderFruitOverlay(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, IIcon texture, int multiplier) {

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

	private boolean renderFruitOverlayWithColorMultiplier(IBlockAccess world, Block block, int x, int y, int z, float r, float g, float b,
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

	/* IItemRenderer */
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
				renderLeafBlock((RenderBlocks) data[0], item, -0.5f, -0.5f, -0.5f);
				break;
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
				renderLeafBlock((RenderBlocks) data[0], item, 0f, 0.0f, 0.0f);
				break;
			case INVENTORY:
				renderLeafBlock((RenderBlocks) data[0], item, -0.5f, -0.5f, -0.5f);
				break;
			default:
		}
	}

	private ITree getTree(ItemStack itemStack) {
		return new Tree(itemStack.getTagCompound());
	}

	private void renderLeafBlock(RenderBlocks renderer, ItemStack itemStack, float x, float y, float z) {
		Tessellator tessellator = Tessellator.instance;
		Block block = StackUtils.getBlock(itemStack);

		if (!(itemStack.getItem() instanceof ItemLeavesBlock) || !itemStack.hasTagCompound())
			return;

		ITree tree = getTree(itemStack);
		if (tree == null)
			return;

		TileLeaves leaves = new TileLeaves();
		leaves.setTree(tree);

		IIcon leavesIcon = leaves.getIcon(Proxies.render.fancyGraphicsEnabled());
		int color = leaves.determineFoliageColour();

		float r1 = (float)(color >> 16 & 255) / 255.0F;
		float g1 = (float)(color >> 8 & 255) / 255.0F;
		float b1 = (float)(color & 255) / 255.0F;
		GL11.glColor4f(r1, g1, b1, 1.0F);

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
		if (!leaves.hasFruit())
			return;

		leaves.addRipeness(20);
		int fruitColor = leaves.determineFruitColour();
		IIcon fruitTexture = leaves.getFruitTexture();
		if (fruitTexture == null)
			return;

		float r2 = (float)(fruitColor >> 16 & 255) / 255.0F;
		float g2 = (float)(fruitColor >> 8 & 255) / 255.0F;
		float b2 = (float)(fruitColor & 255) / 255.0F;
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
