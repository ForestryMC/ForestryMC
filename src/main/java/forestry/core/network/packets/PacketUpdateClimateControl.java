package forestry.core.network.packets;

import java.io.IOException;

import forestry.api.climate.IClimateControl;
import forestry.api.climate.IClimateControlProvider;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketUpdateClimateControl extends ForestryPacket implements IForestryPacketServer {
	private final BlockPos pos;
	private final float controlTemperature;
	private final float controlHumidity;

	public PacketUpdateClimateControl(IClimateControlProvider provider) {
		pos = provider.getCoordinates();

		IClimateControl control = provider.getClimateControl();
		controlTemperature = control.getControlTemperature();
		controlHumidity = control.getControlHumidity();
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.CLIMATE_CONTROL_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeFloat(controlTemperature);
		data.writeFloat(controlHumidity);
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			BlockPos pos = data.readBlockPos();
			float controlTemperature = data.readFloat();
			float controlHumidity = data.readFloat();

			TileEntity tile = player.world.getTileEntity(pos);
			IClimateControl control = null;
			if (tile instanceof IMultiblockComponent) {
				IMultiblockComponent component = (IMultiblockComponent) tile;
				IMultiblockLogic logic = component.getMultiblockLogic();
				if (logic.isConnected() && logic.getController() instanceof IClimateControlProvider) {
					control = ((IClimateControlProvider) logic.getController()).getClimateControl();
				}
			} else if (tile instanceof IClimateControlProvider) {
				control = ((IClimateControlProvider) tile).getClimateControl();
			}

			if (control != null) {
				control.setControlTemperature(controlTemperature);
				control.setControlHumidity(controlHumidity);
			}
		}
	}
}
