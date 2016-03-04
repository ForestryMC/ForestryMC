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

import java.util.Arrays;
import java.util.List;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.genetics.AlleleManager;
import forestry.arboriculture.genetics.GrowthProvider;
import forestry.arboriculture.genetics.GrowthProviderTropical;
import forestry.core.genetics.alleles.AlleleCategorized;

public class AlleleGrowth extends AlleleCategorized implements IAlleleGrowth {

	public static IAlleleGrowth growthLightLevel;
	public static IAlleleGrowth growthAcacia;
	public static IAlleleGrowth growthTropical;

	public static void createAlleles() {
		List<IAlleleGrowth> growthAlleles = Arrays.asList(
				growthLightLevel = new AlleleGrowth("lightlevel", new GrowthProvider()),
				growthAcacia = new AlleleGrowth("acacia", new GrowthProvider()),
				growthTropical = new AlleleGrowth("tropical", new GrowthProviderTropical())
		);

		for (IAlleleGrowth alleleGrowth : growthAlleles) {
			AlleleManager.alleleRegistry.registerAllele(alleleGrowth, EnumTreeChromosome.GROWTH);
		}
	}

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
