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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.IToolPipette;
import forestry.core.config.Constants;
import forestry.core.fluids.PipetteContents;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemPipette extends ItemForestry implements IToolPipette {

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
		return !contained.isFull();
	}

	@Override
	public int fill(ItemStack itemstack, FluidStack liquid, boolean doFill) {
		PipetteContents contained = new PipetteContents(itemstack.getTagCompound());

		int limit = getCapacity(itemstack);
		int filled;

		if (contained.getContents() == null) {
			if (liquid.amount > limit) {
				filled = limit;
			} else {
				filled = liquid.amount;
			}

			contained.setContents(new FluidStack(liquid, filled));
			filled = liquid.amount;
		} else {
			if (contained.getContents().amount >= limit) {
				return 0;
			}
			if (!contained.getContents().isFluidEqual(liquid)) {
				return 0;
			}

			int space = limit - contained.getContents().amount;

			if (liquid.amount > space) {
				filled = space;
			} else {
				filled = liquid.amount;
			}

			contained.getContents().amount += filled;
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

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon primaryIcon;
	@SideOnly(Side.CLIENT)
	private IIcon secondaryIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		primaryIcon = TextureManager.registerTex(register, StringUtil.cleanItemName(this) + ".0");
		secondaryIcon = TextureManager.registerTex(register, StringUtil.cleanItemName(this) + ".1");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage) {
		if (damage <= 0) {
			return primaryIcon;
		} else {
			return secondaryIcon;
		}
	}

	@Override
	public FluidStack drain(ItemStack pipette, int maxDrain, boolean doDrain) {
		PipetteContents contained = new PipetteContents(pipette.getTagCompound());
		if (contained.getContents() == null || contained.getContents().getFluid().getID() <= 0) {
			return null;
		}

		int drained = maxDrain;
		if (contained.getContents().amount < drained) {
			drained = contained.getContents().amount;
		}

		if (doDrain) {
			contained.getContents().amount -= drained;

			if (contained.getContents().amount <= 0) {
				pipette.setTagCompound(null);
				pipette.setItemDamage(0);
			} else {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				contained.writeToNBT(nbttagcompound);
				pipette.setTagCompound(nbttagcompound);
			}
		}

		return new FluidStack(contained.getContents(), drained);
	}

	@Override
	public int getCapacity(ItemStack pipette) {
		return Constants.BUCKET_VOLUME;
	}
}
