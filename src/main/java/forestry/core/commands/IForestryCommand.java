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

import java.util.List;
import java.util.SortedSet;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IForestryCommand extends ICommand {

	String getFullCommandString();

	@Override
	List<String> getCommandAliases();

	int getPermissionLevel();

	SortedSet<SubCommand> getChildren();

	void printHelp(ICommandSender sender);
}
