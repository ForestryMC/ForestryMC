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
package forestry.api.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import net.minecraftforge.registries.ObjectHolder;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;

public interface ISolderRecipe extends IForestryRecipe {

	RecipeType<ISolderRecipe> TYPE = RecipeManagers.create("forestry:solder");

	class Companion {
		@ObjectHolder("forestry:solder")
		public static final RecipeSerializer<ISolderRecipe> SERIALIZER = null;
	}

	boolean matches(ICircuitLayout layout, ItemStack itemstack);

	ICircuitLayout getLayout();

	ItemStack getResource();

	ICircuit getCircuit();

	@Override
	default RecipeType<?> getType() {
		return TYPE;
	}

	@Override
	default RecipeSerializer<?> getSerializer() {
		return ISolderRecipe.Companion.SERIALIZER;
	}
}
