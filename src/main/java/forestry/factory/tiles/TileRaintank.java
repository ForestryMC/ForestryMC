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
import javax.annotation.Nullable;
import java.io.IOException;

import forestry.api.core.IErrorLogic;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.ContainerFiller;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TileBase;
import forestry.factory.gui.ContainerRaintank;
import forestry.factory.gui.GuiRaintank;
import forestry.factory.inventory.InventoryRaintank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileRaintank extends TileBase implements ISidedInventory, ILiquidTankTile {
	private static final FluidStack STACK_WATER = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
	private static final FluidStack WATER_PER_UPDATE = new FluidStack(FluidRegistry.WATER, Constants.RAINTANK_AMOUNT_PER_UPDATE);

	@Nonnull
	private final FilteredTank resourceTank;
	@Nonnull
	private final TankManager tankManager;
	@Nonnull
	private final ContainerFiller containerFiller;

	private Boolean canDumpBelow = null;
	private boolean dumpingFluid = false;

	// client
	private int fillingProgress;

	public TileRaintank() {
		super("raintank");
		setInternalInventory(new InventoryRaintank(this));

		resourceTank = new FilteredTank(Constants.RAINTANK_TANK_CAPACITY).setFilters(FluidRegistry.WATER);

		tankManager = new TankManager(this, resourceTank);

		containerFiller = new ContainerFiller(resourceTank, Constants.RAINTANK_FILLING_TIME, this, InventoryRaintank.SLOT_RESOURCE, InventoryRaintank.SLOT_PRODUCT);
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
		if (updateOnInterval(20)) {
			IErrorLogic errorLogic = getErrorLogic();

			BlockPos pos = getPos();
			Biome biome = worldObj.getBiome(pos);
			errorLogic.setCondition(!biome.canRain(), EnumErrorCode.NO_RAIN_BIOME);

			BlockPos posAbove = pos.up();
			boolean hasSky = worldObj.canBlockSeeSky(posAbove);
			errorLogic.setCondition(!hasSky, EnumErrorCode.NO_SKY_RAIN_TANK);

			errorLogic.setCondition(!worldObj.isRainingAt(posAbove), EnumErrorCode.NOT_RAINING);

			if (!errorLogic.hasErrors()) {
				resourceTank.fillInternal(WATER_PER_UPDATE, true);
			}

			containerFiller.updateServerSide();
		}

		if (canDumpBelow == null) {
			canDumpBelow = FluidHelper.canAcceptFluid(worldObj, getPos().down(), EnumFacing.UP, STACK_WATER);
		}

		if (canDumpBelow) {
			if (dumpingFluid || updateOnInterval(20)) {
				dumpingFluid = dumpFluidBelow();
			}
		}
	}

	private boolean dumpFluidBelow() {
		if (!resourceTank.isEmpty()) {
			IFluidHandler fluidDestination = FluidUtil.getFluidHandler(worldObj, pos.down(), EnumFacing.UP);
			if (fluidDestination != null) {
				if (FluidUtil.tryFluidTransfer(fluidDestination, tankManager, Fluid.BUCKET_VOLUME / 20, true) != null) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isFilling() {
		return fillingProgress > 0;
	}

	public int getFillProgressScaled(int i) {
		return fillingProgress * i / Constants.RAINTANK_FILLING_TIME;
	}

	/* SMP GUI */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
			case 0:
				fillingProgress = j;
				break;
		}
	}

	public void sendGUINetworkData(Container container, IContainerListener iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, containerFiller.getFillingProgress());
	}

	@Nonnull
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public void onNeighborTileChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborTileChange(world, pos, neighbor);

		if (neighbor.equals(pos.down())) {
			canDumpBelow = FluidHelper.canAcceptFluid(worldObj, neighbor, EnumFacing.UP, STACK_WATER);
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankManager);
		}
		return super.getCapability(capability, facing);
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
