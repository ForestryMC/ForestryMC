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
    ItemStack getCamouflageBlock(String type);

    /**
     * @return The default camouflage block item for the type.
     */
    ItemStack getDefaultCamouflageBlock(String type);

    /**
     * @return True if the handler can handle this type of camouflage.
     */
    boolean canHandleType(String type);

    /**
     * Set the camouflage block item for the type.
     * @return True if the block has chanced.
     */
    boolean setCamouflageBlock(String type, ItemStack camouflageBlock, boolean sendClientUpdate);
}
