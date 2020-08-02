package forestry.core.features;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import net.minecraftforge.common.ToolType;

import forestry.core.ModuleCore;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.blocks.BlockCore;
import forestry.core.blocks.BlockHumus;
import forestry.core.blocks.BlockResourceOre;
import forestry.core.blocks.BlockResourceStorage;
import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.blocks.EnumResourceType;
import forestry.core.items.ItemBlockBase;
import forestry.core.items.ItemBlockForestry;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class CoreBlocks {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleCore.class);

    public static final FeatureBlockGroup<BlockCore, BlockTypeCoreTesr> BASE = REGISTRY.blockGroup(BlockCore::new, BlockTypeCoreTesr.VALUES).itemWithType(ItemBlockBase::new).create();
    public static final FeatureBlock<BlockBogEarth, ItemBlockForestry> BOG_EARTH = REGISTRY.block(BlockBogEarth::new, ItemBlockForestry::new, "bog_earth");
    public static final FeatureBlock<Block, ItemBlockForestry> PEAT = REGISTRY.block(() -> new Block(Block.Properties.create(Material.EARTH)
            .hardnessAndResistance(0.5f)
            .sound(SoundType.GROUND)
            .harvestTool(ToolType.SHOVEL)
            .harvestLevel(0)), "peat");
    public static final FeatureBlock<BlockHumus, ItemBlockForestry> HUMUS = REGISTRY.block(BlockHumus::new, ItemBlockForestry::new, "humus");
    public static final FeatureBlockGroup<BlockResourceStorage, EnumResourceType> RESOURCE_STORAGE = REGISTRY.blockGroup(BlockResourceStorage::new, EnumResourceType.VALUES).item(ItemBlockForestry::new).identifier("resource_storage").create();
    public static final FeatureBlockGroup<BlockResourceOre, EnumResourceType> RESOURCE_ORE = REGISTRY.blockGroup(BlockResourceOre::new, new EnumResourceType[]{EnumResourceType.APATITE, EnumResourceType.COPPER, EnumResourceType.TIN}).item(ItemBlockForestry::new).identifier("resource_ore").create();

    private CoreBlocks() {
    }
}
