/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.genetics.alleles;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import genetics.api.classification.IClassification;

public interface IAlleleSpeciesBuilder<B> {

    B cast();

    IAlleleForestrySpecies build();

    B setTemperature(EnumTemperature temperature);

    B setHumidity(EnumHumidity humidity);

    B setHasEffect();

    /**
     * Secret species are not shown in creative mode.
     */
    B setIsSecret();

    /**
     * Uncounted species do not count toward total species discovered.
     */
    B setIsNotCounted();

    /**
     * Whether this species is genetically dominant (false means it is recessive)
     */
    B setDominant(boolean isDominant);

    /**
     * Binomial name of the species sans genus. "humboldti" will have the bee species flavour name be "Apis humboldti". Feel free to use fun names or null.
     */
    B setBinomial(String binomial);

    /**
     * Authority for the binomial name, e.g. "Sengir" on species of base Forestry.
     */
    B setAuthority(String authority);

    /**
     * Unlocalized description for this species
     */
    B setDescriptionKey(String description);

    /**
     * Unlocalized name for this species
     */
    B setTranslationKey(String translationKey);

    /**
     * Classification of this species
     */
    B setBranch(IClassification branch);

    /**
     * Manually the genetic complexity.
     * If this is not set, the complexity is based on the number of breeding steps to reach this species.
     *
     * @see IAlleleForestrySpecies#getComplexity()
     */
    B setComplexity(int complexity);
}
