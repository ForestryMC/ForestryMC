package forestry.energy.compat.mj;

import net.minecraftforge.fml.common.Optional;

import forestry.core.config.Constants;
import forestry.energy.EnergyManager;

import buildcraft.api.mj.IMjRedstoneReceiver;
import static java.lang.Math.min;

@Optional.Interface(iface = "buildcraft.api.mj.IMjRedstoneReceiver", modid = Constants.BCLIB_MOD_ID)
public class MjRedstoneReceiverWrapper extends MjConnectorWrapper implements IMjRedstoneReceiver {
	public MjRedstoneReceiverWrapper(EnergyManager energyManager) {
		super(energyManager);
	}

	@Override
	public long getPowerRequested() {
		return MjHelper.rfToMicro(min(energyManager.getMaxEnergyReceived(), energyManager.getMaxEnergyStored() - energyManager.getEnergyStored()));
	}

	@Override
	public long receivePower(long microJoules, boolean simulate) {
		return microJoules - MjHelper.rfToMicro(energyManager.receiveEnergy(MjHelper.microToRf(microJoules), simulate));
	}
}
