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
package forestry.apiculture.proxy;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.RenderingRegistry;

import forestry.apiculture.entities.EntityBee;
import forestry.apiculture.entities.EntityFXBee;
import forestry.apiculture.render.ParticleRenderer;
import forestry.apiculture.render.RenderBeeEntity;
import forestry.apiculture.render.RenderBeeItem;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.ProxyRenderClient;
import forestry.core.render.IBlockRenderer;
import forestry.core.render.RenderAnalyzer;
import forestry.core.render.TextureManager;
import forestry.plugins.PluginApiculture;

public class ProxyApicultureClient extends ProxyApiculture {

	@Override
	public void initializeRendering() {
		if (PluginApiculture.fancyRenderedBees) {
			RenderingRegistry.registerEntityRenderingHandler(EntityBee.class, new RenderBeeEntity());

			MinecraftForgeClient.registerItemRenderer(ForestryItem.beeDroneGE.item(), new RenderBeeItem());
			MinecraftForgeClient.registerItemRenderer(ForestryItem.beePrincessGE.item(), new RenderBeeItem());
			MinecraftForgeClient.registerItemRenderer(ForestryItem.beeQueenGE.item(), new RenderBeeItem());
		}
	}

	@Override
	public void addBeeHiveFX(String icon, World world, ChunkCoordinates coordinates, int color) {
		if (!ProxyRenderClient.shouldSpawnParticle(world)) {
			return;
		}

		EntityFX fx = new EntityFXBee(world, coordinates.posX + 0.5D, coordinates.posY + 0.5D, coordinates.posZ + 0.5D, color);
		fx.setParticleIcon(TextureManager.getInstance().getDefault(icon));
		ParticleRenderer.getInstance().addEffect(fx);
	}

	@Override
	public void addBeeSwarmFX(String icon, World world, double xCoord, double yCoord, double zCoord, int color) {
		if (!ProxyRenderClient.shouldSpawnParticle(world)) {
			return;
		}

		EntityFX fx;

		if (world.rand.nextBoolean()) {
			fx = new EntityFXBee(world, xCoord, yCoord, zCoord, color);
		} else {

			double spawnX = xCoord + world.rand.nextInt(4) - 2;
			double spawnY = yCoord + world.rand.nextInt(4) - 2;
			double spawnZ = zCoord + world.rand.nextInt(4) - 2;

			fx = new EntityFXBee(world, spawnX, spawnY, spawnZ, color);
		}

		fx.setParticleIcon(TextureManager.getInstance().getDefault(icon));
		ParticleRenderer.getInstance().addEffect(fx);
	}

	@Override
	public IBlockRenderer getRendererAnalyzer(String gfxBase) {
		return new RenderAnalyzer(gfxBase);
	}

}
