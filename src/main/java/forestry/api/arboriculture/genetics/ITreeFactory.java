/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.arboriculture.genetics;

import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.ILeafSpriteProvider;

import java.awt.*;

public interface ITreeFactory {
    /**
     * Creates a new tree species.
     * See IAlleleTreeSpeciesBuilder and IAlleleSpeciesBuilder for adding additional properties to the returned species.
     *
     * @param uid               Unique Identifier for this species
     * @param modID             The modID from the mod of the species
     * @param speciesIdentifier Unique Identifier for this species independent from the individual type
     * @return a new tree species allele.
     */
    IAlleleTreeSpeciesBuilder createSpecies(String modID, String uid, String speciesIdentifier);

    /**
     * Get one of the built-in Forestry leaf types. Default type is deciduous.
     */
    ILeafSpriteProvider getLeafIconProvider(EnumLeafType enumLeafType, Color color, Color colorPollinated);
}
