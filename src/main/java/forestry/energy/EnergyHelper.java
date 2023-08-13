package forestry.energy;

import forestry.core.config.Preference;

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

}
