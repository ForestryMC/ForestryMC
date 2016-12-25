package forestry.greenhouse.tiles;

import java.io.IOException;

import forestry.api.climate.IClimateInfo;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.climate.ClimateInfo;
import forestry.core.network.PacketBufferForestry;
import net.minecraft.nbt.NBTTagCompound;

public class TileGreenhouseClimateControl extends TileGreenhouse implements IGreenhouseComponent.ClimateControl {

	private IClimateInfo climateControl;

	public TileGreenhouseClimateControl() {
		super();
		climateControl = ClimateInfo.MAX;
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		climateControl = new ClimateInfo(packetData);
	}

	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		packetData.setFloat("Temperature", climateControl.getTemperature());
		packetData.setFloat("Humidity", climateControl.getHumidity());
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		climateControl = new ClimateInfo(data);
		super.readFromNBT(data);
	}


	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data.setFloat("Temperature", climateControl.getTemperature());
		data.setFloat("Humidity", climateControl.getHumidity());
		return super.writeToNBT(data);
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		data.writeFloat(climateControl.getTemperature());
		data.writeFloat(climateControl.getHumidity());
		super.writeGuiData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		climateControl = new ClimateInfo(data);
		super.readGuiData(data);
	}

	@Override
	public IClimateInfo getControlClimate() {
		return climateControl;
	}

	@Override
	public void setControlClimate(IClimateInfo climateControl) {
		this.climateControl = climateControl;
	}

}
