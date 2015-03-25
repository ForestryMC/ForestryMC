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

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.ITree;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.core.network.PacketIds;
import forestry.plugins.PluginArboriculture;

public class PacketLeaf extends PacketTreeContainer {

	private static final short hasFruitFlag = 1;
	private static final short isPollinatedFlag = 1 << 1;

	private byte leafState = 0;
	private int colourFruits = -1;
	private String fruitAlleleUID;

	public PacketLeaf() {

	}

	public PacketLeaf(TileLeaves leaves) {
		super(PacketIds.LEAF, leaves);

		if (leaves.hasFruit()) {
			leafState |= hasFruitFlag;
			fruitAlleleUID = leaves.getTree().getGenome().getActiveAllele(EnumTreeChromosome.FRUITS).getUID();
			colourFruits = leaves.getFruitColour();
		}
		if (leaves.isPollinated()) {
			leafState |= isPollinatedFlag;
		}
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeByte(leafState);

		if (isFruitLeaf()) {
			data.writeUTF(fruitAlleleUID);
			data.writeInt(colourFruits);
		}
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		leafState = data.readByte();

		if (isFruitLeaf()) {
			fruitAlleleUID = data.readUTF();
			colourFruits = data.readInt();
		}
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

	@Override
	public ITree getTree() {
		IAllele[] treeTemplate = PluginArboriculture.treeInterface.getTemplate(speciesUID);
		if (treeTemplate == null) {
			return null;
		}

		if (fruitAlleleUID != null) {
			IAllele fruitAllele = AlleleManager.alleleRegistry.getAllele(fruitAlleleUID);
			if (fruitAllele != null) {
				treeTemplate[EnumTreeChromosome.FRUITS.ordinal()] = fruitAllele;
			}
		}

		ITree tree = PluginArboriculture.treeInterface.templateAsIndividual(treeTemplate);
		if (isPollinated()) {
			tree.mate(tree);
		}
		return tree;
	}
}
