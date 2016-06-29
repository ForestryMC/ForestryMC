/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICamouflageHandler{

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
     */
    void setCamouflageBlock(String type, ItemStack camouflageBlock);
    
    /**
     * @return The coordinates of the handler as a BlockPos.
     */
	BlockPos getCoordinates();
	
	World getWorld();
}
