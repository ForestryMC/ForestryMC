/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.item.ItemStack;

public interface ICamouflageHandler extends ILocatable {

	/**
	 * @return The item of the block that is the camouflage of this handler for the type.
	 */
	ItemStack getCamouflageBlock();

	/**
	 * @return The default camouflage block item for the type.
	 */
	ItemStack getDefaultCamouflageBlock();

	/**
	 * Set the camouflage block item for the type.
	 *
	 * @return True if the block has chanced.
	 */
	boolean setCamouflageBlock(ItemStack camouflageBlock, boolean sendClientUpdate);
}
