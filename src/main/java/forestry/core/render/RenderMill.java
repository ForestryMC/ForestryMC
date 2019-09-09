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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.platform.GlStateManager;

import forestry.core.tiles.TileMill;

public class RenderMill implements IForestryRenderer<TileMill> {
	private enum Textures {PEDESTAL, EXTENSION, BLADE_1, BLADE_2, CHARGE}

	private final ResourceLocation[] textures;

	private final RendererModel pedestal;
	private final RendererModel column;
	private final RendererModel extension;
	private final RendererModel blade1;
	private final RendererModel blade2;

	public RenderMill(String baseTexture) {
		Model model = new MillModelBase();
		pedestal = new RendererModel(model, 0, 0);
		pedestal.addBox(-8F, -8F, -8F, 16, 1, 16);
		pedestal.rotationPointX = 8;
		pedestal.rotationPointY = 8;
		pedestal.rotationPointZ = 8;

		column = new RendererModel(model, 0, 0);
		column.addBox(-2, -7F, -2, 4, 15, 4);
		column.rotationPointX = 8;
		column.rotationPointY = 8;
		column.rotationPointZ = 8;

		extension = new RendererModel(model, 0, 0);
		extension.addBox(1F, 8F, 7F, 14, 2, 2);
		extension.rotationPointX = 0;
		extension.rotationPointY = 0;
		extension.rotationPointZ = 0;

		blade1 = new RendererModel(model, 0, 0);
		blade1.addBox(-4F, -5F, -3F, 8, 12, 1);
		blade1.rotationPointX = 8;
		blade1.rotationPointY = 8;
		blade1.rotationPointZ = 8;

		blade2 = new RendererModel(model, 0, 0);
		blade2.addBox(-4F, -5F, 2F, 8, 12, 1);
		blade2.rotationPointX = 8;
		blade2.rotationPointY = 8;
		blade2.rotationPointZ = 8;

		textures = new ResourceLocation[12];

		textures[Textures.PEDESTAL.ordinal()] = new ForestryResource(baseTexture + "pedestal.png");
		textures[Textures.EXTENSION.ordinal()] = new ForestryResource(baseTexture + "extension.png");
		textures[Textures.BLADE_1.ordinal()] = new ForestryResource(baseTexture + "blade1.png");
		textures[Textures.BLADE_2.ordinal()] = new ForestryResource(baseTexture + "blade2.png");

		for (int i = 0; i < 8; i++) {
			textures[Textures.CHARGE.ordinal() + i] = new ForestryResource(baseTexture + "column_" + i + ".png");
		}
	}

	public RenderMill(String baseTexture, byte charges) {
		this(baseTexture);
	}

	@Override
	public void renderTile(TileMill tile, double x, double y, double z, float partialTicks, int destroyStage) {
		render(tile.progress, tile.charge, Direction.WEST, x, y, z);
	}

	@Override
	public void renderItem(ItemStack stack) {
		render(0.0f, 0, Direction.WEST, 0, 0, 0);
	}

	private void render(float progress, int charge, Direction orientation, double x, double y, double z) {

		GlStateManager.pushMatrix();

		GlStateManager.translatef((float) x, (float) y, (float) z);

		float step;

		if (progress > 0.5) {
			step = 3.99F - (progress - 0.5F) * 2F * 3.99F;
		} else {
			step = progress * 2F * 3.99F;
		}

		float[] angle = {0, 0, 0};
		float[] translate = {0, 0, 0};
		float tfactor = step / 16;

		switch (orientation) {
			case EAST:
				// angle [2] = (float) Math.PI / 2;
				angle[1] = (float) Math.PI;
				angle[2] = (float) -Math.PI / 2;
				translate[0] = 1;
				break;
			case WEST:
				// 2, -PI/2
				angle[2] = (float) Math.PI / 2;
				translate[0] = -1;
				break;
			case UP:
				translate[1] = 1;
				break;
			case DOWN:
				angle[2] = (float) Math.PI;
				translate[1] = -1;
				break;
			case SOUTH:
				angle[0] = (float) Math.PI / 2;
				angle[2] = (float) Math.PI / 2;
				translate[2] = 1;
				break;
			case NORTH:
			default:
				angle[0] = (float) -Math.PI / 2;
				angle[2] = (float) Math.PI / 2;
				translate[2] = -1;
				break;
		}

		pedestal.rotateAngleX = angle[0];
		pedestal.rotateAngleY = angle[2];
		pedestal.rotateAngleZ = angle[1];

		column.rotateAngleX = angle[0];
		column.rotateAngleY = angle[2];
		column.rotateAngleZ = angle[1];

		blade1.rotateAngleX = angle[0];
		blade1.rotateAngleY = angle[2];
		blade1.rotateAngleZ = angle[1];

		blade2.rotateAngleX = angle[0];
		blade2.rotateAngleY = angle[2];
		blade2.rotateAngleZ = angle[1];

		float factor = (float) (1.0 / 16.0);

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.bindTexture(textures[Textures.PEDESTAL.ordinal()]);
		pedestal.render(factor);

		textureManager.bindTexture(textures[Textures.CHARGE.ordinal() + charge]);
		column.render(factor);

		textureManager.bindTexture(textures[Textures.EXTENSION.ordinal()]);
		extension.render(factor);

		textureManager.bindTexture(textures[Textures.BLADE_1.ordinal()]);
		GlStateManager.translatef(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
		blade1.render(factor);

		// Reset
		GlStateManager.translatef(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

		textureManager.bindTexture(textures[Textures.BLADE_2.ordinal()]);
		GlStateManager.translatef(-translate[0] * tfactor, translate[1] * tfactor, -translate[2] * tfactor);
		blade2.render(factor);

		GlStateManager.popMatrix();

	}

	private static class MillModelBase extends Model {
	}
}
