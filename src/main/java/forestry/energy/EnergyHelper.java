package forestry.energy;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import forestry.api.core.ForestryAPI;
import forestry.core.config.Config;
import forestry.core.tiles.TileEngine;
import forestry.energy.compat.mj.MjHelper;
import forestry.energy.compat.tesla.TeslaHelper;

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
	public static int sendEnergy(EnergyManager energyManager, EnumFacing orientation, @Nullable TileEntity tile) {
		return sendEnergy(energyManager, orientation, tile, Integer.MAX_VALUE, false);
	}

	/**
	 * Sends amount of energy to the tile at orientation.
	 * For power sources.
	 *
	 * @return amount sent
	 */
	public static int sendEnergy(EnergyManager energyManager, EnumFacing orientation, @Nullable TileEntity tile, int amount, boolean simulate) {
		int extractable = energyManager.extractEnergy(amount, true);
		if (extractable > 0) {
			EnumFacing side = orientation.getOpposite();
			final int sent = sendEnergyToTile(tile, side, extractable, simulate);
			energyManager.extractEnergy(sent, simulate);
			return sent;
		}
		return 0;
	}

	private static int sendEnergyToTile(@Nullable TileEntity tile, EnumFacing side, int extractable, boolean simulate) {
		if (tile == null) {
			return 0;
		}

		if (tile instanceof TileEngine) { // engine chaining
			TileEngine receptor = (TileEngine) tile;
			return receptor.getEnergyManager().receiveEnergy(extractable, simulate);
		}

		if (Config.enableRF && tile.hasCapability(CapabilityEnergy.ENERGY, side)) {
			IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side);
			if (energyStorage != null) {
				return energyStorage.receiveEnergy(extractable, simulate);
			}
		}

		if (Config.enableTesla && TeslaHelper.isEnergyReceiver(tile, side)) {
			return TeslaHelper.sendEnergy(tile, side, extractable, simulate);
		}

		if (Config.enableMJ && MjHelper.isEnergyReceiver(tile, side)) {
			return MjHelper.sendEnergy(tile, side, extractable, simulate);
		}

		return 0;
	}

	/**
	 * @return whether this can send energy to the target tile
	 */
	public static boolean canSendEnergy(EnergyManager energyManager, EnumFacing orientation, TileEntity tile) {
		return sendEnergy(energyManager, orientation, tile, Integer.MAX_VALUE, true) > 0;
	}

	public static boolean isEnergyReceiverOrEngine(EnumFacing side, @Nullable TileEntity tile) {
		if (tile == null) {
			return false;
		}
		if (tile instanceof TileEngine) { // engine chaining
			return true;
		}

		if (tile.hasCapability(CapabilityEnergy.ENERGY, side)) {
			IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, side);
			return energyStorage != null && energyStorage.canReceive();
		}

		return TeslaHelper.isEnergyReceiver(tile, side) ||
			MjHelper.isEnergyReceiver(tile, side);
	}
}
