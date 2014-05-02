/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.genetics;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IFruitProvider;
import forestry.core.genetics.Allele;

public class AlleleFruit extends Allele implements IAlleleFruit {

	private IFruitProvider provider;

	public AlleleFruit(String uid, IFruitProvider provider) {
		this(uid, provider, false);
	}

	public AlleleFruit(String uid, IFruitProvider provider, boolean isDominant) {
		super(uid, isDominant);
		this.provider = provider;
	}

	@Override
	public IFruitProvider getProvider() {
		return this.provider;
	}
	
	@Override
	public String getName() {
		return getProvider().getDescription();
	}

}
