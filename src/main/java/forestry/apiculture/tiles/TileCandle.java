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
package forestry.apiculture.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

import forestry.apiculture.network.packets.PacketCandleUpdate;

public class TileCandle extends TileEntity {
	private int colour;
	private boolean lit;

	// This is a non-ticking Tile Entity.
	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public Packet getDescriptionPacket() {
		PacketCandleUpdate updateCandle = new PacketCandleUpdate(this);
		return updateCandle.getPacket();
	}

	public void onPacketUpdate(int colour, boolean isLit) {
		this.colour = colour;
		this.lit = isLit;
	}

	@Override
	public void readFromNBT(NBTTagCompound tagRoot) {
		super.readFromNBT(tagRoot);
		colour = tagRoot.getInteger("colour");
		lit = tagRoot.getBoolean("lit");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagRoot) {
		super.writeToNBT(tagRoot);
		tagRoot.setInteger("colour", this.colour);
		tagRoot.setBoolean("lit", this.lit);
	}

	public boolean isLit() {
		return lit;
	}

	public void setLit(boolean lit) {
		this.lit = lit;
	}

	public int getColour() {
		return colour;
	}

	public void setColour(int value) {
		this.colour = value;
	}

	public void addColour(int colour2) {
		int[] myColour = fromIntColour(this.colour);
		int[] addColour = fromIntColour(colour2);
		this.colour = toIntColour((addColour[0] + myColour[0]) / 2, (addColour[0] + myColour[0]) / 2, (addColour[2] + myColour[2]) / 2);
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
