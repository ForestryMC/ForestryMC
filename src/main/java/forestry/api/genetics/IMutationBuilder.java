/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

import genetics.api.mutation.IMutation;

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
	IMutationBuilder restrictBiomeType(Biome.Category... types);

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
	IMutationBuilder requireResource(BlockState... acceptedBlockStates);

	/**
	 * Require some other custom mutation condition
	 */
	IMutationBuilder addMutationCondition(IMutationCondition mutationCondition);
}
