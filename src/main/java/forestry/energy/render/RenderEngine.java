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
package forestry.energy.render;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

import com.mojang.blaze3d.systems.RenderSystem;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.render.ForestryResource;
import forestry.core.render.IForestryRenderer;
import forestry.core.render.RenderHelper;
import forestry.core.tiles.TemperatureState;
import forestry.energy.tiles.TileEngine;

public class RenderEngine implements IForestryRenderer<TileEngine> {

	private enum Textures {

		BASE, PISTON, EXTENSION, TRUNK_HIGHEST, TRUNK_HIGHER, TRUNK_HIGH, TRUNK_MEDIUM, TRUNK_LOW
	}

	private static final float[] angleMap = new float[6];

	static {
		angleMap[Direction.EAST.ordinal()] = (float) -Math.PI / 2;
		angleMap[Direction.WEST.ordinal()] = (float) Math.PI / 2;
		angleMap[Direction.UP.ordinal()] = 0;
		angleMap[Direction.DOWN.ordinal()] = (float) Math.PI;
		angleMap[Direction.SOUTH.ordinal()] = (float) Math.PI / 2;
		angleMap[Direction.NORTH.ordinal()] = (float) -Math.PI / 2;
	}

	public RenderEngine(String baseTexture) {
	}

	@Override
	public void renderTile(TileEngine tile, RenderHelper helper) {
		Level worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(tile.getTemperatureState(), tile.progress, facing, helper);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(TemperatureState.COOL, 0.25F, Direction.UP, helper);
	}

	private void render(TemperatureState state, float progress, Direction orientation, RenderHelper helper) {
		// RenderSystem.color3f(1f, 1f, 1f);

		float step;

		if (progress > 0.5) {
			step = 5.99F - (progress - 0.5F) * 2F * 5.99F;
		} else {
			step = progress * 2F * 5.99F;
		}

		float tfactor = step / 16;

		Vector3f rotation = new Vector3f(0, 0, 0);
		float[] translate = {orientation.getStepX(), orientation.getStepY(), orientation.getStepZ()};

		switch (orientation) {
			case EAST, WEST, DOWN -> rotation.setZ(angleMap[orientation.ordinal()]);
			case SOUTH, NORTH -> rotation.setX(angleMap[orientation.ordinal()]);
		}

		helper.setRotation(rotation);

		helper.push();

		helper.translate(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
		helper.translate(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

		float chamberf = 2F / 16F;

		if (step > 0) {
			for (int i = 0; i <= step + 2; i += 2) {
				helper.translate(translate[0] * chamberf, translate[1] * chamberf, translate[2] * chamberf);
			}
		}
		helper.pop();
	}
}
