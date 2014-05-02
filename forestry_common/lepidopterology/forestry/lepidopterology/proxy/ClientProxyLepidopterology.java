/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.lepidopterology.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraftforge.client.MinecraftForgeClient;

import forestry.core.config.ForestryItem;
import forestry.core.utils.Localization;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.render.ButterflyItemRenderer;
import forestry.lepidopterology.render.RenderButterfly;

public class ClientProxyLepidopterology extends ProxyLepidopterology {

	@Override
	public void initializeRendering() {
		RenderingRegistry.registerEntityRenderingHandler(EntityButterfly.class, new RenderButterfly());
		MinecraftForgeClient.registerItemRenderer(ForestryItem.butterflyGE.item(), new ButterflyItemRenderer());
	}

	@Override
	public void addLocalizations() {
		Localization.instance.addLocalization("/lang/forestry/lepidopterology/");
	}

}
