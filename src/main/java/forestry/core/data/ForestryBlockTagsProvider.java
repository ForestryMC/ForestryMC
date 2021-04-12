package forestry.core.data;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.blocks.EnumResourceType;
import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

//TODO: Split up ?
public final class ForestryBlockTagsProvider extends BlockTagsProvider {
	@Nullable
	private Set<ResourceLocation> filter = null;

	public ForestryBlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, Constants.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		//super.registerTags();
		filter = new HashSet<>(this.builders.keySet());
		if (ModuleHelper.isEnabled(ForestryModuleUids.CHARCOAL)) {
			tag(ForestryTags.Blocks.CHARCOAL).add(CharcoalBlocks.CHARCOAL.block());
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			tag(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS.blockArray());
			tag(BlockTags.LOGS).add(ArboricultureBlocks.LOGS.blockArray());
			tag(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS.blockArray());
			tag(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS.blockArray());
			tag(BlockTags.FENCES).add(ArboricultureBlocks.FENCES.blockArray());
			tag(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES.blockArray());
			tag(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES.blockArray());
			tag(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES.blockArray());
			tag(BlockTags.SLABS).add(ArboricultureBlocks.SLABS.blockArray());
			tag(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS.blockArray());
			tag(BlockTags.DOORS).add(ArboricultureBlocks.DOORS.blockArray());
			tag(BlockTags.WOODEN_DOORS).add(ArboricultureBlocks.DOORS.blockArray());

			tag(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS_FIREPROOF.blockArray());
			tag(BlockTags.LOGS).add(ArboricultureBlocks.LOGS_FIREPROOF.blockArray());
			tag(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS_FIREPROOF.blockArray());
			tag(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS_FIREPROOF.blockArray());
			tag(BlockTags.FENCES).add(ArboricultureBlocks.FENCES_FIREPROOF.blockArray());
			tag(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES_FIREPROOF.blockArray());
			tag(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES_FIREPROOF.blockArray());
			tag(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES_FIREPROOF.blockArray());
			tag(BlockTags.SLABS).add(ArboricultureBlocks.SLABS_FIREPROOF.blockArray());
			tag(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS_FIREPROOF.blockArray());

			tag(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS_VANILLA_FIREPROOF.blockArray());
			tag(BlockTags.LOGS).add(ArboricultureBlocks.LOGS_VANILLA_FIREPROOF.blockArray());
			tag(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.blockArray());
			tag(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.blockArray());
			tag(BlockTags.FENCES).add(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
			tag(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
			tag(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
			tag(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
			tag(BlockTags.SLABS).add(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());
			tag(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());

			tag(BlockTags.SAPLINGS).add(ArboricultureBlocks.SAPLING_GE.block());
			tag(BlockTags.LEAVES).add(ArboricultureBlocks.LEAVES.block()).add(ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.blockArray()).add(ArboricultureBlocks.LEAVES_DEFAULT.blockArray()).add(ArboricultureBlocks.LEAVES_DECORATIVE.blockArray());

			//getOrCreateBuilder(Tags.Blocks.CHESTS).add(registry.treeChest);
			//getOrCreateBuilder(Tags.Blocks.CHESTS_WOODEN).add(registry.treeChest);
		}

		addToTag(Tags.Blocks.ORES, ForestryTags.Blocks.ORES_COPPER, ForestryTags.Blocks.ORES_TIN, ForestryTags.Blocks.ORES_APATITE);
		tag(ForestryTags.Blocks.ORES_COPPER).add(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER).block());
		tag(ForestryTags.Blocks.ORES_TIN).add(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).block());
		tag(ForestryTags.Blocks.ORES_APATITE).add(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE).block());

		addToTag(Tags.Blocks.STORAGE_BLOCKS, ForestryTags.Blocks.STORAGE_BLOCKS_APATITE, ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE, ForestryTags.Blocks.STORAGE_BLOCKS_COPPER, ForestryTags.Blocks.STORAGE_BLOCKS_TIN);
		tag(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.APATITE).block());
		tag(ForestryTags.Blocks.STORAGE_BLOCKS_TIN).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.TIN).block());
		tag(ForestryTags.Blocks.STORAGE_BLOCKS_COPPER).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.COPPER).block());
		tag(ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.BRONZE).block());

		tag(Tags.Blocks.DIRT).add(CoreBlocks.HUMUS.block());
	}

	@SafeVarargs
	protected final void addToTag(ITag.INamedTag<Block> tag, ITag.INamedTag<Block>... providers) {
		TagsProvider.Builder<Block> builder = tag(tag);
		for (ITag.INamedTag<Block> provider : providers) {
			builder.addTag(provider);
		}
	}

	@Override
	@Nullable
	protected Path getPath(ResourceLocation id) {
		return filter != null && filter.contains(id) ? null : super.getPath(id); //We don't want to save vanilla tags.
	}

	@Override
	public String getName() {
		return "Forestry Block Tags";
	}
}
