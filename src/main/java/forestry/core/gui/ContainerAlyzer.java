package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import forestry.core.gui.slots.SlotFiltered;
import forestry.core.inventory.AlyzerInventory;
import forestry.core.proxy.Proxies;

public class ContainerAlyzer extends ContainerItemInventory {

	private final AlyzerInventory inventory;

	public ContainerAlyzer(AlyzerInventory inventory, EntityPlayer player) {
		super(inventory, player);

		InventoryPlayer inventoryplayer = player.inventory;
		this.inventory = inventory;

		final int xPosLeftSlots = 223;

		// Energy
		this.addSlotToContainer(new SlotFiltered(inventory, AlyzerInventory.SLOT_ENERGY, xPosLeftSlots, 8));

		// Bee to analyze
		this.addSlotToContainer(new SlotFiltered(inventory, AlyzerInventory.SLOT_SPECIMEN, xPosLeftSlots, 26));

		// Analyzed bee
		this.addSlotToContainer(new SlotFiltered(inventory, AlyzerInventory.SLOT_ANALYZE_1, xPosLeftSlots, 57));
		this.addSlotToContainer(new SlotFiltered(inventory, AlyzerInventory.SLOT_ANALYZE_2, xPosLeftSlots, 75));
		this.addSlotToContainer(new SlotFiltered(inventory, AlyzerInventory.SLOT_ANALYZE_3, xPosLeftSlots, 93));
		this.addSlotToContainer(new SlotFiltered(inventory, AlyzerInventory.SLOT_ANALYZE_4, xPosLeftSlots, 111));
		this.addSlotToContainer(new SlotFiltered(inventory, AlyzerInventory.SLOT_ANALYZE_5, xPosLeftSlots, 129));

		final int xPosPlayerInv = 43;
		final int xSpacePlayerInv = 18;
		final int yPosPlayerInv = 156;
		final int ySpacePlayerInv = 18;

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				int slot = l1 + i1 * 9 + 9;
				int x = xPosPlayerInv + (l1 * xSpacePlayerInv);
				int y = yPosPlayerInv + (i1 * ySpacePlayerInv);
				addSecuredSlot(inventoryplayer, slot, x, y);
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			int x = xPosPlayerInv + (j1 * xSpacePlayerInv);
			addSecuredSlot(inventoryplayer, j1, x, 214);
		}

	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (!Proxies.common.isSimulating(entityplayer.worldObj)) {
			return;
		}

		// Last slot is the energy slot, so we don't save that one.
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (i == AlyzerInventory.SLOT_ENERGY) {
				continue;
			}
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null) {
				continue;
			}

			Proxies.common.dropItemPlayer(entityplayer, stack);
			inventory.setInventorySlotContents(i, null);
		}

		inventory.onGuiSaved(entityplayer);
	}

}
