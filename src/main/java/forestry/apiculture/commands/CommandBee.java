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
package forestry.apiculture.commands;

import java.util.List;

import forestry.core.commands.CommandMode;
import forestry.core.commands.CommandSaveStats;
import forestry.core.commands.ICommandModeHelper;
import forestry.core.commands.IStatsSaveHelper;
import forestry.core.commands.SubCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandBee extends SubCommand {

	public CommandBee() {
		super("bee");
		addAlias("beekeeping");

		IStatsSaveHelper saveHelper = new BeeStatsSaveHelper();
		ICommandModeHelper modeHelper = new BeeModeHelper();

		addChildCommand(new CommandMode(modeHelper));
		addChildCommand(new CommandSaveStats(saveHelper, modeHelper));
		addChildCommand(new CommandBeeGive());
	}

	@Override
	public String getCommandName() {
		return null;
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return false;
	}
}
