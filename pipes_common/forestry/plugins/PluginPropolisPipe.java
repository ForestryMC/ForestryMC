/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.plugins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.core.PluginInfo;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.proxy.Proxies;
import forestry.pipes.GuiHandlerPipes;
import forestry.pipes.network.PacketHandlerPipes;
import forestry.pipes.proxy.ProxyPipes;

@PluginInfo(pluginID = "Pipes", name = "Pipes", author = "SirSengir", url = Defaults.URL, description = "Adds the apiarist's pipe for beekeeping if apiculture is enabled and BuildCraft 3 is present.")
public class PluginPropolisPipe extends NativePlugin {

	public static ProxyPipes proxy;

	static String CONFIG_CATEGORY = "pipes";
	public static Configuration config;
	public static int propolisPipeItemId;

	public static String textureBees = Defaults.TEXTURE_PATH_GUI + "/analyzer_icons.png";
	/**
	 * Pipe used to sort bees from Forestry.
	 */
	public static Item pipeItemsPropolis;

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded("BuildCraft|Transport");
	}

	@Override
	public void preInit() {

		config = new Configuration();

		propolisPipeItemId = Integer.parseInt(PluginPropolisPipe.config.get("propolisPipe", CONFIG_CATEGORY, 14000).Value);

		config.save();

	}

	@Override
	public void doInit() {
	}

	@Override
	public void postInit() {
		String proxyClass = "forestry.pipes.proxy.ProxyPipes";
		if (FMLCommonHandler.instance().getSide().isClient())
			proxyClass = "forestry.pipes.proxy.ClientProxyPipes";

		proxy = (ProxyPipes) Proxies.common.instantiateIfModLoaded("BuildCraft|Transport", proxyClass);

		if (proxy == null)
			return;

		proxy.initPropolisPipe();
		proxy.registerCraftingPropolis(ForestryItem.propolis.getItemStack());
		proxy.addLocalizations();

	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerPipes();
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerPipes();
	}
	
	@Override
	protected void registerItems() {
	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	protected void registerRecipes() {
	}

}
