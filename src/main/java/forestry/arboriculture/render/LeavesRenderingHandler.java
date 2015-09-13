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
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;

import forestry.api.core.IModelRenderer;
import forestry.api.core.sprite.ISprite;
import forestry.arboriculture.gadgets.ForestryBlockLeaves;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.items.ItemLeavesBlock;
import forestry.core.proxy.Proxies;
import forestry.core.render.OverlayRenderingHandler;

/**
 * Ugly but serviceable renderer for leaves, taking fruits into account.
 */
public class LeavesRenderingHandler extends OverlayRenderingHandler {
	
	@Override
	public void renderInventory(Block block, ItemStack itemStack, IModelRenderer renderer, ItemRenderType type) {

		if (!(itemStack.getItem() instanceof ItemLeavesBlock) || block == null) {
			return;
		}

		TileLeaves leaves = new TileLeaves();
		if (itemStack.hasTagCompound()) {
			leaves.readFromNBT(itemStack.getTagCompound());
		} else {
			leaves.setTree(TreeHelper.treeTemplates.get(0));
		}

		GL11.glEnable(GL11.GL_BLEND);

		ISprite leavesIcon = leaves.getIcon(Proxies.render.fancyGraphicsEnabled());
		if (leavesIcon == null) {
			return;
		}
		int color = leaves.getFoliageColour(Proxies.common.getPlayer());

		float r1 = (color >> 16 & 255) / 255.0F;
		float g1 = (color >> 8 & 255) / 255.0F;
		float b1 = (color & 255) / 255.0F;
		renderer.setColorOpaque_F(r1, g1, b1);

		GL11.glTranslatef(0, 0, 0);

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		renderer.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(null, leavesIcon);
		renderer.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(null, leavesIcon);
		renderer.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(null, leavesIcon);
		renderer.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(null, leavesIcon);
		renderer.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(null, leavesIcon);
		renderer.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(null, leavesIcon);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);


		// add fruit
		if (!leaves.hasFruit()) {
			return;
		}

		ISprite fruitTexture = leaves.getFruitTexture();
		if (fruitTexture == null) {
			return;
		}
		int fruitColor = leaves.getFruitColour();

		float r2 = (fruitColor >> 16 & 255) / 255.0F;
		float g2 = (fruitColor >> 8 & 255) / 255.0F;
		float b2 = (fruitColor & 255) / 255.0F;
		renderer.setColorOpaque_F(r2, g2, b2);

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		renderer.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(null, fruitTexture);
		renderer.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(null, fruitTexture);
		renderer.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(null, fruitTexture);
		renderer.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(null, fruitTexture);
		renderer.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(null, fruitTexture);
		renderer.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(null, fruitTexture);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
	
	@Override
	public boolean renderInWorld(Block block, IBlockAccess world, BlockPos pos, IModelRenderer renderer) {

		TileLeaves tile = ForestryBlockLeaves.getLeafTile(world, pos);
		if (tile == null) {
			return false;
		}

		// Render the plain leaf block.
		renderer.renderStandardBlock(block, pos, tile.getIcon(Proxies.render.fancyGraphicsEnabled()));

		// Render overlay for fruit leaves.
		ISprite fruitIcon = tile.getFruitTexture();

		if (fruitIcon != null) {
			int fruitColor = tile.getFruitColour();
			renderFruitOverlay(world, block, pos, renderer, fruitIcon, fruitColor);
		}

		return true;
	}

	private static boolean renderFruitOverlay(IBlockAccess world, Block block, BlockPos pos, IModelRenderer renderer, ISprite texture, int multiplier) {

		float mR = (multiplier >> 16 & 255) / 255.0F;
		float mG = (multiplier >> 8 & 255) / 255.0F;
		float mB = (multiplier & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable) {
			mR = (mR * 30.0F + mG * 59.0F + mB * 11.0F) / 100.0F;
			mG = (mR * 30.0F + mG * 70.0F) / 100.0F;
			mB = (mR * 30.0F + mB * 70.0F) / 100.0F;
		}

		return renderFruitOverlayWithColorMultiplier(world, block, pos, mR, mG, mB, renderer, texture);
	}

	private static boolean renderFruitOverlayWithColorMultiplier(IBlockAccess world, Block block, BlockPos pos, float r, float g, float b, IModelRenderer renderer, ISprite texture) {

		int mixedBrightness = block.getMixedBrightnessForBlock(world, pos);

		float adjR = 0.5f * r;
		float adjG = 0.5f * g;
		float adjB = 0.5f * b;

		// Bottom
		renderBottomFace(world, block, pos, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderTopFace(world, block, pos, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderEastFace(world, block, pos, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderWestFace(world, block, pos, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderNorthFace(world, block, pos, renderer, texture, mixedBrightness, adjR, adjG, adjB);
		renderSouthFace(world, block, pos, renderer, texture, mixedBrightness, adjR, adjG, adjB);

		return true;
	}
	
	@Override
	public TextureAtlasSprite getTexture() {
		return null;
	}
}
