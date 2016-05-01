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

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;

import net.minecraftforge.items.IItemHandler;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class InventoryIterator<T extends IInvSlot> implements Iterable<T> {

	public static InventoryIterator<IExtInvSlot> getIterable(IInventory inv) {
		if (inv instanceof ISidedInventory) {
			return new SidedInventoryIterator((ISidedInventory) inv);
		}
		return new StandardInventoryIterator(inv);
	}

	public static InventoryIterator<IInvSlot> getIterable(IItemHandler inv) {
		return new ItemHandlerInventoryIterator(inv);
	}
}
