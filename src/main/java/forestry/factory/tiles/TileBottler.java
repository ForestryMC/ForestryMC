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

import com.google.common.base.Objects;

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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.common.Optional;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.factory.triggers.FactoryTriggers;

import buildcraft.api.statements.ITriggerExternal;

public class TileBottler extends TilePowered implements ISidedInventory, ILiquidTankTile, IFluidHandler {
	private static final int TICKS_PER_RECIPE_TIME = 5;
	private static final int ENERGY_PER_RECIPE_TIME = 1000;

	public static final short SLOT_INPUT_EMPTY_CAN = 0;
	public static final short SLOT_OUTPUT = 1;
	public static final short SLOT_INPUT_FULL_CAN = 2;

	private final StandardTank resourceTank;
	private final TankManager tankManager;

	private BottlerRecipe currentRecipe;

	public TileBottler() {
		super(1100, 4000, 200);

		setInternalInventory(new BottlerInventoryAdapter(this));

		setHints(Config.hints.get("bottler"));
		resourceTank = new StandardTank(Constants.PROCESSOR_TANK_CAPACITY);
		tankManager = new TankManager(this, resourceTank);
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.BottlerGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
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
			FluidHelper.drainContainers(tankManager, this, SLOT_INPUT_FULL_CAN);
		}
	}

	@Override
	public boolean workCycle() {
		FluidHelper.FillStatus status = FluidHelper.fillContainers(tankManager, this, SLOT_INPUT_EMPTY_CAN, SLOT_OUTPUT, currentRecipe.input.getFluid());
		return status == FluidHelper.FillStatus.SUCCESS;
	}

	private void checkRecipe() {
		ItemStack emptyCan = getStackInSlot(SLOT_INPUT_EMPTY_CAN);
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
		if (inventory.getStackInSlot(SLOT_INPUT_EMPTY_CAN) == null) {
			return false;
		}

		return ((float) inventory.getStackInSlot(SLOT_INPUT_EMPTY_CAN).stackSize / (float) inventory.getStackInSlot(SLOT_INPUT_EMPTY_CAN).getMaxStackSize()) > percentage;
	}

	@Override
	public boolean hasWork() {
		checkRecipe();

		IErrorLogic errorLogic = getErrorLogic();

		FluidHelper.FillStatus status;

		if (errorLogic.setCondition(currentRecipe == null, EnumErrorCode.NORECIPE)) {
			status = FluidHelper.FillStatus.SUCCESS;
		} else {
			status = FluidHelper.fillContainers(tankManager, this, SLOT_INPUT_EMPTY_CAN, SLOT_OUTPUT, currentRecipe.input.getFluid(), false);
		}

		errorLogic.setCondition(status == FluidHelper.FillStatus.NO_FLUID, EnumErrorCode.NORESOURCE);
		errorLogic.setCondition(status == FluidHelper.FillStatus.NO_SPACE, EnumErrorCode.NOSPACE);
		return currentRecipe != null && status == FluidHelper.FillStatus.SUCCESS;
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

	private static class BottlerInventoryAdapter extends TileInventoryAdapter<TileBottler> {
		public BottlerInventoryAdapter(TileBottler tileBottler) {
			super(tileBottler, 3, "Items");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (slotIndex == SLOT_INPUT_EMPTY_CAN) {
				return FluidHelper.isFillableContainer(itemStack);
			} else if (slotIndex == SLOT_INPUT_FULL_CAN) {
				FluidStack fluidStack = FluidHelper.getFluidStackInContainer(itemStack);
				return fluidStack != null && FluidRegistry.isFluidRegistered(fluidStack.getFluid());
			}
			return false;
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
			return slotIndex == SLOT_OUTPUT;
		}
	}

	public static class BottlerRecipe {
		public final FluidStack input;
		public final ItemStack empty;
		public final ItemStack filled;

		private BottlerRecipe(ItemStack empty, FluidStack input, ItemStack filled) {
			this.input = input;
			if (empty.getItem() instanceof IFluidContainerItem) {
				FluidStack emptyFluid = FluidHelper.getFluidStackInContainer(empty);
				if (emptyFluid != null) {
					this.input.amount -= emptyFluid.amount;
				}
				if (this.input.amount > Constants.BUCKET_VOLUME) {
					this.input.amount = Constants.BUCKET_VOLUME;
				}
			}
			this.empty = empty;
			this.filled = filled;
		}

		public boolean matches(ItemStack emptyCan, FluidStack resource) {
			if (emptyCan == null || resource == null || !emptyCan.isItemEqual(empty)) {
				return false;
			}

			if (empty.getItem() instanceof IFluidContainerItem) {
				return true;
			} else {
				return resource.containsFluid(input);
			}
		}

		public static BottlerRecipe getRecipe(FluidStack res, ItemStack empty) {
			if (res == null || empty == null) {
				return null;
			}

			ItemStack filled = FluidHelper.getFilledContainer(res.getFluid(), empty);
			if (filled == null) {
				return null;
			}

			FluidStack input = FluidHelper.getFluidStackInContainer(filled);
			if (input == null) {
				return null;
			}

			return new BottlerRecipe(empty, input, filled);
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this)
					.addValue(input.amount).addValue(input.getLocalizedName())
					.add("empty", empty)
					.add("filled", filled)
					.toString();
		}
	}

}
