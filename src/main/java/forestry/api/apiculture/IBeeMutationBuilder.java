/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.IMutationBuilder;

public interface IBeeMutationBuilder extends IMutationBuilder {
	@Override
	IBeeMutation build();
}
