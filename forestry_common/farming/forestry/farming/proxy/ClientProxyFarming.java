/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.proxy;

import net.minecraft.item.Item;

import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

import forestry.core.config.ForestryBlock;
import forestry.core.utils.Localization;
import forestry.farming.render.FarmItemRenderer;
import forestry.farming.render.FarmRenderingHandler;
import forestry.plugins.PluginFarming;

public class ClientProxyFarming extends ProxyFarming {

	@Override
	public void initializeRendering() {
		PluginFarming.modelIdFarmBlock = RenderingRegistry.getNextAvailableRenderId();

		RenderingRegistry.registerBlockHandler(new FarmRenderingHandler());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ForestryBlock.farm), new FarmItemRenderer());
	}

	@Override
	public void addLocalizations() {
		Localization.instance.addLocalization("/lang/forestry/farming/");
	}

}
