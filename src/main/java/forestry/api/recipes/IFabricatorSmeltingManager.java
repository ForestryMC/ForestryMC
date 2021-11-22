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

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

public interface IFabricatorSmeltingManager extends ICraftingProvider<IFabricatorSmeltingRecipe> {
	/**
	 * Add a smelting recipe to the Fabricator
	 * @param resource item to be melted down
	 * @param molten resulting fluid
	 * @param meltingPoint temperature at which the item melts. Glass is 1000, Sand is 3000.
	 */
	void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint);
}
