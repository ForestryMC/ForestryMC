package forestry.storage.compat;

import forestry.core.utils.JeiUtil;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.storage.ModuleBackpacks;
import forestry.storage.items.ItemRegistryBackpacks;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class StorageJeiPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.BACKPACKS)) {
			return;
		}

		ItemRegistryBackpacks items = ModuleBackpacks.getItems();

		JeiUtil.addDescription(registry, "minerBag",
				items.minerBackpack,
				items.minerBackpackT2
		);
		JeiUtil.addDescription(registry, "diggerBag",
				items.diggerBackpack,
				items.diggerBackpackT2
		);
		JeiUtil.addDescription(registry, "foresterBag",
				items.foresterBackpack,
				items.foresterBackpackT2
		);
		JeiUtil.addDescription(registry, "hunter",
				items.hunterBackpack,
				items.hunterBackpackT2
		);
		JeiUtil.addDescription(registry, "adventurerBag",
				items.adventurerBackpack,
				items.adventurerBackpackT2
		);
		JeiUtil.addDescription(registry, "builderBag",
				items.builderBackpack,
				items.builderBackpackT2
		);
		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			JeiUtil.addDescription(registry, items.apiaristBackpack);
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
			JeiUtil.addDescription(registry, items.lepidopteristBackpack);
		}
	}
}
