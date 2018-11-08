package forestry.energy.compat.tesla;

import net.minecraftforge.fml.common.Optional;

import forestry.core.config.Constants;
import forestry.energy.EnergyManager;

import net.darkhax.tesla.api.ITeslaConsumer;

@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = Constants.TESLA_MOD_ID)
public class TeslaConsumerWrapper implements ITeslaConsumer {
	private final EnergyManager energyManager;

	public TeslaConsumerWrapper(EnergyManager energyManager) {
		this.energyManager = energyManager;
	}

	@Optional.Method(modid = Constants.TESLA_MOD_ID)
	@Override
	public long givePower(long power, boolean simulated) {
		int intPower = (int) Math.min(power, Integer.MAX_VALUE);
		return energyManager.receiveEnergy(intPower, simulated);
	}
}
