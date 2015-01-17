package forestry.apiculture;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;

public class VanillaFertilizeGrowthRule implements IFlowerGrowthRule {

	private final List<Block> allowedItems;

	public VanillaFertilizeGrowthRule(Block... allowedItems) {
		this.allowedItems = Arrays.asList(allowedItems);
	}

	@Override
	public boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual, int x, int y, int z) {
		Block ground = world.getBlock(x, y, z);
		int groundMeta;
		for (Block b : this.allowedItems) {
			if (b == ground) {
				groundMeta = world.getBlockMetadata(x, y, z);
				if (groundMeta > 6)
					return false;
				if (groundMeta < 6)
					groundMeta += world.rand.nextInt(1) + 1;
				else
					groundMeta = 7;

				return world.setBlockMetadataWithNotify(x, y, z, groundMeta, 2);
			}
		}

		return false;
	}

}
