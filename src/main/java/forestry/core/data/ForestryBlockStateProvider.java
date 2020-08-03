package forestry.core.data;

import com.google.common.collect.Table;
import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.features.CultivationBlocks;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.features.FarmingBlocks;
import forestry.lepidopterology.features.LepidopterologyBlocks;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;
import forestry.modules.features.FeatureBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class ForestryBlockStateProvider extends BlockStateProvider {

    public ForestryBlockStateProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    public void registerStates() {
        for (ForestryFluids fluid : ForestryFluids.values()) {
            Block block = fluid.getFeature().fluidBlock().block();
            addVariants(block, new Builder().alwaysIgnore(FlowingFluidBlock.LEVEL).always(variant -> variant.model("forestry:block/fluid_" + fluid.getTag().getPath())));
        }
        addCocoon(LepidopterologyBlocks.COCOON);
        addCocoon(LepidopterologyBlocks.COCOON_SOLID);
        //Replaced by the model loader later
        for (BlockFarm farm : FarmingBlocks.FARM.getBlocks()) {
            addVariants(farm, new Builder().always(variant -> variant.model("forestry:block/farm")));
        }

        for (Table.Cell<BlockTypePlanter, BlockPlanter.Mode, FeatureBlock<BlockPlanter, BlockItem>> cell : CultivationBlocks.PLANTER.getFeatureByTypes().cellSet()) {
            addCultivationBlock(cell.getValue(), cell.getRowKey());
        }
    }

    private void addCultivationBlock(FeatureBlock<? extends Block, BlockItem> feature, BlockTypePlanter planter) {
        addVariants(feature.block(), new Builder()
                .always((variant) -> variant.model("forestry:block/" + planter.getString()))
                .property(BlockStateProperties.FACING, Direction.EAST, (variant) -> variant.rotationY(90))
                .property(BlockStateProperties.FACING, Direction.SOUTH, (variant) -> variant.rotationY(180))
                .property(BlockStateProperties.FACING, Direction.WEST, (variant) -> variant.rotationY(270)));
    }

    private void addCocoon(FeatureBlock<? extends Block, BlockItem> feature) {
        BlockState state = feature.defaultState();
        addVariants(feature.block(), new Builder()
                .property(AlleleButterflyCocoon.AGE, 0, variant -> variant.model(Constants.MOD_ID + ":block/cocoon_early"))
                .property(AlleleButterflyCocoon.AGE, 1, variant -> variant.model(Constants.MOD_ID + ":block/cocoon_middle"))
                .property(AlleleButterflyCocoon.AGE, 2, variant -> variant.model(Constants.MOD_ID + ":block/cocoon_late"))
                .state(state.with(AlleleButterflyCocoon.AGE, 2).with(AlleleButterflyCocoon.COCOON, ButterflyAlleles.cocoonSilk), variant -> variant.model(Constants.MOD_ID + ":block/cocoon_silk_late")));
    }
}
