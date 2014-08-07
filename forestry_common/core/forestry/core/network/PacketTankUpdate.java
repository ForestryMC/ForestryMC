/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
