/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IHousing;

public interface IButterflyNursery extends IHousing {
	
	IButterfly getCaterpillar();
	
	ITree getNanny();
	
	void setCaterpillar(IButterfly butterfly);
	
	boolean canNurse(IButterfly butterfly);
	
}
