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
package forestry.storage.gui;

import forestry.api.genetics.ISpeciesRoot;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.storage.GuiHandlerStorage.PagedInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

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
				addSlot(new SlotCustom(inventory, y + page * pageSize + x * 5, 100 + y * 18, 21 + x * 18, speciesRoot));
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
