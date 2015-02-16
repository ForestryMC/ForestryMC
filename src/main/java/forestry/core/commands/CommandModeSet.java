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

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public final class CommandModeSet extends SubCommand {
	private final String[] modeStringArr;
	private final ICommandModeHelper modeSetter;

	public CommandModeSet(ICommandModeHelper modeSetter) {
		super("set");
		setPermLevel(PermLevel.ADMIN);

		this.modeSetter = modeSetter;
		modeStringArr = modeSetter.getModeNames();

	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args) {
		if (args.length == 0 || args.length > 2) {
			printHelp(sender);
			return;
		}

		World world;
		if (args.length == 2) {
			world = CommandHelpers.getWorld(sender, this, args, 0);
		} else {
			world = CommandHelpers.getWorld(sender, this);
		}

		String desired = args[args.length - 1];

		String modeName = modeSetter.getModeNameMatching(desired);
		if (modeName == null) {
			CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.set.error", desired);
			printHelp(sender);
			return;
		}

		modeSetter.setMode(world, modeName);
		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.mode.set.success", modeName);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] incomplete) {
		return CommandHelpers.getListOfStringsMatchingLastWord(incomplete, modeStringArr);
	}
}
