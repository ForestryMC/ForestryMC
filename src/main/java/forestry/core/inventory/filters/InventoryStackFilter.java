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
package forestry.core.inventory.filters;

import forestry.core.utils.InventoryUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryStackFilter extends StackFilter {

    private final IInventory inv;

    public InventoryStackFilter(IInventory inv) {
        this.inv = inv;
    }

    @Override
    public boolean apply(final ItemStack stack) {
        return InventoryUtil.containsItem(inv, stack);
    }
}
