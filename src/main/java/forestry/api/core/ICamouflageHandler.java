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

    ItemStack getCamouflageBlock(EnumCamouflageType type);
    
    ItemStack getDefaultCamouflageBlock(EnumCamouflageType type);

    void setCamouflageBlock(EnumCamouflageType type, ItemStack camouflageBlock);
    
	BlockPos getCoordinates();

	World getWorld();
	
}
