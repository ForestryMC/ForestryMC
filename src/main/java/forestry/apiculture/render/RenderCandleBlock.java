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
package forestry.apiculture.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import forestry.apiculture.blocks.BlockCandle;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.proxy.Proxies;

public class RenderCandleBlock implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (block.getRenderType() == Proxies.render.getCandleRenderId()) {
			renderBlockCandle(world, x, y, z, (BlockCandle) block);
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return Proxies.render.getCandleRenderId();
	}

	private static boolean renderBlockCandle(IBlockAccess world, int x, int y, int z, BlockCandle block) {
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileCandle)) {
			return false;
		}
		TileCandle tileCandle = (TileCandle) tileEntity;

		boolean isLit = tileCandle.isLit();

		IIcon iconA = block.getTextureFromPassAndLit(0, isLit);
		IIcon iconB = block.getTextureFromPassAndLit(1, isLit);

		int colour = tileCandle.getColour();
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, block.getLightValue(world, x, y, z)));
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		double d0 = 0.4000000059604645D;
		double d1 = 0.5D - d0;
		double d2 = 0.20000000298023224D;

		if (meta == 1) {
			renderCandleAtAngle(iconA, x - d1, y + d2, z, -d0, 0.0D, 0xffffff);
			renderCandleAtAngle(iconB, x - d1, y + d2, z, -d0, 0.0D, colour);
		} else if (meta == 2) {
			renderCandleAtAngle(iconA, x + d1, y + d2, z, d0, 0.0D, 0xffffff);
			renderCandleAtAngle(iconB, x + d1, y + d2, z, d0, 0.0D, colour);
		} else if (meta == 3) {
			renderCandleAtAngle(iconA, x, y + d2, z - d1, 0.0D, -d0, 0xffffff);
			renderCandleAtAngle(iconB, x, y + d2, z - d1, 0.0D, -d0, colour);
		} else if (meta == 4) {
			renderCandleAtAngle(iconA, x, y + d2, z + d1, 0.0D, d0, 0xffffff);
			renderCandleAtAngle(iconB, x, y + d2, z + d1, 0.0D, d0, colour);
		} else {
			renderCandleAtAngle(iconA, x, y, z, 0.0D, 0.0D, 0xffffff);
			renderCandleAtAngle(iconB, x, y, z, 0.0D, 0.0D, colour);
		}

		return true;
	}

	private static void renderCandleAtAngle(IIcon icon, double x, double y, double z, double par8, double par10, int colour) {
		Tessellator tessellator = Tessellator.instance;
		double minU = icon.getMinU();
		double minV = icon.getMinV();
		double maxU = icon.getMaxU();
		double maxV = icon.getMaxV();
		double textureLightTopU = icon.getInterpolatedU(7.0D);
		double d10 = icon.getInterpolatedV(6.0D);
		double textureLightBottomU = icon.getInterpolatedU(9.0D);
		double d12 = icon.getInterpolatedV(8.0D);
		//double d13 = (double)icon.getInterpolatedV(7.0D);
		double d14 = icon.getInterpolatedV(13.0D);
		//double textureLightBottomV = (double)icon.getInterpolatedV(8.0D);
		double d16 = icon.getInterpolatedV(15.0D);
		x += 0.5D;
		z += 0.5D;
		double d17 = x - 0.5D;
		double d18 = x + 0.5D;
		double d19 = z - 0.5D;
		double d20 = z + 0.5D;
		double d21 = 0.0625D;
		double d22 = 0.625D;
		tessellator.setColorOpaque_I(colour);
		// top quad
		tessellator.addVertexWithUV(x + par8 * (1.0D - d22) - d21, y + d22, z + par10 * (1.0D - d22) - d21, textureLightTopU, d10);
		tessellator.addVertexWithUV(x + par8 * (1.0D - d22) - d21, y + d22, z + par10 * (1.0D - d22) + d21, textureLightTopU, d12);
		tessellator.addVertexWithUV(x + par8 * (1.0D - d22) + d21, y + d22, z + par10 * (1.0D - d22) + d21, textureLightBottomU, d12);
		tessellator.addVertexWithUV(x + par8 * (1.0D - d22) + d21, y + d22, z + par10 * (1.0D - d22) - d21, textureLightBottomU, d10);
		// -x quad
		tessellator.addVertexWithUV(x - d21, y + 1d, d19, minU, minV);
		tessellator.addVertexWithUV(x - d21 + par8, y, d19 + par10, minU, maxV);
		tessellator.addVertexWithUV(x - d21 + par8, y, d20 + par10, maxU, maxV);
		tessellator.addVertexWithUV(x - d21, y + 1d, d20, maxU, minV);
		// +x quad
		tessellator.addVertexWithUV(x + d21, y + 1.0D, d20, minU, minV);
		tessellator.addVertexWithUV(x + par8 + d21, y, d20 + par10, minU, maxV);
		tessellator.addVertexWithUV(x + par8 + d21, y, d19 + par10, maxU, maxV);
		tessellator.addVertexWithUV(x + d21, y + 1.0D, d19, maxU, minV);
		// -z quad
		tessellator.addVertexWithUV(d18, y + 1.0D, z - d21, minU, minV);
		tessellator.addVertexWithUV(d18 + par8, y, z - d21 + par10, minU, maxV);
		tessellator.addVertexWithUV(d17 + par8, y, z - d21 + par10, maxU, maxV);
		tessellator.addVertexWithUV(d17, y + 1.0D, z - d21, maxU, minV);
		// +z quad
		tessellator.addVertexWithUV(d17, y + 1.0D, z + d21, minU, minV);
		tessellator.addVertexWithUV(d17 + par8, y, z + d21 + par10, minU, maxV);
		tessellator.addVertexWithUV(d18 + par8, y, z + d21 + par10, maxU, maxV);
		tessellator.addVertexWithUV(d18, y + 1.0D, z + d21, maxU, minV);
		// bottom quad
		tessellator.addVertexWithUV(x + d21 + par8, y, z - d21 + par10, textureLightBottomU, d14);
		tessellator.addVertexWithUV(x + d21 + par8, y, z + d21 + par10, textureLightBottomU, d16);
		tessellator.addVertexWithUV(x - d21 + par8, y, z + d21 + par10, textureLightTopU, d16);
		tessellator.addVertexWithUV(x - d21 + par8, y, z - d21 + par10, textureLightTopU, d14);
	}
}
