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
package forestry.energy.tiles;

import forestry.core.inventory.InventoryAdapterTile;
import ic2.api.item.ElectricItem;
import net.minecraft.item.ItemStack;

public class InventoryEngineElectric extends InventoryAdapterTile<TileEngineElectric> {
    public static final short SLOT_BATTERY = 0;

    public InventoryEngineElectric(TileEngineElectric engineTin) {
        super(engineTin, 1, "electrical");
    }

    @Override
    public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
        if (slotIndex == SLOT_BATTERY) {
            return ElectricItem.manager.getCharge(itemStack) > 0;
        }
        return false;
    }
}
