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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import forestry.core.utils.StringUtil;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandHelpers {

	public static World getWorld(ICommandSender sender, IForestryCommand command, String[] args, int worldArgIndex) {
		// Handle passed in world argument
		if (worldArgIndex < args.length) {
			try {
				int dim = Integer.parseInt(args[worldArgIndex]);
				World world = MinecraftServer.getServer().worldServerForDimension(dim);
				if (world != null) {
					return world;
				}
			} catch (Exception ex) {
				throwWrongUsage(sender, command);
			}
		}
		return getWorld(sender, command);
	}

	public static World getWorld(ICommandSender sender, IForestryCommand command) {
		return sender.getEntityWorld();
	}

	public static EntityPlayerMP getPlayer(ICommandSender sender, String playerName) {
		return CommandBase.getPlayer(sender, playerName);
	}

	public static String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	public static void sendLocalizedChatMessage(ICommandSender sender, String locTag, Object... args) {
		sender.addChatMessage(new ChatComponentTranslation(locTag, args));
	}

	public static void sendLocalizedChatMessage(ICommandSender sender, ChatStyle chatStyle, String locTag, Object... args) {
		ChatComponentTranslation chat = new ChatComponentTranslation(locTag, args);
		chat.setChatStyle(chatStyle);
		sender.addChatMessage(chat);
	}

	/**
	 * Avoid using this function if at all possible. Commands are processed on the server,
	 * which has no localization information.
	 *
	 * StringUtil.localize() is NOT a valid alternative for sendLocalizedChatMessage().
	 * Messages will not be localized properly if you use StringUtil.localize().
	 */
	public static void sendChatMessage(ICommandSender sender, String message) {
		sender.addChatMessage(new ChatComponentText(message));
	}

	public static void throwWrongUsage(ICommandSender sender, IForestryCommand command) throws WrongUsageException {
		throw new WrongUsageException(StringUtil.localizeAndFormat("chat.help", command.getCommandUsage(sender)));
	}

	public static void processChildCommand(ICommandSender sender, SubCommand child, String[] args) {
		if (!sender.canCommandSenderUseCommand(child.getPermissionLevel(), child.getFullCommandString())) {
			throw new WrongUsageException(StringUtil.localize("chat.command.noperms"));
		}
		String[] newargs = new String[args.length - 1];
		System.arraycopy(args, 1, newargs, 0, newargs.length);
		child.processCommand(sender, newargs);
	}

	public static void printHelp(ICommandSender sender, IForestryCommand command) {

		String commandString = command.getFullCommandString().replace(" ", ".");

		ChatStyle header = new ChatStyle();
		header.setColor(EnumChatFormatting.BLUE);
		sendLocalizedChatMessage(sender, header, "for.chat.command." + commandString + ".format", command.getFullCommandString());

		ChatStyle body = new ChatStyle();
		body.setColor(EnumChatFormatting.GRAY);

		List<String> commandAliases = command.getCommandAliases();
		if (commandAliases.size() > 0) {
			sendLocalizedChatMessage(sender, body, "for.chat.command.aliases", commandAliases.toString().replace("[", "").replace("]", ""));
		}

		int permLevel = command.getPermissionLevel();
		if (permLevel > 0) {
			sendLocalizedChatMessage(sender, body, "for.chat.command.permlevel", permLevel);
		}

		sendLocalizedChatMessage(sender, body, "for.chat.command." + commandString + ".help");

		if (!command.getChildren().isEmpty()) {
			sendLocalizedChatMessage(sender, "for.chat.command.list");
			for (SubCommand child : command.getChildren()) {
				sendLocalizedChatMessage(sender, "for.chat.command." + child.getFullCommandString().replace(" ", ".") + ".desc", child.getCommandName());
			}
		}
	}

	public static boolean processStandardCommands(ICommandSender sender, IForestryCommand command, String[] args) {
		if (args.length >= 1) {
			if (args[0].equals("help")) {
				command.printHelp(sender);
				return true;
			}
			for (SubCommand child : command.getChildren()) {
				if (matches(args[0], child)) {
					processChildCommand(sender, child, args);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean matches(String commandName, IForestryCommand command) {
		if (commandName.equals(command.getCommandName())) {
			return true;
		} else if (command.getCommandAliases() != null) {
			for (String alias : command.getCommandAliases()) {
				if (commandName.equals(alias)) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static List<String> getListOfStringsMatchingLastWord(String[] strings, String... lastWords) {
		return CommandBase.getListOfStringsMatchingLastWord(strings, lastWords);
	}

	public static List<String> addStandardTabCompletionOptions(IForestryCommand command, ICommandSender sender, String[] incomplete) {
		if (incomplete.length > 1) {
			String commandName = incomplete[0];
			for (SubCommand child : command.getChildren()) {
				if (CommandHelpers.matches(commandName, child)) {
					String[] incompleteRemaining = Arrays.copyOfRange(incomplete, 1, incomplete.length);
					return child.addTabCompletionOptions(sender, incompleteRemaining);
				}
			}
		}

		List<String> commandNames = new ArrayList<>();
		for (SubCommand child : command.getChildren()) {
			commandNames.add(child.getCommandName());
		}
		commandNames.add("help");

		String[] commandNamesArr = commandNames.toArray(new String[commandNames.size()]);
		return CommandHelpers.getListOfStringsMatchingLastWord(incomplete, commandNamesArr);
	}

}
