/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nullable;

public interface IIndividualTranslator<I extends IIndividual, O> {
	@Nullable
	I getIndividualFromObject(O objectToTranslator);
}
