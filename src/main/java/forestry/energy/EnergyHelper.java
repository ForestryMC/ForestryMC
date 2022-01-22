package forestry.energy;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;

import forestry.core.config.Preference;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import forestry.api.core.ForestryAPI;
import forestry.core.config.Config;
import forestry.energy.tiles.TileEngine;

public class EnergyHelper {
	public static int scaleForDifficulty(int energyValue) {
		return Math.round(energyValue * Preference.ENERGY_DEMAND_MODIFIER);
	}

	/**
	 * Consumes one work cycle's worth of energy.
	 *
	 * @return true if the energy to do work was consumed
	 */
	public static boolean consumeEnergyToDoWork(EnergyManager energyManager, int ticksPerWorkCycle, int energyPerWorkCycle) {
		if (energyPerWorkCycle == 0) {
			return true;
		}
		int energyPerCycle = (int) Math.ceil(energyPerWorkCycle / (float) ticksPerWorkCycle);
		if (energyManager.getEnergyStored() < energyPerCycle) {
			return false;
		}

		energyManager.drainEnergy(energyPerCycle);

		return true;
	}

	/**
	 * Sends as much energy as it can to the tile at orientation.
	 * For power sources.
	 *
	 * @return amount sent
	 */
	public static int sendEnergy(EnergyManager energyManager, Direction orientation, @Nullable BlockEntity tile) {
		return sendEnergy(energyManager, orientation, tile, Integer.MAX_VALUE, false);
	}

	/**
	 * Sends amount of energy to the tile at orientation.
	 * For power sources.
	 *
	 * @return amount sent
	 */
	public static int sendEnergy(EnergyManager energyManager, Direction orientation, @Nullable BlockEntity tile, int amount, boolean simulate) {
		int extractable = energyManager.extractEnergy(amount, true);
		if (extractable > 0) {
			Direction side = orientation.getOpposite();
			final int sent = sendEnergyToTile(tile, side, extractable, simulate);
			energyManager.extractEnergy(sent, simulate);
			return sent;
		}
		return 0;
	}

	private static int sendEnergyToTile(@Nullable BlockEntity tile, Direction side, int extractable, boolean simulate) {
		if (tile == null) {
			return 0;
		}

		if (tile instanceof TileEngine receptor) { // engine chaining
			return receptor.getEnergyManager().receiveEnergy(extractable, simulate);
		}

		if (Config.enableRF) {
			LazyOptional<IEnergyStorage> energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side);
			if (energyStorage.isPresent()) {
				return energyStorage.orElse(null).receiveEnergy(extractable, simulate);
			}
		}

		return 0;
	}

	/**
	 * @return whether this can send energy to the target tile
	 */
	public static boolean canSendEnergy(EnergyManager energyManager, Direction orientation, BlockEntity tile) {
		return sendEnergy(energyManager, orientation, tile, Integer.MAX_VALUE, true) > 0;
	}

	public static boolean isEnergyReceiverOrEngine(Direction side, @Nullable BlockEntity tile) {
		if (tile == null) {
			return false;
		}
		if (tile instanceof TileEngine) { // engine chaining
			return true;
		}

		LazyOptional<IEnergyStorage> energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side);
		if (energyStorage.isPresent()) {
			return energyStorage.orElse(null).canReceive();
		}

		return false;
	}
}
