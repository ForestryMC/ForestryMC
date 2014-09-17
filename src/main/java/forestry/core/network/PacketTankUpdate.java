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
package forestry.core.network;

import forestry.core.fluids.tanks.StandardTank;
import net.minecraft.nbt.NBTTagCompound;


public class PacketTankUpdate extends PacketNBT {
	
	public PacketTankUpdate(int tankSlot, StandardTank tank) {
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
