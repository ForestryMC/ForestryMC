package forestry.apiculture;

import net.minecraft.world.World;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;

public class VanillaDefaultGrowthRule implements IFlowerGrowthRule {

	@Override
	public boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual, int x, int y, int z) {
		return true;
	}

}
