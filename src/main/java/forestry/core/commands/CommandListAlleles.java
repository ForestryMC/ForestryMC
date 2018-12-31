package forestry.core.commands;


import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.apiculture.genetics.BeeGenome;
import forestry.arboriculture.genetics.TreeGenome;
import forestry.lepidopterology.genetics.ButterflyGenome;

public class CommandListAlleles extends SubCommand {

	public CommandListAlleles() {
		super("listAlleles");
		setPermLevel(PermLevel.ADMIN);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		EntityPlayer player = CommandBase.getPlayer(server, sender, sender.getName());

		ItemStack stack = player.getHeldItemMainhand();

		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(stack);
		if (individual == null) {
			return;
		}

		IChromosomeType[] types = null;
		IGenome genome = individual.getGenome();

		if (genome instanceof BeeGenome) {
			types = EnumBeeChromosome.values();
		} else if (genome instanceof TreeGenome) {
			types = EnumTreeChromosome.values();
		} else if (genome instanceof ButterflyGenome) {
			types = EnumButterflyChromosome.values();
		}

		if (types == null) {
			return;
		}

		for (IChromosomeType type : types) {
			CommandHelpers.sendChatMessage(sender, type.getName() + ": " + genome.getActiveAllele(type).getAlleleName() + " " + genome.getInactiveAllele(type).getAlleleName());
		}
	}
}
