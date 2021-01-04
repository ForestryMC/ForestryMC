/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.tiles;

import forestry.api.core.IErrorLogic;
import forestry.core.circuits.ISpeedUpgradable;
import forestry.core.errors.EnumErrorCode;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.render.TankRenderInfo;
import forestry.energy.EnergyHelper;
import forestry.energy.EnergyManager;
import forestry.energy.EnergyTransferMode;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.io.IOException;

//import forestry.core.capabilities.HasWorkWrapper;

//import static forestry.core.capabilities.HasWorkWrapper.CAPABILITY_HAS_WORK;

public abstract class TilePowered extends TileBase implements IRenderableTile, ISpeedUpgradable, IStreamableGui {

    private static final int WORK_TICK_INTERVAL = 5; // one Forestry work tick happens every WORK_TICK_INTERVAL game ticks

    private final EnergyManager energyManager;
    protected float speedMultiplier = 1.0f;
    protected float powerMultiplier = 1.0f;
    private int workCounter;
    private int ticksPerWorkCycle;
    private int energyPerWorkCycle;
    // the number of work ticks that this tile has had no power
    private int noPowerTime = 0;

    protected TilePowered(TileEntityType<?> type, int maxTransfer, int capacity) {
        super(type);
        this.energyManager = new EnergyManager(maxTransfer, capacity);
        this.energyManager.setExternalMode(EnergyTransferMode.RECEIVE);

        this.ticksPerWorkCycle = 4;
    }

    public EnergyManager getEnergyManager() {
        return energyManager;
    }

    public int getWorkCounter() {
        return workCounter;
    }

    public int getTicksPerWorkCycle() {
        if (world.isRemote) {
            return ticksPerWorkCycle;
        }
        return Math.round(ticksPerWorkCycle / speedMultiplier);
    }

    public void setTicksPerWorkCycle(int ticksPerWorkCycle) {
        this.ticksPerWorkCycle = ticksPerWorkCycle;
        this.workCounter = 0;
    }

    public int getEnergyPerWorkCycle() {
        return Math.round(energyPerWorkCycle * powerMultiplier);
    }

    public void setEnergyPerWorkCycle(int energyPerWorkCycle) {
        this.energyPerWorkCycle = EnergyHelper.scaleForDifficulty(energyPerWorkCycle);
    }

    /* STATE INFORMATION */
    public boolean hasResourcesMin(float percentage) {
        return false;
    }

    public boolean hasFuelMin(float percentage) {
        return false;
    }

    public abstract boolean hasWork();

    @Override
    protected void updateServerSide() {
        super.updateServerSide();

        if (!updateOnInterval(WORK_TICK_INTERVAL)) {
            return;
        }

        IErrorLogic errorLogic = getErrorLogic();

        boolean disabled = isRedstoneActivated();
        errorLogic.setCondition(disabled, EnumErrorCode.DISABLED_BY_REDSTONE);
        if (disabled) {
            return;
        }

        if (!hasWork()) {
            return;
        }

        int ticksPerWorkCycle = getTicksPerWorkCycle();

        if (workCounter < ticksPerWorkCycle) {
            int energyPerWorkCycle = getEnergyPerWorkCycle();
            boolean consumedEnergy = EnergyHelper.consumeEnergyToDoWork(
                    energyManager,
                    ticksPerWorkCycle,
                    energyPerWorkCycle
            );
            if (consumedEnergy) {
                errorLogic.setCondition(false, EnumErrorCode.NO_POWER);
                workCounter++;
                noPowerTime = 0;
            } else {
                noPowerTime++;
                if (noPowerTime > 4) {
                    errorLogic.setCondition(true, EnumErrorCode.NO_POWER);
                }
            }
        }

        if (workCounter >= ticksPerWorkCycle) {
            if (workCycle()) {
                workCounter = 0;
            }
        }
    }

    protected abstract boolean workCycle();

    public int getProgressScaled(int i) {
        int ticksPerWorkCycle = getTicksPerWorkCycle();
        if (ticksPerWorkCycle == 0) {
            return 0;
        }

        return workCounter * i / ticksPerWorkCycle;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt = super.write(nbt);
        energyManager.write(nbt);
        return nbt;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        energyManager.read(nbt);
    }

    @Override
    public void writeGuiData(PacketBufferForestry data) {
        energyManager.writeData(data);
        data.writeVarInt(workCounter);
        data.writeVarInt(getTicksPerWorkCycle());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readGuiData(PacketBufferForestry data) throws IOException {
        energyManager.readData(data);
        workCounter = data.readVarInt();
        ticksPerWorkCycle = data.readVarInt();
    }

    /* ISpeedUpgradable */
    @Override
    public void applySpeedUpgrade(double speedChange, double powerChange) {
        speedMultiplier += speedChange;
        powerMultiplier += powerChange;
        workCounter = 0;
    }

    /* IRenderableTile */
    @Override
    public TankRenderInfo getResourceTankInfo() {
        return TankRenderInfo.EMPTY;
    }

    @Override
    public TankRenderInfo getProductTankInfo() {
        return TankRenderInfo.EMPTY;
    }

    /* IPowerHandler */
    //	@Override
    //	public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
    //		if (capability == CAPABILITY_HAS_WORK) {
    //			return true;
    //		}
    //		return energyManager.hasCapability(capability) || super.hasCapability(capability, facing);
    //	}

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        //		if (capability == CAPABILITY_HAS_WORK) {
        //			return CAPABILITY_HAS_WORK.cast(new HasWorkWrapper(this));
        //		}
        LazyOptional<T> energyCapability = energyManager.getCapability(capability);
        if (energyCapability.isPresent()) {
            return energyCapability;
        }
        return super.getCapability(capability, facing);
    }
}
