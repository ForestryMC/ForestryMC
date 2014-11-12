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
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandHelpers {

	public static World getWorld(ICommandSender sender, IForestryCommand command, String[] args, int worldArgIndex) {

		// Handle passed in world argument
		if (worldArgIndex < args.length)
			try {
				int dim = Integer.parseInt(args[worldArgIndex]);
				World world = MinecraftServer.getServer().worldServerForDimension(dim);
				if (world != null)
					return world;
			} catch (Exception ex) {
				throwWrongUsage(sender, command);
			}

		if (sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			return player.worldObj;
		} else
			return MinecraftServer.getServer().worldServerForDimension(0);

	}

	public static String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	public static void sendLocalizedChatMessage(ICommandSender sender, String locTag, Object... args) {
		sender.addChatMessage(new ChatComponentTranslation(locTag, args));
	}

	public static void sendChatMessage(ICommandSender sender, String message) {
		sender.addChatMessage(new ChatComponentText(message));
	}

	public static void throwWrongUsage(ICommandSender sender, IForestryCommand command) throws WrongUsageException {
		throw new WrongUsageException(StringUtil.localizeAndFormat("chat.help", command.getCommandUsage(sender)));
	}

	public static void processChildCommand(ICommandSender sender, SubCommand child, String[] args) {
		if (!sender.canCommandSenderUseCommand(child.getRequiredPermissionLevel(), child.getFullCommandString()))
			throw new WrongUsageException(StringUtil.localize("chat.command.noperms"));
		String[] newargs = new String[args.length - 1];
		System.arraycopy(args, 1, newargs, 0, newargs.length);
		child.processCommand(sender, newargs);
	}

	public static void printHelp(ICommandSender sender, IForestryCommand command) {
		sendLocalizedChatMessage(sender, "for.chat.command.format", command.getCommandFormat(sender));
		sendLocalizedChatMessage(sender, "for.chat.command.aliases", command.getCommandAliases().toString().replace("[", "").replace("]", ""));
		sendLocalizedChatMessage(sender, "for.chat.command.permlevel", command.getRequiredPermissionLevel());
		sendLocalizedChatMessage(sender, "for.chat.command." + command.getFullCommandString().replace(" ", ".") + ".help");
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
		if (commandName.equals(command.getCommandName()))
			return true;
		else if (command.getCommandAliases() != null)
			for (String alias : command.getCommandAliases()) {
				if (commandName.equals(alias))
					return true;
			}
		return false;
	}
}
