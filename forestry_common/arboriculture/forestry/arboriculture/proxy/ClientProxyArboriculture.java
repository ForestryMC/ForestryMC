/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.proxy;

import net.minecraft.item.Item;
import net.minecraft.world.ColorizerFoliage;

import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

import forestry.arboriculture.render.FenceRenderingHandler;
import forestry.arboriculture.render.FruitPodRenderingHandler;
import forestry.arboriculture.render.LeavesRenderingHandler;
import forestry.arboriculture.render.SaplingRenderHandler;
import forestry.arboriculture.render.StairItemRenderer;
import forestry.core.config.ForestryBlock;
import forestry.core.utils.Localization;
import forestry.plugins.PluginArboriculture;

public class ClientProxyArboriculture extends ProxyArboriculture {
	@Override
	public void initializeRendering() {
		PluginArboriculture.modelIdSaplings = RenderingRegistry.getNextAvailableRenderId();
		PluginArboriculture.modelIdLeaves = RenderingRegistry.getNextAvailableRenderId();
		PluginArboriculture.modelIdFences = RenderingRegistry.getNextAvailableRenderId();
		PluginArboriculture.modelIdPods = RenderingRegistry.getNextAvailableRenderId();

		RenderingRegistry.registerBlockHandler(new SaplingRenderHandler());
		RenderingRegistry.registerBlockHandler(new LeavesRenderingHandler());
		RenderingRegistry.registerBlockHandler(new FenceRenderingHandler());
		RenderingRegistry.registerBlockHandler(new FruitPodRenderingHandler());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ForestryBlock.stairs), new StairItemRenderer());
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

	@Override
	public void addLocalizations() {
		Localization.instance.addLocalization("/lang/forestry/arboriculture/");
	}

}
