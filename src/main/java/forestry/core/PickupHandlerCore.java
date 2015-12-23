/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;

public class PickupHandlerCore implements IPickupHandler {

	@Override
	public boolean onItemPickup(EntityPlayer entityPlayer, EntityItem entityitem) {
		ItemStack itemstack = entityitem.getEntityItem();
		if (itemstack == null || itemstack.stackSize <= 0) {
			return false;
		}

		ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(itemstack);
		if (root != null) {
			IIndividual individual = root.getMember(itemstack);
			if (individual != null) {
				IBreedingTracker tracker = root.getBreedingTracker(entityitem.worldObj, entityPlayer.getGameProfile());
				tracker.registerPickup(individual);
			}
		}

		return false;
	}

}
