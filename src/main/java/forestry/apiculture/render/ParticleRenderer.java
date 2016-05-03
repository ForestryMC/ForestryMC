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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import forestry.core.proxy.Proxies;

@SideOnly(Side.CLIENT)
public class ParticleRenderer {
	private static final String name = "forestry-particles";

	// singleton getter >>

	public static synchronized ParticleRenderer getInstance() {
		if (instance == null) {
			instance = new ParticleRenderer();
		}

		return instance;
	}

	private static ParticleRenderer instance = null;

	// << singleton getter


	public synchronized void addEffect(EntityFX particle) {
		if (lazyAdd) {
			newParticles.add(particle);
		} else {
			particles.add(particle);
		}
	}


	// forge + fml handlers >>

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		EntityPlayer thePlayer = Proxies.common.getPlayer();
		render(thePlayer, event.getPartialTicks());
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			synchronized (this) {
				particles.clear();
			}
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.END) {
			update();
		}
	}

	// << forge + fml handlers

	private ParticleRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private synchronized void update() {
		Minecraft.getMinecraft().mcProfiler.startSection(name + "-update");

		lazyAdd = true;

		for (Iterator<EntityFX> it = particles.iterator(); it.hasNext(); ) {
			EntityFX particle = it.next();

			particle.onUpdate();

			if (!particle.isAlive()) {
				it.remove();
			}
		}

		lazyAdd = false;
		particles.addAll(newParticles);
		newParticles.clear();

		Minecraft.getMinecraft().mcProfiler.endSection();
	}

	private synchronized void render(Entity entityIn, float partialTicks) {
		Minecraft.getMinecraft().mcProfiler.startSection(name + "-render");

		float rotationX = ActiveRenderInfo.getRotationX();
		float rotationZ = ActiveRenderInfo.getRotationZ();
		float rotationYZ = ActiveRenderInfo.getRotationYZ();
		float rotationXY = ActiveRenderInfo.getRotationXY();
		float rotationXZ = ActiveRenderInfo.getRotationXZ();

		EntityLivingBase player = (EntityLivingBase) Minecraft.getMinecraft().getRenderViewEntity();
		EntityFX.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		EntityFX.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		EntityFX.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		// bind the texture
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		// gl states/settings for drawing
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		for (EntityFX particle : particles) {
			particle.renderParticle(vertexBuffer, entityIn, partialTicks, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
		}

		tessellator.draw();

		// restore previous gl state
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

		Minecraft.getMinecraft().mcProfiler.endSection();
	}

	private boolean lazyAdd = false;
	private final List<EntityFX> particles = new ArrayList<>();
	private final List<EntityFX> newParticles = new ArrayList<>();
}
