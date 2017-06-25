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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileNaturalistChest;

public class RenderNaturalistChest extends TileEntitySpecialRenderer<TileNaturalistChest> {

	private final ModelChest chestModel = new ModelChest();
	private final ResourceLocation texture;

	public RenderNaturalistChest(String textureName) {
		texture = new ForestryResource(Constants.TEXTURE_PATH_BLOCKS + "/" + textureName + ".png");
	}

	/**
	 * @param chest If it null its render the item else it render the tile entity.
	 */
	@Override
	public void render(TileNaturalistChest chest, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (chest != null) {
			World worldObj = chest.getWorldObj();
			if (worldObj.isBlockLoaded(chest.getPos())) {
				IBlockState blockState = worldObj.getBlockState(chest.getPos());
				if (blockState.getBlock() instanceof BlockBase) {
					EnumFacing facing = blockState.getValue(BlockBase.FACING);
					render(facing, chest.prevLidAngle, chest.lidAngle, x, y, z, partialTicks);
					return;
				}
			}
		}
		render(EnumFacing.SOUTH, 0, 0, x, y, z, 0);
	}

	public void render(EnumFacing orientation, float prevLidAngle, float lidAngle, double x, double y, double z, float partialTick) {
		GlStateManager.pushMatrix();
		bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);

		int rotation;
		switch (orientation) {
			case EAST:
				rotation = -90;
				break;
			case NORTH:
				rotation = 180;
				break;
			case WEST:
				rotation = 90;
				break;
			default:
			case SOUTH:
				rotation = 0;
				break;
		}

		GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);

		float angle = prevLidAngle + (lidAngle - prevLidAngle) * partialTick;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		chestModel.chestLid.rotateAngleX = -(angle * (float) Math.PI / 2.0F);
		chestModel.renderAll();

		GlStateManager.popMatrix();
	}
}
