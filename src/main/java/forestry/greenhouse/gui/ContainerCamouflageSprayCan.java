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
package forestry.greenhouse.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.gui.ContainerItemInventory;
import forestry.greenhouse.inventory.ItemInventoryCamouflageSprayCan;

public class ContainerCamouflageSprayCan extends ContainerItemInventory<ItemInventoryCamouflageSprayCan> {

	public ContainerCamouflageSprayCan(ItemInventoryCamouflageSprayCan inventory, InventoryPlayer playerInventory) {
		super(inventory, playerInventory, 8, 84);
	}

}
