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

import net.minecraft.world.ColorizerFoliage;

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
import forestry.core.config.ForestryBlock;
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

		MinecraftForgeClient.registerItemRenderer(ForestryBlock.logs.item(), new RenderLogItem());
		MinecraftForgeClient.registerItemRenderer(ForestryBlock.logsFireproof.item(), new RenderLogItem());

		MinecraftForgeClient.registerItemRenderer(ForestryBlock.stairs.item(), new RenderStairItem());
		MinecraftForgeClient.registerItemRenderer(ForestryBlock.stairsFireproof.item(), new RenderStairItem());

		MinecraftForgeClient.registerItemRenderer(ForestryBlock.planks.item(), new RenderPlankItem());
		MinecraftForgeClient.registerItemRenderer(ForestryBlock.planksFireproof.item(), new RenderPlankItem());

		MinecraftForgeClient.registerItemRenderer(ForestryBlock.slabs.item(), new RenderSlabItem());
		MinecraftForgeClient.registerItemRenderer(ForestryBlock.slabsFireproof.item(), new RenderSlabItem());

		MinecraftForgeClient.registerItemRenderer(ForestryBlock.fences.item(), new RenderFenceItem());
		MinecraftForgeClient.registerItemRenderer(ForestryBlock.fencesFireproof.item(), new RenderFenceItem());

		MinecraftForgeClient.registerItemRenderer(ForestryBlock.leaves.item(), new RenderLeavesItem());
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
