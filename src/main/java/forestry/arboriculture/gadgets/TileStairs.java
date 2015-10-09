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

import javax.annotation.Nonnull;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

import forestry.arboriculture.WoodType;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketTileStream;
import forestry.core.proxy.Proxies;

public class TileStairs extends TileEntity implements IStreamable {

	@Nonnull
	private WoodType woodType = WoodType.LARCH;

	@Nonnull
	public WoodType getWoodType() {
		return this.woodType;
	}

	public void setWoodType(@Nonnull WoodType woodType) {
		this.woodType = woodType;
		sendNetworkUpdate();
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("WT")) {
			woodType = WoodType.VALUES[nbttagcompound.getShort("WT")];
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setShort("WT", (short) woodType.ordinal());
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
		return new PacketTileStream(this).getPacket();
	}

	public void sendNetworkUpdate() {
		Proxies.net.sendNetworkPacket(new PacketTileStream(this));
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(woodType.ordinal());
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		int woodTypeOrdinal = data.readVarInt();
		woodType = WoodType.values()[woodTypeOrdinal];
		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}
}
