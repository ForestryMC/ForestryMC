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
import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.common.Optional;

import forestry.api.core.IErrorLogic;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.gui.ContainerBottler;
import forestry.factory.gui.GuiBottler;
import forestry.factory.inventory.InventoryBottler;
import forestry.factory.recipes.BottlerRecipe;
import forestry.factory.triggers.FactoryTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class TileBottler extends TilePowered implements ISidedInventory, ILiquidTankTile, IFluidHandler {
	private static final int TICKS_PER_RECIPE_TIME = 5;
	private static final int ENERGY_PER_RECIPE_TIME = 1000;

	private final StandardTank resourceTank;
	private final TankManager tankManager;

	private BottlerRecipe currentRecipe;

	public TileBottler() {
		super("bottler", 1100, 4000);

		setInternalInventory(new InventoryBottler(this));

		resourceTank = new StandardTank(Constants.PROCESSOR_TANK_CAPACITY);
		tankManager = new TankManager(this, resourceTank);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		tankManager.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readFromNBT(nbttagcompound);
		checkRecipe();
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
		super.updateServerSide();

		if (updateOnInterval(20)) {
			FluidHelper.drainContainers(tankManager, this, InventoryBottler.SLOT_INPUT_FULL_CAN);
		}
	}

	@Override
	public boolean workCycle() {
		FluidHelper.FillStatus status = FluidHelper.fillContainers(tankManager, this, InventoryBottler.SLOT_INPUT_EMPTY_CAN, InventoryBottler.SLOT_OUTPUT, currentRecipe.input.getFluid());
		return status == FluidHelper.FillStatus.SUCCESS;
	}

	private void checkRecipe() {
		ItemStack emptyCan = getStackInSlot(InventoryBottler.SLOT_INPUT_EMPTY_CAN);
		FluidStack resource = resourceTank.getFluid();

		if (currentRecipe == null || !currentRecipe.matches(emptyCan, resource)) {
			currentRecipe = BottlerRecipe.getRecipe(resource, emptyCan);
			if (currentRecipe != null) {
				float viscosityMultiplier = resource.getFluid().getViscosity(resource) / 1000.0f;
				viscosityMultiplier = ((viscosityMultiplier - 1f) / 20f) + 1f; // scale down the effect

				int fillAmount = Math.min(currentRecipe.input.amount, resource.amount);
				float fillTime = fillAmount / (float) Constants.BUCKET_VOLUME;
				fillTime *= viscosityMultiplier;

				setTicksPerWorkCycle(Math.round(fillTime * TICKS_PER_RECIPE_TIME));
				setEnergyPerWorkCycle(Math.round(fillTime * ENERGY_PER_RECIPE_TIME));
			}
		}
	}

	@Override
	public boolean hasResourcesMin(float percentage) {
		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(InventoryBottler.SLOT_INPUT_EMPTY_CAN) == null) {
			return false;
		}

		return ((float) inventory.getStackInSlot(InventoryBottler.SLOT_INPUT_EMPTY_CAN).stackSize / (float) inventory.getStackInSlot(InventoryBottler.SLOT_INPUT_EMPTY_CAN).getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		checkRecipe();

		IErrorLogic errorLogic = getErrorLogic();

		FluidHelper.FillStatus status;

		if (currentRecipe == null) {
			status = FluidHelper.FillStatus.NO_FLUID;
		} else {
			status = FluidHelper.fillContainers(tankManager, this, InventoryBottler.SLOT_INPUT_EMPTY_CAN, InventoryBottler.SLOT_OUTPUT, currentRecipe.input.getFluid(), false);
		}

		errorLogic.setCondition(status == FluidHelper.FillStatus.NO_FLUID, EnumErrorCode.NO_RESOURCE_LIQUID);
		errorLogic.setCondition(status == FluidHelper.FillStatus.NO_SPACE, EnumErrorCode.NO_SPACE_INVENTORY);
		return status == FluidHelper.FillStatus.SUCCESS;
	}

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
	}

	/* ILIQUIDCONTAINER */
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

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
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return tankManager.getTankInfo(from);
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<>();
		res.add(FactoryTriggers.lowResource25);
		res.add(FactoryTriggers.lowResource10);
		return res;
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiBottler(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerBottler(player.inventory, this);
	}
}
