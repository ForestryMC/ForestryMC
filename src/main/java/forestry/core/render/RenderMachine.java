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
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Locale;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;
import forestry.core.tiles.IRenderableTile;
import forestry.core.tiles.TileBase;

public class RenderMachine implements IForestryRenderer<TileBase> {
	public static final ModelLayerLocation MODEL_LAYER = IForestryRenderer.register("machine");
	
	private static final String BASE_FRONT = "basefront";
	private static final String BASE_BACK = "baseback";
	private static final String RESOURCE_TANK = "resourceTank";
	private static final String PRODUCT_TANK = "productTank";
	
	private final ModelPart basefront;
	private final ModelPart baseback;
	private final ModelPart resourceTank;
	private final ModelPart productTank;

	private final ResourceLocation textureBase;
	private final ResourceLocation textureResourceTank;
	private final ResourceLocation textureProductTank;

	private final EnumMap<EnumTankLevel, ResourceLocation> texturesTankLevels = new EnumMap<>(EnumTankLevel.class);

	public RenderMachine(ModelPart root, String baseTexture) {
		basefront = root.getChild(BASE_FRONT);
		baseback = root.getChild(BASE_BACK);
		resourceTank = root.getChild(RESOURCE_TANK);
		productTank = root.getChild(PRODUCT_TANK);
		
		textureBase = new ResourceLocation(Constants.MOD_ID, baseTexture + "base.png");
		textureProductTank = new ResourceLocation(Constants.MOD_ID, baseTexture + "tank_product_empty.png");
		textureResourceTank = new ResourceLocation(Constants.MOD_ID, baseTexture + "tank_resource_empty.png");

		for (EnumTankLevel tankLevel : EnumTankLevel.values()) {
			if (tankLevel == EnumTankLevel.EMPTY) {
				continue;
			}
			String tankLevelString = tankLevel.toString().toLowerCase(Locale.ENGLISH);
			texturesTankLevels.put(tankLevel, new ResourceLocation(Constants.MOD_ID, "textures/block/machine_tank_" + tankLevelString + ".png"));
		}
	}
	
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        partdefinition.addOrReplaceChild(BASE_FRONT, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-8F, -8F, -8F, 16, 4, 16), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(BASE_BACK, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-8F, 4F, -8F, 16, 4, 16), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(RESOURCE_TANK, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-6F, -8F, -6F, 12, 16, 6), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(PRODUCT_TANK, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-6F, -8F, 0F, 12, 16, 6), PartPose.offset(8, 8, 8));
		
		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void renderTile(TileBase tile, RenderHelper helper) {
		IRenderableTile generator = (IRenderableTile) tile;
		Level worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(generator.getResourceTankInfo(), generator.getProductTankInfo(), facing, helper);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(TankRenderInfo.EMPTY, TankRenderInfo.EMPTY, Direction.SOUTH, helper);
	}

	private void render(TankRenderInfo resourceTankInfo, TankRenderInfo productTankInfo, Direction orientation, RenderHelper helper) {
		Vector3f rotation = new Vector3f(0, 0, 0);

		switch (orientation) {
			case EAST:
				rotation.set(0, (float) Math.PI, (float) -Math.PI / 2);
				break;
			case WEST:
				rotation.set(0, 0, (float) Math.PI / 2);
				break;
			case UP:
				break;
			case DOWN:
				rotation.set(0, 0, (float) Math.PI);
				break;
			case SOUTH:
				rotation.set((float) Math.PI / 2, 0, (float) Math.PI / 2);
				break;
			case NORTH:
			default:
				rotation.set((float) -Math.PI / 2, 0, (float) Math.PI / 2);
				break;
		}

		helper.setRotation(rotation);
		helper.renderModel(textureBase, basefront, baseback);

		renderTank(resourceTank, textureResourceTank, resourceTankInfo, helper);
		renderTank(productTank, textureProductTank, productTankInfo, helper);

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderTank(ModelPart tankModel, ResourceLocation textureBase, TankRenderInfo renderInfo, RenderHelper helper) {
		helper.renderModel(textureBase, tankModel);

		ResourceLocation textureResourceTankLevel = texturesTankLevels.get(renderInfo.getLevel());
		if (textureResourceTankLevel == null) {
			return;
		}

		FluidType attributes = renderInfo.getFluidStack().getFluid().getFluidType();
		int color = attributes.getColor();
		ForestryFluids definition = ForestryFluids.getFluidDefinition(renderInfo.getFluidStack().getFluid());
		if (color < 0) {
			color = Color.BLUE.getRGB();
			if (definition != null) {
				color = definition.getParticleColor().getRGB();
			}
		}
		float[] colors = new float[3];
		colors[0] = (color >> 16 & 255) / 255f;
		colors[1] = (color >> 8 & 255) / 255f;
		colors[2] = (color & 255) / 255f;
		helper.color(colors[0], colors[1], colors[2], 1.0f);

		helper.renderModel(textureResourceTankLevel, tankModel);

		helper.color(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
