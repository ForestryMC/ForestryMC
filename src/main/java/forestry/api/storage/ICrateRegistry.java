/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Makes a new crate, registers it with the game registry,
 * and creates crating and uncrating recipes for the Carpenter.
 * The icon is rendered automatically from the contained item.
 * If an oreDictName is specified, it will make recipes using that.
 *
 * Can only be called during the Init stage.
 */
public interface ICrateRegistry {

	void registerCrate(@Nonnull Item item);

	void registerCrate(@Nonnull Block block);

	void registerCrate(@Nonnull ItemStack stack);

	void registerCrate(@Nonnull String oreDictName);

}
