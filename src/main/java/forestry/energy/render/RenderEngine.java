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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.render.ForestryResource;
import forestry.core.render.IBlockRenderer;
import forestry.core.tiles.TemperatureState;
import forestry.core.tiles.TileEngine;
import forestry.core.utils.Log;

public class RenderEngine extends TileEntitySpecialRenderer implements IBlockRenderer {

	private final ModelBase model = new ModelBase() {
	};
	private final ModelRenderer boiler;
	private final ModelRenderer trunk;
	private final ModelRenderer piston;
	private final ModelRenderer extension;

	private enum Textures {

		BASE, PISTON, EXTENSION, TRUNK_HIGHEST, TRUNK_HIGHER, TRUNK_HIGH, TRUNK_MEDIUM, TRUNK_LOW
	}

	private ResourceLocation[] textures;
	private static final float[] angleMap = new float[6];

	static {
		angleMap[ForgeDirection.EAST.ordinal()] = (float) -Math.PI / 2;
		angleMap[ForgeDirection.WEST.ordinal()] = (float) Math.PI / 2;
		angleMap[ForgeDirection.UP.ordinal()] = 0;
		angleMap[ForgeDirection.DOWN.ordinal()] = (float) Math.PI;
		angleMap[ForgeDirection.SOUTH.ordinal()] = (float) Math.PI / 2;
		angleMap[ForgeDirection.NORTH.ordinal()] = (float) -Math.PI / 2;
	}

	public RenderEngine() {
		boiler = new ModelRenderer(model, 0, 0);
		boiler.addBox(-8F, -8F, -8F, 16, 6, 16);
		boiler.rotationPointX = 8;
		boiler.rotationPointY = 8;
		boiler.rotationPointZ = 8;

		trunk = new ModelRenderer(model, 0, 0);
		trunk.addBox(-4F, -4F, -4F, 8, 12, 8);
		trunk.rotationPointX = 8F;
		trunk.rotationPointY = 8F;
		trunk.rotationPointZ = 8F;

		piston = new ModelRenderer(model, 0, 0);
		piston.addBox(-6F, -2, -6F, 12, 4, 12);
		piston.rotationPointX = 8F;
		piston.rotationPointY = 8F;
		piston.rotationPointZ = 8F;

		extension = new ModelRenderer(model, 0, 0);
		extension.addBox(-5F, -3, -5F, 10, 2, 10);
		extension.rotationPointX = 8F;
		extension.rotationPointY = 8F;
		extension.rotationPointZ = 8F;
	}

	public RenderEngine(String baseTexture) {
		this();

		textures = new ResourceLocation[]{
				new ForestryResource(baseTexture + "base.png"),
				new ForestryResource(baseTexture + "piston.png"),
				new ForestryResource(baseTexture + "extension.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCKS + "/engine_trunk_highest.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCKS + "/engine_trunk_higher.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCKS + "/engine_trunk_high.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCKS + "/engine_trunk_medium.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCKS + "/engine_trunk_low.png"),};
	}

	@Override
	public void inventoryRender(double x, double y, double z) {
		render(TemperatureState.COOL, 0.25F, ForgeDirection.UP, x, y, z);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double d, double d1, double d2, float f) {
		if (tile instanceof TileEngine) {
			TileEngine tileEngine = (TileEngine) tile;
			render(tileEngine.getTemperatureState(), tileEngine.progress, tileEngine.getOrientation(), d, d1, d2);
		} else {
			Log.severe("Tried to render a tile entity that is not an engine: " + tile);
		}
	}

	private void render(TemperatureState state, float progress, ForgeDirection orientation, double x, double y, double z) {

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glColor3f(1, 1, 1);

		GL11.glTranslatef((float) x, (float) y, (float) z);

		float step;

		if (progress > 0.5) {
			step = 5.99F - (progress - 0.5F) * 2F * 5.99F;
		} else {
			step = progress * 2F * 5.99F;
		}

		float tfactor = step / 16;

		float[] angle = {0, 0, 0};
		float[] translate = {orientation.offsetX, orientation.offsetY, orientation.offsetZ};

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

		Proxies.render.bindTexture(textures[Textures.BASE.ordinal()]);
		boiler.render(factor);

		Proxies.render.bindTexture(textures[Textures.PISTON.ordinal()]);
		GL11.glTranslatef(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
		piston.render(factor);
		GL11.glTranslatef(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

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
		Proxies.render.bindTexture(texture);
		trunk.render(factor);

		Proxies.render.bindTexture(textures[Textures.EXTENSION.ordinal()]);
		float chamberf = 2F / 16F;

		if (step > 0) {
			for (int i = 0; i <= step + 2; i += 2) {
				extension.render(factor);
				GL11.glTranslatef(translate[0] * chamberf, translate[1] * chamberf, translate[2] * chamberf);
			}
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}
