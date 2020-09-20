package forestry.farming.compat;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.blocks.EnumFarmMaterial;
import forestry.farming.features.FarmingBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class FarmingInfoRecipeCategory extends ForestryRecipeCategory<FarmingInfoRecipeWrapper> {
    public static final ResourceLocation UID = new ResourceLocation(Constants.MOD_ID, "farming");
    private final IDrawable slotDrawable;
    private final IDrawable addition;
    private final IDrawable arrow;
    private final IDrawable icon;

    public FarmingInfoRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createBlankDrawable(144, 90), "for.jei.farming");
        this.slotDrawable = guiHelper.getSlotDrawable();
        ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, "textures/gui/jei/recipes.png");
        addition = guiHelper.createDrawable(resourceLocation, 44, 0, 15, 15);
        arrow = guiHelper.createDrawable(resourceLocation, 59, 0, 15, 15);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(FarmingBlocks.FARM.get(
                EnumFarmBlockType.PLAIN,
                EnumFarmMaterial.BRICK
        ).block()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends FarmingInfoRecipeWrapper> getRecipeClass() {
        return FarmingInfoRecipeWrapper.class;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(FarmingInfoRecipeWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        slotDrawable.draw(matrixStack, 63, 18);
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                slotDrawable.draw(matrixStack, x * 18, 54 + y * 18);
            }
        }

        addition.draw(matrixStack, 37, 64);

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                slotDrawable.draw(matrixStack, 54 + x * 18, 54 + y * 18);
            }
        }

        arrow.draw(matrixStack, 91, 64);

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                slotDrawable.draw(matrixStack, 108 + x * 18, 54 + y * 18);
            }
        }
    }

    @Override
    public void setRecipe(
            IRecipeLayout recipeLayout,
            FarmingInfoRecipeWrapper recipeWrapper,
            IIngredients ingredients
    ) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
        guiItemStacks.init(0, true, 63, 18);
        if (inputs.size() > 0) {
            guiItemStacks.set(0, inputs.get(0));
        }

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                int index = 1 + x + y * 2;
                guiItemStacks.init(index, true, x * 18, 54 + y * 18);
                if (inputs.size() > index) {
                    List<ItemStack> stack = inputs.get(index);
                    guiItemStacks.set(index, stack);
                }
            }
        }

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                int index = 5 + x + y * 2;
                guiItemStacks.init(index, true, 54 + x * 18, 54 + y * 18);
                if (inputs.size() > index) {
                    List<ItemStack> stack = inputs.get(index);
                    guiItemStacks.set(index, stack);
                }
            }
        }

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                int index = 9 + x + y * 2;
                guiItemStacks.init(index, false, 108 + x * 18, 54 + y * 18);
                if (outputs.size() > x + y * 2) {
                    List<ItemStack> stack = outputs.get(x + y * 2);
                    guiItemStacks.set(index, stack);
                }
            }
        }
    }
}
