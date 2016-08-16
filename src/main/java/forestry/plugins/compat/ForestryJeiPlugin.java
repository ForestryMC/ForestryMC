package forestry.plugins.compat;

import javax.annotation.Nonnull;
import java.util.List;

import forestry.core.PluginCore;
import forestry.core.blocks.BlockRegistryCore;
import forestry.core.items.ItemRegistryCore;
import forestry.core.utils.JeiUtil;
import forestry.plugins.PluginManager;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class ForestryJeiPlugin extends BlankModPlugin {
	@Override
	public void register(@Nonnull IModRegistry registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IItemBlacklist itemBlacklist = jeiHelpers.getItemBlacklist();
		List<ItemStack> hiddenItems = PluginManager.getHiddenItems();
		for (ItemStack hiddenItem : hiddenItems) {
			itemBlacklist.addItemToBlacklist(hiddenItem);
		}

		ItemRegistryCore items = PluginCore.items;

		JeiUtil.addDescription(registry,
				items.solderingIron,
				items.circuitboards,
				items.tubes,
				items.pipette,
				items.kitPickaxe,
				items.kitShovel,
				items.fertilizerCompound
		);

		BlockRegistryCore blocks = PluginCore.blocks;
		JeiUtil.addDescription(registry,
				blocks.analyzer,
				blocks.bogEarth,
				blocks.escritoire,
				blocks.humus
		);
	}
}
