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
package forestry.arboriculture.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.ColorizerFoliage;

import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.RenderingRegistry;

import forestry.arboriculture.render.RenderFenceItem;
import forestry.arboriculture.render.RenderFruitPodBlock;
import forestry.arboriculture.render.RenderLeavesBlock;
import forestry.arboriculture.render.RenderLeavesItem;
import forestry.arboriculture.render.RenderLogItem;
import forestry.arboriculture.render.RenderPlankItem;
import forestry.arboriculture.render.RenderSaplingBlock;
import forestry.arboriculture.render.RenderSlabItem;
import forestry.arboriculture.render.RenderStairItem;
import forestry.plugins.PluginArboriculture;

public class ProxyArboricultureClient extends ProxyArboriculture {
	@Override
	public void initializeRendering() {
		PluginArboriculture.modelIdSaplings = RenderingRegistry.getNextAvailableRenderId();
		PluginArboriculture.modelIdLeaves = RenderingRegistry.getNextAvailableRenderId();
		PluginArboriculture.modelIdPods = RenderingRegistry.getNextAvailableRenderId();

		RenderingRegistry.registerBlockHandler(new RenderSaplingBlock());
		RenderingRegistry.registerBlockHandler(new RenderLeavesBlock());
		RenderingRegistry.registerBlockHandler(new RenderFruitPodBlock());

		registerItemRenderer(PluginArboriculture.blocks.logs, new RenderLogItem());
		registerItemRenderer(PluginArboriculture.blocks.logsFireproof, new RenderLogItem());

		registerItemRenderer(PluginArboriculture.blocks.stairs, new RenderStairItem());
		registerItemRenderer(PluginArboriculture.blocks.stairsFireproof, new RenderStairItem());

		registerItemRenderer(PluginArboriculture.blocks.planks, new RenderPlankItem());
		registerItemRenderer(PluginArboriculture.blocks.planksFireproof, new RenderPlankItem());

		registerItemRenderer(PluginArboriculture.blocks.slabs, new RenderSlabItem());
		registerItemRenderer(PluginArboriculture.blocks.slabsFireproof, new RenderSlabItem());

		registerItemRenderer(PluginArboriculture.blocks.fences, new RenderFenceItem());
		registerItemRenderer(PluginArboriculture.blocks.fencesFireproof, new RenderFenceItem());

		registerItemRenderer(PluginArboriculture.blocks.leaves, new RenderLeavesItem());
	}

	private static void registerItemRenderer(Block block, IItemRenderer renderer) {
		Item item = Item.getItemFromBlock(block);
		MinecraftForgeClient.registerItemRenderer(item, renderer);
	}

	@Override
	public int getFoliageColorBasic() {
		return ColorizerFoliage.getFoliageColorBasic();
	}

	@Override
	public int getFoliageColorBirch() {
		return ColorizerFoliage.getFoliageColorBirch();
	}

	@Override
	public int getFoliageColorPine() {
		return ColorizerFoliage.getFoliageColorPine();
	}
}
