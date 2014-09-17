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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import forestry.api.core.IToolPipette;
import forestry.core.fluids.tanks.FakeTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketTankUpdate;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;

public class ContainerLiquidTanks extends ContainerForestry {

	private ILiquidTankContainer tanks;

	public ContainerLiquidTanks(IInventory inventory, ILiquidTankContainer tanks) {
		super(inventory);
		this.tanks = tanks;
	}

	public void handlePipetteClick(int slot, EntityPlayer player) {

		ItemStack itemstack = player.inventory.getItemStack();
		if (itemstack == null)
			return;

		Item held = itemstack.getItem();
		if (!(held instanceof IToolPipette))
			return;

		if (!Proxies.common.isSimulating(player.worldObj)) {

			PacketPayload payload = new PacketPayload(1, 0, 0);
			payload.intPayload[0] = slot;
			Proxies.net.sendToServer(new PacketUpdate(PacketIds.PIPETTE_CLICK, payload));
			return;

		}

		IToolPipette pipette = (IToolPipette) held;
		int liquidAmount = tanks.getTanks()[slot].getFluid().amount;
		if (pipette.canPipette(itemstack) && liquidAmount > 0) {

			if (liquidAmount > 0) {
				int filled = pipette.fill(itemstack, tanks.getTanks()[slot].drain(1000, false),
						true);
				tanks.getTanks()[slot].drain(filled, true);
			}

		} else {

			IFluidTank tank = tanks.getTanks()[slot];
			FluidStack potential = pipette.drain(itemstack, pipette.getCapacity(itemstack), false);
			if (potential != null)
				pipette.drain(itemstack, tank.fill(potential, true), true);

		}
	}
	private Map<Integer, StandardTank> syncedFluids = new HashMap<Integer, StandardTank>();

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < tanks.getTanks().length; i++) {
			StandardTank tank = tanks.getTanks()[i];

			// If null has been synced
			if (tank.getFluid() == null && getTank(i).getFluidAmount() <= 0)
				continue;
			// If fluid has been synced
			if (tank.getFluid() != null && tank.getFluid().isFluidStackIdentical(getTank(i).getFluid()))
				continue;

			for (int j = 0; j < this.crafters.size(); ++j) {
				if (this.crafters.get(j) instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) this.crafters.get(j);
					Proxies.net.sendToPlayer(new PacketTankUpdate(i, tank), player);
				}
			}

			syncedFluids.put(i, new StandardTank(tank.getFluid() == null ? null : tank.getFluid().copy(), tank.getCapacity()));

		}
	}

	@Override
	public void onTankUpdate(NBTTagCompound nbt) {
		int tankID = nbt.getByte("tank");
		int capacity = nbt.getShort("capacity");
		tanks.getTanks()[tankID].readFromNBT(nbt);

		StandardTank tank = new StandardTank(capacity);
		tank.readFromNBT(nbt);
		syncedFluids.put(tankID, tank);
	}

	@Override
	public StandardTank getTank(int slot) {
		return syncedFluids.get(slot) == null ? FakeTank.INSTANCE : syncedFluids.get(slot);
	}
}
