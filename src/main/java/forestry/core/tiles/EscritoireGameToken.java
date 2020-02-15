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
package forestry.core.tiles;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.INBTTagable;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.utils.ColourUtil;
import forestry.core.utils.StringUtil;

public class EscritoireGameToken implements INBTTagable, IStreamable {

	private enum State {
		UNREVEALED,// face down
		PROBED,    // shown by escritoire probe action
		SELECTED,  // selected by the user as the first half of a match
		MATCHED,   // successfully matched
		FAILED;    // failed to match
		public static final State[] VALUES = values();
	}

	private static final String[] OVERLAY_NONE = new String[0];
	private static final String[] OVERLAY_FAILED = new String[]{"errors/errored"};
	private static final String[] OVERLAY_SELECTED = new String[]{"errors/unknown"};

	private ItemStack tokenStack;
	private State state = State.UNREVEALED;

	@SuppressWarnings("unused")
	public EscritoireGameToken() {
		// required for IStreamable serialization
	}

	public EscritoireGameToken(ItemStack tokenStack) {
		this.tokenStack = tokenStack;
	}

	public EscritoireGameToken(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	public ItemStack getTokenStack() {
		return tokenStack;
	}

	public boolean isVisible() {
		return state != State.UNREVEALED;
	}

	public boolean isProbed() {
		return state == State.PROBED;
	}

	public boolean isMatched() {
		return state == State.MATCHED;
	}

	public boolean isSelected() {
		return state == State.SELECTED;
	}

	public void setFailed() {
		state = State.FAILED;
	}

	public void setProbed(boolean probed) {
		if (probed) {
			state = State.PROBED;
		} else {
			state = State.UNREVEALED;
		}
	}

	public void setSelected() {
		state = State.SELECTED;
	}

	public void setMatched() {
		state = State.MATCHED;
	}

	public int getTokenColour() {
		if (tokenStack == null || !isVisible()) {
			return 0xffffff;
		}

		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(tokenStack);
		int iconColor = individual.getGenome().getPrimary().getIconColour(0);

		if (state == State.MATCHED) {
			return ColourUtil.multiplyRGBComponents(iconColor, 0.7f);
		} else {
			return iconColor;
		}
	}

	public String getTooltip() {
		return tokenStack != null ? tokenStack.getDisplayName() : StringUtil.localize("gui.unknown");
	}

	public String[] getOverlayIcons() {
		switch (state) {
			case FAILED:
				return OVERLAY_FAILED;
			case SELECTED:
				return OVERLAY_SELECTED;
			default:
				return OVERLAY_NONE;
		}
	}

	public boolean matches(EscritoireGameToken other) {
		return ItemStack.areItemStacksEqual(tokenStack, other.getTokenStack());
	}

	/* INBTTagable */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKey("state")) {
			int stateOrdinal = nbttagcompound.getInteger("state");
			state = State.values()[stateOrdinal];
		}

		if (nbttagcompound.hasKey("tokenStack")) {
			tokenStack = ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("tokenStack"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("state", state.ordinal());

		if (tokenStack != null) {
			NBTTagCompound stackcompound = new NBTTagCompound();
			tokenStack.writeToNBT(stackcompound);
			nbttagcompound.setTag("tokenStack", stackcompound);
		}
	}

	/* IStreamable */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeEnum(state, State.VALUES);
		data.writeItemStack(tokenStack);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		state = data.readEnum(State.VALUES);
		tokenStack = data.readItemStack();
	}
}
