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
package forestry.factory.tiles;

import javax.annotation.Nonnull;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TileBase;
import forestry.core.utils.ItemStackUtil;
import forestry.factory.gui.ContainerRaintank;
import forestry.factory.gui.GuiRaintank;
import forestry.factory.inventory.InventoryRaintank;

public class TileRaintank extends TileBase implements ISidedInventory, ILiquidTankTile {
	private static final FluidStack STACK_WATER = new FluidStack(FluidRegistry.WATER, Constants.RAINTANK_AMOUNT_PER_UPDATE);

	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	private int fillingTime;
	private ItemStack usedEmpty;

	public TileRaintank() {
		super("raintank");
		setInternalInventory(new InventoryRaintank(this));

		resourceTank = new FilteredTank(Constants.RAINTANK_TANK_CAPACITY).setFilters(FluidRegistry.WATER);

		tankManager = new TankManager(this, resourceTank);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		tankManager.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		tankManager.writeData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankManager.readData(data);
	}

	@Override
	public void updateServerSide() {

		if (!updateOnInterval(20)) {
			return;
		}

		IErrorLogic errorLogic = getErrorLogic();

		Biome biome = worldObj.getBiome(getPos());
		errorLogic.setCondition(!biome.canRain(), EnumErrorCode.NO_RAIN_BIOME);

		BlockPos posAbove = getPos().up();
		boolean hasSky = worldObj.canBlockSeeSky(posAbove);
		errorLogic.setCondition(!hasSky, EnumErrorCode.NO_SKY_RAIN_TANK);

		errorLogic.setCondition(!worldObj.isRainingAt(posAbove), EnumErrorCode.NOT_RAINING);

		if (!errorLogic.hasErrors()) {
			resourceTank.fillInternal(STACK_WATER, true);
		}
		
		if (!ItemStackUtil.isIdenticalItem(usedEmpty, getStackInSlot(InventoryRaintank.SLOT_RESOURCE))) {
			fillingTime = 0;
			usedEmpty = null;
		}

		if (usedEmpty == null) {
			usedEmpty = getStackInSlot(InventoryRaintank.SLOT_RESOURCE);
		}

		if (!isFilling()) {
			FluidHelper.FillStatus canFill = FluidHelper.fillContainers(tankManager, this, InventoryRaintank.SLOT_RESOURCE, InventoryRaintank.SLOT_PRODUCT, FluidRegistry.WATER, false);
			if (canFill == FluidHelper.FillStatus.SUCCESS) {
				fillingTime = Constants.RAINTANK_FILLING_TIME;
			}
		} else {
			fillingTime--;
			if (fillingTime <= 0) {
				FluidHelper.FillStatus filled = FluidHelper.fillContainers(tankManager, this, InventoryRaintank.SLOT_RESOURCE, InventoryRaintank.SLOT_PRODUCT, FluidRegistry.WATER, true);
				if (filled == FluidHelper.FillStatus.SUCCESS) {
					fillingTime = 0;
				}
			}
		}
	}

	public boolean isFilling() {
		return fillingTime > 0;
	}

	public int getFillProgressScaled(int i) {
		return fillingTime * i / Constants.RAINTANK_FILLING_TIME;
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
			case 0:
				fillingTime = j;
				break;
		}
	}

	public void sendGUINetworkData(Container container, IContainerListener iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, fillingTime);
	}

	@Nonnull
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return true;
		}
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return super.getCapability(capability, facing);
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankManager);
		}
		return null;
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiRaintank(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerRaintank(player.inventory, this);
	}
}
