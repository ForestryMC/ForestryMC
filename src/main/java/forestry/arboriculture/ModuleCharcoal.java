package forestry.arboriculture;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;

import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ItemGroups;
import forestry.api.modules.ForestryModule;
import forestry.arboriculture.charcoal.CharcoalManager;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.ItemGroupForestry;
import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CHARCOAL, name = "Charcoal", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.charcoal.description")
public class ModuleCharcoal extends BlankForestryModule {

	@Override
	public void setupAPI() {
		TreeManager.charcoalManager = new CharcoalManager();
	}

	@Override
	public void registerFeatures() {
		CharcoalBlocks.LOAM.getClass();
	}

	@Override
	public void postInit() {
		ICharcoalManager manager = TreeManager.charcoalManager;
		if (manager != null) {
			manager.registerWall(Blocks.CLAY, 3);
			manager.registerWall(CharcoalBlocks.LOAM.block(), 4);
			manager.registerWall(Blocks.END_STONE, 6);
			manager.registerWall(Blocks.END_STONE_BRICKS, 6);
			manager.registerWall(Blocks.DIRT, 2);
			manager.registerWall(Blocks.GRAVEL, 1);
			manager.registerWall(Blocks.NETHERRACK, 3);
			manager.registerWall(CoreBlocks.ASH_BRICK.block(), 5);
		}
	}

	public static ItemGroup getTag() {
		return ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE) ? ItemGroups.tabArboriculture : ItemGroupForestry.tabForestry;
	}
}
