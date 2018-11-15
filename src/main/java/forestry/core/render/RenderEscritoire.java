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
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.models.ModelEscritoire;
import forestry.core.tiles.TileEscritoire;

public class RenderEscritoire extends TileEntitySpecialRenderer<TileEscritoire> {

	private static final ResourceLocation texture = new ForestryResource(Constants.TEXTURE_PATH_BLOCKS + "/escritoire.png");
	private final ModelEscritoire modelEscritoire = new ModelEscritoire();
	@Nullable
	private EntityItem dummyEntityItem;
	private long lastTick;

	private EntityItem dummyItem(World world) {
		if (dummyEntityItem == null) {
			dummyEntityItem = new EntityItem(world);
		} else {
			dummyEntityItem.world = world;
		}
		return dummyEntityItem;
	}

	/**
	 * @param escritoire If it null its render the item else it render the tile entity.
	 */
	@Override
	public void render(TileEscritoire escritoire, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (escritoire != null) {
			World world = escritoire.getWorldObj();
			if (world.isBlockLoaded(escritoire.getPos())) {
				IBlockState blockState = world.getBlockState(escritoire.getPos());
				if (blockState.getBlock() instanceof BlockBase) {
					EnumFacing facing = blockState.getValue(BlockBase.FACING);
					render(escritoire.getIndividualOnDisplay(), world, facing, x, y, z);
					return;
				}
			}
		}
		render(ItemStack.EMPTY, null, EnumFacing.SOUTH, x, y, z);
	}

	private void render(ItemStack itemstack, @Nullable World world, EnumFacing orientation, double x, double y, double z) {
		float factor = (float) (1.0 / 16.0);

		Minecraft minecraft = Minecraft.getMinecraft();
		GlStateManager.pushMatrix();
		{
			GlStateManager.translate((float) x + 0.5f, (float) y + 0.875f, (float) z + 0.5f);

			float[] angle = {(float) Math.PI, 0, 0};

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

			TextureManager textureManager = minecraft.getTextureManager();
			textureManager.bindTexture(texture);
			modelEscritoire.render(null, angle[0], angle[1], angle[2], 0f, 0f, factor);
		}
		GlStateManager.popMatrix();

		if (!itemstack.isEmpty() && world != null) {
			EntityItem dummyItem = dummyItem(world);

			float renderScale = 0.75f;

			GlStateManager.pushMatrix();
			{
				GlStateManager.translate((float) x + 0.5f, (float) y + 0.6f, (float) z + 0.5f);
				GlStateManager.scale(renderScale, renderScale, renderScale);
				dummyItem.setItem(itemstack);

				if (world.getTotalWorldTime() != lastTick) {
					lastTick = world.getTotalWorldTime();
					dummyItem.onUpdate();
				}

				RenderManager rendermanager = minecraft.getRenderManager();
				rendermanager.renderEntity(dummyItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
			}
			GlStateManager.popMatrix();

			dummyItem.world = null; // prevent leaking the world object
		}
	}
}
