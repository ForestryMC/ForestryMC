package genetics.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import genetics.api.GeneticsAPI;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class CommandListAlleles {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("listAlleles").requires(PermLevel.ADMIN).executes(CommandListAlleles::execute);
    }

    public static int execute(CommandContext<CommandSource> context) throws CommandException, CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();

        ItemStack stack = player.getHeldItemMainhand();

        Optional<IIndividual> individual = GeneticsAPI.apiInstance.getRootHelper().getIndividual(stack);
        if (!individual.isPresent()) {
            return 0;
        }

        IGenome genome = individual.get().getGenome();

        for (IChromosome chromosome : genome.getChromosomes()) {
            IChromosomeType type = chromosome.getType();

            CommandHelpers.sendChatMessage(
                    context.getSource(),
                    type.getName() + ": "
                    + I18n.format(genome.getActiveAllele(type).getLocalisationKey()) + " "
                    + I18n.format(genome.getInactiveAllele(type).getLocalisationKey())
            );
        }

        GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles().forEach(
                a -> System.out.println(a.getRegistryName() + ": " + I18n.format(a.getLocalisationKey()))
        );

        return 1;
    }
}
