package forestry.core.data;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.Tags;

import forestry.apiculture.features.ApicultureItems;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.ModuleCore;
import forestry.core.items.ItemRegistryCore;
import forestry.modules.features.FeatureItem;

public final class ForestryItemTagsProvider extends ItemTagsProvider {
	@Nullable
	private Set<ResourceLocation> filter = null;

	public ForestryItemTagsProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void registerTags() {
		super.registerTags();
		filter = this.tagToBuilder.keySet().stream().map(Tag::getId).collect(Collectors.toSet());
		copy(ForestryTags.Blocks.CHARCOAL, ForestryTags.Items.CHARCOAL);
		getBuilder(ItemTags.SAPLINGS).add(ModuleArboriculture.getItems().sapling);
		getBuilder(ForestryTags.Items.BEE_COMBS).add(ApicultureItems.BEE_COMBS.getFeatures().stream().map(FeatureItem::getItem).toArray(Item[]::new));
		getBuilder(ForestryTags.Items.PROPOLIS).add(ApicultureItems.PROPOLIS.getFeatures().stream().map(FeatureItem::getItem).toArray(Item[]::new));
		getBuilder(ForestryTags.Items.ASH).add(ModuleCore.getItems().ash);
		getBuilder(ForestryTags.Items.DROP_HONEY).add(ApicultureItems.HONEY_DROPS.getFeatures().stream().map(FeatureItem::getItem).toArray(Item[]::new));

		ItemRegistryCore coreItems = ModuleCore.getItems();
		getBuilder(Tags.Items.INGOTS).add(ForestryTags.Items.INGOT_BRONZE, ForestryTags.Items.INGOT_COPPER, ForestryTags.Items.INGOT_TIN);
		getBuilder(ForestryTags.Items.INGOT_BRONZE).add(coreItems.ingotBronze.getItem());
		getBuilder(ForestryTags.Items.INGOT_COPPER).add(coreItems.ingotCopper.getItem());
		getBuilder(ForestryTags.Items.INGOT_TIN).add(coreItems.ingotTin.getItem());
		getBuilder(ForestryTags.Items.GEM_APATITE).add(coreItems.apatite);

		copy(ForestryTags.Blocks.ORE_COPPER, ForestryTags.Items.ORE_COPPER);
		copy(ForestryTags.Blocks.ORE_TIN, ForestryTags.Items.ORE_TIN);
		copy(ForestryTags.Blocks.ORE_APATITE, ForestryTags.Items.ORE_APATITE);

		copy(ForestryTags.Blocks.STORAGE_BLOCK_COPPER, ForestryTags.Items.STORAGE_BLOCK_COPPER);
		copy(ForestryTags.Blocks.STORAGE_BLOCK_TIN, ForestryTags.Items.STORAGE_BLOCK_TIN);
		copy(ForestryTags.Blocks.STORAGE_BLOCK_BRONZE, ForestryTags.Items.STORAGE_BLOCK_BRONZE);
		copy(ForestryTags.Blocks.STORAGE_BLOCK_APATITE, ForestryTags.Items.STORAGE_BLOCK_APATITE);

		getBuilder(ForestryTags.Items.GEARS).add(ForestryTags.Items.GEAR_BRONZE, ForestryTags.Items.GEAR_COPPER, ForestryTags.Items.GEAR_TIN);
		getBuilder(ForestryTags.Items.GEAR_BRONZE).add(coreItems.gearBronze.getItem());
		getBuilder(ForestryTags.Items.GEAR_COPPER).add(coreItems.gearCopper.getItem());
		getBuilder(ForestryTags.Items.GEAR_TIN).add(coreItems.gearTin.getItem());
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
