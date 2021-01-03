package forestry.factory.recipes.jei;

import forestry.core.config.Constants;
import forestry.core.gui.ContainerForestry;
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
import forestry.factory.recipes.jei.bottler.BottlerRecipeMaker;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeCategory;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeMaker;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeTransferHandler;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeCategory;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeMaker;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeCategory;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeMaker;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeTransferHandler;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeCategory;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeMaker;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeCategory;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeMaker;
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeCategory;
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeMaker;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeCategory;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeMaker;
import forestry.factory.recipes.jei.still.StillRecipeCategory;
import forestry.factory.recipes.jei.still.StillRecipeMaker;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
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

    static class ForestryAdvancedGuiHandler<T extends Container> implements IGuiContainerHandler<GuiForestry<?>> {
        @Override
        public List<Rectangle2d> getGuiExtraAreas(GuiForestry guiContainer) {
            return guiContainer.getExtraGuiAreas();
        }

        @Nullable
        @Override
        public Object getIngredientUnderMouse(GuiForestry guiContainer, double mouseX, double mouseY) {
            return guiContainer.getFluidStackAtPosition(mouseX, mouseY);
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if (!ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
            return;
        }

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        List<IRecipeCategory> categories = new ArrayList<>();

        if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
            categories.add(new BottlerRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            categories.add(new CarpenterRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
            categories.add(new CentrifugeRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();
            categories.add(new FabricatorRecipeCategory(guiHelper, recipeManager));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
            categories.add(new FermenterRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
            categories.add(new MoistenerRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.RAINMAKER)) {
            categories.add(new RainmakerRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
            categories.add(new SqueezerRecipeCategory(guiHelper));
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
            categories.add(new StillRecipeCategory(guiHelper));
        }

        registry.addRecipeCategories(categories.toArray(new IRecipeCategory[0]));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            registry.addRecipeTransferHandler(
                    new CarpenterRecipeTransferHandler(),
                    ForestryRecipeCategoryUid.CARPENTER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            registry.addRecipeTransferHandler(
                    new FabricatorRecipeTransferHandler(),
                    ForestryRecipeCategoryUid.FABRICATOR
            );
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).block()),
                    ForestryRecipeCategoryUid.BOTTLER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CARPENTER).block()),
                    ForestryRecipeCategoryUid.CARPENTER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).block()),
                    ForestryRecipeCategoryUid.CENTRIFUGE
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR).block()),
                    ForestryRecipeCategoryUid.FABRICATOR
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER).block()),
                    ForestryRecipeCategoryUid.FERMENTER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.MOISTENER).block()),
                    ForestryRecipeCategoryUid.MOISTENER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.RAINMAKER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).block()),
                    ForestryRecipeCategoryUid.RAINMAKER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.SQUEEZER).block()),
                    ForestryRecipeCategoryUid.SQUEEZER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
            registry.addRecipeCatalyst(
                    new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).block()),
                    ForestryRecipeCategoryUid.STILL
            );
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registry) {
        registry.addGenericGuiContainerHandler(GuiForestry.class, new ForestryAdvancedGuiHandler<ContainerForestry>());

        if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
            registry.addRecipeClickArea(
                    GuiBottler.class,
                    107,
                    33,
                    26,
                    22,
                    ForestryRecipeCategoryUid.BOTTLER
            );
            registry.addRecipeClickArea(
                    GuiBottler.class,
                    45,
                    33,
                    26,
                    22,
                    ForestryRecipeCategoryUid.BOTTLER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            registry.addRecipeClickArea(
                    GuiCarpenter.class,
                    98,
                    48,
                    21,
                    26,
                    ForestryRecipeCategoryUid.CARPENTER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
            registry.addRecipeClickArea(
                    GuiCentrifuge.class,
                    38,
                    22,
                    38,
                    14,
                    ForestryRecipeCategoryUid.CENTRIFUGE
            );
            registry.addRecipeClickArea(
                    GuiCentrifuge.class,
                    38,
                    54,
                    38,
                    14,
                    ForestryRecipeCategoryUid.CENTRIFUGE
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            registry.addRecipeClickArea(
                    GuiFabricator.class,
                    121,
                    53,
                    18,
                    18,
                    ForestryRecipeCategoryUid.FABRICATOR
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
            registry.addRecipeClickArea(
                    GuiFermenter.class,
                    72,
                    40,
                    32,
                    18,
                    ForestryRecipeCategoryUid.FERMENTER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
            registry.addRecipeClickArea(
                    GuiMoistener.class,
                    123,
                    35,
                    19,
                    21,
                    ForestryRecipeCategoryUid.MOISTENER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
            registry.addRecipeClickArea(
                    GuiSqueezer.class,
                    76,
                    41,
                    43,
                    16,
                    ForestryRecipeCategoryUid.SQUEEZER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
            registry.addRecipeClickArea(
                    GuiStill.class,
                    73,
                    17,
                    33,
                    57,
                    ForestryRecipeCategoryUid.STILL
            );
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        if (!ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
            return;
        }

        RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();

        if (ModuleFactory.machineEnabled(MachineUIDs.BOTTLER)) {
            registry.addRecipes(
                    BottlerRecipeMaker.getBottlerRecipes(registry.getIngredientManager()),
                    ForestryRecipeCategoryUid.BOTTLER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CARPENTER)) {
            registry.addRecipes(
                    CarpenterRecipeMaker.getCarpenterRecipes(recipeManager),
                    ForestryRecipeCategoryUid.CARPENTER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.CENTRIFUGE)) {
            registry.addRecipes(
                    CentrifugeRecipeMaker.getCentrifugeRecipe(recipeManager),
                    ForestryRecipeCategoryUid.CENTRIFUGE
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FABRICATOR)) {
            registry.addRecipes(
                    FabricatorRecipeMaker.getFabricatorRecipes(recipeManager),
                    ForestryRecipeCategoryUid.FABRICATOR
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.FERMENTER)) {
            registry.addRecipes(
                    FermenterRecipeMaker.getFermenterRecipes(recipeManager, registry.getJeiHelpers().getStackHelper()),
                    ForestryRecipeCategoryUid.FERMENTER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.MOISTENER)) {
            registry.addRecipes(
                    MoistenerRecipeMaker.getMoistenerRecipes(recipeManager),
                    ForestryRecipeCategoryUid.MOISTENER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.RAINMAKER)) {
            registry.addRecipes(
                    RainmakerRecipeMaker.getRecipes(),
                    ForestryRecipeCategoryUid.RAINMAKER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.SQUEEZER)) {
            registry.addRecipes(
                    SqueezerRecipeMaker.getSqueezerRecipes(recipeManager),
                    ForestryRecipeCategoryUid.SQUEEZER
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.STILL)) {
            registry.addRecipes(
                    StillRecipeMaker.getStillRecipes(recipeManager),
                    ForestryRecipeCategoryUid.STILL
            );
        }

        if (ModuleFactory.machineEnabled(MachineUIDs.RAINTANK)) {
            JeiUtil.addDescription(registry, FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).getBlock());
        }
    }
}
