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
package forestry.core.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.network.GuiId;

public class ItemWithGui extends ItemForestry {
	private final GuiId guiId;

	public ItemWithGui(GuiId guiId) {
		this.guiId = guiId;
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			openGui(world, entityplayer);
		}

		return itemstack;
	}

	protected final void openGui(World world, EntityPlayer entityplayer) {
		entityplayer.openGui(ForestryAPI.instance, guiId.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY, (int) entityplayer.posZ);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack itemstack, EntityPlayer player) {
		if (itemstack != null &&
				player instanceof EntityPlayerMP &&
				player.openContainer instanceof ContainerItemInventory) {
			player.closeScreen();
		}

		return super.onDroppedByPlayer(itemstack, player);
	}
}
