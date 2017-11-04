/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import java.util.Locale;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.ISpeciesRoot;

public enum EnumButterflyChromosome implements IChromosomeType {
	/**
	 * Species of the bee. Alleles here must implement {@link IAlleleButterflySpecies}.
	 */
	SPECIES(IAlleleButterflySpecies.class, "species", true),
	/**
	 * Physical size.
	 */
	SIZE(IAlleleFloat.class, "size", true),
	/**
	 * Flight speed.
	 */
	SPEED(IAlleleFloat.class, "speed"),
	/**
	 * How long the butterfly can last without access to matching pollinatables.
	 */
	LIFESPAN(IAlleleInteger.class, "life"),
	/**
	 * Species with a higher metabolism have a higher appetite and may cause more damage to their environment.
	 */
	METABOLISM(IAlleleInteger.class, "metabolism"),
	/**
	 * Determines likelyhood of caterpillars and length of caterpillar/pupation phase. Also: Number of max caterpillars after mating?
	 */
	FERTILITY(IAlleleInteger.class, "fertility"),
	/**
	 * Not sure yet.
	 */
	TEMPERATURE_TOLERANCE(IAlleleTolerance.class, "tempTol"),
	/**
	 * Not sure yet.
	 */
	HUMIDITY_TOLERANCE(IAlleleTolerance.class, "humidTol"),
	/**
	 * Only nocturnal butterflys/moths will fly at night. Allows daylight activity for naturally nocturnal species.
	 */
	NOCTURNAL(IAlleleBoolean.class, "nocturnal"),
	/**
	 * Only tolerant flyers will fly in the rain.
	 */
	TOLERANT_FLYER(IAlleleBoolean.class, "rainFlying"),
	/**
	 * Fire resistance.
	 */
	FIRE_RESIST(IAlleleBoolean.class, "fireproof"),
	/**
	 * Required flowers/leaves.
	 */
	FLOWER_PROVIDER(IAlleleFlowers.class, "flower"),
	/**
	 * Extra effect to surroundings. (?)
	 */
	EFFECT(IAlleleButterflyEffect.class, "effect"),

	COCOON(IAlleleButterflyCocoon.class, "cocoon");

	private final Class<? extends IAllele> alleleClass;
	private final String shortName;
	private final boolean neededOnClientSide;

	EnumButterflyChromosome(Class<? extends IAllele> alleleClass, String shortName) {
		this(alleleClass, shortName, false);
	}

	EnumButterflyChromosome(Class<? extends IAllele> alleleClass, String shortName, boolean neededOnClientSide) {
		this.alleleClass = alleleClass;
		this.shortName = shortName;
		this.neededOnClientSide = neededOnClientSide;
	}

	@Override
	public Class<? extends IAllele> getAlleleClass() {
		return alleleClass;
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public boolean isNeededOnClientSide() {
		return neededOnClientSide;
	}

	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return ButterflyManager.butterflyRoot;
	}
}
