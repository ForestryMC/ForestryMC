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
package forestry.mail.items;

import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemWithGui;
import forestry.mail.gui.ContainerCatalogue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemCatalogue extends ItemWithGui {

    public ItemCatalogue() {
        super((new Item.Properties()).group(ItemGroupForestry.tabForestry));
    }

    @Nullable
    @Override
    public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
        return new ContainerCatalogue(windowId, player.inventory);
    }
}
