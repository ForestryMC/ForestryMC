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

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IBee;
import forestry.apiculture.genetics.BeeDefinition;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IIndividual;
import genetics.commands.CommandHelpers;
import genetics.commands.PermLevel;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommandBeeGive {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("give").requires(PermLevel.ADMIN)
                       .then(Commands.argument("bee", BeeArgument.beeArgument())
                                     .then(Commands.argument("type", EnumArgument.enumArgument(EnumBeeType.class))
                                                   .then(Commands.argument("player", EntityArgument.player())
                                                                 .executes(a -> execute(
                                                                         a.getSource(),
                                                                         a.getArgument("bee", IBee.class),
                                                                         a.getArgument("type", EnumBeeType.class),
                                                                         EntityArgument.getPlayer(a, "player")
                                                                 )))
                                                   .executes(a -> execute(
                                                           a.getSource(),
                                                           a.getArgument("bee", IBee.class),
                                                           a.getArgument("type", EnumBeeType.class),
                                                           a.getSource().asPlayer()
                                                   )))
                                     .executes(a -> execute(
                                             a.getSource(),
                                             a.getArgument("bee", IBee.class),
                                             EnumBeeType.QUEEN,
                                             a.getSource().asPlayer()
                                     )))
                       .executes(a -> execute(
                               a.getSource(),
                               BeeDefinition.FOREST.createIndividual(),
                               EnumBeeType.QUEEN,
                               a.getSource().asPlayer()
                       ));
    }

    public static int execute(CommandSource source, IBee bee, EnumBeeType type, PlayerEntity player) {
        IBee beeCopy = (IBee) bee.copy();
        if (type == EnumBeeType.QUEEN) {
            beeCopy.mate(beeCopy.getGenome());
        }

        ItemStack beeStack = BeeManager.beeRoot.createStack(beeCopy, type);
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

    public static class BeeArgument implements ArgumentType<IBee> {

        public static BeeArgument beeArgument() {
            return new BeeArgument();
        }

        @Override
        public IBee parse(final StringReader reader) throws CommandSyntaxException {
            ResourceLocation location = ResourceLocation.read(reader);

            return BeeManager.beeRoot.getIndividualTemplates()
                                     .stream()
                                     .filter(a -> a.getGenome().getActiveAllele(
                                             BeeChromosomes.SPECIES).getRegistryName().equals(location))
                                     .findFirst()
                                     .orElseThrow(() -> new SimpleCommandExceptionType(
                                             new LiteralMessage("Invalid Bee Type: " + location)
                                     ).create());
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(
                final CommandContext<S> context,
                final SuggestionsBuilder builder
        ) {
            return ISuggestionProvider.suggest(BeeManager.beeRoot.getIndividualTemplates().stream()
                                                                 .map(IIndividual::getGenome)
                                                                 .map(a -> a.getActiveAllele(BeeChromosomes.SPECIES))
                                                                 .map(IAllele::getRegistryName)
                                                                 .map(ResourceLocation::toString), builder);
        }

        @Override
        public Collection<String> getExamples() {
            return BeeManager.beeRoot.getIndividualTemplates().stream()
                                     .map(IIndividual::getGenome)
                                     .map(a -> a.getActiveAllele(BeeChromosomes.SPECIES))
                                     .map(IAllele::getRegistryName)
                                     .map(ResourceLocation::toString)
                                     .collect(Collectors.toList());
        }
    }
}
