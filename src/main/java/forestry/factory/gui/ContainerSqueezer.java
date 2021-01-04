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

import forestry.core.gui.ContainerLiquidTanksSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.tiles.TileUtil;
import forestry.factory.features.FactoryContainers;
import forestry.factory.inventory.InventorySqueezer;
import forestry.factory.tiles.TileSqueezer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerSqueezer extends ContainerLiquidTanksSocketed<TileSqueezer> {

    public static ContainerSqueezer fromNetwork(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileSqueezer tile = TileUtil.getTile(inv.player.world, data.readBlockPos(), TileSqueezer.class);
        return new ContainerSqueezer(windowId, inv, tile);    //TODO nullability.
    }

    public ContainerSqueezer(int windowId, PlayerInventory player, TileSqueezer tile) {
        super(windowId, FactoryContainers.SQUEEZER.containerType(), player, tile, 8, 84);

        // Resource inventory
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                this.addSlot(new SlotFiltered(this.tile, column + row * 3, 17 + column * 18, 21 + row * 18));
            }
        }

        // Remnants slot
        this.addSlot(new SlotOutput(this.tile, InventorySqueezer.SLOT_REMNANT, 97, 60));

        // Can slot
        this.addSlot(new SlotLiquidIn(this.tile, InventorySqueezer.SLOT_CAN_INPUT, 147, 24));
        // Output slot
        this.addSlot(new SlotOutput(this.tile, InventorySqueezer.SLOT_CAN_OUTPUT, 147, 60));
    }
}
