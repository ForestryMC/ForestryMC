/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology.genetics;

public interface IButterflyFactory {
    /**
     * Creates a new butterfly species.
     * See IAlleleButterflySpeciesBuilder and IAlleleSpeciesBuilder for adding additional properties to the returned species.
     *
     * @param uid Unique Identifier for this species
     * @return a new butterfly species allele.
     */
    IAlleleButterflySpeciesBuilder createSpecies(String modId, String uid, String speciesIdentifier);
}
