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

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

public final class CommandSaveStats extends SubCommand {

	private static final String discoveredSymbol;
	private static final String blacklistedSymbol;
	private static final String notCountedSymbol;

	static {
		discoveredSymbol = Translator.translateToLocal("for.chat.command.forestry.stats.save.key.discovered.symbol");
		blacklistedSymbol = Translator.translateToLocal("for.chat.command.forestry.stats.save.key.blacklisted.symbol");
		notCountedSymbol = Translator.translateToLocal("for.chat.command.forestry.stats.save.key.notCounted.symbol");
	}

	private final IStatsSaveHelper saveHelper;
	private final ICommandModeHelper modeHelper;

	public CommandSaveStats(IStatsSaveHelper saveHelper, ICommandModeHelper modeHelper) {
		super("save");
		this.saveHelper = saveHelper;
		this.modeHelper = modeHelper;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return CommandHelpers.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
	}

	@Override
	public void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 1) {
			printHelp(sender);
			return;
		}

		String newLine = System.getProperty("line.separator");
		World world = sender.getEntityWorld();

		EntityPlayerMP player;
		if (args.length > 0) {
			player = CommandBase.getPlayer(server, sender, args[0]);
		} else {
			player = CommandBase.getPlayer(server, sender, sender.getName());
		}

		Collection<String> statistics = new ArrayList<>();

		String date = DateFormat.getInstance().format(new Date());
		statistics.add(Translator.translateToLocalFormatted(saveHelper.getUnlocalizedSaveStatsString(), player.getDisplayName(), date));
		statistics.add("");
		statistics.add(Translator.translateToLocalFormatted("for.chat.command.forestry.stats.save.mode", modeHelper.getModeName(world)));
		statistics.add("");

		IBreedingTracker tracker = saveHelper.getBreedingTracker(world, player.getGameProfile());
		saveHelper.addExtraInfo(statistics, tracker);

		Collection<IAlleleSpecies> species = saveHelper.getSpecies();

		String speciesCount = Translator.translateToLocal("for.gui.speciescount");
		String speciesCountLine = String.format("%s (%s):", speciesCount, species.size());
		statistics.add(speciesCountLine);
		statistics.add(StringUtil.line(speciesCountLine.length()));

		statistics.add(discoveredSymbol + ": " + Translator.translateToLocal("for.chat.command.forestry.stats.save.key.discovered"));
		statistics.add(blacklistedSymbol + ": " + Translator.translateToLocal("for.chat.command.forestry.stats.save.key.blacklisted"));
		statistics.add(notCountedSymbol + ": " + Translator.translateToLocal("for.chat.command.forestry.stats.save.key.notCounted"));
		statistics.add("");

		String header = generateSpeciesListHeader();
		statistics.add(header);

		statistics.add(StringUtil.line(header.length()));
		statistics.add("");

		for (IAlleleSpecies allele : species) {
			statistics.add(generateSpeciesListEntry(allele, tracker));
		}

		File file = new File(Proxies.common.getForestryRoot(), "config/" + Constants.MOD_ID + "/stats/" + player.getDisplayNameString() + '-' + saveHelper.getFileSuffix() + ".log");
		try {
			File folder = file.getParentFile();
			if (folder != null && !folder.exists()) {
				boolean success = file.getParentFile().mkdirs();
				if (!success) {
					CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.stats.save.error1");
					return;
				}
			}

			if (!file.exists() && !file.createNewFile()) {
				CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.stats.save.error1");
				return;
			}

			if (!file.canWrite()) {
				CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.stats.save.error2");
				return;
			}

			FileOutputStream fileout = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileout, StandardCharsets.UTF_8));

			writer.write("# " + Constants.MOD_ID + newLine + "# " + Constants.VERSION + newLine);

			for (String line : statistics) {
				writer.write(line + newLine);
			}

			writer.close();

		} catch (IOException ex) {
			CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.stats.save.error3");
			Log.error(Translator.translateToLocal("for.for.chat.command.forestry.stats.save.error3"), ex);
			return;
		}

		CommandHelpers.sendLocalizedChatMessage(sender, "for.chat.command.forestry.stats.save.saved", player.getDisplayName());
	}

	private static String generateSpeciesListHeader() {
		String authority = Translator.translateToLocal("for.gui.alyzer.authority");
		String species = Translator.translateToLocal("for.gui.species");
		return speciesListEntry(discoveredSymbol, blacklistedSymbol, notCountedSymbol, "UID", species, authority);
	}

	private static String generateSpeciesListEntry(IAlleleSpecies species, IBreedingTracker tracker) {
		String discovered = "";
		if (tracker.isDiscovered(species)) {
			discovered = discoveredSymbol;
		}

		String blacklisted = "";
		if (AlleleManager.alleleRegistry.isBlacklisted(species.getUID())) {
			blacklisted = blacklistedSymbol;
		}

		String notCounted = "";
		if (!species.isCounted()) {
			notCounted = notCountedSymbol;
		}

		return speciesListEntry(discovered, blacklisted, notCounted, species.getUID(), species.getAlleleName(), species.getAuthority());
	}

	private static String speciesListEntry(String discovered, String blacklisted, String notCounted, String UID, String speciesName, String authority) {
		return String.format("[ %-2s ] [ %-2s ] [ %-2s ]\t%-40s %-20s %-20s", discovered, blacklisted, notCounted, UID, speciesName, authority);
	}
}
