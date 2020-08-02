package forestry.factory.recipes.jei;


import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.modules.ForestryModuleUids;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
//import forestry.core.utils.JeiUtil;
//import forestry.factory.recipes.jei.bottler.BottlerRecipeCategory;
//import forestry.factory.recipes.jei.bottler.BottlerRecipeMaker;
//import forestry.factory.recipes.jei.carpenter.CarpenterRecipeCategory;
//import forestry.factory.recipes.jei.carpenter.CarpenterRecipeMaker;
//import forestry.factory.recipes.jei.carpenter.CarpenterRecipeTransferHandler;
//import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeCategory;
//import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeMaker;
//import forestry.factory.recipes.jei.fabricator.FabricatorRecipeCategory;
//import forestry.factory.recipes.jei.fabricator.FabricatorRecipeMaker;
//import forestry.factory.recipes.jei.fabricator.FabricatorRecipeTransferHandler;
//import forestry.factory.recipes.jei.fermenter.FermenterRecipeCategory;
//import forestry.factory.recipes.jei.fermenter.FermenterRecipeMaker;
//import forestry.factory.recipes.jei.moistener.MoistenerRecipeCategory;
//import forestry.factory.recipes.jei.moistener.MoistenerRecipeMaker;
//import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeCategory;
//import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeMaker;
//import forestry.factory.recipes.jei.squeezer.SqueezerRecipeCategory;
//import forestry.factory.recipes.jei.squeezer.SqueezerRecipeMaker;
//import forestry.factory.recipes.jei.still.StillRecipeCategory;
//import forestry.factory.recipes.jei.still.StillRecipeMaker;


