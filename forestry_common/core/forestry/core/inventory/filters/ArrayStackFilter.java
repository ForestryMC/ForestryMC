/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.inventory.filters;

import net.minecraft.item.ItemStack;

import forestry.core.inventory.InvTools;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ArrayStackFilter implements IStackFilter {

    private final ItemStack[] stacks;

    public ArrayStackFilter(ItemStack... stacks) {
        this.stacks = stacks;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stacks.length == 0 || !hasFilter()) {
            return true;
        }
        return InvTools.isItemEqual(stack, stacks);
    }

    public ItemStack[] getStacks() {
        return stacks;
    }

    public boolean hasFilter() {
        for (ItemStack filter : stacks) {
            if (filter != null) {
                return true;
            }
        }
        return false;
    }
}
