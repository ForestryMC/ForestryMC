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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import forestry.core.config.Config;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IBlockRenderer;
import forestry.core.render.BlockRenderingHandler;
import forestry.core.render.ModelIndex;
import forestry.core.render.ModelManager;
import forestry.core.render.RenderEscritoire;
import forestry.core.render.RenderMachine;
import forestry.core.render.RenderMill;
import forestry.core.render.TextureManager;
import forestry.core.utils.ForestryResource;

public class ClientProxyRender extends ProxyRender {

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
	
	@Override
	public void registerModel(final ModelIndex index) {
		BlockRenderingHandler.byModelRenderer.add(index);
		StateMapperBase ignoreState = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
			     return index.blockModel;
			}
		};
		ModelLoader.setCustomStateMapper(index.block, ignoreState);
	}
	
	@Override
	public void registerStateMapper(Block block, IStateMapper mapper){
		ModelLoader.setCustomStateMapper(block, mapper);
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
	public short registerItemTexUID(String modifier, short uid, String ident) {
		TextureManager.getInstance().registerTexUID(uid, modifier, ident);
		return uid;
	}

	@Override
	public short registerTerrainTexUID(String modifier, short uid, String ident) {
		TextureManager.getInstance().registerTexUID(uid, modifier, ident);
		return uid;
	}

	@Override
	public void registerVillagerSkin(int villagerId, String texturePath) {
		VillagerRegistry.instance().registerVillagerSkin(villagerId, new ForestryResource(texturePath));
	}
	
	@Override
	public void initModels(){
		ModelManager.registerModels();
	}
}
