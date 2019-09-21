package forestry.core.data;

import com.google.common.collect.Table;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;

import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.features.CultivationBlocks;
import forestry.farming.features.FarmingBlocks;
import forestry.modules.features.FeatureBlock;

public class ForestryItemModelProvider extends ModelProvider {
	public ForestryItemModelProvider(DataGenerator generator) {
		super(generator, "item");
	}

	@Override
	protected void registerModels() {
		for (BlockItem farm : FarmingBlocks.FARM.getItems()) {
			registerModel(farm, new ModelBuilder().parent("forestry:block/farm"));
		}

		for(Table.Cell<BlockTypePlanter, BlockPlanter.Mode, FeatureBlock<BlockPlanter, BlockItem>> cell : CultivationBlocks.PLANTER.getFeatureByTypes().cellSet()) {
			Block block = cell.getValue().block();
			registerModel(block, new ModelBuilder().parent("forestry:block/" + cell.getRowKey().getName()));
		}
	}
}
