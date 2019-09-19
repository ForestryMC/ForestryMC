package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import forestry.core.tiles.TileForestry;

public class MachineProperties<T extends TileForestry> implements IMachineProperties<T> {
	private final String name;
	private final Class<T> teClass;
	private final VoxelShape shape;
	@Nullable
	private Block block;

	public MachineProperties(Class<T> teClass, String name) {
		this(teClass, name, VoxelShapes.fullCube());
	}

	public MachineProperties(Class<T> teClass, String name, VoxelShape shape) {
		this.teClass = teClass;
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
	public void registerTileEntity() {
		//TODO - need to make sure that everything that used to call this is now a registered TileEntityType
		//		TileUtil.registerTile(teClass, name);
	}

	@Override
	public void clientSetup() {

	}

	@Override
	public TileEntity createTileEntity() {
		try {
			return teClass.getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to instantiate tile entity of class " + teClass.getName(), e);
		}
	}

	@Override
	public Class<T> getTeClass() {
		return teClass;
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
