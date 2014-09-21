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

	public byte isRipeningUpdate = 0;
	
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
		if(leaves.hasFruit())
			leafState |= 1 << 0;
		if(leaves.isPollinated())
			leafState |= 1 << 1;
		textureIndexFancy = leaves.determineTextureIndex(true);
		textureIndexPlain = leaves.determineTextureIndex(false);
		textureIndexFruit = leaves.determineOverlayIndex();
		colourLeaves = leaves.determineFoliageColour();
		colourFruits = leaves.determineFruitColour();
	}

	public PacketLeafUpdate(TileLeaves leaves, int fruitColour) {
		super(PacketIds.LEAF_UPDATE, leaves);

		isRipeningUpdate = 1;
		colourFruits = fruitColour;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);

		data.writeByte(isRipeningUpdate);

		if(isRipeningUpdate()) {
			data.writeInt(colourFruits);
		} else {
			data.writeByte(leafState);
			data.writeShort(textureIndexFancy);
			data.writeShort(textureIndexPlain);
			data.writeShort(textureIndexFruit);
			data.writeInt(colourLeaves);
			data.writeInt(colourFruits);
		}
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		
		isRipeningUpdate = data.readByte();
		if(isRipeningUpdate()) {
			colourFruits = data.readInt();
		} else {
			leafState = data.readByte();
			textureIndexFancy = data.readShort();
			textureIndexPlain = data.readShort();
			textureIndexFruit = data.readShort();
			colourLeaves = data.readInt();
			colourFruits = data.readInt();
		}
	}
	
	public boolean isRipeningUpdate() {
		return isRipeningUpdate != 0;
	}
	
	public boolean isFruitLeaf() {
		return (leafState & (1 << 0)) > 0;
	}
	public boolean isPollinated() {
		return (leafState & (1 << 1)) > 0;
	}
}
