package forestry.core.data;

import com.google.common.collect.Table;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;

import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
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

public class ForestryBlockStateProvider extends BlockStateProvider {

	public ForestryBlockStateProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void registerStates() {
		for (ForestryFluids fluid : ForestryFluids.values()) {
			Block block = fluid.getFeature().fluidBlock().block();
			addVariants(block, new Builder().alwaysIgnore(LiquidBlock.LEVEL).always(variant -> variant.model("forestry:block/fluid_" + fluid.getTag().getPath())));
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

		addVariants(CoreBlocks.APATITE_ORE.block(), new Builder().always(variant -> variant.model(Constants.MOD_ID + ":block/apatite_ore")));
		addVariants(CoreBlocks.DEEPSLATE_APATITE_ORE.block(), new Builder().always(variant -> variant.model(Constants.MOD_ID + ":block/deepslate_apatite_ore")));
		addVariants(CoreBlocks.TIN_ORE.block(), new Builder().always(variant -> variant.model(Constants.MOD_ID + ":block/tin_ore")));
		addVariants(CoreBlocks.DEEPSLATE_TIN_ORE.block(), new Builder().always(variant -> variant.model(Constants.MOD_ID + ":block/deepslate_tin_ore")));
	}

	private void addCultivationBlock(FeatureBlock<? extends Block, BlockItem> feature, BlockTypePlanter planter) {
		addVariants(feature.block(), new Builder()
				.always((variant) -> variant.model("forestry:block/" + planter.getSerializedName()))
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
				.state(state.setValue(AlleleButterflyCocoon.AGE, 2).setValue(AlleleButterflyCocoon.COCOON, ButterflyAlleles.cocoonSilk), variant -> variant.model(Constants.MOD_ID + ":block/cocoon_silk_late")));
	}
}
