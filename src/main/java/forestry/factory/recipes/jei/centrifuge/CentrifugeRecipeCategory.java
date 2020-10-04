package forestry.factory.recipes.jei.centrifuge;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.recipes.jei.ForestryTooltipCallback;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
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

import java.util.*;
import java.util.Map.Entry;

public class CentrifugeRecipeCategory extends ForestryRecipeCategory<CentrifugeRecipeWrapper> {
    private static final int[][] OUTPUTS = new int[][]{
            {0, 0},
            {1, 0},
            {2, 0},
            {0, 1},
            {1, 1},
            {2, 1},
            {0, 2},
            {1, 2},
            {2, 2}
    };

    private static final Comparator<Entry<ItemStack, Float>> highestChanceComparator = (o1, o2) -> o2.getValue()
                                                                                                     .compareTo(o1.getValue());

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;

    private final static ResourceLocation guiTexture = new ResourceLocation(
            Constants.MOD_ID,
            Constants.TEXTURE_PATH_GUI + "centrifugesocket2.png"
    );
    private final IDrawableAnimated arrow;
    private final IDrawable icon;

    public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createDrawable(guiTexture, 11, 18, 154, 54), "block.forestry.centrifuge");

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 0, 4, 17);
        this.arrow = guiHelper.createAnimatedDrawable(
                arrowDrawable,
                80,
                IDrawableAnimated.StartDirection.BOTTOM,
                false
        );
        this.icon = guiHelper.createDrawableIngredient(
                new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).block())
        );
    }

    @Override
    public ResourceLocation getUid() {
        return ForestryRecipeCategoryUid.CENTRIFUGE;
    }

    @Override
    public Class<? extends CentrifugeRecipeWrapper> getRecipeClass() {
        return CentrifugeRecipeWrapper.class;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(CentrifugeRecipeWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack, 32, 18);
        arrow.draw(matrixStack, 56, 18);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CentrifugeRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 4, 18);
        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        guiItemStacks.set(inputSlot, inputs.get(0));

        ForestryTooltipCallback tooltip = new ForestryTooltipCallback();

        Map<ItemStack, Float> products = new LinkedHashMap<>();
        for (ICentrifugeRecipe.Product product : recipeWrapper.getRecipe().getAllProducts()) {
            products.put(product.getStack(), product.getProbability());
        }

        setResults(tooltip, products, guiItemStacks);
        guiItemStacks.addTooltipCallback(tooltip);
    }

    private static void setResults(
            ForestryTooltipCallback tooltip,
            Map<ItemStack, Float> outputs,
            IGuiItemStackGroup guiItemStacks
    ) {
        Set<Entry<ItemStack, Float>> entrySet = outputs.entrySet();
        if (entrySet.isEmpty()) {
            return;
        }

        Queue<Entry<ItemStack, Float>> sortByChance = new PriorityQueue<>(entrySet.size(), highestChanceComparator);
        sortByChance.addAll(entrySet);

        int i = 0;
        while (!sortByChance.isEmpty()) {
            Entry<ItemStack, Float> stack = sortByChance.poll();
            if (i >= OUTPUTS.length) {
                return;
            }

            int x = 100 + OUTPUTS[i][0] * 18;
            int y = OUTPUTS[i][1] * 18;
            int slotIndex = outputSlot + i;
            guiItemStacks.init(slotIndex, false, x, y);
            guiItemStacks.set(slotIndex, stack.getKey());
            tooltip.addChanceTooltip(slotIndex, stack.getValue());
            i++;
        }
    }
}
