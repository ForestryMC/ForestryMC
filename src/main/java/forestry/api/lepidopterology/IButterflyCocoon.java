/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import forestry.api.genetics.IHousing;
import forestry.api.lepidopterology.genetics.IButterfly;

public interface IButterflyCocoon extends IHousing {

	IButterfly getCaterpillar();

	void setCaterpillar(IButterfly butterfly);

	boolean isSolid();

}
