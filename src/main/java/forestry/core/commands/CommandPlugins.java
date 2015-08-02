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

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import forestry.core.utils.StringUtil;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginManager;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandPlugins extends SubCommand {

	public CommandPlugins() {
		super("plugins");
		addAlias("plug");
		addChildCommand(new CommandPluginsInfo());
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args) {
		if (args.length == 0) {
			listPluginsForSender(sender);
		} else {
			CommandHelpers.throwWrongUsage(sender, this);
		}
	}

	private static void listPluginsForSender(ICommandSender sender) {
		StringBuilder pluginList = new StringBuilder();
		for (PluginManager.Module pluginModule : PluginManager.getLoadedModules()) {
			if (pluginList.length() > 0) {
				pluginList.append(", ");
			}
			pluginList.append(makeListEntry(pluginModule.instance()));
		}
		CommandHelpers.sendChatMessage(sender, pluginList.toString());
	}

	private static String makeListEntry(ForestryPlugin plugin) {
		String entry = plugin.isAvailable() ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString();

		Plugin info = plugin.getClass().getAnnotation(Plugin.class);
		if (info != null) {
			entry += info.pluginID();
			if (!info.version().isEmpty()) {
				entry += " (" + info.version() + ")";
			}
		} else {
			entry += "???";
		}

		return entry;
	}

	public static class CommandPluginsInfo extends SubCommand {

		public CommandPluginsInfo() {
			super("info");
			addAlias("i");
		}

		@Override
		public void processSubCommand(ICommandSender sender, String[] args) {
			if (args.length == 1) {
				listPluginInfoForSender(sender, args[0]);
			} else {
				CommandHelpers.throwWrongUsage(sender, this);
			}
		}

		private static void listPluginInfoForSender(ICommandSender sender, String plugin) {
			ForestryPlugin found = null;
			for (PluginManager.Module pluginModule : PluginManager.getLoadedModules()) {
				Plugin info = pluginModule.instance().getClass().getAnnotation(Plugin.class);
				if (info == null) {
					continue;
				}

				if ((info.pluginID().equalsIgnoreCase(plugin) || info.name().equalsIgnoreCase(plugin))) {
					found = pluginModule.instance();
					break;
				}
			}

			if (found == null) {
				throw new CommandException(StringUtil.localizeAndFormat("chat.plugins.error", plugin));
			}

			EnumChatFormatting formatting = found.isAvailable() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;

			Plugin info = found.getClass().getAnnotation(Plugin.class);
			if (info != null) {
				CommandHelpers.sendChatMessage(sender, formatting + "Plugin: " + info.name());
				if (!info.version().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, EnumChatFormatting.BLUE + "Version: " + info.version());
				}
				if (!info.author().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, EnumChatFormatting.BLUE + "Author(s): " + info.author());
				}
				if (!info.url().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, EnumChatFormatting.BLUE + "URL: " + info.url());
				}
				if (!info.unlocalizedDescription().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, StatCollector.translateToLocal(info.unlocalizedDescription()));
				}
			}

		}

	}

}
