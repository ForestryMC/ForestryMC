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
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
//
//import forestry.core.config.Constants;
//
///**
// * @author CovertJaguar <http://www.railcraft.info/>
// */
//public class RootCommand extends CommandBase implements IForestryCommand {
//
//	public static final String ROOT_COMMAND_NAME = Constants.MOD_ID;
//	public static final String ROOT_COMMAND_ALIAS = "for";
//
//	private final SortedSet<SubCommand> children = new TreeSet<>();
//
//	public void addChildCommand(SubCommand child) {
//		child.setParent(this);
//		children.add(child);
//	}
//
//	/* CommandBase */
//
//	@Override
//	public String getName() {
//		return ROOT_COMMAND_NAME;
//	}
//
//	@Override
//	public String getUsage(ICommandSender sender) {
//		return "/" + this.getName() + " help";
//	}
//
//	@Override
//	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//		if (!CommandHelpers.processStandardCommands(server, sender, this, args)) {
//			CommandHelpers.throwWrongUsage(sender, this);
//		}
//	}
//
//	/**
//	 * Used only for CommandBase.
//	 * It gets obfuscated, so the name needs to be different from the one in IForestryCommand.
//	 */
//	@Override
//	public int getRequiredPermissionLevel() {
//		return getPermissionLevel();
//	}
//
//	@Override
//	public List<String> getAliases() {
//		List<String> aliases = new ArrayList<>();
//		aliases.add(ROOT_COMMAND_ALIAS);
//		return aliases;
//	}
//
//	@Override
//	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
//		return CommandHelpers.addStandardTabCompletionOptions(server, this, sender, args, targetPos);
//	}
//
//	/* IForestryCommand */
//
//	@Override
//	public String getFullCommandString() {
//		return getName();
//	}
//
//	@Override
//	public int getPermissionLevel() {
//		return 0;
//	}
//
//	@Override
//	public SortedSet<SubCommand> getChildren() {
//		return children;
//	}
//
//	@Override
//	public void printHelp(ICommandSender sender) {
//		CommandHelpers.printHelp(sender, this);
//	}
//
//}
