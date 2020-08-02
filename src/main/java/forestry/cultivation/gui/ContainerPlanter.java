package forestry.cultivation.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.gui.slots.SlotOutput;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.TileUtil;
import forestry.cultivation.features.CultivationContainers;
import forestry.cultivation.inventory.InventoryPlanter;
import forestry.cultivation.tiles.TilePlanter;

public class ContainerPlanter extends ContainerLiquidTanks<TilePlanter> {

    public static ContainerPlanter fromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        TilePlanter planter = TileUtil.getTile(playerInv.player.world, extraData.readBlockPos(), TilePlanter.class);
        return new ContainerPlanter(windowId, playerInv, planter);    //TODO what to do if Planter null
    }

    public ContainerPlanter(int windowId, PlayerInventory playerInventory, TilePlanter tileForestry) {
        super(windowId, CultivationContainers.PLANTER.containerType(), playerInventory, tileForestry, 21, 110);

        // Resources
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                addSlot(new SlotFiltered(tile.getInternalInventory(), InventoryPlanter.SLOT_RESOURCES_1 + j + i * 2, 11 + j * 18, 65 + i * 18));
            }
        }

        // Germlings
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                addSlot(new SlotFiltered(tile.getInternalInventory(), InventoryPlanter.SLOT_GERMLINGS_1 + j + i * 2, 71 + j * 18, 65 + i * 18));
            }
        }

        // Production
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                addSlot(new SlotOutput(tile.getInternalInventory(), InventoryPlanter.SLOT_PRODUCTION_1 + j + i * 2, 131 + j * 18, 65 + i * 18));
            }
        }

        // Fertilizer
        addSlot(new SlotFiltered(tile.getInternalInventory(), InventoryPlanter.SLOT_FERTILIZER, 83, 22));
        // Can Slot
        addSlot(new SlotLiquidIn(tile.getInternalInventory(), InventoryPlanter.SLOT_CAN, 178, 18));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        PacketGuiUpdate packet = new PacketGuiUpdate(tile);
        sendPacketToListeners(packet);
    }
}
