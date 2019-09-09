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

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.util.math.MathHelper;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.lepidopterology.entities.EntityButterfly;

@OnlyIn(Dist.CLIENT)
public class ModelButterfly extends EntityModel<EntityButterfly> {

	private final RendererModel wingRight;
	private final RendererModel eyeRight;
	private final RendererModel eyeLeft;
	private final RendererModel wingLeft;
	private final RendererModel body;

	private float scale;

	public ModelButterfly() {
		textureWidth = 64;
		textureHeight = 32;

		wingRight = new RendererModel(this, 0, 0);
		wingRight.addBox(-7F, 0F, -6F, 7, 1, 13);
		wingRight.setRotationPoint(-0.5F, 0.5F, 0F);
		wingRight.setTextureSize(64, 32);
		wingRight.mirror = true;
		setRotation(wingRight, 0F, 0F, 0F);
		eyeRight = new RendererModel(this, 40, 9);
		eyeRight.addBox(0F, 0F, 0F, 1, 1, 1);
		eyeRight.setRotationPoint(-1.1F, -0.5F, -4.5F);
		eyeRight.setTextureSize(64, 32);
		eyeRight.mirror = true;
		setRotation(eyeRight, 0F, 0F, 0F);
		eyeLeft = new RendererModel(this, 40, 7);
		eyeLeft.addBox(0F, 0F, 0F, 1, 1, 1);
		eyeLeft.setRotationPoint(0.1F, -0.5F, -4.5F);
		eyeLeft.setTextureSize(64, 32);
		eyeLeft.mirror = true;
		setRotation(eyeLeft, 0F, 0F, 0F);
		wingLeft = new RendererModel(this, 0, 14);
		wingLeft.addBox(0F, 0F, -6F, 7, 1, 13);
		wingLeft.setRotationPoint(0.5F, 0.5F, 0F);
		wingLeft.setTextureSize(64, 32);
		wingLeft.mirror = true;
		setRotation(wingLeft, 0F, 0F, 0F);
		body = new RendererModel(this, 40, 0);
		body.addBox(0F, 0F, -4F, 1, 1, 6);
		body.setRotationPoint(0F, 0F, 0F);
		body.setTextureSize(64, 32);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0.7853982F);
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	public void render(EntityButterfly entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

		//body.rotateAngleX = ((float)Math.PI / 4F) + MathHelper.cos(swing * 0.1F) * 0.15F;
		//body.rotateAngleY = 0.0F;
		wingRight.rotateAngleZ = MathHelper.cos(ageInTicks * 1.3F) * (float) Math.PI * 0.25F;
		wingLeft.rotateAngleZ = -wingRight.rotateAngleZ;

		GlStateManager.pushMatrix();
		GlStateManager.scalef(this.scale, this.scale, this.scale);
		GlStateManager.translatef(0.0F, 20.0f / this.scale * scale, 0.0F);
		wingRight.render(scale);
		eyeRight.render(scale);
		eyeLeft.render(scale);
		wingLeft.render(scale);
		body.render(scale);
		GlStateManager.popMatrix();
	}

	private static void setRotation(RendererModel model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
