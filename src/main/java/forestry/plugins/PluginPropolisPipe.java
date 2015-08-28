/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.plugins;

import java.util.EnumSet;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.network.IPacketHandler;
import forestry.core.proxy.Proxies;
import forestry.pipes.GuiHandlerPipes;
import forestry.pipes.network.PacketHandlerPipes;
import forestry.pipes.proxy.ProxyPipes;

@Plugin(pluginID = "Pipes", name = "Pipes", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.pipes.description")
public class PluginPropolisPipe extends ForestryPlugin {

	public static ProxyPipes proxy;
	/**
	 * Pipe used to sort bees from Forestry.
	 */
	public static Item pipeItemsPropolis;

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded("BuildCraft|Transport") &&
				Proxies.common.isModLoaded("BuildCraft|Core", "[6.0, 7.0)");
	}

	@Override
	public String getFailMessage() {
		return "BuildCraft|Transport not found";
	}

	@Override
	public EnumSet<PluginManager.Module> getDependancies() {
		EnumSet<PluginManager.Module> deps = super.getDependancies();
		deps.add(PluginManager.Module.APICULTURE);
		return deps;
	}

	@Override
	public void postInit() {
		String proxyClass = "forestry.pipes.proxy.ProxyPipes";
		if (FMLCommonHandler.instance().getSide().isClient()) {
			proxyClass = "forestry.pipes.proxy.ClientProxyPipes";
		}

		proxy = (ProxyPipes) Proxies.common.instantiateIfModLoaded("BuildCraft|Transport", proxyClass);

		if (proxy == null) {
			return;
		}

		proxy.initPropolisPipe();
		ProxyPipes.registerCraftingPropolis(ForestryItem.propolis.getItemStack());
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerPipes();
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerPipes();
	}

}
