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

import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.lepidopterology.entities.EntityButterfly;

@OnlyIn(Dist.CLIENT)
public class ModelButterfly extends SegmentedModel<EntityButterfly> {

    private final ModelRenderer wingRight;
    private final ModelRenderer eyeRight;
    private final ModelRenderer eyeLeft;
    private final ModelRenderer wingLeft;
    private final ModelRenderer body;
    private final ImmutableList<ModelRenderer> parts;

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
        parts = ImmutableList.of(wingRight, eyeLeft, eyeRight, wingLeft, body);
    }

    @Override
    public ImmutableList<ModelRenderer> getParts() {
        return parts;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void render(MatrixStack transformation, IVertexBuilder builder, int packedLight, int packetLight2, float ageInTicks, float netHeadYaw, float headPitch, float alpha) {
        transformation.scale(this.scale, this.scale, this.scale);
        transformation.translate(0.0F, 20.0f / this.scale * scale, 0.0F);

        super.render(transformation, builder, packedLight, packetLight2, ageInTicks, netHeadYaw, headPitch, alpha);
    }

    @Override
    public void setRotationAngles(EntityButterfly entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //body.rotateAngleX = ((float)Math.PI / 4F) + MathHelper.cos(swing * 0.1F) * 0.15F;
        //body.rotateAngleY = 0.0F;
        wingRight.rotateAngleZ = MathHelper.cos(ageInTicks * 1.3F) * (float) Math.PI * 0.25F;
        wingLeft.rotateAngleZ = -wingRight.rotateAngleZ;
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

}
