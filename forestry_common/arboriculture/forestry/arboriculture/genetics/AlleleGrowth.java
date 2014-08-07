/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
