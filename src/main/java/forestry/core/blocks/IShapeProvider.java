package forestry.core.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

@FunctionalInterface
public interface IShapeProvider {

    VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context);
}
