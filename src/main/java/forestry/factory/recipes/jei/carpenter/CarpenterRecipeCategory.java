package forestry.factory.recipes.jei.carpenter;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class CarpenterRecipeCategory extends ForestryRecipeCategory<CarpenterRecipeWrapper> {
    private static final int boxSlot = 0;
    private static final int craftOutputSlot = 1;
    private static final int craftInputSlot = 2;
    private static final int inputTank = 0;

    private final static ResourceLocation guiTexture = new ResourceLocation(
            Constants.MOD_ID,
            Constants.TEXTURE_PATH_GUI + "carpenter.png"
    );
    private final ICraftingGridHelper craftingGridHelper;
    private final IDrawableAnimated arrow;
    private final IDrawable tankOverlay;
    private final IDrawable icon;

    public CarpenterRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createDrawable(guiTexture, 9, 16, 158, 61), "block.forestry.carpenter");

        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot);
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 59, 4, 17);
        this.arrow = guiHelper.createAnimatedDrawable(
                arrowDrawable,
                200,
                IDrawableAnimated.StartDirection.BOTTOM,
                false
        );
        this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
        this.icon = guiHelper.createDrawableIngredient(
                new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CARPENTER).block())
        );
    }


    @Override
    public ResourceLocation getUid() {
        return ForestryRecipeCategoryUid.CARPENTER;
    }

    @Override
    public Class<? extends CarpenterRecipeWrapper> getRecipeClass() {
        return CarpenterRecipeWrapper.class;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(CarpenterRecipeWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack, 89, 34);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CarpenterRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiIngredientGroup guiIngredients = recipeLayout.getIngredientsGroup(VanillaTypes.ITEM);
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

        guiIngredients.init(boxSlot, true, 73, 3);

        guiIngredients.init(craftOutputSlot, false, 70, 34);

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot + x + y * 3;
                guiIngredients.init(index, true, x * 18, 3 + y * 18);
            }
        }

        guiFluidStacks.init(inputTank, true, 141, 1, 16, 58, 10000, false, tankOverlay);

        ICarpenterRecipe recipe = recipeWrapper.getRecipe();
        Ingredient box = recipe.getBox();
        if (!box.hasNoMatchingItems()) {
            guiIngredients.set(boxSlot, box);
        }

        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        guiIngredients.set(craftOutputSlot, outputs.get(0));

        ShapedRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();

        List<List<Ingredient>> craftingInputs = new ArrayList<>();
        craftingInputs.add(recipeWrapper.getIngredients());

        craftingGridHelper.setInputs(
                guiIngredients,
                craftingInputs,
                craftingGridRecipe.getWidth(),
                craftingGridRecipe.getHeight()
        );

        List<List<FluidStack>> fluidInputs = ingredients.getInputs(VanillaTypes.FLUID);
        if (!fluidInputs.isEmpty()) {
            guiFluidStacks.set(inputTank, fluidInputs.get(0));
        }
    }
}
