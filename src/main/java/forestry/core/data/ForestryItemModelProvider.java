package forestry.core.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;

import forestry.farming.features.FarmingBlocks;

public class ForestryItemModelProvider extends ModelProvider {
	public ForestryItemModelProvider(DataGenerator generator) {
		super(generator, "item");
	}

	@Override
	protected void registerModels() {
		for (BlockItem farm : FarmingBlocks.FARM.getItems()) {
			registerModel(farm, new ModelBuilder().parent("forestry:block/farm"));
		}
	}
}
