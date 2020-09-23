package forestry.factory.recipes.jei.fabricator;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.render.ForestryResource;
import forestry.factory.blocks.BlockTypeFactoryPlain;
import forestry.factory.features.FactoryBlocks;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabricatorRecipeCategory extends ForestryRecipeCategory<FabricatorRecipeWrapper> {
    private static final int planSlot = 0;
    private static final int smeltingInputSlot = 1;
    private static final int craftOutputSlot = 2;
    private static final int craftInputSlot = 3;

    private static final int inputTank = 0;

    private final static ResourceLocation guiTexture = new ForestryResource("textures/gui/fabricator.png");
    private final ICraftingGridHelper craftingGridHelper;
    private final IDrawable icon;

    public FabricatorRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createDrawable(guiTexture, 20, 16, 136, 54), "block.forestry.fabricator");

        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR)
                .block()));
    }

    @Override
    public ResourceLocation getUid() {
        return ForestryRecipeCategoryUid.FABRICATOR;
    }

    @Override
    public Class<? extends FabricatorRecipeWrapper> getRecipeClass() {
        return FabricatorRecipeWrapper.class;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, FabricatorRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiIngredientGroup guiIngredients = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

        guiIngredients.init(planSlot, true, 118, 0);

        guiIngredients.init(smeltingInputSlot, true, 5, 4);

        guiIngredients.init(craftOutputSlot, false, 118, 36);

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot + x + y * 3;
                guiIngredients.init(index, true, 46 + x * 18, y * 18);
            }
        }

        guiFluidStacks.init(inputTank, true, 6, 32, 16, 16, 2000, false, null);

        IFabricatorRecipe recipe = recipeWrapper.getRecipe();

        ItemStack plan = recipe.getPlan();
        if (!plan.isEmpty()) {
            guiIngredients.set(planSlot, plan);
        }

        List<Ingredient> smeltingInput = new ArrayList<>();
        Fluid recipeFluid = recipe.getLiquid().getFluid();
        for (IFabricatorSmeltingRecipe s : getSmeltingInputs().get(recipeFluid)) {
            smeltingInput.add(s.getResource());
        }

        if (!smeltingInput.isEmpty()) {
            guiIngredients.set(smeltingInputSlot, smeltingInput);
        }

        List<List<ItemStack>> itemOutputs = ingredients.getOutputs(VanillaTypes.ITEM);
        guiIngredients.set(craftOutputSlot, itemOutputs.get(0));

        List<List<ItemStack>> itemStackInputs = ingredients.getInputs(VanillaTypes.ITEM);
        craftingGridHelper.setInputs(guiIngredients, itemStackInputs, recipe.getWidth(), recipe.getHeight());

        List<List<FluidStack>> fluidInputs = ingredients.getInputs(VanillaTypes.FLUID);
        if (!fluidInputs.isEmpty()) {
            guiFluidStacks.set(inputTank, fluidInputs.get(0));
        }
    }

    private static Map<Fluid, List<IFabricatorSmeltingRecipe>> getSmeltingInputs() {
        Map<Fluid, List<IFabricatorSmeltingRecipe>> smeltingInputs = new HashMap<>();
        for (IFabricatorSmeltingRecipe smelting : FabricatorSmeltingRecipeManager.recipes) {
            Fluid fluid = smelting.getProduct().getFluid();
            if (!smeltingInputs.containsKey(fluid)) {
                smeltingInputs.put(fluid, new ArrayList<>());
            }

            smeltingInputs.get(fluid).add(smelting);
        }

        return smeltingInputs;
    }
}
