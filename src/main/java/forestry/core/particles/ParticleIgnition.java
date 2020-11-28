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
package forestry.core.particles;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO particles
@OnlyIn(Dist.CLIENT)
public class ParticleIgnition extends SpriteTexturedParticle {
    private final float ignitionParticleScale;

    public ParticleIgnition(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z, 0, 0, 0);
        this.motionX *= 0.8;
        this.motionY *= 0.8;
        this.motionZ *= 0.8;
        this.motionY = this.rand.nextFloat() * 0.4F + 0.05F;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleScale *= this.rand.nextFloat() / 2 + 0.3F;
        this.ignitionParticleScale = 1.0f;
        this.maxAge = (int) (16.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public int getBrightnessForRender(float p_70070_1_) {
        int i = super.getBrightnessForRender(p_70070_1_);
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
}