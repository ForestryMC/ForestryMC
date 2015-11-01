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

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.IItemStackDisplay;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.SlotUtil;
import forestry.factory.recipes.CarpenterRecipeManager;

public class TileCarpenter extends TilePowered implements ISidedInventory, ILiquidTankTile, IFluidHandler, IItemStackDisplay {
	private static final int TICKS_PER_RECIPE_TIME = 1;
	private static final int ENERGY_PER_WORK_CYCLE = 2040;
	private static final int ENERGY_PER_RECIPE_TIME = ENERGY_PER_WORK_CYCLE / 10;

	public final static int SLOT_CRAFTING_1 = 0;
	public final static int SLOT_CRAFTING_COUNT = 9;
	public final static int SLOT_BOX = 9;
	public final static int SLOT_PRODUCT = 10;
	public final static int SLOT_PRODUCT_COUNT = 1;
	public final static int SLOT_CAN_INPUT = 11;
	public final static short SLOT_INVENTORY_1 = 12;
	public final static short SLOT_INVENTORY_COUNT = 18;

	/* MEMBER */
	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	private final TileInventoryAdapter craftingInventory;
	private final InventoryCraftResult craftPreviewInventory;

	@Nullable
	private ICarpenterRecipe currentRecipe;

	private ItemStack getBoxStack() {
		return getInternalInventory().getStackInSlot(SLOT_BOX);
	}

	public TileCarpenter() {
		super(1100, 4000, ENERGY_PER_WORK_CYCLE);
		setHints(Config.hints.get("carpenter"));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, CarpenterRecipeManager.recipeFluids);

		craftingInventory = new TileInventoryAdapter<>(this, 10, "CraftItems");
		craftPreviewInventory = new InventoryCraftResult();
		setInternalInventory(new CarpenterInventoryAdapter(this));

		tankManager = new TankManager(this, resourceTank);
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.CarpenterGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
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
			FluidHelper.drainContainers(tankManager, this, SLOT_CAN_INPUT);
		}
	}

	@Override
	public boolean workCycle() {
		if (!removeResources(true)) {
			return false;
		}

		ItemStack pendingProduct = currentRecipe.getCraftingGridRecipe().getRecipeOutput();
		InventoryUtil.tryAddStack(this, pendingProduct, SLOT_PRODUCT, SLOT_PRODUCT_COUNT, true);

		return true;
	}

	private boolean removeResources(boolean doRemove) {
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

		if (currentRecipe.getBox() != null) {
			ItemStack box = getStackInSlot(SLOT_BOX);
			if (box == null || box.stackSize == 0) {
				return false;
			}
			if (doRemove) {
				decrStackSize(SLOT_BOX, 1);
			}
		}

		EntityPlayer player = PlayerUtil.getPlayer(worldObj, getAccessHandler().getOwner());
		ItemStack[] craftingSets = InventoryUtil.getStacks(craftingInventory, SLOT_CRAFTING_1, SLOT_CRAFTING_COUNT);
		IInventory inventory = new InventoryMapper(getInternalInventory(), SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
		return InventoryUtil.removeSets(inventory, 1, craftingSets, player, true, true, false, doRemove);
	}

	/* STATE INFORMATION */
	@Override
	public boolean hasWork() {
		if (updateOnInterval(20)) {
			checkRecipe();
		}

		boolean hasRecipe = (currentRecipe != null);
		boolean hasResources = true;
		boolean canAdd = true;

		if (hasRecipe) {
			hasResources = removeResources(false);

			ItemStack pendingProduct = currentRecipe.getCraftingGridRecipe().getRecipeOutput();
			canAdd = InventoryUtil.tryAddStack(this, pendingProduct, SLOT_PRODUCT, SLOT_PRODUCT_COUNT, true, false);
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NORECIPE);
		errorLogic.setCondition(!hasResources, EnumErrorCode.NORESOURCE);
		errorLogic.setCondition(!canAdd, EnumErrorCode.NOSPACE);

		return hasRecipe && hasResources && canAdd;
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

	private static class CarpenterInventoryAdapter extends TileInventoryAdapter<TileCarpenter> {
		public CarpenterInventoryAdapter(TileCarpenter carpenter) {
			super(carpenter, 30, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_CAN_INPUT) {
				Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
				return tile.tankManager.accepts(fluid);
			} else if (slotIndex == SLOT_BOX) {
				return CarpenterRecipeManager.isBox(itemStack);
			} else if (canSlotAccept(SLOT_CAN_INPUT, itemStack) || canSlotAccept(SLOT_BOX, itemStack)) {
				return false;
			}

			return SlotUtil.isSlotInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return slotIndex == SLOT_PRODUCT;
		}
	}

}
