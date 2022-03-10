package forestry.core.data;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.climatology.features.ClimatologyBlocks;
import forestry.core.blocks.EnumResourceType;
import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.database.features.DatabaseBlocks;
import forestry.energy.features.EnergyBlocks;
import forestry.factory.features.FactoryBlocks;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.features.FarmingBlocks;
import forestry.lepidopterology.features.LepidopterologyBlocks;
import forestry.mail.features.MailBlocks;
import forestry.modules.features.FeatureBlockGroup;
import forestry.worktable.features.WorktableBlocks;

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

		tag(ForestryTags.Blocks.MINEABLE_SCOOP);
		tag(ForestryTags.Blocks.MINEABLE_GRAFTER);

		{
			tag(BlockTags.MINEABLE_WITH_AXE).add(ApicultureBlocks.BEE_CHEST.block());
			tag(BlockTags.MINEABLE_WITH_AXE).add(LepidopterologyBlocks.BUTTERFLY_CHEST.block());

			tag(BlockTags.MINEABLE_WITH_AXE).add(ArboricultureBlocks.TREE_CHEST.block());
			tag(BlockTags.MINEABLE_WITH_AXE).add(CharcoalBlocks.WOOD_PILE.block());
			tag(BlockTags.MINEABLE_WITH_AXE).add(ClimatologyBlocks.HABITATFORMER.block());

			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CoreBlocks.APATITE_ORE.block());
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CoreBlocks.DEEPSLATE_APATITE_ORE.block());
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CoreBlocks.TIN_ORE.block());
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CoreBlocks.DEEPSLATE_TIN_ORE.block());
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CoreBlocks.RAW_TIN_BLOCK.block());

			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CharcoalBlocks.CHARCOAL.block());
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(DatabaseBlocks.DATABASE.block());
			tag(BlockTags.MINEABLE_WITH_PICKAXE).add(WorktableBlocks.WORKTABLE.block());

			for (BlockFarm block : FarmingBlocks.FARM.getBlocks()) {
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
			}

			for (Block block : union(CoreBlocks.RESOURCE_STORAGE, EnergyBlocks.ENGINES, FactoryBlocks.PLAIN, FactoryBlocks.TESR, MailBlocks.BASE)) {
				tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
			}

			tag(BlockTags.MINEABLE_WITH_SHOVEL).add(CoreBlocks.HUMUS.block());
			tag(BlockTags.MINEABLE_WITH_SHOVEL).add(CoreBlocks.BOG_EARTH.block());
			tag(BlockTags.MINEABLE_WITH_SHOVEL).add(CoreBlocks.PEAT.block());

			for (Block block : union(
					CoreBlocks.BASE,
					ApicultureBlocks.ALVEARY, ApicultureBlocks.BASE,
					ArboricultureBlocks.DOORS,
					ArboricultureBlocks.PLANKS, ArboricultureBlocks.PLANKS_FIREPROOF, ArboricultureBlocks.PLANKS_VANILLA_FIREPROOF,
					ArboricultureBlocks.LOGS, ArboricultureBlocks.LOGS_FIREPROOF, ArboricultureBlocks.LOGS_VANILLA_FIREPROOF,
					ArboricultureBlocks.FENCES, ArboricultureBlocks.FENCES_FIREPROOF, ArboricultureBlocks.FENCES_VANILLA_FIREPROOF)) {
				tag(BlockTags.MINEABLE_WITH_AXE).add(block);
			}
		}

		tag(BlockTags.MINEABLE_WITH_PICKAXE).addTag(ForestryTags.Blocks.ORES_TIN).addTag(ForestryTags.Blocks.ORES_APATITE).addTag(ForestryTags.Blocks.STORAGE_BLOCKS_RAW_TIN);
		tag(BlockTags.NEEDS_STONE_TOOL).addTag(ForestryTags.Blocks.ORES_TIN).addTag(ForestryTags.Blocks.ORES_APATITE).addTag(ForestryTags.Blocks.STORAGE_BLOCKS_RAW_TIN);

		tag(ForestryTags.Blocks.CHARCOAL).add(CharcoalBlocks.CHARCOAL.block());
		tag(Tags.Blocks.CHESTS).add(ApicultureBlocks.BEE_CHEST.block());
		tag(BlockTags.PLANKS).add(ArboricultureBlocks.PLANKS.blockArray());
		tag(BlockTags.LOGS).add(ArboricultureBlocks.LOGS.blockArray());
		tag(BlockTags.LOGS_THAT_BURN).add(ArboricultureBlocks.LOGS.blockArray());
		tag(BlockTags.STAIRS).add(ArboricultureBlocks.STAIRS.blockArray());
		tag(BlockTags.WOODEN_STAIRS).add(ArboricultureBlocks.STAIRS.blockArray());
		tag(BlockTags.FENCES).add(ArboricultureBlocks.FENCES.blockArray());
		tag(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES.blockArray());
		tag(Tags.Blocks.FENCES).add(ArboricultureBlocks.FENCES.blockArray());
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
		tag(Tags.Blocks.FENCES).add(ArboricultureBlocks.FENCES_FIREPROOF.blockArray());
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
		tag(Tags.Blocks.FENCES).add(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
		tag(BlockTags.WOODEN_FENCES).add(ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.blockArray());
		tag(Tags.Blocks.FENCE_GATES).add(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
		tag(Tags.Blocks.FENCE_GATES_WOODEN).add(ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.blockArray());
		tag(BlockTags.SLABS).add(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());
		tag(BlockTags.WOODEN_SLABS).add(ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.blockArray());

		tag(BlockTags.SAPLINGS).add(ArboricultureBlocks.SAPLING_GE.block());
		tag(BlockTags.LEAVES).add(ArboricultureBlocks.LEAVES.block()).add(ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.blockArray()).add(ArboricultureBlocks.LEAVES_DEFAULT.blockArray()).add(ArboricultureBlocks.LEAVES_DECORATIVE.blockArray());
		tag(Tags.Blocks.CHESTS).add(ArboricultureBlocks.TREE_CHEST.block());

		tag(Tags.Blocks.CHESTS).add(LepidopterologyBlocks.BUTTERFLY_CHEST.block());

		addToTag(Tags.Blocks.ORES, ForestryTags.Blocks.ORES_TIN, ForestryTags.Blocks.ORES_APATITE);
		tag(ForestryTags.Blocks.ORES_TIN).add(CoreBlocks.TIN_ORE.block(), CoreBlocks.DEEPSLATE_TIN_ORE.block());
		tag(ForestryTags.Blocks.ORES_APATITE).add(CoreBlocks.APATITE_ORE.block(), CoreBlocks.DEEPSLATE_APATITE_ORE.block());
		tag(ForestryTags.Blocks.STORAGE_BLOCKS_RAW_TIN).add(CoreBlocks.RAW_TIN_BLOCK.block());

		addToTag(Tags.Blocks.STORAGE_BLOCKS, ForestryTags.Blocks.STORAGE_BLOCKS_APATITE, ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE, ForestryTags.Blocks.STORAGE_BLOCKS_TIN);
		tag(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.APATITE).block());
		tag(ForestryTags.Blocks.STORAGE_BLOCKS_TIN).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.TIN).block());
		tag(ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE).add(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.BRONZE).block());

		tag(ForestryTags.Blocks.PALM_LOGS).add(ArboricultureBlocks.LOGS.get(EnumForestryWoodType.PALM).block());
		tag(ForestryTags.Blocks.PAPAYA_LOGS).add(ArboricultureBlocks.LOGS.get(EnumForestryWoodType.PAPAYA).block());

		tag(BlockTags.DIRT).add(CoreBlocks.HUMUS.block());
	}

	@SafeVarargs
	protected final void addToTag(TagKey<Block> tag, TagKey<Block>... providers) {
		TagsProvider.TagAppender<Block> builder = tag(tag);
		for (TagKey<Block> provider : providers) {
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

	private static Collection<Block> union(FeatureBlockGroup<?, ?>... features) {
		Set<Block> set = new HashSet<>();

		for (FeatureBlockGroup<?, ?> feature : features) {
			set.addAll(feature.getBlocks());
		}

		return set;
	}
}
