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
import net.minecraft.util.math.BlockPos;

import forestry.api.greenhouse.IGreenhouseSettings;
import forestry.api.greenhouse.Position2D;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.utils.Translator;

public enum ScreenStep {
	EDGE_0, EDGE_1, HEIGHT, DEPTH, NONE;

	public static final String NBT_KEY = "step";

	public static ScreenStep getStep(int ordinal) {
		if (ordinal >= values().length) {
			ordinal = 0;
		}
		return values()[ordinal];
	}

	public static ScreenStep get(ItemStack itemStack) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			itemStack.setTagCompound(nbtTagCompound = new NBTTagCompound());
		}
		if (!nbtTagCompound.hasKey(NBT_KEY)) {
			ScreenMode mode = ScreenMode.get(itemStack);
			nbtTagCompound.setByte(NBT_KEY, (byte) mode.getFirstStep().ordinal());
		}
		return ScreenStep.getStep(nbtTagCompound.getByte(NBT_KEY));
	}

	public static void set(ItemStack itemStack, ScreenStep step) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null) {
			itemStack.setTagCompound(nbtTagCompound = new NBTTagCompound());
		}
		nbtTagCompound.setByte(NBT_KEY, (byte) step.ordinal());
	}

	public void apply(IGreenhouseSettings settings, IGreenhouseController controller, BlockPos position) {
		BlockPos centerPosition = controller.getCenterCoordinates();
		BlockPos edgePosition = position.subtract(centerPosition);
		switch (this) {
			case EDGE_0:
				settings.setEdge(0, new Position2D(edgePosition));
				break;
			case EDGE_1:
				settings.setEdge(1, new Position2D(edgePosition));
				break;
			case HEIGHT:
				int height = position.getY() - centerPosition.getY() + 1;
				if (height <= 0) {
					height = 1;
				}
				settings.setHeight(height);
				break;
			case DEPTH:
				int depth = centerPosition.getY() - position.getY();
				if (depth < 0) {
					depth = 0;
				}
				settings.setDepth(depth);
				break;
		}
	}

	public String getDisplayName() {
		return Translator.translateToLocal("for.greenhouse_screen.step." + getName() + ".name");
	}

	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
