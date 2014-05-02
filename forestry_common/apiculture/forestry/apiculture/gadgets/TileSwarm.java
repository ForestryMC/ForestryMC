/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.gadgets;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import forestry.core.utils.InventoryAdapter;

public class TileSwarm extends TileEntity {

	public InventoryAdapter contained = new InventoryAdapter(2, "Contained");

	@Override
	public boolean canUpdate() {
		return false;
	}

	public TileSwarm setContained(ItemStack[] bees) {
		for (ItemStack itemstack : bees)
			contained.addStack(itemstack, false, true);

		return this;
	}

	public boolean containsBees() {
		for (int i = 0; i < contained.getSizeInventory(); i++)
			if (contained.getStackInSlot(i) != null)
				return true;

		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		contained.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		contained.writeToNBT(nbttagcompound);
	}

}
