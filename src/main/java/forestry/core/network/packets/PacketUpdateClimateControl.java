package forestry.core.network.packets;

import java.io.IOException;

import forestry.api.climate.IClimateControl;
import forestry.api.climate.IClimateControlProvider;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class PacketUpdateClimateControl extends PacketCoordinates implements IForestryPacketServer {

    private float controlTemperature;
    private float controlHumidity;
	
	public PacketUpdateClimateControl() {
	}
	
	public PacketUpdateClimateControl(IClimateControlProvider provider) {
		super(provider.getCoordinates());
		
		IClimateControl control = provider.getClimateControl();
		controlTemperature = control.getControlTemperature();
		controlHumidity = control.getControlHumidity();
	}
	
	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		
		data.writeFloat(controlTemperature);
		data.writeFloat(controlHumidity);
	}
	
	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		
		controlTemperature = data.readFloat();
		controlHumidity = data.readFloat();
	}
	
	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.CLIMATE_CONTROL_UPDATE;
	}
	
	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		TileEntity tile = getTarget(player.worldObj);
		IClimateControl control = null;
		if(tile instanceof IMultiblockComponent){
			IMultiblockComponent component = (IMultiblockComponent) tile;
			IMultiblockLogic logic = component.getMultiblockLogic();
			if(logic.isConnected() && logic.getController() instanceof IClimateControlProvider){
				control = ((IClimateControlProvider) logic.getController()).getClimateControl();
			}
		}else if(tile instanceof IClimateControlProvider){
			control = ((IClimateControlProvider) tile).getClimateControl();
		}
		if(control != null){
			control.setControlTemperature(controlTemperature);
			control.setControlHumidity(controlHumidity);
		}
	}

}
