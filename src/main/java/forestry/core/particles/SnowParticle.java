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
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SnowParticle extends SpriteTexturedParticle {
    public SnowParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);

//        this.particleScale *= 0.5F;
        this.maxAge = (int) (40.0D / (Math.random() * 0.8D + 0.2D));

        this.motionX *= 0.01D;
        this.motionY *= -0.4D;
        this.motionZ *= 0.01D;
    }

    @Override
    public void renderParticle(IVertexBuilder iVertexBuilder, ActiveRenderInfo activeRenderInfo, float partialTicks) {
        double x = this.prevPosX + (this.posX - this.prevPosX)/* * partialTicks - interpPosX*/;
        double y = this.prevPosY + (this.posY - this.prevPosY)/* * partialTicks - interpPosY*/;
        double z = this.prevPosZ + (this.posZ - this.prevPosZ)/* * partialTicks - interpPosZ*/;

        float minU = 0;//this.particleTextureIndexX / 16.0F;
        float maxU = minU + 0.0624375F;
        float minV = 0;//this.particleTextureIndexY / 16.0F;
        float maxV = minV + 0.0624375F;
        float scale = 0.1F * this.particleScale;

        //		if (this.particleTexture != null) {
        minU = this.sprite.getMinU();
        maxU = this.sprite.getMaxU();
        minV = this.sprite.getMinV();
        maxV = this.sprite.getMaxV();
        //		}

        for (int i = 0; i < 5; i++) {
            renderParticle(
                    iVertexBuilder,
                    this.posX,
                    this.posY,
                    this.posZ,
                    activeRenderInfo.getRotation().getX(),
                    activeRenderInfo.getRotation().getX() + activeRenderInfo.getRotation().getZ(),
                    activeRenderInfo.getRotation().getZ(),
                    activeRenderInfo.getRotation().getY() + activeRenderInfo.getRotation().getZ(),
                    activeRenderInfo.getRotation().getX() + activeRenderInfo.getRotation().getY(),
                    minU,
                    maxU,
                    minV,
                    maxV,
                    this.particleScale,
                    partialTicks
            );
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private void renderParticle(
            IVertexBuilder buffer,
            double x,
            double y,
            double z,
            float rotationX,
            float rotationXZ,
            float rotationZ,
            float rotationYZ,
            float rotationXY,
            float minU,
            float maxU,
            float minV,
            float maxV,
            float scale,
            float partialTicks
    ) {
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        buffer.pos(
                x - rotationX * scale - rotationYZ * scale,
                y - rotationXZ * scale,
                z - rotationZ * scale - rotationXY * scale
        ).tex(maxU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(
                j,
                k
        ).endVertex();
        buffer.pos(
                x - rotationX * scale + rotationYZ * scale,
                y + rotationXZ * scale,
                z - rotationZ * scale + rotationXY * scale
        ).tex(maxU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(
                j,
                k
        ).endVertex();
        buffer.pos(
                x + rotationX * scale + rotationYZ * scale,
                y + rotationXZ * scale,
                z + rotationZ * scale + rotationXY * scale
        ).tex(minU, minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(
                j,
                k
        ).endVertex();
        buffer.pos(
                x + rotationX * scale - rotationYZ * scale,
                y - rotationXZ * scale,
                z + rotationZ * scale - rotationXY * scale
        ).tex(minU, maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(
                j,
                k
        ).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<SnowParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle makeParticle(
                SnowParticleData typeIn,
                ClientWorld worldIn,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            SnowParticle particle = new SnowParticle(
                    worldIn,
                    typeIn.particleStart.getX(),
                    typeIn.particleStart.getY(),
                    typeIn.particleStart.getZ()
            );
            particle.selectSpriteRandomly(spriteSet);
            return particle;
        }
    }
}
