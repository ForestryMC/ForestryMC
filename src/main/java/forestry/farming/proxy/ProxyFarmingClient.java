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
package forestry.farming.proxy;

import deleteme.Todos;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.inventory.InventoryMenu;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import forestry.core.models.ClientManager;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.features.FarmingBlocks;
import forestry.farming.models.ModelFarmBlock;
import forestry.modules.IClientModuleHandler;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyFarmingClient extends ProxyFarming implements IClientModuleHandler {

	@Override
	public void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
		Todos.todo();
		ClientManager.getInstance().registerModel(new ModelFarmBlock(), FarmingBlocks.FARM);
	}

	@Override
	public void setupClient(FMLClientSetupEvent event) {
		FarmingBlocks.FARM.getBlocks().forEach((block) -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped()));
	}

	@Override
	public void registerSprites(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location() != InventoryMenu.BLOCK_ATLAS) {
			return;
		}
		EnumFarmBlockType.gatherSprites(event);
	}

	@Override
	public void handleSprites(TextureStitchEvent.Post event) {
		if (event.getAtlas().location() != InventoryMenu.BLOCK_ATLAS) {
			return;
		}
		EnumFarmBlockType.fillSprites(event);
	}
}
