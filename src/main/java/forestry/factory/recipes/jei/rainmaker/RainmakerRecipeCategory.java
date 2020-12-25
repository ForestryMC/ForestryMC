package forestry.factory.recipes.jei.rainmaker;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RainmakerRecipeCategory extends ForestryRecipeCategory<RainmakerRecipeWrapper> {
    private static final int SLOT_INPUT_INDEX = 0;
    private final IDrawable slot;
    private final IDrawable icon;

    public RainmakerRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createBlankDrawable(150, 30), "block.forestry.rainmaker");
        this.slot = guiHelper.getSlotDrawable();
        this.icon = guiHelper.createDrawableIngredient(
                new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.RAINMAKER).block())
        );
    }

    @Override
    public ResourceLocation getUid() {
        return ForestryRecipeCategoryUid.RAINMAKER;
    }

    @Override
    public Class<? extends RainmakerRecipeWrapper> getRecipeClass() {
        return RainmakerRecipeWrapper.class;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(RainmakerRecipeWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
        int recipeWidth = this.getBackground().getWidth();
        int recipeHeight = this.getBackground().getHeight();
        recipe.drawInfo(recipeWidth, recipeHeight, matrixStack, mouseX, mouseY);
        slot.draw(matrixStack);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RainmakerRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(SLOT_INPUT_INDEX, true, 0, 0);
        guiItemStacks.set(ingredients);
    }
}
