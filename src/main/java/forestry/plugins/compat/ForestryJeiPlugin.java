package forestry.plugins.compat;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.plugins.PluginManager;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

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
	}
}
