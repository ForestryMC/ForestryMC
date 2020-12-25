/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.apiculture.genetics;

import genetics.api.classification.IClassification;

public interface IBeeFactory {

    /**
     * Creates a new bee species.
     * See IAlleleBeeSpeciesBuilder and IAlleleSpeciesBuilder for adding additional properties to the returned species.
     *
     * @param modId The modId of the mod that is creating the species
     * @param uid   Unique Identifier for this species
     * @return a new bee species allele.
     */
    IAlleleBeeSpeciesBuilder createSpecies(String modId, String uid, String speciesIdentifier);

    /**
     * Creates a new bee branch.
     * Must be registered with AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup();
     *
     * @param uid        Unique Identifier for this branch
     * @param scientific approximates a "genus" in real life. Real life examples: "Micrapis", "Megapis"
     * @return a new bee branch
     */
    IClassification createBranch(String uid, String scientific);
}
