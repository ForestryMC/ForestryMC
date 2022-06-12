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

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileEscritoire;
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

public class RenderEscritoire implements IForestryRenderer<TileEscritoire> {
	public static final ModelLayerLocation MODEL_LAYER = IForestryRenderer.register("escritoire");

	private static final ResourceLocation TEXTURE = new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/escritoire.png");

	private enum Parts {
		DESK, STANDRB, STANDRF, STANDLB, STANDLF, DRAWERS, STANDLOWLF, STANDLOWRB, STANDLOWRF, STANDLOWLB
	}
	private final ModelPart desk;
	private final ModelPart standRB;
	private final ModelPart standRF;
	private final ModelPart standLB;
	private final ModelPart standLF;
	private final ModelPart drawers;
	private final ModelPart standLowLF;
	private final ModelPart standLowRB;
	private final ModelPart standLowRF;
	private final ModelPart standLowLB;
	
	public RenderEscritoire(ModelPart root) {
		desk = root.getChild(Parts.DESK.name());
		standRB = root.getChild(Parts.STANDRB.name());
		standRF = root.getChild(Parts.STANDRF.name());
		standLB = root.getChild(Parts.STANDLB.name());
		standLF = root.getChild(Parts.STANDLF.name());
		drawers = root.getChild(Parts.DRAWERS.name());
		standLowRB = root.getChild(Parts.STANDLOWRB.name());
		standLowRF = root.getChild(Parts.STANDLOWRF.name());
		standLowLB = root.getChild(Parts.STANDLOWLB.name());
		standLowLF = root.getChild(Parts.STANDLOWLF.name());
	}
	
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        partdefinition.addOrReplaceChild(Parts.DESK.name(), CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-8F, 3F, -7.8F, 16, 2, 15).mirror(), PartPose.offset(0, 0, 0));
        partdefinition.addOrReplaceChild(Parts.STANDRB.name(), CubeListBuilder.create().texOffs(38, 18)
            	.addBox(5F, 4F, 5F, 2, 6, 2).mirror(), PartPose.offset(0, 0, 0));
        partdefinition.addOrReplaceChild(Parts.STANDRF.name(), CubeListBuilder.create().texOffs(38, 18)
            	.addBox(5F, 4F, -7F, 2, 6, 2).mirror(), PartPose.offset(0, 0, 0));
        partdefinition.addOrReplaceChild(Parts.STANDLB.name(), CubeListBuilder.create().texOffs(38, 18)
            	.addBox(-7F, 4F, 5F, 2, 6, 2).mirror(), PartPose.offset(0, 0, 0));        
        partdefinition.addOrReplaceChild(Parts.STANDLF.name(), CubeListBuilder.create().texOffs(38, 18)
            	.addBox(-7F, 4F, -7F, 2, 6, 2).mirror(), PartPose.offset(0, 0, 0));
        partdefinition.addOrReplaceChild(Parts.DRAWERS.name(), CubeListBuilder.create().texOffs(0, 18)
            	.addBox(-7.5F, -2F, 4.5F, 15, 5, 3).mirror(), PartPose.offset(0, 0, 0)); 
        partdefinition.addOrReplaceChild(Parts.STANDLOWRB.name(), CubeListBuilder.create().texOffs(0, 26)
            	.addBox(5.5F, 10F, 5.5F, 1, 4, 1).mirror(), PartPose.offset(0, 0, 0)); 
        partdefinition.addOrReplaceChild(Parts.STANDLOWRF.name(), CubeListBuilder.create().texOffs(0, 26)
            	.addBox(5.5F, 10F, -6.5F, 1, 4, 1).mirror(), PartPose.offset(0, 0, 0)); 
        partdefinition.addOrReplaceChild(Parts.STANDLOWLB.name(), CubeListBuilder.create().texOffs(0, 26)
            	.addBox(-6.5F, 10F, 5.5F, 1, 4, 1).mirror(), PartPose.offset(0, 0, 0)); 
        partdefinition.addOrReplaceChild(Parts.STANDLOWLF.name(), CubeListBuilder.create().texOffs(0, 26)
            	.addBox(-6.5F, 10F, -6.5F, 1, 4, 1).mirror(), PartPose.offset(0, 0, 0));
		
		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void renderTile(TileEscritoire tile, RenderHelper helper) {
		Level world = tile.getWorldObj();
		BlockState blockState = world.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(tile.getIndividualOnDisplay(), world, facing, helper);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(ItemStack.EMPTY, null, Direction.SOUTH, helper);
	}

	private void render(ItemStack itemstack, @Nullable Level world, Direction orientation, RenderHelper helper) {
		helper.push();
		{
			helper.translate(0.5f, 0.875f, 0.5f);

			Vector3f rotation = new Vector3f((float) Math.PI, 0.0f, 0.0f);

			switch (orientation) {
				case EAST:
					rotation.setY((float) Math.PI / 2);
					break;
				case SOUTH:
					break;
				case NORTH:
					rotation.setY((float) Math.PI);
					break;
				case WEST:
				default:
					rotation.setY((float) -Math.PI / 2);
					break;
			}
			helper.setRotation(rotation);
			helper.renderModel(TEXTURE, new Vector3f(0.0872665F, 0, 0), desk);
			helper.renderModel(TEXTURE,
				standRB, standRF, standLB, standLF, drawers, standLowLF, standLowRB, standLowRF, standLowLB);
		}
		helper.pop();

		if (!itemstack.isEmpty() && world != null) {

			float renderScale = 0.75f;

			helper.push();
			{
				helper.translate(0.5f, 0.6f, 0.5f);
				helper.scale(renderScale, renderScale, renderScale);
				helper.renderItem(itemstack, world);
			}
			helper.pop();
		}
	}
}
