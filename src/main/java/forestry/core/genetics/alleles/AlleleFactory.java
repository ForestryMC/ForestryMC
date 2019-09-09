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
package forestry.core.genetics.alleles;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IChromosomeType;

import forestry.api.genetics.IAlleleFactory;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IFlowerProvider;
import forestry.apiculture.genetics.alleles.AlleleFlowers;

public class AlleleFactory implements IAlleleFactory {

	@Override
	public IAlleleFlowers createFlowers(String modId, String category, String valueName, IFlowerProvider value, boolean isDominant, IChromosomeType... types) {
		IAlleleFlowers alleleFlowers = new AlleleFlowers(modId, category, valueName, value, isDominant);
		GeneticsAPI.apiInstance.getAlleleRegistry().registerAllele(alleleFlowers, types);
		//TODO: Test if this is a good idea to register it at here
		return alleleFlowers;
	}
}
