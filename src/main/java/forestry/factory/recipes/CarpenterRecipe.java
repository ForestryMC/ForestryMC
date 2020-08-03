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
package forestry.factory.recipes;

import com.google.common.base.Preconditions;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.ShapedRecipeCustom;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CarpenterRecipe implements ICarpenterRecipe {

    private final int packagingTime;
    private final FluidStack liquid;
    private final ItemStack box;
    private final ShapedRecipeCustom internal;

    public CarpenterRecipe(int packagingTime, FluidStack liquid, ItemStack box, ShapedRecipeCustom internal) {
        Preconditions.checkNotNull(box);
        Preconditions.checkNotNull(internal);
        this.packagingTime = packagingTime;
        this.liquid = liquid;
        this.box = box;
        this.internal = internal;
    }

    @Override
    public int getPackagingTime() {
        return packagingTime;
    }

    @Override
    public ItemStack getBox() {
        return box;
    }

    @Override
    public FluidStack getFluidResource() {
        return liquid;
    }

    @Override
    public IDescriptiveRecipe getCraftingGridRecipe() {
        return internal;
    }
}
