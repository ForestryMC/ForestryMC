package forestry.arboriculture.charcoal.jei;

import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.config.Constants;
import forestry.modules.ForestryModuleUids;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class CharcoalJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Constants.MOD_ID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
//        registration.addRecipes(TreeManager.charcoalManager.getWalls(), getPluginUid());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (!ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CHARCOAL))) {
            return;
        }

        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CharcoalPileWallCategory(guiHelper));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
//        registration.getCraftingCategory().addCategoryExtension(CharcoalPileWall.class, CharcoalPileWallWrapper::new);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ICharcoalManager charcoalManager = TreeManager.charcoalManager;
        if (!ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CHARCOAL)) ||
            charcoalManager == null) {
            return;
        }

        registration.addRecipeCatalyst(new ItemStack(CharcoalBlocks.WOOD_PILE.getItem()), getPluginUid());
    }
}
