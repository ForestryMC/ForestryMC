/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.proxy;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

import forestry.apiculture.entities.EntityBee;
import forestry.apiculture.render.BeeItemRenderer;
import forestry.apiculture.render.EntityBeeFX;
import forestry.apiculture.render.ParticleRenderer;
import forestry.apiculture.render.RenderBee;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.render.RenderAnalyzer;
import forestry.core.render.TextureManager;
import forestry.core.utils.Localization;
import forestry.plugins.PluginApiculture;

public class ClientProxyApiculture extends ProxyApiculture {

	@Override
	public void initializeRendering() {
		if(PluginApiculture.fancyRenderedBees) {
			RenderingRegistry.registerEntityRenderingHandler(EntityBee.class, new RenderBee());

			MinecraftForgeClient.registerItemRenderer(ForestryItem.beeDroneGE.item(), new BeeItemRenderer());
			MinecraftForgeClient.registerItemRenderer(ForestryItem.beePrincessGE.item(), new BeeItemRenderer());
			MinecraftForgeClient.registerItemRenderer(ForestryItem.beeQueenGE.item(), new BeeItemRenderer());
		}
	}

	@Override
	public void addBeeHiveFX(String icon, World world, double xCoord, double yCoord, double zCoord, int color, int areaX, int areaY, int areaZ) {
		if (!Config.enableParticleFX)
			return;

		EntityFX fx;

		if (world.rand.nextBoolean()) {
			fx = new EntityBeeFX(world, xCoord + 0.5D, yCoord + 0.75D, zCoord + 0.5D, 0.0f, 0.0f, 0.0f, color);
		} else {
			double spawnX = xCoord + world.rand.nextInt(areaX * 2) - areaX;
			double spawnY = yCoord + world.rand.nextInt(areaY);
			double spawnZ = zCoord + world.rand.nextInt(areaZ * 2) - areaZ;

			fx = new EntityBeeFX(world, spawnX, spawnY, spawnZ, 0.0f, 0.0f, 0.0f, color);
		}

		fx.setParticleIcon(TextureManager.getInstance().getDefault(icon));
		ParticleRenderer.getInstance().addEffect(fx);
	}

	@Override
	public void addBeeSwarmFX(String icon, World world, double xCoord, double yCoord, double zCoord, int color) {
		if (!Config.enableParticleFX)
			return;

		EntityFX fx;

		if (world.rand.nextBoolean())
			fx = new EntityBeeFX(world, xCoord, yCoord, zCoord, 0.0f, 0.0f, 0.0f, color);
		else {

			double spawnX = xCoord + world.rand.nextInt(4) - 2;
			double spawnY = yCoord + world.rand.nextInt(4) - 2;
			double spawnZ = zCoord + world.rand.nextInt(4) - 2;

			fx = new EntityBeeFX(world, spawnX, spawnY, spawnZ, 0.0f, 0.0f, 0.0f, color);
		}

		fx.setParticleIcon(TextureManager.getInstance().getDefault(icon));
		ParticleRenderer.getInstance().addEffect(fx);
	}

	@Override
	public IBlockRenderer getRendererAnalyzer(String gfxBase) {
		return new RenderAnalyzer(gfxBase);
	}

	@Override
	public void addLocalizations() {
		Localization.instance.addLocalization("/lang/forestry/apiculture/");
	}

}
