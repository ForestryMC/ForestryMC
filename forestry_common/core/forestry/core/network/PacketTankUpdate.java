/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.network;

import net.minecraft.nbt.NBTTagCompound;

import forestry.core.utils.ForestryTank;

public class PacketTankUpdate extends PacketNBT {
	
	public PacketTankUpdate(int tankSlot, ForestryTank tank) {
		super(PacketIds.TANK_UPDATE);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("tank", (byte) tankSlot);
		nbt.setShort("capacity", (short) tank.getCapacity());
		tank.writeToNBT(nbt);
		this.nbttagcompound = nbt;
	}

	public PacketTankUpdate() {
	}

}
