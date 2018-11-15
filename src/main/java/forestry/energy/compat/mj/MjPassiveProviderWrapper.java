package forestry.energy.compat.mj;

import net.minecraftforge.fml.common.Optional;

import forestry.core.config.Constants;
import forestry.energy.EnergyManager;

import buildcraft.api.mj.IMjPassiveProvider;

@Optional.Interface(iface = "buildcraft.api.mj.IMjPassiveProvider", modid = Constants.BCLIB_MOD_ID)
public class MjPassiveProviderWrapper extends MjConnectorWrapper implements IMjPassiveProvider {
	public MjPassiveProviderWrapper(EnergyManager energyManager) {
		super(energyManager);
	}

	@Override
	public long extractPower(long min, long max, boolean simulate) {
		int max1 = MjHelper.microToRf(max);
		int actualMin = energyManager.extractEnergy(max1, true);
		if (actualMin < min) {
			return 0;
		}
		return MjHelper.rfToMicro(energyManager.extractEnergy(max1, simulate));
	}
}
