/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ICrateRegistry {

	/**
	 * Makes a new crate, registers it with the game registry,
	 * and creates crating and uncrating recipes for the Carpenter.
	 * The icon is rendered automatically from the contained item.
	 *
	 * Can only be called during the Init stage.
	 */
	void registerCrate(Item item);

	void registerCrate(Block block);

	void registerCrate(ItemStack stack);

	/**
	 * Same as the above, but uses the ore dictionary for inputs in the Carpenter crating recipe.
	 */
	void registerCrateUsingOreDict(Item item);

	void registerCrateUsingOreDict(Block block);

	void registerCrateUsingOreDict(ItemStack stack);

}
