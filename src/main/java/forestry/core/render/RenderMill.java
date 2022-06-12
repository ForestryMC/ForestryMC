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

import com.mojang.math.Vector3f;

import forestry.core.tiles.TileMill;
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

public class RenderMill implements IForestryRenderer<TileMill> {
	public static final ModelLayerLocation MODEL_LAYER = IForestryRenderer.register("mill");
	
	private enum Textures {PEDESTAL, EXTENSION, BLADE_1, BLADE_2, CHARGE}
	
	private final ResourceLocation[] textures;

	private final ModelPart pedestal;
	private final ModelPart column;
	private final ModelPart extension;
	private final ModelPart blade1;
	private final ModelPart blade2;

	public RenderMill(ModelPart root, String baseTexture) {
		this.pedestal = root.getChild(Textures.PEDESTAL.name());
		this.column = root.getChild(Textures.CHARGE.name());
		this.extension = root.getChild(Textures.EXTENSION.name());
		this.blade1 = root.getChild(Textures.BLADE_1.name());
		this.blade2 = root.getChild(Textures.BLADE_2.name());
		
		textures = new ResourceLocation[12];

		textures[Textures.PEDESTAL.ordinal()] = new ForestryResource(baseTexture + "pedestal.png");
		textures[Textures.EXTENSION.ordinal()] = new ForestryResource(baseTexture + "extension.png");
		textures[Textures.BLADE_1.ordinal()] = new ForestryResource(baseTexture + "blade1.png");
		textures[Textures.BLADE_2.ordinal()] = new ForestryResource(baseTexture + "blade2.png");

		for (int i = 0; i < 8; i++) {
			textures[Textures.CHARGE.ordinal() + i] = new ForestryResource(baseTexture + "column_" + i + ".png");
		}
	}
	
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        partdefinition.addOrReplaceChild(Textures.PEDESTAL.name(), CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-8F, -8F, -8F, 16, 1, 16), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(Textures.CHARGE.name(), CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-2, -7F, -2, 4, 15, 4), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(Textures.EXTENSION.name(), CubeListBuilder.create().texOffs(0, 0)
            	.addBox(1F, 8F, 7F, 14, 2, 2), PartPose.offset(0, 0, 0));
        partdefinition.addOrReplaceChild(Textures.BLADE_1.name(), CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-4F, -5F, -3F, 8, 12, 1), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(Textures.BLADE_2.name(), CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-4F, -5F, 2F, 8, 12, 1), PartPose.offset(8, 8, 8));
        
		return LayerDefinition.create(meshdefinition, 64, 32);
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
		
		helper.renderModel(textures[Textures.PEDESTAL.ordinal()], pedestal);

		helper.renderModel(textures[Textures.CHARGE.ordinal() + charge], column);

		Vector3f invertedRotation = rotation.copy();
		invertedRotation.mul(-1);
		helper.renderModel(textures[Textures.EXTENSION.ordinal() + charge], invertedRotation, extension);

		helper.translate(translate[0] * tfactor, translate[1] * tfactor, translate[2] * tfactor);
		helper.renderModel(textures[Textures.BLADE_1.ordinal() + charge], blade1);

		// Reset
		helper.translate(-translate[0] * tfactor, -translate[1] * tfactor, -translate[2] * tfactor);

		helper.translate(-translate[0] * tfactor, translate[1] * tfactor, -translate[2] * tfactor);
		helper.renderModel(textures[Textures.BLADE_2.ordinal() + charge], blade2);

		helper.pop();

	}
}
