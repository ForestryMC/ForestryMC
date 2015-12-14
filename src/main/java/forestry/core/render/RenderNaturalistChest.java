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

import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Constants;
import forestry.core.tiles.TileNaturalistChest;

public class RenderNaturalistChest extends TileEntitySpecialRenderer implements IBlockRenderer {

	private final ModelChest chestModel = new ModelChest();
	private final ResourceLocation texture;

	public RenderNaturalistChest(String textureName) {
		texture = new ForestryResource(Constants.TEXTURE_PATH_BLOCKS + "/" + textureName + ".png");
	}

	@Override
	public void inventoryRender(double x, double y, double z) {
		render(ForgeDirection.EAST, 0, 0, x, y, z, 0);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTick) {
		TileNaturalistChest chest = (TileNaturalistChest) tileentity;
		render(chest.getOrientation(), chest.prevLidAngle, chest.lidAngle, x, y, z, partialTick);
	}

	public void render(ForgeDirection orientation, float prevLidAngle, float lidAngle, double x, double y, double z, float partialTick) {
		GL11.glPushMatrix();
		bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

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

		GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		float angle = prevLidAngle + (lidAngle - prevLidAngle) * partialTick;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		chestModel.chestLid.rotateAngleX = -(angle * (float) Math.PI / 2.0F);
		chestModel.renderAll();

		GL11.glPopMatrix();
	}
}
