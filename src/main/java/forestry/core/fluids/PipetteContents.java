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

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.INBTTagable;

public class PipetteContents implements INBTTagable {
	private FluidStack contents;

	public PipetteContents(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null) {
			readFromNBT(nbttagcompound);
		}
	}

	public FluidStack getContents() {
		return contents;
	}

	public void setContents(FluidStack contents) {
		this.contents = contents;
	}

	public boolean isFull() {
		if (contents == null) {
			return false;
		}

		return contents.getFluid().getID() > 0 && contents.amount >= 1000;
	}

	public void addTooltip(List<String> list) {
		if (contents == null) {
			return;
		}

		String descr = contents.getFluid().getLocalizedName(contents);
		descr += " (" + contents.amount + " mb)";

		list.add(descr);
	}

	/* INBTTagable */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		contents = FluidStack.loadFluidStackFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if (contents != null) {
			contents.writeToNBT(nbttagcompound);
		}
	}
}
