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

package forestry.core.inventory.iterators;

import net.minecraft.item.ItemStack;

/**
 * This interface extends IInvSlot by allowing you to modify a slot directly.
 * This is only valid on inventories backed by IInventory.
 * <p/>
 * <p/>
 * Created by CovertJaguar on 3/16/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IExtInvSlot extends IInvSlot {
	/**
	 * Sets the current ItemStack in the slot.
	 */
	void setStackInSlot(ItemStack stack);
}
