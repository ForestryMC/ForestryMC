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

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
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
	public void processSubCommand(ICommandSender sender, String[] args) {
		if (args.length <= 0) {
			printHelp(sender);
			return;
		}

		String modeName = modeHelper.getModeNameMatching(args[0]);

		if (modeName == null) {
			CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.info.error", args[0]);
			printHelp(sender);
			return;
		}

		ChatStyle green = new ChatStyle();
		green.setColor(EnumChatFormatting.GREEN);
		CommandHelpers.sendLocalizedChatMessage(sender, green, modeName);

		for (String desc : modeHelper.getDescription(modeName)) {
			CommandHelpers.sendLocalizedChatMessage(sender, "for." + desc);
		}
	}

	@Override
	public void printHelp(ICommandSender sender) {
		super.printHelp(sender);

		World world = CommandHelpers.getWorld(sender, this);

		String modeName = modeHelper.getModeName(world);
		String worldName = String.valueOf(world.getWorldInfo().getSaveVersion());

		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.info.current", modeName, worldName);
		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.info.available", helpString);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] incomplete) {
		return CommandHelpers.getListOfStringsMatchingLastWord(incomplete, modeStringArr);
	}
}
