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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public abstract class SubCommand implements IForestryCommand {

	public enum PermLevel {

		EVERYONE(0), ADMIN(2);
		public final int permLevel;

		PermLevel(int permLevel) {
			this.permLevel = permLevel;
		}

	}

	private final String name;
	private final List<String> aliases = new ArrayList<>();
	private PermLevel permLevel = PermLevel.EVERYONE;
	private IForestryCommand parent;
	private final SortedSet<SubCommand> children = new TreeSet<>(new Comparator<SubCommand>() {

		@Override
		public int compare(SubCommand o1, SubCommand o2) {
			return o1.compareTo(o2);
		}
	});

	public SubCommand(String name) {
		this.name = name;
	}

	@Override
	public final String getCommandName() {
		return name;
	}

	public SubCommand addChildCommand(SubCommand child) {
		child.setParent(this);
		children.add(child);
		return this;
	}

	void setParent(IForestryCommand parent) {
		this.parent = parent;
	}

	@Override
	public SortedSet<SubCommand> getChildren() {
		return children;
	}

	public void addAlias(String alias) {
		aliases.add(alias);
	}

	@Override
	public List<String> getCommandAliases() {
		return aliases;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] incomplete) {
		return CommandHelpers.addStandardTabCompletionOptions(this, sender, incomplete);
	}

	@Override
	public final void processCommand(ICommandSender sender, String[] args) {
		if (!CommandHelpers.processStandardCommands(sender, this, args)) {
			processSubCommand(sender, args);
		}
	}

	public void processSubCommand(ICommandSender sender, String[] args) {
		printHelp(sender);
	}

	public SubCommand setPermLevel(PermLevel permLevel) {
		this.permLevel = permLevel;
		return this;
	}

	@Override
	public final int getPermissionLevel() {
		return permLevel.permLevel;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.canCommandSenderUseCommand(getPermissionLevel(), getCommandName());
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getFullCommandString() + " help";
	}

	@Override
	public void printHelp(ICommandSender sender) {
		CommandHelpers.printHelp(sender, this);
	}

	@Override
	public String getFullCommandString() {
		return parent.getFullCommandString() + " " + getCommandName();
	}

	public int compareTo(ICommand command) {
		return this.getCommandName().compareTo(command.getCommandName());
	}

	@Override
	public int compareTo(@Nonnull Object command) {
		return this.compareTo((ICommand) command);
	}

}
