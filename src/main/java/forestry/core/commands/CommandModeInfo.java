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

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import genetics.commands.CommandHelpers;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.util.stream.Stream;

public class CommandModeInfo implements Command<CommandSource> {
    private final ICommandModeHelper modeHelper;

    public CommandModeInfo(ICommandModeHelper modeHelper) {
        this.modeHelper = modeHelper;
    }

    public static ArgumentBuilder<CommandSource, ?> register(ICommandModeHelper modeHelper) {
        return Commands.literal("info")
                       .then(Commands.argument("mode", StringArgumentType.word()).suggests((ctx, builder) -> {
                           Stream.of(modeHelper.getModeNames()).forEach(builder::suggest);
                           return builder.buildFuture();
                       }).executes(new CommandModeInfo(modeHelper)));

    }

    @Override
    public int run(CommandContext<CommandSource> ctxContext) {

        String modeName = ctxContext.getArgument("mode", String.class);


        Style green = Style.EMPTY;
        green.setFormatting(TextFormatting.GREEN);
        CommandHelpers.sendLocalizedChatMessage(ctxContext.getSource(), green, modeName);

        for (String desc : modeHelper.getDescription(modeName)) {
            CommandHelpers.sendLocalizedChatMessage(ctxContext.getSource(), "for." + desc);
        }

        return 1;
    }
}
