package forestry.arboriculture.charcoal.jei;

import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.config.Constants;
import forestry.modules.ForestryModuleUids;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class CharcoalJeiPlugin implements IModPlugin {

	public static final ResourceLocation RECIPE_UID = new ResourceLocation("forestry:charcoal.pile");

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CHARCOAL);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new CharcoalPileWallCategory(guiHelper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		ICharcoalManager charcoalManager = TreeManager.charcoalManager;
		if (charcoalManager == null) {
			return;
		}

		registration.addRecipes(charcoalManager.getWalls(), RECIPE_UID);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(CharcoalBlocks.WOOD_PILE.stack(), RECIPE_UID);
	}
}
