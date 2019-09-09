/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology.genetics;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeList;
import genetics.api.individual.IChromosomeValue;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleFlowers;

public class ButterflyChromosomes {
	public static final IChromosomeList TYPES = GeneticsAPI.apiInstance.getChromosomeList("rootButterflies");
	/**
	 * Species of the bee. Alleles here must implement {@link IAlleleButterflySpecies}.
	 */
	public static final IChromosomeAllele<IAlleleButterflySpecies> SPECIES = TYPES.builder().name("species").asAllele(IAlleleButterflySpecies.class);
	/**
	 * Physical size.
	 */
	public static final IChromosomeValue<Float> SIZE = TYPES.builder().name("size").asValue(Float.class);
	/**
	 * Flight speed.
	 */
	public static final IChromosomeValue<Float> SPEED = TYPES.builder().name("speed").asValue(Float.class);
	/**
	 * How long the butterfly can last without access to matching pollinatables.
	 */
	public static final IChromosomeValue<Integer> LIFESPAN = TYPES.builder().name("lifespan").asValue(Integer.class);
	/**
	 * Species with a higher metabolism have a higher appetite and may cause more damage to their environment.
	 */
	public static final IChromosomeValue<Integer> METABOLISM = TYPES.builder().name("metabolism").asValue(Integer.class);
	/**
	 * Determines likelyhood of caterpillars and length of caterpillar/pupation phase. Also: Number of max caterpillars after mating?
	 */
	public static final IChromosomeValue<Integer> FERTILITY = TYPES.builder().name("fertility").asValue(Integer.class);
	/**
	 * Not sure yet.
	 */
	public static final IChromosomeValue<EnumTolerance> TEMPERATURE_TOLERANCE = TYPES.builder().name("temperature_tolerance").asValue(EnumTolerance.class);
	/**
	 * Not sure yet.
	 */
	public static final IChromosomeValue<EnumTolerance> HUMIDITY_TOLERANCE = TYPES.builder().name("humidity_tolerance").asValue(EnumTolerance.class);
	/**
	 * Only nocturnal butterflys/moths will fly at night. Allows daylight activity for naturally nocturnal species.
	 */
	public static final IChromosomeValue<Boolean> NOCTURNAL = TYPES.builder().name("nocturnal").asValue(Boolean.class);
	/**
	 * Only tolerant flyers will fly in the rain.
	 */
	public static final IChromosomeValue<Boolean> TOLERANT_FLYER = TYPES.builder().name("tolerant_flyer").asValue(Boolean.class);
	/**
	 * Fire resistance.
	 */
	public static final IChromosomeValue<Boolean> FIRE_RESIST = TYPES.builder().name("fire_resist").asValue(Boolean.class);
	/**
	 * Required flowers/leaves.
	 */
	public static final IChromosomeAllele<IAlleleFlowers> FLOWER_PROVIDER = TYPES.builder().name("flower_provider").asAllele(IAlleleFlowers.class);
	/**
	 * Extra effect to surroundings. (?)
	 */
	public static final IChromosomeAllele<IAlleleButterflyEffect> EFFECT = TYPES.builder().name("effect").asAllele(IAlleleButterflyEffect.class);

	public static final IChromosomeAllele<IAlleleButterflyCocoon> COCOON = TYPES.builder().name("cocoon").asAllele(IAlleleButterflyCocoon.class);

	private ButterflyChromosomes() {
	}
}
