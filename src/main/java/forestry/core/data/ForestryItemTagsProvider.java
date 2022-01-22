package forestry.core.data;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import forestry.apiculture.features.ApicultureItems;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.mail.features.MailItems;

public final class ForestryItemTagsProvider extends ItemTagsProvider {
	@Nullable
	private Set<ResourceLocation> filter = null;

	public ForestryItemTagsProvider(DataGenerator generator, ForestryBlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, blockTagsProvider, Constants.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		super.addTags();
		builders.remove(ItemTags.SAPLINGS.getName());
		//builders.remove()
		filter = new HashSet<>(this.builders.keySet());
		filter.remove(ItemTags.LOGS.getName());
		filter.remove(ItemTags.PLANKS.getName());
		filter.remove(ItemTags.WOODEN_DOORS.getName());
		filter.remove(ItemTags.STAIRS.getName());
		filter.remove(ItemTags.SLABS.getName());
		filter.remove(ItemTags.DOORS.getName());
		filter.remove(ItemTags.LOGS_THAT_BURN.getName());
		filter.remove(ItemTags.WOODEN_STAIRS.getName());
		filter.remove(ItemTags.WOODEN_FENCES.getName());
		addToTag(ForestryTags.Items.GEARS, ForestryTags.Items.GEARS_BRONZE, ForestryTags.Items.GEARS_COPPER, ForestryTags.Items.GEARS_TIN);
		tag(ForestryTags.Items.GEARS_BRONZE).add(CoreItems.GEAR_BRONZE.item());
		tag(ForestryTags.Items.GEARS_TIN).add(CoreItems.GEAR_TIN.item());
		tag(ForestryTags.Items.GEARS_COPPER).add(CoreItems.GEAR_COPPER.item());
		tag(ForestryTags.Items.GEARS_STONE);

		addToTag(Tags.Items.INGOTS, ForestryTags.Items.INGOTS_BRONZE, ForestryTags.Items.INGOTS_COPPER, ForestryTags.Items.INGOTS_TIN);
		tag(ForestryTags.Items.INGOTS_BRONZE).add(CoreItems.INGOT_BRONZE.item());
		tag(ForestryTags.Items.INGOTS_TIN).add(CoreItems.INGOT_TIN.item());

		tag(ForestryTags.Items.DUSTS_ASH).add(CoreItems.ASH.item());
		tag(ForestryTags.Items.GEMS_APATITE).add(CoreItems.APATITE.item());

		addToTag(Tags.Items.STORAGE_BLOCKS, ForestryTags.Items.STORAGE_BLOCKS_APATITE, ForestryTags.Items.STORAGE_BLOCKS_BRONZE, ForestryTags.Items.STORAGE_BLOCKS_TIN);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE, ForestryTags.Items.STORAGE_BLOCKS_APATITE);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_TIN, ForestryTags.Items.STORAGE_BLOCKS_TIN);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE, ForestryTags.Items.STORAGE_BLOCKS_BRONZE);

		copy(ForestryTags.Blocks.CHARCOAL, ForestryTags.Items.CHARCOAL);

		//copy(BlockTags., ForestryTags.Items.STORAGE_BLOCKS_APATITE);
		tag(ItemTags.SAPLINGS).add(ArboricultureItems.SAPLING.item());
		tag(ForestryTags.Items.BEE_COMBS).add(ApicultureItems.BEE_COMBS.itemArray());
		tag(ForestryTags.Items.PROPOLIS).add(ApicultureItems.PROPOLIS.itemArray());
		tag(ForestryTags.Items.DROP_HONEY).add(ApicultureItems.HONEY_DROPS.itemArray());

		addToTag(Tags.Items.ORES, ForestryTags.Items.ORES_TIN, ForestryTags.Items.ORES_APATITE);
		copy(ForestryTags.Blocks.ORES_TIN, ForestryTags.Items.ORES_TIN);
		copy(ForestryTags.Blocks.ORES_APATITE, ForestryTags.Items.ORES_APATITE);

		tag(ForestryTags.Items.STAMPS).add(MailItems.STAMPS.itemArray());

		tag(ForestryTags.Items.FRUITS).add(CoreItems.FRUITS.itemArray());
		tag(ForestryTags.Items.DUSTS_ASH).add(CoreItems.ASH.item());
		tag(ForestryTags.Items.SAWDUST).add(CoreItems.WOOD_PULP.item());

		copy(Tags.Blocks.FENCES, Tags.Items.FENCES);
		copy(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
		copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
		copy(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
	}

	@SafeVarargs
	protected final void addToTag(Tag.Named<Item> tag, Tag.Named<Item>... providers) {
		TagsProvider.TagAppender<Item> builder = tag(tag);
		for (Tag.Named<Item> provider : providers) {
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
		return "Forestry Item Tags";
	}
}
