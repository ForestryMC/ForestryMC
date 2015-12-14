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
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.arboriculture.tiles.TileSapling;
import forestry.plugins.PluginArboriculture;

public class RenderSaplingBlock implements ISimpleBlockRenderingHandler {

	private static int renderLayer = 0;
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		if (modelId != this.getRenderId()) {
			return false;
		}

		TileSapling tile = BlockSapling.getSaplingTile(world, x, y, z);

		if (tile == null || tile.getTree() == null) {
			return true;
		}

		IAlleleTreeSpecies species = tile.getTree().getGenome().getPrimary();

		renderCrossedSquares(species, world, block, x, y, z);
		renderLayer = 1;
		renderCrossedSquares(species, world, block, x, y, z);
		renderLayer = 0;
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return PluginArboriculture.modelIdSaplings;
	}

	private static boolean renderCrossedSquares(IAlleleTreeSpecies species, IBlockAccess world, Block block, int x, int y, int z) {

		Tessellator tess = Tessellator.instance;
		
		tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		int colourMultiplier = species.getGermlingColour(EnumGermlingType.SAPLING, renderLayer);
		float r = (colourMultiplier >> 16 & 255) / 255.0F;
		float g = (colourMultiplier >> 8 & 255) / 255.0F;
		float b = (colourMultiplier & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable) {

			r = (r * 30.0F + g * 59.0F + b * 11.0F) / 100.0F;
			g = (r * 30.0F + g * 70.0F) / 100.0F;
			b = (r * 30.0F + b * 70.0F) / 100.0F;
		}

		tess.setColorOpaque_F(r, g, b);
		drawCrossedSquares(species, x, y, z, 1.0f);
		return true;
	}

	private static void drawCrossedSquares(IAlleleTreeSpecies species, double par3, double par5, double par7, float mod) {

		Tessellator tess = Tessellator.instance;
		IIcon icon = species.getGermlingIcon(EnumGermlingType.SAPLING, renderLayer);

		double d3 = (double) icon.getMinU();
		double d4 = (double) icon.getMinV();
		double d5 = (double) icon.getMaxU();
		double d6 = (double) icon.getMaxV();
		double d7 = 0.45D * mod;
		double d8 = par3 + 0.5D - d7;
		double d9 = par3 + 0.5D + d7;
		double d10 = par7 + 0.5D - d7;
		double d11 = par7 + 0.5D + d7;
		tess.addVertexWithUV(d8, par5 + mod, d10, d3, d4);
		tess.addVertexWithUV(d8, par5 + 0.0D, d10, d3, d6);
		tess.addVertexWithUV(d9, par5 + 0.0D, d11, d5, d6);
		tess.addVertexWithUV(d9, par5 + mod, d11, d5, d4);
		tess.addVertexWithUV(d9, par5 + mod, d11, d3, d4);
		tess.addVertexWithUV(d9, par5 + 0.0D, d11, d3, d6);
		tess.addVertexWithUV(d8, par5 + 0.0D, d10, d5, d6);
		tess.addVertexWithUV(d8, par5 + mod, d10, d5, d4);
		tess.addVertexWithUV(d8, par5 + mod, d11, d3, d4);
		tess.addVertexWithUV(d8, par5 + 0.0D, d11, d3, d6);
		tess.addVertexWithUV(d9, par5 + 0.0D, d10, d5, d6);
		tess.addVertexWithUV(d9, par5 + mod, d10, d5, d4);
		tess.addVertexWithUV(d9, par5 + mod, d10, d3, d4);
		tess.addVertexWithUV(d9, par5 + 0.0D, d10, d3, d6);
		tess.addVertexWithUV(d8, par5 + 0.0D, d11, d5, d6);
		tess.addVertexWithUV(d8, par5 + mod, d11, d5, d4);
	}

}
