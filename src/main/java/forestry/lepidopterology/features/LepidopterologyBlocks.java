package forestry.lepidopterology.features;

import forestry.api.core.ItemGroups;
import forestry.core.items.ItemBlockBase;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.blocks.BlockCocoon;
import forestry.lepidopterology.blocks.BlockLepidopterology;
import forestry.lepidopterology.blocks.BlockSolidCocoon;
import forestry.lepidopterology.blocks.BlockTypeLepidopterologyTesr;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

@FeatureProvider
public class LepidopterologyBlocks {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleLepidopterology.class);

    public static final FeatureBlock<BlockLepidopterology, ItemBlockBase> BUTTERFLY_CHEST = REGISTRY.block(
            () -> new BlockLepidopterology(BlockTypeLepidopterologyTesr.LEPICHEST),
            (block) -> new ItemBlockBase<>(
                    block,
                    new Item.Properties().group(ItemGroups.tabLepidopterology),
                    BlockTypeLepidopterologyTesr.LEPICHEST
            ),
            "butterfly_chest"
    );
    public static final FeatureBlock<BlockCocoon, BlockItem> COCOON = REGISTRY.block(BlockCocoon::new, "cocoon");
    public static final FeatureBlock<BlockSolidCocoon, BlockItem> COCOON_SOLID = REGISTRY.block(
            BlockSolidCocoon::new,
            "cocoon_solid"
    );

    private LepidopterologyBlocks() {
    }
}
