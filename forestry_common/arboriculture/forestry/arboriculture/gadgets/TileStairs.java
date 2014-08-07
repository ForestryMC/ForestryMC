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
package forestry.arboriculture.gadgets;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

import forestry.arboriculture.WoodType;
import forestry.core.network.ForestryPacket;
import forestry.core.network.INetworkedEntity;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketTileNBT;
import forestry.core.proxy.Proxies;

public class TileStairs extends TileEntity implements INetworkedEntity {

	private WoodType type;

	public WoodType getType() {
		return this.type;
	}

	public void setType(WoodType type) {
		this.type = type;
		sendNetworkUpdate();
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("WT"))
			type = WoodType.VALUES[nbttagcompound.getShort("WT")];
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		if (type != null)
			nbttagcompound.setShort("WT", (short) type.ordinal());
	}

	/* UPDATING */
	/**
	 * This doesn't use normal TE updates
	 */
	@Override
	public boolean canUpdate() {
		return false;
	}

	/* INETWORKEDENTITY */
	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileNBT(PacketIds.TILE_NBT, this).getPacket();
	}

	@Override
	public void sendNetworkUpdate() {
		Proxies.net.sendNetworkPacket(new PacketTileNBT(PacketIds.TILE_NBT, this), xCoord, yCoord, zCoord);
	}

	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		PacketTileNBT packet = (PacketTileNBT) packetRaw;
		this.readFromNBT(packet.getTagCompound());
		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}

}
