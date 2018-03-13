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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import forestry.core.blocks.BlockBase;
import forestry.core.fluids.Fluids;
import forestry.core.tiles.IRenderableTile;
import forestry.core.tiles.TileBase;

public class RenderMachine extends TileEntitySpecialRenderer<TileBase> {

	private final ModelRenderer basefront;
	private final ModelRenderer baseback;
	private final ModelRenderer resourceTank;
	private final ModelRenderer productTank;

	private final ResourceLocation textureBase;
	private final ResourceLocation textureResourceTank;
	private final ResourceLocation textureProductTank;

	private final EnumMap<EnumTankLevel, ResourceLocation> texturesTankLevels = new EnumMap<>(EnumTankLevel.class);

	public RenderMachine(String baseTexture) {
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

	/**
	 * @param tile If it null its render the item else it render the tile entity.
	 */
	@Override
	public void render(TileBase tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (tile != null) {
			IRenderableTile generator = (IRenderableTile) tile;
			World worldObj = tile.getWorldObj();
			if (worldObj.isBlockLoaded(tile.getPos())) {
				IBlockState blockState = worldObj.getBlockState(tile.getPos());
				if (blockState.getBlock() instanceof BlockBase) {
					EnumFacing facing = blockState.getValue(BlockBase.FACING);
					render(generator.getResourceTankInfo(), generator.getProductTankInfo(), facing, x, y, z, destroyStage);
					return;
				}
			}
		}
		render(TankRenderInfo.EMPTY, TankRenderInfo.EMPTY, EnumFacing.SOUTH, x, y, z, -1);
	}

	private void render(TankRenderInfo resourceTankInfo, TankRenderInfo productTankInfo, EnumFacing orientation, double x, double y, double z, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
		float[] angle = {0, 0, 0};

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

		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(textureBase);

		basefront.render(factor);
		baseback.render(factor);

		renderTank(resourceTank, textureResourceTank, resourceTankInfo, factor);
		renderTank(productTank, textureProductTank, productTankInfo, factor);

		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderTank(ModelRenderer tankModel, ResourceLocation textureBase, TankRenderInfo renderInfo, float factor) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(textureBase);
		tankModel.render(factor);

		ResourceLocation textureResourceTankLevel = texturesTankLevels.get(renderInfo.getLevel());
		if (textureResourceTankLevel == null) {
			return;
		}

		// TODO: render fluid overlay on tank
		Fluids fluidDefinition = Fluids.getFluidDefinition(renderInfo.getFluidStack());
		Color primaryTankColor = fluidDefinition == null ? Color.BLUE : fluidDefinition.getParticleColor();
		float[] colors = new float[3];
		primaryTankColor.getRGBColorComponents(colors);
		GlStateManager.color(colors[0], colors[1], colors[2], 1.0f);

		textureManager.bindTexture(textureResourceTankLevel);
		tankModel.render(factor);

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	private static class RenderModelBase extends ModelBase {
	}
}
