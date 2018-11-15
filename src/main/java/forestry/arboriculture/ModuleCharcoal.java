package forestry.arboriculture;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.TreeManager;
import forestry.api.modules.ForestryModule;
import forestry.arboriculture.blocks.BlockRegistryCharcoal;
import forestry.arboriculture.charcoal.CharcoalManager;
import forestry.core.ModuleCore;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CHARCOAL, name = "Charcoal", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.charcoal.description")
public class ModuleCharcoal extends BlankForestryModule {
	@Nullable
	private static BlockRegistryCharcoal blocks;

	public static BlockRegistryCharcoal getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	@Override
	public void setupAPI() {
		TreeManager.charcoalManager = new CharcoalManager();
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryCharcoal();
	}

	@Override
	public void postInit() {
		ICharcoalManager manager = TreeManager.charcoalManager;
		if (manager != null) {
			manager.registerWall(Blocks.CLAY, 3);
			manager.registerWall(getBlocks().loam, 4);
			manager.registerWall(Blocks.END_STONE, 6);
			manager.registerWall(Blocks.END_BRICKS, 6);
			manager.registerWall(Blocks.DIRT, 2);
			manager.registerWall(Blocks.GRAVEL, 1);
			manager.registerWall(Blocks.NETHERRACK, 3);
			manager.registerWall(ModuleCore.getBlocks().ashBrick, 5);
		}
	}
}
