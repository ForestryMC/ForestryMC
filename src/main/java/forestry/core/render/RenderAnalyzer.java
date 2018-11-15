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

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.apiculture.render.ModelAnalyzer;
import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileAnalyzer;

public class RenderAnalyzer extends TileEntitySpecialRenderer<TileAnalyzer> {

	private final ModelAnalyzer model;
	@Nullable
	private EntityItem dummyEntityItem;
	private long lastTick;

	public RenderAnalyzer(String baseTexture) {
		this.model = new ModelAnalyzer(baseTexture);
	}

	private EntityItem dummyItem(World world) {
		if (dummyEntityItem == null) {
			dummyEntityItem = new EntityItem(world);
		} else {
			dummyEntityItem.world = world;
		}
		return dummyEntityItem;
	}

	/**
	 * @param analyzer If it null its render the item else it render the tile entity.
	 */
	@Override
	public void render(TileAnalyzer analyzer, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (analyzer != null) {
			World worldObj = analyzer.getWorldObj();
			if (worldObj.isBlockLoaded(analyzer.getPos())) {
				IBlockState blockState = worldObj.getBlockState(analyzer.getPos());
				if (blockState.getBlock() instanceof BlockBase) {
					EnumFacing facing = blockState.getValue(BlockBase.FACING);
					render(analyzer.getIndividualOnDisplay(), worldObj, facing, x, y, z);
					return;
				}
			}
		}
		render(ItemStack.EMPTY, null, EnumFacing.WEST, x, y, z);
	}

	private void render(ItemStack itemstack, @Nullable World world, EnumFacing orientation, double x, double y, double z) {

		model.render(orientation, (float) x, (float) y, (float) z);
		if (itemstack.isEmpty() || world == null) {
			return;
		}
		EntityItem dummyItem = dummyItem(world);
		float renderScale = 1.0f;

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.translate(0.5f, 0.2f, 0.5f);
		GlStateManager.scale(renderScale, renderScale, renderScale);
		dummyItem.setItem(itemstack);

		if (world.getTotalWorldTime() != lastTick) {
			lastTick = world.getTotalWorldTime();
			dummyItem.onUpdate();
		}
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();

		rendermanager.renderEntity(dummyItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
		GlStateManager.popMatrix();

	}

}
