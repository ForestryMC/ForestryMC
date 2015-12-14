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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEscritoire extends ModelBase {

	//fields
	private final ModelRenderer desk;
	private final ModelRenderer standRB;
	private final ModelRenderer standRF;
	private final ModelRenderer standLB;
	private final ModelRenderer standLF;
	private final ModelRenderer drawers;
	private final ModelRenderer standLowLF;
	private final ModelRenderer standLowRB;
	private final ModelRenderer standLowRF;
	private final ModelRenderer standLowLB;

	public ModelEscritoire() {
		textureWidth = 64;
		textureHeight = 32;

		desk = new ModelRenderer(this, 0, 0);
		desk.addBox(-8F, 3F, -7.8F, 16, 2, 15);
		desk.setRotationPoint(0F, 0F, 0F);
		desk.setTextureSize(64, 32);
		desk.mirror = true;
		setRotation(desk, 0.0872665f, 0f, 0f);
		standRB = new ModelRenderer(this, 38, 18);
		standRB.addBox(5F, 4F, 5F, 2, 6, 2);
		standRB.setRotationPoint(0F, 0F, 0F);
		standRB.setTextureSize(64, 32);
		standRB.mirror = true;
		setRotation(standRB, 0F, 0F, 0F);
		standRF = new ModelRenderer(this, 38, 18);
		standRF.addBox(5F, 4F, -7F, 2, 6, 2);
		standRF.setRotationPoint(0F, 0F, 0F);
		standRF.setTextureSize(64, 32);
		standRF.mirror = true;
		setRotation(standRF, 0F, 0F, 0F);
		standLB = new ModelRenderer(this, 38, 18);
		standLB.addBox(-7F, 4F, 5F, 2, 6, 2);
		standLB.setRotationPoint(0F, 0F, 0F);
		standLB.setTextureSize(64, 32);
		standLB.mirror = true;
		setRotation(standLB, 0F, 0F, 0F);
		standLF = new ModelRenderer(this, 38, 18);
		standLF.addBox(-7F, 4F, -7F, 2, 6, 2);
		standLF.setRotationPoint(0F, 0F, 0F);
		standLF.setTextureSize(64, 32);
		standLF.mirror = true;
		setRotation(standLF, 0F, 0F, 0F);
		drawers = new ModelRenderer(this, 0, 18);
		drawers.addBox(-7.5F, -2F, 4.5F, 15, 5, 3);
		drawers.setRotationPoint(0F, 0F, 0F);
		drawers.setTextureSize(64, 32);
		drawers.mirror = true;
		setRotation(drawers, 0F, 0F, 0F);
		standLowLF = new ModelRenderer(this, 0, 26);
		standLowLF.addBox(-6.5F, 10F, -6.5F, 1, 4, 1);
		standLowLF.setRotationPoint(0F, 0F, 0F);
		standLowLF.setTextureSize(64, 32);
		standLowLF.mirror = true;
		setRotation(standLowLF, 0F, 0F, 0F);
		standLowRB = new ModelRenderer(this, 0, 26);
		standLowRB.addBox(5.5F, 10F, 5.5F, 1, 4, 1);
		standLowRB.setRotationPoint(0F, 0F, 0F);
		standLowRB.setTextureSize(64, 32);
		standLowRB.mirror = true;
		setRotation(standLowRB, 0F, 0F, 0F);
		standLowRF = new ModelRenderer(this, 0, 26);
		standLowRF.addBox(5.5F, 10F, -6.5F, 1, 4, 1);
		standLowRF.setRotationPoint(0F, 0F, 0F);
		standLowRF.setTextureSize(64, 32);
		standLowRF.mirror = true;
		setRotation(standLowRF, 0F, 0F, 0F);
		standLowLB = new ModelRenderer(this, 0, 26);
		standLowLB.addBox(-6.5F, 10F, 5.5F, 1, 4, 1);
		standLowLB.setRotationPoint(0F, 0F, 0F);
		standLowLB.setTextureSize(64, 32);
		standLowLB.mirror = true;
		setRotation(standLowLB, 0F, 0F, 0F);
		/*
		desk = new ModelRenderer(this, 0, 0);
		desk.addBox(0F, 0F, 0F, 16, 2, 15);
		desk.setRotationPoint(-8F, 4F, -7.5F);
		desk.setTextureSize(64, 32);
		desk.mirror = true;
		setRotation(desk, 0.0872665F, 0F, 0F);
		standRB = new ModelRenderer(this, 38, 18);
		standRB.addBox(0F, 0F, 0F, 2, 6, 2);
		standRB.setRotationPoint(5F, 4F, 5F);
		standRB.setTextureSize(64, 32);
		standRB.mirror = true;
		setRotation(standRB, 0F, 0F, 0F);
		standRF = new ModelRenderer(this, 38, 18);
		standRF.addBox(0F, 0F, 0F, 2, 6, 2);
		standRF.setRotationPoint(5F, 4F, -7F);
		standRF.setTextureSize(64, 32);
		standRF.mirror = true;
		setRotation(standRF, 0F, 0F, 0F);
		standLB = new ModelRenderer(this, 38, 18);
		standLB.addBox(0F, 0F, 0F, 2, 6, 2);
		standLB.setRotationPoint(-7F, 4F, 5F);
		standLB.setTextureSize(64, 32);
		standLB.mirror = true;
		setRotation(standLB, 0F, 0F, 0F);
		standLF = new ModelRenderer(this, 38, 18);
		standLF.addBox(0F, 0F, 0F, 2, 6, 2);
		standLF.setRotationPoint(-7F, 4F, -7F);
		standLF.setTextureSize(64, 32);
		standLF.mirror = true;
		setRotation(standLF, 0F, 0F, 0F);
		drawers = new ModelRenderer(this, 0, 18);
		drawers.addBox(0F, 0F, 0F, 16, 5, 3);
		drawers.setRotationPoint(-8F, -2F, 4.5F);
		drawers.setTextureSize(64, 32);
		drawers.mirror = true;
		setRotation(drawers, 0F, 0F, 0F);
		standLowLF = new ModelRenderer(this, 0, 25);
		standLowLF.addBox(0F, 0F, 0F, 1, 4, 1);
		standLowLF.setRotationPoint(-6.5F, 10F, -6.5F);
		standLowLF.setTextureSize(64, 32);
		standLowLF.mirror = true;
		setRotation(standLowLF, 0F, 0F, 0F);
		standLowRB = new ModelRenderer(this, 0, 25);
		standLowRB.addBox(0F, 0F, 0F, 1, 4, 1);
		standLowRB.setRotationPoint(5.5F, 10F, 5.5F);
		standLowRB.setTextureSize(64, 32);
		standLowRB.mirror = true;
		setRotation(standLowRB, 0F, 0F, 0F);
		standLowRF = new ModelRenderer(this, 0, 25);
		standLowRF.addBox(0F, 0F, 0F, 1, 4, 1);
		standLowRF.setRotationPoint(5.5F, 10F, -6.5F);
		standLowRF.setTextureSize(64, 32);
		standLowRF.mirror = true;
		setRotation(standLowRF, 0F, 0F, 0F);
		standLowLB = new ModelRenderer(this, 0, 25);
		standLowLB.addBox(0F, 0F, 0F, 1, 4, 1);
		standLowLB.setRotationPoint(-6.5F, 10F, 5.5F);
		standLowLB.setTextureSize(64, 32);
		standLowLB.mirror = true;
		setRotation(standLowLB, 0F, 0F, 0F);
		*/
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		desk.render(f5);
		standRB.render(f5);
		standRF.render(f5);
		standLB.render(f5);
		standLF.render(f5);
		drawers.render(f5);
		standLowLF.render(f5);
		standLowRB.render(f5);
		standLowRF.render(f5);
		standLowLB.render(f5);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		setRotation(desk, f + 0.0872665f, f1, f2);
		setRotation(standRB, f, f1, f2);
		setRotation(standRF, f, f1, f2);
		setRotation(standLB, f, f1, f2);
		setRotation(standLF, f, f1, f2);
		setRotation(drawers, f, f1, f2);
		setRotation(standLowLF, f, f1, f2);
		setRotation(standLowRB, f, f1, f2);
		setRotation(standLowRF, f, f1, f2);
		setRotation(standLowLB, f, f1, f2);
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
