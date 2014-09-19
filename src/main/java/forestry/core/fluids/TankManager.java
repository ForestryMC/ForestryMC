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

import com.google.common.collect.ForwardingList;
import forestry.core.fluids.tanks.FakeTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.ITileFilter;
import forestry.core.network.PacketGuiInteger;
import forestry.core.proxy.Proxies;
import forestry.core.utils.NBTUtil;
import forestry.core.utils.NBTUtil.NBTList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TankManager extends ForwardingList<StandardTank> implements IFluidHandler, List<StandardTank> {

	public static final ITileFilter TANK_FILTER = new ITileFilter() {
		@Override
		public boolean matches(TileEntity tile) {
			return tile instanceof IFluidHandler;
		}

	};
	private static final byte NETWORK_DATA = 3;
	private final List<StandardTank> tanks = new ArrayList<StandardTank>();
	private final List<FluidStack> prevFluidStacks = new ArrayList<FluidStack>();
	private final List<Integer> prevColor = new ArrayList<Integer>();

	public TankManager() {
	}

	public TankManager(StandardTank... tanks) {
		addAll(Arrays.asList(tanks));
	}

	@Override
	protected List<StandardTank> delegate() {
		return tanks;
	}

	@Override
	public boolean addAll(Collection<? extends StandardTank> collection) {
		boolean changed = false;
		for (StandardTank tank : collection)
			changed |= add(tank);

		return changed;
	}

	@Override
	public boolean add(StandardTank tank) {
		boolean added = tanks.add(tank);
		int index = tanks.indexOf(tank);
		tank.setTankIndex(index);
		prevFluidStacks.add(tank.getFluid() == null ? null : tank.getFluid().copy());
		prevColor.add(tank.getColor());
		return added;
	}

	public int maxMessageId() {
		return NETWORK_DATA * tanks.size();
	}

	public void writeTanksToNBT(NBTTagCompound data) {
		NBTTagList tagList = new NBTTagList();
		for (byte slot = 0; slot < tanks.size(); slot++) {
			StandardTank tank = tanks.get(slot);
			if (tank.getFluid() != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("tank", slot);
				tank.writeToNBT(tag);
				tagList.appendTag(tag);
			}
		}
		data.setTag("tanks", tagList);
	}

	public void readTanksFromNBT(NBTTagCompound data) {
		NBTList<NBTTagCompound> tagList = NBTUtil.getNBTList(data, "tanks", NBTUtil.EnumNBTType.COMPOUND);
		for (NBTTagCompound tag : tagList) {
			int slot = tag.getByte("tank");
			if (slot >= 0 && slot < tanks.size())
				tanks.get(slot).readFromNBT(tag);
		}
	}

	public void writePacketData(DataOutputStream data) throws IOException {
		for (int i = 0; i < tanks.size(); i++)
			writePacketData(data, i);
	}

	public void writePacketData(DataOutputStream data, int tankIndex) throws IOException {
		if (tankIndex >= tanks.size())
			return;
		StandardTank tank = tanks.get(tankIndex);
		FluidStack fluidStack = tank.getFluid();
		if (fluidStack != null) {
			data.writeShort(fluidStack.fluidID);
			data.writeInt(fluidStack.amount);
			data.writeInt(fluidStack.getFluid().getColor(fluidStack));
		} else
			data.writeShort(-1);
	}

	public void readPacketData(DataInputStream data) throws IOException {
		for (int i = 0; i < tanks.size(); i++)
			readPacketData(data, i);
	}

	public void readPacketData(DataInputStream data, int tankIndex) throws IOException {
		if (tankIndex >= tanks.size())
			return;
		StandardTank tank = tanks.get(tankIndex);
		int fluidId = data.readShort();
		if (fluidId != -1) {
			tank.setFluid(new FluidStack(fluidId, data.readInt()));
			tank.colorCache = data.readInt();
		} else
			tank.setFluid(null);
	}

	public void initGuiData(Container container, ICrafting player) {
		for (StandardTank tank : tanks)
			initGuiData(container, player, tank.getTankIndex());
	}

	public void initGuiData(Container container, ICrafting player, int tankIndex) {
		if (tankIndex >= tanks.size())
			return;
		FluidStack fluidStack = tanks.get(tankIndex).getFluid();
		int fluidId = -1;
		int fluidAmount = 0;
		if (fluidStack != null && fluidStack.amount > 0) {
			fluidId = fluidStack.getFluid().getID();
			fluidAmount = fluidStack.amount;
		}

		player.sendProgressBarUpdate(container, tankIndex * NETWORK_DATA + 0, fluidId);
		PacketGuiInteger packet = new PacketGuiInteger(container.windowId, tankIndex * NETWORK_DATA + 1, fluidAmount);
		Proxies.net.sendToPlayer(packet, (EntityPlayerMP)player);
	}

	public void updateGuiData(Container container, List crafters) {
		for (StandardTank tank : tanks)
			updateGuiData(container, crafters, tank.getTankIndex());
	}

	public void updateGuiData(Container container, List crafters, int tankIndex) {
		StandardTank tank = tanks.get(tankIndex);
		FluidStack fluidStack = tank.getFluid();
		FluidStack prev = prevFluidStacks.get(tankIndex);
		int color = tank.getColor();
		int pColor = prevColor.get(tankIndex);

		for (Object crafter1 : crafters) {
			ICrafting crafter = (ICrafting) crafter1;
			EntityPlayerMP player = (EntityPlayerMP) crafter1;
			if (fluidStack == null ^ prev == null) {
				int fluidId = -1;
				int fluidAmount = 0;
				if (fluidStack != null) {
					fluidId = fluidStack.fluidID;
					fluidAmount = fluidStack.amount;
				}
				crafter.sendProgressBarUpdate(container, tankIndex * NETWORK_DATA + 0, fluidId);
				PacketGuiInteger packet = new PacketGuiInteger(container.windowId, tankIndex * NETWORK_DATA + 1, fluidAmount);
				Proxies.net.sendToPlayer(packet, player);
			} else if (fluidStack != null && prev != null) {
				if (fluidStack.getFluid() != prev.getFluid())
					crafter.sendProgressBarUpdate(container, tankIndex * NETWORK_DATA + 0, fluidStack.fluidID);
				if (fluidStack.amount != prev.amount) {
					PacketGuiInteger packet = new PacketGuiInteger(container.windowId, tankIndex * NETWORK_DATA + 1, fluidStack.amount);
					Proxies.net.sendToPlayer(packet, player);
				}
				if (color != pColor) {
					PacketGuiInteger packet = new PacketGuiInteger(container.windowId, tankIndex * NETWORK_DATA + 2, color);
					Proxies.net.sendToPlayer(packet, player);
				}
			}
		}

		prevFluidStacks.set(tankIndex, tank.getFluid() == null ? null : tank.getFluid().copy());
		prevColor.set(tankIndex, color);
	}

	public void processGuiUpdate(int messageId, int data) {
		int tankIndex = messageId / NETWORK_DATA;

		if (tankIndex >= tanks.size())
			return;
		StandardTank tank = tanks.get(tankIndex);
		FluidStack fluidStack = tank.getFluid();
		if (fluidStack == null) {
			fluidStack = new FluidStack(-1, 0);
			tank.setFluid(fluidStack);
		}
		int fluidId = fluidStack.fluidID;
		int amount = fluidStack.amount;
		int color = tank.colorCache;
		boolean newLiquid = false;
		switch (messageId % NETWORK_DATA) {
		case 0:
			fluidId = data;
			newLiquid = true;
			break;
		case 1:
			amount = data;
			break;
		case 2:
			color = data;
			break;
		}
		if (newLiquid) {
			fluidStack = new FluidStack(fluidId, 0);
			tank.setFluid(fluidStack);
		}
		fluidStack.amount = amount;
		tank.colorCache = color;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		for (StandardTank tank : tanks)
			if (tankAcceptsFluid(tank, resource))
				return fill(tank.getTankIndex(), resource, doFill);

		return FakeTank.INSTANCE.fill(resource, doFill);
	}

	public int fill(int tankIndex, FluidStack resource, boolean doFill) {
		if (tankIndex < 0 || tankIndex >= tanks.size() || resource == null)
			return 0;

		StandardTank tank = tanks.get(tankIndex);
		if (!tank.canBeFilledExternally())
			return 0;

		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		for (StandardTank tank : tanks)
			if (tankCanDrain(tank))
				return drain(tank.getTankIndex(), maxDrain, doDrain);
		return FakeTank.INSTANCE.drain(maxDrain, doDrain);
	}

	public FluidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		if (tankIndex < 0 || tankIndex >= tanks.size())
			return null;

		StandardTank tank = tanks.get(tankIndex);
		if (!tank.canBeDrainedExternally())
			return null;

		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		for (StandardTank tank : tanks)
			if (tankCanDrainFluid(tank, resource))
				return tank.drain(resource.amount, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection direction) {
		FluidTankInfo[] info = new FluidTankInfo[size()];
		for (int i = 0; i < size(); i++)
			info[i] = get(i).getInfo();
		return info;
	}

	public FluidTankInfo[] getTankInfo() {
		return getTankInfo(ForgeDirection.UNKNOWN);
	}

	public FluidTankInfo getTankInfo(int tankIndex) {
		return get(tankIndex).getInfo();
	}

	public FluidStack getFluid(int tankIndex) {
		return get(tankIndex).getFluid();
	}

	@Override
	public StandardTank get(int tankIndex) {
		return tanks.get(tankIndex);
	}

	public void setCapacity(int tankIndex, int capacity) {
		StandardTank tank = get(tankIndex);
		tank.setCapacity(capacity);
		FluidStack fluidStack = tank.getFluid();
		if (fluidStack != null && fluidStack.amount > capacity)
			fluidStack.amount = capacity;
	}

	public static IFluidHandler getTankFromTile(TileEntity tile) {
		IFluidHandler tank = null;
		if (tile instanceof IFluidHandler)
			tank = (IFluidHandler) tile;
		return tank;
	}

	private boolean tankAcceptsFluid(StandardTank tank, FluidStack fluidStack) {
		if (fluidStack == null)
			return false;
		if (!tank.canBeFilledExternally())
			return false;
		if (tank.fill(fluidStack, false) > 0)
			return true;
		return false;
	}

	private boolean tankCanDrain(StandardTank tank) {
		if (!tank.canBeDrainedExternally())
			return false;
		FluidStack drained = tank.drain(1, false);
		if (drained != null && drained.amount > 0)
			return true;
		return false;
	}

	private boolean tankCanDrainFluid(StandardTank tank, FluidStack fluidStack) {
		return fluidStack != null && tankCanDrain(tank);
	}

}
