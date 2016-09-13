package forestry.energy.compat.rf;

import cofh.api.energy.IEnergyReceiver;
import forestry.core.config.Constants;
import forestry.energy.EnergyManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

/**
 * Implements {@link IEnergyReceiver} by using an {@link EnergyManager} as a delegate.
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = Constants.RF_MOD_ID)
public interface IEnergyReceiverDelegated extends IEnergyReceiver, IEnergyHandlerDelegated {
	@Optional.Method(modid = Constants.RF_MOD_ID)
	@Override
	default int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		EnergyManager energyManager = getEnergyManager();
		if (energyManager == null) {
			return 0;
		} else if (energyManager.getExternalMode().canReceive()) {
			return energyManager.receiveEnergy(maxReceive, simulate);
		} else {
			return 0;
		}
	}
}
