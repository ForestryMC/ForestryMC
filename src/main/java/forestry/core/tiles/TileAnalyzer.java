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
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import genetics.api.GeneticHelper;
import genetics.api.individual.IIndividual;
import genetics.utils.RootUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.io.IOException;

public class TileAnalyzer extends TilePowered implements ISidedInventory, ILiquidTankTile, IItemStackDisplay {
    private static final int TIME_TO_ANALYZE = 125;
    private static final int HONEY_REQUIRED = 100;

    private final FilteredTank resourceTank;
    private final TankManager tankManager;
    private final IInventory invInput;
    private final IInventory invOutput;

    @Nullable
    private IIndividual specimenToAnalyze;
    private ItemStack individualOnDisplayClient = ItemStack.EMPTY;

    /* CONSTRUCTOR */
    public TileAnalyzer() {
        super(CoreTiles.ANALYZER.tileType(), 800, Constants.MACHINE_MAX_ENERGY);
        setInternalInventory(new InventoryAnalyzer(this));
        resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(ForestryFluids.HONEY.getFluid());
        tankManager = new TankManager(this, resourceTank);
        invInput = new InventoryMapper(
                getInternalInventory(),
                InventoryAnalyzer.SLOT_INPUT_1,
                InventoryAnalyzer.SLOT_INPUT_COUNT
        );
        invOutput = new InventoryMapper(
                getInternalInventory(),
                InventoryAnalyzer.SLOT_OUTPUT_1,
                InventoryAnalyzer.SLOT_OUTPUT_COUNT
        );
    }

    /* SAVING & LOADING */

    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);
        tankManager.write(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);
        tankManager.read(compoundNBT);

        ItemStack stackToAnalyze = getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);
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
        ItemStack stackToAnalyze = getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);
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

        setInventorySlotContents(InventoryAnalyzer.SLOT_ANALYZE, ItemStack.EMPTY);
        PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
        NetworkUtil.sendNetworkPacket(packet, pos, world);

        return true;
    }

    @Nullable
    private Integer getInputSlotIndex() {
        for (int slotIndex = 0; slotIndex < invInput.getSizeInventory(); slotIndex++) {
            ItemStack inputStack = invInput.getStackInSlot(slotIndex);
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
        data.writeItemStack(displayStack);
        tankManager.writeData(data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readData(PacketBufferForestry data) throws IOException {
        super.readData(data);
        individualOnDisplayClient = data.readItemStack();
        tankManager.readData(data);
    }

    @Override
    public void handleItemStackForDisplay(ItemStack itemStack) {
        if (!ItemStack.areItemStacksEqual(itemStack, individualOnDisplayClient)) {
            individualOnDisplayClient = itemStack;
            //TODO
            BlockPos pos = getPos();
            Minecraft.getInstance().worldRenderer.markForRerender(pos.getX(), pos.getY(), pos.getZ());
            //			world.markForRerender(getPos());
        }
    }

    /* STATE INFORMATION */
    @Override
    public boolean hasWork() {
        moveSpecimenToAnalyzeSlot();

        ItemStack specimen = getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);

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
        if (!getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE).isEmpty()) {
            return;
        }

        Integer slotIndex = getInputSlotIndex();
        if (slotIndex == null) {
            return;
        }

        ItemStack inputStack = invInput.getStackInSlot(slotIndex);
        if (inputStack.isEmpty()) {
            return;
        }

        if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE) && !TreeManager.treeRoot.isMember(inputStack)) {
            inputStack = GeneticsUtil.convertToGeneticEquivalent(inputStack);
        }

        specimenToAnalyze = RootUtils.getIndividualOrNull(inputStack);
        if (specimenToAnalyze == null) {
            return;
        }

        setInventorySlotContents(InventoryAnalyzer.SLOT_ANALYZE, inputStack);
        invInput.setInventorySlotContents(slotIndex, ItemStack.EMPTY);

        if (specimenToAnalyze.isAnalyzed()) {
            setTicksPerWorkCycle(1);
            setEnergyPerWorkCycle(0);
        } else {
            setTicksPerWorkCycle(TIME_TO_ANALYZE);
            setEnergyPerWorkCycle(Config.analyzerEnergyPerWork);
        }

        PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
        NetworkUtil.sendNetworkPacket(packet, pos, world);
    }

    public ItemStack getIndividualOnDisplay() {
        if (world.isRemote) {
            return individualOnDisplayClient;
        }
        return getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);
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
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerAnalyzer(windowId, player.inventory, this);
    }
}
