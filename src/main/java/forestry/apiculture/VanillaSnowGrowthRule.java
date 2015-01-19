package forestry.apiculture;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.core.config.Defaults;
import forestry.core.utils.StackUtils;

public class VanillaSnowGrowthRule implements IFlowerGrowthRule {

	@Override
	public boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual, int x, int y, int z) {
		if (world.getBlock(x, y, z) != Blocks.snow)
			return false;
		Block ground = world.getBlock(x, y - 1, z);
		if (ground != Blocks.dirt && ground != Blocks.grass)
			return false;
		ItemStack flower = fr.getRandomPlantableFlower(flowerType, world.rand);
		world.setBlockToAir(x, y, z);
		return world.setBlock(x, y, z, StackUtils.getBlock(flower), flower.getItemDamage(), Defaults.FLAG_BLOCK_SYNCH);
	}

}
