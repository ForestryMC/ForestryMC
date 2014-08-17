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
package forestry.arboriculture;

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

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IArboristTracker;
import forestry.api.arboriculture.ITreekeepingMode;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.config.Defaults;
import forestry.core.config.Version;
import forestry.core.proxy.Proxies;
import forestry.core.utils.CommandMC;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginArboriculture;

public class CommandTreekeepingMode extends CommandMC {

	String[] modeStrings;

	public CommandTreekeepingMode() {
		modeStrings = new String[PluginArboriculture.treeInterface.getTreekeepingModes().size()];
		for (int i = 0; i < PluginArboriculture.treeInterface.getTreekeepingModes().size(); i++)
			modeStrings[i] = PluginArboriculture.treeInterface.getTreekeepingModes().get(i).getName();
	}

	@Override
	public int compareTo(Object arg0) {
		return this.getCommandName().compareTo(((ICommand) arg0).getCommandName());
	}

	@Override
	public String getCommandName() {
		return "treekeeping";
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

			ITreekeepingMode mode = PluginArboriculture.treeInterface.getTreekeepingMode(desired);
			if (mode == null)
				throw new CommandException("A treekeeping mode called '%s' is not available.", desired);

			PluginArboriculture.treeInterface.setTreekeepingMode(world, mode.getName());
			func_152373_a(sender, this, "Treekeeping mode set to %s.", mode.getName());

		} else if (arguments[0].matches("save")) {
			if (arguments.length <= 1)
				throw new WrongUsageException("/" + this.getCommandName() + " save <player-name>");

			saveStatistics(sender, arguments);
		} else if (arguments[0].matches("help")) {
			sendChatMessage(sender, StringUtil.localizeAndFormat("chat.command.trees.help.0", this.getCommandName()));
			sendChatMessage(sender, StringUtil.localize("chat.command.trees.help.1"));
			sendChatMessage(sender, StringUtil.localize("chat.command.trees.help.2"));
			sendChatMessage(sender, StringUtil.localize("chat.command.trees.help.3"));
			sendChatMessage(sender, StringUtil.localize("chat.command.trees.help.4"));
			sendChatMessage(sender, StringUtil.localize("chat.command.trees.help.5"));
		}
	}

	private void saveStatistics(ICommandSender sender, String[] arguments) {

		String newLine = System.getProperty("line.separator");
		World world = getWorld(sender, arguments);

		String player = arguments[1];
		Collection<String> statistics = new ArrayList<String>();

		statistics.add(String.format("Treekeeping statistics for %s on %s:", player, DateFormat.getInstance().format(new Date())));
		statistics.add("");
		statistics.add("MODE: " + PluginArboriculture.treeInterface.getTreekeepingMode(world).getName());
		statistics.add("");

		GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(player);
		IArboristTracker tracker = PluginArboriculture.treeInterface.getBreedingTracker(world, profile);
		if (tracker == null)
			statistics.add("No statistics found.");
		else {
			statistics.add("BRED:");
			statistics.add("-----");
			statistics.add("");

			Collection<IAlleleTreeSpecies> species = new ArrayList<IAlleleTreeSpecies>();
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values())
				if (allele instanceof IAlleleTreeSpecies)
					species.add((IAlleleTreeSpecies) allele);

			statistics.add(String.format("SPECIES (%s):", species.size()));
			statistics.add("-------------");
			statistics.add("");

			for (IAlleleTreeSpecies allele : species)
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

	private String generateSpeciesListEntry(IAlleleTreeSpecies species, IArboristTracker tracker) {
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

		sendChatMessage(sender, "Current: " + PluginArboriculture.treeInterface.getTreekeepingMode(world).getName() + " (#" + world.getWorldInfo().getSaveVersion()
				+ ")");

		String help = "";
		for (ITreekeepingMode mode : PluginArboriculture.treeInterface.getTreekeepingModes()) {
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

		ITreekeepingMode found = null;
		for (ITreekeepingMode mode : PluginArboriculture.treeInterface.getTreekeepingModes())
			if (mode.getName().equalsIgnoreCase(arguments[1])) {
				found = mode;
				break;
			}

		if (found == null)
			throw new CommandException("No treekeeping mode called '%s' is available.", arguments[1]);

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
