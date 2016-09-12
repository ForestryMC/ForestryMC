package forestry.greenhouse;

import forestry.api.climate.EnumClimatiserModes;
import forestry.api.climate.EnumClimatiserTypes;
import forestry.api.climate.IClimateControl;
import forestry.api.climate.IClimatePosition;
import forestry.api.climate.IClimateRegion;
import forestry.api.climate.IClimatiserDefinition;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.core.climate.BiomeClimateControl;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouseWindow;
import forestry.greenhouse.tiles.TileGreenhouseWindow.WindowMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class GreenhouseClimateWindow extends GreenhouseClimateSource<TileGreenhouseWindow>{

	private Biome biome;
	
	public GreenhouseClimateWindow(int ticksForChange) {
		super(ticksForChange);
	}
	
	@Override
	public void changeClimate(int tickCount, IClimateRegion region) {
		World world = region.getWorld();
		IClimatiserDefinition definition = provider.getDefinition();
		IMultiblockLogic logic = provider.getMultiblockLogic();
		IMultiblockController controller = logic.getController();
		Iterable<BlockPos> positionsInRange = provider.getPositionsInRange();
		
		WindowMode windowMode = provider.getMode();
		if(logic.isConnected() && controller.isAssembled() && controller instanceof IGreenhouseControllerInternal && positionsInRange != null && positionsInRange.iterator().hasNext() && region != null){
			IGreenhouseControllerInternal greenhouseInternal = (IGreenhouseControllerInternal) controller;
			IClimateControl climateControl = getClimateControl(greenhouseInternal);
			float controlTemp = climateControl.getControlTemperature();
			float controlHum = climateControl.getControlHumidity();
			if(!greenhouseInternal.canWork()){
				if(windowMode == WindowMode.OPEN){
					provider.setMode(windowMode = WindowMode.CONTROL);
				}
			}else{
				if(windowMode == WindowMode.CONTROL){
					provider.setMode(windowMode = WindowMode.OPEN);
				}
			}
			if(windowMode == WindowMode.OPEN){
				int dimensionID = world.provider.getDimension();
				EnumClimatiserTypes type = definition.getType();
				EnumClimatiserModes mode = definition.getMode();
				float maxChange = definition.getChange();
				
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
									}
								}else if(position.getTemperature() > controlTemp){
									if(canChange(mode, EnumClimatiserModes.NEGATIVE)){
										position.addTemperature(-Math.min(position.getTemperature() - controlTemp, change));
									}
								}
							}
							if (canChange(type, EnumClimatiserTypes.HUMIDITY)) {
								if(position.getHumidity() < controlHum) {
									if(canChange(mode, EnumClimatiserModes.POSITIVE)){
										position.addHumidity(Math.min(change, controlHum - position.getHumidity()));
									}
								}else if(position.getHumidity() > controlHum){
									if(canChange(mode, EnumClimatiserModes.NEGATIVE)){
										position.addHumidity(-Math.min(position.getHumidity() - controlHum, change));
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	protected IClimateControl getClimateControl(IGreenhouseControllerInternal greenhouseInternal) {
		if(biome == null){
			BlockPos pos = provider.getCoordinates();
			World world = provider.getWorld();
			biome = world.getBiome(pos);
		}
		return BiomeClimateControl.getControl(biome);
	}

}
