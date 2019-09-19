package forestry.mail.features;

import forestry.core.config.Constants;
import forestry.mail.items.EnumStampDefinition;
import forestry.mail.items.ItemCatalogue;
import forestry.mail.items.ItemLetter;
import forestry.mail.items.ItemStamp;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureItemGroup;
import forestry.modules.features.FeatureItemTable;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class MailItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.MAIL);

	public static final FeatureItemGroup<ItemStamp, EnumStampDefinition> STAMPS = REGISTRY.itemGroup(ItemStamp::new, "stamp", EnumStampDefinition.VALUES);
	public static final FeatureItemTable<ItemLetter, ItemLetter.Size, ItemLetter.State> LETTERS = REGISTRY.itemTable(ItemLetter::new, ItemLetter.Size.values(), ItemLetter.State.values(), "letter");
	public static final FeatureItem<ItemCatalogue> CATALOGUE = REGISTRY.item(ItemCatalogue::new, "catalogue");

	private MailItems() {
	}
}
