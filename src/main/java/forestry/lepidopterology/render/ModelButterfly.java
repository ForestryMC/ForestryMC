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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

public class ModelButterfly extends ModelBase {

	private final ModelRenderer wingRight;
	private final ModelRenderer eyeRight;
	private final ModelRenderer eyeLeft;
	private final ModelRenderer wingLeft;
	private final ModelRenderer body;

	private float scale;

	public ModelButterfly() {
		textureWidth = 64;
		textureHeight = 32;

		wingRight = new ModelRenderer(this, 0, 0);
		wingRight.addBox(-7F, 0F, -6F, 7, 1, 13);
		wingRight.setRotationPoint(-0.5F, 0.5F, 0F);
		wingRight.setTextureSize(64, 32);
		wingRight.mirror = true;
		setRotation(wingRight, 0F, 0F, 0F);
		eyeRight = new ModelRenderer(this, 40, 9);
		eyeRight.addBox(0F, 0F, 0F, 1, 1, 1);
		eyeRight.setRotationPoint(-1.1F, -0.5F, -4.5F);
		eyeRight.setTextureSize(64, 32);
		eyeRight.mirror = true;
		setRotation(eyeRight, 0F, 0F, 0F);
		eyeLeft = new ModelRenderer(this, 40, 7);
		eyeLeft.addBox(0F, 0F, 0F, 1, 1, 1);
		eyeLeft.setRotationPoint(0.1F, -0.5F, -4.5F);
		eyeLeft.setTextureSize(64, 32);
		eyeLeft.mirror = true;
		setRotation(eyeLeft, 0F, 0F, 0F);
		wingLeft = new ModelRenderer(this, 0, 14);
		wingLeft.addBox(0F, 0F, -6F, 7, 1, 13);
		wingLeft.setRotationPoint(0.5F, 0.5F, 0F);
		wingLeft.setTextureSize(64, 32);
		wingLeft.mirror = true;
		setRotation(wingLeft, 0F, 0F, 0F);
		body = new ModelRenderer(this, 40, 0);
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
	public void render(Entity entity, float f, float f1, float swing, float f3, float f4, float f5) {
		super.render(entity, f, f1, swing, f3, f4, f5);

		//body.rotateAngleX = ((float)Math.PI / 4F) + MathHelper.cos(swing * 0.1F) * 0.15F;
		//body.rotateAngleY = 0.0F;
		wingRight.rotateAngleZ = MathHelper.cos(swing * 1.3F) * (float) Math.PI * 0.25F;
		wingLeft.rotateAngleZ = -wingRight.rotateAngleZ;

		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(0.0F, (20.0f / scale) * f5, 0.0F);
		wingRight.render(f5);
		eyeRight.render(f5);
		eyeLeft.render(f5);
		wingLeft.render(f5);
		body.render(f5);
		GL11.glPopMatrix();

	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float swing, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, swing, f3, f4, f5, entity);
	}

}
