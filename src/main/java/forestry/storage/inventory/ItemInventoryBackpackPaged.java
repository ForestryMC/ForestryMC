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

import forestry.api.core.ForestryAPI;
import forestry.core.GuiHandlerBase;
import forestry.core.gui.IPagedInventory;
import forestry.core.network.GuiId;

public class ItemInventoryBackpackPaged extends ItemInventoryBackpack implements IPagedInventory {
	private final GuiId guiId;

	public ItemInventoryBackpackPaged(EntityPlayer player, int size, ItemStack itemstack, GuiId guiId) {
		super(player, size, itemstack);
		this.guiId = guiId;
	}

	@Override
	public void flipPage(EntityPlayer player, int page) {
		player.openGui(ForestryAPI.instance, GuiHandlerBase.encodeGuiData(guiId, page), player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}
}
