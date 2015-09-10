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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.Collections;
import java.util.List;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.core.IModelRenderer;
import forestry.api.core.sprite.ISprite;
import forestry.arboriculture.gadgets.BlockSapling;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.core.gadgets.UnlistedBlockAccess;
import forestry.core.gadgets.UnlistedBlockPos;
import forestry.core.render.ModelManager;

public class SaplingRenderHandler implements ISmartBlockModel {

	private static int renderLayer = 0;
	
	@Override
	public List getFaceQuads(EnumFacing p_177551_1_) {
		return Collections.emptyList();
	}

	@Override
	public List getGeneralQuads() {
		return Collections.emptyList();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public IBakedModel handleBlockState(IBlockState state) {
		IExtendedBlockState extend = (IExtendedBlockState) state;
		IModelRenderer renderer = ModelManager.getInstance().createNewRenderer();
		Block blk = state.getBlock();
		IBlockAccess world = extend.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = extend.getValue(UnlistedBlockPos.POS);
		renderer.setRenderBoundsFromBlock( blk );
		renderInWorld(blk, world, pos, renderer);
		return renderer.finalizeModel(false);
	}
	
	public boolean renderInWorld(Block block, IBlockAccess world, BlockPos pos, IModelRenderer renderer) {

		TileSapling tile = BlockSapling.getSaplingTile(world, pos);

		if (tile == null || tile.getTree() == null) {
			return true;
		}

		IAlleleTreeSpecies species = tile.getTree().getGenome().getPrimary();

		renderCrossedSquares(species, world, block, pos, renderer);
		renderLayer = 1;
		renderCrossedSquares(species, world, block, pos, renderer);
		renderLayer = 0;
		return true;
	}

	private static boolean renderCrossedSquares(IAlleleTreeSpecies species, IBlockAccess world, Block block, BlockPos pos, IModelRenderer renderer) {
		
		renderer.setBrightness(block.getMixedBrightnessForBlock(world, pos));
		int colourMultiplier = species.getGermlingColour(EnumGermlingType.SAPLING, renderLayer);
		float r = (colourMultiplier >> 16 & 255) / 255.0F;
		float g = (colourMultiplier >> 8 & 255) / 255.0F;
		float b = (colourMultiplier & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable) {

			r = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
			g = (r * 30.0F + g * 70.0F) / 100.0F;
			b = (r * 30.0F + b * 70.0F) / 100.0F;
		}

		renderer.setColorOpaque_F(r, g, b);
		drawCrossedSquares(species, pos.getX(), pos.getY(), pos.getZ(), 1.0f, renderer);
		return true;
	}

	private static void drawCrossedSquares(IAlleleTreeSpecies species, double par3, double par5, double par7, float mod, IModelRenderer renderer){

		ISprite icon = species.getGermlingIcon(EnumGermlingType.SAPLING, renderLayer);

		double d3 = icon.getMinU();
		double d4 = icon.getMinV();
		double d5 = icon.getMaxU();
		double d6 = icon.getMaxV();
		double d7 = 0.45D * mod;
		double d8 = par3 + 0.5D - d7;
		double d9 = par3 + 0.5D + d7;
		double d10 = par7 + 0.5D - d7;
		double d11 = par7 + 0.5D + d7;
		
		renderer.addVertexWithUV(EnumFacing.NORTH, d8, par5 + mod, d10, d3, d4);
		renderer.addVertexWithUV(EnumFacing.NORTH, d8, par5 + 0.0D, d10, d3, d6);
		renderer.addVertexWithUV(EnumFacing.NORTH, d9, par5 + 0.0D, d11, d5, d6);
		renderer.addVertexWithUV(EnumFacing.NORTH, d9, par5 + mod, d11, d5, d4);
		
		renderer.addVertexWithUV(EnumFacing.SOUTH, d9, par5 + mod, d11, d3, d4);
		renderer.addVertexWithUV(EnumFacing.SOUTH, d9, par5 + 0.0D, d11, d3, d6);
		renderer.addVertexWithUV(EnumFacing.SOUTH, d8, par5 + 0.0D, d10, d5, d6);
		renderer.addVertexWithUV(EnumFacing.SOUTH, d8, par5 + mod, d10, d5, d4);
		
		renderer.addVertexWithUV(EnumFacing.WEST, d8, par5 + mod, d11, d3, d4);
		renderer.addVertexWithUV(EnumFacing.WEST, d8, par5 + 0.0D, d11, d3, d6);
		renderer.addVertexWithUV(EnumFacing.WEST, d9, par5 + 0.0D, d10, d5, d6);
		renderer.addVertexWithUV(EnumFacing.WEST, d9, par5 + mod, d10, d5, d4);
		
		renderer.addVertexWithUV(EnumFacing.EAST, d9, par5 + mod, d10, d3, d4);
		renderer.addVertexWithUV(EnumFacing.EAST, d9, par5 + 0.0D, d10, d3, d6);
		renderer.addVertexWithUV(EnumFacing.EAST, d8, par5 + 0.0D, d11, d5, d6);
		renderer.addVertexWithUV(EnumFacing.EAST, d8, par5 + mod, d11, d5, d4);
	}

}
