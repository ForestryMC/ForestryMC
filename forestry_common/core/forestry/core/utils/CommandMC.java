/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.utils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public abstract class CommandMC extends CommandBase {

	protected World getWorld(ICommandSender sender, String[] arguments) {

		// Handle passed in world argument
		if (arguments.length > 2) {
			int dim = 0;
			try {
				dim = Integer.parseInt(arguments[1]);
			} catch (Exception ex) {
				throw new WrongUsageException("/" + getCommandName() + " set [<world-#>] <beekeeping-mode>");
			}

			return MinecraftServer.getServer().worldServerForDimension(dim);
		}

		if (sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			return player.worldObj;
		} else
			return MinecraftServer.getServer().worldServerForDimension(0);

	}

	protected String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	protected void sendChatMessage(ICommandSender sender, String message) {
		sender.addChatMessage(new ChatComponentText(message));
	}
}
