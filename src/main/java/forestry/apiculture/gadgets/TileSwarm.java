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
package forestry.apiculture.gadgets;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import forestry.core.utils.InventoryAdapter;

public class TileSwarm extends TileEntity {

	public final InventoryAdapter contained = new InventoryAdapter(2, "Contained");

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
