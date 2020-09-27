/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.recipes;

import forestry.apiculture.items.ItemHoneyComb;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;

import javax.annotation.Nullable;
import java.util.List;

public class CombBlockRecipeWrapper implements IRecipeCategoryExtension {
    private final int meta;
    @Nullable
    private static final ItemHoneyComb comb = null;
    @Nullable
    private static final List<?> blockHoneyCombs = null;

    public CombBlockRecipeWrapper(int meta) {
        this.meta = meta;
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
//        if (comb == null) {
//            comb = ApicultureBlocks.BEE_COMB.getItems();
//        }
//
//        if (blockHoneyCombs == null) {
//            blockHoneyCombs = ApicultureBlocks.BEE_COMB.getBlocks();
//        }
//
//        NonNullList<ItemStack> combs = NonNullList.withSize(9, new ItemStack(comb, 1, meta));
//        ingredients.setInputs(ItemStack.class, combs);
//        ingredients.setOutput(ItemStack.class, new ItemStack(blockHoneyCombs[meta / 16], 1/*, meta & 15*/));
    }
}
