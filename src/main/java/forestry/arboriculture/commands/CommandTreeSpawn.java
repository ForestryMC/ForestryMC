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
package forestry.arboriculture.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.commands.CommandHelpers;
import forestry.core.commands.SubCommand;

public final class CommandTreeSpawn extends SubCommand {

	private final ITreeSpawner treeSpawner;

	public CommandTreeSpawn(String name, ITreeSpawner treeSpawner) {
		super(name);
		setPermLevel(PermLevel.ADMIN);
		this.treeSpawner = treeSpawner;
	}

	@Override
	public final void processSubCommand(ICommandSender sender, String[] arguments) {
		if (arguments.length < 1 || arguments.length > 2) {
			printHelp(sender);
			return;
		}

		EntityPlayer player;
		String treeName;
		try {
			player = CommandHelpers.getPlayer(sender, arguments[arguments.length - 1]);
			String[] argumentsWithoutPlayer = new String[arguments.length - 1];
			System.arraycopy(arguments, 0, argumentsWithoutPlayer, 0, arguments.length - 1);
			treeName = StringUtils.join(argumentsWithoutPlayer, " ");
		} catch (PlayerNotFoundException e) {
			player = CommandHelpers.getPlayer(sender, sender.getCommandSenderName());
			treeName = StringUtils.join(arguments, " ");
		}

		boolean success = treeSpawner.spawn(sender, treeName, player);
		if (!success) {
			printHelp(sender);
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] parameters) {
		if (parameters.length == 1) {
			List<String> tabCompletion = CommandHelpers.getListOfStringsMatchingLastWord(parameters, getSpecies());
			tabCompletion.add("help");
			return tabCompletion;
		} else if (parameters.length == 2) {
			return CommandHelpers.getListOfStringsMatchingLastWord(parameters, CommandHelpers.getPlayers());
		}
		return null;
	}

	private static String[] getSpecies() {
		List<String> species = new ArrayList<>();

		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				species.add(allele.getName().replaceAll("\\s", ""));
			}
		}

		return species.toArray(new String[species.size()]);
	}

}
