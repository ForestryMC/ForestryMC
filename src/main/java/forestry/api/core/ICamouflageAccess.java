/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface ICamouflageAccess {
	
	/**
	 * Register a item camouflage handler for a type.
	 * 
	 * @param type The camouflage type for of handler.
	 * @param itemHandler The handler that is to register.
	 */
	void registerCamouflageItemHandler(@Nonnull ICamouflageItemHandler itemHandler);
	
	/**
	 * @return A list of all item camouflage handler's that are registered for that camouflage type.
	 */
	List<ICamouflageItemHandler> getCamouflageItemHandler(@Nonnull String type);
	
	void addItemToBlackList(@Nonnull String type, @Nonnull ItemStack camouflageBlock);
	
	void addModIdToBlackList(@Nonnull String type, @Nonnull String modID);
	
	boolean isItemBlackListed(@Nonnull String type, @Nonnull ItemStack camouflageBlock);
	
	ICamouflageItemHandler getHandlerFromItem(ItemStack stack);
	
}
