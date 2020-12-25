/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.energy.gui;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.TileUtil;
import forestry.energy.features.EnergyContainers;
import forestry.energy.inventory.InventoryGenerator;
import forestry.energy.tiles.TileEuGenerator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerGenerator extends ContainerLiquidTanks<TileEuGenerator> {

    public ContainerGenerator(int windowId, PlayerInventory player, TileEuGenerator tile) {
        super(windowId, EnergyContainers.GENERATOR.containerType(), player, tile, 8, 84);

        this.addSlot(new SlotLiquidIn(tile, InventoryGenerator.SLOT_CAN, 22, 38));
    }

    //TODO dedupe
    public static ContainerGenerator fromNetwork(int windowId, PlayerInventory inv, PacketBuffer extraData) {
        TileEuGenerator tile = TileUtil.getTile(inv.player.world, extraData.readBlockPos(), TileEuGenerator.class);
        return new ContainerGenerator(windowId, inv, tile);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        PacketGuiUpdate packet = new PacketGuiUpdate(tile);
        sendPacketToListeners(packet);
    }
}
