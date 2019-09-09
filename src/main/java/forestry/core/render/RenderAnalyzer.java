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
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import com.mojang.blaze3d.platform.GlStateManager;

import forestry.apiculture.render.ModelAnalyzer;
import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileAnalyzer;

public class RenderAnalyzer implements IForestryRenderer<TileAnalyzer> {

	private final ModelAnalyzer model;
	@Nullable
	private ItemEntity dummyEntityItem;
	private long lastTick;

	public RenderAnalyzer(String baseTexture) {
		this.model = new ModelAnalyzer(baseTexture);
	}

	private ItemEntity dummyItem(World world, double x, double y, double z) {
		if (dummyEntityItem == null) {
			dummyEntityItem = new ItemEntity(world, x, y, z);
		} else {
			dummyEntityItem.world = world;
		}
		return dummyEntityItem;
	}

	@Override
	public void renderTile(TileAnalyzer tile, double x, double y, double z, float partialTicks, int destroyStage) {
		World worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.get(BlockBase.FACING);
			render(tile.getIndividualOnDisplay(), worldObj, facing, x, y, z);
		}
	}

	@Override
	public void renderItem(ItemStack stack) {
		render(ItemStack.EMPTY, null, Direction.WEST, 0, 0, 0);
	}

	private void render(ItemStack itemstack, @Nullable World world, Direction orientation, double x, double y, double z) {

		model.render(orientation, (float) x, (float) y, (float) z);
		if (itemstack.isEmpty() || world == null) {
			return;
		}
		ItemEntity dummyItem = dummyItem(world, x, y, z);
		float renderScale = 1.0f;

		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) x, (float) y, (float) z);
		GlStateManager.translatef(0.5f, 0.2f, 0.5f);
		GlStateManager.scalef(renderScale, renderScale, renderScale);
		dummyItem.setItem(itemstack);

		if (world.getGameTime() != lastTick) {
			lastTick = world.getGameTime();
			dummyItem.tick();    //TODO - correct?
		}
		EntityRendererManager rendermanager = Minecraft.getInstance().getRenderManager();

		rendermanager.renderEntity(dummyItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
		GlStateManager.popMatrix();

	}

}
