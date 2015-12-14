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
package forestry.core.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public abstract class RenderOverlayBlock implements ISimpleBlockRenderingHandler {

	protected static final double OVERLAY_SHIFT = 0.001;

	private static int determineMixedBrightness(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, int mixedBrightness) {
		return renderer.renderMinY > 0.0D ? mixedBrightness : block.getMixedBrightnessForBlock(world, x, y, z);
	}

	protected static void renderBottomFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, IIcon textureIndex, int mixedBrightness,
			float r, float g, float b) {

		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
			return;
		}

		Tessellator tesselator = Tessellator.instance;

		tesselator.setBrightness(determineMixedBrightness(world, block, x, y - 1, z, renderer, mixedBrightness));
		tesselator.setColorOpaque_F(r, g, b);
		renderer.renderFaceYNeg(block, x, y - OVERLAY_SHIFT, z, textureIndex);

	}

	protected static void renderTopFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, IIcon textureIndex, int mixedBrightness, float r,
			float g, float b) {

		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
			return;
		}

		Tessellator tesselator = Tessellator.instance;

		tesselator.setBrightness(determineMixedBrightness(world, block, x, y + 1, z, renderer, mixedBrightness));
		tesselator.setColorOpaque_F(r, g, b);
		renderer.renderFaceYPos(block, x, y + OVERLAY_SHIFT, z, textureIndex);

	}

	protected static void renderEastFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, IIcon textureIndex, int mixedBrightness, float r,
			float g, float b) {

		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x, y, z - 1, 2)) {
			return;
		}

		Tessellator tesselator = Tessellator.instance;

		tesselator.setBrightness(determineMixedBrightness(world, block, x, y, z - 1, renderer, mixedBrightness));
		tesselator.setColorOpaque_F(r, g, b);
		renderer.renderFaceZNeg(block, x, y, z - OVERLAY_SHIFT, textureIndex);

	}

	protected static void renderWestFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, IIcon textureIndex, int mixedBrightness, float r,
			float g, float b) {

		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x, y, z + 1, 3)) {
			return;
		}

		Tessellator tesselator = Tessellator.instance;

		tesselator.setBrightness(determineMixedBrightness(world, block, x, y, z + 1, renderer, mixedBrightness));
		tesselator.setColorOpaque_F(r, g, b);
		renderer.renderFaceZPos(block, x, y, z + OVERLAY_SHIFT, textureIndex);

	}

	protected static void renderNorthFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, IIcon textureIndex, int mixedBrightness,
			float r, float g, float b) {

		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x - 1, y, z, 4)) {
			return;
		}

		Tessellator tesselator = Tessellator.instance;

		tesselator.setBrightness(determineMixedBrightness(world, block, x - 1, y, z, renderer, mixedBrightness));
		tesselator.setColorOpaque_F(r, g, b);
		renderer.renderFaceXNeg(block, x - OVERLAY_SHIFT, y, z, textureIndex);

	}

	protected static void renderSouthFace(IBlockAccess world, Block block, int x, int y, int z, RenderBlocks renderer, IIcon textureIndex, int mixedBrightness,
			float r, float g, float b) {

		if (!renderer.renderAllFaces && !block.shouldSideBeRendered(world, x + 1, y, z, 5)) {
			return;
		}

		Tessellator tesselator = Tessellator.instance;

		tesselator.setBrightness(determineMixedBrightness(world, block, x + 1, y, z, renderer, mixedBrightness));
		tesselator.setColorOpaque_F(r, g, b);
		renderer.renderFaceXPos(block, x + OVERLAY_SHIFT, y, z, textureIndex);

	}

}
