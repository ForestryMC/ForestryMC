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
package forestry.core.fluids;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.INbtWritable;
import net.minecraftforge.fluids.FluidUtil;

public class PipetteContents {
	@Nonnull
	private final FluidStack contents;

	@Nullable
	public static PipetteContents create(@Nullable ItemStack itemStack) {
		FluidStack contents = FluidUtil.getFluidContained(itemStack);
		if (contents == null) {
			return null;
		}
		return new PipetteContents(contents);
	}

	public PipetteContents(@Nonnull FluidStack contents) {
		this.contents = contents;
	}

	@Nonnull
	public FluidStack getContents() {
		return contents;
	}

	public boolean isFull() {
		return contents.amount >= Fluid.BUCKET_VOLUME;
	}

	public void addTooltip(List<String> list) {
		String descr = contents.getFluid().getLocalizedName(contents);
		descr += " (" + contents.amount + " mb)";

		list.add(descr);
	}
}
