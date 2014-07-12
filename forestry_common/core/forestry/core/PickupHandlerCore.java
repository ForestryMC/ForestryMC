/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.interfaces.IPickupHandler;

public class PickupHandlerCore implements IPickupHandler {

	@Override
	public boolean onItemPickup(EntityPlayer entityPlayer, EntityItem entityitem) {
		ItemStack itemstack = entityitem.getEntityItem();
		if (itemstack == null || itemstack.stackSize <= 0)
			return false;

		ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(itemstack);
		if(root != null)
			root.getBreedingTracker(entityitem.worldObj, entityPlayer.getGameProfile()).registerPickup(root.getMember(itemstack));

		return true;
	}

}
