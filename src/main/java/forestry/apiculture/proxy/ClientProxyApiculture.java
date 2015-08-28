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

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import forestry.apiculture.entities.EntityBee;
import forestry.apiculture.render.BeeItemRenderer;
import forestry.apiculture.render.EntityBeeFX;
import forestry.apiculture.render.ParticleRenderer;
import forestry.apiculture.render.RenderBee;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.ClientProxyRender;
import forestry.core.render.RenderAnalyzer;
import forestry.core.render.TextureManager;
import forestry.plugins.PluginApiculture;

public class ClientProxyApiculture extends ProxyApiculture {

	@Override
	public void initializeRendering() {
		if (PluginApiculture.fancyRenderedBees) {
			RenderingRegistry.registerEntityRenderingHandler(EntityBee.class, new RenderBee());

			MinecraftForgeClient.registerItemRenderer(ForestryItem.beeDroneGE.item(), new BeeItemRenderer());
			MinecraftForgeClient.registerItemRenderer(ForestryItem.beePrincessGE.item(), new BeeItemRenderer());
			MinecraftForgeClient.registerItemRenderer(ForestryItem.beeQueenGE.item(), new BeeItemRenderer());
		}
	}

	@Override
	public void addBeeHiveFX(String icon, World world, BlockPos coordinates, int color) {
		if (!ClientProxyRender.shouldSpawnParticle(world)) {
			return;
		}

		EntityFX fx = new EntityBeeFX(world, coordinates.getX() + 0.5D, coordinates.getY() + 0.5D, coordinates.getZ() + 0.5D, color);
		fx.setParticleIcon(TextureManager.getInstance().getDefault(icon).getSprite());
		ParticleRenderer.getInstance().addEffect(fx);
	}

	@Override
	public void addBeeSwarmFX(String icon, World world, double xCoord, double yCoord, double zCoord, int color) {
		if (!ClientProxyRender.shouldSpawnParticle(world)) {
			return;
		}

		EntityFX fx;

		if (world.rand.nextBoolean()) {
			fx = new EntityBeeFX(world, xCoord, yCoord, zCoord, color);
		} else {

			double spawnX = xCoord + world.rand.nextInt(4) - 2;
			double spawnY = yCoord + world.rand.nextInt(4) - 2;
			double spawnZ = zCoord + world.rand.nextInt(4) - 2;

			fx = new EntityBeeFX(world, spawnX, spawnY, spawnZ, color);
		}

		fx.setParticleIcon(TextureManager.getInstance().getDefault(icon).getSprite());
		ParticleRenderer.getInstance().addEffect(fx);
	}

	@Override
	public IBlockRenderer getRendererAnalyzer(String gfxBase) {
		return new RenderAnalyzer(gfxBase);
	}

}
