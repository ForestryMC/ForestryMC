package forestry.core.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;

import forestry.core.features.CoreContainers;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.inventory.ItemInventoryAlyzer;

public class ContainerAlyzer extends ContainerItemInventory<ItemInventoryAlyzer> {

	public static ContainerAlyzer fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
		InteractionHand hand = extraData.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		Player player = playerInv.player;
		ItemInventoryAlyzer inv = new ItemInventoryAlyzer(player, player.getItemInHand(hand));
		return new ContainerAlyzer(windowId, inv, player);
	}

	public ContainerAlyzer(int windowId, ItemInventoryAlyzer inventory, Player player) {
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
}
