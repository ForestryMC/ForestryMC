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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import forestry.api.modules.ForestryModule;
import forestry.api.modules.IForestryModule;
import forestry.core.utils.Translator;
import forestry.modules.ModuleManager;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandModules extends SubCommand {

	public CommandModules() {
		super("module");
		addAlias("mod");
		addChildCommand(new CommandPluginsInfo());
	}

	@Override
	public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			listModulesForSender(sender);
		} else {
			CommandHelpers.throwWrongUsage(sender, this);
		}
	}

	private static void listModulesForSender(ICommandSender sender) {
		StringBuilder pluginList = new StringBuilder();
		for (IForestryModule module : ModuleManager.getLoadedModules()) {
			if (pluginList.length() > 0) {
				pluginList.append(", ");
			}
			pluginList.append(makeListEntry(module));
		}
		CommandHelpers.sendChatMessage(sender, pluginList.toString());
	}

	private static String makeListEntry(IForestryModule module) {
		String entry = module.isAvailable() ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();

		ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
		if (info != null) {
			entry += info.moduleID();
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
		public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 1) {
				listModuleInfoForSender(sender, args[0]);
			} else {
				CommandHelpers.throwWrongUsage(sender, this);
			}
		}

		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
			List<String> tabCompletions = super.getTabCompletions(server, sender, args, targetPos);
			for (IForestryModule module : ModuleManager.getLoadedModules()) {
				ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
				if (info != null) {
					tabCompletions.add(info.moduleID());
				}
			}
			return tabCompletions;
		}

		private static void listModuleInfoForSender(ICommandSender sender, String pluginUid) throws CommandException {
			IForestryModule found = null;
			for (IForestryModule module : ModuleManager.getLoadedModules()) {
				ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
				if (info == null) {
					continue;
				}

				if (info.moduleID().equalsIgnoreCase(pluginUid) || info.name().equalsIgnoreCase(pluginUid)) {
					found = module;
					break;
				}
			}

			if (found == null) {
				throw new CommandException(Translator.translateToLocalFormatted("for.chat.modules.error", pluginUid));
			}

			TextFormatting formatting = found.isAvailable() ? TextFormatting.GREEN : TextFormatting.RED;

			ForestryModule info = found.getClass().getAnnotation(ForestryModule.class);
			if (info != null) {
				CommandHelpers.sendChatMessage(sender, formatting + "Module: " + info.name());
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
