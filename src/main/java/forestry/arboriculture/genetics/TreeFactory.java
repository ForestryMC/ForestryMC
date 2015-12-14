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
package forestry.arboriculture.genetics;

import java.awt.Color;

import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpeciesCustom;
import forestry.api.arboriculture.IGermlingIconProvider;
import forestry.api.arboriculture.ILeafIconProvider;
import forestry.api.arboriculture.ITreeFactory;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.arboriculture.genetics.alleles.AlleleTreeSpecies;
import forestry.arboriculture.render.IconProviderLeaves;

public class TreeFactory implements ITreeFactory {
	@Override
	public IAlleleTreeSpeciesCustom createSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean dominant, IClassification branch, String binomial, ILeafIconProvider leafIconProvider, IGermlingIconProvider germlingIconProvider, ITreeGenerator generator) {
		IAlleleTreeSpeciesCustom treeSpecies = new AlleleTreeSpecies(uid, unlocalizedName, authority, unlocalizedDescription, dominant, branch, binomial, leafIconProvider, germlingIconProvider, generator);
		AlleleManager.alleleRegistry.registerAllele(treeSpecies, EnumTreeChromosome.SPECIES);
		return treeSpecies;
	}

	@Override
	public ILeafIconProvider getLeafIconProvider(EnumLeafType enumLeafType, Color color, Color colorPollinated) {
		return new IconProviderLeaves(enumLeafType, color, colorPollinated);
	}
}
