package forestry.core.data;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.Tags;

import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.blocks.EnumResourceType;
import forestry.core.features.CoreBlocks;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

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
		filter = this.tagToBuilder.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
		if (ModuleHelper.isEnabled(ForestryModuleUids.CHARCOAL)) {
			func_240522_a_(ForestryTags.Blocks.CHARCOAL).func_240534_a_(CharcoalBlocks.CHARCOAL.block());
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			func_240522_a_(BlockTags.PLANKS).func_240534_a_(ArboricultureBlocks.PLANKS.blockArray());
			func_240522_a_(BlockTags.LOGS).func_240534_a_(ArboricultureBlocks.LOGS.blockArray());
			func_240522_a_(BlockTags.STAIRS).func_240534_a_(ArboricultureBlocks.STAIRS.blockArray());
			func_240522_a_(BlockTags.WOODEN_STAIRS).func_240534_a_(ArboricultureBlocks.STAIRS.blockArray());
			func_240522_a_(BlockTags.FENCES).func_240534_a_(ArboricultureBlocks.FENCES.blockArray());
			func_240522_a_(BlockTags.WOODEN_FENCES).func_240534_a_(ArboricultureBlocks.FENCES.blockArray());
			func_240522_a_(Tags.Blocks.FENCE_GATES).func_240534_a_(ArboricultureBlocks.FENCE_GATES.blockArray());
			func_240522_a_(Tags.Blocks.FENCE_GATES_WOODEN).func_240534_a_(ArboricultureBlocks.FENCE_GATES.blockArray());
			func_240522_a_(BlockTags.SLABS).func_240534_a_(ArboricultureBlocks.SLABS.blockArray());
			func_240522_a_(BlockTags.WOODEN_SLABS).func_240534_a_(ArboricultureBlocks.SLABS.blockArray());
			func_240522_a_(BlockTags.DOORS).func_240534_a_(ArboricultureBlocks.DOORS.blockArray());
			func_240522_a_(BlockTags.WOODEN_DOORS).func_240534_a_(ArboricultureBlocks.DOORS.blockArray());

			func_240522_a_(BlockTags.PLANKS).func_240534_a_(ArboricultureBlocks.PLANKS_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.LOGS).func_240534_a_(ArboricultureBlocks.LOGS_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.STAIRS).func_240534_a_(ArboricultureBlocks.STAIRS_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.WOODEN_STAIRS).func_240534_a_(ArboricultureBlocks.STAIRS_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.FENCES).func_240534_a_(ArboricultureBlocks.FENCES_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.WOODEN_FENCES).func_240534_a_(ArboricultureBlocks.FENCES_FIREPROOF.blockArray());
			func_240522_a_(Tags.Blocks.FENCE_GATES).func_240534_a_(ArboricultureBlocks.FENCE_GATES_FIREPROOF.blockArray());
			func_240522_a_(Tags.Blocks.FENCE_GATES_WOODEN).func_240534_a_(ArboricultureBlocks.FENCE_GATES_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.SLABS).func_240534_a_(ArboricultureBlocks.SLABS_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.WOODEN_SLABS).func_240534_a_(ArboricultureBlocks.SLABS_FIREPROOF.blockArray());

			func_240522_a_(BlockTags.PLANKS).func_240534_a_(ArboricultureBlocks.PLANKS_VANILLA_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.LOGS).func_240534_a_(ArboricultureBlocks.LOGS_VANILLA_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.STAIRS).func_240534_a_(ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.WOODEN_STAIRS).func_240534_a_(ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.FENCES).func_240534_a_(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.WOODEN_FENCES).func_240534_a_(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
			func_240522_a_(Tags.Blocks.FENCE_GATES).func_240534_a_(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
			func_240522_a_(Tags.Blocks.FENCE_GATES_WOODEN).func_240534_a_(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.SLABS).func_240534_a_(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());
			func_240522_a_(BlockTags.WOODEN_SLABS).func_240534_a_(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());

			func_240522_a_(BlockTags.SAPLINGS).func_240534_a_(ArboricultureBlocks.SAPLING_GE.block());
			func_240522_a_(BlockTags.LEAVES).func_240534_a_(ArboricultureBlocks.LEAVES.block()).func_240534_a_(ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.blockArray()).func_240534_a_(ArboricultureBlocks.LEAVES_DEFAULT.blockArray()).func_240534_a_(ArboricultureBlocks.LEAVES_DECORATIVE.blockArray());

			//func_240522_a_(Tags.Blocks.CHESTS).func_240534_a_(registry.treeChest);
			//func_240522_a_(Tags.Blocks.CHESTS_WOODEN).func_240534_a_(registry.treeChest);
		}

		addToTag(Tags.Blocks.ORES, ForestryTags.Blocks.ORES_COPPER, ForestryTags.Blocks.ORES_TIN, ForestryTags.Blocks.ORES_APATITE);
		func_240522_a_(ForestryTags.Blocks.ORES_COPPER).func_240534_a_(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER).block());
		func_240522_a_(ForestryTags.Blocks.ORES_TIN).func_240534_a_(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).block());
		func_240522_a_(ForestryTags.Blocks.ORES_APATITE).func_240534_a_(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE).block());

		addToTag(Tags.Blocks.STORAGE_BLOCKS, ForestryTags.Blocks.STORAGE_BLOCKS_APATITE, ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE, ForestryTags.Blocks.STORAGE_BLOCKS_COPPER, ForestryTags.Blocks.STORAGE_BLOCKS_TIN);
		func_240522_a_(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE).func_240534_a_(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.APATITE).block());
		func_240522_a_(ForestryTags.Blocks.STORAGE_BLOCKS_TIN).func_240534_a_(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.TIN).block());
		func_240522_a_(ForestryTags.Blocks.STORAGE_BLOCKS_COPPER).func_240534_a_(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.COPPER).block());
		func_240522_a_(ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE).func_240534_a_(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.BRONZE).block());
	}

	@SafeVarargs
	protected final void addToTag(ITag.INamedTag<Block> tag, ITag.INamedTag<Block>... providers) {
		TagsProvider.Builder<Block> builder = func_240522_a_(tag);
		for (ITag.INamedTag<Block> provider : providers) {
			builder.func_240531_a_(provider);
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
