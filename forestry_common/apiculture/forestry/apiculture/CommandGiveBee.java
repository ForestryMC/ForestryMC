/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.core.utils.CommandMC;
import forestry.plugins.PluginApiculture;

public class CommandGiveBee extends CommandMC {

	EnumBeeType type;

	public CommandGiveBee(EnumBeeType type) {
		this.type = type;
	}

	@Override
	public String getCommandName() {
		return "give" + type.toString().toLowerCase();
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "/" + getCommandName() + " <player-name> <species-name>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) {
		if (arguments.length >= 2) {
			EntityPlayer player = getPlayer(sender, arguments[0]);

			String parameter = arguments[1];

			IAlleleBeeSpecies species = null;

			search: for (String uid : AlleleManager.alleleRegistry.getRegisteredAlleles().keySet()) {

				if (!uid.equals(parameter))
					continue;

				if (AlleleManager.alleleRegistry.getAllele(uid) instanceof IAlleleBeeSpecies) {
					species = (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(uid);
					break search;
				}
			}

			search: if (species == null)
				for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values())
					if (allele instanceof IAlleleBeeSpecies)
						if (((IAlleleBeeSpecies) allele).getName().equals(parameter)) {
							species = (IAlleleBeeSpecies) allele;
							break search;
						}

			if (species == null)
				throw new SpeciesNotFoundException(parameter);

			IAllele[] template = PluginApiculture.beeInterface.getTemplate(species.getUID());

			if (template == null)
				throw new TemplateNotFoundException(species);

			IBeeGenome genome = PluginApiculture.beeInterface.templateAsGenome(template);

			IBee bee = PluginApiculture.beeInterface.getBee(player.worldObj, genome);

			if(this.type == EnumBeeType.QUEEN)
				bee.mate(bee);
			
			ItemStack beestack = PluginApiculture.beeInterface.getMemberStack(bee, type.ordinal());
			player.dropPlayerItemWithRandomChoice(beestack, true);
			func_152373_a(sender, this, "Player %s was given a %s bee.", player.getCommandSenderName(), ((IAlleleSpecies) template[0]).getName());
		} else
			throw new WrongUsageException("/" + getCommandName() + " <player-name> <species-name>");
	}

	/**
	 * Adds the strings available in this command to the given list of tab completion options.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] parameters) {
		if (parameters.length == 1)
			return getListOfStringsMatchingLastWord(parameters, this.getPlayers());
		else if (parameters.length == 2)
			return getListOfStringsMatchingLastWord(parameters, this.getSpecies());
		else
			return null;
	}

	protected String[] getSpecies() {
		List<String> species = new ArrayList<String>();

		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values())
			if (allele instanceof IAlleleBeeSpecies)
				species.add(((IAlleleSpecies) allele).getName());

		return species.toArray(new String[] {});
	}
}
