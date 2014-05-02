/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.inventory.filters;

import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InvertedStackFilter implements IStackFilter {

    private final IStackFilter filter;

    public InvertedStackFilter(IStackFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return !filter.matches(stack);
    }
}
