package forestry.apiculture.features;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.api.core.ItemGroups;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.blocks.BlockAlveary;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.blocks.BlockApiculture;
import forestry.apiculture.blocks.BlockBeeHive;
import forestry.apiculture.blocks.BlockHoneyComb;
import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.blocks.BlockTypeApicultureTesr;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.ItemBlockHoneyComb;
import forestry.core.blocks.BlockBase;
import forestry.core.items.ItemBlockBase;
import forestry.core.items.ItemBlockForestry;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ApicultureBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleApiculture.class);

	public static final FeatureBlockGroup<BlockApiculture, BlockTypeApiculture> BASE = REGISTRY.blockGroup(BlockApiculture::new, BlockTypeApiculture.values()).item((block) -> new ItemBlockForestry<>(block, new Item.Properties().tab(ItemGroups.tabApiculture))).create();

	public static final FeatureBlock<BlockBase<BlockTypeApicultureTesr>, ItemBlockBase> BEE_CHEST = REGISTRY.block(() -> new BlockBase<>(BlockTypeApicultureTesr.APIARIST_CHEST, Block.Properties.of(Material.WOOD)), (block) -> new ItemBlockBase<>(block, new Item.Properties().tab(ItemGroups.tabApiculture), BlockTypeApicultureTesr.APIARIST_CHEST), "bee_chest");

	public static final FeatureBlockGroup<BlockBeeHive, IHiveRegistry.HiveType> BEEHIVE = REGISTRY.blockGroup(BlockBeeHive::new, IHiveRegistry.HiveType.VALUES).itemWithType((block, type) -> new ItemBlockForestry<>(block, new Item.Properties().tab(type == IHiveRegistry.HiveType.SWARM ? null : ItemGroups.tabApiculture))).identifier("beehive").create();

	public static final FeatureBlockGroup<BlockHoneyComb, EnumHoneyComb> BEE_COMB = REGISTRY.blockGroup(BlockHoneyComb::new, EnumHoneyComb.VALUES).item(ItemBlockHoneyComb::new).identifier("block_bee_comb").create();
	public static final FeatureBlockGroup<BlockAlveary, BlockAlvearyType> ALVEARY = REGISTRY.blockGroup(BlockAlveary::new, BlockAlvearyType.VALUES).item(blockAlveary -> new ItemBlockForestry<>(blockAlveary, new Item.Properties().tab(ItemGroups.tabApiculture))).identifier("alveary").create();

	private ApicultureBlocks() {
	}
}
