package forestry.plugins;

import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.core.ModuleCore;
import forestry.core.blocks.BlockRegistryCore;
import forestry.core.items.ItemRegistryCore;
import forestry.core.utils.JeiUtil;
import forestry.modules.ModuleManager;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;

@JEIPlugin
@SuppressWarnings("unused")
public class ForestryJeiPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IIngredientBlacklist ingredientBlacklist = jeiHelpers.getIngredientBlacklist();
		List<ItemStack> hiddenItems = ModuleManager.getInternalHandler().getHiddenItems();
		for (ItemStack hiddenItem : hiddenItems) {
			ingredientBlacklist.addIngredientToBlacklist(hiddenItem);
		}

		ItemRegistryCore items = ModuleCore.getItems();

		JeiUtil.addDescription(registry,
			items.solderingIron,
			items.circuitboards,
			items.tubes,
			items.pipette,
			items.kitPickaxe,
			items.kitShovel,
			items.fertilizerCompound
		);

		BlockRegistryCore blocks = ModuleCore.getBlocks();

		JeiUtil.addDescription(registry,
			blocks.analyzer,
			blocks.bogEarth,
			blocks.escritoire,
			blocks.humus
		);
	}
}
