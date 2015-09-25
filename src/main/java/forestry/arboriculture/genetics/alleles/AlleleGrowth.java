/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.genetics.alleles;

import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.core.genetics.alleles.AlleleCategorized;

public class AlleleGrowth extends AlleleCategorized implements IAlleleGrowth {

	private final IGrowthProvider provider;

	public AlleleGrowth(String name, IGrowthProvider provider) {
		this(name, provider, false);
	}

	public AlleleGrowth(String name, IGrowthProvider provider, boolean isDominant) {
		super("forestry", "growth", name, isDominant);
		this.provider = provider;
	}

	@Override
	public IGrowthProvider getProvider() {
		return provider;
	}

}
