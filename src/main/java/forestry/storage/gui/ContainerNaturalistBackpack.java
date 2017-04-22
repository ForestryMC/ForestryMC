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

import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.core.gui.IGuiSelectable;
import forestry.storage.inventory.ItemInventoryBackpackPaged;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerNaturalistBackpack extends ContainerItemInventory<ItemInventoryBackpackPaged> implements IGuiSelectable {

	public ContainerNaturalistBackpack(EntityPlayer player, ItemInventoryBackpackPaged inventory, int selectedPage) {
		super(inventory, player.inventory, 18, 120);

		ContainerNaturalistInventory.addInventory(this, inventory, selectedPage);
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, int primary, int secondary) {
		inventory.flipPage(player, (short) primary);
	}
}
