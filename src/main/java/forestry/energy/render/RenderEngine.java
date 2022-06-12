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
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.render.ForestryResource;
import forestry.core.render.IForestryRenderer;
import forestry.core.render.RenderHelper;
import forestry.core.tiles.TemperatureState;
import forestry.energy.tiles.TileEngine;

public class RenderEngine implements IForestryRenderer<TileEngine> {
	// probably need one per engine?
	public static final ModelLayerLocation ENGINE = IForestryRenderer.register("engine");
	
	private static final String EXTENSION = "EXTENSION";
	private static final String PISTON = "PISTON";
	private static final String TRUNK = "TRUNK";
	private static final String BOILER = "BOILER";
	
	private final ModelPart boiler;
	private final ModelPart trunk;
	private final ModelPart piston;
	private final ModelPart extension;

	private ResourceLocation[] textures;
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

	public RenderEngine(BlockEntityRendererProvider.Context ctx, String baseTexture) {
		ModelPart root = ctx.bakeLayer(ENGINE);
		boiler = root.getChild(BOILER);
		trunk = root.getChild(TRUNK);
		piston = root.getChild(PISTON);
		extension = root.getChild(EXTENSION);
		
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

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        partdefinition.addOrReplaceChild(BOILER, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-8F, -8F, -8F, 16, 6, 16), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(TRUNK, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-4F, -4F, -4F, 8, 12, 8), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(PISTON, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-6F, -2, -6F, 12, 4, 12), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(EXTENSION, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-5F, -3, -5F, 10, 2, 10), PartPose.offset(8, 8, 8));

		return LayerDefinition.create(meshdefinition, 64, 32);
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
			case SOUTH, NORTH, UP -> rotation.setX(angleMap[orientation.ordinal()]);
		}

		helper.setRotation(rotation);
		helper.renderModel(textures[Textures.BASE.ordinal()], boiler);

		helper.push();

		helper.translate(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
		helper.renderModel(textures[Textures.PISTON.ordinal()], piston);
		helper.translate(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

		ResourceLocation texture = switch (state) {
			case OVERHEATING -> textures[Textures.TRUNK_HIGHEST.ordinal()];
			case RUNNING_HOT -> textures[Textures.TRUNK_HIGHER.ordinal()];
			case OPERATING_TEMPERATURE -> textures[Textures.TRUNK_HIGH.ordinal()];
			case WARMED_UP -> textures[Textures.TRUNK_MEDIUM.ordinal()];
			case COOL -> textures[Textures.TRUNK_LOW.ordinal()];
			default -> textures[Textures.TRUNK_LOW.ordinal()];
		};
		
		helper.renderModel(texture, trunk);
		
		float chamberf = 2F / 16F;

		if (step > 0) {
			for (int i = 0; i <= step + 2; i += 2) {
				helper.renderModel(textures[Textures.EXTENSION.ordinal()], extension);
				helper.translate(translate[0] * chamberf, translate[1] * chamberf, translate[2] * chamberf);
			}
		}
		helper.pop();
	}
}
