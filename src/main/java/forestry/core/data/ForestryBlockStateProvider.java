package forestry.core.data;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.data.DataGenerator;

import forestry.core.fluids.ForestryFluids;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.features.FarmingBlocks;

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
		//Replaced by the model loader later
		for (BlockFarm farm : FarmingBlocks.FARM.getBlocks()) {
			addVariants(farm, new Builder().always(variant -> variant.model("forestry:block/farm")));
		}
	}
}
