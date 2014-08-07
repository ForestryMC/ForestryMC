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
package forestry.apiculture.gadgets;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileCandle extends TileEntity {
	private int colour;

	// This is a non-ticking Tile Entity.
	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

	@Override
	public void readFromNBT(NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		this.setColour(tagRoot.getInteger("colour"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		tagRoot.setInteger("colour", this.colour);
	}

	public int getColour() {
		return this.colour;
	}

	public void setColour(int value) {
		this.colour = value;
	}

	public void setColour(int red, int green, int blue) {
		this.colour = toIntColour(red, green, blue);
	}

	/*public void setColour(int[] cs) {
		this.colour = toIntColour(cs[0], cs[1], cs[2]);
	}*/

	public void addColour(int red, int green, int blue) {
		int[] myColour = fromIntColour(this.colour);
		this.colour = toIntColour((red + myColour[0]) / 2, (green + myColour[0]) / 2, (blue + myColour[2]) / 2);
	}

	private static int[] fromIntColour(int value) {
		int[] cs = new int[3];
		cs[0] = (value & 0xff0000) >> 16;
		cs[1] = (value & 0x00ff00) >> 8;
		cs[2] = value & 0x0000ff;
		return cs;
	}

	private static int toIntColour(int r, int g, int b) {
		return r << 16 | g << 8 | b;
	}
}
