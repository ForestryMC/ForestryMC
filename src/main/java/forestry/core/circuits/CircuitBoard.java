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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ICircuitSocketType;
import forestry.core.utils.Translator;

public class CircuitBoard implements ICircuitBoard {

	private final EnumCircuitBoardType type;
	@Nullable
	private final ICircuitLayout layout;
	private final ICircuit[] circuits;

	public CircuitBoard(EnumCircuitBoardType type, @Nullable ICircuitLayout layout, ICircuit[] circuits) {
		this.type = type;
		this.layout = layout;
		this.circuits = circuits;
	}

	public CircuitBoard(NBTTagCompound nbttagcompound) {
		type = EnumCircuitBoardType.values()[nbttagcompound.getShort("T")];

		// Layout
		ICircuitLayout layout = null;
		if (nbttagcompound.hasKey("LY")) {
			layout = ChipsetManager.circuitRegistry.getLayout(nbttagcompound.getString("LY"));
		}
		if (layout == null) {
			ChipsetManager.circuitRegistry.getDefaultLayout();
		}
		this.layout = layout;

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
	@SideOnly(Side.CLIENT)
	public int getPrimaryColor() {
		return type.getPrimaryColor();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSecondaryColor() {
		return type.getSecondaryColor();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addTooltip(List<String> list) {
		if (layout != null) {
			list.add(TextFormatting.GOLD + layout.getUsage() + ":");
			List<String> extendedTooltip = new ArrayList<>();
			for (ICircuit circuit : circuits) {
				if (circuit != null) {
					circuit.addTooltip(extendedTooltip);
				}
			}

			if (GuiScreen.isShiftKeyDown() || extendedTooltip.size() <= 4) {
				list.addAll(extendedTooltip);
			} else {
				list.add(TextFormatting.ITALIC + "<" + Translator.translateToLocal("for.gui.tooltip.tmi") + ">");
			}
		} else {
			int socketCount = type.getSockets();
			String localizationKey = "item.for.circuitboard.tooltip." + (socketCount == 1 ? "singular" : "plural");
			String tooltip = Translator.translateToLocalFormatted(localizationKey, type.getSockets());
			list.add(tooltip);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {

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
		return nbttagcompound;
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
		if (layout == null) {
			return CircuitSocketType.NONE;
		}
		return layout.getSocketType();
	}
}
