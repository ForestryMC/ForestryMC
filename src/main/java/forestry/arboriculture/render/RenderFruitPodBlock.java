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
import net.minecraft.block.BlockDirectional;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import forestry.arboriculture.blocks.BlockFruitPod;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.plugins.PluginArboriculture;

public class RenderFruitPodBlock implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

		int maturity = 0;

		BlockFruitPod blockPod = (BlockFruitPod) block;
		TileFruitPod pod = BlockFruitPod.getPodTile(world, x, y, z);
		if (pod != null) {
			maturity = pod.getMaturity();
		}

		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(blockPod.getMixedBrightnessForBlock(world, x, y, z));
		tessellator.setColorOpaque_F(1.0f, 1.0f, 1.0f);
		int metadata = world.getBlockMetadata(x, y, z);
		IIcon podIcon = blockPod.getIcon(world, x, y, z, metadata);
		int notchDirection = BlockDirectional.getDirection(metadata);

		int k1 = 4 + maturity * 2;
		int l1 = 5 + maturity * 2;
		double d0 = 15.0D - k1;
		double d1 = 15.0D;
		double d2 = 4.0D;
		double d3 = 4.0D + l1;
		double d4 = (double) podIcon.getInterpolatedU(d0);
		double d5 = (double) podIcon.getInterpolatedU(d1);
		double d6 = (double) podIcon.getInterpolatedV(d2);
		double d7 = (double) podIcon.getInterpolatedV(d3);
		double d8 = 0.0D;
		double d9 = 0.0D;

		switch (notchDirection) {
			case 0:
				d8 = 8.0D - k1 / 2;
				d9 = 15.0D - k1;
				break;
			case 1:
				d8 = 1.0D;
				d9 = 8.0D - k1 / 2;
				break;
			case 2:
				d8 = 8.0D - k1 / 2;
				d9 = 1.0D;
				break;
			case 3:
				d8 = 15.0D - k1;
				d9 = 8.0D - k1 / 2;
		}

		double d10 = x + d8 / 16.0D;
		double d11 = x + (d8 + k1) / 16.0D;
		double d12 = y + (12.0D - l1) / 16.0D;
		double d13 = y + 0.75D;
		double d14 = z + d9 / 16.0D;
		double d15 = z + (d9 + k1) / 16.0D;
		tessellator.addVertexWithUV(d10, d12, d14, d4, d7);
		tessellator.addVertexWithUV(d10, d12, d15, d5, d7);
		tessellator.addVertexWithUV(d10, d13, d15, d5, d6);
		tessellator.addVertexWithUV(d10, d13, d14, d4, d6);
		tessellator.addVertexWithUV(d11, d12, d15, d4, d7);
		tessellator.addVertexWithUV(d11, d12, d14, d5, d7);
		tessellator.addVertexWithUV(d11, d13, d14, d5, d6);
		tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
		tessellator.addVertexWithUV(d11, d12, d14, d4, d7);
		tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
		tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
		tessellator.addVertexWithUV(d11, d13, d14, d4, d6);
		tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
		tessellator.addVertexWithUV(d11, d12, d15, d5, d7);
		tessellator.addVertexWithUV(d11, d13, d15, d5, d6);
		tessellator.addVertexWithUV(d10, d13, d15, d4, d6);
		int i2 = k1;

		if (maturity >= 2) {
			i2 = k1 - 1;
		}

		d4 = (double) podIcon.getMinU();
		d5 = (double) podIcon.getInterpolatedU((double) i2);
		d6 = (double) podIcon.getMinV();
		d7 = (double) podIcon.getInterpolatedV((double) i2);

		tessellator.addVertexWithUV(d10, d13, d15, d4, d7);
		tessellator.addVertexWithUV(d11, d13, d15, d5, d7);
		tessellator.addVertexWithUV(d11, d13, d14, d5, d6);
		tessellator.addVertexWithUV(d10, d13, d14, d4, d6);
		tessellator.addVertexWithUV(d10, d12, d14, d4, d6);
		tessellator.addVertexWithUV(d11, d12, d14, d5, d6);
		tessellator.addVertexWithUV(d11, d12, d15, d5, d7);
		tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
		d4 = (double) podIcon.getInterpolatedU(12.0D);
		d5 = (double) podIcon.getMaxU();
		d6 = (double) podIcon.getMinV();
		d7 = (double) podIcon.getInterpolatedV(4.0D);
		d8 = 8.0D;
		d9 = 0.0D;
		double d16;

		switch (notchDirection) {
			case 0:
				d8 = 8.0D;
				d9 = 12.0D;
				d16 = d4;
				d4 = d5;
				d5 = d16;
				break;
			case 1:
				d8 = 0.0D;
				d9 = 8.0D;
				break;
			case 2:
				d8 = 8.0D;
				d9 = 0.0D;
				break;
			case 3:
				d8 = 12.0D;
				d9 = 8.0D;
				d16 = d4;
				d4 = d5;
				d5 = d16;
		}

		d10 = x + d8 / 16.0D;
		d11 = x + (d8 + 4.0D) / 16.0D;
		d12 = y + 0.75D;
		d13 = y + 1.0D;
		d14 = z + d9 / 16.0D;
		d15 = z + (d9 + 4.0D) / 16.0D;

		if (notchDirection != 2 && notchDirection != 0) {
			if (notchDirection == 1 || notchDirection == 3) {
				tessellator.addVertexWithUV(d11, d12, d14, d4, d7);
				tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
				tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
				tessellator.addVertexWithUV(d11, d13, d14, d4, d6);
				tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
				tessellator.addVertexWithUV(d11, d12, d14, d4, d7);
				tessellator.addVertexWithUV(d11, d13, d14, d4, d6);
				tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
			}
		} else {
			tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
			tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
			tessellator.addVertexWithUV(d10, d13, d15, d4, d6);
			tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
			tessellator.addVertexWithUV(d10, d12, d15, d4, d7);
			tessellator.addVertexWithUV(d10, d12, d14, d5, d7);
			tessellator.addVertexWithUV(d10, d13, d14, d5, d6);
			tessellator.addVertexWithUV(d10, d13, d15, d4, d6);
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return PluginArboriculture.modelIdPods;
	}

}
