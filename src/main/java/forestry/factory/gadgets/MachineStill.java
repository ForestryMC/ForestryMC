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
package forestry.factory.gadgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.core.ForestryAPI;
import forestry.api.recipes.IStillManager;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.gadgets.TileBase;
import forestry.core.gadgets.TilePowered;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.Utils;

public class MachineStill extends TilePowered implements ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final short SLOT_PRODUCT = 0;
	public static final short SLOT_RESOURCE = 1;
	public static final short SLOT_CAN = 2;

	public static class Recipe {

		public final int timePerUnit;
		public final FluidStack input;
		public final FluidStack output;

		public Recipe(int timePerUnit, FluidStack input, FluidStack output) {
			this.timePerUnit = timePerUnit;
			if (input == null) {
				throw new IllegalArgumentException("Still recipes need an input. Input was null.");
			}
			if (output == null) {
				throw new IllegalArgumentException("Still recipes need an output. Output was null.");
			}
			this.input = input;
			this.output = output;
		}

		public boolean matches(FluidStack res) {
			if (res == null) {
				return false;
			}

			return input.isFluidEqual(res);
		}
	}

	public static class RecipeManager implements IStillManager {

		public static final ArrayList<MachineStill.Recipe> recipes = new ArrayList<MachineStill.Recipe>();
		public static final HashSet<Fluid> recipeFluidInputs = new HashSet<Fluid>();
		public static final HashSet<Fluid> recipeFluidOutputs = new HashSet<Fluid>();

		@Override
		public void addRecipe(int timePerUnit, FluidStack input, FluidStack output) {
			recipes.add(new MachineStill.Recipe(timePerUnit, input, output));
			if (input != null) {
				recipeFluidInputs.add(input.getFluid());
			}
			if (output != null) {
				recipeFluidOutputs.add(output.getFluid());
			}
		}

		public static Recipe findMatchingRecipe(FluidStack item) {
			for (Recipe recipe : recipes) {
				if (recipe.matches(item)) {
					return recipe;
				}
			}
			return null;
		}

		public static boolean isInput(FluidStack res) {
			return recipeFluidInputs.contains(res.getFluid());
		}

		@Override
		public Map<Object[], Object[]> getRecipes() {
			HashMap<Object[], Object[]> recipeList = new HashMap<Object[], Object[]>();

			for (Recipe recipe : recipes) {
				recipeList.put(new Object[]{recipe.input}, new Object[]{recipe.output});
			}

			return recipeList;
		}
	}

	/* MEMBER */
	public final FilteredTank resourceTank;
	public final FilteredTank productTank;
	private final TankManager tankManager;

	private Recipe currentRecipe;
	private FluidStack bufferedLiquid;
	public int distillationTime = 0;
	public int distillationTotalTime = 0;

	public MachineStill() {
		super(1100, 50, 8000);
		setInternalInventory(new TileInventoryAdapter(this, 3, "Items") {
			@Override
			public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
				if (slotIndex == SLOT_RESOURCE) {
					return FluidHelper.isEmptyContainer(itemStack);
				} else if (slotIndex == SLOT_CAN) {
					Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
					return resourceTank.accepts(fluid);
				}
				return false;
			}

			@Override
			public boolean canExtractItem(int slotIndex, ItemStack itemstack, EnumFacing side) {
				return slotIndex == SLOT_PRODUCT;
			}
		});
		setHints(Config.hints.get("still"));
		resourceTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, RecipeManager.recipeFluidInputs);
		resourceTank.tankMode = StandardTank.TankMode.INPUT;
		productTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, RecipeManager.recipeFluidOutputs);
		productTank.tankMode = StandardTank.TankMode.OUTPUT;
		tankManager = new TankManager(resourceTank, productTank);
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.StillGUI.ordinal(), player.worldObj, pos.getX(), pos.getY(), pos.getZ());
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
	public void updateServerSide() {

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
		if (getErrorState() == EnumErrorCode.NORECIPE && currentRecipe != null) {
			setErrorState(EnumErrorCode.OK);
		}

		if (energyManager.getTotalEnergyStored() == 0) {
			setErrorState(EnumErrorCode.NOPOWER);
		}
	}

	@Override
	public boolean workCycle() {

		checkRecipe();

		// Ongoing process
		if (distillationTime > 0 && currentRecipe != null) {

			distillationTime -= currentRecipe.input.amount;
			productTank.fill(currentRecipe.output, true);

			setErrorState(EnumErrorCode.OK);
			return true;

		} else if (currentRecipe != null) {

			int resourceRequired = currentRecipe.timePerUnit * currentRecipe.input.amount;

			if (productTank.fill(currentRecipe.output, false) < currentRecipe.output.amount) {
				setErrorState(EnumErrorCode.NOSPACETANK);
			} else if (resourceTank.getFluidAmount() < resourceRequired) {
				setErrorState(EnumErrorCode.NORESOURCE);
			} else {
				// Start next cycle if enough bio mass is available
				distillationTime = distillationTotalTime = resourceRequired;
				resourceTank.drain(resourceRequired, true);
				bufferedLiquid = new FluidStack(currentRecipe.input.fluidID, resourceRequired);

				setErrorState(EnumErrorCode.OK);
				return true;
			}
		}

		bufferedLiquid = null;
		return false;
	}

	public void checkRecipe() {
		Recipe sameRec = RecipeManager.findMatchingRecipe(resourceTank.getFluid());

		if (sameRec == null && bufferedLiquid != null && distillationTime > 0) {
			sameRec = RecipeManager.findMatchingRecipe(new FluidStack(bufferedLiquid.fluidID, distillationTime));
		}

		if (sameRec == null) {
			setErrorState(EnumErrorCode.NORECIPE);
		}

		if (currentRecipe != sameRec) {
			currentRecipe = sameRec;
			resetRecipe();
		}
	}

	private void resetRecipe() {
	}

	@Override
	public boolean isWorking() {
		return distillationTime > 0 || currentRecipe != null && productTank.getFluidAmount() + currentRecipe.output.amount <= Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public boolean hasWork() {
		if (currentRecipe == null) {
			return false;
		}

		return (distillationTime > 0 || resourceTank.getFluidAmount() >= currentRecipe.timePerUnit * currentRecipe.input.amount)
				&& productTank.getFluidAmount() <= productTank.getCapacity() - currentRecipe.output.amount;
	}

	public int getDistillationProgressScaled(int i) {
		if (distillationTotalTime == 0) {
			return i;
		}

		return (distillationTime * i) / distillationTotalTime;
	}

	public int getResourceScaled(int i) {
		return (resourceTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	public int getProductScaled(int i) {
		return (productTank.getFluidAmount() * i) / Defaults.PROCESSOR_TANK_CAPACITY;
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return Utils.rateTankLevel(getResourceScaled(100));
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return Utils.rateTankLevel(getProductScaled(100));
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
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		return tankManager.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		return tankManager.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int quantityMax, boolean doEmpty) {
		return tankManager.drain(from, quantityMax, doEmpty);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return tankManager.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return tankManager.canDrain(from, fluid);
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return tankManager.getTankInfo(from);
	}

}
