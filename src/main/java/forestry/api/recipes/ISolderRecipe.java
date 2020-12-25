/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.api.recipes;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.registries.ObjectHolder;

public interface ISolderRecipe extends IForestryRecipe {

    IRecipeType<ISolderRecipe> TYPE = RecipeManagers.create("forestry:solder");

    boolean matches(ICircuitLayout layout, ItemStack itemstack);

    ICircuitLayout getLayout();

    ItemStack getResource();

    ICircuit getCircuit();

    @Override
    default IRecipeType<?> getType() {
        return TYPE;
    }

    @Override
    default IRecipeSerializer<?> getSerializer() {
        return Companion.SERIALIZER;
    }

    class Companion {
        @ObjectHolder("forestry:solder")
        public static final IRecipeSerializer<ISolderRecipe> SERIALIZER = null;
    }
}
