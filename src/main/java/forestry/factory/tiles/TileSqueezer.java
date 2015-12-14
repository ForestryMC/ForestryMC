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
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.circuits.ISocketable;
import forestry.core.circuits.ISpeedUpgradable;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.PlayerUtil;
import forestry.factory.gui.ContainerSqueezer;
import forestry.factory.gui.GuiSqueezer;
import forestry.factory.inventory.InventorySqueezer;
import forestry.factory.recipes.SqueezerRecipeManager;

public class TileSqueezer extends TilePowered implements ISocketable, ISidedInventory, ILiquidTankTile, IFluidHandler, ISpeedUpgradable {
	private static final int TICKS_PER_RECIPE_TIME = 1;
	private static final int ENERGY_PER_WORK_CYCLE = 2000;
	private static final int ENERGY_PER_RECIPE_TIME = ENERGY_PER_WORK_CYCLE / 10;

	private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");

	private final TankManager tankManager;
	private final StandardTank productTank;
	private final InventorySqueezer inventory;

	private ISqueezerRecipe currentRecipe;

	public TileSqueezer() {
		super("squeezer", 1100, Constants.MACHINE_MAX_ENERGY);
		this.inventory = new InventorySqueezer(this);
		setInternalInventory(this.inventory);
		this.productTank = new StandardTank(Constants.PROCESSOR_TANK_CAPACITY);
		this.productTank.tankMode = StandardTank.TankMode.OUTPUT;
		this.tankManager = new TankManager(this, productTank);
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		tankManager.writeToNBT(nbttagcompound);
		sockets.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readFromNBT(nbttagcompound);
		sockets.readFromNBT(nbttagcompound);

		ItemStack chip = sockets.getStackInSlot(0);
		if (chip != null) {
			ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(chip);
			if (chipset != null) {
				chipset.onLoad(this);
			}
		}
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
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		super.writeGuiData(data);
		sockets.writeData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		super.readGuiData(data);
		sockets.readData(data);
	}

	// / WORKING
	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (updateOnInterval(20)) {
			inventory.fillContainers(productTank.getFluid(), tankManager);
		}
	}

	@Override
	public boolean workCycle() {
		EntityPlayer player = PlayerUtil.getPlayer(worldObj, getAccessHandler().getOwner());
		if (!inventory.removeResources(currentRecipe.getResources(), player)) {
			return false;
		}

		FluidStack resultFluid = currentRecipe.getFluidOutput();
		productTank.fill(resultFluid, true);

		if (currentRecipe.getRemnants() != null && (worldObj.rand.nextFloat() < currentRecipe.getRemnantsChance())) {
			ItemStack remnant = currentRecipe.getRemnants().copy();
			inventory.addRemnant(remnant, true);
		}

		return true;
	}

	private boolean checkRecipe() {
		ISqueezerRecipe matchingRecipe = null;
		if (inventory.hasResources()) {
			ItemStack[] resources = inventory.getResources();

			if (currentRecipe != null && ItemStackUtil.containsSets(currentRecipe.getResources(), resources, true, false) > 0) {
				matchingRecipe = currentRecipe;
			} else {
				matchingRecipe = SqueezerRecipeManager.findMatchingRecipe(resources);
			}
		}

		if (currentRecipe != matchingRecipe) {
			currentRecipe = matchingRecipe;
			if (currentRecipe != null) {
				int recipeTime = currentRecipe.getProcessingTime();
				setTicksPerWorkCycle(recipeTime * TICKS_PER_RECIPE_TIME);
				setEnergyPerWorkCycle(recipeTime * ENERGY_PER_RECIPE_TIME);
			}
		}

		getErrorLogic().setCondition(currentRecipe == null, EnumErrorCode.NO_RECIPE);
		return currentRecipe != null;
	}

	@Override
	public boolean hasWork() {
		checkRecipe();

		boolean hasResources = inventory.hasResources();
		boolean hasRecipe = true;
		boolean canFill = true;
		boolean canAdd = true;

		if (hasResources) {
			hasRecipe = (currentRecipe != null);
			if (hasRecipe) {
				FluidStack resultFluid = currentRecipe.getFluidOutput();
				canFill = productTank.canFill(resultFluid);

				if (currentRecipe.getRemnants() != null) {
					canAdd = inventory.addRemnant(currentRecipe.getRemnants(), false);
				}
			}
		}

		IErrorLogic errorLogic = getErrorLogic();
		errorLogic.setCondition(!hasResources, EnumErrorCode.NO_RESOURCE);
		errorLogic.setCondition(!hasRecipe, EnumErrorCode.NO_RECIPE);
		errorLogic.setCondition(!canFill, EnumErrorCode.NO_SPACE_TANK);
		errorLogic.setCondition(!canAdd, EnumErrorCode.NO_SPACE_INVENTORY);

		return hasResources && hasRecipe && canFill && canAdd;
	}

	@Override
	public TankRenderInfo getProductTankInfo() {
		return new TankRenderInfo(productTank);
	}

	/* ILIQUIDCONTAINER IMPLEMENTATION */
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

	/* ISocketable */
	@Override
	public int getSocketCount() {
		return sockets.getSizeInventory();
	}

	@Override
	public ItemStack getSocket(int slot) {
		return sockets.getStackInSlot(slot);
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {

		if (stack != null && !ChipsetManager.circuitRegistry.isChipset(stack)) {
			return;
		}

		// Dispose correctly of old chipsets
		if (sockets.getStackInSlot(slot) != null) {
			if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
				ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(sockets.getStackInSlot(slot));
				if (chipset != null) {
					chipset.onRemoval(this);
				}
			}
		}

		sockets.setInventorySlotContents(slot, stack);
		if (stack == null) {
			return;
		}

		ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(stack);
		if (chipset != null) {
			chipset.onInsertion(this);
		}
	}

	@Override
	public ICircuitSocketType getSocketType() {
		return CircuitSocketType.MACHINE;
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiSqueezer(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerSqueezer(player.inventory, this);
	}
}
