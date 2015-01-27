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
package forestry.arboriculture.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import forestry.arboriculture.gadgets.TileLeaves;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketTileNBT;

public class PacketLeafUpdate extends PacketTileNBT {

	private static short hasFruitFlag = 1;
	private static short isPollinatedFlag = 1 << 1;

	private byte leafState = 0;
	public short textureIndexFancy;
	public short textureIndexPlain;
	public short textureIndexFruit;
	
	public int colourLeaves;
	public int colourFruits;
	
	public PacketLeafUpdate() {
	}

	public PacketLeafUpdate(TileLeaves leaves) {
		super(PacketIds.LEAF_UPDATE, leaves);

		leafState = 0;
		if (leaves.hasFruit()) {
			leafState |= hasFruitFlag;
		}
		if (leaves.isPollinated()) {
			leafState |= isPollinatedFlag;
		}
		textureIndexFancy = leaves.determineTextureIndex(true);
		textureIndexPlain = leaves.determineTextureIndex(false);
		textureIndexFruit = leaves.determineOverlayIndex();
		colourLeaves = leaves.determineFoliageColour();
		colourFruits = leaves.determineFruitColour();
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);

		data.writeByte(leafState);
		data.writeShort(textureIndexFancy);
		data.writeShort(textureIndexPlain);
		data.writeShort(textureIndexFruit);
		data.writeInt(colourLeaves);
		data.writeInt(colourFruits);
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);

		leafState = data.readByte();
		textureIndexFancy = data.readShort();
		textureIndexPlain = data.readShort();
		textureIndexFruit = data.readShort();
		colourLeaves = data.readInt();
		colourFruits = data.readInt();
	}

	public boolean isFruitLeaf() {
		return (leafState & hasFruitFlag) > 0;
	}

	public boolean isPollinated() {
		return (leafState & isPollinatedFlag) > 0;
	}
}
