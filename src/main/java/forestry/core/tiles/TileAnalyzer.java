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
package forestry.core.tiles;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.gui.ContainerAnalyzer;
import forestry.core.gui.GuiAnalyzer;
import forestry.core.inventory.InventoryAnalyzer;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.InventoryUtil;
import forestry.plugins.PluginManager;

public class TileAnalyzer extends TilePowered implements ISidedInventory, ILiquidTankTile, IFluidHandler, IItemStackDisplay {
	private static final int TIME_TO_ANALYZE = 125;
	private static final int HONEY_REQUIRED = 100;
	private static final int ENERGY_PER_WORK_CYCLE = 20320;

	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	private final IInventory invInput;
	private final IInventory invOutput;

	private IIndividual specimenToAnalyze;
	private ItemStack individualOnDisplayClient;

	/* CONSTRUCTOR */
	public TileAnalyzer() {
		super("analyzer", 800, Constants.MACHINE_MAX_ENERGY);
		setInternalInventory(new InventoryAnalyzer(this));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, Fluids.HONEY.getFluid());
		tankManager = new TankManager(this, resourceTank);
		invInput = new InventoryMapper(getInternalInventory(), InventoryAnalyzer.SLOT_INPUT_1, InventoryAnalyzer.SLOT_INPUT_COUNT);
		invOutput = new InventoryMapper(getInternalInventory(), InventoryAnalyzer.SLOT_OUTPUT_1, InventoryAnalyzer.SLOT_OUTPUT_COUNT);
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

		ItemStack stackToAnalyze = getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);
		if (stackToAnalyze != null) {
			specimenToAnalyze = AlleleManager.alleleRegistry.getIndividual(stackToAnalyze);
		}
	}

	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (updateOnInterval(20)) {
			// Check if we have suitable items waiting in the can slot
			FluidHelper.drainContainers(tankManager, this, InventoryAnalyzer.SLOT_CAN);
		}
	}

	/* WORKING */
	@Override
	public boolean workCycle() {
		ItemStack stackToAnalyze = getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);
		if (stackToAnalyze == null) {
			return false;
		}

		if (!specimenToAnalyze.isAnalyzed()) {
			if (!resourceTank.canDrain(HONEY_REQUIRED)) {
				return false;
			}
			resourceTank.drain(HONEY_REQUIRED, true);

			specimenToAnalyze.analyze();

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			specimenToAnalyze.writeToNBT(nbttagcompound);
			stackToAnalyze.setTagCompound(nbttagcompound);
		}

		boolean added = InventoryUtil.tryAddStack(invOutput, stackToAnalyze, true);
		if (!added) {
			return false;
		}

		setInventorySlotContents(InventoryAnalyzer.SLOT_ANALYZE, null);
		PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
		Proxies.net.sendNetworkPacket(packet, worldObj);

		return true;
	}

	private IInvSlot getInputSlot() {
		for (IInvSlot slot : InventoryIterator.getIterable(invInput)) {
			ItemStack inputStack = slot.getStackInSlot();
			if (AlleleManager.alleleRegistry.isIndividual(inputStack)) {
				return slot;
			}
		}
		return null;
	}

	/* Network */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		ItemStack displayStack = getIndividualOnDisplay();
		data.writeItemStack(displayStack);
		tankManager.writeData(data);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		individualOnDisplayClient = data.readItemStack();
		tankManager.readData(data);
	}

	@Override
	public void handleItemStackForDisplay(ItemStack itemStack) {
		if (!ItemStack.areItemStacksEqual(itemStack, individualOnDisplayClient)) {
			individualOnDisplayClient = itemStack;
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		}
	}

	/* STATE INFORMATION */
	@Override
	public boolean hasWork() {
		moveSpecimenToAnalyzeSlot();

		ItemStack specimen = getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);

		boolean hasSpecimen = (specimen != null);
		boolean hasResource = true;
		boolean hasSpace = true;

		if (hasSpecimen) {
			hasSpace = InventoryUtil.tryAddStack(invOutput, specimen, true, false);

			if (!specimenToAnalyze.isAnalyzed()) {
				hasResource = resourceTank.canDrain(HONEY_REQUIRED);
			}
		}

		getErrorLogic().setCondition(!hasSpecimen, EnumErrorCode.NO_SPECIMEN);
		getErrorLogic().setCondition(!hasResource, EnumErrorCode.NO_RESOURCE_LIQUID);
		getErrorLogic().setCondition(!hasSpace, EnumErrorCode.NO_SPACE_INVENTORY);

		return hasSpecimen && hasResource && hasSpace;
	}

	private void moveSpecimenToAnalyzeSlot() {
		if (getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE) != null) {
			return;
		}

		IInvSlot slot = getInputSlot();
		if (slot == null) {
			return;
		}

		ItemStack inputStack = slot.getStackInSlot();
		if (inputStack == null) {
			return;
		}

		if (PluginManager.Module.ARBORICULTURE.isEnabled() && !TreeManager.treeRoot.isMember(inputStack)) {
			ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(inputStack);
			if (ersatz != null) {
				inputStack = ersatz;
			}
		}

		specimenToAnalyze = AlleleManager.alleleRegistry.getIndividual(inputStack);
		if (specimenToAnalyze == null) {
			return;
		}

		setInventorySlotContents(InventoryAnalyzer.SLOT_ANALYZE, inputStack);
		slot.setStackInSlot(null);

		if (specimenToAnalyze.isAnalyzed()) {
			setTicksPerWorkCycle(1);
			setEnergyPerWorkCycle(0);
		} else {
			setTicksPerWorkCycle(TIME_TO_ANALYZE);
			setEnergyPerWorkCycle(ENERGY_PER_WORK_CYCLE);
		}

		PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
		Proxies.net.sendNetworkPacket(packet, worldObj);
	}

	public ItemStack getIndividualOnDisplay() {
		if (worldObj.isRemote) {
			return individualOnDisplayClient;
		}
		return getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);
	}

	/* ILiquidTankTile */
	@Override
	public TankManager getTankManager() {
		return tankManager;
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
		return new GuiAnalyzer(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerAnalyzer(player.inventory, this);
	}
}
