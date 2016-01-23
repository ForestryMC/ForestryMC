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

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Constants;
import forestry.core.inventory.InventoryEscritoire;
import forestry.core.models.ModelEscritoire;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileEscritoire;

public class RenderEscritoire extends TileEntitySpecialRenderer<TileEscritoire> {

	private static final ResourceLocation texture = new ForestryResource(Constants.TEXTURE_PATH_BLOCKS + "/escritoire.png");
	private final ModelEscritoire modelEscritoire;

	public RenderEscritoire() {
		modelEscritoire = new ModelEscritoire();

	}
	
	/**
	 * @param escritoire If it null its render the item else it render the tile entity.
	 */
	@Override
	public void renderTileEntityAt(TileEscritoire escritoire, double x, double y, double z, float partialTicks, int destroyStage) {
		if(escritoire != null){
			render(escritoire.getStackInSlot(InventoryEscritoire.SLOT_ANALYZE), escritoire.getOrientation(), x, y, z);
		}else{
			render(null, EnumFacing.EAST, x, y, z);
		}
	}

	private void render(ItemStack itemstack, EnumFacing orientation, double x, double y, double z) {
		float factor = (float) (1.0 / 16.0);

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5f, (float) y + 0.875f, (float) z + 0.5f);

		float[] angle = {(float) Math.PI, 0, 0};

		if (orientation == null) {
			orientation = EnumFacing.WEST;
		}
		switch (orientation) {
			case EAST:
				angle[1] = (float) Math.PI / 2;
				break;
			case SOUTH:
				break;
			case NORTH:
				angle[1] = (float) Math.PI;
				break;
			case WEST:
			default:
				angle[1] = -(float) Math.PI / 2;
				break;
		}

		Proxies.render.bindTexture(texture);
		modelEscritoire.render(null, angle[0], angle[1], angle[2], 0f, 0f, factor);

		GL11.glPopMatrix();

		/*
		if(itemstack != null) {
			float renderScale = 1.0f;

			GL11.glPushMatrix();
			GL11.glTranslatef((float) x, (float) y, (float) z);
			GL11.glTranslatef(0.6f, 0.8f, 0.5f);
			GL11.glRotatef(90.0f, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(renderScale, renderScale, renderScale);

			RenderItem.renderInFrame = true;
			dummyEntityItem.setEntityItemStack(itemstack);
			customRenderItem.doRenderItem(dummyEntityItem, 0, 0, 0, 0, 0);
			RenderItem.renderInFrame = false;
			GL11.glPopMatrix();
		}
		 */

	}
}
