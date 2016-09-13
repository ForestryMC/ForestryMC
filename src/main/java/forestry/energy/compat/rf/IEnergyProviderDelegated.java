package forestry.energy.compat.rf;

import cofh.api.energy.IEnergyProvider;
import forestry.core.config.Constants;
import forestry.energy.EnergyManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

/**
 * Implements {@link IEnergyProvider} by using an {@link EnergyManager} as a delegate.
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyProvider", modid = Constants.RF_MOD_ID)
public interface IEnergyProviderDelegated extends IEnergyProvider, IEnergyHandlerDelegated {
	@Optional.Method(modid = Constants.RF_MOD_ID)
	@Override
	default int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		EnergyManager energyManager = getEnergyManager();
		if (energyManager.getExternalMode().canExtract()) {
			return energyManager.extractEnergy(maxExtract, simulate);
		} else {
			return 0;
		}
	}
}
