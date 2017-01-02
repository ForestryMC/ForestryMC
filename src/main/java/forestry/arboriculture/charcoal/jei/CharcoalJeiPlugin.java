package forestry.arboriculture.charcoal.jei;

import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.PluginArboriculture;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class CharcoalJeiPlugin extends BlankModPlugin {

	public static final String RECIPE_UID = "forestry.charcoal.pile";
	
	@Override
	public void register(IModRegistry registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper(); 
		registry.addRecipeCategories(new CharcoalPileWallCategory(guiHelper));
		registry.addRecipeHandlers(new CharcoalPileWallHandler(guiHelper));
		registry.addRecipes(TreeManager.pileWalls);
		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginArboriculture.getBlocks().woodPile), RECIPE_UID);
	}
	
}
