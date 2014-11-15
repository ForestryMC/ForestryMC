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

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.apiculture.SpeciesNotFoundException;
import forestry.apiculture.TemplateNotFoundException;
import forestry.core.commands.CommandHelpers;
import forestry.core.commands.SubCommand;
import forestry.plugins.PluginApiculture;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandBeekeepingGiveBee extends SubCommand {

	private String beeTypeHelpString;
	private String[] beeTypeArr;

	public CommandBeekeepingGiveBee() {
		super("give");
		setPermLevel(PermLevel.ADMIN);

		List<String> beeTypeStrings = new ArrayList<String>();
		for (EnumBeeType type : EnumBeeType.values()) {
			if (type == EnumBeeType.NONE)
				continue;
			beeTypeStrings.add(type.getName());
		}

		beeTypeArr = beeTypeStrings.toArray(new String[beeTypeStrings.size()]);

		StringBuilder beeTypeHelp = new StringBuilder();
		String separator = ", ";

		Iterator<String> iter = beeTypeStrings.iterator();
		while (iter.hasNext()) {
			beeTypeHelp.append(iter.next());
			if (iter.hasNext())
				beeTypeHelp.append(separator);
		}
		beeTypeHelpString = beeTypeHelp.toString();
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] arguments) {
		if (arguments.length < 3) {
			printHelp(sender);
			return;
		}

		EnumBeeType beeType = getBeeType(arguments[0]);
		if (beeType == EnumBeeType.NONE) {
			printHelp(sender);
			return;
		}

		EntityPlayer player = CommandHelpers.getPlayer(sender, arguments[1]);

		String parameter = arguments[2];

		IAlleleBeeSpecies species = null;

		for (String uid : AlleleManager.alleleRegistry.getRegisteredAlleles().keySet()) {

			if (!uid.equals(parameter))
				continue;

			if (AlleleManager.alleleRegistry.getAllele(uid) instanceof IAlleleBeeSpecies) {
				species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(uid);
				break;
			}
		}

		if (species == null) {
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
				if (allele instanceof IAlleleBeeSpecies && allele.getName().equals(parameter)) {
					species = (IAlleleBeeSpecies) allele;
					break;
				}
			}
		}

		if (species == null)
			throw new SpeciesNotFoundException(parameter);

		IAllele[] template = PluginApiculture.beeInterface.getTemplate(species.getUID());

		if (template == null)
			throw new TemplateNotFoundException(species);

		IBeeGenome genome = PluginApiculture.beeInterface.templateAsGenome(template);

		IBee bee = PluginApiculture.beeInterface.getBee(player.worldObj, genome);

		if(beeType == EnumBeeType.QUEEN)
			bee.mate(bee);

		ItemStack beeStack = PluginApiculture.beeInterface.getMemberStack(bee, beeType.ordinal());
		player.dropPlayerItemWithRandomChoice(beeStack, true);

		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.give.given", player.getCommandSenderName(), template[0].getName(), beeType.getName());
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] parameters) {
		if (parameters.length == 1) {
			List<String> tabCompletion = CommandHelpers.getListOfStringsMatchingLastWord(parameters, beeTypeArr);
			tabCompletion.add("help");
			return tabCompletion;
		} else if (parameters.length == 2) {
			return CommandHelpers.getListOfStringsMatchingLastWord(parameters, CommandHelpers.getPlayers());
		} else if (parameters.length == 3) {
			return CommandHelpers.getListOfStringsMatchingLastWord(parameters, getSpecies());
		}
		return null;
	}

	String[] getSpecies() {
		List<String> species = new ArrayList<String>();

		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values())
			if (allele instanceof IAlleleBeeSpecies)
				species.add(allele.getName());

		return species.toArray(new String[species.size()]);
	}

	EnumBeeType getBeeType(String beeTypeName) {
		for (EnumBeeType beeType : EnumBeeType.values()) {
			if (beeType.getName().equalsIgnoreCase(beeTypeName))
				return beeType;
		}
		return EnumBeeType.NONE;
	}

	@Override
	public void printHelp(ICommandSender sender) {
		super.printHelp(sender);
		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.give.available", beeTypeHelpString);
	}
}
