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
package forestry.storage.inventory;

import forestry.core.gui.IPagedInventory;
import forestry.storage.items.ItemBackpackNaturalist;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemInventoryBackpackPaged extends ItemInventoryBackpack implements IPagedInventory {
    private final ItemBackpackNaturalist backpackNaturalist;

    public ItemInventoryBackpackPaged(
            PlayerEntity player,
            int size,
            ItemStack itemstack,
            ItemBackpackNaturalist backpackNaturalist
    ) {
        super(player, size, itemstack);
        this.backpackNaturalist = backpackNaturalist;
    }

    //TODO gui
    @Override
    public void flipPage(ServerPlayerEntity player, short page) {
        ItemStack heldItem = player.getHeldItem(player.getActiveHand());
        NetworkHooks.openGui(player, new ItemBackpackNaturalist.ContainerProvider(heldItem), b -> {

        });
    }
}
