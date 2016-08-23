package forestry.greenhouse.tiles;

import java.io.IOException;

import forestry.api.climate.IClimateControl;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import net.minecraft.nbt.NBTTagCompound;

public class TileGreenhouseClimateControl extends TileGreenhouse implements IGreenhouseComponent.ClimateControl {

	private final forestry.core.climate.ClimateControl climateControl;
	
	public TileGreenhouseClimateControl() {
		super();
		climateControl = new forestry.core.climate.ClimateControl();
	}
	
	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		climateControl.readFromNBT(packetData);
	}
	
	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		climateControl.writeToNBT(packetData);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		climateControl.readFromNBT(data);
		super.readFromNBT(data);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		climateControl.writeToNBT(data);
		return super.writeToNBT(data);
	}
	
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		climateControl.writeData(data);
		super.writeGuiData(data);
	}
	
	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		climateControl.readData(data);
		super.readGuiData(data);
	}
	
	@Override
	public IClimateControl getClimateControl() {
		return climateControl;
	}

}
