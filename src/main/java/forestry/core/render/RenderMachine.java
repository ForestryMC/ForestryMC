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

import java.awt.Color;
import java.util.EnumMap;
import java.util.Locale;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.proxy.Proxies;
import forestry.core.tiles.IRenderableTile;

public class RenderMachine extends TileEntitySpecialRenderer implements IBlockRenderer {

	private final ModelRenderer basefront;
	private final ModelRenderer baseback;
	private final ModelRenderer resourceTank;
	private final ModelRenderer productTank;

	private ResourceLocation textureBase;
	private ResourceLocation textureResourceTank;
	private ResourceLocation textureProductTank;

	private final EnumMap<EnumTankLevel, ResourceLocation> texturesTankLevels = new EnumMap<>(EnumTankLevel.class);

	private RenderMachine() {
		ModelBase model = new RenderModelBase();

		basefront = new ModelRenderer(model, 0, 0);
		basefront.addBox(-8F, -8F, -8F, 16, 4, 16);
		basefront.rotationPointX = 8;
		basefront.rotationPointY = 8;
		basefront.rotationPointZ = 8;

		baseback = new ModelRenderer(model, 0, 0);
		baseback.addBox(-8F, 4F, -8F, 16, 4, 16);
		baseback.rotationPointX = 8;
		baseback.rotationPointY = 8;
		baseback.rotationPointZ = 8;

		resourceTank = new ModelRenderer(model, 0, 0);
		resourceTank.addBox(-6F, -8F, -6F, 12, 16, 6);
		resourceTank.rotationPointX = 8;
		resourceTank.rotationPointY = 8;
		resourceTank.rotationPointZ = 8;

		productTank = new ModelRenderer(model, 0, 0);
		productTank.addBox(-6F, -8F, 0F, 12, 16, 6);
		productTank.rotationPointX = 8;
		productTank.rotationPointY = 8;
		productTank.rotationPointZ = 8;
	}

	public RenderMachine(String baseTexture) {
		this();

		textureBase = new ForestryResource(baseTexture + "base.png");
		textureProductTank = new ForestryResource(baseTexture + "tank_product_empty.png");
		textureResourceTank = new ForestryResource(baseTexture + "tank_resource_empty.png");

		for (EnumTankLevel tankLevel : EnumTankLevel.values()) {
			if (tankLevel == EnumTankLevel.EMPTY) {
				continue;
			}
			String tankLevelString = tankLevel.toString().toLowerCase(Locale.ENGLISH);
			texturesTankLevels.put(tankLevel, new ForestryResource("textures/blocks/machine_tank_" + tankLevelString + ".png"));
		}
	}

	@Override
	public void inventoryRender(double x, double y, double z) {
		render(TankRenderInfo.EMPTY, TankRenderInfo.EMPTY, ForgeDirection.SOUTH, x, y, z);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		IRenderableTile generator = (IRenderableTile) tileentity;
		render(generator.getResourceTankInfo(), generator.getProductTankInfo(), generator.getOrientation(), d, d1, d2);
	}

	private void render(TankRenderInfo resourceTankInfo, TankRenderInfo productTankInfo, ForgeDirection orientation, double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);

		float[] angle = {0, 0, 0};

		if (orientation == null) {
			orientation = ForgeDirection.WEST;
		}
		switch (orientation) {
			case EAST:
				angle[1] = (float) Math.PI;
				angle[2] = (float) -Math.PI / 2;
				break;
			case WEST:
				angle[2] = (float) Math.PI / 2;
				break;
			case UP:
				break;
			case DOWN:
				angle[2] = (float) Math.PI;
				break;
			case SOUTH:
				angle[0] = (float) Math.PI / 2;
				angle[2] = (float) Math.PI / 2;
				break;
			case NORTH:
			default:
				angle[0] = (float) -Math.PI / 2;
				angle[2] = (float) Math.PI / 2;
				break;
		}

		basefront.rotateAngleX = angle[0];
		basefront.rotateAngleY = angle[1];
		basefront.rotateAngleZ = angle[2];

		baseback.rotateAngleX = angle[0];
		baseback.rotateAngleY = angle[1];
		baseback.rotateAngleZ = angle[2];

		resourceTank.rotateAngleX = angle[0];
		resourceTank.rotateAngleY = angle[1];
		resourceTank.rotateAngleZ = angle[2];

		productTank.rotateAngleX = angle[0];
		productTank.rotateAngleY = angle[1];
		productTank.rotateAngleZ = angle[2];

		float factor = (float) (1.0 / 16.0);

		Proxies.render.bindTexture(textureBase);
		basefront.render(factor);
		baseback.render(factor);

		renderTank(resourceTank, textureResourceTank, resourceTankInfo, factor);
		renderTank(productTank, textureProductTank, productTankInfo, factor);

		GL11.glPopMatrix();
	}

	private void renderTank(ModelRenderer tankModel, ResourceLocation textureBase, TankRenderInfo renderInfo, float factor) {
		Proxies.render.bindTexture(textureBase);
		tankModel.render(factor);

		ResourceLocation textureResourceTankLevel = texturesTankLevels.get(renderInfo.getLevel());
		if (textureResourceTankLevel == null) {
			return;
		}

		Color primaryTankColor = renderInfo.getFluidColor();
		float[] colors = new float[3];
		primaryTankColor.getRGBColorComponents(colors);
		GL11.glColor4f(colors[0], colors[1], colors[2], 1.0f);

		Proxies.render.bindTexture(textureResourceTankLevel);
		tankModel.render(factor);

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	private static class RenderModelBase extends ModelBase {
	}
}
