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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITree;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.core.network.ForestryPacket;
import forestry.core.network.ILocatedPacket;
import forestry.core.network.PacketIds;

public class PacketLeafUpdate extends ForestryPacket implements ILocatedPacket {

	private static final short hasFruitFlag = 1;
	private static final short isPollinatedFlag = 1 << 1;

	private int posX, posY, posZ;

	private byte leafState = 0;
	private int colourFruits = -1;
	private String speciesUID = "";
	
	public PacketLeafUpdate() {
	}

	public PacketLeafUpdate(TileLeaves leaves) {
		super(PacketIds.LEAF_UPDATE);

		posX = leaves.getXCoord();
		posY = leaves.getYCoord();
		posZ = leaves.getZCoord();

		leafState = 0;
		if (leaves.hasFruit()) {
			leafState |= hasFruitFlag;
		}
		if (leaves.isPollinated()) {
			leafState |= isPollinatedFlag;
		}

		if (leaves.hasFruit()) {
			colourFruits = leaves.getFruitColour();
		}

		ITree tree = leaves.getTree();
		if (tree != null) {
			speciesUID = tree.getIdent();
		}
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeShort(posX);
		data.writeShort(posY);
		data.writeShort(posZ);
		data.writeByte(leafState);
		data.writeUTF(speciesUID);
		data.writeInt(colourFruits);
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		posX = data.readShort();
		posY = data.readShort();
		posZ = data.readShort();
		leafState = data.readByte();
		speciesUID = data.readUTF();
		colourFruits = data.readInt();
	}

	public boolean isFruitLeaf() {
		return (leafState & hasFruitFlag) > 0;
	}

	public boolean isPollinated() {
		return (leafState & isPollinatedFlag) > 0;
	}

	public int getColourFruits() {
		return colourFruits;
	}

	public String getSpeciesUID() {
		return speciesUID;
	}

	@Override
	public TileEntity getTarget(World world) {
		return world.getTileEntity(posX, posY, posZ);
	}
}
