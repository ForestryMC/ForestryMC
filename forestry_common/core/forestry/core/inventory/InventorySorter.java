/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.inventory;

import java.util.Comparator;

import net.minecraft.inventory.IInventory;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum InventorySorter implements Comparator<IInventory> {

    SIZE_DECENDING {
        @Override
        public int compare(IInventory inv1, IInventory inv2) {
            return inv2.getSizeInventory() - inv1.getSizeInventory();
        }

    };
}
