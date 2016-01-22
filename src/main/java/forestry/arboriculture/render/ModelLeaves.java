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
import forestry.api.core.IModelBaker;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.proxy.Proxies;
import forestry.core.render.model.ModelBlockOverlay;

public class ModelLeaves extends ModelBlockOverlay {

	@Override
	public void renderInventory(Block block, ItemStack itemStack, IModelBaker renderer) {

		if (!(itemStack.getItem() instanceof ItemBlockLeaves) || block == null) {
			return;
		}

		TileLeaves leaves = new TileLeaves();
		if (itemStack.hasTagCompound()) {
			leaves.readFromNBT(itemStack.getTagCompound());
		} else {
			leaves.setTree(TreeHelper.treeTemplates.get(0));
		}

		TextureAtlasSprite leavesIcon = leaves.getSprite(Proxies.render.fancyGraphicsEnabled());
		if (leavesIcon == null) {
			return;
		}
		int color = leaves.getFoliageColour(Proxies.common.getPlayer());
		renderer.setColor(color);

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		renderer.renderFaceYNeg(null, leavesIcon);
		renderer.renderFaceYPos(null, leavesIcon);
		renderer.renderFaceZNeg(null, leavesIcon);
		renderer.renderFaceZPos(null, leavesIcon);
		renderer.renderFaceXNeg(null, leavesIcon);
		renderer.renderFaceXPos(null, leavesIcon);

		// add fruit
		if (!leaves.hasFruit()) {
			return;
		}

		TextureAtlasSprite fruitTexture = leaves.getFruitTexture();
		if (fruitTexture == null) {
			return;
		}
		int fruitColor = leaves.getFruitColour();
		renderer.setColor(fruitColor);

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		renderer.renderFaceYNeg(null, fruitTexture);
		renderer.renderFaceYPos(null, fruitTexture);
		renderer.renderFaceZNeg(null, fruitTexture);
		renderer.renderFaceZPos(null, fruitTexture);
		renderer.renderFaceXNeg(null, fruitTexture);
		renderer.renderFaceXPos(null, fruitTexture);
	}

	@Override
	public boolean renderInWorld(Block block, IBlockAccess world, BlockPos pos, IModelBaker renderer) {

		TileLeaves tile = BlockForestryLeaves.getLeafTile(world, pos);
		if (tile == null) {
			return false;
		}

		// Render the plain leaf block.
		renderer.renderStandardBlock(block, pos, tile.getSprite(Proxies.render.fancyGraphicsEnabled()));

		// Render overlay for fruit leaves.
		TextureAtlasSprite fruitIcon = tile.getFruitTexture();

		if (fruitIcon != null) {
			int fruitColor = tile.getFruitColour();
			renderFruitOverlay(world, block, pos, renderer, fruitIcon, fruitColor);
		}

		return true;
	}

	private static boolean renderFruitOverlay(IBlockAccess world, Block block, BlockPos pos, IModelBaker renderer,
			TextureAtlasSprite texture, int multiplier) {

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

	private static boolean renderFruitOverlayWithColorMultiplier(IBlockAccess world, Block block, BlockPos pos, float r,
			float g, float b, IModelBaker renderer, TextureAtlasSprite texture) {

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
	public TextureAtlasSprite getParticleTexture() {
		// TODO Auto-generated method stub
		return super.getParticleTexture();
	}
}
