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

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.math.Vector3f;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileAnalyzer;

public class RenderAnalyzer implements IForestryRenderer<TileAnalyzer> {
	public static final ModelLayerLocation MODEL_LAYER = IForestryRenderer.register("analyzer");
	
	private static final String TOWER2 = "tower2";
	private static final String TOWER1 = "tower1";
	private static final String COVER = "cover";
	private static final String PEDESTAL = "pedestal";
	private final ModelPart pedestal;
	private final ModelPart cover;
	private final ModelPart tower1;
	private final ModelPart tower2;

	private final ResourceLocation[] textures;

	public RenderAnalyzer(final ModelPart root) {
        this.pedestal = root.getChild(PEDESTAL);
        this.cover = root.getChild(COVER);
        this.tower1 = root.getChild(TOWER1);
        this.tower2 = root.getChild(TOWER2);
        
        textures = new ResourceLocation[]{
				new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/analyzer_pedestal.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/analyzer_tower1.png"),
				new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/analyzer_tower2.png"),
		};
	}
	
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        partdefinition.addOrReplaceChild(PEDESTAL, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-8F, -8F, -8F, 16, 1, 16), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(COVER, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-8F, -8F, -8F, 16, 1, 16), PartPose.offset(8, 8, 8)); 
        partdefinition.addOrReplaceChild(TOWER1, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(-8, -7, -7, 2, 14, 14), PartPose.offset(8, 8, 8));
        partdefinition.addOrReplaceChild(TOWER2, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(6, -7, -7, 2, 14, 14), PartPose.offset(8, 8, 8));
        
        return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void renderTile(TileAnalyzer tile, RenderHelper helper) {
		Level worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(tile.getIndividualOnDisplay(), worldObj, facing, helper);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(ItemStack.EMPTY, null, Direction.WEST, helper);
	}

	private void render(ItemStack itemstack, @Nullable Level world, Direction orientation, RenderHelper helper) {
		Vector3f rotation = new Vector3f(0, 0, 0);
		switch (orientation) {
			case EAST:
				rotation.setY((float) Math.PI / 2);
				break;
			case WEST:
				rotation.setY((float) -Math.PI / 2);
				break;
			case SOUTH:
				break;
			case NORTH:
			default:
				rotation.setY((float) Math.PI);
				break;
		}
		helper.setRotation(rotation);
		helper.push();

		helper.renderModel(textures[0], pedestal);
		helper.renderModel(textures[0], new Vector3f(0, 0, (float) Math.PI), cover);

		helper.renderModel(textures[1], tower1);

		helper.renderModel(textures[2], tower2);
		helper.pop();
		if (itemstack.isEmpty() || world == null) {
			return;
		}
		float renderScale = 1.0f;

		helper.push();
		helper.translate(0.5f, 0.2f, 0.5f);
		helper.scale(renderScale, renderScale, renderScale);

		helper.renderItem(itemstack, world);
		helper.pop();
	}

}
