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
package forestry.apiculture.entities;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class ParticleBeeTargetEntity extends Particle {
    private final Vector3d origin;
    private final Entity entity;

    public ParticleBeeTargetEntity(ClientWorld world, Vector3d origin, Entity entity, int color) {
        super(world, origin.x, origin.y, origin.z, 0.0D, 0.0D, 0.0D);
        //		setParticleTexture(ModuleApiculture.getBeeSprite());
        //TODO - particleManager has a TextureManager so register through that?
        this.origin = origin;
        this.entity = entity;

        //this.motionX = (entity.posX - this.posX) * 0.015;
        //this.motionY = (entity.posY + 1.62F - this.posY) * 0.015;
        //this.motionZ = (entity.posZ - this.posZ) * 0.015;

        particleRed = (color >> 16 & 255) / 255.0F;
        particleGreen = (color >> 8 & 255) / 255.0F;
        particleBlue = (color & 255) / 255.0F;

        this.setSize(0.1F, 0.1F);
        //TODO multiplySCaleBy?
        //		this.particleScale *= 0.2F;
        this.maxAge = (int) (80.0D / (Math.random() * 0.8D + 0.2D));

        this.motionX *= 0.9D;
        this.motionY *= 0.9D;
        this.motionZ *= 0.9D;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.age == this.maxAge / 2) {
            this.motionX = (origin.x - this.posX) * 0.03;
            this.motionY = (origin.y - this.posY) * 0.03;
            this.motionZ = (origin.z - this.posZ) * 0.03;
        }

        if (this.age < this.maxAge * 0.5) {
            // fly near the entity
            this.motionX = (entity.getPosX() - this.posX) * 0.09;
            this.motionX = (this.motionX + 0.2 * (-0.5 + rand.nextFloat())) / 2;
            this.motionY = (entity.getPosY() + 1.62F - this.posY) * 0.03;
            this.motionY = (this.motionY + 0.4 * (-0.5 + rand.nextFloat())) / 4;
            this.motionZ = (entity.getPosZ() - this.posZ) * 0.09;
            this.motionZ = (this.motionZ + 0.2 * (-0.5 + rand.nextFloat())) / 2;
        } else if (this.age < this.maxAge * 0.75) {
            // venture back
            this.motionX *= 0.95;
            this.motionY = (origin.y - this.posY) * 0.03;
            this.motionY = (this.motionY + 0.2 * (-0.5 + rand.nextFloat())) / 2;
            this.motionZ *= 0.95;
        } else {
            // get to origin
            this.motionX = (origin.x - this.posX) * 0.03;
            this.motionY = (origin.y - this.posY) * 0.03;
            this.motionY = (this.motionY + 0.2 * (-0.5 + rand.nextFloat())) / 2;
            this.motionZ = (origin.z - this.posZ) * 0.03;
        }

        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(IVertexBuilder builder, ActiveRenderInfo renderInfo, float partialTicks) {
        Vector3d projectedView = renderInfo.getProjectedView();
        float xPos = (float) (MathHelper.lerp(partialTicks, this.prevPosX, this.posX) - projectedView.getX());
        float yPos = (float) (MathHelper.lerp(partialTicks, this.prevPosY, this.posY) - projectedView.getY());
        float zPos = (float) (MathHelper.lerp(partialTicks, this.prevPosZ, this.posZ) - projectedView.getZ());
    }

	/*@Override
	public void renderParticle(BufferBuilder buffer, ActiveRenderInfo info, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;

		//TODO particle texture
		//		if (this.particleTexture != null) {
		//			minU = particleTexture.getMinU();
		//			maxU = particleTexture.getMaxU();
		//			minV = particleTexture.getMinV();
		//			maxV = particleTexture.getMaxV();
		//		}
		//		TODO multiplyScaleBy
		float f10 = 0.1F * 1;//particleScale;
		float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		buffer.pos(f11 - rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 - rotationYZ * f10 - rotationXZ * f10).tex(maxU, maxV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
		buffer.pos(f11 - rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 - rotationYZ * f10 + rotationXZ * f10).tex(maxU, minV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
		buffer.pos(f11 + rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 + rotationYZ * f10 + rotationXZ * f10).tex(minU, minV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
		buffer.pos(f11 + rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 + rotationYZ * f10 - rotationXZ * f10).tex(minU, maxV).color(particleRed, particleGreen, particleBlue, 1.0F).lightmap(j, k).endVertex();
	}*/

    // avoid calculating lighting for bees, it is too much processing
    @Override
    public int getBrightnessForRender(float p_189214_1_) {
        return 15728880;
    }

    // avoid calculating collisions
    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }


    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;    //TODO - not sure on this one
    }
}
