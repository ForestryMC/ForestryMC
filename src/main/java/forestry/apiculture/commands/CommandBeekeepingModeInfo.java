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

import forestry.api.apiculture.IBeekeepingMode;
import forestry.core.commands.CommandHelpers;
import forestry.core.commands.SubCommand;
import forestry.plugins.PluginApiculture;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandBeekeepingModeInfo extends SubCommand {

	private String[] modeStringArr;
	private String helpString;

	public CommandBeekeepingModeInfo() {
		super("info");

		List<String> modeStrings = new ArrayList<String>();
		for (IBeekeepingMode mode : PluginApiculture.beeInterface.getBeekeepingModes())
			modeStrings.add(mode.getName());

		modeStringArr = modeStrings.toArray(new String[modeStrings.size()]);

		StringBuilder help = new StringBuilder();
		String separator = ", ";

		Iterator<String> iter = modeStrings.iterator();
		while (iter.hasNext()) {
			help.append(iter.next());
			if (iter.hasNext())
				help.append(separator);
		}
		helpString = help.toString();
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args) {
		if (args.length <= 0) {
			printHelp(sender);
			return;
		}

		String modeName = args[0];

		IBeekeepingMode found = findModeWithName(modeName);
		if (found == null) {
			CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.mode.info.error", modeName);
			printHelp(sender);
			return;
		}

		ChatStyle green = new ChatStyle();
		green.setColor(EnumChatFormatting.GREEN);
		CommandHelpers.sendLocalizedChatMessage(sender, green, found.getName());

		for (String desc : found.getDescription())
			CommandHelpers.sendLocalizedChatMessage(sender, "for." + desc);
	}

	private IBeekeepingMode findModeWithName(String name) {
		for (IBeekeepingMode mode : PluginApiculture.beeInterface.getBeekeepingModes()) {
			if (mode.getName().equalsIgnoreCase(name))
				return mode;
		}
		return null;
	}

	@Override
	public void printHelp(ICommandSender sender) {
		super.printHelp(sender);

		World world = CommandHelpers.getWorld(sender, this);

		String modeName = PluginApiculture.beeInterface.getBeekeepingMode(world).getName();
		String worldName = String.valueOf(world.getWorldInfo().getSaveVersion());

		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.mode.info.current", modeName, worldName);
		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.mode.info.available", helpString);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] incomplete) {
		return CommandHelpers.getListOfStringsMatchingLastWord(incomplete, modeStringArr);
	}
}
