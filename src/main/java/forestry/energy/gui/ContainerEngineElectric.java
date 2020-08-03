/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.energy.gui;

import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.TileUtil;
import forestry.energy.features.EnergyContainers;
import forestry.energy.inventory.InventoryEngineElectric;
import forestry.energy.tiles.TileEngineElectric;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerEngineElectric extends ContainerSocketed<TileEngineElectric> {

    //TODO dedupe
    public static ContainerEngineElectric fromNetwork(int windowId, PlayerInventory inv, PacketBuffer extraData) {
        TileEngineElectric tile = TileUtil.getTile(inv.player.world, extraData.readBlockPos(), TileEngineElectric.class);
        return new ContainerEngineElectric(windowId, inv, tile);
    }

    public ContainerEngineElectric(int windowId, PlayerInventory player, TileEngineElectric tile) {
        super(windowId, EnergyContainers.ENGINE_ELECTRIC.containerType(), player, tile, 8, 84);

        this.addSlot(new SlotFiltered(tile, InventoryEngineElectric.SLOT_BATTERY, 84, 53));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        PacketGuiUpdate packet = new PacketGuiUpdate(tile);
        sendPacketToListeners(packet);
    }

}
