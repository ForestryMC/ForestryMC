/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes.proxy;

import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.core.utils.Localization;
import buildcraft.transport.TransportProxyClient;
import forestry.plugins.PluginPropolisPipe;

public class ClientProxyPipes extends ProxyPipes {

	@Override
	public void registerCustomItemRenderer(int itemID, IItemRenderer basemod) {
		MinecraftForgeClient.registerItemRenderer(itemID, basemod);
	}

	@Override
	public void initPropolisPipe() {
		super.initPropolisPipe();
		registerCustomItemRenderer(PluginPropolisPipe.pipeItemsPropolis.itemID, TransportProxyClient.pipeItemRenderer);
	}

	@Override
	public void addLocalizations() {
		Localization.addLocalization("/lang/forestry/pipes/", "en_US");
	}

}
