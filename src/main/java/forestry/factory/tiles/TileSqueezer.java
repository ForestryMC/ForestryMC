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

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
import forestry.core.fluids.StandardTank;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.network.PacketBufferForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.TilePowered;
import forestry.core.utils.ItemStackUtil;
import forestry.factory.gui.ContainerSqueezer;
import forestry.factory.gui.GuiSqueezer;
import forestry.factory.inventory.InventorySqueezer;
import forestry.factory.recipes.SqueezerRecipeManager;

public class TileSqueezer extends TilePowered implements ISocketable, ISidedInventory, ILiquidTankTile, ISpeedUpgradable {
	private static final int TICKS_PER_RECIPE_TIME = 1;
	private static final int ENERGY_PER_WORK_CYCLE = 2000;
	private static final int ENERGY_PER_RECIPE_TIME = ENERGY_PER_WORK_CYCLE / 10;

	private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");

	private final TankManager tankManager;
	private final StandardTank productTank;
	private final InventorySqueezer inventory;
	@Nullable
	private ISqueezerRecipe currentRecipe;

	public TileSqueezer() {
		super(1100, Constants.MACHINE_MAX_ENERGY);
		this.inventory = new InventorySqueezer(this);
		setInternalInventory(this.inventory);
		this.productTank = new StandardTank(Constants.PROCESSOR_TANK_CAPACITY, false, true);
		this.tankManager = new TankManager(this, productTank);
	}

	/* LOADING & SAVING */

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		tankManager.writeToNBT(nbttagcompound);
		sockets.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		tankManager.readFromNBT(nbttagcompound);
		sockets.readFromNBT(nbttagcompound);

		ItemStack chip = sockets.getStackInSlot(0);
		if (!chip.isEmpty()) {
			ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(chip);
			if (chipset != null) {
				chipset.onLoad(this);
			}
		}
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		tankManager.writeData(data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		tankManager.readData(data);
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		super.writeGuiData(data);
		sockets.writeData(data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readGuiData(PacketBufferForestry data) throws IOException {
		super.readGuiData(data);
		sockets.readData(data);
	}

	// / WORKING
	@Override
	public void updateServerSide() {
		super.updateServerSide();

		if (updateOnInterval(20)) {
			FluidStack fluid = productTank.getFluid();
			if (fluid != null) {
				inventory.fillContainers(fluid, tankManager);
			}
		}
	}

	@Override
	public boolean workCycle() {
		if (currentRecipe == null) {
			return false;
		}
		if (!inventory.removeResources(currentRecipe.getResources())) {
			return false;
		}

		FluidStack resultFluid = currentRecipe.getFluidOutput();
		productTank.fillInternal(resultFluid, true);

		if (!currentRecipe.getRemnants().isEmpty() && world.rand.nextFloat() < currentRecipe.getRemnantsChance()) {
			ItemStack remnant = currentRecipe.getRemnants().copy();
			inventory.addRemnant(remnant, true);
		}

		return true;
	}

	private boolean checkRecipe() {
		ISqueezerRecipe matchingRecipe = null;
		if (inventory.hasResources()) {
			NonNullList<ItemStack> resources = inventory.getResources();

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
			hasRecipe = currentRecipe != null;
			if (hasRecipe) {
				FluidStack resultFluid = currentRecipe.getFluidOutput();
				canFill = productTank.fillInternal(resultFluid, false) == resultFluid.amount;

				if (!currentRecipe.getRemnants().isEmpty()) {
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


	@Override
	public TankManager getTankManager() {
		return tankManager;
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
		if (stack.isEmpty() || ChipsetManager.circuitRegistry.isChipset(stack)) {
			// Dispose correctly of old chipsets
			if (!sockets.getStackInSlot(slot).isEmpty()) {
				if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
					ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(sockets.getStackInSlot(slot));
					if (chipset != null) {
						chipset.onRemoval(this);
					}
				}
			}

			sockets.setInventorySlotContents(slot, stack);
			if (!stack.isEmpty()) {
				ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(stack);
				if (chipset != null) {
					chipset.onInsertion(this);
				}
			}
		}
	}

	@Override
	public ICircuitSocketType getSocketType() {
		return CircuitSocketType.MACHINE;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tankManager);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiSqueezer(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerSqueezer(player.inventory, this);
	}
}
