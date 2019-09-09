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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ICircuitSocketType;

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

	public CircuitBoard(CompoundNBT CompoundNBT) {
		type = EnumCircuitBoardType.values()[CompoundNBT.getShort("T")];

		// Layout
		ICircuitLayout layout = null;
		if (CompoundNBT.contains("LY")) {
			layout = ChipsetManager.circuitRegistry.getLayout(CompoundNBT.getString("LY"));
		}
		if (layout == null) {
			ChipsetManager.circuitRegistry.getDefaultLayout();
		}
		this.layout = layout;

		circuits = new ICircuit[4];

		for (int i = 0; i < 4; i++) {
			if (!CompoundNBT.contains("CA.I" + i)) {
				continue;
			}
			ICircuit circuit = ChipsetManager.circuitRegistry.getCircuit(CompoundNBT.getString("CA.I" + i));
			if (circuit != null) {
				circuits[i] = circuit;
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getPrimaryColor() {
		return type.getPrimaryColor();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getSecondaryColor() {
		return type.getSecondaryColor();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addTooltip(List<ITextComponent> list) {
		if (layout != null) {
			list.add(new StringTextComponent(layout.getUsage() + ":").setStyle((new Style()).setColor(TextFormatting.GOLD)));
			List<ITextComponent> extendedTooltip = new ArrayList<>();
			for (ICircuit circuit : circuits) {
				if (circuit != null) {
					circuit.addTooltip(extendedTooltip);
				}
			}

			if (Screen.hasShiftDown() || extendedTooltip.size() <= 4) {
				list.addAll(extendedTooltip);
			} else {
				list.add(new StringTextComponent("<").setStyle((new Style()).setUnderlined(true).setColor(TextFormatting.GRAY))
					.appendSibling(new TranslationTextComponent("for.gui.tooltip.tmi"))
					.appendSibling(new StringTextComponent(">")));
			}
		} else {
			int socketCount = type.getSockets();
			String localizationKey = "item.forestry.circuit_board.tooltip." + (socketCount == 1 ? "singular" : "plural");
			list.add(new TranslationTextComponent(localizationKey, type.getSockets()).applyTextStyle(TextFormatting.GRAY));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT CompoundNBT) {

		CompoundNBT.putShort("T", (short) type.ordinal());

		// Layout
		if (layout != null) {
			CompoundNBT.putString("LY", layout.getUID());
		}

		// Circuits
		for (int i = 0; i < circuits.length; i++) {
			ICircuit circuit = circuits[i];
			if (circuit == null) {
				continue;
			}

			CompoundNBT.putString("CA.I" + i, circuit.getUID());
		}
		return CompoundNBT;
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
