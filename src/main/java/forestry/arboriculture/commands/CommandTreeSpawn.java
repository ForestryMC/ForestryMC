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
package forestry.arboriculture.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.genetics.TreeDefinition;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IIndividual;
import genetics.commands.PermLevel;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class CommandTreeSpawn {
    public static ArgumentBuilder<CommandSource, ?> register(String name, ITreeSpawner treeSpawner) {
        return Commands.literal(name).requires(PermLevel.ADMIN)
                       .then(Commands.argument("type", TreeArgument.treeArgument())
                                     .executes(a -> run(
                                             treeSpawner,
                                             a.getSource(),
                                             a.getArgument("type", ITree.class)
                                     )))
                       .executes(a -> run(treeSpawner, a.getSource(), TreeDefinition.Oak.createIndividual()));
    }

    public static int run(
            ITreeSpawner treeSpawner,
            CommandSource source,
            ITree tree
    ) throws CommandSyntaxException {
        return treeSpawner.spawn(source, tree, source.asPlayer());
    }

    public static class TreeArgument implements ArgumentType<ITree> {

        public static TreeArgument treeArgument() {
            return new TreeArgument();
        }

        @Override
        public ITree parse(final StringReader reader) throws CommandSyntaxException {
            ResourceLocation location = ResourceLocation.read(reader);

            return TreeManager.treeRoot.templateAsIndividual(TreeManager.treeRoot.getTemplate(location.toString()));
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(
                final CommandContext<S> context,
                final SuggestionsBuilder builder
        ) {
            return ISuggestionProvider.suggest(TreeManager.treeRoot.getIndividualTemplates().stream()
                                                                   .map(IIndividual::getGenome)
                                                                   .map(a -> a.getActiveAllele(TreeChromosomes.SPECIES))
                                                                   .map(IAllele::getRegistryName)
                                                                   .map(ResourceLocation::toString), builder);
        }

        @Override
        public Collection<String> getExamples() {
            return TreeManager.treeRoot.getIndividualTemplates().stream()
                                       .map(IIndividual::getGenome)
                                       .map(a -> a.getActiveAllele(TreeChromosomes.SPECIES))
                                       .map(IAllele::getRegistryName)
                                       .map(ResourceLocation::toString)
                                       .collect(Collectors.toList());
        }
    }
}
