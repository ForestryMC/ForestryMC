/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

public interface IClimateControl {

    float getControlTemperature();

    float getControlHumidity();
    
    void setControlTemperature(float temperature);

    void setControlHumidity(float humidity);
	
}
