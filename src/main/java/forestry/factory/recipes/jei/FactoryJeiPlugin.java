package forestry.factory.recipes.jei;


import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.utils.JeiUtil;
import forestry.factory.MachineUIDs;
import forestry.factory.ModuleFactory;
import forestry.factory.blocks.BlockTypeFactoryPlain;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import forestry.factory.gui.*;
import forestry.factory.recipes.jei.bottler.BottlerRecipeCategory;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeCategory;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeMaker;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeTransferHandler;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeCategory;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeMaker;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeCategory;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeMaker;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeTransferHandler;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeCategory;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeCategory;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeMaker;
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeCategory;
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeMaker;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeCategory;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeMaker;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class FactoryJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FACTORY);
    }

    private static class ForestryAdvancedGuiHandler implements IGuiContainerHandler<GuiForestry<?>> {
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

    @Nullable
    public static IJeiHelpers jeiHelpers;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if (!ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
            return;
        }

        jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        List<IRecipeCategory> categories = new ArrayList<>();

        if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
            registry.addRecipeCategories(new BottlerRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            registry.addRecipeCategories(new CarpenterRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
            registry.addRecipeCategories(new CentrifugeRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            registry.addRecipeCategories(new FabricatorRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
            registry.addRecipeCategories(new FermenterRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
            registry.addRecipeCategories(new MoistenerRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.RAINMAKER)) {
            registry.addRecipeCategories(new RainmakerRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
            registry.addRecipeCategories(new SqueezerRecipeCategory(guiHelper));
        }

//        if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
//            registry.addRecipeCategories(new StillRecipeCategory(guiHelper));
//        }
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            registry.addRecipeTransferHandler(
                    new CarpenterRecipeTransferHandler(),
                    new ResourceLocation(ForestryRecipeCategoryUid.CARPENTER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            registry.addRecipeTransferHandler(
                    new FabricatorRecipeTransferHandler(),
                    new ResourceLocation(ForestryRecipeCategoryUid.FABRICATOR)
            );
        }
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).block()),
                    new ResourceLocation(ForestryRecipeCategoryUid.BOTTLER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CARPENTER).block()),
                    new ResourceLocation(ForestryRecipeCategoryUid.CARPENTER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).block()),
                    new ResourceLocation(ForestryRecipeCategoryUid.CENTRIFUGE)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR).block()),
                    new ResourceLocation(ForestryRecipeCategoryUid.FABRICATOR)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER).block()),
                    new ResourceLocation(ForestryRecipeCategoryUid.FERMENTER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.MOISTENER).block()),
                    new ResourceLocation(ForestryRecipeCategoryUid.MOISTENER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.RAINMAKER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).block()),
                    new ResourceLocation(ForestryRecipeCategoryUid.RAINMAKER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.SQUEEZER).block()),
                    new ResourceLocation(ForestryRecipeCategoryUid.SQUEEZER)
            );
        }

//        if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
//            registry.addRecipeCatalyst(
//                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).block()),
//                    new ResourceLocation(ForestryRecipeCategoryUid.STILL)
//            );
//        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registry) {
//        registry.addGuiContainerHandler(GuiForestry.class, new ForestryAdvancedGuiHandler());

        if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
            registry.addRecipeClickArea(
                    GuiBottler.class,
                    107,
                    33,
                    26,
                    22,
                    new ResourceLocation(ForestryRecipeCategoryUid.BOTTLER)
            );
            registry.addRecipeClickArea(
                    GuiBottler.class,
                    45,
                    33,
                    26,
                    22,
                    new ResourceLocation(ForestryRecipeCategoryUid.BOTTLER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            registry.addRecipeClickArea(
                    GuiCarpenter.class,
                    98,
                    48,
                    21,
                    26,
                    new ResourceLocation(ForestryRecipeCategoryUid.CARPENTER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
            registry.addRecipeClickArea(
                    GuiCentrifuge.class,
                    38,
                    22,
                    38,
                    14,
                    new ResourceLocation(ForestryRecipeCategoryUid.CENTRIFUGE)
            );
            registry.addRecipeClickArea(
                    GuiCentrifuge.class,
                    38,
                    54,
                    38,
                    14,
                    new ResourceLocation(ForestryRecipeCategoryUid.CENTRIFUGE)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            registry.addRecipeClickArea(
                    GuiFabricator.class,
                    121,
                    53,
                    18,
                    18,
                    new ResourceLocation(ForestryRecipeCategoryUid.FABRICATOR)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
            registry.addRecipeClickArea(
                    GuiFermenter.class,
                    72,
                    40,
                    32,
                    18,
                    new ResourceLocation(ForestryRecipeCategoryUid.FERMENTER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
            registry.addRecipeClickArea(
                    GuiMoistener.class,
                    123,
                    35,
                    19,
                    21,
                    new ResourceLocation(ForestryRecipeCategoryUid.MOISTENER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
            registry.addRecipeClickArea(
                    GuiSqueezer.class,
                    76,
                    41,
                    43,
                    16,
                    new ResourceLocation(ForestryRecipeCategoryUid.SQUEEZER)
            );
        }

//        if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
//            registry.addRecipeClickArea(GuiStill.class,
//                    73,
//                    17,
//                    33,
//                    57,
//                    new ResourceLocation(ForestryRecipeCategoryUid.STILL));
//        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        if (!ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
            return;
        }

//        if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
//            registry.addRecipes(
//                    BottlerRecipeMaker.getBottlerRecipes(registry.getIngredientManager()),
//                    new ResourceLocation(ForestryRecipeCategoryUid.BOTTLER)
//            );
//        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            registry.addRecipes(
                    CarpenterRecipeMaker.getCarpenterRecipes(),
                    new ResourceLocation(ForestryRecipeCategoryUid.CARPENTER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
            registry.addRecipes(
                    CentrifugeRecipeMaker.getCentrifugeRecipe(),
                    new ResourceLocation(ForestryRecipeCategoryUid.CENTRIFUGE)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            registry.addRecipes(
                    FabricatorRecipeMaker.getFabricatorRecipes(),
                    new ResourceLocation(ForestryRecipeCategoryUid.FABRICATOR)
            );
        }

//        if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
//            registry.addRecipes(
//                    FermenterRecipeMaker.getFermenterRecipes(jeiHelpers.getStackHelper()),
//                    new ResourceLocation(ForestryRecipeCategoryUid.FERMENTER)
//            );
//        }

        if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
            registry.addRecipes(
                    MoistenerRecipeMaker.getMoistenerRecipes(),
                    new ResourceLocation(ForestryRecipeCategoryUid.MOISTENER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.RAINMAKER)) {
            registry.addRecipes(
                    RainmakerRecipeMaker.getRecipes(),
                    new ResourceLocation(ForestryRecipeCategoryUid.RAINMAKER)
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
            registry.addRecipes(
                    SqueezerRecipeMaker.getSqueezerRecipes(),
                    new ResourceLocation(ForestryRecipeCategoryUid.SQUEEZER)
            );
            registry.addRecipes(
                    SqueezerRecipeMaker.getSqueezerContainerRecipes(registry.getIngredientManager()),
                    new ResourceLocation(ForestryRecipeCategoryUid.SQUEEZER)
            );
        }

//        if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
//            registry.addRecipes(
//                    StillRecipeMaker.getStillRecipes(),
//                    new ResourceLocation(ForestryRecipeCategoryUid.STILL)
//            );
//        }

        if (ModuleFactory.machineEnabled(MachineUIDs.RAINTANK)) {
            JeiUtil.addDescription(registry, FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).getBlock());
        }
    }
}
