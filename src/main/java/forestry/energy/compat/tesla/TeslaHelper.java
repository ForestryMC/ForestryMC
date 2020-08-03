package forestry.energy.compat.tesla;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

//import net.minecraftforge.fml.common.Optional;


//import net.darkhax.tesla.api.ITeslaConsumer;
//import net.darkhax.tesla.api.ITeslaHolder;
//import net.darkhax.tesla.api.ITeslaProducer;

public class TeslaHelper {
    //	@Nullable
    //	@CapabilityInject(ITeslaConsumer.class)
    //	public static Capability<ITeslaConsumer> TESLA_CONSUMER = null;
    //
    //	@Nullable
    //	@CapabilityInject(ITeslaProducer.class)
    //	public static Capability<ITeslaProducer> TESLA_PRODUCER = null;
    //
    //	@Nullable
    //	@CapabilityInject(ITeslaHolder.class)
    //	public static Capability<ITeslaHolder> TESLA_HOLDER = null;

    public static boolean isLoaded() {
        //		return TESLA_CONSUMER != null && TESLA_PRODUCER != null && TESLA_HOLDER != null;
        return false;
    }

    public static boolean isEnergyReceiver(TileEntity tile, Direction side) {
        return isLoaded() && _isEnergyReceiver(tile, side);
    }

    public static int sendEnergy(TileEntity tile, Direction side, int amount, boolean simulate) {
        if (isLoaded()) {
            return _sendEnergy(tile, side, amount, simulate);
        } else {
            return 0;
        }
    }

    //	@Optional.Method(modid = Constants.TESLA_MOD_ID)
    private static boolean _isEnergyReceiver(TileEntity tile, Direction side) {
        return false;
        //		return TeslaHelper.TESLA_CONSUMER != null && tile.hasCapability(TeslaHelper.TESLA_CONSUMER, side);
    }

    //	@Optional.Method(modid = Constants.TESLA_MOD_ID)
    private static int _sendEnergy(TileEntity tile, Direction side, int amount, boolean simulate) {
        //		ITeslaConsumer consumer = tile.getCapability(TESLA_CONSUMER, side);
        //		if (consumer == null) {
        //			if (tile.hasCapability(TESLA_CONSUMER, side)) {
        //				Log.error("Tile claims to support Tesla but does not have the capability. {} {}", tile.getPos(), tile);
        //			}
        //			return 0;
        //		}
        //		return (int) consumer.givePower(amount, simulate);
        return 0;
    }

    public static boolean isTeslaCapability(Capability<?> capability) {
        if (!isLoaded()) {
            return false;
        }
        return false;
        //		return capability == TESLA_CONSUMER || capability == TESLA_HOLDER || capability == TESLA_PRODUCER;
    }
}
