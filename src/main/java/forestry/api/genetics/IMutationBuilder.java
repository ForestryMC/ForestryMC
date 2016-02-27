/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

/** Set custom mutation requirements */
public interface IMutationBuilder<C extends IChromosomeType<C>> {
	@Nonnull
	IMutation<C> build();

	/** Prevent this mutation from being shown in the analyzers */
	IMutationBuilder<C> setIsSecret();

	/** Require a specific temperature for this mutation to occur */
	IMutationBuilder<C> restrictTemperature(EnumTemperature temperature);

	IMutationBuilder<C> restrictTemperature(EnumTemperature minTemperature, EnumTemperature maxTemperature);

	/** Require a specific humidity for this mutation to occur */
	IMutationBuilder<C> restrictHumidity(EnumHumidity humidity);

	IMutationBuilder<C> restrictHumidity(EnumHumidity minHumidity, EnumHumidity maxHumidity);

	/**
	 * Restrict this mutation to certain types of biomes.
	 * @param types The types of biomes this mutation can occur.
	 */
	IMutationBuilder<C> restrictBiomeType(BiomeDictionary.Type... types);

	/** Restrict the days of the year that this mutation can occur */
	IMutationBuilder<C> restrictDateRange(int startMonth, int startDay, int endMonth, int endDay);

	/** Restrict the time of day that this mutation can occur */
	IMutationBuilder<C> requireDay();

	IMutationBuilder<C> requireNight();

	/** Require a specific resource to be under the location of the mutation */
	IMutationBuilder<C> requireResource(Block block, int meta);

	IMutationBuilder<C> requireResource(String oreDictName);

	/** Require some other custom mutation condition */
	IMutationBuilder<C> addMutationCondition(IMutationCondition mutationCondition);
}
