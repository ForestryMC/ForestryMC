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
package forestry.core.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

import forestry.core.features.CoreContainers;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.inventory.InventoryEscritoire;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.EscritoireGame;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileUtil;

public class ContainerEscritoire extends ContainerTile<TileEscritoire> implements IGuiSelectable {
    private long lastUpdate;

    //TODO duplicated code with every other ContainerTile, refactor at some point
    public static ContainerEscritoire fromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        TileEscritoire tile = TileUtil.getTile(playerInv.player.world, extraData.readBlockPos(), TileEscritoire.class);
        return new ContainerEscritoire(windowId, playerInv.player, tile);
    }

    public ContainerEscritoire(int id, PlayerEntity player, TileEscritoire tile) {
        super(id, CoreContainers.ESCRITOIRE.containerType(), player.inventory, tile, 34, 153);

        // Analyze slot
        addSlot(new SlotFiltered(this.tile, InventoryEscritoire.SLOT_ANALYZE, 97, 67).setPickupWatcher(this.tile).setStackLimit(1));

        for (int i = 0; i < InventoryEscritoire.SLOTS_INPUT_COUNT; i++) {
            addSlot(new SlotFiltered(this.tile, InventoryEscritoire.SLOT_INPUT_1 + i, 17, 49 + i * 18).setBlockedTexture("slots/blocked_2"));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                addSlot(new SlotOutput(this.tile, InventoryEscritoire.SLOT_RESULTS_1 + i * 2 + j, 177 + j * 18, 85 + i * 18));
            }
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        long gameLastUpdate = tile.getGame().getLastUpdate();
        if (lastUpdate != gameLastUpdate) {
            lastUpdate = gameLastUpdate;
            sendPacketToListeners(new PacketGuiUpdate(tile));
        }
    }

    @Override
    public void handleSelectionRequest(ServerPlayerEntity player, int primary, int secondary) {
        EscritoireGame.Status status = tile.getGame().getStatus();
        if (status != EscritoireGame.Status.PLAYING) {
            return;
        }

        if (primary == -1) {
            tile.probe();
        } else {
            tile.choose(player.getGameProfile(), primary);
        }
    }
}
