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
//import java.util.List;
//
//import net.minecraft.command.ICommandSender;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//public final class CommandModeSet extends SubCommand {
//	private final String[] modeStringArr;
//	private final ICommandModeHelper modeSetter;
//
//	public CommandModeSet(ICommandModeHelper modeSetter) {
//		super("set");
//		setPermLevel(PermLevel.ADMIN);
//
//		this.modeSetter = modeSetter;
//		modeStringArr = modeSetter.getModeNames();
//
//	}
//
//	@Override
//	public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) {
//		if (args.length == 0 || args.length > 2) {
//			printHelp(sender);
//			return;
//		}
//
//		World world = sender.getEntityWorld();
//
//		String modeName = args[args.length - 1];
//
//		if (modeSetter.setMode(world, modeName)) {
//			CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.set.success", modeName);
//		} else {
//			CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.set.error", modeName);
//			printHelp(sender);
//		}
//	}
//
//	@Override
//	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
//		return CommandHelpers.getListOfStringsMatchingLastWord(args, modeStringArr);
//	}
//}
