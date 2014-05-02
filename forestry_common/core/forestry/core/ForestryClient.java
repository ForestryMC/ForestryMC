/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core;

import cpw.mods.fml.client.registry.RenderingRegistry;

import forestry.apiculture.render.BlockCandleRenderer;
import forestry.core.proxy.Proxies;
import forestry.core.render.BlockRenderingHandler;

public class ForestryClient extends ForestryCore {

	public static int byBlockModelId;
	public static int candleRenderId;
	public static int blockModelIdEngine;

	@Override
	public void init(Object basemod) {

		super.init(basemod);

		byBlockModelId = Proxies.render.getNextAvailableRenderId();
		candleRenderId = Proxies.render.getNextAvailableRenderId();
		blockModelIdEngine = Proxies.render.getNextAvailableRenderId();

		BlockRenderingHandler renderHandler = new BlockRenderingHandler();
		RenderingRegistry.registerBlockHandler(byBlockModelId, renderHandler);
		RenderingRegistry.registerBlockHandler(candleRenderId, new BlockCandleRenderer());
	}

	@Override
	public void postInit() {
		super.postInit();
	}

}
