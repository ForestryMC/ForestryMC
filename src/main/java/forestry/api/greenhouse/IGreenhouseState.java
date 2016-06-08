/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.multiblock.IMultiblockComponent;

public interface IGreenhouseState {
	
	@Nonnull
	EnumTemperature getTemperature();

	@Nonnull
	EnumHumidity getHumidity();

	float getExactTemperature();

	float getExactHumidity();
	
	Set<IInternalBlock> getInternalBlocks();
	
	Collection<IMultiblockComponent> getGreenhouseComponents();
	
}
