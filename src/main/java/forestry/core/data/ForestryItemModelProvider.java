package forestry.core.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ResourceLocation;

import forestry.arboriculture.features.ArboricultureItems;
import forestry.core.config.Constants;
import forestry.farming.features.FarmingBlocks;
import forestry.lepidopterology.features.LepidopterologyItems;
import forestry.modules.features.FeatureItem;
import forestry.storage.ModuleCrates;
import forestry.storage.items.ItemCrated;

public class ForestryItemModelProvider extends ModelProvider {
	public ForestryItemModelProvider(DataGenerator generator) {
		super(generator, "item");
	}

	@Override
	protected void registerModels() {
		for (BlockItem farm : FarmingBlocks.FARM.getItems()) {
			registerModel(farm, new ModelBuilder().parent(Constants.MOD_ID + ":block/farm"));
		}
		registerModel(LepidopterologyItems.CATERPILLAR_GE, new ModelBuilder()
			.item()
			.layer(0, new ResourceLocation(Constants.MOD_ID, "item/caterpillar.body2"))
			.layer(1, new ResourceLocation(Constants.MOD_ID, "item/caterpillar.body")));
		registerModel(LepidopterologyItems.SERUM_GE, new ModelBuilder()
			.item()
			.layer(0, new ResourceLocation(Constants.MOD_ID, "item/liquids/jar.bottle"))
			.layer(1, new ResourceLocation(Constants.MOD_ID, "item/liquids/jar.contents")));
		registerModel(ArboricultureItems.SAPLING, new ModelBuilder().parent("item/oak_sapling"));
		for (FeatureItem<ItemCrated> featureCrated : ModuleCrates.crates) {
			registerModel(featureCrated, new ModelBuilder()
				.parent(Constants.MOD_ID + ":item/crate-filled"));
		}
	}
}
