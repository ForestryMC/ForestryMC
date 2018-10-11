package forestry.energy.compat.mj;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.Optional;

import forestry.core.config.Constants;
import forestry.energy.EnergyManager;

import buildcraft.api.mj.IMjConnector;

@Optional.Interface(iface = "buildcraft.api.mj.IMjConnector", modid = Constants.BCLIB_MOD_ID)
public class MjConnectorWrapper implements IMjConnector {
	protected final EnergyManager energyManager;

	public MjConnectorWrapper(EnergyManager energyManager) {
		this.energyManager = energyManager;
	}

	@Override
	public boolean canConnect(@Nonnull IMjConnector other) {
		return true;
	}
}
