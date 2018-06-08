package forestry.energy.compat.mj;

import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.IMjRedstoneReceiver;
import forestry.core.config.Constants;
import forestry.energy.EnergyManager;
import net.minecraftforge.fml.common.Optional;

import static java.lang.Math.min;

@Optional.Interface(iface = "buildcraft.api.mj.IMjReceiver", modid = Constants.BCLIB_MOD_ID)
public class MjReceiverWrapper extends MjConnectorWrapper implements IMjReceiver, IMjRedstoneReceiver {
	public MjReceiverWrapper(EnergyManager energyManager) {
		super(energyManager);
	}

	@Override
	public long getPowerRequested() {
		return MjHelper.toMicroJoules(min(energyManager.getMaxEnergyReceived(), energyManager.getMaxEnergyStored() - energyManager.getEnergyStored()));
	}

	@Override
	public long receivePower(long microJoules, boolean simulate) {
		return microJoules - MjHelper.toMicroJoules(energyManager.receiveEnergy(MjHelper.fromMicroJoules(microJoules), simulate));
	}
}
