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
package forestry.factory.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotEmptyLiquidContainerIn;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.tiles.TileUtil;
import forestry.factory.features.FactoryContainers;
import forestry.factory.inventory.InventoryBottler;
import forestry.factory.tiles.TileBottler;

public class ContainerBottler extends ContainerLiquidTanks<TileBottler> {

    public static ContainerBottler fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileBottler tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileBottler.class);
        return new ContainerBottler(windowId, inv, tile);    //TODO nullability.
    }

    public ContainerBottler(int windowId, PlayerInventory player, TileBottler tile) {
        super(windowId, FactoryContainers.BOTTLER.containerType(), player, tile, 8, 84);

        this.addSlot(new SlotLiquidIn(tile, InventoryBottler.SLOT_INPUT_FULL_CONTAINER, 18, 7));
        this.addSlot(new SlotOutput(tile, InventoryBottler.SLOT_EMPTYING_PROCESSING, 18, 35).setPickupWatcher(tile));
        this.addSlot(new SlotOutput(tile, InventoryBottler.SLOT_OUTPUT_EMPTY_CONTAINER, 18, 63));
        this.addSlot(new SlotEmptyLiquidContainerIn(tile, InventoryBottler.SLOT_INPUT_EMPTY_CONTAINER, 142, 7));
        this.addSlot(new SlotOutput(tile, InventoryBottler.SLOT_FILLING_PROCESSING, 142, 35).setPickupWatcher(tile));
        this.addSlot(new SlotOutput(tile, InventoryBottler.SLOT_OUTPUT_FULL_CONTAINER, 142, 63));
    }
}
