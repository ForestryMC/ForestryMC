/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.climate;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

import forestry.api.climate.IClimateControl;

public class ClimateControl implements IClimateControl, INbtWritable, INbtReadable, IStreamable {

	private float temperature;
	private float humidity;
	
	public ClimateControl() {
		temperature = 2.0F;
		humidity = 2.0F;
	}
	
	@Override
	public float getControlTemperature() {
		return temperature;
	}

	@Override
	public float getControlHumidity() {
		return humidity;
	}

	@Override
	public void setControlTemperature(float temperature) {
		if(temperature > 2.0F){
			temperature = 2.0F;
		}else{
			this.temperature = temperature;
		}
	}

	@Override
	public void setControlHumidity(float humidity) {
		if(humidity > 2.0F){
			humidity = 2.0F;
		}else{
			this.humidity = humidity;
		}
	}
	
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeFloat(temperature);
		data.writeFloat(humidity);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		temperature = data.readFloat();
		humidity = data.readFloat();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		temperature = nbt.getFloat("Temperature");
		humidity = nbt.getFloat("Humidity");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("Temperature", temperature);
		nbt.setFloat("Humidity", humidity);
		return nbt;
	}

}
