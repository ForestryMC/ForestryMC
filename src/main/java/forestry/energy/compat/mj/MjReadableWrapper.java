package forestry.energy.compat.mj;

import buildcraft.api.mj.IMjReadable;
import forestry.core.config.Constants;
import forestry.energy.EnergyManager;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "buildcraft.api.mj.IMjReadable", modid = Constants.BCLIB_MOD_ID)
public class MjReadableWrapper extends MjConnectorWrapper implements IMjReadable {
	public MjReadableWrapper(EnergyManager energyManager) {
		super(energyManager);
	}

	@Override
	public long getStored() {
		return MjHelper.toMicroJoules(MjHelper.rfToMj(energyManager.getEnergyStored()));
	}

	@Override
	public long getCapacity() {
		return MjHelper.toMicroJoules(MjHelper.rfToMj(energyManager.getMaxEnergyStored()));
	}
}