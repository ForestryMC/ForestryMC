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

import java.util.ArrayList;
import java.util.List;

import forestry.core.inventory.InventoryObject;

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

    public static InventoryIterator<? extends IInvSlot> getIterable(InventoryObject inv) {
        if (inv.getObject() instanceof ISidedInventory) {
			return new SidedInventoryIterator((ISidedInventory) inv.getObject());
		}
        if (inv.getObject() instanceof IInventory) {
			return new StandardInventoryIterator((IInventory) inv.getObject());
		}
        if (inv.getObject() instanceof IItemHandler) {
			return new ItemHandlerInventoryIterator((IItemHandler) inv.getObject());
		}
        throw new RuntimeException("Invalid Inventory Object");
    }

    public Iterable<T> notNull() {
        List<T> filledSlots = new ArrayList<T>(32);
        for (T slot : this) {
            if (slot.getStackInSlot() != null) {
				filledSlots.add(slot);
			}
        }
        return filledSlots;
    }
}
