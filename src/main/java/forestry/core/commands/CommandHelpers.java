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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import forestry.core.utils.Translator;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandHelpers {

	public static void sendLocalizedChatMessage(ICommandSender sender, String locTag, Object... args) {
		sender.sendMessage(new TextComponentTranslation(locTag, args));
	}

	public static void sendLocalizedChatMessage(ICommandSender sender, Style chatStyle, String locTag, Object... args) {
		TextComponentTranslation chat = new TextComponentTranslation(locTag, args);
		chat.setStyle(chatStyle);
		sender.sendMessage(chat);
	}

	/**
	 * Avoid using this function if at all possible. Commands are processed on the server,
	 * which has no localization information.
	 * <p>
	 * StringUtil.localize() is NOT a valid alternative for sendLocalizedChatMessage().
	 * Messages will not be localized properly if you use StringUtil.localize().
	 */
	public static void sendChatMessage(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

	public static void throwWrongUsage(ICommandSender sender, IForestryCommand command) throws WrongUsageException {
		throw new WrongUsageException(Translator.translateToLocalFormatted("for.chat.help", command.getUsage(sender)));
	}

	public static void processChildCommand(MinecraftServer server, ICommandSender sender, SubCommand child, String[] args) throws CommandException {
		if (!sender.canUseCommand(child.getPermissionLevel(), child.getFullCommandString())) {
			throw new WrongUsageException(Translator.translateToLocal("for.chat.command.noperms"));
		}
		String[] newargs = new String[args.length - 1];
		System.arraycopy(args, 1, newargs, 0, newargs.length);
		child.execute(server, sender, newargs);
	}

	public static void printHelp(ICommandSender sender, IForestryCommand command) {

		String commandString = command.getFullCommandString().replace(" ", ".");

		Style header = new Style();
		header.setColor(TextFormatting.BLUE);
		sendLocalizedChatMessage(sender, header, "for.chat.command." + commandString + ".format", command.getFullCommandString());

		Style body = new Style();
		body.setColor(TextFormatting.GRAY);

		List<String> commandAliases = command.getAliases();
		if (!commandAliases.isEmpty()) {
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
				sendLocalizedChatMessage(sender, "for.chat.command." + child.getFullCommandString().replace(" ", ".") + ".desc", child.getName());
			}
		}
	}

	public static boolean processStandardCommands(MinecraftServer server, ICommandSender sender, IForestryCommand command, String[] args) throws CommandException {
		if (args.length >= 1) {
			if (args[0].equals("help")) {
				command.printHelp(sender);
				return true;
			}
			for (SubCommand child : command.getChildren()) {
				if (matches(args[0], child)) {
					processChildCommand(server, sender, child, args);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean matches(String commandName, IForestryCommand command) {
		if (commandName.equals(command.getName())) {
			return true;
		} else {
			for (String alias : command.getAliases()) {
				if (commandName.equals(alias)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<String> getListOfStringsMatchingLastWord(String[] strings, String... lastWords) {
		return CommandBase.getListOfStringsMatchingLastWord(strings, lastWords);
	}

	public static List<String> addStandardTabCompletionOptions(MinecraftServer server, IForestryCommand command, ICommandSender sender, String[] incomplete, @Nullable BlockPos pos) {
		if (incomplete.length > 1) {
			String commandName = incomplete[0];
			for (SubCommand child : command.getChildren()) {
				if (CommandHelpers.matches(commandName, child)) {
					String[] incompleteRemaining = Arrays.copyOfRange(incomplete, 1, incomplete.length);
					return child.getTabCompletions(server, sender, incompleteRemaining, pos);
				}
			}
		}

		List<String> commandNames = new ArrayList<>();
		for (SubCommand child : command.getChildren()) {
			commandNames.add(child.getName());
		}
		commandNames.add("help");

		String[] commandNamesArr = commandNames.toArray(new String[commandNames.size()]);
		return CommandHelpers.getListOfStringsMatchingLastWord(incomplete, commandNamesArr);
	}

}
