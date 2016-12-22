/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.BiomeDictionary;

/**
 * Set custom mutation requirements
 */
public interface IMutationBuilder {

	IMutation build();

	/**
	 * Prevent this mutation from being shown in the analyzers
	 */
	IMutationBuilder setIsSecret();

	/**
	 * Require a specific temperature for this mutation to occur
	 */
	IMutationBuilder restrictTemperature(EnumTemperature temperature);

	IMutationBuilder restrictTemperature(EnumTemperature minTemperature, EnumTemperature maxTemperature);

	/**
	 * Require a specific humidity for this mutation to occur
	 */
	IMutationBuilder restrictHumidity(EnumHumidity humidity);

	IMutationBuilder restrictHumidity(EnumHumidity minHumidity, EnumHumidity maxHumidity);

	/**
	 * Restrict this mutation to certain types of biomes.
	 *
	 * @param types The types of biomes this mutation can occur.
	 */
	IMutationBuilder restrictBiomeType(BiomeDictionary.Type... types);

	/**
	 * Restrict the days of the year that this mutation can occur
	 */
	IMutationBuilder restrictDateRange(int startMonth, int startDay, int endMonth, int endDay);

	/**
	 * Restrict the time of day that this mutation can occur
	 */
	IMutationBuilder requireDay();

	IMutationBuilder requireNight();

	/**
	 * Require a specific resource to be under the location of the mutation
	 */
	IMutationBuilder requireResource(IBlockState... acceptedBlockStates);

	IMutationBuilder requireResource(String oreDictName);

	/**
	 * Require some other custom mutation condition
	 */
	IMutationBuilder addMutationCondition(IMutationCondition mutationCondition);
}
