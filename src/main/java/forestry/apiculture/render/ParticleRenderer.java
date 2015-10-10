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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

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
		render(event.partialTicks);
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
		FMLCommonHandler.instance().bus().register(this);
	}

	private synchronized void update() {
		Minecraft.getMinecraft().mcProfiler.startSection(name + "-update");

		lazyAdd = true;

		for (Iterator<EntityFX> it = particles.iterator(); it.hasNext(); ) {
			EntityFX particle = it.next();

			particle.onUpdate();

			if (particle.isDead) {
				it.remove();
			}
		}

		lazyAdd = false;
		particles.addAll(newParticles);
		newParticles.clear();

		Minecraft.getMinecraft().mcProfiler.endSection();
	}

	private synchronized void render(float partialTicks) {
		Minecraft.getMinecraft().mcProfiler.startSection(name + "-render");

		float rotationX = ActiveRenderInfo.rotationX;
		float rotationZ = ActiveRenderInfo.rotationZ;
		float rotationYZ = ActiveRenderInfo.rotationYZ;
		float rotationXY = ActiveRenderInfo.rotationXY;
		float rotationXZ = ActiveRenderInfo.rotationXZ;

		EntityLivingBase player = Minecraft.getMinecraft().renderViewEntity;
		EntityFX.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		EntityFX.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		EntityFX.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		// bind the texture
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);

		// save the old gl state
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// gl states/settings for drawing
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();

		for (EntityFX particle : particles) {
			tessellator.setBrightness(particle.getBrightnessForRender(partialTicks));

			particle.renderParticle(tessellator, partialTicks, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
		}

		tessellator.draw();

		// restore previous gl state
		GL11.glPopAttrib();

		Minecraft.getMinecraft().mcProfiler.endSection();
	}

	private boolean lazyAdd = false;
	private final List<EntityFX> particles = new ArrayList<>();
	private final List<EntityFX> newParticles = new ArrayList<>();
}
