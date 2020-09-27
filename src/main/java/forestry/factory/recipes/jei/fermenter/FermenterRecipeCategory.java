package forestry.factory.recipes.jei.fermenter;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.render.ForestryResource;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FermenterRecipeCategory extends ForestryRecipeCategory<FermenterRecipeWrapper> {
    private static final int resourceSlot = 0;
    private static final int fuelSlot = 1;

    private static final int inputTank = 0;
    private static final int outputTank = 1;

    private static final ResourceLocation guiTexture = new ForestryResource("textures/gui/fermenter.png");

    private final IDrawableAnimated progressBar0;
    private final IDrawableAnimated progressBar1;
    private final IDrawable tankOverlay;
    private final IDrawable icon;

    public FermenterRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createDrawable(guiTexture, 34, 18, 108, 60), "block.forestry.fermenter");

        IDrawableStatic progressBarDrawable0 = guiHelper.createDrawable(guiTexture, 176, 60, 4, 18);
        this.progressBar0 = guiHelper.createAnimatedDrawable(
                progressBarDrawable0,
                40,
                IDrawableAnimated.StartDirection.BOTTOM,
                false
        );
        IDrawableStatic progressBarDrawable1 = guiHelper.createDrawable(guiTexture, 176, 78, 4, 18);
        this.progressBar1 = guiHelper.createAnimatedDrawable(
                progressBarDrawable1,
                80,
                IDrawableAnimated.StartDirection.BOTTOM,
                false
        );
        this.tankOverlay = guiHelper.createDrawable(guiTexture, 192, 0, 16, 58);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER)
                                                                                       .block()));
    }

    @Override
    public ResourceLocation getUid() {
        return ForestryRecipeCategoryUid.FERMENTER;
    }

    @Override
    public Class<? extends FermenterRecipeWrapper> getRecipeClass() {
        return FermenterRecipeWrapper.class;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(FermenterRecipeWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        progressBar0.draw(matrixStack, 40, 14);
        progressBar1.draw(matrixStack, 64, 28);
    }

    @Override
    public void setRecipe(
            IRecipeLayout recipeLayout,
            FermenterRecipeWrapper fermenterRecipeWrapper,
            IIngredients ingredients
    ) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

        guiItemStacks.init(resourceSlot, true, 50, 4);
        guiItemStacks.init(fuelSlot, true, 40, 38);

        guiFluidStacks.init(inputTank, true, 1, 1, 16, 58, 3000, false, tankOverlay);
        guiFluidStacks.init(outputTank, false, 91, 1, 16, 58, 3000, false, tankOverlay);

        guiItemStacks.set(ingredients);
        guiFluidStacks.set(ingredients);
    }
}
