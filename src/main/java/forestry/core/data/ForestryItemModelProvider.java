package forestry.core.data;

import com.google.common.collect.Table;
import com.google.gson.JsonPrimitive;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.core.config.Constants;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.features.CultivationBlocks;
import forestry.farming.features.FarmingBlocks;
import forestry.lepidopterology.features.LepidopterologyItems;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureItem;
import forestry.storage.ModuleCrates;
import forestry.storage.items.ItemCrated;
import forestry.storage.models.CrateModel;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ResourceLocation;

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
        registerModel(
                ArboricultureItems.SAPLING,
                new ModelBuilder().parent("forge:item/default")
                                  .loader(new ResourceLocation(Constants.MOD_ID, "sapling_ge"))
        );
        for (FeatureItem<ItemCrated> featureCrated : ModuleCrates.crates) {
            registerModel(featureCrated, new ModelBuilder()
                    .parent(Constants.MOD_ID + ":item/crate-filled")
                    .loader(CrateModel.Loader.LOCATION)
                    .loaderData("variant", new JsonPrimitive(featureCrated.getIdentifier()))
            );
        }

        for (Table.Cell<BlockTypePlanter, BlockPlanter.Mode, FeatureBlock<BlockPlanter, BlockItem>> cell : CultivationBlocks.PLANTER
                .getFeatureByTypes()
                .cellSet()) {
            Block block = cell.getValue().block();
            registerModel(block, new ModelBuilder().parent("forestry:block/" + cell.getRowKey().getString()));
        }
    }
}
