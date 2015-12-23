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

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import forestry.api.core.IToolPipette;
import forestry.core.network.packets.PacketPipetteClick;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ILiquidTankTile;

public class ContainerLiquidTanksHelper<T extends TileEntity & ILiquidTankTile> implements IContainerLiquidTanks {

	private final T tile;

	public ContainerLiquidTanksHelper(T tile) {
		this.tile = tile;
	}

	@Override
	public void handlePipetteClickClient(int slot, EntityPlayer player) {
		ItemStack itemstack = player.inventory.getItemStack();
		if (itemstack == null || !(itemstack.getItem() instanceof IToolPipette)) {
			return;
		}

		Proxies.net.sendToServer(new PacketPipetteClick(tile, slot));
	}

	@Override
	public void handlePipetteClick(int slot, EntityPlayerMP player) {
		ItemStack itemstack = player.inventory.getItemStack();
		if (itemstack == null) {
			return;
		}

		Item held = itemstack.getItem();
		if (!(held instanceof IToolPipette)) {
			return;
		}

		IToolPipette pipette = (IToolPipette) held;
		IFluidTank tank = tile.getTankManager().getTank(slot);
		int liquidAmount = tank.getFluidAmount();

		if (pipette.canPipette(itemstack) && liquidAmount > 0) {
			if (liquidAmount > 0) {
				FluidStack fillAmount = tank.drain(1000, false);
				int filled = pipette.fill(itemstack, fillAmount, true);
				tank.drain(filled, true);
				player.updateHeldItem();
			}
		} else {
			FluidStack potential = pipette.drain(itemstack, pipette.getCapacity(itemstack), false);
			if (potential != null) {
				pipette.drain(itemstack, tank.fill(potential, true), true);
				player.updateHeldItem();
			}
		}
	}

	@Override
	public IFluidTank getTank(int slot) {
		return tile.getTankManager().getTank(slot);
	}
}
