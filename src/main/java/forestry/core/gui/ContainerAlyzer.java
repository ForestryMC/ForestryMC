package forestry.core.gui;

import forestry.core.features.CoreContainers;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.inventory.ItemInventoryAlyzer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

public class ContainerAlyzer extends ContainerItemInventory<ItemInventoryAlyzer> {

    public ContainerAlyzer(int windowId, ItemInventoryAlyzer inventory, PlayerEntity player) {
        super(windowId, inventory, player.inventory, 43, 156, CoreContainers.ALYZER.containerType());

        final int xPosLeftSlots = 223;

        this.addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ENERGY, xPosLeftSlots, 8));

        this.addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_SPECIMEN, xPosLeftSlots, 26));

        this.addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_1, xPosLeftSlots, 57));
        this.addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_2, xPosLeftSlots, 75));
        this.addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_3, xPosLeftSlots, 93));
        this.addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_4, xPosLeftSlots, 111));
        this.addSlot(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_5, xPosLeftSlots, 129));
    }

    public static ContainerAlyzer fromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        Hand hand = extraData.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
        PlayerEntity player = playerInv.player;
        ItemInventoryAlyzer inv = new ItemInventoryAlyzer(player, player.getHeldItem(hand));
        return new ContainerAlyzer(windowId, inv, player);
    }
}
