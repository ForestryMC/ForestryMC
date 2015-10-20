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

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.IStillRecipe;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.recipes.StillRecipeManager;

public class TileStill extends TilePowered implements ISidedInventory, ILiquidTankTile {

	/* CONSTANTS */
	public static final short SLOT_PRODUCT = 0;
	public static final short SLOT_RESOURCE = 1;
	public static final short SLOT_CAN = 2;

	/* MEMBER */
	private final FilteredTank resourceTank;
	private final FilteredTank productTank;
	private final TankManager tankManager;

	private IStillRecipe currentRecipe;
	private FluidStack bufferedLiquid;
	private int distillationTime = 0;
	private int distillationTotalTime = 0;

	public TileStill() {
		super(1100, 8000, 200);
		setInternalInventory(new StillInventoryAdapter(this));
		setHints(Config.hints.get("still"));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, StillRecipeManager.recipeFluidInputs);
		resourceTank.tankMode = StandardTank.TankMode.INPUT;
		productTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, StillRecipeManager.recipeFluidOutputs);
		productTank.tankMode = StandardTank.TankMode.OUTPUT;
		tankManager = new TankManager(resourceTank, productTank);
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.StillGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("DistillationTime", distillationTime);
		nbttagcompound.setInteger("DistillationTotalTime", distillationTotalTime);

		tankManager.writeTanksToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		distillationTime = nbttagcompound.getInteger("DistillationTime");
		distillationTotalTime = nbttagcompound.getInteger("DistillationTotalTime");

		tankManager.readTanksFromNBT(nbttagcompound);

		checkRecipe();
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		tankManager.writePacketData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		tankManager.readPacketData(data);
	}

	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (!updateOnInterval(20)) {
			return;
		}

		IInventoryAdapter inventory = getInternalInventory();
		// Check if we have suitable items waiting in the item slot
		if (inventory.getStackInSlot(SLOT_CAN) != null) {
			FluidHelper.drainContainers(tankManager, inventory, SLOT_CAN);
		}

		// Can product liquid if possible
		if (inventory.getStackInSlot(SLOT_RESOURCE) != null) {
			FluidStack fluidStack = productTank.getFluid();
			if (fluidStack != null) {
				FluidHelper.fillContainers(tankManager, inventory, SLOT_RESOURCE, SLOT_PRODUCT, fluidStack.getFluid());
			}
		}

		checkRecipe();
	}

	@Override
	public boolean workCycle() {

		IErrorLogic errorLogic = getErrorLogic();

		checkRecipe();

		// Ongoing process
		if (distillationTime > 0 && !errorLogic.hasErrors()) {

			distillationTime -= currentRecipe.getInput().amount;
			productTank.fill(currentRecipe.getOutput(), true);

			return true;

		} else if (currentRecipe != null) {

			int resourceRequired = currentRecipe.getCyclesPerUnit() * currentRecipe.getInput().amount;

			boolean canFill = productTank.fill(currentRecipe.getOutput(), false) == currentRecipe.getOutput().amount;
			errorLogic.setCondition(!canFill, EnumErrorCode.NOSPACETANK);

			boolean hasResource = resourceTank.getFluidAmount() >= resourceRequired;
			errorLogic.setCondition(!hasResource, EnumErrorCode.NORESOURCE);

			if (!errorLogic.hasErrors()) {
				// Start next cycle if enough bio mass is available
				distillationTime = distillationTotalTime = resourceRequired;
				resourceTank.drain(resourceRequired, true);
				bufferedLiquid = new FluidStack(currentRecipe.getInput(), resourceRequired);

				return true;
			}
		}

		bufferedLiquid = null;
		return false;
	}

	private void checkRecipe() {
		IStillRecipe matchingRecipe = StillRecipeManager.findMatchingRecipe(resourceTank.getFluid());

		if (matchingRecipe == null && bufferedLiquid != null && distillationTime > 0) {
			matchingRecipe = StillRecipeManager.findMatchingRecipe(new FluidStack(bufferedLiquid, distillationTime));
		}

		if (currentRecipe != matchingRecipe) {
			currentRecipe = matchingRecipe;
		}

		getErrorLogic().setCondition(currentRecipe == null, EnumErrorCode.NORECIPE);
	}

	@Override
	public boolean hasWork() {
		if (currentRecipe == null) {
			return false;
		}

		return (distillationTime > 0 || resourceTank.getFluidAmount() >= currentRecipe.getCyclesPerUnit() * currentRecipe.getInput().amount)
				&& productTank.getFluidAmount() <= productTank.getCapacity() - currentRecipe.getOutput().amount;
	}

	public int getDistillationProgressScaled(int i) {
		if (distillationTotalTime == 0) {
			return i;
		}

		return (distillationTime * i) / distillationTotalTime;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Constants.PROCESSOR_TANK_CAPACITY;
	}

	public int getProductScaled(int i) {
		return (productTank.getFluidAmount() * i) / Constants.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank, getResourceScaled(100));
	}

	@Override
	public TankRenderInfo getProductTankInfo() {
		return new TankRenderInfo(productTank, getProductScaled(100));
	}

	/* SMP GUI */
	@Override
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;
		switch (i) {
			case 0:
				distillationTime = j;
				break;
			case 1:
				distillationTotalTime = j;
				break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, distillationTime);
		iCrafting.sendProgressBarUpdate(container, i + 1, distillationTotalTime);
	}

	/* IFluidHandler */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int quantityMax, boolean doEmpty) {
		return tankManager.drain(from, quantityMax, doEmpty);
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

	private static class StillInventoryAdapter extends TileInventoryAdapter<TileStill> {
		public StillInventoryAdapter(TileStill still) {
			super(still, 3, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_RESOURCE) {
				return FluidHelper.isEmptyContainer(itemStack);
			} else if (slotIndex == SLOT_CAN) {
				Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
				return tile.resourceTank.accepts(fluid);
			}
			return false;
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return slotIndex == SLOT_PRODUCT;
		}
	}
}
