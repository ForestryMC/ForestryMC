/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.fuels;

import net.minecraft.item.ItemStack;

public class MoistenerFuel {
	/**
	 * The item to use
	 */
	public final ItemStack item;
	/**
	 * The item that leaves the moistener's working slot (i.e. mouldy wheat, decayed wheat, mulch)
	 */
	public final ItemStack product;
	/**
	 * How much this item contributes to the final product of the moistener (i.e. mycelium)
	 */
	public final int moistenerValue;
	/**
	 * What stage this product represents. Resources with lower stage value will be consumed first.
	 */
	public final int stage;

	public MoistenerFuel(ItemStack item, ItemStack product, int stage, int moistenerValue) {
		this.item = item;
		this.product = product;
		this.stage = stage;
		this.moistenerValue = moistenerValue;
	}
}
