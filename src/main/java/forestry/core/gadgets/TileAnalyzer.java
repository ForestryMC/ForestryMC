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
package forestry.core.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IIndividual;
import forestry.core.EnumErrorCode;
import forestry.core.config.Defaults;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.GuiId;
import forestry.core.network.PacketPayload;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.GuiUtil;
import forestry.plugins.PluginApiculture;

public class TileAnalyzer extends TilePowered implements ISidedInventory, ILiquidTankContainer {

	/* CONSTANTS */
	public static final int TIME_TO_ANALYZE = 125;
	public static final int HONEY_REQUIRED = 100;

	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_CAN = 1;
	public static final short SLOT_INPUT_1 = 2;
	public static final short SLOT_INPUT_COUNT = 6;
	public static final short SLOT_OUTPUT_1 = 8;
	public static final short SLOT_OUTPUT_COUNT = 4;

	/* MEMBER */
	private int analyzeTime;

	public final FilteredTank resourceTank;

	private final TankManager tankManager;

	private final IInventory invInput;
	private final IInventory invOutput;

	/* CONSTRUCTOR */
	public TileAnalyzer() {
		super(800, 40, Defaults.MACHINE_MAX_ENERGY);
		setInternalInventory(new TileInventoryAdapter(this, 12, "Items") {
			@Override
			public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
				if (GuiUtil.isIndexInRange(slotIndex, SLOT_INPUT_1, SLOT_INPUT_COUNT)) {
					return AlleleManager.alleleRegistry.isIndividual(itemStack) || GeneticsUtil.getGeneticEquivalent(itemStack) != null;
				} else if (slotIndex == SLOT_CAN) {
					Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
					return resourceTank.accepts(fluid);
				}

				return false;
			}

			@Override
			public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
				return GuiUtil.isIndexInRange(slotIndex, SLOT_OUTPUT_1, SLOT_OUTPUT_COUNT);
			}
		});
		resourceTank = new FilteredTank(Defaults.PROCESSOR_TANK_CAPACITY, Fluids.HONEY.getFluid());
		tankManager = new TankManager(resourceTank);
		invInput = new InventoryMapper(getInternalInventory(), SLOT_INPUT_1, SLOT_INPUT_COUNT);
		invOutput = new InventoryMapper(getInternalInventory(), SLOT_OUTPUT_1, SLOT_OUTPUT_COUNT);
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.AnalyzerGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("AnalyzeTime", analyzeTime);

		tankManager.writeTanksToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		analyzeTime = nbttagcompound.getInteger("AnalyzeTime");

		tankManager.readTanksFromNBT(nbttagcompound);
	}

	@Override
	protected void updateServerSide() {
		if (updateOnInterval(20)) {
			// Check if we have suitable items waiting in the can slot
			FluidHelper.drainContainers(tankManager, this, SLOT_CAN);
		}
	}

	/* WORKING */
	@Override
	public boolean workCycle() {
		ItemStack stackToAnalyze = getStackInSlot(SLOT_ANALYZE);

		if (stackToAnalyze != null) {
			IIndividual individual = AlleleManager.alleleRegistry.getIndividual(stackToAnalyze);
			if (individual == null) {
				setErrorState(EnumErrorCode.UNKNOWN);
				return false;
			}

			if (analyzeTime > 0) {
				analyzeTime--;
				return true;
			} else {
				// Analysis complete.
				if (!individual.isAnalyzed()) {
					individual.analyze();

					NBTTagCompound nbttagcompound = new NBTTagCompound();
					individual.writeToNBT(nbttagcompound);
					stackToAnalyze.setTagCompound(nbttagcompound);
				}

				if (InvTools.tryAddStack(invOutput, stackToAnalyze, true)) {
					setInventorySlotContents(SLOT_ANALYZE, null);
					sendNetworkUpdate();
					return true;
				} else {
					setErrorState(EnumErrorCode.NOSPACE);
					return false;
				}
			}
		}

		// Look for bees in input slots.
		IInvSlot slot = getInputSlot();
		if (slot == null) {
			// Nothing to analyze
			setErrorState(EnumErrorCode.NOTHINGANALYZE);
			return false;
		}

		ItemStack inputStack = slot.getStackInSlot();
		ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(inputStack);
		if (ersatz != null) {
			inputStack = ersatz;
		}
		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(inputStack);
		if (!individual.isAnalyzed()) {
			if (resourceTank.getFluidAmount() < HONEY_REQUIRED) {
				setErrorState(EnumErrorCode.NORESOURCE);
				return false;
			}
			resourceTank.drain(HONEY_REQUIRED, true);
			analyzeTime = TIME_TO_ANALYZE;
		}
		setInventorySlotContents(SLOT_ANALYZE, inputStack);
		slot.setStackInSlot(null);
		setErrorState(EnumErrorCode.OK);
		sendNetworkUpdate();
		return true;
	}

	private IInvSlot getInputSlot() {
		for (IInvSlot slot : InventoryIterator.getIterable(invInput)) {
			ItemStack inputStack = slot.getStackInSlot();
			if (inputStack != null && (AlleleManager.alleleRegistry.isIndividual(inputStack) || GeneticsUtil.getGeneticEquivalent(inputStack) != null)) {
				return slot;
			}
		}
		return null;
	}

	/* Network */
	public PacketPayload getPacketPayload() {
		ItemStack displayStack = getIndividualOnDisplay();
		if (displayStack == null) {
			return null;
		}

		IIndividual displayIndividual = AlleleManager.alleleRegistry.getIndividual(displayStack);
		int type = PluginApiculture.beeInterface.getType(displayStack).ordinal();

		PacketPayload payload = new PacketPayload();
		payload.stringPayload = new String[1];
		payload.shortPayload = new short[2];
		payload.stringPayload[0] = displayIndividual.getIdent();
		payload.shortPayload[0] = (short) type;
		payload.shortPayload[1] = (short) displayStack.stackSize;
		return payload;
	}

	public void fromPacketPayload(PacketPayload payload) {
		ItemStack newIndividualOnDisplay = null;
		if (!payload.isEmpty()) {
			String individualUID = payload.stringPayload[0];
			int type = payload.shortPayload[0];
			int stackSize = payload.shortPayload[1];

			IAllele[] template = PluginApiculture.beeInterface.getTemplate(individualUID);
			IIndividual individual = PluginApiculture.beeInterface.templateAsIndividual(template);

			newIndividualOnDisplay = PluginApiculture.beeInterface.getMemberStack(individual, type);
			newIndividualOnDisplay.stackSize = stackSize;
		}

		if (!ItemStack.areItemStacksEqual(newIndividualOnDisplay, individualOnDisplayClient)) {
			individualOnDisplayClient = newIndividualOnDisplay;
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		}
	}

	/* STATE INFORMATION */
	// @Override
	@Override
	public boolean isWorking() {
		return analyzeTime > 0;
	}

	@Override
	public boolean hasWork() {
		return getErrorState() == EnumErrorCode.OK || getErrorState() == EnumErrorCode.NOPOWER;
	}

	public int getProgressScaled(int i) {
		return (analyzeTime * i) / TIME_TO_ANALYZE;
	}

	private ItemStack individualOnDisplayClient;

	public ItemStack getIndividualOnDisplay() {
		if (worldObj.isRemote) {
			return individualOnDisplayClient;
		}
		return getStackInSlot(SLOT_ANALYZE);
	}

	/* ILiquidTankContainer */
	@Override
	public void getGUINetworkData(int i, int j) {
		i -= tankManager.maxMessageId() + 1;
		switch (i) {
			case 0:
				analyzeTime = j;
				break;
		}
	}

	@Override
	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		int i = tankManager.maxMessageId() + 1;
		iCrafting.sendProgressBarUpdate(container, i, analyzeTime);

	}

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
}
