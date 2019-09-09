/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.genetics;

import net.minecraft.util.math.Vec3i;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeList;
import genetics.api.individual.IChromosomeValue;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleFlowers;

/**
 * Enum representing the order of chromosomes in a bee's genome and what they control.
 *
 * @author SirSengir
 */
public class BeeChromosomes {
	public static final IChromosomeList TYPES = GeneticsAPI.apiInstance.getChromosomeList("rootBees");
	/**
	 * Species of the bee. Alleles here must implement {@link IAlleleBeeSpecies}.
	 */
	public static final IChromosomeAllele<IAlleleBeeSpecies> SPECIES = TYPES.builder().name("species").asAllele(IAlleleBeeSpecies.class);
	/**
	 * (Production) Speed of the bee.
	 */
	public static final IChromosomeValue<Float> SPEED = TYPES.builder().name("speed").asValue(Float.class);
	/**
	 * Lifespan of the bee.
	 */
	public static final IChromosomeValue<Integer> LIFESPAN = TYPES.builder().name("lifespan").asValue(Integer.class);
	/**
	 * Fertility of the bee. Determines number of offspring.
	 */
	public static final IChromosomeValue<Integer> FERTILITY = TYPES.builder().name("fertility").asValue(Integer.class);
	/**
	 * Temperature difference to its native supported one the bee can tolerate.
	 */
	public static final IChromosomeValue<EnumTolerance> TEMPERATURE_TOLERANCE = TYPES.builder().name("temperature_tolerance").asValue(EnumTolerance.class);
	/**
	 * If true, a naturally diurnal bee can work during the night. If true, a naturally nocturnal bee can work during the day.
	 */
	public static final IChromosomeValue<Boolean> NEVER_SLEEPS = TYPES.builder().name("never_sleeps").asValue(Boolean.class);
	/**
	 * Humidity difference to its native supported one the bee can tolerate.
	 */
	public static final IChromosomeValue<EnumTolerance> HUMIDITY_TOLERANCE = TYPES.builder().name("humidity_tolerance").asValue(EnumTolerance.class);
	/**
	 * If true the bee can work during rain.
	 */
	public static final IChromosomeValue<Boolean> TOLERATES_RAIN = TYPES.builder().name("speed").asValue(Boolean.class);
	/**
	 * If true, the bee can work without a clear view of the sky.
	 */
	public static final IChromosomeValue<Boolean> CAVE_DWELLING = TYPES.builder().name("tolerates_rain").asValue(Boolean.class);
	/**
	 * Contains the supported flower provider.
	 */
	public static final IChromosomeAllele<IAlleleFlowers> FLOWER_PROVIDER = TYPES.builder().name("flower_provider").asAllele(IAlleleFlowers.class);
	/**
	 * Determines pollination speed.
	 */
	public static final IChromosomeValue<Integer> FLOWERING = TYPES.builder().name("flowering").asValue(Integer.class);
	/**
	 * Determines the size of the bee's territory.
	 */
	public static final IChromosomeValue<Vec3i> TERRITORY = TYPES.builder().name("territory").asValue(Vec3i.class);
	/**
	 * Determines the bee's effect.
	 */
	public static final IChromosomeAllele<IAlleleBeeEffect> EFFECT = TYPES.builder().name("effect").asAllele(IAlleleBeeEffect.class);

	private BeeChromosomes() {
	}
}
