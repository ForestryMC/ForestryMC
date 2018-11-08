package forestry.energy.compat.tesla;

import net.minecraftforge.fml.common.Optional;

import forestry.core.config.Constants;
import forestry.energy.EnergyManager;

import net.darkhax.tesla.api.ITeslaHolder;

@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = Constants.TESLA_MOD_ID)
public class TeslaHolderWrapper implements ITeslaHolder {
	private final EnergyManager energyManager;

	public TeslaHolderWrapper(EnergyManager energyManager) {
		this.energyManager = energyManager;
	}

	@Optional.Method(modid = Constants.TESLA_MOD_ID)
	@Override
	public long getStoredPower() {
		return energyManager.getEnergyStored();
	}

	@Optional.Method(modid = Constants.TESLA_MOD_ID)
	@Override
	public long getCapacity() {
		return energyManager.getMaxEnergyStored();
	}
}
