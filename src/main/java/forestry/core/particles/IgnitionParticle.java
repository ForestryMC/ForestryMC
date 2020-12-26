/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.particles;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IgnitionParticle extends Particle {

    public IgnitionParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z, 0, 0, 0);
        this.motionX *= 0.8;
        this.motionY *= 0.8;
        this.motionZ *= 0.8;
        this.motionY = this.rand.nextFloat() * 0.4F + 0.05F;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
//        this.particleScale *= this.rand.nextFloat() / 2 + 0.3F;
//        float ignitionParticleScale = 1.0f;
        this.maxAge = (int) (16.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        int i = super.getBrightnessForRender(partialTick);
        short short1 = 240;
        int j = i >> 16 & 255;
        return short1 | j << 16;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }

        float f = (float) this.age / (float) this.maxAge;

        if (this.rand.nextFloat() > f * 2) {
            this.world.addParticle(
                    ParticleTypes.SMOKE,
                    this.posX,
                    this.posY,
                    this.posZ,
                    this.motionX,
                    this.motionY,
                    this.motionZ
            );
        }

        this.motionY -= 0.03D;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.999D;
        this.motionY *= 0.999D;
        this.motionZ *= 0.999D;

        if (this.onGround) {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
        }
    }

    @Override
    public void renderParticle(
            IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks
    ) {

    }
}