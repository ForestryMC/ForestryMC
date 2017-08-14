/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

public enum GreenhouseState {
	CLOSED,
	//If the greenhouse is not closed.
	OPEN,
	//If one chunk of the greenhouse is unloaded.
	UNLOADED_CHUNK,
	//The greenhouse has this state 20 ticks after all chunks are loaded, needed to load all climate sources.
	UNLOADED,
	//If the greenhouse is not assembled.
	UNREADY
}
