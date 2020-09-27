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

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.IForestryModule;
import forestry.modules.ModuleManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandModules {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return LiteralArgumentBuilder.<CommandSource>literal("module")
                .then(CommandPluginsInfo.register())
                .executes(CommandModules::listModulesForSender);
    }

    private static int listModulesForSender(CommandContext<CommandSource> context) {
        StringBuilder pluginList = new StringBuilder();
        for (IForestryModule module : ModuleManager.getLoadedModules()) {
            if (pluginList.length() > 0) {
                pluginList.append(", ");
            }

            pluginList.append(makeListEntry(module));
        }

        CommandHelpers.sendChatMessage(context.getSource(), pluginList.toString());

        return 1;
    }

    private static String makeListEntry(IForestryModule module) {
        String entry = module.isAvailable() ? TextFormatting.GREEN.toString() : TextFormatting.RED.toString();

        ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
        if (info != null) {
            entry += info.moduleID();
            if (!info.version().isEmpty()) {
                entry += " (" + info.version() + ")";
            }
        } else {
            entry += "???";
        }

        return entry;
    }

    public static class CommandPluginsInfo {
        public static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("info")
                           .then(Commands.argument("module", ModuleArgument.modules())
                                         .executes(CommandPluginsInfo::listModuleInfoForSender));
        }

        public static class ModuleArgument implements ArgumentType<IForestryModule> {
            @Override
            public IForestryModule parse(StringReader reader) throws CommandSyntaxException {
                String pluginUid = reader.readUnquotedString();

                IForestryModule found = null;
                for (IForestryModule module : ModuleManager.getLoadedModules()) {
                    ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
                    if (info == null) {
                        continue;
                    }

                    String id = info.moduleID();
                    String name = info.name();
                    if (id.equalsIgnoreCase(pluginUid) || name.equalsIgnoreCase(pluginUid)) {
                        found = module;
                        break;
                    }
                }

                if (found != null) {
                    return found;
                } else {
                    throw new SimpleCommandExceptionType(new TranslationTextComponent(
                            "for.chat.modules.error",
                            pluginUid
                    )).createWithContext(reader);
                }

            }

            public static ModuleArgument modules() {
                return new ModuleArgument();
            }

            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(
                    CommandContext<S> context,
                    SuggestionsBuilder builder
            ) {
                ModuleManager.getLoadedModules().stream()
                             .map(module -> module.getClass().getAnnotation(ForestryModule.class))
                             .filter(Objects::nonNull)
                             .forEach(info -> builder.suggest(info.moduleID()));

                return builder.buildFuture();
            }
        }

        private static int listModuleInfoForSender(CommandContext<CommandSource> context) throws CommandException {
            IForestryModule found = context.getArgument("module", IForestryModule.class);

            TextFormatting formatting = found.isAvailable() ? TextFormatting.GREEN : TextFormatting.RED;

            ForestryModule info = found.getClass().getAnnotation(ForestryModule.class);
            if (info != null) {
                CommandSource sender = context.getSource();

                CommandHelpers.sendChatMessage(sender, formatting + "Module: " + info.name());
                if (!info.version().isEmpty()) {
                    CommandHelpers.sendChatMessage(sender, TextFormatting.BLUE + "Version: " + info.version());
                }

                if (!info.author().isEmpty()) {
                    CommandHelpers.sendChatMessage(sender, TextFormatting.BLUE + "Author(s): " + info.author());
                }

                if (!info.url().isEmpty()) {
                    CommandHelpers.sendChatMessage(sender, TextFormatting.BLUE + "URL: " + info.url());
                }

                if (!info.unlocalizedDescription().isEmpty()) {
                    CommandHelpers.sendChatMessage(
                            sender,
                            new TranslationTextComponent(info.unlocalizedDescription()).getString()
                    );
                }

                return 1;
            }

            return 0;
        }
    }
}
