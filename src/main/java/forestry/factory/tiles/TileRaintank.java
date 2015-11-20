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

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.api.core.BiomeHelper;
import forestry.api.core.IErrorLogic;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
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

public class TileRaintank extends TileBase implements ISidedInventory, ILiquidTankTile, IFluidHandler {
	private static final FluidStack STACK_WATER = Fluids.WATER.getFluid(Constants.RAINTANK_AMOUNT_PER_UPDATE);

	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	private boolean isValidBiome = true;
	private int fillingTime;
	private ItemStack usedEmpty;

	public TileRaintank() {
		super("raintank");
		setInternalInventory(new InventoryRaintank(this));

		resourceTank = new FilteredTank(Constants.RAINTANK_TANK_CAPACITY, FluidRegistry.WATER);
		tankManager = new TankManager(this, resourceTank);
	}

	@Override
	public void validate() {
		// Raintanks in desert biomes are useless
		if (worldObj != null) {
			BiomeGenBase biome = worldObj.getBiomeGenForCoordsBody(xCoord, zCoord);
			isValidBiome = BiomeHelper.canRainOrSnow(biome);
			getErrorLogic().setCondition(!isValidBiome, EnumErrorCode.NO_RAIN_BIOME);
		}

		super.validate();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("IsValidBiome", isValidBiome);

		tankManager.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		isValidBiome = nbttagcompound.getBoolean("IsValidBiome");

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

		errorLogic.setCondition(!isValidBiome, EnumErrorCode.NO_RAIN_BIOME);

		boolean hasSky = worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord);
		errorLogic.setCondition(!hasSky, EnumErrorCode.NO_SKY_RAIN_TANK);

		errorLogic.setCondition(!worldObj.isRaining(), EnumErrorCode.NOT_RAINING);

		if (!errorLogic.hasErrors()) {
			resourceTank.fill(STACK_WATER, true);
		}
		
		if (!ItemStackUtil.isIdenticalItem(usedEmpty, getStackInSlot(InventoryRaintank.SLOT_RESOURCE))) {
			fillingTime = 0;
			usedEmpty = null;
		}

		if (usedEmpty == null) {
			usedEmpty = getStackInSlot(InventoryRaintank.SLOT_RESOURCE);
		}

		if (!isFilling()) {
			FluidHelper.FillStatus canFill = FluidHelper.fillContainers(tankManager, this, InventoryRaintank.SLOT_RESOURCE, InventoryRaintank.SLOT_PRODUCT, Fluids.WATER.getFluid(), false);
			if (canFill == FluidHelper.FillStatus.SUCCESS) {
				fillingTime = Constants.RAINTANK_FILLING_TIME;
			}
		} else {
			fillingTime--;
			if (fillingTime <= 0) {
				FluidHelper.FillStatus filled = FluidHelper.fillContainers(tankManager, this, InventoryRaintank.SLOT_RESOURCE, InventoryRaintank.SLOT_PRODUCT, Fluids.WATER.getFluid());
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
		return (fillingTime * i) / Constants.RAINTANK_FILLING_TIME;
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
			case 0:
				fillingTime = j;
				break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, fillingTime);
	}

	// / ILIQUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tankManager.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return tankManager.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return tankManager.canDrain(from, fluid);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return tankManager.getTankInfo(from);
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
