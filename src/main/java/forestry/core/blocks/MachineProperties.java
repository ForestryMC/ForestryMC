package forestry.core.blocks;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import forestry.core.tiles.TileForestry;
import forestry.modules.features.FeatureTileType;

public class MachineProperties<T extends TileForestry> implements IMachineProperties<T> {
	private static final ISimpleShapeProvider FULL_CUBE = Shapes::block;

	private final String name;
	private final Supplier<FeatureTileType<? extends T>> teType;
	private final IShapeProvider shape;
	@Nullable
	private Block block;

	public MachineProperties(Supplier<FeatureTileType<? extends T>> teType, String name, IShapeProvider shape) {
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
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return shape.getShape(state, reader, pos, context);
	}

	@Override
	public BlockEntity createTileEntity() {
		return teType.get().getTileType().create();
	}

	@Override
	public BlockEntityType<? extends T> getTeType() {
		return teType.get().getTileType();
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public static class Builder<T extends TileForestry, B extends Builder<T, ?>> {
		@Nullable
		protected Supplier<FeatureTileType<? extends T>> type;
		@Nullable
		protected String name;
		protected IShapeProvider shape = FULL_CUBE;

		public Builder(Supplier<FeatureTileType<? extends T>> type, String name) {
			this.type = type;
			this.name = name;
		}

		public Builder() {
		}

		public B setName(String name) {
			this.name = name;
			//noinspection unchecked
			return (B) this;
		}

		public B setType(Supplier<FeatureTileType<? extends T>> teType) {
			this.type = teType;
			//noinspection unchecked
			return (B) this;
		}

		public B setShape(VoxelShape shape) {
			return setShape(() -> shape);
		}

		public B setShape(ISimpleShapeProvider shape) {
			this.shape = shape;
			//noinspection unchecked
			return (B) this;
		}

		public B setShape(IShapeProvider shape) {
			this.shape = shape;
			//noinspection unchecked
			return (B) this;
		}

		public MachineProperties<T> create() {
			Preconditions.checkNotNull(type);
			Preconditions.checkNotNull(name);
			Preconditions.checkNotNull(shape);
			return new MachineProperties<>(type, name, shape);
		}
	}
}
