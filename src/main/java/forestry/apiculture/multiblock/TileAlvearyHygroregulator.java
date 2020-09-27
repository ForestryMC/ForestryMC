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
package forestry.apiculture.multiblock;

import forestry.api.climate.IClimateControlled;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.gui.ContainerAlvearyHygroregulator;
import forestry.apiculture.inventory.InventoryHygroregulator;
import forestry.core.config.Constants;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.recipes.HygroregulatorManager;
import forestry.core.tiles.ILiquidTankTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class TileAlvearyHygroregulator extends TileAlveary implements IInventory, ILiquidTankTile, IAlvearyComponent.Climatiser {
    private final TankManager tankManager;
    private final FilteredTank liquidTank;
    private final IInventoryAdapter inventory;

    @Nullable
    private IHygroregulatorRecipe currentRecipe;
    private int transferTime;

    public TileAlvearyHygroregulator() {
        super(BlockAlvearyType.HYGRO);

        this.inventory = new InventoryHygroregulator(this);

        this.liquidTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(HygroregulatorManager.getRecipeFluids());

        this.tankManager = new TankManager(this, liquidTank);
    }

    @Override
    public IInventoryAdapter getInternalInventory() {
        return inventory;
    }

    @Override
    public boolean allowsAutomation() {
        return true;
    }

    /* UPDATING */
    @Override
    public void changeClimate(int tickCount, IClimateControlled climateControlled) {
        if (transferTime <= 0) {
            FluidStack fluid = liquidTank.getFluid();
            if (!fluid.isEmpty()) {
                currentRecipe = HygroregulatorManager.findMatchingRecipe(fluid);

                if (currentRecipe != null) {
                    liquidTank.drainInternal(
                            currentRecipe.getResource().getAmount(),
                            IFluidHandler.FluidAction.EXECUTE
                    );
                    transferTime = currentRecipe.getTransferTime();
                }
            }
        }

        if (transferTime > 0) {

            transferTime--;
            if (currentRecipe != null) {
                climateControlled.addHumidityChange(currentRecipe.getHumidChange(), 0.0f, 1.0f);
                climateControlled.addTemperatureChange(currentRecipe.getTempChange(), 0.0f, 2.0f);
            } else {
                transferTime = 0;
            }
        }

        if (tickCount % 20 == 0) {
            // Check if we have suitable items waiting in the item slot
            FluidHelper.drainContainers(tankManager, this, 0);
        }
    }

    /* SAVING & LOADING */
    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);
        tankManager.read(compoundNBT);

        transferTime = compoundNBT.getInt("TransferTime");

        if (compoundNBT.contains("CurrentLiquid")) {
            FluidStack liquid = FluidStack.loadFluidStackFromNBT(compoundNBT.getCompound("CurrentLiquid"));
            currentRecipe = HygroregulatorManager.findMatchingRecipe(liquid);
        }
    }


    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);
        tankManager.write(compoundNBT);

        compoundNBT.putInt("TransferTime", transferTime);
        if (currentRecipe != null) {
            CompoundNBT subcompound = new CompoundNBT();
            currentRecipe.getResource().writeToNBT(subcompound);
            compoundNBT.put("CurrentLiquid", subcompound);
        }
        return compoundNBT;
    }

    /* ILIQUIDTANKCONTAINER */

    @Override
    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        LazyOptional<T> superCap = super.getCapability(capability, facing);
        if (superCap.isPresent()) {
            return superCap;
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> tankManager).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerAlvearyHygroregulator(windowId, inv, this);
    }
}
