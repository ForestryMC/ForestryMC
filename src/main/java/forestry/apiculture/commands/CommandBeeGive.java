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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IBee;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.core.commands.CommandHelpers;
import forestry.core.commands.PermLevel;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.command.EnumArgument;

public class CommandBeeGive {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("give").requires(PermLevel.ADMIN)
                       .then(Commands.argument("bee", EnumArgument.enumArgument(BeeDefinition.class))
                                     .then(Commands.argument("type", EnumArgument.enumArgument(EnumBeeType.class))
                                                   .then(Commands.argument("player", EntityArgument.player())
                                                                 .executes(a -> execute(
                                                                         a.getSource(),
                                                                         a.getArgument("bee", BeeDefinition.class),
                                                                         a.getArgument("type", EnumBeeType.class),
                                                                         EntityArgument.getPlayer(a, "player")
                                                                 )))
                                                   .executes(a -> execute(
                                                           a.getSource(),
                                                           a.getArgument("bee", BeeDefinition.class),
                                                           a.getArgument("type", EnumBeeType.class),
                                                           a.getSource().asPlayer()
                                                   )))
                                     .executes(a -> execute(
                                             a.getSource(),
                                             a.getArgument("bee", BeeDefinition.class),
                                             EnumBeeType.QUEEN,
                                             a.getSource().asPlayer()
                                     )))
                       .executes(a -> execute(
                               a.getSource(),
                               BeeDefinition.FOREST,
                               EnumBeeType.QUEEN,
                               a.getSource().asPlayer()
                       ));
    }

    public static int execute(CommandSource source, BeeDefinition definition, EnumBeeType type, PlayerEntity player) {
        IBee bee = definition.createIndividual();
        if (type == EnumBeeType.QUEEN) {
            bee.mate(bee.getGenome());
        }

        ItemStack beeStack = BeeManager.beeRoot.createStack(bee, type);
        player.dropItem(beeStack, false, true);

        CommandHelpers.sendLocalizedChatMessage(
                source,
                "for.chat.command.forestry.bee.give.given",
                player.getName(),
                bee.getGenome().getPrimary().getDisplayName().getString(),
                type.getName()
        );

        return 1;
    }
}
