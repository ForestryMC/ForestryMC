package forestry.core.data;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Set;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.util.ResourceLocation;

public final class ForestryItemTagsProvider extends ItemTagsProvider {
	@Nullable
	private Set<ResourceLocation> filter = null;

	public ForestryItemTagsProvider(DataGenerator generator) {
		super(generator, null);
	}

	@Override
	protected void registerTags() {
		super.registerTags();
		/*filter = this.tagToBuilder.keySet().stream().map(ITag::getId).collect(Collectors.toSet());
		getBuilder(ForestryTags.Items.GEARS).add(ForestryTags.Items.GEARS_BRONZE, ForestryTags.Items.GEARS_COPPER, ForestryTags.Items.GEARS_TIN);
		getBuilder(ForestryTags.Items.GEARS_BRONZE).add(CoreItems.GEAR_BRONZE.item());
		getBuilder(ForestryTags.Items.GEARS_TIN).add(CoreItems.GEAR_TIN.item());
		getBuilder(ForestryTags.Items.GEARS_COPPER).add(CoreItems.GEAR_COPPER.item());

		getBuilder(Tags.Items.INGOTS).add(ForestryTags.Items.INGOTS_BRONZE, ForestryTags.Items.INGOTS_COPPER, ForestryTags.Items.INGOTS_TIN);
		getBuilder(ForestryTags.Items.INGOTS_BRONZE).add(CoreItems.INGOT_BRONZE.item());
		getBuilder(ForestryTags.Items.INGOTS_TIN).add(CoreItems.INGOT_TIN.item());
		getBuilder(ForestryTags.Items.INGOTS_COPPER).add(CoreItems.INGOT_COPPER.item());

		getBuilder(ForestryTags.Items.DUSTS_ASH).add(CoreItems.ASH.item());
		getBuilder(ForestryTags.Items.GEMS_APATITE).add(CoreItems.APATITE.item());

		getBuilder(Tags.Items.STORAGE_BLOCKS).add(ForestryTags.Items.STORAGE_BLOCKS_APATITE, ForestryTags.Items.STORAGE_BLOCKS_BRONZE, ForestryTags.Items.STORAGE_BLOCKS_COPPER, ForestryTags.Items.STORAGE_BLOCKS_TIN);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_APATITE, ForestryTags.Items.STORAGE_BLOCKS_APATITE);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_TIN, ForestryTags.Items.STORAGE_BLOCKS_TIN);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_COPPER, ForestryTags.Items.STORAGE_BLOCKS_COPPER);
		copy(ForestryTags.Blocks.STORAGE_BLOCKS_BRONZE, ForestryTags.Items.STORAGE_BLOCKS_BRONZE);

		copy(ForestryTags.Blocks.CHARCOAL, ForestryTags.Items.CHARCOAL);

		getBuilder(ItemTags.SAPLINGS).add(ArboricultureItems.SAPLING.item());
		getBuilder(ForestryTags.Items.BEE_COMBS).add(ApicultureItems.BEE_COMBS.itemArray());
		getBuilder(ForestryTags.Items.PROPOLIS).add(ApicultureItems.PROPOLIS.itemArray());
		getBuilder(ForestryTags.Items.DROP_HONEY).add(ApicultureItems.HONEY_DROPS.itemArray());

		getBuilder(Tags.Items.ORES).add(ForestryTags.Items.ORES_COPPER, ForestryTags.Items.ORES_TIN, ForestryTags.Items.ORES_APATITE);
		copy(ForestryTags.Blocks.ORES_COPPER, ForestryTags.Items.ORES_COPPER);
		copy(ForestryTags.Blocks.ORES_TIN, ForestryTags.Items.ORES_TIN);
		copy(ForestryTags.Blocks.ORES_APATITE, ForestryTags.Items.ORES_APATITE);

		getBuilder(ForestryTags.Items.STAMPS).add(MailItems.STAMPS.itemArray());

		getBuilder(ForestryTags.Items.FRUITS).add(CoreItems.FRUITS.itemArray());*/
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
