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

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SnowParticle extends SpriteTexturedParticle {
	public SnowParticle(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);

		//        this.particleScale *= 0.5F;
		scale(0.5F);
		this.lifetime = (int) (40.0D / (Math.random() * 0.8D + 0.2D));

		this.xd *= 0.01D;
		this.yd *= -0.4D;
		this.zd *= 0.01D;
	}

	@Override
	public void render(IVertexBuilder builder, ActiveRenderInfo activeRenderInfo, float partialTicks) {
		//double x = this.prevPosX + (this.posX - this.prevPosX)/* * partialTicks - interpPosX*/;
		//double y = this.prevPosY + (this.posY - this.prevPosY)/* * partialTicks - interpPosY*/;
		//double z = this.prevPosZ + (this.posZ - this.prevPosZ)/* * partialTicks - interpPosZ*/;

		/*float minU = 0;//this.particleTextureIndexX / 16.0F;
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
			renderParticle(iVertexBuilder, x,y,z, activeRenderInfo.rotation().x(), activeRenderInfo.rotation().getX() + activeRenderInfo.rotation().getZ(), activeRenderInfo.rotation().getZ(), activeRenderInfo.rotation().getY() + activeRenderInfo.rotation().getZ(), activeRenderInfo.rotation().getX() + activeRenderInfo.rotation().getY(), minU, maxU, minV, maxV, scale, partialTicks);
		}*/
		Vector3d vector3d = activeRenderInfo.getPosition();
		float f = (float) (MathHelper.lerp((double) partialTicks, this.xo, this.x) - vector3d.x());
		float f1 = (float) (MathHelper.lerp((double) partialTicks, this.yo, this.y) - vector3d.y());
		float f2 = (float) (MathHelper.lerp((double) partialTicks, this.zo, this.z) - vector3d.z());
		Quaternion quaternion;
		if (this.roll == 0.0F) {
			quaternion = activeRenderInfo.rotation();
		} else {
			quaternion = new Quaternion(activeRenderInfo.rotation());
			float f3 = MathHelper.lerp(partialTicks, this.oRoll, this.roll);
			quaternion.mul(Vector3f.ZP.rotation(f3));
		}

		Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
		vector3f1.transform(quaternion);
		Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float f4 = this.getQuadSize(partialTicks);

		for (int i = 0; i < 4; ++i) {
			Vector3f vector3f = avector3f[i];
			vector3f.transform(quaternion);
			vector3f.mul(f4);
			vector3f.add(f, f1, f2);
		}

		float f7 = this.getU0();
		float f8 = this.getU1();
		float f5 = this.getV0();
		float f6 = this.getV1();
		int j = this.getLightColor(partialTicks);
		builder.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		builder.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		builder.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		builder.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	/*private void renderParticle(IVertexBuilder buffer, double x, double y, double z, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY, float minU, float maxU, float minV, float maxV, float scale, float partialTicks) {
		int i = this.getLightColor(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		buffer.vertex(x - rotationX * scale - rotationYZ * scale, y - rotationXZ * scale, z - rotationZ * scale - rotationXY * scale).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(j, k).endVertex();
		buffer.vertex(x - rotationX * scale + rotationYZ * scale, y + rotationXZ * scale, z - rotationZ * scale + rotationXY * scale).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(j, k).endVertex();
		buffer.vertex(x + rotationX * scale + rotationYZ * scale, y + rotationXZ * scale, z + rotationZ * scale + rotationXY * scale).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(j, k).endVertex();
		buffer.vertex(x + rotationX * scale - rotationYZ * scale, y - rotationXZ * scale, z + rotationZ * scale - rotationXY * scale).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(j, k).endVertex();
	}*/

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<SnowParticleData> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite sprite) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle(SnowParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			SnowParticle particle = new SnowParticle(worldIn, typeIn.particleStart.x(), typeIn.particleStart.y(), typeIn.particleStart.z());
			particle.pickSprite(spriteSet);
			return particle;
		}
	}
}
