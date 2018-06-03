package forestry.energy.compat.mj;

import buildcraft.api.mj.*;
import forestry.core.config.Constants;
import forestry.core.utils.Log;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class MjHelper {
	@Nullable
	@CapabilityInject(IMjConnector.class)
	public static Capability<IMjConnector> CAP_CONNECTOR = null;

	@Nullable
	@CapabilityInject(IMjReceiver.class)
	public static Capability<IMjReceiver> CAP_RECEIVER = null;

	@Nullable
	@CapabilityInject(IMjRedstoneReceiver.class)
	public static Capability<IMjRedstoneReceiver> CAP_REDSTONE_RECEIVER = null;

	@Nullable
	@CapabilityInject(IMjReadable.class)
	public static Capability<IMjReadable> CAP_READABLE = null;

	@Nullable
	@CapabilityInject(IMjPassiveProvider.class)
	public static Capability<IMjPassiveProvider> CAP_PASSIVE_PROVIDER = null;

	public static boolean isLoaded() {
		return CAP_CONNECTOR != null && CAP_RECEIVER != null && CAP_REDSTONE_RECEIVER != null && CAP_READABLE != null && CAP_PASSIVE_PROVIDER != null;
	}

	public static boolean isEnergyReceiver(TileEntity tile, EnumFacing side) {
		return isLoaded() && _isEnergyReceiver(tile, side);
	}

	public static int sendEnergy(TileEntity tile, EnumFacing side, int amount, boolean simulate) {
		if (isLoaded()) {
			return mjToRf(_sendEnergy(tile, side, rfToMj(amount), simulate));
		} else {
			return 0;
		}
	}

	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	private static boolean _isEnergyReceiver(TileEntity tile, EnumFacing side) {
		return CAP_RECEIVER != null && tile.hasCapability(CAP_RECEIVER, side);
	}

	@Optional.Method(modid = Constants.BCLIB_MOD_ID)
	private static int _sendEnergy(TileEntity tile, EnumFacing side, int amount, boolean simulate) {
		long amountMicro = toMicroJoules(amount);
		IMjReceiver consumer = tile.getCapability(CAP_RECEIVER, side);
		if (consumer == null) {
			if (tile.hasCapability(CAP_RECEIVER, side)) {
				Log.error("Tile claims to support MJ but does not have the capability. {} {}", tile.getPos(), tile);
			}
			return 0;
		}
		return fromMicroJoules(amountMicro - consumer.receivePower(amountMicro, simulate));
	}

	public static boolean isMjCapability(Capability<?> capability) {
		if (!isLoaded()) return false;

		return capability == CAP_CONNECTOR || capability == CAP_RECEIVER || capability == CAP_REDSTONE_RECEIVER ||
				capability == CAP_READABLE || capability == CAP_PASSIVE_PROVIDER;
	}

	public static int fromMicroJoules(long microJoules) {
		return (int) (microJoules / MjAPI.MJ);
	}

	public static long toMicroJoules(long mj) {
		return mj * MjAPI.MJ;
	}

	public static int rfToMj(int rf) {
		return rf / 10;
	}

	public static int mjToRf(int mj) {
		return mj * 10;
	}
}
