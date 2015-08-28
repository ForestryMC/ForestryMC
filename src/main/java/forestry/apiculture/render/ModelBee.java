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
package forestry.apiculture.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import forestry.api.apiculture.EnumBeeType;

public class ModelBee extends ModelBase {

	//fields
	private final ModelRenderer snout;
	private final ModelRenderer torsoWing;
	private final ModelRenderer rump;
	private final ModelRenderer tail;
	private final ModelRenderer tailEnd;
	private final ModelRenderer bridge;
	private final ModelRenderer eyeRight;
	private final ModelRenderer eyeLeft;
	private final ModelRenderer wingLeft;
	private final ModelRenderer wingRight;
	private final ModelRenderer crownQueen;
	private final ModelRenderer crownPrincess;

	private EnumBeeType type = EnumBeeType.DRONE;

	public ModelBee() {
		textureWidth = 64;
		textureHeight = 32;

		snout = new ModelRenderer(this, 18, 11);
		snout.addBox(0F, 0F, 0F, 1, 2, 1);
		snout.setRotationPoint(0F, 1F, -2F);
		snout.setTextureSize(64, 32);
		snout.mirror = true;
		setRotation(snout, 0F, 0F, 0F);
		torsoWing = new ModelRenderer(this, 28, 14);
		torsoWing.addBox(0F, 0F, 0F, 3, 3, 1);
		torsoWing.setRotationPoint(-1F, 0F, -1F);
		torsoWing.setTextureSize(64, 32);
		torsoWing.mirror = true;
		setRotation(torsoWing, 0F, 0F, 0F);
		rump = new ModelRenderer(this, 10, 14);
		rump.addBox(-1F, -1F, 0F, 3, 3, 3);
		rump.setRotationPoint(0F, 1F, 1F);
		rump.setTextureSize(64, 32);
		rump.mirror = true;
		setRotation(rump, 0F, 0F, 0F);
		tail = new ModelRenderer(this, 4, 14);
		tail.addBox(0F, 0F, 0F, 2, 2, 1);
		tail.setRotationPoint(-0.5F, 0.5333334F, 4F);
		tail.setTextureSize(64, 32);
		tail.mirror = true;
		setRotation(tail, 0F, 0F, 0F);
		tailEnd = new ModelRenderer(this, 0, 14);
		tailEnd.addBox(0F, 0F, 0F, 1, 1, 1);
		tailEnd.setRotationPoint(0F, 1F, 5F);
		tailEnd.setTextureSize(64, 32);
		tailEnd.mirror = true;
		setRotation(tailEnd, 0F, 0F, 0F);
		bridge = new ModelRenderer(this, 22, 14);
		bridge.addBox(0F, 0F, 0F, 2, 2, 1);
		bridge.setRotationPoint(-0.5F, 0.5F, 0F);
		bridge.setTextureSize(64, 32);
		bridge.mirror = true;
		setRotation(bridge, 0F, 0F, 0F);
		eyeRight = new ModelRenderer(this, 22, 9);
		eyeRight.addBox(0F, 0F, 0F, 1, 1, 1);
		eyeRight.setRotationPoint(-1F, 0F, -2F);
		eyeRight.setTextureSize(64, 32);
		eyeRight.mirror = true;
		setRotation(eyeRight, 0F, 0F, 0F);
		eyeLeft = new ModelRenderer(this, 18, 9);
		eyeLeft.addBox(0F, 0F, 0F, 1, 1, 1);
		eyeLeft.setRotationPoint(1F, 0F, -2F);
		eyeLeft.setTextureSize(64, 32);
		eyeLeft.mirror = true;
		setRotation(eyeLeft, 0F, 0F, 0F);
		wingLeft = new ModelRenderer(this, 0, 0);
		wingLeft.addBox(0F, 0F, 0F, 3, 1, 6);
		wingLeft.setRotationPoint(1F, -0.1F, -0.5F);
		wingLeft.setTextureSize(64, 32);
		wingLeft.mirror = true;
		setRotation(wingLeft, 0F, 0.1396263F, 0F);
		wingRight = new ModelRenderer(this, 0, 7);
		wingRight.addBox(-3F, 0F, 0F, 3, 1, 6);
		wingRight.setRotationPoint(0F, -0.1F, -0.5F);
		wingRight.setTextureSize(64, 32);
		wingRight.mirror = true;
		setRotation(wingRight, 0F, -0.1396263F, 0F);

		crownQueen = new ModelRenderer(this, 0, 17);
		crownQueen.addBox(0F, 0F, 0F, 2, 1, 2);
		crownQueen.setRotationPoint(-0.5F, -1.5F, -1.5F);
		crownQueen.setTextureSize(64, 32);
		crownQueen.mirror = true;
		setRotation(crownQueen, 0F, 0F, 0F);

		crownPrincess = new ModelRenderer(this, 0, 20);
		crownPrincess.addBox(0F, 0F, 0F, 2, 1, 2);
		crownPrincess.setRotationPoint(-0.5F, -1.5F, -1.5F);
		crownPrincess.setTextureSize(64, 32);
		crownPrincess.mirror = true;
		setRotation(crownPrincess, 0F, 0F, 0F);

	}

	public void setType(EnumBeeType type) {
		this.type = type;
	}

	public void render(Entity entity, float f, float f1, float swing, float f3, float f4, float f5) {
		super.render(entity, f, f1, swing, f3, f4, f5);
		//setRotationAngles(f, f1, f2, f3, f4, f5, entity);

		wingRight.rotateAngleZ = MathHelper.cos(swing * 1.3F) * (float) Math.PI * 0.25F;
		wingLeft.rotateAngleZ = -wingRight.rotateAngleZ;

		GL11.glPushMatrix();
		float scale = 0.75f;
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(0.0F, (12.0f / scale) * f5, 0.0F);

		snout.render(f5);
		torsoWing.render(f5);
		rump.render(f5);
		tail.render(f5);
		tailEnd.render(f5);
		bridge.render(f5);
		eyeRight.render(f5);
		eyeLeft.render(f5);
		wingLeft.render(f5);
		wingRight.render(f5);

		if (type == EnumBeeType.PRINCESS) {
			crownPrincess.render(f5);
		} else if (type == EnumBeeType.QUEEN) {
			crownQueen.render(f5);
		}

		GL11.glPopMatrix();
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
