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

import forestry.apiculture.features.ApicultureItems;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.ModuleCore;
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

		getBuilder(ForestryTags.Items.INGOT_BRONZE).add(ModuleCore.getItems().ingotBronze.getItem());
		getBuilder(ForestryTags.Items.INGOT_COPPER).add(ModuleCore.getItems().ingotCopper.getItem());
		getBuilder(ForestryTags.Items.INGOT_TIN).add(ModuleCore.getItems().ingotTin.getItem());


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
