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
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CommandBeekeepingModeSet extends SubCommand {
	private final String[] modeStringArr;

	public CommandBeekeepingModeSet() {
		super("set");
		setPermLevel(PermLevel.ADMIN);
		int modeStringCount = PluginApiculture.beeInterface.getBeekeepingModes().size();
		List<String> modeStrings = new ArrayList<String>(modeStringCount);
		for (IBeekeepingMode mode : PluginApiculture.beeInterface.getBeekeepingModes())
			modeStrings.add(mode.getName());

		modeStringArr = modeStrings.toArray(new String[modeStringCount]);
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args) {
		if (args.length == 0 || args.length > 2) {
			printHelp(sender);
			return;
		}

		World world;
		if (args.length == 2)
			world = CommandHelpers.getWorld(sender, this, args, 0);
		else
			world = CommandHelpers.getWorld(sender, this);

		String desired = args[args.length - 1];

		IBeekeepingMode mode = PluginApiculture.beeInterface.getBeekeepingMode(desired);
		if (mode == null) {
			CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.mode.set.error", desired);
			printHelp(sender);
			return;
		}

		PluginApiculture.beeInterface.setBeekeepingMode(world, mode.getName());
		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.mode.set.success", mode.getName());
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] incomplete) {
		return CommandHelpers.getListOfStringsMatchingLastWord(incomplete, modeStringArr);
	}
}
