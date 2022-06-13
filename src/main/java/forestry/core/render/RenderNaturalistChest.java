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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileNaturalistChest;
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

public class RenderNaturalistChest implements IForestryRenderer<TileNaturalistChest> {
	public static final ModelLayerLocation MODEL_LAYER = IForestryRenderer.register("naturalistchest");

	private static final String LID = "lid";
	private static final String BASE = "base";
	private static final String LOCK = "lock";
	
	private final ModelPart lid;
	private final ModelPart base;
	private final ModelPart lock;
	private final ResourceLocation texture;
	
	public RenderNaturalistChest(ModelPart root, String textureName) {
		this.lid = root.getChild(LID);
		this.base = root.getChild(BASE);
		this.lock = root.getChild(LOCK);
		texture = new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/" + textureName + ".png");
	}
	
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        partdefinition.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(0, 19)
            	.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F/*, 0.0F*/), PartPose.offset(0, 0, 0));
        partdefinition.addOrReplaceChild(LID, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F/*, 0.0F*/), PartPose.offset(0, 9.0F, 1.0F));
        partdefinition.addOrReplaceChild(LOCK, CubeListBuilder.create().texOffs(0, 0)
            	.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F/*, 0.0F*/), PartPose.offset(0, 8.0F, 0));
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderTile(TileNaturalistChest tile, RenderHelper helper) {
		Level worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(facing, tile.prevLidAngle, tile.lidAngle, helper, helper.partialTicks);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(Direction.SOUTH, 0, 0, helper, helper.partialTicks);
	}

	public void render(Direction orientation, float prevLidAngle, float lidAngle, RenderHelper helper, float partialTick) {
		helper.push();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		helper.translate(0.5D, 0.5D, 0.5D);

		helper.rotate(Vector3f.YP.rotationDegrees(-orientation.toYRot()));
		helper.translate(-0.5D, -0.5D, -0.5D);

		float angle = prevLidAngle + (lidAngle - prevLidAngle) * partialTick;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		float rotation = -(angle * (float) Math.PI / 2.0F);
		helper.renderModel(texture, new Vector3f(rotation, 0.0F, 0.0F), lid, lock);
		helper.renderModel(texture, base);

		helper.pop();
	}
}
