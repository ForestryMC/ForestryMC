/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

import forestry.api.core.IPlugin;
import forestry.api.core.PluginInfo;
import forestry.core.config.Version;
import forestry.core.proxy.Proxies;
import forestry.core.utils.CommandMC;
import forestry.plugins.PluginManager;

public class CommandForestry extends CommandMC {

	@Override
	public int compareTo(Object arg0) {
		return this.getCommandName().compareTo(((ICommand) arg0).getCommandName());
	}

	@Override
	public String getCommandName() {
		return "forestry";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " help";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) {

		if (arguments.length <= 0)
			throw new WrongUsageException("Type '" + this.getCommandUsage(sender) + "' for help.");

		if (arguments[0].matches("version")) {
			commandVersion(sender, arguments);
			return;
		} else if (arguments[0].matches("plugins")) {
			commandPlugins(sender, arguments);
			return;
		} else if (arguments[0].matches("help")) {
			sendChatMessage(sender, "Format: '" + this.getCommandName() + " <command> <arguments>'");
			sendChatMessage(sender, "Available commands:");
			sendChatMessage(sender, "- version : Version information.");
			sendChatMessage(sender, "- plugins : list of Forestry plugins.");
			sendChatMessage(sender, "- plugins info <plugin-name>: information on plugin.");
			return;
		}

		throw new WrongUsageException(this.getCommandUsage(sender));
	}

	private void commandVersion(ICommandSender sender, String[] arguments) {
		String colour = Version.isOutdated() ? "\u00A7c" : "\u00A7a";

		sendChatMessage(sender, String.format(colour + "Forestry %s for Minecraft %s (Latest: %s).", Version.getVersion(),
				Proxies.common.getMinecraftVersion(), Version.getRecommendedVersion()));
		if (Version.isOutdated())
			for (String updateLine : Version.getChangelog())
				sendChatMessage(sender, "\u00A79" + updateLine);
	}

	private void commandPlugins(ICommandSender sender, String[] arguments) {

		if (arguments.length <= 1)
			listPluginsForSender(sender);
		else if (arguments[1].matches("info"))
			listPluginInfoForSender(sender, arguments);

	}

	private void listPluginsForSender(ICommandSender sender) {

		String pluginList = "";

		for (IPlugin plugin : PluginManager.plugins) {
			if (!pluginList.isEmpty())
				pluginList += ", ";
			pluginList += makeListEntry(plugin);
		}

		sendChatMessage(sender, pluginList);
	}

	private void listPluginInfoForSender(ICommandSender sender, String[] arguments) {

		if (arguments.length < 3)
			throw new WrongUsageException("/" + getCommandName() + " plugins info <plugin-name>");

		IPlugin found = null;
		for (IPlugin plugin : PluginManager.plugins) {
			PluginInfo info = plugin.getClass().getAnnotation(PluginInfo.class);
			if (info == null)
				continue;

			if ((info.pluginID().equalsIgnoreCase(arguments[2]) || info.name().equalsIgnoreCase(arguments[2]))) {
				found = plugin;
				break;
			}
		}

		if (found == null)
			throw new CommandException("No information available for plugin " + arguments[2] + ".");

		String entry = "\u00A7c";
		if (found.isAvailable())
			entry = "\u00A7a";
		PluginInfo info = found.getClass().getAnnotation(PluginInfo.class);
		if (info != null) {
			sendChatMessage(sender, entry + "Plugin: " + info.name());
			if (!info.version().isEmpty())
				sendChatMessage(sender, "\u00A79Version: " + info.version());
			if (!info.author().isEmpty())
				sendChatMessage(sender, "\u00A79Author(s): " + info.author());
			if (!info.url().isEmpty())
				sendChatMessage(sender, "\u00A79URL: " + info.url());
			if (!info.description().isEmpty())
				sendChatMessage(sender, info.description());
		}

	}

	private String makeListEntry(IPlugin plugin) {
		String entry = "\u00A7c";
		if (plugin.isAvailable())
			entry = "\u00A7a";

		PluginInfo info = plugin.getClass().getAnnotation(PluginInfo.class);
		if (info != null) {
			entry += info.pluginID();
			if (!info.version().isEmpty())
				entry += " (" + info.version() + ")";
		} else
			entry += "???";

		return entry;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (sender instanceof EntityPlayer)
			return Proxies.common.isOp((EntityPlayer) sender);
		else
			return sender.canCommandSenderUseCommand(4, getCommandName());
	}

}
