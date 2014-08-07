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
