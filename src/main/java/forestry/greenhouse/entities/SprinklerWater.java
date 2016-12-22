package forestry.greenhouse.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class SprinklerWater extends Particle {

	public SprinklerWater(World world, double x, double y, double z, Vec3d vector, ResourceLocation texture) {
		super(world, x, y, z, 0, 0, 0);
		this.particleScale = 0.3F;
		this.motionX = vector.xCoord;
		this.motionY = vector.yCoord;
		this.motionZ = vector.zCoord;
		this.particleMaxAge = 15;
		this.setSize(0.2f, 0.2f);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(VertexBuffer worldRenderer, Entity entity, float partialTicks, float f0, float f1, float f2, float f3, float f4) {
		float f6 = 0;
		float f7 = 1;
		float f8 = 0;
		float f9 = 1;
		float f10 = 0.1F * this.particleScale;
		float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * f0 - interpPosX);
		float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * f0 - interpPosY);
		float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * f0 - interpPosZ);
		Minecraft.getMinecraft().renderEngine.bindTexture(FluidRegistry.WATER.getStill());
		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		worldRenderer.pos((f11 - f1 * f10 - f4 * f10), (f12 - f2 * f10), (f13 - f3 * f10 - f6 * f10)).color(1F, 1F, 1F, 0.5F).tex(f7, f9).lightmap(j, k).endVertex();
		worldRenderer.pos((f11 - f1 * f10 + f4 * f10), (f12 + f2 * f10), (f13 - f3 * f10 + f6 * f10)).color(1F, 1F, 1F, 0.5F).tex(f7, f8).lightmap(j, k).endVertex();
		worldRenderer.pos((f11 + f1 * f10 + f4 * f10), (f12 + f2 * f10), (f13 + f3 * f10 + f6 * f10)).color(1F, 1F, 1F, 0.5F).tex(f6, f8).lightmap(j, k).endVertex();
		worldRenderer.pos((f11 + f1 * f10 - f4 * f10), (f12 - f2 * f10), (f13 + f3 * f10 - f6 * f10)).color(1F, 1F, 1F, 0.5F).tex(f6, f9).lightmap(j, k).endVertex();
	}

	// avoid calculating lighting for water, it is too much processing
	@Override
	public int getBrightnessForRender(float p_189214_1_) {
		return 15728880;
	}
}