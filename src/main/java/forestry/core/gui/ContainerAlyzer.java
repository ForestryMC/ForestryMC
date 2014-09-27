package forestry.core.gui;

import forestry.core.gui.slots.SlotCustom;
import forestry.core.proxy.Proxies;
import forestry.core.utils.AlyzerInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerAlyzer extends ContainerItemInventory {

	private final AlyzerInventory inventory;

	protected ContainerAlyzer(InventoryPlayer inventoryplayer, AlyzerInventory inventory, Object[] acceptedEnergy, Object... acceptedSpecimens) {
		super(inventory, inventoryplayer.player);

		this.inventory = inventory;

		final int xPosLeftSlots = 223;

		// Energy
		this.addSlot(new SlotCustom(inventory, AlyzerInventory.SLOT_ENERGY, xPosLeftSlots, 8, acceptedEnergy));

		// Bee to analyze
		this.addSlot(new SlotCustom(inventory, AlyzerInventory.SLOT_SPECIMEN, xPosLeftSlots, 26, acceptedSpecimens));

		// Analyzed bee
		this.addSlot(new SlotCustom(inventory, AlyzerInventory.SLOT_ANALYZE_1, xPosLeftSlots, 57, acceptedSpecimens));
		this.addSlot(new SlotCustom(inventory, AlyzerInventory.SLOT_ANALYZE_2, xPosLeftSlots, 75, acceptedSpecimens));
		this.addSlot(new SlotCustom(inventory, AlyzerInventory.SLOT_ANALYZE_3, xPosLeftSlots, 93, acceptedSpecimens));
		this.addSlot(new SlotCustom(inventory, AlyzerInventory.SLOT_ANALYZE_4, xPosLeftSlots, 111, acceptedSpecimens));
		this.addSlot(new SlotCustom(inventory, AlyzerInventory.SLOT_ANALYZE_5, xPosLeftSlots, 129, acceptedSpecimens));

		final int xPosPlayerInv = 43;
		final int xSpacePlayerInv = 18;
		final int yPosPlayerInv= 156;
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

		if (!Proxies.common.isSimulating(entityplayer.worldObj))
			return;

		// Last slot is the energy slot, so we don't save that one.
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (i == AlyzerInventory.SLOT_ENERGY)
				continue;
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null)
				continue;

			Proxies.common.dropItemPlayer(entityplayer, stack);
			inventory.setInventorySlotContents(i, null);
		}

		inventory.onGuiSaved(entityplayer);
	}

	@Override
	protected boolean isAcceptedItem(EntityPlayer player, ItemStack stack) {
		return true;
	}

}
