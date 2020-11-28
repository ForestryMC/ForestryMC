package forestry.factory.recipes.jei.still;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StillRecipeCategory extends ForestryRecipeCategory {
    private static final int inputTank = 0;
    private static final int outputTank = 1;

    private static final ResourceLocation guiTexture = new ResourceLocation(
            Constants.MOD_ID,
            Constants.TEXTURE_PATH_GUI + "still.png"
    );

    private final IDrawable tankOverlay;
    private final IDrawable icon;
    private final IDrawableAnimated progressBar;

    public StillRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createDrawable(guiTexture, 34, 14, 108, 60), "block.forestry.still");
        this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);

        IDrawableStatic progressBarDrawable0 = guiHelper.createDrawable(guiTexture, 176, 74, 4, 18);
        this.progressBar = guiHelper.createAnimatedDrawable(
                progressBarDrawable0,
                20,
                IDrawableAnimated.StartDirection.BOTTOM,
                false
        );
        this.icon = guiHelper.createDrawableIngredient(
                new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).block())
        );
    }

    @Override
    public ResourceLocation getUid() {
        return ForestryRecipeCategoryUid.STILL;
    }

    @Override
    public Class<? extends StillRecipeWrapper> getRecipeClass() {
        return StillRecipeWrapper.class;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, Object o, IIngredients ingredients) {
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

        guiFluidStacks.init(inputTank, true, 1, 1, 16, 58, 10000, false, tankOverlay);
        guiFluidStacks.init(outputTank, false, 91, 1, 16, 58, 10000, false, tankOverlay);

        guiFluidStacks.set(ingredients);
    }

    @Override
    public void draw(Object recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        progressBar.draw(matrixStack, 50, 3);
    }
}
