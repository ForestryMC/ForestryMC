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
package forestry.farming.tiles;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IFarmController;
import forestry.energy.EnergyHelper;
import forestry.energy.EnergyManager;
import forestry.farming.features.FarmingTiles;

public class TileFarmGearbox extends TileFarm implements IFarmComponent.Active {

	private static final int WORK_CYCLES = 4;
	private static final int ENERGY_PER_OPERATION = WORK_CYCLES * 50;

	private final EnergyManager energyManager;

	private int activationDelay = 0;
	private int previousDelays = 0;
	private int workCounter;

	public TileFarmGearbox(BlockPos pos, BlockState state) {
		super(FarmingTiles.GEARBOX.tileType(), pos, state);
		energyManager = new EnergyManager(200, 10000);
	}

	/* SAVING & LOADING */
	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);
		energyManager.read(compoundNBT);

		activationDelay = compoundNBT.getInt("ActivationDelay");
		previousDelays = compoundNBT.getInt("PrevDelays");
	}


	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);
		energyManager.write(compoundNBT);

		compoundNBT.putInt("ActivationDelay", activationDelay);
		compoundNBT.putInt("PrevDelays", previousDelays);
	}

	@Override
	public void updateServer(int tickCount) {
		if (energyManager.getEnergyStored() <= 0) {
			return;
		}

		if (activationDelay > 0) {
			activationDelay--;
			return;
		}

		// Hard limit to 4 cycles / second.
		if (workCounter < WORK_CYCLES && EnergyHelper.consumeEnergyToDoWork(energyManager, WORK_CYCLES, ENERGY_PER_OPERATION)) {
			workCounter++;
		}

		if (workCounter >= WORK_CYCLES && tickCount % 5 == 0) {
			IFarmController farmController = getMultiblockLogic().getController();
			if (farmController.doWork()) {
				workCounter = 0;
				previousDelays = 0;
			} else {
				// If the central TE doesn't have work, we add to the activation delay to throttle the CPU usage.
				activationDelay = Math.min(10 * previousDelays, 120);
				previousDelays++; // First delay is free!
			}
		}
	}

	@Override
	public void updateClient(int tickCount) {

	}

	public EnergyManager getEnergyManager() {
		return energyManager;
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		LazyOptional<T> energyCapability = energyManager.getCapability(capability);
		if (energyCapability.isPresent()) {
			return energyCapability;
		}
		return super.getCapability(capability, facing);
	}
}
