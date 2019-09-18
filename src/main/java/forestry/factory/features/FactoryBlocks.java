package forestry.factory.features;

import java.util.function.Function;

import net.minecraft.item.BlockItem;

import forestry.core.config.Constants;
import forestry.core.items.ItemBlockBase;
import forestry.core.items.ItemBlockForestry;
import forestry.factory.blocks.BlockFactoryPlain;
import forestry.factory.blocks.BlockFactoryTESR;
import forestry.factory.blocks.BlockTypeFactoryPlain;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class FactoryBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.FACTORY);

	public static final FeatureBlockGroup<BlockFactoryTESR, BlockTypeFactoryTesr> TESR = REGISTRY.blockGroup(BlockFactoryTESR::new, BlockTypeFactoryTesr.VALUES).setItem(ItemBlockBase::new).create();
	public static final FeatureBlockGroup<BlockFactoryPlain, BlockTypeFactoryPlain> PLAIN = REGISTRY.blockGroup(BlockFactoryPlain::new, BlockTypeFactoryPlain.VALUES).setItem((Function<BlockFactoryPlain, BlockItem>) ItemBlockForestry::new).create();

	private FactoryBlocks() {
	}
}
