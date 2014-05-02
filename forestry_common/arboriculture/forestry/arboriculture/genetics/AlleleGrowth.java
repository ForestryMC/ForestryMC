/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.genetics;

import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.core.genetics.Allele;

public class AlleleGrowth extends Allele implements IAlleleGrowth {

	IGrowthProvider provider;

	public AlleleGrowth(String uid, IGrowthProvider provider) {
		this(uid, provider, false);
	}

	public AlleleGrowth(String uid, IGrowthProvider provider, boolean isDominant) {
		super(uid, isDominant);
		this.provider = provider;
	}

	@Override
	public IGrowthProvider getProvider() {
		return provider;
	}

}
