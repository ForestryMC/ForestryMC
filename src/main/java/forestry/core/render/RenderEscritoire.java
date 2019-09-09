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

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.mojang.blaze3d.platform.GlStateManager;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.models.ModelEscritoire;
import forestry.core.tiles.TileEscritoire;

public class RenderEscritoire implements IForestryRenderer<TileEscritoire> {

	private static final ResourceLocation texture = new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/escritoire.png");
	private final ModelEscritoire modelEscritoire = new ModelEscritoire();
	@Nullable
	private ItemEntity dummyEntityItem;
	private long lastTick;

	private ItemEntity dummyItem(World world) {
		if (dummyEntityItem == null) {
			dummyEntityItem = new ItemEntity(EntityType.ITEM, world);
		} else {
			dummyEntityItem.world = world;
		}
		return dummyEntityItem;
	}

	@Override
	public void renderTile(TileEscritoire tile, double x, double y, double z, float partialTicks, int destroyStage) {
		World world = tile.getWorldObj();
		BlockState blockState = world.getBlockState(tile.getPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.get(BlockBase.FACING);
			render(tile.getIndividualOnDisplay(), world, facing, x, y, z);
		}
	}

	@Override
	public void renderItem(ItemStack stack) {
		render(ItemStack.EMPTY, null, Direction.SOUTH, 0, 0, 0);
	}

	private void render(ItemStack itemstack, @Nullable World world, Direction orientation, double x, double y, double z) {
		float factor = (float) (1.0 / 16.0);

		Minecraft minecraft = Minecraft.getInstance();
		GlStateManager.pushMatrix();
		{
			GlStateManager.translatef((float) x + 0.5f, (float) y + 0.875f, (float) z + 0.5f);

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
			ItemEntity dummyItem = dummyItem(world);

			float renderScale = 0.75f;

			GlStateManager.pushMatrix();
			{
				GlStateManager.translatef((float) x + 0.5f, (float) y + 0.6f, (float) z + 0.5f);
				GlStateManager.scalef(renderScale, renderScale, renderScale);
				dummyItem.setItem(itemstack);

				//TODO - right time?
				if (world.getGameTime() != lastTick) {
					lastTick = world.getGameTime();
					dummyItem.tick();
				}

				EntityRendererManager rendermanager = minecraft.getRenderManager();
				rendermanager.renderEntity(dummyItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
			}
			GlStateManager.popMatrix();

			dummyItem.world = null; // prevent leaking the world object
		}
	}
}
