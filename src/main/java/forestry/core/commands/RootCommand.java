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
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class RootCommand extends CommandBase implements IForestryCommand {

	public static final String ROOT_COMMAND_NAME = "forestry";
	public static final String ROOT_COMMAND_ALIAS = "for";

	private final SortedSet<SubCommand> children = new TreeSet<>(new Comparator<SubCommand>() {

		@Override
		public int compare(SubCommand o1, SubCommand o2) {
			return o1.compareTo(o2);
		}
	});

	public void addChildCommand(SubCommand child) {
		child.setParent(this);
		children.add(child);
	}

	/* CommandBase */

	@Override
	public String getCommandName() {
		return ROOT_COMMAND_NAME;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " help";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (!CommandHelpers.processStandardCommands(sender, this, args)) {
			CommandHelpers.throwWrongUsage(sender, this);
		}
	}

	/**
	 * Used only for CommandBase.
	 * It gets obfuscated, so the name needs to be different from the one in IForestryCommand.
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return getPermissionLevel();
	}

	@Override
	public List<String> getCommandAliases() {
		List<String> aliases = new ArrayList<>();
		aliases.add(ROOT_COMMAND_ALIAS);
		return aliases;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] incomplete) {
		return CommandHelpers.addStandardTabCompletionOptions(this, sender, incomplete);
	}

	/* IForestryCommand */

	@Override
	public String getFullCommandString() {
		return getCommandName();
	}

	@Override
	public int getPermissionLevel() {
		return 0;
	}

	@Override
	public SortedSet<SubCommand> getChildren() {
		return children;
	}

	@Override
	public void printHelp(ICommandSender sender) {
		CommandHelpers.printHelp(sender, this);
	}

}
