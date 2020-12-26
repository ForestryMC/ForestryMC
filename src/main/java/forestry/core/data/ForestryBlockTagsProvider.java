package forestry.core.data;

import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.blocks.EnumResourceType;
import forestry.core.features.CoreBlocks;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//TODO: Split up ?
public final class ForestryBlockTagsProvider extends BlockTagsProvider {
    @Nullable
    private Set<ResourceLocation> filter = null;

    public ForestryBlockTagsProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerTags() {
        //super.registerTags();
        filter = new HashSet<>(this.tagToBuilder.keySet());
        if (ModuleHelper.isEnabled(ForestryModuleUids.CHARCOAL)) {
            getOrCreateBuilder(ForestryTags.Blocks.CHARCOAL).add(CharcoalBlocks.CHARCOAL.block());
        }
        if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
            getOrCreateBuilder(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS.blockArray());
            getOrCreateBuilder(BlockTags.LOGS).add(ArboricultureBlocks.LOGS.blockArray());
            getOrCreateBuilder(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS.blockArray());
            getOrCreateBuilder(BlockTags.FENCES).add(ArboricultureBlocks.FENCES.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES.blockArray());
            getOrCreateBuilder(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES.blockArray());
            getOrCreateBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES.blockArray());
            getOrCreateBuilder(BlockTags.SLABS).add(ArboricultureBlocks.SLABS.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS.blockArray());
            getOrCreateBuilder(BlockTags.DOORS).add(ArboricultureBlocks.DOORS.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_DOORS).add(ArboricultureBlocks.DOORS.blockArray());

            getOrCreateBuilder(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.LOGS).add(ArboricultureBlocks.LOGS_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.FENCES).add(ArboricultureBlocks.FENCES_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES_FIREPROOF.blockArray());
            getOrCreateBuilder(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES_FIREPROOF.blockArray());
            getOrCreateBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.SLABS).add(ArboricultureBlocks.SLABS_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS_FIREPROOF.blockArray());

            getOrCreateBuilder(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS_VANILLA_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.LOGS).add(ArboricultureBlocks.LOGS_VANILLA_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.FENCES).add(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
            getOrCreateBuilder(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
            getOrCreateBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.SLABS).add(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());
            getOrCreateBuilder(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());

            getOrCreateBuilder(BlockTags.SAPLINGS).add(ArboricultureBlocks.SAPLING_GE.block());
            getOrCreateBuilder(BlockTags.LEAVES).add(ArboricultureBlocks.LEAVES.block())
                                                .add(ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.blockArray())
                                                .add(ArboricultureBlocks.LEAVES_DEFAULT.blockArray())
                                                .add(ArboricultureBlocks.LEAVES_DECORATIVE.blockArray());

            //getOrCreateBuilder(Tags.Blocks.CHESTS).add(registry.treeChest);
            //getOrCreateBuilder(Tags.Blocks.CHESTS_WOODEN).add(registry.treeChest);
        }

        addToTag(
                Tags.Blocks.ORES,
                ForestryTags.Blocks.ORES_COPPER,
                ForestryTags.Blocks.ORES_TIN,
                ForestryTags.Blocks.ORES_APATITE
        );
        getOrCreateBuilder(ForestryTags.Blocks.ORES_COPPER).add(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER)
                                                                                       .block());
        getOrCreateBuilder(ForestryTags.Blocks.ORES_TIN).add(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).block());
        getOrCreateBuilder(ForestryTags.Blocks.ORES_APATITE).add(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE)
                                                                                        .block());

        addToTag(
                Tags.Blocks.STORAGE_BLOCKS,
                ForestryTags.Blocks.STORAGE_BLOCKS_APATITE,
                ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE,
                ForestryTags.Blocks.STORAGE_BLOCKS_COPPER,
                ForestryTags.Blocks.STORAGE_BLOCKS_TIN
        );
        getOrCreateBuilder(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE).add(CoreBlocks.RESOURCE_STORAGE.get(
                EnumResourceType.APATITE).block());
        getOrCreateBuilder(ForestryTags.Blocks.STORAGE_BLOCKS_TIN).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.TIN)
                                                                                                  .block());
        getOrCreateBuilder(ForestryTags.Blocks.STORAGE_BLOCKS_COPPER).add(CoreBlocks.RESOURCE_STORAGE.get(
                EnumResourceType.COPPER).block());
        getOrCreateBuilder(ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE).add(CoreBlocks.RESOURCE_STORAGE.get(
                EnumResourceType.BRONZE).block());
    }

    @SafeVarargs
    protected final void addToTag(ITag.INamedTag<Block> tag, ITag.INamedTag<Block>... providers) {
        TagsProvider.Builder<Block> builder = getOrCreateBuilder(tag);
        for (ITag.INamedTag<Block> provider : providers) {
            builder.addTag(provider);
        }
    }

    @Override
    @Nullable
    protected Path makePath(ResourceLocation id) {
        return filter != null && filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
    }

    @Override
    public String getName() {
        return "Forestry Block Tags";
    }
}
