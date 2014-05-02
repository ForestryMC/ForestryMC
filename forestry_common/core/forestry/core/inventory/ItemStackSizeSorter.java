/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.inventory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;

public class ItemStackSizeSorter implements Comparator<ItemStack> {

    private static ItemStackSizeSorter instance;

    private static ItemStackSizeSorter getInstance() {
        if (instance == null) {
            instance = new ItemStackSizeSorter();
        }
        return instance;
    }

    public static void sort(List<ItemStack> list) {
        Collections.sort(list, getInstance());
    }

    @Override
    public int compare(ItemStack o1, ItemStack o2) {
        return o1.stackSize - o2.stackSize;
    }

}
