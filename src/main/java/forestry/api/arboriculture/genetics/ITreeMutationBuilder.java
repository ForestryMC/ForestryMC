/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import forestry.api.genetics.IMutationBuilder;

public interface ITreeMutationBuilder extends IMutationBuilder {
    @Override
    ITreeMutation build();
}
