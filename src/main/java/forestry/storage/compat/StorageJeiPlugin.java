//package forestry.storage.compat;
//
//import forestry.core.utils.JeiUtil;
//import forestry.modules.ForestryModuleUids;
//import forestry.modules.ModuleHelper;
//import forestry.storage.ModuleBackpacks;
//import forestry.storage.items.ItemRegistryBackpacks;
//
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.IModRegistry;
//import mezz.jei.api.JEIPlugin;
//
//@JEIPlugin
//public class StorageJeiPlugin implements IModPlugin {
//	@Override
//	public void register(IModRegistry registry) {
//		if (!ModuleHelper.isEnabled(ForestryModuleUids.BACKPACKS)) {
//			return;
//		}
////TODO JEI
//		ItemRegistryBackpacks items = ModuleBackpacks.getItems();
//
//		JeiUtil.addDescription(registry, "miner_bag",
//			items.minerBackpack,
//			items.minerBackpackT2
//		);
//		JeiUtil.addDescription(registry, "digger_bag",
//			items.diggerBackpack,
//			items.diggerBackpackT2
//		);
//		JeiUtil.addDescription(registry, "forester_bag",
//			items.foresterBackpack,
//			items.foresterBackpackT2
//		);
//		JeiUtil.addDescription(registry, "hunter_bag",
//			items.hunterBackpack,
//			items.hunterBackpackT2
//		);
//		JeiUtil.addDescription(registry, "adventurer_bag",
//			items.adventurerBackpack,
//			items.adventurerBackpackT2
//		);
//		JeiUtil.addDescription(registry, "builder_bag",
//			items.builderBackpack,
//			items.builderBackpackT2
//		);
//		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
//			JeiUtil.addDescription(registry, items.apiaristBackpack);
//		}
//		if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
//			JeiUtil.addDescription(registry, items.lepidopteristBackpack);
//		}
//	}
//}
