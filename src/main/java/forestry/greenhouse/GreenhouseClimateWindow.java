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

import javax.annotation.Nullable;

import forestry.api.climate.EnumClimatiserModes;
import forestry.api.climate.EnumClimatiserTypes;
import forestry.api.climate.IClimateInfo;
import forestry.api.climate.IClimatePosition;
import forestry.api.climate.IClimateRegion;
import forestry.api.climate.IClimatiserDefinition;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.core.climate.BiomeClimateInfo;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouseWindow;
import forestry.greenhouse.tiles.TileGreenhouseWindow.WindowMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class GreenhouseClimateWindow extends GreenhouseClimateSource<TileGreenhouseWindow> {
	@Nullable
	private Biome biome;

	public GreenhouseClimateWindow(int ticksForChange) {
		super(ticksForChange);
	}

	@Override
	public boolean changeClimate(int tickCount, IClimateRegion region) {
		if (provider == null) {
			return false;
		}
		World world = region.getWorld();
		IClimatiserDefinition definition = provider.getDefinition();
		IMultiblockLogic logic = provider.getMultiblockLogic();
		IMultiblockController controller = logic.getController();
		Iterable<BlockPos> positionsInRange = provider.getPositionsInRange();

		boolean hasChange = false;
		WindowMode windowMode = provider.getMode();
		if (logic.isConnected() && controller.isAssembled() && controller instanceof IGreenhouseControllerInternal && positionsInRange.iterator().hasNext()) {
			IGreenhouseControllerInternal greenhouseInternal = (IGreenhouseControllerInternal) controller;
			IClimateInfo climateInfo = getClimateControl(greenhouseInternal);
			float controlTemp = climateInfo.getTemperature();
			float controlHum = climateInfo.getHumidity();
			if (!greenhouseInternal.canWork()) {
				if (windowMode == WindowMode.OPEN) {
					provider.setMode(windowMode = WindowMode.CONTROL);
				}
			} else {
				if (windowMode == WindowMode.CONTROL) {
					provider.setMode(windowMode = WindowMode.OPEN);
				}
			}
			if (windowMode == WindowMode.OPEN) {
				EnumClimatiserTypes type = definition.getType();
				EnumClimatiserModes mode = definition.getMode();
				float maxChange = definition.getChange();

				for (BlockPos pos : positionsInRange) {
					IClimatePosition position = region.getPosition(pos);
					if (position != null) {
						double distance = pos.distanceSq(provider.getCoordinates());
						float change = maxChange;
						if (distance > 0) {
							change = (float) (maxChange / distance);
						}
						if (canChange(type, EnumClimatiserTypes.TEMPERATURE)) {
							if (position.getTemperature() < controlTemp) {
								if (canChange(mode, EnumClimatiserModes.POSITIVE)) {
									position.addTemperature(Math.min(change, controlTemp - position.getTemperature()));
									hasChange = true;
								}
							} else if (position.getTemperature() > controlTemp) {
								if (canChange(mode, EnumClimatiserModes.NEGATIVE)) {
									position.addTemperature(-Math.min(position.getTemperature() - controlTemp, change));
									hasChange = true;
								}
							}
						}
						if (canChange(type, EnumClimatiserTypes.HUMIDITY)) {
							if (position.getHumidity() < controlHum) {
								if (canChange(mode, EnumClimatiserModes.POSITIVE)) {
									position.addHumidity(Math.min(change, controlHum - position.getHumidity()));
									hasChange = true;
								}
							} else if (position.getHumidity() > controlHum) {
								if (canChange(mode, EnumClimatiserModes.NEGATIVE)) {
									position.addHumidity(-Math.min(position.getHumidity() - controlHum, change));
									hasChange = true;
								}
							}
						}
					}
				}
			}
		}
		return hasChange;
	}

	@Override
	protected IClimateInfo getClimateControl(IGreenhouseControllerInternal greenhouseInternal) {
		if (biome == null) {
			BlockPos pos = provider.getCoordinates();
			World world = provider.getWorld();
			biome = world.getBiome(pos);
		}
		return BiomeClimateInfo.getInfo(biome);
	}

}
