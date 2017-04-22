/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface ICamouflageAccess {

	/**
	 * Register a item camouflage handler for a type.
	 *
	 * @param itemHandler The handler that is to register.
	 */
	void registerCamouflageItemHandler(ICamouflageItemHandler itemHandler);

	/**
	 * @return A list of all item camouflage handler's that are registered for that camouflage type.
	 */
	List<ICamouflageItemHandler> getCamouflageItemHandler(String type);

	void addItemToBlackList(String type, ItemStack camouflageBlock);

	void addModIdToBlackList(String type, String modID);

	boolean isItemBlackListed(String type, ItemStack camouflageBlock);

	ICamouflageItemHandler getNoneItemHandler();
	
	ICamouflageItemHandler getHandlerFromItem(ItemStack stack);

}
