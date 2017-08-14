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

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateSource;
import forestry.api.climate.IClimateState;

/**
 * The possible types of the {@link IClimateSource}. Which values the {@link IClimateSource} changes in a {@link IClimateState}.
 */
public enum ClimateSourceType {

	TEMPERATURE{
		
		@Override
		public boolean canChangeHumidity() {
			return false;
		}
		
		@Override
		public boolean canChangeTemperature() {
			return true;
		}
	}, HUMIDITY{
		
		@Override
		public boolean canChangeHumidity() {
			return true;
		}
		
		@Override
		public boolean canChangeTemperature() {
			return false;
		}
	}, BOTH{
		
		@Override
		public boolean canChangeHumidity() {
			return true;
		}
		
		@Override
		public boolean canChangeTemperature() {
			return true;
		}
	};
	
	public boolean affectClimateType(ClimateType type){
		return this == BOTH || type == ClimateType.HUMIDITY && this == HUMIDITY || type == ClimateType.TEMPERATURE && this == TEMPERATURE;
	}
	
	/**
	 * @return true if this sourceType can change the temperature on a {@link IClimateState}, false if can not.
	 */
	public abstract boolean canChangeTemperature();
	
	/**
	 * @return true if this sourceType can change the humidity on a {@link IClimateState}, false if can not.
	 */
	public abstract boolean canChangeHumidity();

}
