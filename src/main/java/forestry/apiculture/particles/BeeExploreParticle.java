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
package forestry.apiculture.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeExploreParticle extends SpriteTexturedParticle {
    private final Vector3d origin;

    public BeeExploreParticle(ClientWorld world, Vector3d origin, BlockPos destination, int color) {
        super(world, origin.getX(), origin.getY(), origin.getZ(), 0.0D, 0.0D, 0.0D);
        this.origin = origin;

        this.motionX = (destination.getX() + 0.5 - this.posX) * 0.015;
        this.motionY = (destination.getY() + 0.5 - this.posY) * 0.015;
        this.motionZ = (destination.getZ() + 0.5 - this.posZ) * 0.015;

        particleRed = (color >> 16 & 255) / 255.0F;
        particleGreen = (color >> 8 & 255) / 255.0F;
        particleBlue = (color & 255) / 255.0F;

        this.setSize(0.1F, 0.1F);

        this.particleScale *= 0.2F;
        this.maxAge = (int) (80.0D / (Math.random() * 0.8D + 0.2D));

        this.motionX *= 0.9D;
        this.motionY *= 0.015D;
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

        if (this.age < this.maxAge * 0.25) {
            // venture out
            this.motionX *= 0.92 + 0.3D * rand.nextFloat();
            this.motionY = (this.motionY + 0.3 * (-0.5 + rand.nextFloat())) / 2;
            this.motionZ *= 0.92 + 0.3D * rand.nextFloat();
        } else if (this.age < this.maxAge * 0.5) {
            // slow down
            this.motionX *= 0.75 + 0.3D * rand.nextFloat();
            this.motionY = (this.motionY + 0.3 * (-0.5 + rand.nextFloat())) / 2;
            this.motionZ *= 0.75 + 0.3D * rand.nextFloat();
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

    // avoid calculating collisions
    @Override
    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BeeParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public Particle makeParticle(
                BeeParticleData typeIn,
                ClientWorld worldIn,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            BeeExploreParticle particle = new BeeExploreParticle(
                    worldIn,
                    typeIn.particleStart,
                    typeIn.direction,
                    typeIn.color
            );
            particle.selectSpriteRandomly(spriteSet);
            return particle;
        }
    }
}
