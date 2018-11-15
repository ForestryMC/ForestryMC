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

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.INbtWritable;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.ColourUtil;
import forestry.core.utils.Translator;

public class EscritoireGameToken implements INbtWritable, IStreamable {

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

	@Nullable
	private IIndividual tokenIndividual;
	private ItemStack tokenStack = ItemStack.EMPTY;

	private State state = State.UNREVEALED;

	public EscritoireGameToken(PacketBufferForestry data) {
		readData(data);
	}

	public EscritoireGameToken(String speciesUid) {
		setTokenSpecies(speciesUid);
	}

	public EscritoireGameToken(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKey("state")) {
			int stateOrdinal = nbttagcompound.getInteger("state");
			state = State.values()[stateOrdinal];
		}

		if (nbttagcompound.hasKey("tokenSpecies")) {
			String speciesUid = nbttagcompound.getString("tokenSpecies");
			setTokenSpecies(speciesUid);
		}
	}

	private void setTokenSpecies(String speciesUid) {
		IAllele allele = AlleleManager.alleleRegistry.getAllele(speciesUid);
		if (allele instanceof IAlleleSpecies) {
			IAlleleSpecies species = (IAlleleSpecies) allele;
			ISpeciesRoot root = species.getRoot();
			IAllele[] template = root.getTemplate(species);
			this.tokenIndividual = root.templateAsIndividual(template);
			this.tokenStack = root.getMemberStack(this.tokenIndividual, root.getIconType());
		}
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
		if (tokenIndividual == null || !isVisible()) {
			return 0xffffff;
		}

		int iconColor = tokenIndividual.getGenome().getPrimary().getSpriteColour(0);

		if (state == State.MATCHED) {
			return ColourUtil.multiplyRGBComponents(iconColor, 0.7f);
		} else {
			return iconColor;
		}
	}


	public String getTooltip() {
		return !tokenStack.isEmpty() ? tokenStack.getDisplayName() : Translator.translateToLocal("for.gui.unknown");
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

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("state", state.ordinal());

		if (tokenIndividual != null) {
			nbttagcompound.setString("tokenSpecies", tokenIndividual.getGenome().getPrimary().getUID());
		}
		return nbttagcompound;
	}

	/* IStreamable */
	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeEnum(state, State.VALUES);
		if (tokenIndividual != null) {
			data.writeBoolean(true);
			data.writeString(tokenIndividual.getGenome().getPrimary().getUID());
		} else {
			data.writeBoolean(false);
		}
	}

	@Override
	public void readData(PacketBufferForestry data) {
		state = data.readEnum(State.VALUES);
		if (data.readBoolean()) {
			String speciesUid = data.readString();
			setTokenSpecies(speciesUid);
		}
	}
}
