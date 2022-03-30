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
package forestry.lepidopterology.render;

import java.util.List;

import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.lepidopterology.entities.EntityButterfly;

@OnlyIn(Dist.CLIENT)
public class ButterflyModel extends ListModel<EntityButterfly> {

	private float scale;

	public ButterflyModel() {
	}

	@Override
	public Iterable<ModelPart> parts() {
		return List.of();
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	public void renderToBuffer(PoseStack transformation, VertexConsumer builder, int packedLight, int packetLight2, float ageInTicks, float netHeadYaw, float headPitch, float alpha) {
		transformation.scale(this.scale, this.scale, this.scale);
		transformation.translate(0.0F, 1.45f / scale, 0.0F);

		super.renderToBuffer(transformation, builder, packedLight, packetLight2, ageInTicks, netHeadYaw, headPitch, alpha);
	}

	@Override
	public void setupAnim(EntityButterfly entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		//body.rotateAngleX = ((float)Math.PI / 4F) + MathHelper.cos(swing * 0.1F) * 0.15F;
		//body.rotateAngleY = 0.0F;
	}

	private static void setRotation(ModelPart model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

}
