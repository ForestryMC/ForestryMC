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
package forestry.core.commands;

import forestry.core.utils.StringUtil;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.StatCollector;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandPlugins extends SubCommand {

	public CommandPlugins() {
		addChildCommand(new CommandPluginsInfo());
	}

	@Override
	public String getCommandName() {
		return "plugins";
	}

	@Override
	public String getCommandFormat(ICommandSender sender) {
		return "/" + getFullCommandString() + " [info <plugin-name>]";
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args) {
		if (args.length == 0)
			listPluginsForSender(sender);
		else
			CommandHelpers.throwWrongUsage(sender, this);
	}

	private void listPluginsForSender(ICommandSender sender) {
		String pluginList = "";
		for (PluginManager.Module pluginModule : PluginManager.getLoadedModules()) {
			if (!pluginList.isEmpty())
				pluginList += ", ";
			pluginList += makeListEntry(pluginModule.instance());
		}
		CommandHelpers.sendChatMessage(sender, pluginList);
	}

	private String makeListEntry(ForestryPlugin plugin) {
		String entry = "\u00A7c";
		if (plugin.isAvailable())
			entry = "\u00A7a";

		Plugin info = plugin.getClass().getAnnotation(Plugin.class);
		if (info != null) {
			entry += info.pluginID();
			if (!info.version().isEmpty())
				entry += " (" + info.version() + ")";
		} else
			entry += "???";

		return entry;
	}

	public static class CommandPluginsInfo extends SubCommand {

		@Override
		public String getCommandName() {
			return "info";
		}

		@Override
		public void processSubCommand(ICommandSender sender, String[] args) {
			if (args.length == 1)
				listPluginInfoForSender(sender, args[0]);
			else
				CommandHelpers.throwWrongUsage(sender, this);
		}

		private void listPluginInfoForSender(ICommandSender sender, String plugin) {
			ForestryPlugin found = null;
			for (PluginManager.Module pluginModule : PluginManager.getLoadedModules()) {
				Plugin info = pluginModule.instance().getClass().getAnnotation(Plugin.class);
				if (info == null)
					continue;

				if ((info.pluginID().equalsIgnoreCase(plugin) || info.name().equalsIgnoreCase(plugin))) {
					found = pluginModule.instance();
					break;
				}
			}

			if (found == null)
				throw new CommandException(StringUtil.localizeAndFormat("chat.plugins.error", plugin));

			String entry = "\u00A7c";
			if (found.isAvailable())
				entry = "\u00A7a";
			Plugin info = found.getClass().getAnnotation(Plugin.class);
			if (info != null) {
				CommandHelpers.sendChatMessage(sender, entry + "Plugin: " + info.name());
				if (!info.version().isEmpty())
					CommandHelpers.sendChatMessage(sender, "\u00A79Version: " + info.version());
				if (!info.author().isEmpty())
					CommandHelpers.sendChatMessage(sender, "\u00A79Author(s): " + info.author());
				if (!info.url().isEmpty())
					CommandHelpers.sendChatMessage(sender, "\u00A79URL: " + info.url());
				if (!info.unlocalizedDescription().isEmpty())
					CommandHelpers.sendChatMessage(sender, StatCollector.translateToLocal(info.unlocalizedDescription()));
			}

		}

	}

}
