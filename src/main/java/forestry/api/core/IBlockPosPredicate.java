package forestry.api.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockPosPredicate {
	boolean test(World world, BlockPos blockPos);
}
