package forestry.arboriculture.charcoal.jei;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import forestry.arboriculture.ModuleCharcoal;
import forestry.arboriculture.charcoal.CharcoalPileWall;
import forestry.core.config.Constants;
import forestry.modules.ForestryModuleUids;

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
		if (!ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CHARCOAL))) {
			return;
		}

		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new CharcoalPileWallCategory(guiHelper));
	}

	@Override
	public void register(IModRegistry registry) {
		ICharcoalManager charcoalManager = TreeManager.charcoalManager;
		if (!ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CHARCOAL)) || charcoalManager == null) {
			return;
		}

		registry.handleRecipes(CharcoalPileWall.class, CharcoalPileWallWrapper::new, RECIPE_UID);
		registry.addRecipes(charcoalManager.getWalls(), RECIPE_UID);
		registry.addRecipeCatalyst(new ItemStack(ModuleCharcoal.getBlocks().woodPile), RECIPE_UID);
	}

}
