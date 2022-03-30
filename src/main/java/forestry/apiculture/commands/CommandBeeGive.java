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

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraftforge.server.command.EnumArgument;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IBee;
import forestry.apiculture.genetics.BeeDefinition;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IIndividual;
import genetics.commands.CommandHelpers;
import genetics.commands.PermLevel;

public class CommandBeeGive {
	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("give").requires(PermLevel.ADMIN)
				.then(Commands.argument("bee", BeeArgument.beeArgument())
						.then(Commands.argument("type", EnumArgument.enumArgument(EnumBeeType.class))
								.then(Commands.argument("player", EntityArgument.player())
										.executes(a -> execute(a.getSource(), a.getArgument("bee", IBee.class), a.getArgument("type", EnumBeeType.class), EntityArgument.getPlayer(a, "player"))))
								.executes(a -> execute(a.getSource(), a.getArgument("bee", IBee.class), a.getArgument("type", EnumBeeType.class), a.getSource().getPlayerOrException())))
						.executes(a -> execute(a.getSource(), a.getArgument("bee", IBee.class), EnumBeeType.QUEEN, a.getSource().getPlayerOrException())))
				.executes(a -> execute(a.getSource(), BeeDefinition.FOREST.createIndividual(), EnumBeeType.QUEEN, a.getSource().getPlayerOrException()));
	}

	public static int execute(CommandSourceStack source, IBee bee, EnumBeeType type, Player player) {
		IBee beeCopy = (IBee) bee.copy();
		if (type == EnumBeeType.QUEEN) {
			beeCopy.mate(beeCopy.getGenome());
		}

		ItemStack beeStack = BeeManager.beeRoot.createStack(beeCopy, type);
		player.drop(beeStack, false, true);

		CommandHelpers.sendLocalizedChatMessage(source, "for.chat.command.forestry.bee.give.given", player.getName(), bee.getGenome().getPrimary().getDisplayName().getString(), type.getName());
		return 1;
	}

	public static class BeeArgument implements ArgumentType<IBee> {

		public static BeeArgument beeArgument() {
			return new BeeArgument();
		}

		@Override
		public IBee parse(final StringReader reader) throws CommandSyntaxException {
			ResourceLocation location = ResourceLocation.read(reader);

			return BeeManager.beeRoot.getIndividualTemplates().stream().filter(a -> a.getGenome().getActiveAllele(BeeChromosomes.SPECIES).getRegistryName().equals(location)).findFirst().orElseThrow(() -> new SimpleCommandExceptionType(new LiteralMessage("Invalid Bee Type: " + location)).create());
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
			return SharedSuggestionProvider.suggest(BeeManager.beeRoot.getIndividualTemplates().stream()
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