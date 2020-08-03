package forestry.core.fluids;

import forestry.modules.features.FeatureFluid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class ForestryFluid extends FlowingFluid {
    public final boolean flowing;
    public final FeatureFluid definition;

    public ForestryFluid(FeatureFluid definition, boolean flowing) {
        this.definition = definition;
        this.flowing = flowing;
    }

    @Override
    protected FluidAttributes createAttributes() {
        ResourceLocation[] resources = definition.getProperties().resources;
        return FluidAttributes.builder(resources[0], resources[1])
                .density(definition.getProperties().density)
                .viscosity(definition.getProperties().viscosity)
                .temperature(definition.getProperties().temperature)
                .build(this);
    }

    @Override
    public Fluid getFlowingFluid() {
        if (flowing) {
            return this;
        }
        return definition.flowing();
    }

    @Override
    public Fluid getStillFluid() {
        if (!flowing) {
            return this;
        }
        return definition.fluid();
    }

    @Override
    protected boolean canSourcesMultiply() {
        return false;
    }

    @Override
    protected void beforeReplacingBlock(IWorld world, BlockPos blockPos, BlockState blockState) {
        TileEntity tileEntity = blockState.hasTileEntity() ? world.getTileEntity(blockPos) : null;
        Block.spawnDrops(blockState, world.getWorld(), blockPos, tileEntity);
    }

    @Override
    protected int getSlopeFindDistance(IWorldReader iWorldReader) {
        return 4;
    }

    @Override
    protected int getLevelDecreasePerBlock(IWorldReader iWorldReader) {
        return 1;
    }

    @Override
    public Item getFilledBucket() {
        return null;    //TODO fluids
    }

    @Override
    protected boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(IWorldReader worldReader) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    public Block getBlock() {
        return definition.fluidBlock().block();
    }

    @Override
    protected BlockState getBlockState(FluidState state) {
        return getBlock().getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state));
    }

    public static class Flowing extends ForestryFluid {
        public Flowing(FeatureFluid definition) {
            super(definition, true);
        }

        @Override
        protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL_1_8);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends ForestryFluid {
        public Source(FeatureFluid definition) {
            super(definition, false);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

}
