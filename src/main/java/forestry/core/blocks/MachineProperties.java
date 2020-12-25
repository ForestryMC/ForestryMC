package forestry.core.blocks;

import com.google.common.base.Preconditions;
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
    private static final ISimpleShapeProvider FULL_CUBE = VoxelShapes::fullCube;

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

    @Nullable
    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public void setBlock(Block block) {
        this.block = block;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return shape.getShape(state, reader, pos, context);
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
    public String getString() {
        return name;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return true;
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
