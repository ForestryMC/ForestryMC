/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
import forestry.arboriculture.gadgets.BlockSapling;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.plugins.PluginArboriculture;

public class SaplingRenderHandler implements ISimpleBlockRenderingHandler {

	public static int renderLayer = 0;
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		if (modelId != this.getRenderId())
			return false;

		TileSapling tile = BlockSapling.getSaplingTile(world, x, y, z);

		IAlleleTreeSpecies species = (IAlleleTreeSpecies) PluginArboriculture.treeInterface.getDefaultTemplate()[0];
		if (tile == null || tile.getTree() == null)
			return true;
		species = tile.getTree().getGenome().getPrimary();

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

	protected boolean renderCrossedSquares(IAlleleTreeSpecies species, IBlockAccess world, Block block, int x, int y, int z) {

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
		drawCrossedSquares(world, species, block, x, y, z, x, y, z, 1.0f);
		return true;
	}

	protected void drawCrossedSquares(IBlockAccess world, IAlleleTreeSpecies species, Block block, int x, int y, int z, double par3, double par5, double par7, float mod) {

		Tessellator tess = Tessellator.instance;
		IIcon icon = species.getGermlingIcon(EnumGermlingType.SAPLING, renderLayer);

        double d3 = (double)icon.getMinU();
        double d4 = (double)icon.getMinV();
        double d5 = (double)icon.getMaxU();
        double d6 = (double)icon.getMaxV();
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
