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
package forestry.pipes.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;

public class PacketGenomeFilterChange extends PacketCoordinates {

	private int orientation;
	private int pattern;
	private int allele;
	private String species;

	public PacketGenomeFilterChange(DataInputStream data) throws IOException {
		super(data);
	}

	public PacketGenomeFilterChange(TileEntity tile, ForgeDirection orientation, int pattern, int allele, IAllele species) {
		super(PacketId.PROP_SEND_FILTER_CHANGE_GENOME, tile);

		this.orientation = orientation.ordinal();
		this.pattern = pattern;
		this.allele = allele;
		if (species != null) {
			this.species = species.getUID();
		} else {
			this.species = "";
		}
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeShort(orientation);
		data.writeShort(pattern);
		data.writeShort(allele);
		data.writeUTF(species);
	}

	@Override
	protected void readData(DataInputStream data) throws IOException {
		super.readData(data);
		orientation = data.readShort();
		pattern = data.readShort();
		allele = data.readShort();
		species = data.readUTF();
	}

	public int getOrientation() {
		return orientation;
	}

	public int getPattern() {
		return pattern;
	}

	public int getAllele() {
		return allele;
	}

	public IAllele getSpecies() {
		if (species.length() == 0) {
			return null;
		}
		return AlleleManager.alleleRegistry.getAllele(species);
	}
}
