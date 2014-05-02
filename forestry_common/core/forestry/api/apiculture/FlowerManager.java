/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IFlowerProvider;

public class FlowerManager {
	/**
	 * ItemStacks representing simple flower blocks. Meta-sensitive, processed by the basic {@link IFlowerProvider}.
	 */
	public static ArrayList<ItemStack> plainFlowers = new ArrayList<ItemStack>();
}
