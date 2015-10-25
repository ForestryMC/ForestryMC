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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import forestry.core.fluids.tanks.FakeTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.network.PacketProgressBarUpdate;
import forestry.core.network.PacketTankLevelUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.render.EnumTankLevel;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.utils.NBTUtil;
import forestry.core.utils.NBTUtil.NBTList;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TankManager implements ITankManager {

	private static final byte NETWORK_DATA = 3;
	private final List<StandardTank> tanks = new ArrayList<>();
	private final List<EnumTankLevel> tankLevels = new ArrayList<>();
	private final List<FluidStack> prevFluidStacks = new ArrayList<>();
	private final List<Integer> prevColor = new ArrayList<>();
	// tank updates
	@Nullable
	private final ILiquidTankTile tile;

	public TankManager() {
		this.tile = null;
	}

	public TankManager(StandardTank... tanks) {
		this(null, tanks);
	}

	public TankManager(@Nullable ILiquidTankTile tile, StandardTank... tanks) {
		this.tile = tile;
		addAll(Arrays.asList(tanks));
	}

	public final boolean addAll(@Nonnull Collection<? extends StandardTank> collection) {
		boolean addedAll = true;
		for (StandardTank tank : collection) {
			addedAll &= add(tank);
		}
		return addedAll;
	}

	public boolean add(@Nonnull StandardTank tank) {
		boolean added = tanks.add(tank);
		int index = tanks.indexOf(tank);
		tank.setTankIndex(index);
		tankLevels.add(EnumTankLevel.rateTankLevel(tank));
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
			if (slot >= 0 && slot < tanks.size()) {
				StandardTank tank = tanks.get(slot);
				tank.readFromNBT(tag);
				updateTankLevels(tank, false);
			}
		}
	}

	public void writePacketData(DataOutputStream data) throws IOException {
		for (int i = 0; i < tanks.size(); i++) {
			writePacketData(data, i);
		}
	}

	public void writePacketData(DataOutputStream data, int tankIndex) throws IOException {
		if (tankIndex >= tanks.size()) {
			return;
		}
		StandardTank tank = tanks.get(tankIndex);
		FluidStack fluidStack = tank.getFluid();
		if (fluidStack != null) {
			data.writeShort(fluidStack.getFluid().getID());
			data.writeInt(fluidStack.amount);
			data.writeInt(fluidStack.getFluid().getColor(fluidStack));
		} else {
			data.writeShort(-1);
		}
	}

	public void readPacketData(DataInputStream data) throws IOException {
		for (int i = 0; i < tanks.size(); i++) {
			readPacketData(data, i);
		}
	}

	public void readPacketData(DataInputStream data, int tankIndex) throws IOException {
		if (tankIndex >= tanks.size()) {
			return;
		}
		StandardTank tank = tanks.get(tankIndex);
		int fluidId = data.readShort();
		Fluid fluid = FluidRegistry.getFluid(fluidId);
		if (fluid != null) {
			int amount = data.readInt();
			FluidStack fluidStack = new FluidStack(fluid, amount);
			tank.setFluid(fluidStack);
			tank.colorCache = data.readInt();
		} else {
			tank.setFluid(null);
		}
	}

	@Override
	public void initGuiData(Container container, ICrafting player) {
		for (StandardTank tank : tanks) {
			initGuiData(container, player, tank.getTankIndex());
		}
	}

	private void initGuiData(Container container, ICrafting player, int tankIndex) {
		if (tankIndex >= tanks.size()) {
			return;
		}
		FluidStack fluidStack = tanks.get(tankIndex).getFluid();
		int fluidId = -1;
		int fluidAmount = 0;
		if (fluidStack != null && fluidStack.amount > 0) {
			fluidId = fluidStack.getFluid().getID();
			fluidAmount = fluidStack.amount;
		}

		player.sendProgressBarUpdate(container, tankIndex * NETWORK_DATA, fluidId);
		PacketProgressBarUpdate packet = new PacketProgressBarUpdate(container.windowId, tankIndex * NETWORK_DATA + 1, fluidAmount);
		Proxies.net.sendToPlayer(packet, (EntityPlayerMP) player);
	}

	@Override
	public void updateGuiData(Container container, List<EntityPlayerMP> crafters) {
		for (StandardTank tank : tanks) {
			updateGuiData(container, crafters, tank.getTankIndex());
		}
	}

	private void updateGuiData(Container container, List<EntityPlayerMP> crafters, int tankIndex) {
		StandardTank tank = tanks.get(tankIndex);
		FluidStack fluidStack = tank.getFluid();
		FluidStack prev = prevFluidStacks.get(tankIndex);
		int color = tank.getColor();
		int pColor = prevColor.get(tankIndex);

		for (EntityPlayerMP player : crafters) {
			if (fluidStack == null ^ prev == null) {
				int fluidId = -1;
				int fluidAmount = 0;
				if (fluidStack != null) {
					fluidId = fluidStack.getFluid().getID();
					fluidAmount = fluidStack.amount;
				}
				player.sendProgressBarUpdate(container, tankIndex * NETWORK_DATA, fluidId);
				PacketProgressBarUpdate packet = new PacketProgressBarUpdate(container.windowId, tankIndex * NETWORK_DATA + 1, fluidAmount);
				Proxies.net.sendToPlayer(packet, player);
			} else if (fluidStack != null && prev != null) {
				if (fluidStack.getFluid() != prev.getFluid()) {
					player.sendProgressBarUpdate(container, tankIndex * NETWORK_DATA, fluidStack.getFluid().getID());
				}
				if (fluidStack.amount != prev.amount) {
					PacketProgressBarUpdate packet = new PacketProgressBarUpdate(container.windowId, tankIndex * NETWORK_DATA + 1, fluidStack.amount);
					Proxies.net.sendToPlayer(packet, player);
				}
				if (color != pColor) {
					PacketProgressBarUpdate packet = new PacketProgressBarUpdate(container.windowId, tankIndex * NETWORK_DATA + 2, color);
					Proxies.net.sendToPlayer(packet, player);
				}
			}
		}

		prevFluidStacks.set(tankIndex, tank.getFluid() == null ? null : tank.getFluid().copy());
		prevColor.set(tankIndex, color);
	}

	@Override
	public void processGuiUpdate(int messageId, int data) {
		int tankIndex = messageId / NETWORK_DATA;

		if (tankIndex >= tanks.size()) {
			return;
		}
		StandardTank tank = tanks.get(tankIndex);

		switch (messageId % NETWORK_DATA) {
			case 0: {
				Fluid fluid = FluidRegistry.getFluid(data);
				if (fluid != null) {
					FluidStack fluidStack = new FluidStack(fluid, 0);
					tank.setFluid(fluidStack);
				}
				break;
			}
			case 1: {
				FluidStack fluidStack = tank.getFluid();
				if (fluidStack != null) {
					fluidStack.amount = data;
				}
				break;
			}
			case 2: {
				tank.colorCache = data;
				break;
			}
		}
	}

	@Override
	public void processTankUpdate(int tankIndex, FluidStack contents) {
		if (tankIndex < 0 || tankIndex > tanks.size()) {
			return;
		}
		StandardTank tank = tanks.get(tankIndex);
		tank.setFluid(contents);
	}

	@Override
	public IFluidTank getTank(int tankIndex) {
		return tanks.get(tankIndex);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		for (StandardTank tank : tanks) {
			if (tankAcceptsFluid(tank, resource)) {
				return fill(tank.getTankIndex(), resource, doFill);
			}
		}

		return FakeTank.INSTANCE.fill(resource, doFill);
	}

	public int fill(int tankIndex, FluidStack resource, boolean doFill) {
		if (tankIndex < 0 || tankIndex >= tanks.size() || resource == null) {
			return 0;
		}

		StandardTank tank = tanks.get(tankIndex);
		if (!tank.canBeFilledExternally()) {
			return 0;
		}

		int fill = tank.fill(resource, doFill);

		if (doFill) {
			updateTankLevels(tank, true);
		}

		return fill;
	}

	private void updateTankLevels(StandardTank tank, boolean sendUpdate) {
		if (tile == null) {
			return;
		}

		int tankIndex = tank.getTankIndex();
		EnumTankLevel tankLevel = EnumTankLevel.rateTankLevel(tank);
		if (tankLevel != tankLevels.get(tankIndex)) {
			tankLevels.set(tankIndex, tankLevel);
			if (sendUpdate) {
				PacketTankLevelUpdate tankLevelUpdate = new PacketTankLevelUpdate(tile, tankIndex, tank.getFluid());
				Proxies.net.sendNetworkPacket(tankLevelUpdate, tile.getWorld());
			}
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		for (StandardTank tank : tanks) {
			if (tankCanDrain(tank, true)) {
				return drain(tank.getTankIndex(), maxDrain, true, doDrain);
			}
		}
		return FakeTank.INSTANCE.drain(maxDrain, doDrain);
	}

	public FluidStack drain(int tankIndex, int maxDrain, boolean external, boolean doDrain) {
		if (tankIndex < 0 || tankIndex >= tanks.size()) {
			return null;
		}

		StandardTank tank = tanks.get(tankIndex);
		if (external && !tank.canBeDrainedExternally()) {
			return null;
		}

		FluidStack drained = tank.drain(maxDrain, doDrain);

		if (doDrain) {
			updateTankLevels(tank, true);
		}

		return drained;
	}

	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return drain(resource, false, doDrain);
	}

	public FluidStack drain(FluidStack resource, boolean external, boolean doDrain) {
		for (StandardTank tank : tanks) {
			if (tankCanDrainFluid(tank, resource, external)) {
				return drain(tank.getTankIndex(), resource.amount, external, doDrain);
			}
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return drain(resource, true, doDrain);
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
		FluidTankInfo[] info = new FluidTankInfo[tanks.size()];
		for (int i = 0; i < tanks.size(); i++) {
			info[i] = tanks.get(i).getInfo();
		}
		return info;
	}

	public FluidTankInfo[] getTankInfo() {
		return getTankInfo(ForgeDirection.UNKNOWN);
	}

	public FluidTankInfo getTankInfo(int tankIndex) {
		return tanks.get(tankIndex).getInfo();
	}

	public FluidStack getFluid(int tankIndex) {
		return tanks.get(tankIndex).getFluid();
	}

	public int getFluidAmount(int tankIndex) {
		return tanks.get(tankIndex).getFluidAmount();
	}

	public boolean accepts(Fluid fluid) {
		if (fluid == null) {
			return false;
		}
		
		for (StandardTank tank : tanks) {
			if (tank.accepts(fluid)) {
				return true;
			}
		}

		return false;
	}

	private static boolean tankAcceptsFluid(StandardTank tank, FluidStack fluidStack) {
		if (fluidStack == null) {
			return false;
		}
		if (!tank.canBeFilledExternally()) {
			return false;
		}
		return tank.fill(fluidStack, false) > 0;
	}

	private static boolean tankCanDrain(StandardTank tank, boolean external) {
		if (external && !tank.canBeDrainedExternally()) {
			return false;
		}
		FluidStack drained = tank.drain(1, false);
		return drained != null && drained.amount > 0;
	}

	private static boolean tankCanDrainFluid(StandardTank tank, FluidStack fluidStack, boolean external) {
		if (fluidStack == null) {
			return false;
		}
		if (!Fluids.areEqual(tank.getFluidType(), fluidStack)) {
			return false;
		}
		return tankCanDrain(tank, external);
	}
}
