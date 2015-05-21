package forestry.apiculture.genetics;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.core.genetics.IGeneticDefinition;

public interface IBeeDefinition extends IGeneticDefinition {
	@Override
	IBeeGenome getGenome();

	@Override
	IBee getIndividual();

	ItemStack getMemberStack(EnumBeeType beeType);
}
