package forestry.core.data;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
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
		filter = this.tagToBuilder.keySet().stream().map(ITag::getId).collect(Collectors.toSet());
		if (ModuleHelper.isEnabled(ForestryModuleUids.CHARCOAL)) {
			getBuilder(ForestryTags.Blocks.CHARCOAL).add(CharcoalBlocks.CHARCOAL.block());
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			getBuilder(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS.blockArray());
			getBuilder(BlockTags.LOGS).add(ArboricultureBlocks.LOGS.blockArray());
			getBuilder(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS.blockArray());
			getBuilder(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS.blockArray());
			getBuilder(BlockTags.FENCES).add(ArboricultureBlocks.FENCES.blockArray());
			getBuilder(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES.blockArray());
			getBuilder(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES.blockArray());
			getBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES.blockArray());
			getBuilder(BlockTags.SLABS).add(ArboricultureBlocks.SLABS.blockArray());
			getBuilder(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS.blockArray());
			getBuilder(BlockTags.DOORS).add(ArboricultureBlocks.DOORS.blockArray());
			getBuilder(BlockTags.WOODEN_DOORS).add(ArboricultureBlocks.DOORS.blockArray());

			getBuilder(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS_FIREPROOF.blockArray());
			getBuilder(BlockTags.LOGS).add(ArboricultureBlocks.LOGS_FIREPROOF.blockArray());
			getBuilder(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS_FIREPROOF.blockArray());
			getBuilder(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS_FIREPROOF.blockArray());
			getBuilder(BlockTags.FENCES).add(ArboricultureBlocks.FENCES_FIREPROOF.blockArray());
			getBuilder(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES_FIREPROOF.blockArray());
			getBuilder(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES_FIREPROOF.blockArray());
			getBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES_FIREPROOF.blockArray());
			getBuilder(BlockTags.SLABS).add(ArboricultureBlocks.SLABS_FIREPROOF.blockArray());
			getBuilder(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS_FIREPROOF.blockArray());

			getBuilder(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS_VANILLA_FIREPROOF.blockArray());
			getBuilder(BlockTags.LOGS).add(ArboricultureBlocks.LOGS_VANILLA_FIREPROOF.blockArray());
			getBuilder(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.blockArray());
			getBuilder(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.blockArray());
			getBuilder(BlockTags.FENCES).add(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
			getBuilder(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
			getBuilder(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
			getBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
			getBuilder(BlockTags.SLABS).add(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());
			getBuilder(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());

			getBuilder(BlockTags.SAPLINGS).add(ArboricultureBlocks.SAPLING_GE.block());
			getBuilder(BlockTags.LEAVES).add(ArboricultureBlocks.LEAVES.block()).add(ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.blockArray()).add(ArboricultureBlocks.LEAVES_DEFAULT.blockArray()).add(ArboricultureBlocks.LEAVES_DECORATIVE.blockArray());

			//getBuilder(Tags.Blocks.CHESTS).add(registry.treeChest);
			//getBuilder(Tags.Blocks.CHESTS_WOODEN).add(registry.treeChest);
		}

		getBuilder(Tags.Blocks.ORES).add(ForestryTags.Blocks.ORES_COPPER, ForestryTags.Blocks.ORES_TIN, ForestryTags.Blocks.ORES_APATITE);
		getBuilder(ForestryTags.Blocks.ORES_COPPER).add(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER).block());
		getBuilder(ForestryTags.Blocks.ORES_TIN).add(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).block());
		getBuilder(ForestryTags.Blocks.ORES_APATITE).add(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE).block());

		getBuilder(Tags.Blocks.STORAGE_BLOCKS).add(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE, ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE, ForestryTags.Blocks.STORAGE_BLOCKS_COPPER, ForestryTags.Blocks.STORAGE_BLOCKS_TIN);
		getBuilder(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.APATITE).block());
		getBuilder(ForestryTags.Blocks.STORAGE_BLOCKS_TIN).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.TIN).block());
		getBuilder(ForestryTags.Blocks.STORAGE_BLOCKS_COPPER).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.COPPER).block());
		getBuilder(ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.BRONZE).block());
	}

	/*@Override
	@Nullable
	protected Path makePath(ResourceLocation id) {
		return filter != null && filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
	}*/

	@Override
	public String getName() {
		return "Forestry Block Tags";
	}
}
