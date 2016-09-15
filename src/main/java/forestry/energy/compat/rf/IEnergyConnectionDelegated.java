package forestry.energy.compat.rf;

import cofh.api.energy.IEnergyConnection;
import forestry.core.config.Constants;
import forestry.core.tiles.IPowerHandler;
import forestry.energy.EnergyManager;
import forestry.energy.EnergyTransferMode;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

/**
 * Implements {@link IEnergyConnection} by using an {@link EnergyManager} as a delegate.
 */
@Optional.Interface(iface = "cofh.api.energy.IEnergyConnection", modid = Constants.RF_MOD_ID)
public interface IEnergyConnectionDelegated extends IEnergyConnection, IPowerHandler {
	@Optional.Method(modid = Constants.RF_MOD_ID)
	@Override
	default boolean canConnectEnergy(EnumFacing from) {
		EnergyManager energyManager = getEnergyManager();
		if (energyManager == null) {
			return false;
		}
		EnergyTransferMode externalMode = energyManager.getExternalMode();
		return externalMode.canReceive() || externalMode.canExtract();
	}
}
