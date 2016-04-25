/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface ICamouflageHandler{

	/**
	 * @return The item of the block that is the camouflage of this handler for the EnumCamouflageType.
	 */
    ItemStack getCamouflageBlock(EnumCamouflageType type);
    
    /**
     * @return The default camouflage block item for the EnumCamouflageType.
     */
    ItemStack getDefaultCamouflageBlock(EnumCamouflageType type);

    /**
     * Set the camouflage block item for the EnumCamouflageType.
     */
    void setCamouflageBlock(EnumCamouflageType type, ItemStack camouflageBlock);
    
    /**
     * @return The coordinates of the handler as a BlockPos.
     */
	BlockPos getCoordinates();

	/**
	 * @return The world of the handler.
	 */
	World getWorld();
	
}
