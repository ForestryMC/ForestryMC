/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import forestry.api.genetics.IBreedingTracker;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;
import forestry.core.utils.StringUtil;
import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAlleleSpecies;
import genetics.commands.CommandHelpers;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public final class CommandSaveStats implements Command<CommandSource> {

    private static final String discoveredSymbol;
    private static final String blacklistedSymbol;
    private static final String notCountedSymbol;

    static {
        discoveredSymbol = new TranslationTextComponent("for.chat.command.forestry.stats.save.key.discovered.symbol").getString();
        blacklistedSymbol = new TranslationTextComponent("for.chat.command.forestry.stats.save.key.blacklisted.symbol").getString();
        notCountedSymbol = new TranslationTextComponent("for.chat.command.forestry.stats.save.key.notCounted.symbol").getString();
    }

    private final IStatsSaveHelper saveHelper;
    private final ICommandModeHelper modeHelper;

    public CommandSaveStats(IStatsSaveHelper saveHelper, ICommandModeHelper modeHelper) {
        this.saveHelper = saveHelper;
        this.modeHelper = modeHelper;
    }

    public static ArgumentBuilder<CommandSource, ?> register(
            IStatsSaveHelper saveHelper,
            ICommandModeHelper modeHelper
    ) {
        return Commands.literal("save")
                       .then(Commands.argument("player", EntityArgument.player())
                                     .executes(new CommandSaveStats(saveHelper, modeHelper)));

    }

    private static String generateSpeciesListHeader() {
        String authority = new TranslationTextComponent("for.gui.alyzer.authority").getString();
        String species = new TranslationTextComponent("for.gui.species").getString();
        return speciesListEntry(discoveredSymbol, blacklistedSymbol, notCountedSymbol, "UID", species, authority);
    }

    private static String generateSpeciesListEntry(IAlleleSpecies species, IBreedingTracker tracker) {
        String discovered = "";
        if (tracker.isDiscovered(species)) {
            discovered = discoveredSymbol;
        }

        String blacklisted = "";
        if (GeneticsAPI.apiInstance.getAlleleRegistry().isBlacklisted(species.getRegistryName())) {
            blacklisted = blacklistedSymbol;
        }

        String notCounted = "";
        if (!species.isDominant()) {
            notCounted = notCountedSymbol;
        }

        return speciesListEntry(
                discovered,
                blacklisted,
                notCounted,
                species.getRegistryName().toString(),
                species.getDisplayName().getString(),
                species.getAuthority()
        );
    }

    private static String speciesListEntry(
            String discovered,
            String blacklisted,
            String notCounted,
            String UID,
            String speciesName,
            String authority
    ) {
        return String.format(
                "[ %-2s ] [ %-2s ] [ %-2s ]\t%-40s %-20s %-20s",
                discovered,
                blacklisted,
                notCounted,
                UID,
                speciesName,
                authority
        );
    }

    public int run(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        String newLine = System.getProperty("line.separator");

        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");

        World world = ctx.getSource().getWorld();

        Collection<String> statistics = new ArrayList<>();

        String date = DateFormat.getInstance().format(new Date());
        statistics.add(new TranslationTextComponent(
                saveHelper.getUnlocalizedSaveStatsString(),
                player.getDisplayName(),
                date
        ).getString());
        statistics.add("");
        statistics.add(new TranslationTextComponent(
                "for.chat.command.forestry.stats.save.mode",
                modeHelper.getModeName(world)
        ).getString());
        statistics.add("");

        IBreedingTracker tracker = saveHelper.getBreedingTracker(world, player.getGameProfile());
        saveHelper.addExtraInfo(statistics, tracker);

        Collection<? extends IAlleleSpecies> species = saveHelper.getSpecies();

        String speciesCount = new TranslationTextComponent("for.gui.speciescount").getString();
        String speciesCountLine = String.format("%s (%s):", speciesCount, species.size());
        statistics.add(speciesCountLine);
        statistics.add(StringUtil.line(speciesCountLine.length()));

        statistics.add(discoveredSymbol + ": " + new TranslationTextComponent(
                "for.chat.command.forestry.stats.save.key.discovered").getString()
        );
        statistics.add(blacklistedSymbol + ": " + new TranslationTextComponent(
                "for.chat.command.forestry.stats.save.key.blacklisted").getString()
        );
        statistics.add(notCountedSymbol + ": " + new TranslationTextComponent(
                "for.chat.command.forestry.stats.save.key.notCounted").getString()
        );
        statistics.add("");

        String header = generateSpeciesListHeader();
        statistics.add(header);

        statistics.add(StringUtil.line(header.length()));
        statistics.add("");

        for (IAlleleSpecies allele : species) {
            statistics.add(generateSpeciesListEntry(allele, tracker));
        }

        File file = new File(
                Proxies.common.getForestryRoot(),
                "config/" + Constants.MOD_ID + "/stats/" + player.getDisplayName()
                                                                 .getString() + '-' + saveHelper.getFileSuffix() +
                ".log"
        );
        try {
            File folder = file.getParentFile();
            if (folder != null && !folder.exists()) {
                boolean success = file.getParentFile().mkdirs();
                if (!success) {
                    CommandHelpers.sendLocalizedChatMessage(
                            ctx.getSource(),
                            "for.chat.command.forestry.stats.save.error1"
                    );
                    return 0;
                }
            }

            if (!file.exists() && !file.createNewFile()) {
                CommandHelpers.sendLocalizedChatMessage(ctx.getSource(), "for.chat.command.forestry.stats.save.error1");
                return 0;
            }

            if (!file.canWrite()) {
                CommandHelpers.sendLocalizedChatMessage(ctx.getSource(), "for.chat.command.forestry.stats.save.error2");
                return 0;
            }

            FileOutputStream fileout = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileout, StandardCharsets.UTF_8));

            writer.write("# " + Constants.MOD_ID + newLine + "# " + Constants.VERSION + newLine);

            for (String line : statistics) {
                writer.write(line + newLine);
            }

            writer.close();

        } catch (IOException ex) {
            CommandHelpers.sendLocalizedChatMessage(ctx.getSource(), "for.chat.command.forestry.stats.save.error3");
            Log.error(new TranslationTextComponent("for.for.chat.command.forestry.stats.save.error3").getString(), ex);
            return 0;
        }

        CommandHelpers.sendLocalizedChatMessage(
                ctx.getSource(),
                "for.chat.command.forestry.stats.save.saved",
                player.getDisplayName()
        );

        return 1;
    }
}
