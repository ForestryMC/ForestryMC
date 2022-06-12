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

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import com.mojang.math.Vector3f;

import forestry.core.tiles.TileMill;

public class RenderMill implements IForestryRenderer<TileMill> {
	public static final ModelLayerLocation MODEL_LAYER = IForestryRenderer.register("mill");
	
	private enum Textures {PEDESTAL, EXTENSION, BLADE_1, BLADE_2, CHARGE}

	public RenderMill(String baseTexture) {
	}

	public RenderMill(String baseTexture, byte charges) {
		this(baseTexture);
	}

	@Override
	public void renderTile(TileMill tile, RenderHelper helper) {
		render(tile.progress, tile.charge, Direction.WEST, helper);
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(0.0f, 0, Direction.WEST, helper);
	}

	private void render(float progress, int charge, Direction orientation, RenderHelper helper) {

		helper.push();

		float step;

		if (progress > 0.5) {
			step = 3.99F - (progress - 0.5F) * 2F * 3.99F;
		} else {
			step = progress * 2F * 3.99F;
		}

		Vector3f rotation = new Vector3f(0, 0, 0);
		float[] translate = {0, 0, 0};
		float tfactor = step / 16;

		switch (orientation) {
			case EAST -> {
				// angle [2] = (float) Math.PI / 2;
				rotation.setZ((float) Math.PI);
				rotation.setY((float) -Math.PI / 2);
				translate[0] = 1;
			}
			case WEST -> {
				// 2, -PI/2
				rotation.setY((float) Math.PI / 2);
				translate[0] = -1;
			}
			case UP -> translate[1] = 1;
			case DOWN -> {
				rotation.setY((float) Math.PI);
				translate[1] = -1;
			}
			case SOUTH -> {
				rotation.setX((float) Math.PI / 2);
				rotation.setY((float) Math.PI / 2);
				translate[2] = 1;
			}
			case NORTH -> {
				rotation.setX((float) -Math.PI / 2);
				rotation.setY((float) Math.PI / 2);
				translate[2] = -1;
			}
		}

		helper.setRotation(rotation);

		Vector3f invertedRotation = rotation.copy();
		invertedRotation.mul(-1);

		helper.translate(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);

		// Reset
		helper.translate(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

		helper.translate(-translate[0] * tfactor, translate[1] * tfactor, -translate[2] * tfactor);

		helper.pop();

	}
}
