package forestry.core.network.packets;

import java.io.IOException;

import forestry.api.climate.IClimateControlProvider;
import forestry.api.climate.IClimateInfo;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.core.climate.ClimateInfo;
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
	private final IClimateInfo climateInfo;

	public PacketUpdateClimateControl(IClimateControlProvider provider) {
		pos = provider.getCoordinates();

		climateInfo = provider.getControlClimate();
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.CLIMATE_CONTROL_UPDATE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeFloat(climateInfo.getTemperature());
		data.writeFloat(climateInfo.getHumidity());
	}

	public static class Handler implements IForestryPacketHandlerServer {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			BlockPos pos = data.readBlockPos();
			float tmperature = data.readFloat();
			float humidity = data.readFloat();

			TileEntity tile = player.world.getTileEntity(pos);
			IClimateControlProvider control = null;
			if (tile instanceof IMultiblockComponent) {
				IMultiblockComponent component = (IMultiblockComponent) tile;
				IMultiblockLogic logic = component.getMultiblockLogic();
				if (logic.isConnected() && logic.getController() instanceof IClimateControlProvider) {
					control = (IClimateControlProvider) logic.getController();
				}
			} else if (tile instanceof IClimateControlProvider) {
				control = (IClimateControlProvider) tile;
			}

			if (control != null) {
				control.setControlClimate(new ClimateInfo(tmperature, humidity));
			}
		}
	}
}
