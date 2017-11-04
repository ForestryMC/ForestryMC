/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.Locale;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.ISpeciesRoot;

/**
 * Enum representing the order of chromosomes in a bee's genome and what they control.
 *
 * @author SirSengir
 */
public enum EnumBeeChromosome implements IChromosomeType {
	/**
	 * Species of the bee. Alleles here must implement {@link IAlleleBeeSpecies}.
	 */
	SPECIES(IAlleleBeeSpecies.class, "species", true),
	/**
	 * (Production) Speed of the bee.
	 */
	SPEED(IAlleleFloat.class, "speed"),
	/**
	 * Lifespan of the bee.
	 */
	LIFESPAN(IAlleleInteger.class, "lifespan"),
	/**
	 * Fertility of the bee. Determines number of offspring.
	 */
	FERTILITY(IAlleleInteger.class, "fertility"),
	/**
	 * Temperature difference to its native supported one the bee can tolerate.
	 */
	TEMPERATURE_TOLERANCE(IAlleleTolerance.class, "tempTol"),
	/**
	 * If true, a naturally diurnal bee can work during the night. If true, a naturally nocturnal bee can work during the day.
	 */
	NEVER_SLEEPS(IAlleleBoolean.class, "nocturnal"),
	/**
	 * Humidity difference to its native supported one the bee can tolerate.
	 */
	HUMIDITY_TOLERANCE(IAlleleTolerance.class, "humidTol"),
	/**
	 * If true the bee can work during rain.
	 */
	TOLERATES_RAIN(IAlleleBoolean.class, "rainFlying"),
	/**
	 * If true, the bee can work without a clear view of the sky.
	 */
	CAVE_DWELLING(IAlleleBoolean.class, "caveDwell"),
	/**
	 * Contains the supported flower provider.
	 */
	FLOWER_PROVIDER(IAlleleFlowers.class, "flower"),
	/**
	 * Determines pollination speed.
	 */
	FLOWERING(IAlleleInteger.class, "flowering"),
	/**
	 * Determines the size of the bee's territory.
	 */
	TERRITORY(IAlleleArea.class, "territory", true),
	/**
	 * Determines the bee's effect.
	 */
	EFFECT(IAlleleBeeEffect.class, "effect", true);

	private final Class<? extends IAllele> alleleClass;
	private final String shortName;
	private final boolean neededOnClientSide;

	EnumBeeChromosome(Class<? extends IAllele> alleleClass, String shortName) {
		this(alleleClass, shortName, false);
	}

	EnumBeeChromosome(Class<? extends IAllele> alleleClass, String shortName, boolean neededOnClientSide) {
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
		return BeeManager.beeRoot;
	}
}
