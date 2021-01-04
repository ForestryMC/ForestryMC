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

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.IErrorLogic;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
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
import forestry.factory.features.FactoryTiles;
import forestry.factory.gui.ContainerSqueezer;
import forestry.factory.inventory.InventorySqueezer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.io.IOException;

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
        super(FactoryTiles.SQUEEZER.tileType(), 1100, Constants.MACHINE_MAX_ENERGY);
        this.inventory = new InventorySqueezer(this);
        setInternalInventory(this.inventory);
        this.productTank = new StandardTank(Constants.PROCESSOR_TANK_CAPACITY, false, true);
        this.tankManager = new TankManager(this, productTank);
    }

    /* LOADING & SAVING */

    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);
        tankManager.write(compoundNBT);
        sockets.write(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);
        tankManager.read(compoundNBT);
        sockets.read(compoundNBT);

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
    @OnlyIn(Dist.CLIENT)
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
    @OnlyIn(Dist.CLIENT)
    public void readGuiData(PacketBufferForestry data) throws IOException {
        super.readGuiData(data);
        sockets.readData(data);
    }

    // WORKING
    @Override
    public void updateServerSide() {
        super.updateServerSide();

        if (updateOnInterval(20)) {
            FluidStack fluid = productTank.getFluid();
            if (!fluid.isEmpty()) {
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
        productTank.fillInternal(resultFluid, IFluidHandler.FluidAction.EXECUTE);

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

            if (currentRecipe != null && ItemStackUtil.containsSets(
                    currentRecipe.getResources(),
                    resources,
                    false
            ) > 0) {
                matchingRecipe = currentRecipe;
            } else {
                matchingRecipe = RecipeManagers.squeezerManager.findMatchingRecipe(world.getRecipeManager(), resources);
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
                canFill = productTank.fillInternal(resultFluid, IFluidHandler.FluidAction.SIMULATE) ==
                          resultFluid.getAmount();

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
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> tankManager).cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerSqueezer(windowId, inv, this);
    }
}
