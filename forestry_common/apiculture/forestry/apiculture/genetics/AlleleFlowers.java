/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IFlowerProvider;
import forestry.core.genetics.Allele;

public class AlleleFlowers extends Allele implements IAlleleFlowers {

	IFlowerProvider provider;

	public AlleleFlowers(String uid, IFlowerProvider provider) {
		this(uid, provider, false);
	}

	public AlleleFlowers(String uid, IFlowerProvider provider, boolean isDominant) {
		super(uid, isDominant);
		this.provider = provider;
	}

	@Override
	public IFlowerProvider getProvider() {
		return provider;
	}
	
	@Override
	public String getName() {
		return getProvider().getDescription();
	}

}
