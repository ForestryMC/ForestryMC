package forestry.arboriculture.charcoal.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryTooltipCallback;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CharcoalPileWallCategory extends ForestryRecipeCategory<CharcoalPileWallWrapper> {
    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;
    private final IDrawableAnimated arrowAnimated;
    private final IDrawableStatic flame;
    private final IDrawableAnimated flameAnimated;
    private final IDrawable icon;

    public CharcoalPileWallCategory(IGuiHelper helper) {
        super(helper.createBlankDrawable(120, 38), "for.jei.charcoal.pile");
        ResourceLocation resourceLocation = new ResourceLocation(
                Constants.MOD_ID,
                Constants.TEXTURE_PATH_GUI + "jei/recipes.png"
        );
        arrow = helper.createDrawable(resourceLocation, 0, 14, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(resourceLocation, 22, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(
                arrowAnimated,
                160,
                IDrawableAnimated.StartDirection.LEFT,
                false
        );
        flame = helper.createDrawable(resourceLocation, 0, 0, 14, 14);
        IDrawableStatic flameAnimated = helper.createDrawable(resourceLocation, 14, 0, 14, 14);
        this.flameAnimated = helper.createAnimatedDrawable(
                flameAnimated,
                260,
                IDrawableAnimated.StartDirection.TOP,
                true
        );
        this.slot = helper.getSlotDrawable();
        this.icon = helper.createDrawableIngredient(new ItemStack(CharcoalBlocks.CHARCOAL.getBlock()));
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(Constants.MOD_ID);
    }

    @Override
    public Class<? extends CharcoalPileWallWrapper> getRecipeClass() {
        return CharcoalPileWallWrapper.class;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(CharcoalPileWallWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        flame.draw(matrixStack, 52, 0);
        flameAnimated.draw(matrixStack, 52, 0);
        arrow.draw(matrixStack, 50, 16);
        arrowAnimated.draw(matrixStack, 50, 16);
        slot.draw(matrixStack, 0, 16);
        slot.draw(matrixStack, 20, 16);
        slot.draw(matrixStack, 84, 16);
        slot.draw(matrixStack, 104, 16);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CharcoalPileWallWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.init(0, true, 0, 16);
        itemStackGroup.init(1, true, 20, 16);
        itemStackGroup.init(2, false, 84, 16);
        itemStackGroup.init(3, false, 104, 16);

        itemStackGroup.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStackGroup.set(1, new ItemStack(CharcoalBlocks.WOOD_PILE.getBlock()));
        itemStackGroup.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        itemStackGroup.set(3, ingredients.getOutputs(VanillaTypes.ITEM).get(1));

        ForestryTooltipCallback tooltip = new ForestryTooltipCallback();
        tooltip.addFortuneTooltip(2);
        tooltip.addFortuneTooltip(3);
        itemStackGroup.addTooltipCallback(tooltip);
    }
}
