package forestry.core.genetics;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IGeneticFactory;
import forestry.api.genetics.IIndividualHandler;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;

public class GeneticFactory implements IGeneticFactory {

	@Override
	public IIndividualHandler createIndividualHandler(ItemStack itemStack, ISpeciesType speciesType, ISpeciesRoot speciesRoot) {
		return new IndividualHandler(itemStack, () -> speciesType, () -> speciesRoot);
	}
}
