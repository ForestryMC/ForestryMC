package forestry.farming.features;

import forestry.farming.ModuleFarming;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.BlockMushroom;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.blocks.EnumFarmMaterial;
import forestry.farming.items.ItemBlockFarm;
import forestry.modules.features.*;
import net.minecraft.item.BlockItem;

@FeatureProvider
public class FarmingBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleFarming.class);

	public static final FeatureBlock<BlockMushroom, BlockItem> MUSHROOM = REGISTRY.block(BlockMushroom::new, "mushroom");
	public static final FeatureBlockTable<BlockFarm, EnumFarmBlockType, EnumFarmMaterial> FARM = REGISTRY.blockTable(BlockFarm::new, EnumFarmBlockType.VALUES, EnumFarmMaterial.values()).item(ItemBlockFarm::new).identifier("farm").create();

	private FarmingBlocks() {
	}
}
