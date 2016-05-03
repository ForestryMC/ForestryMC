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
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.text.TextFormatting;

import forestry.core.utils.Translator;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.IForestryPlugin;
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
	public void processSubCommand(ICommandSender sender, String[] args) throws WrongUsageException {
		if (args.length == 0) {
			listPluginsForSender(sender);
		} else {
			CommandHelpers.throwWrongUsage(sender, this);
		}
	}

	private static void listPluginsForSender(ICommandSender sender) {
		StringBuilder pluginList = new StringBuilder();
		for (IForestryPlugin plugin : PluginManager.getLoadedPlugins()) {
			if (pluginList.length() > 0) {
				pluginList.append(", ");
			}
			pluginList.append(makeListEntry(plugin));
		}
		CommandHelpers.sendChatMessage(sender, pluginList.toString());
	}

	private static String makeListEntry(IForestryPlugin plugin) {
		String entry = plugin.isAvailable() ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();

		ForestryPlugin info = plugin.getClass().getAnnotation(ForestryPlugin.class);
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
		public void processSubCommand(ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 1) {
				listPluginInfoForSender(sender, args[0]);
			} else {
				CommandHelpers.throwWrongUsage(sender, this);
			}
		}

		private static void listPluginInfoForSender(ICommandSender sender, String pluginUid) throws CommandException {
			IForestryPlugin found = null;
			for (IForestryPlugin plugin : PluginManager.getLoadedPlugins()) {
				ForestryPlugin info = plugin.getClass().getAnnotation(ForestryPlugin.class);
				if (info == null) {
					continue;
				}

				if (info.pluginID().equalsIgnoreCase(pluginUid) || info.name().equalsIgnoreCase(pluginUid)) {
					found = plugin;
					break;
				}
			}

			if (found == null) {
				throw new CommandException(Translator.translateToLocalFormatted("for.chat.plugins.error", pluginUid));
			}

			TextFormatting formatting = found.isAvailable() ? TextFormatting.GREEN : TextFormatting.RED;

			ForestryPlugin info = found.getClass().getAnnotation(ForestryPlugin.class);
			if (info != null) {
				CommandHelpers.sendChatMessage(sender, formatting + "Plugin: " + info.name());
				if (!info.version().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, TextFormatting.BLUE + "Version: " + info.version());
				}
				if (!info.author().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, TextFormatting.BLUE + "Author(s): " + info.author());
				}
				if (!info.url().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, TextFormatting.BLUE + "URL: " + info.url());
				}
				if (!info.unlocalizedDescription().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, Translator.translateToLocal(info.unlocalizedDescription()));
				}
			}

		}

	}

}
