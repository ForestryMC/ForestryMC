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
package forestry.core.inventory;

import net.minecraft.item.ItemStack;

/**
 * This Interface represents an abstract inventory slot. It provides a unified interface for interfacing with Inventories.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IInvSlot {

	boolean canPutStackInSlot(ItemStack stack);

	boolean canTakeStackFromSlot(ItemStack stack);

	ItemStack decreaseStackInSlot();

	/**
	 * It is not legal to edit the stack returned from this function.
	 */
	ItemStack getStackInSlot();

	//    void setStackInSlot(ItemStack stack);

	int getIndex();

}
