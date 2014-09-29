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
package forestry.core.gui.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.ContainerItemInventory;

public class SlotItemInventory extends Slot {

	private final EntityPlayer player;
	private ContainerItemInventory container;

	public SlotItemInventory(ContainerItemInventory container, IInventory inventory, EntityPlayer player, int par2, int par3, int par4) {
		super(inventory, par2, par3, par4);
		this.container = container;
		this.player = player;
	}

	@Override
	public void onSlotChange(ItemStack par1ItemStack, ItemStack par2ItemStack) {
		super.onSlotChange(par1ItemStack, par2ItemStack);
		container.saveInventory(player);
	}

}
