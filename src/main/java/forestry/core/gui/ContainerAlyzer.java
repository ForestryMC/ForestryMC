package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;

import forestry.core.gui.slots.SlotFiltered;
import forestry.core.inventory.ItemInventoryAlyzer;

public class ContainerAlyzer extends ContainerItemInventory<ItemInventoryAlyzer> {

	public ContainerAlyzer(ItemInventoryAlyzer inventory, EntityPlayer player) {
		super(inventory, player.inventory, 43, 156);

		final int xPosLeftSlots = 223;

		addSlotToContainer(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ENERGY, xPosLeftSlots, 8));

		addSlotToContainer(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_SPECIMEN, xPosLeftSlots, 26));

		addSlotToContainer(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_1, xPosLeftSlots, 57));
		addSlotToContainer(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_2, xPosLeftSlots, 75));
		addSlotToContainer(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_3, xPosLeftSlots, 93));
		addSlotToContainer(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_4, xPosLeftSlots, 111));
		addSlotToContainer(new SlotFiltered(inventory, ItemInventoryAlyzer.SLOT_ANALYZE_5, xPosLeftSlots, 129));
	}
}
