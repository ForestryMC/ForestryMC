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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.ChatFormatting;

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
import forestry.core.utils.Translator;
import forestry.modules.ModuleManager;

import genetics.commands.CommandHelpers;
import net.minecraft.network.chat.Component;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CommandModules {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return LiteralArgumentBuilder.<CommandSourceStack>literal("module")
				.then(CommandPluginsInfo.register())
				.executes(CommandModules::listModulesForSender);
	}

	private static int listModulesForSender(CommandContext<CommandSourceStack> context) {
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
		String entry = module.isAvailable() ? ChatFormatting.GREEN.toString() : ChatFormatting.RED.toString();

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
		public static ArgumentBuilder<CommandSourceStack, ?> register() {
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
					throw new SimpleCommandExceptionType(Component.translatable("for.chat.modules.error", pluginUid)).createWithContext(reader);
				}

			}

			public static ModuleArgument modules() {
				return new ModuleArgument();
			}

			@Override
			public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
				ModuleManager.getLoadedModules().stream()
						.map(module -> module.getClass().getAnnotation(ForestryModule.class))
						.filter(Objects::nonNull)
						.forEach(info -> builder.suggest(info.moduleID()));

				return builder.buildFuture();
			}
		}

		private static int listModuleInfoForSender(CommandContext<CommandSourceStack> context) throws CommandRuntimeException {
			IForestryModule found = context.getArgument("module", IForestryModule.class);

			ChatFormatting formatting = found.isAvailable() ? ChatFormatting.GREEN : ChatFormatting.RED;

			ForestryModule info = found.getClass().getAnnotation(ForestryModule.class);
			if (info != null) {
				CommandSourceStack sender = context.getSource();

				CommandHelpers.sendChatMessage(sender, formatting + "Module: " + info.name());
				if (!info.version().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, ChatFormatting.BLUE + "Version: " + info.version());
				}
				if (!info.author().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, ChatFormatting.BLUE + "Author(s): " + info.author());
				}
				if (!info.url().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, ChatFormatting.BLUE + "URL: " + info.url());
				}
				if (!info.unlocalizedDescription().isEmpty()) {
					CommandHelpers.sendChatMessage(sender, Translator.translateToLocal(info.unlocalizedDescription()));
				}

				return 1;
			}

			return 0;
		}
	}
}
