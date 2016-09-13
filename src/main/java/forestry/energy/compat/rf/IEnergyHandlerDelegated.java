package forestry.energy.compat.rf;

import cofh.api.energy.IEnergyHandler;
import forestry.core.config.Constants;
import forestry.energy.EnergyManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

/**
 * Implements {@link IEnergyHandler} by using an {@link EnergyManager} as a delegate.
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = Constants.RF_MOD_ID)
public interface IEnergyHandlerDelegated extends IEnergyHandler, IEnergyConnectionDelegated {
	@Optional.Method(modid = Constants.RF_MOD_ID)
	@Override
	default int getEnergyStored(EnumFacing from) {
		EnergyManager energyManager = getEnergyManager();
		if (energyManager == null) {
			return 0;
		}
		return energyManager.getEnergyStored();
	}

	@Optional.Method(modid = Constants.RF_MOD_ID)
	@Override
	default int getMaxEnergyStored(EnumFacing from) {
		EnergyManager energyManager = getEnergyManager();
		if (energyManager == null) {
			return 0;
		}
		return energyManager.getMaxEnergyStored();
	}
}
