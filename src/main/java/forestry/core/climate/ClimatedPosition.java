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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import forestry.api.core.ForestryAPI;
import forestry.api.core.climate.IClimateWorld;
import forestry.api.core.climate.IClimatedPosition;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.greenhouse.IGreenhouseState;
import forestry.api.multiblock.IGreenhouseController;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class ClimatedPosition implements IClimatedPosition {

	@Nonnull 
	protected final IClimateWorld climateWorld;
	@Nonnull 
	protected final BlockPos pos;
	protected float temperature;
	protected float humidity;
	
	public ClimatedPosition(IClimateWorld climateWorld, BlockPos pos) {
		this.climateWorld = climateWorld;
		this.pos = pos;
	}
	
	public ClimatedPosition(@Nonnull IClimateWorld climateWorld, @Nonnull BlockPos pos, float temperature, float humidity) {
		this.climateWorld = climateWorld;
		this.pos = pos;
		this.temperature = temperature;
		this.humidity = humidity;
	}
	
	@Nonnull 
	@Override
	public IClimateWorld getClimateWorld() {
		return climateWorld;
	}

	@Nonnull 
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
	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}

	@Override
	public float getHumidity() {
		return humidity;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		temperature = nbt.getFloat("Temperature");
		humidity = nbt.getFloat("Humidity");
		NBTTagList handlersNBT = nbt.getTagList("Handlers", 10);
		for(int i = 0;i < handlersNBT.tagCount();i++){
			NBTTagCompound handlerNBT = handlersNBT.getCompoundTagAt(i);
			
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("Temperature", temperature);
		nbt.setFloat("Humidity", humidity);
		return nbt;
	}
	
	@Override
	public void updateClimate() {
		boolean canHold = false;
		for(IClimateHandler handler : getHandlers()){
			if(handler != null){
				if(handler.canHoldClimate(this)){
					canHold = true;
					break;
				}
			}
		}
		if(canHold){
			for(EnumFacing facing : EnumFacing.VALUES){
				BlockPos facePos = pos.offset(facing);
				IClimatedPosition climatedPosition = climateWorld.getPosition(facePos);
				if(climatedPosition != null){
					if(temperature > climatedPosition.getTemperature() + 0.01F){
						temperature-= 0.01F;
						climatedPosition.setTemperature(climatedPosition.getTemperature() + 0.01F);
					}
					if(humidity > climatedPosition.getHumidity() + 0.01F){
						humidity-= 0.01F;
						climatedPosition.setHumidity(climatedPosition.getHumidity() + 0.01F);
					}
				}
			}
		}else{
			Biome biome = climateWorld.getWorld().getBiome(pos);
			float biomeTemperature = biome.getTemperature();
			float biomeHumidity = biome.getRainfall();
			
			if(temperature != biomeTemperature){
				if(temperature > biomeTemperature){
					temperature-=0.01F;
				}else{
					temperature+=0.01F;
				}
			}
			
			if(humidity != biomeHumidity){
				if(humidity > biomeHumidity){
					humidity-=0.01F;
				}else{
					humidity+=0.01F;
				}
			}
		}
	}
	
	@Override
	public List<IClimateHandler> getHandlers() {
		List<IClimateHandler> handlers = new ArrayList<>();
		TileEntity tile = climateWorld.getWorld().getTileEntity(pos);
		if(tile != null){
			if(tile instanceof IClimateHandler){
				handlers.add((IClimateHandler) tile);
			}
			if(tile instanceof IMultiblockComponent){
				IMultiblockComponent component = (IMultiblockComponent) tile;
				IMultiblockLogic logic = component.getMultiblockLogic();
				if(logic.getController() instanceof IClimateHandler){
					handlers.add((IClimateHandler) logic.getController());
				}
			}
			IGreenhouseController greenhouse = GreenhouseManager.greenhouseHelper.getGreenhouseController(climateWorld.getWorld(), pos);
			if(greenhouse != null){
				handlers.add(greenhouse);
			}
		}
		return handlers;
	}

}
