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
package forestry.core.circuits;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ICircuitSocketType;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class CircuitBoard<T> implements ICircuitBoard {

	private EnumCircuitBoardType type;
	private ICircuitLayout layout;
	private ICircuit[] circuits;

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
		return type.getPrimaryColor();
	}

	@Override
	public int getSecondaryColor() {
		return type.getSecondaryColor();
	}

	@Override
	public void addTooltip(List<String> list) {
		if (layout != null) {
			list.add(EnumChatFormatting.GOLD + layout.getUsage() + ":");
		}

		List<String> extendedTooltip = new ArrayList<>();
		for (ICircuit circuit : circuits) {
			if (circuit != null) {
				circuit.addTooltip(extendedTooltip);
			}
		}

		if (Proxies.common.isShiftDown() || extendedTooltip.size() <= 4) {
			list.addAll(extendedTooltip);
		} else {
			list.add(EnumChatFormatting.ITALIC + "<" + StringUtil.localize("gui.tooltip.tmi") + ">");
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		type = EnumCircuitBoardType.values()[nbttagcompound.getShort("T")];

		// Layout
		if (nbttagcompound.hasKey("LY")) {
			layout = ChipsetManager.circuitRegistry.getLayout(nbttagcompound.getString("LY"));
		}
		if (layout == null) {
			ChipsetManager.circuitRegistry.getDefaultLayout();
		}

		circuits = new ICircuit[4];

		for (int i = 0; i < 4; i++) {
			if (!nbttagcompound.hasKey("CA.I" + i)) {
				continue;
			}
			ICircuit circuit = ChipsetManager.circuitRegistry.getCircuit(nbttagcompound.getString("CA.I" + i));
			if (circuit != null) {
				circuits[i] = circuit;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound.setShort("T", (short) type.ordinal());

		// Layout
		if (layout != null) {
			nbttagcompound.setString("LY", layout.getUID());
		}

		// Circuits
		for (int i = 0; i < circuits.length; i++) {
			ICircuit circuit = circuits[i];
			if (circuit == null) {
				continue;
			}

			nbttagcompound.setString("CA.I" + i, circuit.getUID());
		}

	}

	@Override
	public void onInsertion(Object tile) {
		for (int i = 0; i < circuits.length; i++) {
			ICircuit circuit = circuits[i];
			if (circuit == null) {
				continue;
			}
			circuit.onInsertion(i, tile);
		}
	}

	@Override
	public void onLoad(Object tile) {
		for (int i = 0; i < circuits.length; i++) {
			ICircuit circuit = circuits[i];
			if (circuit == null) {
				continue;
			}
			circuit.onLoad(i, tile);
		}
	}

	@Override
	public void onRemoval(Object tile) {
		for (int i = 0; i < circuits.length; i++) {
			ICircuit circuit = circuits[i];
			if (circuit == null) {
				continue;
			}
			circuit.onRemoval(i, tile);
		}
	}

	@Override
	public void onTick(Object tile) {
		for (int i = 0; i < circuits.length; i++) {
			ICircuit circuit = circuits[i];
			if (circuit == null) {
				continue;
			}
			circuit.onTick(i, tile);
		}
	}

	@Override
	public ICircuit[] getCircuits() {
		return circuits;
	}

	@Override
	public ICircuitSocketType getSocketType() {
		return layout.getSocketType();
	}
}
