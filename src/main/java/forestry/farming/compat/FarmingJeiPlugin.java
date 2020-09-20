package forestry.farming.compat;

import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class FarmingJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Constants.MOD_ID, "farming");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
            return;
        }

        registration.addRecipes(
                FarmingInfoRecipeMaker.getRecipes(),
                FarmingInfoRecipeCategory.UID
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.INTRICATE),
                FarmingInfoRecipeCategory.UID
        );
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (!ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
            return;
        }

        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new FarmingInfoRecipeCategory(guiHelper));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        if (!ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
            return;
        }

//        BlockRegistryFarming blocks = ModuleFarming.getBlocks();
//        Item farmBlock = Item.getItemFromBlock(blocks.farm);
//        registration.registerSubtypeInterpreter(farmBlock, itemStack -> {
//            CompoundNBT nbt = itemStack.getTag();
//            EnumFarmBlockTexture texture = EnumFarmBlockTexture.getFromCompound(nbt);
//            return itemStack.getItemDamage() + "." + texture.getUid();
//        });
    }
}
