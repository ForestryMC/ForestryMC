/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
