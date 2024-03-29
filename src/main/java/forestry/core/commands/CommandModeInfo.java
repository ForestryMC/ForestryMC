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

import java.util.stream.Stream;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import genetics.commands.CommandHelpers;

public class CommandModeInfo implements Command<CommandSourceStack> {
	private final ICommandModeHelper modeHelper;

	public CommandModeInfo(ICommandModeHelper modeHelper) {
		this.modeHelper = modeHelper;
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register(ICommandModeHelper modeHelper) {
		return Commands.literal("info")
				.then(Commands.argument("mode", StringArgumentType.word()).suggests((ctx, builder) -> {
					Stream.of(modeHelper.getModeNames()).forEach(builder::suggest);
					return builder.buildFuture();
				}).executes(new CommandModeInfo(modeHelper)));

	}

    @Override
	public int run(CommandContext<CommandSourceStack> ctxContext) {

		String modeName = ctxContext.getArgument("mode", String.class);


		Style green = Style.EMPTY;
		green.withColor(ChatFormatting.GREEN);
		CommandHelpers.sendLocalizedChatMessage(ctxContext.getSource(), green, modeName);

		for (String desc : modeHelper.getDescription(modeName)) {
			CommandHelpers.sendLocalizedChatMessage(ctxContext.getSource(), "for." + desc);
		}

		return 1;
	}
}
