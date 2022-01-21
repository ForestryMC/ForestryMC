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

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.lepidopterology.entities.EntityButterfly;

@OnlyIn(Dist.CLIENT)
public class ButterflyModel extends ListModel<EntityButterfly> {

	private final ModelPart wingRight;
	private final ModelPart eyeRight;
	private final ModelPart eyeLeft;
	private final ModelPart wingLeft;
	private final ModelPart body;
	private final ImmutableList<ModelPart> parts;

	private float scale;

	public ButterflyModel() {
		texWidth = 64;
		texHeight = 32;

		wingRight = new ModelPart(this, 0, 0);
		wingRight.addBox(-7F, 0F, -6F, 7, 1, 13);
		wingRight.setPos(-0.5F, 0.5F, 0F);
		wingRight.setTexSize(64, 32);
		wingRight.mirror = true;

		setRotation(wingRight, 0F, 0F, 0F);
		eyeRight = new ModelPart(this, 40, 9);
		eyeRight.addBox(0F, 0F, 0F, 1, 1, 1);
		eyeRight.setPos(-1.1F, -0.5F, -4.5F);
		eyeRight.setTexSize(64, 32);
		eyeRight.mirror = true;
		setRotation(eyeRight, 0F, 0F, 0F);
		eyeLeft = new ModelPart(this, 40, 7);
		eyeLeft.addBox(0F, 0F, 0F, 1, 1, 1);
		eyeLeft.setPos(0.1F, -0.5F, -4.5F);
		eyeLeft.setTexSize(64, 32);
		eyeLeft.mirror = true;
		setRotation(eyeLeft, 0F, 0F, 0F);
		wingLeft = new ModelPart(this, 0, 14);
		wingLeft.addBox(0F, 0F, -6F, 7, 1, 13);
		wingLeft.setPos(0.5F, 0.5F, 0F);
		wingLeft.setTexSize(64, 32);
		wingLeft.mirror = true;
		setRotation(wingLeft, 0F, 0F, 0F);
		body = new ModelPart(this, 40, 0);
		body.addBox(0F, 0F, -4F, 1, 1, 6);
		body.setPos(0F, 0F, 0F);
		body.setTexSize(64, 32);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0.7853982F);
		parts = ImmutableList.of(wingRight, eyeLeft, eyeRight, wingLeft, body);
	}

	@Override
	public ImmutableList<ModelPart> parts() {
		return parts;
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
		wingRight.zRot = Mth.cos(ageInTicks * 1.3F) * (float) Math.PI * 0.25F;
		wingLeft.zRot = -wingRight.zRot;
	}

	private static void setRotation(ModelPart model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

}
