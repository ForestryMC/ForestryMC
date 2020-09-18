package forestry.arboriculture.charcoal.jei;

import com.google.common.collect.ImmutableList;
import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.arboriculture.features.CharcoalBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CharcoalPileWallWrapper implements IRecipeCategoryExtension {
    private final ICharcoalPileWall pileWall;

    public CharcoalPileWallWrapper(ICharcoalPileWall pileWall) {
        this.pileWall = pileWall;
    }

    public void setIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, pileWall.getDisplayItems());
        int amount = 9 + pileWall.getCharcoalAmount();
        ItemStack charcoal = new ItemStack(Items.COAL, amount);
        ItemStack ash = new ItemStack(CharcoalBlocks.ASH.getItem(), amount / 4);
        ImmutableList<ItemStack> outputs = ImmutableList.of(
                charcoal,
                ash
        );
        ingredients.setOutputs(VanillaTypes.ITEM, outputs);
    }
}
