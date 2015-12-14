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
package forestry.storage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.core.gui.GuiHandler;
import forestry.core.gui.IPagedInventory;
import forestry.storage.items.ItemBackpackNaturalist;

public class ItemInventoryBackpackPaged extends ItemInventoryBackpack implements IPagedInventory {
	private final ItemBackpackNaturalist backpackNaturalist;

	public ItemInventoryBackpackPaged(EntityPlayer player, int size, ItemStack itemstack, ItemBackpackNaturalist backpackNaturalist) {
		super(player, size, itemstack);
		this.backpackNaturalist = backpackNaturalist;
	}

	@Override
	public void flipPage(EntityPlayer player, short page) {
		GuiHandler.openGui(player, backpackNaturalist, page);
	}
}
