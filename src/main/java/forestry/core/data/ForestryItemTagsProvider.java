package forestry.core.data;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.Tags;

import forestry.apiculture.features.ApicultureItems;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.core.features.CoreItems;
import forestry.mail.features.MailItems;

public final class ForestryItemTagsProvider extends ItemTagsProvider {
	@Nullable
	private Set<ResourceLocation> filter = null;

	public ForestryItemTagsProvider(DataGenerator generator, ForestryBlockTagsProvider blockTagsProvider) {
		super(generator, blockTagsProvider);
	}

	@Override
	protected void addTags() {
		super.addTags();
		builders.remove(ItemTags.SAPLINGS.getName());
		filter = this.builders.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
		addToTag(ForestryTags.Items.GEARS, ForestryTags.Items.GEARS_BRONZE, ForestryTags.Items.GEARS_COPPER, ForestryTags.Items.GEARS_TIN);
		tag(ForestryTags.Items.GEARS_BRONZE).add(CoreItems.GEAR_BRONZE.item());
		tag(ForestryTags.Items.GEARS_TIN).add(CoreItems.GEAR_TIN.item());
		tag(ForestryTags.Items.GEARS_COPPER).add(CoreItems.GEAR_COPPER.item());
		tag(ForestryTags.Items.GEARS_STONE);

		addToTag(Tags.Items.INGOTS, ForestryTags.Items.INGOTS_BRONZE, ForestryTags.Items.INGOTS_COPPER, ForestryTags.Items.INGOTS_TIN);
		tag(ForestryTags.Items.INGOTS_BRONZE).add(CoreItems.INGOT_BRONZE.item());
		tag(ForestryTags.Items.INGOTS_TIN).add(CoreItems.INGOT_TIN.item());
		tag(ForestryTags.Items.INGOTS_COPPER).add(CoreItems.INGOT_COPPER.item());

		tag(ForestryTags.Items.DUSTS_ASH).add(CoreItems.ASH.item());
		tag(ForestryTags.Items.GEMS_APATITE).add(CoreItems.APATITE.item());

		addToTag(Tags.Items.STORAGE_BLOCKS, ForestryTags.Items.STORAGE_BLOCKS_APATITE, ForestryTags.Items.STORAGE_BLOCKS_BRONZE, ForestryTags.Items.STORAGE_BLOCKS_COPPER, ForestryTags.Items.STORAGE_BLOCKS_TIN);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE, ForestryTags.Items.STORAGE_BLOCKS_APATITE);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_TIN, ForestryTags.Items.STORAGE_BLOCKS_TIN);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_COPPER, ForestryTags.Items.STORAGE_BLOCKS_COPPER);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE, ForestryTags.Items.STORAGE_BLOCKS_BRONZE);

		copy(ForestryTags.Blocks.CHARCOAL, ForestryTags.Items.CHARCOAL);

		tag(ItemTags.SAPLINGS).add(ArboricultureItems.SAPLING.item());
		tag(ForestryTags.Items.BEE_COMBS).add(ApicultureItems.BEE_COMBS.itemArray());
		tag(ForestryTags.Items.PROPOLIS).add(ApicultureItems.PROPOLIS.itemArray());
		tag(ForestryTags.Items.DROP_HONEY).add(ApicultureItems.HONEY_DROPS.itemArray());

		addToTag(Tags.Items.ORES, ForestryTags.Items.ORES_COPPER, ForestryTags.Items.ORES_TIN, ForestryTags.Items.ORES_APATITE);
		copy(ForestryTags.Blocks.ORES_COPPER, ForestryTags.Items.ORES_COPPER);
		copy(ForestryTags.Blocks.ORES_TIN, ForestryTags.Items.ORES_TIN);
		copy(ForestryTags.Blocks.ORES_APATITE, ForestryTags.Items.ORES_APATITE);

		tag(ForestryTags.Items.STAMPS).add(MailItems.STAMPS.itemArray());

		tag(ForestryTags.Items.FRUITS).add(CoreItems.FRUITS.itemArray());
	}

	@SafeVarargs
	protected final void addToTag(ITag.INamedTag<Item> tag, ITag.INamedTag<Item>... providers) {
		TagsProvider.Builder<Item> builder = tag(tag);
		for (ITag.INamedTag<Item> provider : providers) {
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
