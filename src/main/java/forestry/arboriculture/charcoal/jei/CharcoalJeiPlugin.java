package forestry.arboriculture.charcoal.jei;

import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.ModuleCharcoal;
import forestry.arboriculture.charcoal.CharcoalPileWall;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

@JEIPlugin
public class CharcoalJeiPlugin implements IModPlugin {

	public static final String RECIPE_UID = "forestry.charcoal.pile";

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new CharcoalPileWallCategory(guiHelper));
	}

	@Override
	public void register(IModRegistry registry) {
		registry.handleRecipes(CharcoalPileWall.class, CharcoalPileWallWrapper::new, RECIPE_UID);
		registry.addRecipes(TreeManager.pileWalls, RECIPE_UID);
		registry.addRecipeCatalyst(new ItemStack(ModuleCharcoal.getBlocks().woodPile), RECIPE_UID);
	}

}
