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
package forestry.greenhouse.items;

import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.core.utils.Translator;

public enum ScreenMode {
	NONE(ScreenStep.EDGE_0, ScreenStep.EDGE_1, ScreenStep.HEIGHT, ScreenStep.DEPTH), EDGES(ScreenStep.EDGE_0, ScreenStep.EDGE_1), PREVIEW(ScreenStep.NONE);

	public static final String NBT_KEY = "mode";

	private ScreenStep[] steps;

	ScreenMode(ScreenStep... steps) {
		this.steps = steps;
	}

	public static ScreenMode getMode(int ordinal) {
		if (ordinal >= values().length) {
			ordinal = 0;
		}
		return values()[ordinal];
	}

	public static ScreenMode get(ItemStack itemStack) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			itemStack.setTagCompound(nbtTagCompound = new NBTTagCompound());
		}
		if (!nbtTagCompound.hasKey(NBT_KEY)) {
			nbtTagCompound.setByte(NBT_KEY, (byte) NONE.ordinal());
		}
		return getMode(nbtTagCompound.getByte(NBT_KEY));
	}

	public static void set(ItemStack itemStack, ScreenMode mode) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			itemStack.setTagCompound(nbtTagCompound = new NBTTagCompound());
		}
		nbtTagCompound.setByte(NBT_KEY, (byte) mode.ordinal());
	}

	public ScreenStep[] getSteps() {
		return steps;
	}

	public ScreenMode getNextMode() {
		return getMode(ordinal() + 1);
	}

	public ScreenStep getFirstStep() {
		return steps[0];
	}

	public ScreenStep getNextStep(ScreenStep ScreenStep) {
		int ordinal = ScreenStep.ordinal() + 1;
		if (ordinal >= steps.length) {
			ordinal = 0;
		}
		return steps[ordinal];
	}

	public String getDisplayName() {
		return Translator.translateToLocal("for.greenhouse_screen.mode." + getName() + ".name");
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
