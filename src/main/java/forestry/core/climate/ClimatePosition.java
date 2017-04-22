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

import forestry.api.climate.IClimateInfo;
import forestry.api.climate.IClimatePosition;
import forestry.api.climate.IClimateRegion;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class ClimatePosition implements IClimatePosition {
	protected final IClimateRegion region;

	protected final BlockPos pos;
	protected float temperature;
	protected float humidity;

	public ClimatePosition(IClimateRegion rRegion, BlockPos pos, NBTTagCompound nbtTag) {
		this.region = rRegion;
		this.pos = pos;
		readFromNBT(nbtTag);
	}

	public ClimatePosition(IClimateRegion region, BlockPos pos) {
		Biome biome = region.getWorld().getBiome(pos);
		this.region = region;
		this.pos = pos;
		this.temperature = biome.getTemperature();
		this.humidity = biome.getRainfall();
	}

	public ClimatePosition(IClimateRegion region, BlockPos pos, float temperature, float humidity) {
		this.region = region;
		this.pos = pos;
		this.temperature = temperature;
		this.humidity = humidity;
	}

	@Override
	public IClimateRegion getClimateRegion() {
		return region;
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	@Override
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	@Override
	public float getTemperature() {
		return temperature;
	}

	@Override
	public void addTemperature(float temperature) {
		setTemperature(getTemperature() + temperature);
	}

	@Override
	public void addHumidity(float humidity) {
		setHumidity(getHumidity() + humidity);
	}

	@Override
	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}

	@Override
	public float getHumidity() {
		return humidity;
	}
	
	@Override
	public IClimateInfo getInfo() {
		return new ClimateInfo(temperature, humidity);
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
