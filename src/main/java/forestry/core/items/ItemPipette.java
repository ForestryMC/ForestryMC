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
package forestry.core.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.INBTTagable;
import forestry.api.core.IToolPipette;
import forestry.core.config.Defaults;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemPipette extends ItemForestry implements IToolPipette {

	class PipetteContents implements INBTTagable {

		FluidStack contents;

		public PipetteContents(NBTTagCompound nbttagcompound) {
			if (nbttagcompound != null) {
				readFromNBT(nbttagcompound);
			}
		}

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

		public boolean isFull(int limit) {
			if (contents == null) {
				return false;
			}

			return contents.fluidID > 0 && contents.amount >= limit;
		}

		public void addTooltip(List<String> list) {
			if (contents == null) {
				return;
			}

			String descr = contents.getFluid().getLocalizedName(contents);
			descr += " (" + contents.amount + " mb)";

			list.add(descr);
		}
	}

	public ItemPipette() {
		super();
		setMaxStackSize(1);
		setFull3D();
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public boolean canPipette(ItemStack itemstack) {
		PipetteContents contained = new PipetteContents(itemstack.getTagCompound());
		return !contained.isFull(1000);
	}

	@Override
	public int fill(ItemStack itemstack, FluidStack liquid, boolean doFill) {
		PipetteContents contained = new PipetteContents(itemstack.getTagCompound());

		int limit = getCapacity(itemstack);
		int filled;

		if (contained.contents == null) {

			if (liquid.amount > limit) {
				filled = limit;
			} else {
				filled = liquid.amount;
			}

			contained.contents = new FluidStack(liquid.fluidID, filled);
			filled = liquid.amount;

		} else {

			if (contained.contents.amount >= limit) {
				return 0;
			}
			if (!contained.contents.isFluidEqual(liquid)) {
				return 0;
			}

			int space = limit - contained.contents.amount;

			if (liquid.amount > space) {
				filled = space;
			} else {
				filled = liquid.amount;
			}

			contained.contents.amount += filled;
		}

		if (doFill) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			contained.writeToNBT(nbttagcompound);
			itemstack.setTagCompound(nbttagcompound);
			itemstack.setItemDamage(1);
		}

		return filled;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		PipetteContents contained = new PipetteContents(itemstack.getTagCompound());
		contained.addTooltip(list);
	}

	@Override
	public FluidStack drain(ItemStack pipette, int maxDrain, boolean doDrain) {
		PipetteContents contained = new PipetteContents(pipette.getTagCompound());
		if (contained.contents == null || contained.contents.fluidID <= 0) {
			return null;
		}

		int drained = maxDrain;
		if (contained.contents.amount < drained) {
			drained = contained.contents.amount;
		}

		if (doDrain) {
			contained.contents.amount -= drained;

			if (contained.contents.amount <= 0) {
				pipette.setTagCompound(null);
				pipette.setItemDamage(0);
			} else {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				contained.writeToNBT(nbttagcompound);
				pipette.setTagCompound(nbttagcompound);
			}
		}

		return new FluidStack(contained.contents.fluidID, drained);
	}

	@Override
	public int getCapacity(ItemStack pipette) {
		return Defaults.BUCKET_VOLUME;
	}
}
