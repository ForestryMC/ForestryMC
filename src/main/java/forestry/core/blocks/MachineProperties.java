package forestry.core.blocks;

import forestry.core.tiles.TileForestry;
import forestry.modules.features.FeatureTileType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MachineProperties<T extends TileForestry> implements IMachineProperties<T> {
	private final String name;
    private final Supplier<FeatureTileType<? extends T>> teType;
	private final VoxelShape shape;
	@Nullable
	private Block block;

    public MachineProperties(Supplier<FeatureTileType<? extends T>> teType, String name) {
        this(teType, name, VoxelShapes.fullCube());
    }

    public MachineProperties(Supplier<FeatureTileType<? extends T>> teType, String name, VoxelShape shape) {
        this.teType = teType;
		this.name = name;
		this.shape = shape;
	}

	@Override
	public void setBlock(Block block) {
		this.block = block;
	}

	@Nullable
	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return shape;
	}

	@Override
	public TileEntity createTileEntity() {
        return teType.get().getTileType().create();
	}

	@Override
    public TileEntityType<? extends T> getTeType() {
        return teType.get().getTileType();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return true;
	}
}
