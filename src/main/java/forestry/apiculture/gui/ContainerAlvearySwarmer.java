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
package forestry.apiculture.gui;

import forestry.api.apiculture.BeeManager;
import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotCustom;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class ContainerAlvearySwarmer extends ContainerForestry {

	public ContainerAlvearySwarmer(InventoryPlayer player, TileAlvearySwarmer tile) {
		super(tile);

		this.addSlot(new SlotCustom(tile, 0, 79, 52, getInducerItems()));
		this.addSlot(new SlotCustom(tile, 1, 100, 39, getInducerItems()));
		this.addSlot(new SlotCustom(tile, 2, 58, 39, getInducerItems()));
		this.addSlot(new SlotCustom(tile, 3, 79, 26, getInducerItems()));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 87 + i * 18));
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(player, i, 8 + i * 18, 145));

	}

	private Object[] getInducerItems() {
		Set<ItemStack> inducers = BeeManager.inducers.keySet();
		return inducers.toArray(new Object[inducers.size()]);
	}
}
