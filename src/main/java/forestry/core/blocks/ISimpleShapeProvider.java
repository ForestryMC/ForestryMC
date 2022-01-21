package forestry.core.blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

@FunctionalInterface
public interface ISimpleShapeProvider extends IShapeProvider {

	VoxelShape getShape();

	@Override
	default VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return getShape();
	}
}
