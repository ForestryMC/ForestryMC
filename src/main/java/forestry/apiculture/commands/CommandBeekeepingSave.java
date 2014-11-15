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

import com.mojang.authlib.GameProfile;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.commands.CommandHelpers;
import forestry.core.commands.SubCommand;
import forestry.core.config.Defaults;
import forestry.core.config.Version;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginApiculture;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommandBeekeepingSave extends SubCommand {

	private final String discoveredSymbol;
	private final String blacklistedSymbol;
	private final String notCountedSymbol;

	public CommandBeekeepingSave() {
		super("save");
		discoveredSymbol = StatCollector.translateToLocal("for.chat.command.forestry.beekeeping.save.key.discovered.symbol");
		blacklistedSymbol = StatCollector.translateToLocal("for.chat.command.forestry.beekeeping.save.key.blacklisted.symbol");
		notCountedSymbol = StatCollector.translateToLocal("for.chat.command.forestry.beekeeping.save.key.notCounted.symbol");
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] parameters) {
		return CommandHelpers.getListOfStringsMatchingLastWord(parameters, CommandHelpers.getPlayers());
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args) {
		if (args.length != 1) {
			printHelp(sender);
			return;
		}

		String newLine = System.getProperty("line.separator");
		World world = CommandHelpers.getWorld(sender, this);

		String player = args[0];
		Collection<String> statistics = new ArrayList<String>();

		String date = DateFormat.getInstance().format(new Date());
		statistics.add(StatCollector.translateToLocalFormatted("for.chat.command.forestry.beekeeping.save.stats", player, date));
		statistics.add("");
		String modeName = PluginApiculture.beeInterface.getBeekeepingMode(world).getName();
		statistics.add(StatCollector.translateToLocalFormatted("for.chat.command.forestry.beekeeping.save.mode", modeName));
		statistics.add("");

		GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(player);
		IApiaristTracker tracker = PluginApiculture.beeInterface.getBreedingTracker(world, profile);
		if (tracker == null)
			statistics.add(StatCollector.translateToLocal("for.chat.command.forestry.beekeeping.save.error4"));
		else {
			String discoveredLine = StatCollector.translateToLocal("for.chat.command.forestry.beekeeping.save.key.discovered") + ":";
			statistics.add(discoveredLine);
			statistics.add(line(discoveredLine.length()));

			String queen = StatCollector.translateToLocal("for.bees.grammar.queen.type");
			String princess = StatCollector.translateToLocal("for.bees.grammar.princess.type");
			String drone = StatCollector.translateToLocal("for.bees.grammar.drone.type");
			statistics.add(queen + ":\t\t" + tracker.getQueenCount());
			statistics.add(princess + ":\t" + tracker.getPrincessCount());
			statistics.add(drone + ":\t\t" + tracker.getDroneCount());
			statistics.add("");

			Collection<IAlleleBeeSpecies> species = new ArrayList<IAlleleBeeSpecies>();
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values())
				if (allele instanceof IAlleleBeeSpecies)
					species.add((IAlleleBeeSpecies) allele);

			String speciesCount = StatCollector.translateToLocal("for.gui.speciescount");
			String speciesCountLine = String.format("%s (%s):", speciesCount, species.size());
			statistics.add(speciesCountLine);
			statistics.add(line(speciesCountLine.length()));

			statistics.add(discoveredSymbol + ": " + StatCollector.translateToLocal("for.chat.command.forestry.beekeeping.save.key.discovered"));
			statistics.add(blacklistedSymbol + ": " + StatCollector.translateToLocal("for.chat.command.forestry.beekeeping.save.key.blacklisted"));
			statistics.add(notCountedSymbol + ": " + StatCollector.translateToLocal("for.chat.command.forestry.beekeeping.save.key.notCounted"));
			statistics.add("");

			String header = generateSpeciesListHeader();
			statistics.add(header);

			statistics.add(line(header.length()));
			statistics.add("");

			for (IAlleleBeeSpecies allele : species)
				statistics.add(generateSpeciesListEntry(allele, tracker));
		}

		File file = new File(Proxies.common.getForestryRoot(), "config/" + Defaults.MOD.toLowerCase(Locale.ENGLISH) + "/stats/" + player + ".log");
		try {
			File folder = file.getParentFile();
			if (folder != null && !folder.exists()) {
				boolean success = file.getParentFile().mkdirs();
				if (!success) {
					CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.save.error1");
					return;
				}
			}

			if (!file.exists() && !file.createNewFile()) {
				CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.save.error1");
				return;
			}

			if (!file.canWrite()) {
				CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.save.error2");
				return;
			}

			FileOutputStream fileout = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileout, "UTF-8"));

			writer.write("# " + Defaults.MOD + newLine + "# " + Version.getVersion() + newLine);

			for (String line : statistics)
				writer.write(line + newLine);

			writer.close();

		}
		catch (IOException ex) {
			CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.save.error3");
			ex.printStackTrace();
			return;
		}

		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.beekeeping.save.saved", player);
	}

	private String line(int length) {
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < length; i++)
			line.append("-");

		return line.toString();
	}

	private String generateSpeciesListHeader() {
		String authority = StatCollector.translateToLocal("for.gui.alyzer.authority");
		String species = StatCollector.translateToLocal("for.gui.species");
		return speciesListEntry(discoveredSymbol, blacklistedSymbol, notCountedSymbol, "UID", species, authority);
	}

	private String generateSpeciesListEntry(IAlleleBeeSpecies species, IApiaristTracker tracker) {
		String discovered = "";
		if (tracker.isDiscovered(species))
			discovered = discoveredSymbol;

		String blacklisted = "";
		if (AlleleManager.alleleRegistry.isBlacklisted(species.getUID()))
			blacklisted = blacklistedSymbol;

		String notCounted = "";
		if (!species.isCounted())
			notCounted = notCountedSymbol;

		return speciesListEntry(discovered, blacklisted, notCounted, species.getUID(), species.getName(), species.getAuthority());
	}

	private String speciesListEntry(String discovered, String blacklisted, String notCounted, String UID, String speciesName, String authority) {
		return String.format("[ %-2s ] [ %-2s ] [ %-2s ]\t%-40s %-20s %-20s", discovered, blacklisted, notCounted, UID, speciesName, authority);
	}
}
