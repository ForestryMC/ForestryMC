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
package forestry.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;

import forestry.apiculture.entities.EntityFXBee;
import forestry.apiculture.render.ParticleRenderer;
import forestry.apiculture.render.RenderCandleBlock;
import forestry.apiculture.render.TextureHabitatLocator;
import forestry.core.config.Config;
import forestry.core.entities.EntityFXHoneydust;
import forestry.core.entities.EntityFXIgnition;
import forestry.core.entities.EntityFXSnow;
import forestry.core.render.ForestryResource;
import forestry.core.render.IBlockRenderer;
import forestry.core.render.RenderBlock;
import forestry.core.render.RenderEscritoire;
import forestry.core.render.RenderMachine;
import forestry.core.render.RenderMill;
import forestry.core.render.RenderNaturalistChest;
import forestry.core.render.SpriteSheet;
import forestry.core.render.TextureManager;
import forestry.core.render.TileRendererIndex;
import forestry.core.tiles.MachineDefinition;

public class ProxyRenderClient extends ProxyRender {

	private int byBlockModelId;
	private int candleRenderId;

	@Override
	public void init() {
		byBlockModelId = RenderingRegistry.getNextAvailableRenderId();
		candleRenderId = RenderingRegistry.getNextAvailableRenderId();

		RenderBlock renderHandler = new RenderBlock();
		RenderingRegistry.registerBlockHandler(byBlockModelId, renderHandler);
		RenderingRegistry.registerBlockHandler(candleRenderId, new RenderCandleBlock());
	}

	@Override
	public int getCandleRenderId() {
		return candleRenderId;
	}

	@Override
	public int getByBlockModelRenderId() {
		return byBlockModelId;
	}

	@Override
	public boolean fancyGraphicsEnabled() {
		return Proxies.common.getClientInstance().gameSettings.fancyGraphics;
	}

	@Override
	public boolean hasRendering() {
		return true;
	}

	@Override
	public void registerTESR(MachineDefinition definition) {
		RenderBlock.byBlockRenderer.put(new TileRendererIndex(definition.getBlock(), definition.getMeta()), definition.renderer);
		ClientRegistry.bindTileEntitySpecialRenderer(definition.teClass, (TileEntitySpecialRenderer) definition.renderer);
	}

	@Override
	public IBlockRenderer getRenderDefaultMachine(String gfxBase) {
		if (gfxBase == null) {
			return null;
		}
		return new RenderMachine(gfxBase);
	}

	@Override
	public IBlockRenderer getRenderMill(String gfxBase) {
		return new RenderMill(gfxBase);
	}

	@Override
	public IBlockRenderer getRenderMill(String gfxBase, byte charges) {
		return new RenderMill(gfxBase, charges);
	}

	@Override
	public IBlockRenderer getRenderEscritoire() {
		return new RenderEscritoire();
	}

	@Override
	public IBlockRenderer getRenderChest(String textureName) {
		return new RenderNaturalistChest(textureName);
	}

	@Override
	public void registerVillagerSkin(int villagerId, String texturePath) {
		VillagerRegistry.instance().registerVillagerSkin(villagerId, new ForestryResource(texturePath));
	}

	@Override
	public void setHabitatLocatorTexture(Entity player, ChunkCoordinates coordinates) {
		TextureHabitatLocator.getInstance().setTargetCoordinates(coordinates);
	}

	@Override
	public IResourceManager getSelectedTexturePack() {
		return Proxies.common.getClientInstance().getResourceManager();
	}

	@Override
	public void bindTexture(ResourceLocation location) {
		Proxies.common.getClientInstance().getTextureManager().bindTexture(location);
	}

	@Override
	public void bindTexture(SpriteSheet spriteSheet) {
		bindTexture(spriteSheet.getLocation());
	}

	public static boolean shouldSpawnParticle(World world) {
		if (!Config.enableParticleFX) {
			return false;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();
		int particleSetting = mc.gameSettings.particleSetting;

		// minimal
		if (particleSetting == 2) {
			return world.rand.nextInt(10) == 0;
		}

		// decreased
		if (particleSetting == 1) {
			return world.rand.nextInt(3) != 0;
		}

		// all
		return true;
	}

	@Override
	public void addBeeHiveFX(String icon, World world, double d1, double d2, double d3, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		EntityFX fx = new EntityFXBee(world, d1, d2, d3, color);
		fx.setParticleIcon(TextureManager.getInstance().getDefault(icon));
		ParticleRenderer.getInstance().addEffect(fx);
	}

	@Override
	public void addEntitySwarmFX(World world, double d1, double d2, double d3) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityFXHoneydust(world, d1, d2, d3, 0, 0, 0));
	}

	@Override
	public void addEntityExplodeFX(World world, double d1, double d2, double d3) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityExplodeFX(world, d1, d2, d3, 0, 0, 0));
	}

	@Override
	public void addEntitySnowFX(World world, double d1, double d2, double d3) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityFXSnow(world, d1 + world.rand.nextGaussian(), d2, d3 + world.rand.nextGaussian()));
	}

	@Override
	public void addEntityIgnitionFX(World world, double d1, double d2, double d3) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		Proxies.common.getClientInstance().effectRenderer.addEffect(new EntityFXIgnition(world, d1, d2, d3));
	}

	@Override
	public void addEntityPotionFX(World world, double d1, double d2, double d3, int color) {
		if (!shouldSpawnParticle(world)) {
			return;
		}

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		EntityFX entityfx = new EntitySpellParticleFX(world, d1, d2, d3, 0, 0, 0);
		entityfx.setRBGColorF(red, green, blue);

		Proxies.common.getClientInstance().effectRenderer.addEffect(entityfx);
	}
}
