package forestry.energy;

import forestry.api.core.ForestryAPI;
import forestry.core.tiles.TileEngine;
import forestry.energy.compat.rf.RFHelper;
import forestry.energy.compat.tesla.TeslaHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyHelper {
	public static int scaleForDifficulty(int energyValue) {
		float energyModifier = ForestryAPI.activeMode.getFloatSetting("energy.demand.modifier");
		return Math.round(energyValue * energyModifier);
	}

	/**
	 * Consumes one work cycle's worth of energy.
	 *
	 * @return true if the energy to do work was consumed
	 */
	public static boolean consumeEnergyToDoWork(EnergyManager energyManager, int ticksPerWorkCycle, int energyPerWorkCycle) {
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
	public static int sendEnergy(EnergyManager energyManager, EnumFacing orientation, TileEntity tile) {
		return sendEnergy(energyManager, orientation, tile, Integer.MAX_VALUE, false);
	}

	/**
	 * Sends amount of energy to the tile at orientation.
	 * For power sources.
	 *
	 * @return amount sent
	 */
	public static int sendEnergy(EnergyManager energyManager, EnumFacing orientation, TileEntity tile, int amount, boolean simulate) {
		if (tile != null) {
			int extractable = energyManager.extractEnergy(amount, true);
			if (extractable > 0) {
				final int sent;
				EnumFacing side = orientation.getOpposite();
				if (tile instanceof TileEngine) { // engine chaining
					TileEngine receptor = (TileEngine) tile;
					sent = receptor.getEnergyManager().receiveEnergy(extractable, simulate);
				} else if (tile.hasCapability(CapabilityEnergy.ENERGY, side)) {
					IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side);
					sent = energyStorage.receiveEnergy(extractable, simulate);
				} else if (TeslaHelper.isEnergyReceiver(tile, side)) {
					sent = TeslaHelper.sendEnergy(tile, side, extractable, simulate);
				} else if (RFHelper.isEnergyReceiver(tile, side)) {
					sent = RFHelper.sendEnergy(tile, side, extractable, simulate);
				} else {
					sent = 0;
				}

				energyManager.extractEnergy(sent, simulate);
				return sent;
			}
		}
		return 0;
	}

	/**
	 * @return whether this can send energy to the target tile
	 */
	public static boolean canSendEnergy(EnergyManager energyManager, EnumFacing orientation, TileEntity tile) {
		return sendEnergy(energyManager, orientation, tile, Integer.MAX_VALUE, true) > 0;
	}

	public static boolean isEnergyReceiverOrEngine(EnumFacing side, TileEntity tile) {
		if (tile != null) {
			if (tile instanceof TileEngine) { // engine chaining
				return true;
			}

			if (tile.hasCapability(CapabilityEnergy.ENERGY, side)) {
				IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side);
				return energyStorage.canReceive();
			}

			if (TeslaHelper.isEnergyReceiver(tile, side)) {
				return true;
			}

			if (RFHelper.isEnergyReceiver(tile, side)) {
				return true;
			}
		}
		return false;
	}
}
