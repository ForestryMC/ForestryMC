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
package forestry.apiculture.gui;

import forestry.apiculture.features.ApicultureContainers;
import forestry.apiculture.inventory.InventoryAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.inventory.watchers.ISlotPickupWatcher;
import forestry.core.tiles.TileUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerAlvearySieve extends ContainerTile<TileAlvearySieve> {

    public static ContainerAlvearySieve fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileAlvearySieve tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileAlvearySieve.class);
        return new ContainerAlvearySieve(windowId, inv, tile);    //TODO nullability.
    }

    public ContainerAlvearySieve(int windowId, PlayerInventory player, TileAlvearySieve tile) {
        super(windowId, ApicultureContainers.ALVEARY_SIEVE.containerType(), player, tile, 8, 87);

        ISlotPickupWatcher crafter = tile.getCrafter();

        addSlot(new SlotOutput(tile, 0, 94, 52).setPickupWatcher(crafter));
        addSlot(new SlotOutput(tile, 1, 115, 39).setPickupWatcher(crafter));
        addSlot(new SlotOutput(tile, 2, 73, 39).setPickupWatcher(crafter));
        addSlot(new SlotOutput(tile, 3, 94, 26).setPickupWatcher(crafter));

        addSlot(new SlotFiltered(tile, InventoryAlvearySieve.SLOT_SIEVE, 43, 39).setPickupWatcher(crafter));
    }
}
