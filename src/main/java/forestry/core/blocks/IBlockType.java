package forestry.core.blocks;

import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import forestry.api.core.IBlockSubtype;

public interface IBlockType extends IBlockSubtype, ISimpleShapeProvider {
	IMachineProperties<?> getMachineProperties();

	@Override
	default VoxelShape getShape() {
		return VoxelShapes.fullCube();
	}
}
