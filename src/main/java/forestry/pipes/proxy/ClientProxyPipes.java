/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes.proxy;

import buildcraft.transport.TransportProxyClient;
import forestry.plugins.PluginPropolisPipe;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxyPipes extends ProxyPipes {

	@Override
	public void registerCustomItemRenderer(Item item, IItemRenderer basemod) {
		MinecraftForgeClient.registerItemRenderer(item, basemod);
	}

	@Override
	public void initPropolisPipe() {
		super.initPropolisPipe();
		registerCustomItemRenderer(PluginPropolisPipe.pipeItemsPropolis, TransportProxyClient.pipeItemRenderer);
	}
}
