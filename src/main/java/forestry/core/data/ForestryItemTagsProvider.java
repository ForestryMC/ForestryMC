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
	protected void registerTags() {
		super.registerTags();
		tagToBuilder.remove(ItemTags.SAPLINGS.getName());
		filter = this.tagToBuilder.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
		addToTag(ForestryTags.Items.GEARS, ForestryTags.Items.GEARS_BRONZE, ForestryTags.Items.GEARS_COPPER, ForestryTags.Items.GEARS_TIN);
		func_240522_a_(ForestryTags.Items.GEARS_BRONZE).func_240534_a_(CoreItems.GEAR_BRONZE.item());
		func_240522_a_(ForestryTags.Items.GEARS_TIN).func_240534_a_(CoreItems.GEAR_TIN.item());
		func_240522_a_(ForestryTags.Items.GEARS_COPPER).func_240534_a_(CoreItems.GEAR_COPPER.item());
		func_240522_a_(ForestryTags.Items.GEARS_STONE);

		addToTag(Tags.Items.INGOTS, ForestryTags.Items.INGOTS_BRONZE, ForestryTags.Items.INGOTS_COPPER, ForestryTags.Items.INGOTS_TIN);
		func_240522_a_(ForestryTags.Items.INGOTS_BRONZE).func_240534_a_(CoreItems.INGOT_BRONZE.item());
		func_240522_a_(ForestryTags.Items.INGOTS_TIN).func_240534_a_(CoreItems.INGOT_TIN.item());
		func_240522_a_(ForestryTags.Items.INGOTS_COPPER).func_240534_a_(CoreItems.INGOT_COPPER.item());

		func_240522_a_(ForestryTags.Items.DUSTS_ASH).func_240534_a_(CoreItems.ASH.item());
		func_240522_a_(ForestryTags.Items.GEMS_APATITE).func_240534_a_(CoreItems.APATITE.item());

		addToTag(Tags.Items.STORAGE_BLOCKS, ForestryTags.Items.STORAGE_BLOCKS_APATITE, ForestryTags.Items.STORAGE_BLOCKS_BRONZE, ForestryTags.Items.STORAGE_BLOCKS_COPPER, ForestryTags.Items.STORAGE_BLOCKS_TIN);
		func_240521_a_(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE, ForestryTags.Items.STORAGE_BLOCKS_APATITE);
		func_240521_a_(ForestryTags.Blocks.STORAGE_BLOCKS_TIN, ForestryTags.Items.STORAGE_BLOCKS_TIN);
		func_240521_a_(ForestryTags.Blocks.STORAGE_BLOCKS_COPPER, ForestryTags.Items.STORAGE_BLOCKS_COPPER);
		func_240521_a_(ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE, ForestryTags.Items.STORAGE_BLOCKS_BRONZE);

		func_240521_a_(ForestryTags.Blocks.CHARCOAL, ForestryTags.Items.CHARCOAL);

		func_240522_a_(ItemTags.SAPLINGS).func_240534_a_(ArboricultureItems.SAPLING.item());
		func_240522_a_(ForestryTags.Items.BEE_COMBS).func_240534_a_(ApicultureItems.BEE_COMBS.itemArray());
		func_240522_a_(ForestryTags.Items.PROPOLIS).func_240534_a_(ApicultureItems.PROPOLIS.itemArray());
		func_240522_a_(ForestryTags.Items.DROP_HONEY).func_240534_a_(ApicultureItems.HONEY_DROPS.itemArray());

		addToTag(Tags.Items.ORES, ForestryTags.Items.ORES_COPPER, ForestryTags.Items.ORES_TIN, ForestryTags.Items.ORES_APATITE);
		func_240521_a_(ForestryTags.Blocks.ORES_COPPER, ForestryTags.Items.ORES_COPPER);
		func_240521_a_(ForestryTags.Blocks.ORES_TIN, ForestryTags.Items.ORES_TIN);
		func_240521_a_(ForestryTags.Blocks.ORES_APATITE, ForestryTags.Items.ORES_APATITE);

		func_240522_a_(ForestryTags.Items.STAMPS).func_240534_a_(MailItems.STAMPS.itemArray());

		func_240522_a_(ForestryTags.Items.FRUITS).func_240534_a_(CoreItems.FRUITS.itemArray());
	}

	@SafeVarargs
	protected final void addToTag(ITag.INamedTag<Item> tag, ITag.INamedTag<Item>... providers) {
		TagsProvider.Builder<Item> builder = func_240522_a_(tag);
		for (ITag.INamedTag<Item> provider : providers) {
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
		return "Forestry Item Tags";
	}
}
