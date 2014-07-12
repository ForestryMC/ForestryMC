/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.config.Defaults;
import forestry.core.config.Version;
import forestry.core.proxy.Proxies;
import forestry.core.utils.CommandMC;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginApiculture;

public class CommandBeekeepingMode extends CommandMC {

	String[] modeStrings;

	public CommandBeekeepingMode() {
		modeStrings = new String[PluginApiculture.beeInterface.getBeekeepingModes().size()];
		for (int i = 0; i < PluginApiculture.beeInterface.getBeekeepingModes().size(); i++)
			modeStrings[i] = PluginApiculture.beeInterface.getBeekeepingModes().get(i).getName();
	}

	@Override
	public int compareTo(Object arg0) {
		return this.getCommandName().compareTo(((ICommand) arg0).getCommandName());
	}

	@Override
	public String getCommandName() {
		return "beekeeping";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/" + this.getCommandName() + " help";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) {

		if (arguments.length <= 0)
			throw new WrongUsageException("Type '" + this.getCommandUsage(sender) + "' for help.");

		if (arguments[0].matches("list"))
			listModes(sender, arguments);
		else if (arguments[0].matches("info"))
			listModeInfo(sender, arguments);
		else if (arguments[0].matches("set")) {
			if (arguments.length <= 1)
				throw new WrongUsageException("/" + this.getCommandName() + " set [<world-#>] <mode-name>");

			World world = getWorld(sender, arguments);
			String desired = arguments[arguments.length - 1];

			IBeekeepingMode mode = PluginApiculture.beeInterface.getBeekeepingMode(desired);
			if (mode == null)
				throw new CommandException("A bekeeping mode called '%s' is not available.", desired);

			PluginApiculture.beeInterface.setBeekeepingMode(world, mode.getName());
			func_152373_a(sender, this, "Beekeeping mode set to %s.", mode.getName());

		} else if (arguments[0].matches("save")) {
			if (arguments.length <= 1)
				throw new WrongUsageException("/" + this.getCommandName() + " save <player-name>");

			saveStatistics(sender, arguments);
		} else if (arguments[0].matches("help")) {
			sendChatMessage(sender, "Format: '/" + this.getCommandName() + " <command> <arguments>'");
			sendChatMessage(sender, "Available commands:");
			sendChatMessage(sender, "- list [<world-#>]: lists current and available beekeeping modes.");
			sendChatMessage(sender, "- info <mode-name> : information on beekeeping mode.");
			sendChatMessage(sender, "- set [<world-#>] <mode-name>: set beekeeping mode for world.");
			sendChatMessage(sender, "- save [<world-#>] <player-name>: save beekeeping statistics for the given player.");
		}
	}

	private void saveStatistics(ICommandSender sender, String[] arguments) {

		String newLine = System.getProperty("line.separator");
		World world = getWorld(sender, arguments);

		String player = arguments[1];
		Collection<String> statistics = new ArrayList<String>();

		statistics.add(String.format("Beekeeping statistics for %s on %s:", player, DateFormat.getInstance().format(new Date())));
		statistics.add("");
		statistics.add("MODE: " + PluginApiculture.beeInterface.getBeekeepingMode(world).getName());
		statistics.add("");

		GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(player);
		IApiaristTracker tracker = PluginApiculture.beeInterface.getBreedingTracker(world, profile);
		if (tracker == null)
			statistics.add("No statistics found.");
		else {
			statistics.add("BRED:");
			statistics.add("-----");
			statistics.add("");

			statistics.add("Queens:\t\t" + tracker.getQueenCount());
			statistics.add("Princesses:\t" + tracker.getPrincessCount());
			statistics.add("Drones:\t\t" + tracker.getDroneCount());
			statistics.add("");

			Collection<IAlleleBeeSpecies> species = new ArrayList<IAlleleBeeSpecies>();
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values())
				if (allele instanceof IAlleleBeeSpecies)
					species.add((IAlleleBeeSpecies) allele);

			statistics.add(String.format("SPECIES (%s):", species.size()));
			statistics.add("-------------");
			statistics.add("");

			for (IAlleleBeeSpecies allele : species)
				statistics.add(generateSpeciesListEntry(allele, tracker));
		}

		File file = new File(Proxies.common.getForestryRoot(), "config/" + Defaults.MOD.toLowerCase(Locale.ENGLISH) + "/stats/" + player + ".log");
		try {

			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();

			if (!file.exists() && !file.createNewFile()) {
				sendChatMessage(sender, "Log file could not be created. Failed to save statistics.");
				return;
			}

			if (!file.canWrite()) {
				sendChatMessage(sender, "Cannot write to log file. Failed to save statistics.");
				return;
			}

			FileOutputStream fileout = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileout, "UTF-8"));

			writer.write("# " + Defaults.MOD + newLine + "# " + Version.getVersion() + newLine);

			for (String line : statistics)
				writer.write(line + newLine);

			writer.close();

		} catch (Exception ex) {
			sendChatMessage(sender, "Write operation threw an exception. Failed to save statistics.");
			ex.printStackTrace();
		}

		sendChatMessage(sender, "Saved statistics for player " + player);
	}

	private String generateSpeciesListEntry(IAlleleBeeSpecies species, IApiaristTracker tracker) {
		String discovered = "[   ]";
		if (tracker.isDiscovered(species))
			discovered = "[ X ]";
		String blacklisted = "[    ]";
		if (AlleleManager.alleleRegistry.isBlacklisted(species.getUID()))
			blacklisted = "[ BL ]";
		String notcounted = "[    ]";
		if (!species.isCounted())
			notcounted = "[ NC ]";

		return String.format("%s %s %s\t%-40s %-20s %-20s", discovered, blacklisted, notcounted, species.getUID(), species.getName(), species.getAuthority());
	}

	private void listModes(ICommandSender sender, String[] arguments) {
		World world = getWorld(sender, arguments);

		sendChatMessage(sender, "Current: " + PluginApiculture.beeInterface.getBeekeepingMode(world).getName() + " (#" + world.getWorldInfo().getSaveVersion() + ")");

		String help = "";
		for (IBeekeepingMode mode : PluginApiculture.beeInterface.getBeekeepingModes()) {
			if (!help.isEmpty())
				help += ", ";
			help += mode.getName();
		}
		sendChatMessage(sender, "Available modes: " + help);
		return;
	}

	private void listModeInfo(ICommandSender sender, String[] arguments) {
		if (arguments.length <= 1)
			throw new WrongUsageException("/" + this.getCommandName() + " info <mode-name>");

		IBeekeepingMode found = null;
		for (IBeekeepingMode mode : PluginApiculture.beeInterface.getBeekeepingModes())
			if (mode.getName().equalsIgnoreCase(arguments[1])) {
				found = mode;
				break;
			}

		if (found == null)
			throw new CommandException("No beekeeping mode called '%s' is available.", arguments[1]);

		sendChatMessage(sender, "\u00A7aMode: " + found.getName());
		for (String desc : found.getDescription())
			sendChatMessage(sender, StringUtil.localize(desc));
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (sender instanceof EntityPlayer)
			return Proxies.common.isOp((EntityPlayer) sender);
		else
			return sender.canCommandSenderUseCommand(4, getCommandName());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] incomplete) {
		return getListOfStringsMatchingLastWord(incomplete, modeStrings);
	}

}
