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

import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.api.arboriculture.TreeManager;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.features.CoreTiles;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.ForestryFluids;
import forestry.core.fluids.TankManager;
import forestry.core.gui.ContainerAnalyzer;
import forestry.core.inventory.InventoryAnalyzer;
import forestry.core.inventory.wrappers.InventoryMapper;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.NetworkUtil;

import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;
import genetics.utils.RootUtils;

public class TileAnalyzer extends TilePowered implements WorldlyContainer, ILiquidTankTile, IItemStackDisplay {
	private static final int TIME_TO_ANALYZE = 125;
	private static final int HONEY_REQUIRED = 100;

	private final FilteredTank resourceTank;
	private final TankManager tankManager;
	private final Container invInput;
	private final Container invOutput;

	@Nullable
	private IIndividual specimenToAnalyze;
	private ItemStack individualOnDisplayClient = ItemStack.EMPTY;

	/* CONSTRUCTOR */
	public TileAnalyzer(BlockPos pos, BlockState state) {
		super(CoreTiles.ANALYZER.tileType(), pos, state, 800, Constants.MACHINE_MAX_ENERGY);
		setInternalInventory(new InventoryAnalyzer(this));
		resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(ForestryFluids.HONEY.getFluid());
		tankManager = new TankManager(this, resourceTank);
		invInput = new InventoryMapper(getInternalInventory(), InventoryAnalyzer.SLOT_INPUT_1, InventoryAnalyzer.SLOT_INPUT_COUNT);
		invOutput = new InventoryMapper(getInternalInventory(), InventoryAnalyzer.SLOT_OUTPUT_1, InventoryAnalyzer.SLOT_OUTPUT_COUNT);
	}

	/* SAVING & LOADING */

	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);
		tankManager.write(compoundNBT);
	}

	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);
		tankManager.read(compoundNBT);

		ItemStack stackToAnalyze = getItem(InventoryAnalyzer.SLOT_ANALYZE);
		if (!stackToAnalyze.isEmpty()) {
			specimenToAnalyze = RootUtils.getIndividualOrNull(stackToAnalyze);
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
		ItemStack stackToAnalyze = getItem(InventoryAnalyzer.SLOT_ANALYZE);
		if (stackToAnalyze.isEmpty() || specimenToAnalyze == null) {
			return false;
		}

		if (!specimenToAnalyze.isAnalyzed()) {
			FluidStack drained = resourceTank.drain(HONEY_REQUIRED, IFluidHandler.FluidAction.SIMULATE);
			if (drained.isEmpty() || drained.getAmount() != HONEY_REQUIRED) {
				return false;
			}
			resourceTank.drain(HONEY_REQUIRED, IFluidHandler.FluidAction.EXECUTE);

			specimenToAnalyze.analyze();

			GeneticHelper.setIndividual(stackToAnalyze, specimenToAnalyze);
		}

		boolean added = InventoryUtil.tryAddStack(invOutput, stackToAnalyze, true);
		if (!added) {
			return false;
		}

		setItem(InventoryAnalyzer.SLOT_ANALYZE, ItemStack.EMPTY);
		PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
		NetworkUtil.sendNetworkPacket(packet, worldPosition, level);

		return true;
	}

	@Nullable
	private Integer getInputSlotIndex() {
		for (int slotIndex = 0; slotIndex < invInput.getContainerSize(); slotIndex++) {
			ItemStack inputStack = invInput.getItem(slotIndex);
			if (RootUtils.isIndividual(inputStack)) {
				return slotIndex;
			}
		}
		return null;
	}

	/* Network */
	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		ItemStack displayStack = getIndividualOnDisplay();
		data.writeItem(displayStack);
		tankManager.writeData(data);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		individualOnDisplayClient = data.readItem();
		tankManager.readData(data);
	}

	@Override
	public void handleItemStackForDisplay(ItemStack itemStack) {
		if (!ItemStack.matches(itemStack, individualOnDisplayClient)) {
			individualOnDisplayClient = itemStack;
			//TODO
			BlockPos pos = getBlockPos();
			Minecraft.getInstance().levelRenderer.setSectionDirty(pos.getX(), pos.getY(), pos.getZ());
			//			world.markForRerender(getPos());
		}
	}

	/* STATE INFORMATION */
	@Override
	public boolean hasWork() {
		moveSpecimenToAnalyzeSlot();

		ItemStack specimen = getItem(InventoryAnalyzer.SLOT_ANALYZE);

		boolean hasSpecimen = !specimen.isEmpty();
		boolean hasResource = true;
		boolean hasSpace = true;

		if (hasSpecimen) {
			hasSpace = InventoryUtil.tryAddStack(invOutput, specimen, true, false);

			if (specimenToAnalyze != null && !specimenToAnalyze.isAnalyzed()) {
				FluidStack drained = resourceTank.drain(HONEY_REQUIRED, IFluidHandler.FluidAction.SIMULATE);
				hasResource = !drained.isEmpty() && drained.getAmount() == HONEY_REQUIRED;
			}
		}

		getErrorLogic().setCondition(!hasSpecimen, EnumErrorCode.NO_SPECIMEN);
		getErrorLogic().setCondition(!hasResource, EnumErrorCode.NO_RESOURCE_LIQUID);
		getErrorLogic().setCondition(!hasSpace, EnumErrorCode.NO_SPACE_INVENTORY);

		return hasSpecimen && hasResource && hasSpace;
	}

	private void moveSpecimenToAnalyzeSlot() {
		if (!getItem(InventoryAnalyzer.SLOT_ANALYZE).isEmpty()) {
			return;
		}

		Integer slotIndex = getInputSlotIndex();
		if (slotIndex == null) {
			return;
		}

		ItemStack inputStack = invInput.getItem(slotIndex);
		if (inputStack.isEmpty()) {
			return;
		}

		if (true && !TreeManager.treeRoot.isMember(inputStack)) {
			inputStack = GeneticsUtil.convertToGeneticEquivalent(inputStack);
		}

		specimenToAnalyze = RootUtils.getIndividualOrNull(inputStack);
		if (specimenToAnalyze == null) {
			return;
		}

		setItem(InventoryAnalyzer.SLOT_ANALYZE, inputStack);
		invInput.setItem(slotIndex, ItemStack.EMPTY);

		if (specimenToAnalyze.isAnalyzed()) {
			setTicksPerWorkCycle(1);
			setEnergyPerWorkCycle(0);
		} else {
			setTicksPerWorkCycle(TIME_TO_ANALYZE);
			setEnergyPerWorkCycle(Config.analyzerEnergyPerWork);
		}

		PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
		NetworkUtil.sendNetworkPacket(packet, worldPosition, level);
	}

	public ItemStack getIndividualOnDisplay() {
		if (level.isClientSide) {
			return individualOnDisplayClient;
		}
		return getItem(InventoryAnalyzer.SLOT_ANALYZE);
	}

	/* ILiquidTankTile */

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> tankManager).cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerAnalyzer(windowId, player.getInventory(), this);
	}
}
