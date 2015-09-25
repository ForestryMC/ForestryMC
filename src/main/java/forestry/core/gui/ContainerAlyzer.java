package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.core.gui.slots.SlotFiltered;
import forestry.core.items.ItemAlyzer;
import forestry.core.proxy.Proxies;

public class ContainerAlyzer extends ContainerItemInventory<ItemAlyzer.AlyzerInventory> {

	public ContainerAlyzer(ItemAlyzer.AlyzerInventory inventory, EntityPlayer player) {
		super(inventory, player.inventory, 43, 156);

		final int xPosLeftSlots = 223;

		// Energy
		this.addSlotToContainer(new SlotFiltered(inventory, ItemAlyzer.AlyzerInventory.SLOT_ENERGY, xPosLeftSlots, 8));

		// Bee to analyze
		this.addSlotToContainer(new SlotFiltered(inventory, ItemAlyzer.AlyzerInventory.SLOT_SPECIMEN, xPosLeftSlots, 26));

		// Analyzed bee
		this.addSlotToContainer(new SlotFiltered(inventory, ItemAlyzer.AlyzerInventory.SLOT_ANALYZE_1, xPosLeftSlots, 57));
		this.addSlotToContainer(new SlotFiltered(inventory, ItemAlyzer.AlyzerInventory.SLOT_ANALYZE_2, xPosLeftSlots, 75));
		this.addSlotToContainer(new SlotFiltered(inventory, ItemAlyzer.AlyzerInventory.SLOT_ANALYZE_3, xPosLeftSlots, 93));
		this.addSlotToContainer(new SlotFiltered(inventory, ItemAlyzer.AlyzerInventory.SLOT_ANALYZE_4, xPosLeftSlots, 111));
		this.addSlotToContainer(new SlotFiltered(inventory, ItemAlyzer.AlyzerInventory.SLOT_ANALYZE_5, xPosLeftSlots, 129));
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (entityplayer.worldObj.isRemote) {
			return;
		}

		// Last slot is the energy slot, so we don't save that one.
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (i == ItemAlyzer.AlyzerInventory.SLOT_ENERGY) {
				continue;
			}
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null) {
				continue;
			}

			Proxies.common.dropItemPlayer(entityplayer, stack);
			inventory.setInventorySlotContents(i, null);
		}
	}

}
