/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.energy.tiles;

import forestry.api.core.IErrorLogic;
import forestry.api.fuels.FuelManager;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.ITankManager;
import forestry.core.fluids.TankManager;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.render.TankRenderInfo;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.IRenderableTile;
import forestry.core.tiles.TileBase;
import forestry.energy.gui.ContainerGenerator;
import forestry.energy.inventory.InventoryGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
//import forestry.plugins.ForestryCompatPlugins;

//import ic2.api.energy.prefab.BasicSource;

public class TileEuGenerator extends TileBase implements ISidedInventory, ILiquidTankTile, IRenderableTile, IStreamableGui {
    private static final int maxEnergy = 30000;

    private final TankManager tankManager;
    private final FilteredTank resourceTank;

    private final int tickCount = 0;

    //	@Nullable
    //	private BasicSource ic2EnergySource;

    public TileEuGenerator() {
        super(TileEntityType.DISPENSER);//"generator" TODO tileentitytypes

        setInternalInventory(new InventoryGenerator(this));

        resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY);
        resourceTank.setFilters(FuelManager.generatorFuel.keySet());

        tankManager = new TankManager(this, resourceTank);

        //		if (ModuleHelper.isModuleEnabled(ForestryCompatPlugins.ID, ForestryModuleUids.INDUSTRIALCRAFT2)) {
        //			ic2EnergySource = new BasicSource(this, maxEnergy, 1);
        //		}
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);

        //		if (ic2EnergySource != null) {
        //			ic2EnergySource.write(CompoundNBT);
        //		}

        tankManager.write(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);

        //		if (ic2EnergySource != null) {
        //			ic2EnergySource.read(CompoundNBT);
        //		}

        tankManager.read(compoundNBT);
    }

    @Override
    public void writeData(PacketBufferForestry data) {
        super.writeData(data);
        tankManager.writeData(data);
    }

    @Override
    public void readData(PacketBufferForestry data) throws IOException {
        super.readData(data);
        tankManager.readData(data);
    }
    //TODO
    //
    //	@Override
    //	public void onChunkUnload() {
    //		if (ic2EnergySource != null) {
    //			ic2EnergySource.onChunkUnload();
    //		}
    //
    //		super.onChunkUnload();
    //	}

    @Override
    public void remove() {
        //		if (ic2EnergySource != null) {
        //			ic2EnergySource.invalidate();
        //		}

        super.remove();
    }

    @Override
    public void updateServerSide() {
        if (updateOnInterval(20)) {
            FluidHelper.drainContainers(tankManager, this, InventoryGenerator.SLOT_CAN);
        }

        IErrorLogic errorLogic = getErrorLogic();

        // No work to be done if IC2 is unavailable.
        //		if (errorLogic.setCondition(ic2EnergySource == null, EnumErrorCode.NO_ENERGY_NET)) {
        //			return;
        //		}
        //
        //		ic2EnergySource.update();
        //
        //		if (resourceTank.getFluidAmount() > 0) {
        //			GeneratorFuel fuel = FuelManager.generatorFuel.get(resourceTank.getFluid().getFluid());
        //			if (resourceTank.canDrainFluidType(fuel.getFuelConsumed()) && ic2EnergySource.getFreeCapacity() >= fuel.getEu()) {
        //				ic2EnergySource.addEnergy(fuel.getEu());
        //				this.tickCount++;
        //
        //				if (tickCount >= fuel.getRate()) {
        //					tickCount = 0;
        //					resourceTank.drain(fuel.getFuelConsumed().amount, true);
        //				}
        //			}
        //
        //		}

        boolean hasFuel = resourceTank.getFluidAmount() > 0;
        errorLogic.setCondition(!hasFuel, EnumErrorCode.NO_FUEL);
    }

    public boolean isWorking() {
        return resourceTank.getFluidAmount() > 0;
    }

    public int getResourceScaled(int i) {
        return (resourceTank.getFluidAmount() * i) / Constants.PROCESSOR_TANK_CAPACITY;
    }

    public int getStoredScaled(int i) {
        //		if (ic2EnergySource == null) {
        return 0;
        //		}
        //
        //		return (int) (ic2EnergySource.getEnergyStored() * i) / maxEnergy;
    }

    @Override
    public TankRenderInfo getResourceTankInfo() {
        return new TankRenderInfo(resourceTank);
    }

    @Override
    public TankRenderInfo getProductTankInfo() {
        return TankRenderInfo.EMPTY;
    }

    @Override
    public void writeGuiData(PacketBufferForestry data) {
        //		if (ic2EnergySource != null) {
        //			data.writeDouble(ic2EnergySource.getEnergyStored());
        //		}
    }

    @Override
    public void readGuiData(PacketBufferForestry data) {
        //		if (ic2EnergySource != null) {
        //			ic2EnergySource.setEnergyStored(data.readDouble());
        //		}
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerGenerator(windowId, inv, this);
    }

    @Nonnull
    @Override
    public ITankManager getTankManager() {
        return tankManager;
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        LazyOptional<T> superCap = super.getCapability(capability, facing);
        if (superCap.isPresent()) {
            return superCap;
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> tankManager).cast();
        }
        return LazyOptional.empty();
    }
}
