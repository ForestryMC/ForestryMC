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

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.mojang.blaze3d.platform.GlStateManager;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.render.ForestryResource;
import forestry.core.render.IForestryRenderer;
import forestry.core.tiles.TemperatureState;
import forestry.energy.tiles.TileEngine;

public class RenderEngine implements IForestryRenderer<TileEngine> {
	private final RendererModel boiler;
	private final RendererModel trunk;
	private final RendererModel piston;
	private final RendererModel extension;

	private enum Textures {

		BASE, PISTON, EXTENSION, TRUNK_HIGHEST, TRUNK_HIGHER, TRUNK_HIGH, TRUNK_MEDIUM, TRUNK_LOW
	}

	private ResourceLocation[] textures;
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
		Model model = new EngineModelBase();
		boiler = new RendererModel(model, 0, 0);
		boiler.addBox(-8F, -8F, -8F, 16, 6, 16);
		boiler.rotationPointX = 8;
		boiler.rotationPointY = 8;
		boiler.rotationPointZ = 8;

		trunk = new RendererModel(model, 0, 0);
		trunk.addBox(-4F, -4F, -4F, 8, 12, 8);
		trunk.rotationPointX = 8F;
		trunk.rotationPointY = 8F;
		trunk.rotationPointZ = 8F;

		piston = new RendererModel(model, 0, 0);
		piston.addBox(-6F, -2, -6F, 12, 4, 12);
		piston.rotationPointX = 8F;
		piston.rotationPointY = 8F;
		piston.rotationPointZ = 8F;

		extension = new RendererModel(model, 0, 0);
		extension.addBox(-5F, -3, -5F, 10, 2, 10);
		extension.rotationPointX = 8F;
		extension.rotationPointY = 8F;
		extension.rotationPointZ = 8F;

		textures = new ResourceLocation[]{
			new ForestryResource(baseTexture + "base.png"),
			new ForestryResource(baseTexture + "piston.png"),
			new ForestryResource(baseTexture + "extension.png"),
			new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_highest.png"),
			new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_higher.png"),
			new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_high.png"),
			new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_medium.png"),
			new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_low.png"),};
	}

	@Override
	public void renderTile(TileEngine tile, double x, double y, double z, float partialTicks, int destroyStage) {
		World worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.get(BlockBase.FACING);
			render(tile.getTemperatureState(), tile.progress, facing, x, y, z);
		}
	}

	@Override
	public void renderItem(ItemStack stack) {
		render(TemperatureState.COOL, 0.25F, Direction.UP, 0, 0, 0);
	}

	private void render(TemperatureState state, float progress, Direction orientation, double x, double y, double z) {
		GlStateManager.color3f(1f, 1f, 1f);

		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) x, (float) y, (float) z);

		float step;

		if (progress > 0.5) {
			step = 5.99F - (progress - 0.5F) * 2F * 5.99F;
		} else {
			step = progress * 2F * 5.99F;
		}

		float tfactor = step / 16;

		float[] angle = {0, 0, 0};
		float[] translate = {orientation.getXOffset(), orientation.getYOffset(), orientation.getZOffset()};

		switch (orientation) {
			case EAST:
			case WEST:
			case DOWN:
				angle[2] = angleMap[orientation.ordinal()];
				break;
			case SOUTH:
			case NORTH:
			default:
				angle[0] = angleMap[orientation.ordinal()];
				break;
		}

		boiler.rotateAngleX = angle[0];
		boiler.rotateAngleY = angle[1];
		boiler.rotateAngleZ = angle[2];

		trunk.rotateAngleX = angle[0];
		trunk.rotateAngleY = angle[1];
		trunk.rotateAngleZ = angle[2];

		piston.rotateAngleX = angle[0];
		piston.rotateAngleY = angle[1];
		piston.rotateAngleZ = angle[2];

		extension.rotateAngleX = angle[0];
		extension.rotateAngleY = angle[1];
		extension.rotateAngleZ = angle[2];

		float factor = (float) (1.0 / 16.0);

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.bindTexture(textures[Textures.BASE.ordinal()]);
		boiler.render(factor);

		textureManager.bindTexture(textures[Textures.PISTON.ordinal()]);
		GlStateManager.translatef(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
		piston.render(factor);
		GlStateManager.translatef(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

		ResourceLocation texture;

		switch (state) {
			case OVERHEATING:
				texture = textures[Textures.TRUNK_HIGHEST.ordinal()];
				break;
			case RUNNING_HOT:
				texture = textures[Textures.TRUNK_HIGHER.ordinal()];
				break;
			case OPERATING_TEMPERATURE:
				texture = textures[Textures.TRUNK_HIGH.ordinal()];
				break;
			case WARMED_UP:
				texture = textures[Textures.TRUNK_MEDIUM.ordinal()];
				break;
			case COOL:
			default:
				texture = textures[Textures.TRUNK_LOW.ordinal()];
				break;

		}
		textureManager.bindTexture(texture);
		trunk.render(factor);

		textureManager.bindTexture(textures[Textures.EXTENSION.ordinal()]);
		float chamberf = 2F / 16F;

		if (step > 0) {
			for (int i = 0; i <= step + 2; i += 2) {
				extension.render(factor);
				GlStateManager.translatef(translate[0] * chamberf, translate[1] * chamberf, translate[2] * chamberf);
			}
		}

		GlStateManager.popMatrix();
	}

	private static class EngineModelBase extends Model {
	}
}
