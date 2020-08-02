package forestry.core.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

@FunctionalInterface
public interface ISimpleShapeProvider extends IShapeProvider {

    VoxelShape getShape();

    @Override
    default VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return getShape();
    }
}
