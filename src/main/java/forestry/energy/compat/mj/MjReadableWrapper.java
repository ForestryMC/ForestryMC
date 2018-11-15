package forestry.energy.compat.mj;

import net.minecraftforge.fml.common.Optional;

import forestry.core.config.Constants;
import forestry.energy.EnergyManager;

import buildcraft.api.mj.IMjReadable;

@Optional.Interface(iface = "buildcraft.api.mj.IMjReadable", modid = Constants.BCLIB_MOD_ID)
public class MjReadableWrapper extends MjConnectorWrapper implements IMjReadable {
	public MjReadableWrapper(EnergyManager energyManager) {
		super(energyManager);
	}

	@Override
	public long getStored() {
		return MjHelper.rfToMicro(energyManager.getEnergyStored());
	}

	@Override
	public long getCapacity() {
		return MjHelper.rfToMicro(energyManager.getMaxEnergyStored());
	}
}
