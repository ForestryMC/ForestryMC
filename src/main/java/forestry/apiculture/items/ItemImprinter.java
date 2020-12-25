/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.apiculture.items;

import forestry.api.core.ItemGroups;
import forestry.apiculture.gui.ContainerImprinter;
import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.core.items.ItemWithGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemImprinter extends ItemWithGui {
    public ItemImprinter() {
        super((new Item.Properties()).group(ItemGroups.tabApiculture).maxStackSize(1));
    }

    @Override
    public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
        return new ContainerImprinter(windowId, player.inventory, new ItemInventoryImprinter(player, heldItem));
    }
}
