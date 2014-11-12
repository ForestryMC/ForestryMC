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
package forestry.lepidopterology.commands;

import forestry.core.commands.*;
import forestry.lepidopterology.entities.EntityButterfly;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandButterfly extends SubCommand {

	public CommandButterfly() {
		addAlias("bfly");
		addChildCommand(new CommandButterflyKill());
	}

	@Override
	public String getCommandName() {
		return "butterfly";
	}

	@Override
	public String getCommandFormat(ICommandSender sender) {
		return "/" + getFullCommandString() + " <args>";
	}

	public static class CommandButterflyKill extends SubCommand {

		public CommandButterflyKill() {
			addAlias("killall");
		}

		@Override
		public String getCommandName() {
			return "kill";
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public String getCommandFormat(ICommandSender sender) {
			return "/" + getFullCommandString() + " [<dimension#>]";
		}

		@Override
		public void processSubCommand(ICommandSender sender, String[] args) {
			if (args.length > 1)
				CommandHelpers.throwWrongUsage(sender, this);

			World world = CommandHelpers.getWorld(sender, this, args, 0);
			if (world == null)
				CommandHelpers.throwWrongUsage(sender, this);
			else
				for (Object entity : world.loadedEntityList) {
					if (entity instanceof EntityButterfly)
						((EntityButterfly) entity).setDead();
				}
		}

	}

}
