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

import javax.annotation.Nullable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CommandModeInfo extends SubCommand {
	private final String[] modeStringArr;
	private final String helpString;
	private final ICommandModeHelper modeHelper;

	public CommandModeInfo(ICommandModeHelper modeHelper) {
		super("info");

		this.modeHelper = modeHelper;
		modeStringArr = modeHelper.getModeNames();
		helpString = StringUtils.join(modeStringArr, ", ");
	}

	@Override
	public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length <= 0) {
			printHelp(sender);
			return;
		}

		String modeName = args[0];

		if (modeName == null) {
			CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.info.error", args[0]);
			printHelp(sender);
			return;
		}

		Style green = new Style();
		green.setColor(TextFormatting.GREEN);
		CommandHelpers.sendLocalizedChatMessage(sender, green, modeName);

		for (String desc : modeHelper.getDescription(modeName)) {
			CommandHelpers.sendLocalizedChatMessage(sender, "for." + desc);
		}
	}

	@Override
	public void printHelp(ICommandSender sender) {
		super.printHelp(sender);

		World world = sender.getEntityWorld();

		String modeName = modeHelper.getModeName(world);
		String worldName = String.valueOf(world.getWorldInfo().getSaveVersion());

		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.info.current", modeName, worldName);
		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.info.available", helpString);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return CommandHelpers.getListOfStringsMatchingLastWord(args, modeStringArr);
	}
}
