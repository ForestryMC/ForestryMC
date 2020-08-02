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
package forestry.energy.tiles;

import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.api.core.IErrorLogic;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.FuelManager;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.StandardTank;
import forestry.core.fluids.TankManager;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.ILiquidTankTile;
import forestry.energy.features.EnergyTiles;
import forestry.energy.gui.ContainerEngineBiogas;
import forestry.energy.inventory.InventoryEngineBiogas;

public class TileEngineBiogas extends TileEngine implements ISidedInventory, ILiquidTankTile {
    private final FilteredTank fuelTank;
    private final FilteredTank heatingTank;
    private final StandardTank burnTank;
    private final TankManager tankManager;

    private boolean shutdown; // true if the engine is too cold and needs to warm itself up.

    public TileEngineBiogas() {
        super(EnergyTiles.BIOGAS_ENGINE.tileType(), "engine.bronze", Constants.ENGINE_BRONZE_HEAT_MAX, 300000);

        setInternalInventory(new InventoryEngineBiogas(this));

        fuelTank = new FilteredTank(Constants.ENGINE_TANK_CAPACITY).setFilters(FuelManager.bronzeEngineFuel.keySet());
        heatingTank = new FilteredTank(Constants.ENGINE_TANK_CAPACITY, true, false).setFilters(Fluids.LAVA);
        burnTank = new StandardTank(FluidAttributes.BUCKET_VOLUME, false, false);

        this.tankManager = new TankManager(this, fuelTank, heatingTank, burnTank);
    }

    @Override
    public TankManager getTankManager() {
        return tankManager;
    }

    @Nullable
    public Fluid getBurnTankFluidType() {
        return burnTank.getFluidType();
    }

    @Override
    public void updateServerSide() {
        super.updateServerSide();
        if (!updateOnInterval(20)) {
            return;
        }

        // Check if we have suitable items waiting in the item slot
        FluidHelper.drainContainers(tankManager, this, InventoryEngineBiogas.SLOT_CAN);

        IErrorLogic errorLogic = getErrorLogic();

        boolean hasHeat = getHeatLevel() > 0.2 || heatingTank.getFluidAmount() > 0;
        errorLogic.setCondition(!hasHeat, EnumErrorCode.NO_HEAT);

        boolean hasFuel = burnTank.getFluidAmount() > 0 || fuelTank.getFluidAmount() > 0;
        errorLogic.setCondition(!hasFuel, EnumErrorCode.NO_FUEL);
    }