//import mezz.jei.api.IGuiHelper;
//import mezz.jei.api.IJeiHelpers;
//import mezz.jei.api.IModRegistry;
//import mezz.jei.api.recipe.IRecipeCategory;
//import mezz.jei.api.recipe.IRecipeCategoryRegistration;
//import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class FactoryJeiPlugin implements IModPlugin {

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(GuiForestry.class, new ForestryAdvancedGuiHandler());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FACTORY);
    }

    private static class ForestryAdvancedGuiHandler implements IGuiContainerHandler<GuiForestry> {
        @Override
        public List<Rectangle2d> getGuiExtraAreas(GuiForestry guiContainer) {
            return ((GuiForestry<?>) guiContainer).getExtraGuiAreas();
        }

        @Nullable
        @Override
        public Object getIngredientUnderMouse(GuiForestry guiContainer, double mouseX, double mouseY) {
            return guiContainer.getFluidStackAtPosition(mouseX, mouseY);
        }
    }

    //	@Nullable
    //	public static IJeiHelpers jeiHelpers;
    //
    //	@Override
    //	public void registerCategories(IRecipeCategoryRegistration registry) {
    //		if (!ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
    //			return;
    //		}
    //
    //		jeiHelpers = registry.getJeiHelpers();
    //		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
    //
    //		List<IRecipeCategory> categories = new ArrayList<>();
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
    //			categories.add(new BottlerRecipeCategory(guiHelper));
    //		}
    //		if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
    //			categories.add(new CarpenterRecipeCategory(guiHelper));
    //		}
    //		if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
    //			categories.add(new CentrifugeRecipeCategory(guiHelper));
    //		}
    //		if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
    //			categories.add(new FabricatorRecipeCategory(guiHelper));
    //		}
    //		if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
    //			categories.add(new FermenterRecipeCategory(guiHelper));
    //		}
    //		if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
    //			categories.add(new MoistenerRecipeCategory(guiHelper));
    //		}
    //		if (ModuleFactory.machineEnabled(MachineUIDs.RAINMAKER)) {
    //			categories.add(new RainmakerRecipeCategory(guiHelper));
    //		}
    //		if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
    //			categories.add(new SqueezerRecipeCategory(guiHelper));
    //		}
    //		if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
    //			categories.add(new StillRecipeCategory(guiHelper));
    //		}
    //
    //		if (!categories.isEmpty()) {
    //			registry.addRecipeCategories(
    //				categories.toArray(new IRecipeCategory[0])
    //			);
    //		}
    //	}
    //
    //	@Override
    //	public void register(IModRegistry registry) {
    //		if (!ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
    //			return;
    //		}
    //		registry.addAdvancedGuiHandlers(new ForestryAdvancedGuiHandler());
    //
    //
    //		jeiHelpers = registry.getJeiHelpers();
    //
    //		BlockRegistryFactory blocks = ModuleFactory.getBlocks();
    //		Preconditions.checkNotNull(blocks);
    //
    //		IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
    //			registry.addRecipes(BottlerRecipeMaker.getBottlerRecipes(registry.getIngredientRegistry()), ForestryRecipeCategoryUid.BOTTLER);
    //			registry.addRecipeClickArea(GuiBottler.class, 107, 33, 26, 22, ForestryRecipeCategoryUid.BOTTLER);
    //			registry.addRecipeClickArea(GuiBottler.class, 45, 33, 26, 22, ForestryRecipeCategoryUid.BOTTLER);
    //			registry.addRecipeCatalyst(new ItemStack(blocks.bottler), ForestryRecipeCategoryUid.BOTTLER);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.bottler));
    //		}
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
    //			registry.addRecipes(CarpenterRecipeMaker.getCarpenterRecipes(), ForestryRecipeCategoryUid.CARPENTER);
    //			registry.addRecipeClickArea(GuiCarpenter.class, 98, 48, 21, 26, ForestryRecipeCategoryUid.CARPENTER);
    //			registry.addRecipeCatalyst(new ItemStack(blocks.carpenter), ForestryRecipeCategoryUid.CARPENTER);
    //			transferRegistry.addRecipeTransferHandler(new CarpenterRecipeTransferHandler(), ForestryRecipeCategoryUid.CARPENTER);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.carpenter));
    //		}
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
    //			registry.addRecipes(CentrifugeRecipeMaker.getCentrifugeRecipe(), ForestryRecipeCategoryUid.CENTRIFUGE);
    //			registry.addRecipeClickArea(GuiCentrifuge.class, 38, 22, 38, 14, ForestryRecipeCategoryUid.CENTRIFUGE);
    //			registry.addRecipeClickArea(GuiCentrifuge.class, 38, 54, 38, 14, ForestryRecipeCategoryUid.CENTRIFUGE);
    //			registry.addRecipeCatalyst(new ItemStack(blocks.centrifuge), ForestryRecipeCategoryUid.CENTRIFUGE);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.centrifuge));
    //		}
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
    //			registry.addRecipes(FabricatorRecipeMaker.getFabricatorRecipes(), ForestryRecipeCategoryUid.FABRICATOR);
    //			registry.addRecipeClickArea(GuiFabricator.class, 121, 53, 18, 18, ForestryRecipeCategoryUid.FABRICATOR);
    //			registry.addRecipeCatalyst(new ItemStack(blocks.fabricator), ForestryRecipeCategoryUid.FABRICATOR);
    //			transferRegistry.addRecipeTransferHandler(new FabricatorRecipeTransferHandler(), ForestryRecipeCategoryUid.FABRICATOR);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.fabricator));
    //		}
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
    //			registry.addRecipes(FermenterRecipeMaker.getFermenterRecipes(jeiHelpers.getStackHelper()), ForestryRecipeCategoryUid.FERMENTER);
    //			registry.addRecipeClickArea(GuiFermenter.class, 72, 40, 32, 18, ForestryRecipeCategoryUid.FERMENTER);
    //			registry.addRecipeCatalyst(new ItemStack(blocks.fermenter), ForestryRecipeCategoryUid.FERMENTER);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.fermenter));
    //		}
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
    //			registry.addRecipes(MoistenerRecipeMaker.getMoistenerRecipes(), ForestryRecipeCategoryUid.MOISTENER);
    //			registry.addRecipeClickArea(GuiMoistener.class, 123, 35, 19, 21, ForestryRecipeCategoryUid.MOISTENER);
    //			registry.addRecipeCatalyst(new ItemStack(blocks.moistener), ForestryRecipeCategoryUid.MOISTENER);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.moistener));
    //		}
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.RAINMAKER)) {
    //			registry.addRecipes(RainmakerRecipeMaker.getRecipes(), ForestryRecipeCategoryUid.RAINMAKER);
    //			registry.addRecipeCatalyst(new ItemStack(blocks.rainmaker), ForestryRecipeCategoryUid.RAINMAKER);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.rainmaker));
    //		}
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
    //			registry.addRecipes(SqueezerRecipeMaker.getSqueezerRecipes(), ForestryRecipeCategoryUid.SQUEEZER);
    //			registry.addRecipes(SqueezerRecipeMaker.getSqueezerContainerRecipes(registry.getIngredientRegistry()), ForestryRecipeCategoryUid.SQUEEZER);
    //			registry.addRecipeClickArea(GuiSqueezer.class, 76, 41, 43, 16, ForestryRecipeCategoryUid.SQUEEZER);
    //			registry.addRecipeCatalyst(new ItemStack(blocks.squeezer), ForestryRecipeCategoryUid.SQUEEZER);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.squeezer));
    //		}
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
    //			registry.addRecipes(StillRecipeMaker.getStillRecipes(), ForestryRecipeCategoryUid.STILL);
    //			registry.addRecipeClickArea(GuiStill.class, 73, 17, 33, 57, ForestryRecipeCategoryUid.STILL);
    //			registry.addRecipeCatalyst(new ItemStack(blocks.still), ForestryRecipeCategoryUid.STILL);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.still));
    //		}
    //
    //		if (ModuleFactory.machineEnabled(MachineUIDs.RAINTANK)) {
    //			JeiUtil.addDescription(registry, blocks.raintank);
    //		} else {
    //			jeiHelpers.getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(blocks.raintank));
    //		}
    //
    //	}
    //
}
