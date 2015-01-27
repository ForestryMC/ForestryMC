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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;

import forestry.core.config.Config;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.render.BlockRenderingHandler;
import forestry.core.render.EntitySnowFX;
import forestry.core.render.RenderEscritoire;
import forestry.core.render.RenderMachine;
import forestry.core.render.RenderMill;
import forestry.core.render.TextureManager;
import forestry.core.render.TileRendererIndex;
import forestry.core.utils.ForestryResource;

public class ClientProxyRender extends ProxyRender {

	@Override
	public int getNextAvailableRenderId() {
		return RenderingRegistry.getNextAvailableRenderId();
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
		BlockRenderingHandler.byBlockRenderer.put(new TileRendererIndex(definition.block, definition.meta), definition.renderer);
		ClientRegistry.bindTileEntitySpecialRenderer(definition.teClass, (TileEntitySpecialRenderer) definition.renderer);
	}

	@Override
	public IBlockRenderer getRenderDefaultMachine(String gfxBase) {
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

	private boolean shouldSpawnParticle(World world, boolean canDisable) {
		if (canDisable && !Config.enableParticleFX) {
			return false;
		}
		Minecraft mc = FMLClientHandler.instance().getClient();
		int particleSetting = mc.gameSettings.particleSetting;
		if (!canDisable && particleSetting > 1) {
			particleSetting = 1;
		}
		if (particleSetting == 1 && world.rand.nextInt(3) == 0) {
			particleSetting = 2;
		}
		if (particleSetting > 1) {
			return false;
		}
		return true;
	}

	@Override
	public void addSnowFX(World world, double xCoord, double yCoord, double zCoord, int color, int areaX, int areaY, int areaZ) {
		if (!shouldSpawnParticle(world, true)) {
			return;
		}

		double spawnX = xCoord + world.rand.nextInt(areaX * 2) - areaX;
		double spawnY = yCoord + world.rand.nextInt(areaY);
		double spawnZ = zCoord + world.rand.nextInt(areaZ * 2) - areaZ;

		Proxies.common.getClientInstance().effectRenderer.addEffect(new EntitySnowFX(world, spawnX, spawnY, spawnZ, 0.0f, 0.0f, 0.0f));
	}

	@Override
	public short registerItemTexUID(IIconRegister register, short uid, String ident) {
		TextureManager.getInstance().registerTexUID(register, uid, ident);
		return uid;
	}

	@Override
	public short registerTerrainTexUID(IIconRegister register, short uid, String ident) {
		TextureManager.getInstance().registerTexUID(register, uid, ident);
		return uid;
	}

	@Override
	public void registerVillagerSkin(int villagerId, String texturePath) {
		VillagerRegistry.instance().registerVillagerSkin(villagerId, new ForestryResource(texturePath));
	}
}
