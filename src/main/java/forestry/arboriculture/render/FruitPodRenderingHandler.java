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
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import uristqwerty.CraftGuide.api.ItemSlotImplementation;

import java.util.Collections;
import java.util.List;

import forestry.api.core.IModelRenderer;
import forestry.api.core.sprite.ISprite;
import forestry.arboriculture.gadgets.BlockFruitPod;
import forestry.arboriculture.gadgets.TileFruitPod;
import forestry.core.gadgets.UnlistedBlockAccess;
import forestry.core.gadgets.UnlistedBlockPos;
import forestry.core.render.ModelManager;
import forestry.plugins.PluginArboriculture;

public class FruitPodRenderingHandler implements ISmartBlockModel{

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

		int maturity = 0;

		BlockFruitPod blockPod = (BlockFruitPod) block;
		TileFruitPod pod = BlockFruitPod.getPodTile(world, pos);
		if (pod != null) {
			maturity = pod.getMaturity();
		}

		renderer.setBrightness(blockPod.getMixedBrightnessForBlock(world, x, y, z));
		renderer.setColorOpaque_F(1.0f, 1.0f, 1.0f);
		int metadata = world.getBlockMetadata(x, y, z);
		ISprite podIcon = blockPod.getIcon(world, x, y, z, metadata);
		int notchDirection = BlockDirectional.getDirection(metadata);

		int k1 = 4 + maturity * 2;
		int l1 = 5 + maturity * 2;
		double d0 = 15.0D - k1;
		double d1 = 15.0D;
		double d2 = 4.0D;
		double d3 = 4.0D + l1;
		double d4 = podIcon.getInterpolatedU(d0);
		double d5 = podIcon.getInterpolatedU(d1);
		double d6 = podIcon.getInterpolatedV(d2);
		double d7 = podIcon.getInterpolatedV(d3);
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

		double d10 = d8 / 16.0D;
		double d11 = (d8 + k1) / 16.0D;
		double d12 = (12.0D - l1) / 16.0D;
		double d13 = 0.75D;
		double d14 = d9 / 16.0D;
		double d15 = (d9 + k1) / 16.0D;
		renderer.addVertexWithUV(d10, d12, d14, d4, d7);
		renderer.addVertexWithUV(d10, d12, d15, d5, d7);
		renderer.addVertexWithUV(d10, d13, d15, d5, d6);
		renderer.addVertexWithUV(d10, d13, d14, d4, d6);
		
		renderer.addVertexWithUV(d11, d12, d15, d4, d7);
		renderer.addVertexWithUV(d11, d12, d14, d5, d7);
		renderer.addVertexWithUV(d11, d13, d14, d5, d6);
		renderer.addVertexWithUV(d11, d13, d15, d4, d6);
		
		renderer.addVertexWithUV(d11, d12, d14, d4, d7);		
		renderer.addVertexWithUV(d10, d12, d14, d5, d7);
		renderer.addVertexWithUV(d10, d13, d14, d5, d6);
		renderer.addVertexWithUV(d11, d13, d14, d4, d6);
		
		renderer.addVertexWithUV(d10, d12, d15, d4, d7);
		renderer.addVertexWithUV(d11, d12, d15, d5, d7);
		renderer.addVertexWithUV(d11, d13, d15, d5, d6);
		renderer.addVertexWithUV(d10, d13, d15, d4, d6);
		int i2 = k1;

		if (maturity >= 2) {
			i2 = k1 - 1;
		}

		d4 = podIcon.getMinU();
		d5 = podIcon.getInterpolatedU(i2);
		d6 = podIcon.getMinV();
		d7 = podIcon.getInterpolatedV(i2);

		renderer.addVertexWithUV(d10, d13, d15, d4, d7);
		renderer.addVertexWithUV(d11, d13, d15, d5, d7);
		renderer.addVertexWithUV(d11, d13, d14, d5, d6);
		renderer.addVertexWithUV(d10, d13, d14, d4, d6);
		
		renderer.addVertexWithUV(d10, d12, d14, d4, d6);
		renderer.addVertexWithUV(d11, d12, d14, d5, d6);
		renderer.addVertexWithUV(d11, d12, d15, d5, d7);
		renderer.addVertexWithUV(d10, d12, d15, d4, d7);
		d4 = podIcon.getInterpolatedU(12.0D);
		d5 = podIcon.getMaxU();
		d6 = podIcon.getMinV();
		d7 = podIcon.getInterpolatedV(4.0D);
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
				renderer.addVertexWithUV(d11, d12, d14, d4, d7);
				renderer.addVertexWithUV(d10, d12, d14, d5, d7);
				renderer.addVertexWithUV(d10, d13, d14, d5, d6);
				renderer.addVertexWithUV(d11, d13, d14, d4, d6);
				
				renderer.addVertexWithUV(d10, d12, d14, d5, d7);
				renderer.addVertexWithUV(d11, d12, d14, d4, d7);
				renderer.addVertexWithUV(d11, d13, d14, d4, d6);
				renderer.addVertexWithUV(d10, d13, d14, d5, d6);
			}
		} else {
			renderer.addVertexWithUV(d10, d12, d14, d5, d7);
			renderer.addVertexWithUV(d10, d12, d15, d4, d7);
			renderer.addVertexWithUV(d10, d13, d15, d4, d6);
			renderer.addVertexWithUV(d10, d13, d14, d5, d6);
			
			renderer.addVertexWithUV(d10, d12, d15, d4, d7);
			renderer.addVertexWithUV(d10, d12, d14, d5, d7);
			renderer.addVertexWithUV(d10, d13, d14, d5, d6);
			renderer.addVertexWithUV(d10, d13, d15, d4, d6);
		}

		return true;
	}
	
	
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

}
