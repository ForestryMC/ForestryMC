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
package forestry.greenhouse;

import forestry.api.climate.EnumClimatiserModes;
import forestry.api.climate.EnumClimatiserTypes;
import forestry.api.climate.IClimateControl;
import forestry.api.climate.IClimatePosition;
import forestry.api.climate.IClimateRegion;
import forestry.api.climate.IClimatiserDefinition;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.core.climate.ClimateSource;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouseClimatiser;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GreenhouseClimateSource<P extends TileGreenhouseClimatiser> extends ClimateSource<P> {

	public GreenhouseClimateSource(int ticksForChange) {
		super(ticksForChange);
	}
	
	@Override
	public boolean changeClimate(int tickCount, IClimateRegion region) {
		World world = region.getWorld();
		IClimatiserDefinition definition = provider.getDefinition();
		IMultiblockLogic logic = provider.getMultiblockLogic();
		IMultiblockController controller = logic.getController();
		Iterable<BlockPos> positionsInRange = provider.getPositionsInRange();
		
		boolean hasChange = false;
		boolean isActive = false;
		if(logic.isConnected() && controller.isAssembled() && controller instanceof IGreenhouseControllerInternal && positionsInRange != null && positionsInRange.iterator().hasNext() && region != null){
			IGreenhouseControllerInternal greenhouseInternal = (IGreenhouseControllerInternal) controller;
			IClimateControl climateControl = getClimateControl(greenhouseInternal);
			float controlTemp = climateControl.getControlTemperature();
			float controlHum = climateControl.getControlHumidity();
			isActive = greenhouseInternal.canWork() && provider.canWork();
			EnumClimatiserTypes type = definition.getType();
			EnumClimatiserModes mode = definition.getMode();
			float maxChange = definition.getChange();
			if(isActive){
				if(canChange(type, EnumClimatiserTypes.TEMPERATURE)){
					if(region.getTemperature() < controlTemp) {
						if(!canChange(mode, EnumClimatiserModes.POSITIVE)){
							isActive = false;
						}
					}else if(region.getTemperature() > controlTemp){
						if(!canChange(mode, EnumClimatiserModes.NEGATIVE)){
							isActive = false;
						}
					}
				}
				if (canChange(type, EnumClimatiserTypes.HUMIDITY)) {
					if(region.getTemperature() < controlHum) {
						if(!canChange(mode, EnumClimatiserModes.POSITIVE)){
							isActive = false;
						}
					}else if(region.getTemperature() > controlHum){
						if(!canChange(mode, EnumClimatiserModes.NEGATIVE)){
							isActive = false;
						}
					}
				}
			}
			
			if (isActive) {	
				for(BlockPos pos : positionsInRange){
					IClimatePosition position = region.getPositions().get(pos);
					if(position != null){
						double distance = pos.distanceSq(provider.getCoordinates());
						double range = definition.getRange();
						if(distance <= range){
							float change = maxChange;
							if(distance > 0){
								change = (float) (maxChange / distance);
							}
							if (canChange(type, EnumClimatiserTypes.TEMPERATURE)) {
								if(position.getTemperature() < controlTemp) {
									if(canChange(mode, EnumClimatiserModes.POSITIVE)){
										position.addTemperature(Math.min(change, controlTemp - position.getTemperature()));
										hasChange = true;
									}
								}else if(position.getTemperature() > controlTemp){
									if(canChange(mode, EnumClimatiserModes.NEGATIVE)){
										position.addTemperature(-Math.min(position.getTemperature() - controlTemp, change));
										hasChange = true;
									}
								}
							}
							if (canChange(type, EnumClimatiserTypes.HUMIDITY)) {
								if(position.getHumidity() < controlHum) {
									if(canChange(mode, EnumClimatiserModes.POSITIVE)){
										position.addHumidity(Math.min(change, controlHum - position.getHumidity()));
										hasChange = true;
									}
								}else if(position.getHumidity() > controlHum){
									if(canChange(mode, EnumClimatiserModes.NEGATIVE)){
										position.addHumidity(-Math.min(position.getHumidity() - controlHum, change));
										hasChange = true;
									}
								}
							}
						}
					}
				}
			}
		}
		if(provider.isActive() != isActive){
			provider.setActive(isActive);
		}
		return hasChange;
	}
	
	protected boolean canChange(EnumClimatiserTypes climatiserType, EnumClimatiserTypes type){
		if(type == climatiserType || climatiserType == EnumClimatiserTypes.NONE){
			return true;
		}
		return false;
	}
	
	protected boolean canChange(EnumClimatiserModes climatiserMode, EnumClimatiserModes mode){
		if(mode == climatiserMode || climatiserMode == EnumClimatiserModes.NONE){
			return true;
		}
		return false;
	}
	
	protected IClimateControl getClimateControl(IGreenhouseControllerInternal greenhouseInternal){
		return greenhouseInternal.getClimateControl();
	}

}
