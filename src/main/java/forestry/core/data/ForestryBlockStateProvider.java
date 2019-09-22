package forestry.core.data;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;

import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;
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
			addVariants(block, new Builder().alwaysIgnore(FlowingFluidBlock.LEVEL).always(variant -> variant.model("forestry:block/fluid_" + fluid.getTag().getPath())));
		}
		addCocoon(LepidopterologyBlocks.COCOON);
		addCocoon(LepidopterologyBlocks.COCOON_SOLID);
		//Replaced by the model loader later
		for (BlockFarm farm : FarmingBlocks.FARM.getBlocks()) {
			addVariants(farm, new Builder().always(variant -> variant.model("forestry:block/farm")));
		}
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
