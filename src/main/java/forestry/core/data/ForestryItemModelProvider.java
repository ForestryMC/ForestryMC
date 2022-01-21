package forestry.core.data;

import com.google.common.collect.Table;
import com.google.gson.JsonPrimitive;

import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.resources.ResourceLocation;

import forestry.arboriculture.features.ArboricultureItems;
import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.features.CultivationBlocks;
import forestry.farming.features.FarmingBlocks;
import forestry.lepidopterology.features.LepidopterologyItems;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureItem;
import forestry.storage.features.CrateItems;
import forestry.storage.items.ItemCrated;
import forestry.storage.models.CrateModel;

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
		registerModel(ArboricultureItems.SAPLING, new ModelBuilder().parent("forge:item/default").loader(new ResourceLocation(Constants.MOD_ID, "sapling_ge")));
		for (FeatureItem<ItemCrated> featureCrated : CrateItems.getCrates()) {
			registerModel(featureCrated, new ModelBuilder()
					.parent(Constants.MOD_ID + ":item/crate-filled")
					.loader(CrateModel.Loader.LOCATION)
					.loaderData("variant", new JsonPrimitive(featureCrated.getIdentifier()))
			);
		}

		for (Table.Cell<BlockTypePlanter, BlockPlanter.Mode, FeatureBlock<BlockPlanter, BlockItem>> cell : CultivationBlocks.PLANTER.getFeatureByTypes().cellSet()) {
			Block block = cell.getValue().block();
			registerModel(block, new ModelBuilder().parent("forestry:block/" + cell.getRowKey().getSerializedName()));
		}
		for (ForestryFluids fluid : ForestryFluids.values()) {
			BucketItem item = fluid.getBucket();
			if (item == null) {
				continue;
			}
			//"bucket_"
			registerModel(item, new ModelBuilder()
					.loader(new ResourceLocation("forge", "bucket"))
					.parent("forge:item/bucket_drip")
					.loaderData("fluid", new JsonPrimitive(fluid.getFluid().getRegistryName().toString()))
			);
		}
	}
}
