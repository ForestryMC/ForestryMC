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
package forestry.core.circuits;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;

public class CircuitBoard implements ICircuitBoard {

	EnumCircuitBoardType type;
	ICircuitLayout layout;
	ICircuit[] circuits;

	public CircuitBoard(EnumCircuitBoardType type, ICircuitLayout layout, ICircuit[] circuits) {
		this.type = type;
		this.layout = layout;
		this.circuits = circuits;
	}

	public CircuitBoard(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	@Override
	public int getPrimaryColor() {
		return type.primaryColor;
	}

	@Override
	public int getSecondaryColor() {
		return type.secondaryColor;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addTooltip(List list) {
		if (layout != null)
			list.add("\u00A76" + layout.getUsage() + ":");

		for (ICircuit circuit : circuits)
			if (circuit != null)
				circuit.addTooltip(list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		type = EnumCircuitBoardType.values()[nbttagcompound.getShort("T")];

		// Layout
		if (nbttagcompound.hasKey("LY"))
			layout = ChipsetManager.circuitRegistry.getLayout(nbttagcompound.getString("LY"));
		if (layout == null)
			ChipsetManager.circuitRegistry.getDefaultLayout();

		// FIXME: Legacy I
		ArrayList<ICircuit> readcircuits = new ArrayList<ICircuit>();
		if (nbttagcompound.hasKey("CS")) {

			NBTTagList nbttaglist = nbttagcompound.getTagList("CS", 10);
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				readcircuits.add(ChipsetManager.circuitRegistry.getFromLegacyMap(nbttagcompound1.getInteger("I")));
			}

			circuits = readcircuits.toArray(new ICircuit[0]);
			return;

		}

		// FIXME: Legacy II
		if (nbttagcompound.hasKey("CL")) {
			NBTTagList nbttaglist = nbttagcompound.getTagList("CL", 10);
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				readcircuits.add(ChipsetManager.circuitRegistry.getCircuit(nbttagcompound1.getString("I")));
			}

			circuits = readcircuits.toArray(new ICircuit[0]);
			return;
		}

		// New
		if (circuits != null)
			return;

		circuits = new ICircuit[4];
		for (int i = 0; i < 4; i++) {
			if (!nbttagcompound.hasKey("CA.I" + i))
				continue;
			circuits[i] = ChipsetManager.circuitRegistry.getCircuit(nbttagcompound.getString("CA.I" + i));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound.setShort("T", (short) type.ordinal());

		// Layout
		if (layout != null)
			nbttagcompound.setString("LY", layout.getUID());

		// Circuits
		for (int i = 0; i < circuits.length; i++) {
			ICircuit circuit = circuits[i];
			if (circuit == null)
				continue;

			nbttagcompound.setString("CA.I" + i, circuit.getUID());
		}

	}

	@Override
	public void onInsertion(TileEntity tile) {
		for (int i = 0; i < circuits.length; i++) {
			if (circuits[i] == null)
				continue;
			circuits[i].onInsertion(i, tile);
		}
	}

	@Override
	public void onLoad(TileEntity tile) {
		for (int i = 0; i < circuits.length; i++) {
			if (circuits[i] == null)
				continue;
			circuits[i].onLoad(i, tile);
		}
	}

	@Override
	public void onRemoval(TileEntity tile) {
		for (int i = 0; i < circuits.length; i++) {
			if (circuits[i] == null)
				continue;
			circuits[i].onRemoval(i, tile);
		}
	}

	@Override
	public void onTick(TileEntity tile) {
		for (int i = 0; i < circuits.length; i++) {
			if (circuits[i] == null)
				continue;
			circuits[i].onTick(i, tile);
		}
	}

	@Override
	public ICircuit[] getCircuits() {
		return circuits;
	}
}
