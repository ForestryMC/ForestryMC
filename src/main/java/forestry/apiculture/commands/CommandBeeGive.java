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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.commands.CommandHelpers;
import forestry.core.commands.SpeciesNotFoundException;
import forestry.core.commands.SubCommand;
import forestry.core.commands.TemplateNotFoundException;

public class CommandBeeGive extends SubCommand {

	private final String beeTypeHelpString;
	private final String[] beeTypeArr;

	public CommandBeeGive() {
		super("give");
		setPermLevel(PermLevel.ADMIN);

		List<String> beeTypeStrings = new ArrayList<>();
		for (EnumBeeType type : EnumBeeType.values()) {
			if (type == EnumBeeType.NONE) {
				continue;
			}
			beeTypeStrings.add(type.getName());
		}

		beeTypeArr = beeTypeStrings.toArray(new String[beeTypeStrings.size()]);

		StringBuilder beeTypeHelp = new StringBuilder();
		String separator = ", ";

		Iterator<String> iter = beeTypeStrings.iterator();
		while (iter.hasNext()) {
			beeTypeHelp.append(iter.next());
			if (iter.hasNext()) {
				beeTypeHelp.append(separator);
			}
		}
		beeTypeHelpString = beeTypeHelp.toString();
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] arguments) {
		if (arguments.length < 2) {
			printHelp(sender);
			return;
		}

		IBeeGenome beeGenome = getBeeGenome(arguments[0]);
		EnumBeeType beeType = getBeeType(arguments[1]);
		if (beeType == EnumBeeType.NONE) {
			printHelp(sender);
			return;
		}

		EntityPlayer player;
		if (arguments.length == 3) {
			player = CommandHelpers.getPlayer(sender, arguments[2]);
		} else {
			player = CommandHelpers.getPlayer(sender, sender.getCommandSenderName());
		}
		if (player == null) {
			printHelp(sender);
			return;
		}

		IBee bee = BeeManager.beeRoot.getBee(player.worldObj, beeGenome);

		if (beeType == EnumBeeType.QUEEN) {
			bee.mate(bee);
		}

		ItemStack beeStack = BeeManager.beeRoot.getMemberStack(bee, beeType.ordinal());
		player.dropPlayerItemWithRandomChoice(beeStack, true);

		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.bee.give.given", player.getCommandSenderName(), bee.getGenome().getPrimary().getName(), beeType.getName());
	}

	private static IBeeGenome getBeeGenome(String speciesName) {
		IAlleleBeeSpecies species = null;

		for (String uid : AlleleManager.alleleRegistry.getRegisteredAlleles().keySet()) {

			if (!uid.equals(speciesName)) {
				continue;
			}

			if (AlleleManager.alleleRegistry.getAllele(uid) instanceof IAlleleBeeSpecies) {
				species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(uid);
				break;
			}
		}

		if (species == null) {
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
				if (allele instanceof IAlleleBeeSpecies && allele.getName().equals(speciesName)) {
					species = (IAlleleBeeSpecies) allele;
					break;
				}
			}
		}

		if (species == null) {
			throw new SpeciesNotFoundException(speciesName);
		}

		IAllele[] template = BeeManager.beeRoot.getTemplate(species.getUID());

		if (template == null) {
			throw new TemplateNotFoundException(species);
		}

		return BeeManager.beeRoot.templateAsGenome(template);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] parameters) {
		if (parameters.length == 1) {
			List<String> tabCompletion = CommandHelpers.getListOfStringsMatchingLastWord(parameters, getSpecies());
			tabCompletion.add("help");
			return tabCompletion;
		} else if (parameters.length == 2) {
			return CommandHelpers.getListOfStringsMatchingLastWord(parameters, beeTypeArr);
		} else if (parameters.length == 3) {
			return CommandHelpers.getListOfStringsMatchingLastWord(parameters, CommandHelpers.getPlayers());
		}
		return null;
	}

	private static String[] getSpecies() {
		List<String> species = new ArrayList<>();

		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleBeeSpecies) {
				species.add(allele.getName());
			}
		}

		return species.toArray(new String[species.size()]);
	}

	private static EnumBeeType getBeeType(String beeTypeName) {
		for (EnumBeeType beeType : EnumBeeType.values()) {
			if (beeType.getName().equalsIgnoreCase(beeTypeName)) {
				return beeType;
			}
		}
		return EnumBeeType.NONE;
	}

	@Override
	public void printHelp(ICommandSender sender) {
		super.printHelp(sender);
		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.give.available", beeTypeHelpString);
	}
}
