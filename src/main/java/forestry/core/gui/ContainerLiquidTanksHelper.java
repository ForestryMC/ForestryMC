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
package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IToolPipette;
import forestry.core.network.packets.PacketPipetteClick;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.utils.NetworkUtil;

public class ContainerLiquidTanksHelper<T extends TileEntity & ILiquidTankTile> implements IContainerLiquidTanks {

	private final T tile;

	public ContainerLiquidTanksHelper(T tile) {
		this.tile = tile;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handlePipetteClickClient(int slot, EntityPlayer player) {
		ItemStack itemstack = player.inventory.getItemStack();
		if (itemstack.getItem() instanceof IToolPipette) {
			NetworkUtil.sendToServer(new PacketPipetteClick(slot));
		}
	}

	@Override
	public void handlePipetteClick(int slot, EntityPlayerMP player) {
		ItemStack itemstack = player.inventory.getItemStack();
		Item held = itemstack.getItem();
		if (!(held instanceof IToolPipette)) {
			return;
		}

		IToolPipette pipette = (IToolPipette) held;
		IFluidTank tank = tile.getTankManager().getTank(slot);
		int liquidAmount = tank.getFluidAmount();

		IFluidHandlerItem fluidHandlerItem = FluidUtil.getFluidHandler(itemstack);
		if (fluidHandlerItem != null) {
			if (pipette.canPipette(itemstack) && liquidAmount > 0) {
				if (liquidAmount > 0) {
					if (tank instanceof FluidTank) {
						FluidStack fillAmount = ((FluidTank) tank).drainInternal(Fluid.BUCKET_VOLUME, false);
						int filled = fluidHandlerItem.fill(fillAmount, true);
						tank.drain(filled, true);
						player.inventory.setItemStack(fluidHandlerItem.getContainer());
						player.updateHeldItem();
					}
				}
			} else {
				FluidStack potential = fluidHandlerItem.drain(Integer.MAX_VALUE, false);
				if (potential != null) {
					if (tank instanceof FluidTank) {
						int fill = tank.fill(potential, true);
						fluidHandlerItem.drain(fill, true);
						player.inventory.setItemStack(fluidHandlerItem.getContainer());
						player.updateHeldItem();
					}
				}
			}
		}
	}

	@Override
	public IFluidTank getTank(int slot) {
		return tile.getTankManager().getTank(slot);
	}
}
