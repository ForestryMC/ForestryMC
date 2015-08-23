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
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;

import forestry.core.inventory.InvTools;
import forestry.core.inventory.InventoryAdapter;

public class TileSwarm extends TileEntity implements IUpdatePlayerListBox {

	public final InventoryAdapter contained = new InventoryAdapter(2, "Contained");
	
	// Hack to make sure that hives glow.
	// TODO: remove when Mojang fixes this bug: https://bugs.mojang.com/browse/MC-3329
	// Hives should not need to tick normally.
	private boolean updatedLight;

	@Override
	public void update() {

		if (worldObj.isRemote && !updatedLight && worldObj.getWorldTime() % 20 == 0) {
			updatedLight = worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);
		}
	}

	public TileSwarm setContained(ItemStack[] bees) {
		for (ItemStack itemstack : bees) {
			InvTools.addStack(contained, itemstack, false, true);
		}

		return this;
	}

	public boolean containsBees() {
		for (int i = 0; i < contained.getSizeInventory(); i++) {
			if (contained.getStackInSlot(i) != null) {
				return true;
			}
		}

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
