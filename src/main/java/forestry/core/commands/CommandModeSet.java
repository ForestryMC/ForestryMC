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
import net.minecraft.world.level.Level;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import genetics.commands.CommandHelpers;
import genetics.commands.PermLevel;

public final class CommandModeSet implements Command<CommandSourceStack> {
	private final ICommandModeHelper modeSetter;

	public CommandModeSet(ICommandModeHelper modeSetter) {
		this.modeSetter = modeSetter;
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register(ICommandModeHelper modeHelper) {
		return Commands.literal("set").requires(PermLevel.ADMIN)
				.then(Commands.argument("name", StringArgumentType.string())
						.suggests((ctx, builder) -> {
							Stream.of(modeHelper.getModeNames()).forEach(builder::suggest);
							return builder.buildFuture();
						})
						.executes(new CommandModeSet(modeHelper)));

	}

	@Override
	public int run(CommandContext<CommandSourceStack> ctx) {
		Level world = ctx.getSource().getLevel();

		String modeName = ctx.getArgument("name", String.class);

		if (modeSetter.setMode(world, modeName)) {
			CommandHelpers.sendLocalizedChatMessage(ctx.getSource(), "for.chat.command.forestry.mode.set.success", modeName);

			return 1;
		} else {
			CommandHelpers.sendLocalizedChatMessage(ctx.getSource(), "for.chat.command.forestry.mode.set.error", modeName);
			return 0;
		}
	}
}