    /**
     * Burns fuel increasing stored energy
     */
    @Override
    public void burn() {

        currentOutput = 0;

        if (isRedstoneActivated() && (fuelTank.getFluidAmount() >= FluidAttributes.BUCKET_VOLUME || burnTank.getFluidAmount() > 0)) {

            double heatStage = getHeatLevel();

            // If we have reached a safe temperature, enable energy transfer
            if (heatStage > 0.25 && shutdown) {
                shutdown(false);
            } else if (shutdown) {
                if (heatingTank.getFluidAmount() > 0 && heatingTank.getFluidType() == null) {// TODO fluids FluidRegistry.LAVA) {
                    addHeat(Constants.ENGINE_HEAT_VALUE_LAVA);
                    heatingTank.drainInternal(1, IFluidHandler.FluidAction.EXECUTE);
                }
            }

            // We need a minimum temperature to generate energy
            if (heatStage > 0.2) {
                if (burnTank.getFluidAmount() > 0) {
                    FluidStack drained = burnTank.drainInternal(1, IFluidHandler.FluidAction.EXECUTE);
                    currentOutput = determineFuelValue(drained);
                    energyManager.generateEnergy(currentOutput);
                    world.updateComparatorOutputLevel(pos, getBlockState().getBlock());
                } else {
                    FluidStack fuel = fuelTank.drainInternal(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                    int burnTime = determineBurnTime(fuel);
                    if (!fuel.isEmpty()) {
                        fuel.setAmount(burnTime);
                    }
                    burnTank.setCapacity(burnTime);
                    burnTank.setFluid(fuel);
                }
            } else {
                shutdown(true);
            }
        }
    }

    private void shutdown(boolean val) {
        shutdown = val;
    }

    @Override
    public int dissipateHeat() {
        if (heat <= 0) {
            return 0;
        }

        int loss = 1; // Basic loss even when running

        if (!isBurning()) {
            loss++;
        }

        double heatStage = getHeatLevel();
        if (heatStage > 0.55) {
            loss++;
        }

        // Lose extra heat when using water as fuel.
        if (fuelTank.getFluidAmount() > 0) {
            FluidStack fuelFluidStack = fuelTank.getFluid();
            if (!fuelFluidStack.isEmpty()) {
                EngineBronzeFuel fuel = FuelManager.bronzeEngineFuel.get(fuelFluidStack.getFluid());
                if (fuel != null) {
                    loss = loss * fuel.getDissipationMultiplier();
                }
            }
        }

        heat -= loss;
        return loss;
    }

    @Override
    public int generateHeat() {

        int generate = 0;

        if (isRedstoneActivated() && burnTank.getFluidAmount() > 0) {
            double heatStage = getHeatLevel();
            if (heatStage >= 0.75) {
                generate += Constants.ENGINE_BRONZE_HEAT_GENERATION_ENERGY * 3;
            } else if (heatStage > 0.24) {
                generate += Constants.ENGINE_BRONZE_HEAT_GENERATION_ENERGY * 2;
            } else if (heatStage > 0.2) {
                generate += Constants.ENGINE_BRONZE_HEAT_GENERATION_ENERGY;
            }
        }

        heat += generate;
        return generate;

    }

    /**
     * Returns the fuel value (power per cycle) an item of the passed fluid
     */
    private static int determineFuelValue(@Nullable FluidStack fluidStack) {
        if (fluidStack != null) {
            Fluid fluid = fluidStack.getFluid();
            if (FuelManager.bronzeEngineFuel.containsKey(fluid)) {
                return FuelManager.bronzeEngineFuel.get(fluid).getPowerPerCycle();
            }
        }
        return 0;
    }

    /**
     * @return Duration of burn cycle of one bucket
     */
    private static int determineBurnTime(@Nullable FluidStack fluidStack) {
        if (fluidStack != null) {
            Fluid fluid = fluidStack.getFluid();
            if (FuelManager.bronzeEngineFuel.containsKey(fluid)) {
                return FuelManager.bronzeEngineFuel.get(fluid).getBurnDuration();
            }
        }
        return 0;
    }

    // / STATE INFORMATION
    @Override
    protected boolean isBurning() {
        return mayBurn() && burnTank.getFluidAmount() > 0;
    }

    @Override
    public int getBurnTimeRemainingScaled(int i) {
        if (burnTank.getCapacity() == 0) {
            return 0;
        }

        return burnTank.getFluidAmount() * i / burnTank.getCapacity();
    }

    public int getOperatingTemperatureScaled(int i) {
        return (int) Math.round(heat * i / (maxHeat * 0.2));
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);

        if (nbt.contains("shutdown")) {
            shutdown = nbt.getBoolean("shutdown");
        }
        tankManager.read(nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt = super.write(nbt);
        nbt.putBoolean("shutdown", shutdown);
        tankManager.write(nbt);
        return nbt;
    }

    /* NETWORK */
    @Override
    public void writeData(PacketBufferForestry data) {
        super.writeData(data);
        data.writeBoolean(shutdown);
        tankManager.writeData(data);
        burnTank.writeData(data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readData(PacketBufferForestry data) throws IOException {
        super.readData(data);
        shutdown = data.readBoolean();
        tankManager.readData(data);
        burnTank.readData(data);
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
        return new ContainerEngineBiogas(windowId, inv, this);
    }
}
