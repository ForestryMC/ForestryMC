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

import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.IItemStackDisplay;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.PlayerUtil;
import forestry.factory.gui.ContainerCarpenter;
import forestry.factory.gui.GuiCarpenter;
import forestry.factory.inventory.InventoryCarpenter;
import forestry.factory.inventory.InventoryGhostCrafting;
import forestry.factory.recipes.CarpenterRecipeManager;

public class TileCarpenter extends TilePowered implements ISidedInventory, ILiquidTankTile, IFluidHandler, IItemStackDisplay {
	private static final int TICKS_PER_RECIPE_TIME = 1;
	private static final int ENERGY_PER_WORK_CYCLE = 2040;
	private static final int ENERGY_PER_RECIPE_TIME = ENERGY_PER_WORK_CYCLE / 10;

	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	private final InventoryAdapterTile craftingInventory;
	private final InventoryCraftResult craftPreviewInventory;

	@Nullable
	private ICarpenterRecipe currentRecipe;

	private ItemStack getBoxStack() {
		return getInternalInventory().getStackInSlot(InventoryCarpenter.SLOT_BOX);
	}

	public TileCarpenter() {
		super("carpenter", 1100, 4000);
		setEnergyPerWorkCycle(ENERGY_PER_WORK_CYCLE);
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, CarpenterRecipeManager.getRecipeFluids());

		craftingInventory = new InventoryGhostCrafting<>(this, 10);
		craftPreviewInventory = new InventoryCraftResult();
		setInternalInventory(new InventoryCarpenter(this));

		tankManager = new TankManager(this, resourceTank);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		tankManager.writeToNBT(nbttagcompound);
		craftingInventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readFromNBT(nbttagcompound);
		craftingInventory.readFromNBT(nbttagcompound);
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

	public void checkRecipe() {
		if (worldObj.isRemote) {
			return;
		}

		if (!CarpenterRecipeManager.matches(currentRecipe, resourceTank.getFluid(), getBoxStack(), craftingInventory)) {
			currentRecipe = CarpenterRecipeManager.findMatchingRecipe(resourceTank.getFluid(), getBoxStack(), craftingInventory);

			if (currentRecipe != null) {
				int recipeTime = currentRecipe.getPackagingTime();
				setTicksPerWorkCycle(recipeTime * TICKS_PER_RECIPE_TIME);
				setEnergyPerWorkCycle(recipeTime * ENERGY_PER_RECIPE_TIME);

				ItemStack craftingResult = currentRecipe.getCraftingGridRecipe().getRecipeOutput();
				craftPreviewInventory.setInventorySlotContents(0, craftingResult);
			} else {
				craftPreviewInventory.setInventorySlotContents(0, null);
			}
		}
	}

	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (updateOnInterval(20)) {
			FluidHelper.drainContainers(tankManager, this, InventoryCarpenter.SLOT_CAN_INPUT);
		}
	}

	@Override
	public boolean workCycle() {
		if (!removeLiquidResources(true)) {
			return false;
		}
		if (!removeItemResources(true)) {
			return false;
		}

		ItemStack pendingProduct = currentRecipe.getCraftingGridRecipe().getRecipeOutput();
		InventoryUtil.tryAddStack(this, pendingProduct, InventoryCarpenter.SLOT_PRODUCT, InventoryCarpenter.SLOT_PRODUCT_COUNT, true);

		return true;
	}

	private boolean removeLiquidResources(boolean doRemove) {
		if (currentRecipe == null) {
			return true;
		}

		FluidStack fluid = currentRecipe.getFluidResource();
		if (fluid != null) {
			if (!resourceTank.canDrain(fluid)) {
				return false;
			}
			if (doRemove) {
				resourceTank.drain(fluid.amount, true);
			}
		}

		return true;
	}

	private boolean removeItemResources(boolean doRemove) {
		if (currentRecipe == null) {
			return true;
		}

		if (currentRecipe.getBox() != null) {
			ItemStack box = getStackInSlot(InventoryCarpenter.SLOT_BOX);
			if (box == null || box.stackSize == 0) {
				return false;
			}
			if (doRemove) {
				decrStackSize(InventoryCarpenter.SLOT_BOX, 1);
			}
		}

		EntityPlayer player = PlayerUtil.getPlayer(worldObj, getAccessHandler().getOwner());
		ItemStack[] craftingSets = InventoryUtil.getStacks(craftingInventory, InventoryGhostCrafting.SLOT_CRAFTING_1, InventoryGhostCrafting.SLOT_CRAFTING_COUNT);
		IInventory inventory = new InventoryMapper(getInternalInventory(), InventoryCarpenter.SLOT_INVENTORY_1, InventoryCarpenter.SLOT_INVENTORY_COUNT);
		return InventoryUtil.removeSets(inventory, 1, craftingSets, player, true, true, false, doRemove);
	}

	/* STATE INFORMATION */
	@Override
	public boolean hasWork() {
		if (updateOnInterval(20)) {
			checkRecipe();
		}

		boolean hasRecipe = (currentRecipe != null);
		boolean hasLiquidResources = true;
		boolean hasItemResources = true;
		boolean canAdd = true;

		if (hasRecipe) {
			hasLiquidResources = removeLiquidResources(false);
			hasItemResources = removeItemResources(false);

			ItemStack pendingProduct = currentRecipe.getCraftingGridRecipe().getRecipeOutput();
			canAdd = InventoryUtil.tryAddStack(this, pendingProduct, InventoryCarpenter.SLOT_PRODUCT, InventoryCarpenter.SLOT_PRODUCT_COUNT, true, false);
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NO_RECIPE);
		errorLogic.setCondition(!hasLiquidResources, EnumErrorCode.NO_RESOURCE_LIQUID);
		errorLogic.setCondition(!hasItemResources, EnumErrorCode.NO_RESOURCE_INVENTORY);
		errorLogic.setCondition(!canAdd, EnumErrorCode.NO_SPACE_INVENTORY);

		return hasRecipe && hasItemResources && hasLiquidResources && canAdd;
	}

	@Override
	public TankRenderInfo getResourceTankInfo() {
		return new TankRenderInfo(resourceTank);
	}

	/**
	 * @return Inaccessible crafting inventory for the craft grid.
	 */
	public IInventory getCraftingInventory() {
		return craftingInventory;
	}

	public IInventory getCraftPreviewInventory() {
		return craftPreviewInventory;
	}

	@Override
	public void handleItemStackForDisplay(ItemStack itemStack) {
		craftPreviewInventory.setInventorySlotContents(0, itemStack);
	}

	// IFLUIDCONTAINER IMPLEMENTATION
	@Override
	public int fill(ForgeDirection direction, FluidStack resource, boolean doFill) {
		return tankManager.fill(direction, resource, doFill);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
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

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiCarpenter(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerCarpenter(player.inventory, this);
	}
}
