package forestry.energy.compat.rf;

import cofh.api.energy.IEnergyReceiver;
import forestry.core.config.Constants;
import forestry.core.utils.ModUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

public class RFHelper {
	private static boolean loaded = ModUtil.isAPILoaded("cofh.api.energy");

	public static boolean isLoaded() {
		return loaded;
	}
	
	public static boolean isEnergyReceiver(TileEntity tile, EnumFacing side) {
		if (isLoaded()) {
			return _isEnergyReceiver(tile, side);
		} else {
			return false;
		}
	}

	public static int sendEnergy(TileEntity tile, EnumFacing side, int amount, boolean simulate) {
		if (isLoaded()) {
			return _sendEnergy(tile, side, amount, simulate);
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Constants.RF_MOD_ID)
	private static boolean _isEnergyReceiver(TileEntity tile, EnumFacing side) {
		if (tile instanceof IEnergyReceiver) {
			IEnergyReceiver energyReceiver = (IEnergyReceiver) tile;
			return energyReceiver.canConnectEnergy(side);
		} else {
			return false;
		}
	}

	@Optional.Method(modid = Constants.RF_MOD_ID)
	private static int _sendEnergy(TileEntity tile, EnumFacing side, int amount, boolean simulate) {
		if (tile instanceof IEnergyReceiver) {
			IEnergyReceiver receptor = (IEnergyReceiver) tile;
			return receptor.receiveEnergy(side, amount, simulate);
		} else {
			return 0;
		}
	}
}
