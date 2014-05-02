/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.interfaces.IBlockRenderer;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.ForestryResource;

public class RenderMachine extends TileEntitySpecialRenderer implements IBlockRenderer {

	private final ModelBase model = new ModelBase() {
	};

	private final ModelRenderer basefront;
	private final ModelRenderer baseback;
	private final ModelRenderer resourceTank;
	private final ModelRenderer productTank;

	private static enum Textures {
		BASE,
		TANK_R_EMPTY, TANK_R_LOW, TANK_R_MEDIUM, TANK_R_HIGH, TANK_R_MAXIMUM,
		TANK_P_EMPTY, TANK_P_LOW, TANK_P_MEDIUM, TANK_P_HIGH, TANK_P_MAXIMUM
	};
	private ResourceLocation[] textures;

	public RenderMachine() {

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

		textures = new ResourceLocation[] {
				new ForestryResource(baseTexture + "base.png"),

				new ForestryResource(baseTexture + "tank_resource_empty.png"),
				new ForestryResource(baseTexture + "tank_resource_low.png"),
				new ForestryResource(baseTexture + "tank_resource_medium.png"),
				new ForestryResource(baseTexture + "tank_resource_high.png"),
				new ForestryResource(baseTexture + "tank_resource_maximum.png"),

				new ForestryResource(baseTexture + "tank_product_empty.png"),
				new ForestryResource(baseTexture + "tank_product_low.png"),
				new ForestryResource(baseTexture + "tank_product_medium.png"),
				new ForestryResource(baseTexture + "tank_product_high.png"),
				new ForestryResource(baseTexture + "tank_product_maximum.png"),
		};

	}

	@Override
	public void inventoryRender(double x, double y, double z, float f, float f1) {
		render(EnumTankLevel.EMPTY, EnumTankLevel.EMPTY, ForgeDirection.UP, x, y, z);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		IRenderableMachine generator = (IRenderableMachine) tileentity;
		render(generator.getPrimaryLevel(), generator.getSecondaryLevel(), generator.getOrientation(), d, d1, d2);

	}

	private void render(EnumTankLevel waterLevel, EnumTankLevel melangeLevel, ForgeDirection orientation, double x, double y, double z) {
		render(waterLevel.ordinal(), melangeLevel.ordinal(), orientation, x, y, z);
	}

	public void render(int waterLevelInt, int melangeLevelInt, ForgeDirection orientation, double x, double y, double z) {

		EnumTankLevel waterLevel = EnumTankLevel.values()[waterLevelInt];
		EnumTankLevel melangeLevel = EnumTankLevel.values()[melangeLevelInt];

		GL11.glPushMatrix();
		// GL11.glDisable(2896 /* GL_LIGHTING */);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glTranslatef((float) x, (float) y, (float) z);

		float[] angle = { 0, 0, 0 };
		float[] translate = { 0, 0, 0 };

		if (orientation == null)
			orientation = ForgeDirection.WEST;
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

		Proxies.common.bindTexture(textures[Textures.BASE.ordinal()]);
		basefront.render(factor);

		Proxies.common.bindTexture(textures[Textures.BASE.ordinal()]);
		baseback.render(factor);

		ResourceLocation texture;

		switch (waterLevel) {
		case LOW:
			texture = textures[Textures.TANK_R_LOW.ordinal()];
			break;
		case MEDIUM:
			texture = textures[Textures.TANK_R_MEDIUM.ordinal()];
			break;
		case HIGH:
			texture = textures[Textures.TANK_R_HIGH.ordinal()];
			break;
		case MAXIMUM:
			texture = textures[Textures.TANK_R_MAXIMUM.ordinal()];
			break;
		case EMPTY:
		default:
			texture = textures[Textures.TANK_R_EMPTY.ordinal()];
			break;
		}
		Proxies.common.bindTexture(texture);
		resourceTank.render(factor);

		switch (melangeLevel) {
		case LOW:
			texture = textures[Textures.TANK_P_LOW.ordinal()];
			break;
		case MEDIUM:
			texture = textures[Textures.TANK_P_MEDIUM.ordinal()];
			break;
		case HIGH:
			texture = textures[Textures.TANK_P_HIGH.ordinal()];
			break;
		case MAXIMUM:
			texture = textures[Textures.TANK_P_MAXIMUM.ordinal()];
			break;
		case EMPTY:
		default:
			texture = textures[Textures.TANK_P_EMPTY.ordinal()];
			break;
		}
		Proxies.common.bindTexture(texture);
		productTank.render(factor);

		// GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
