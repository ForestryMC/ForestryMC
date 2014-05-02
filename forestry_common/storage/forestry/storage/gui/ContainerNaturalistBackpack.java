/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.storage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.ISpeciesRoot;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.storage.GuiHandlerStorage.PagedInventory;

public class ContainerNaturalistBackpack extends ContainerItemInventory implements IGuiSelectable {

	private final PagedInventory inv;
	private ISpeciesRoot speciesRoot;

	public ContainerNaturalistBackpack(ISpeciesRoot speciesRoot, InventoryPlayer player, PagedInventory inventory, int page, int pageSize) {
		super(inventory, player.player);

		this.inv = inventory;
		this.speciesRoot = speciesRoot;

		// Inventory
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				addSlot(new SlotCustom(inventory, y + page * pageSize + x * 5, 100 + y * 18, 21 + x * 18, new Object[] { speciesRoot }));
			}
		}

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSecuredSlot(player, l1 + i1 * 9 + 9, 18 + l1 * 18, 120 + i1 * 18);
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSecuredSlot(player, j1, 18 + j1 * 18, 178);
		}
	}

	@Override
	public void purgeBag(EntityPlayer player) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null)
				continue;

			if (speciesRoot.isMember(stack))
				continue;

			Proxies.common.dropItemPlayer(player, stack);
			inventory.setInventorySlotContents(i, null);
		}
	}

	@Override
	protected boolean isAcceptedItem(EntityPlayer player, ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {
		inv.flipPage(player, packet.payload.intPayload[0]);
	}

	@Override
	public void setSelection(PacketUpdate packet) {
	}
}
