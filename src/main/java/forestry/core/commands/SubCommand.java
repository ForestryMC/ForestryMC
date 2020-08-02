///*******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// ******************************************************************************/
//package forestry.core.commands;
//
//import javax.annotation.Nullable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.SortedSet;
//import java.util.TreeSet;
//
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommand;
////import net.minecraft.command.ICommandSender;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
//
//import com.mojang.brigadier.Command;
//
///**
// * @author CovertJaguar <http://www.railcraft.info/>
// */
//public abstract class SubCommand<S> implements IForestryCommand<S> {
//
//	public enum PermLevel {
//
//		EVERYONE(0), ADMIN(2);
//		public final int permLevel;
//
//		PermLevel(int permLevel) {
//			this.permLevel = permLevel;
//		}
//
//	}
//
//	private final String name;
//	private final List<String> aliases = new ArrayList<>();
//	private PermLevel permLevel = PermLevel.EVERYONE;
//	@Nullable
//	private IForestryCommand parent;
//	private final SortedSet<SubCommand> children = new TreeSet<>();
//
//	public SubCommand(String name) {
//		this.name = name;
//	}
//
//	@Override
//	public String getName() {
//		return name;
//	}
//
//	public SubCommand addChildCommand(SubCommand child) {
//		child.setParent(this);
//		children.add(child);
//		return this;
//	}
//
//	void setParent(IForestryCommand parent) {
//		this.parent = parent;
//	}
//
//	@Override
//	public SortedSet<SubCommand> getChildren() {
//		return children;
//	}
//
//	public void addAlias(String alias) {
//		aliases.add(alias);
//	}
//
//	@Override
//	public List<String> getAliases() {
//		return aliases;
//	}
//
//	@Override
//	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
//		return CommandHelpers.addStandardTabCompletionOptions(server, this, sender, args, targetPos);
//	}
//
//	@Override
//	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//		if (!CommandHelpers.processStandardCommands(server, sender, this, args)) {
//			executeSubCommand(server, sender, args);
//		}
//	}
//
//	public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//		printHelp(sender);
//	}
//
//	public SubCommand setPermLevel(PermLevel permLevel) {
//		this.permLevel = permLevel;
//		return this;
//	}
//
//	@Override
//	public final int getPermissionLevel() {
//		return permLevel.permLevel;
//	}
//
//	@Override
//	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
//		return sender.canUseCommand(getPermissionLevel(), getName());
//	}
//
//	@Override
//	public boolean isUsernameIndex(String[] args, int index) {
//		return false;
//	}
//
//	@Override
//	public String getUsage(ICommandSender sender) {
//		return "/" + getFullCommandString() + " help";
//	}
//
//	@Override
//	public void printHelp(ICommandSender sender) {
//		CommandHelpers.printHelp(sender, this);
//	}
//
//	@Override
//	public String getFullCommandString() {
//		if (parent == null) {
//			return getName();
//		}
//		return parent.getFullCommandString() + " " + getName();
//	}
//
//	@Override
//	public int compareTo(Command command) {
//		return this.getName().compareTo(command.getName());
//	}
//
//}
