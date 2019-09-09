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

import genetics.api.classification.IClassification;

import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.ILeafProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.genetics.IAlleleTreeSpeciesBuilder;
import forestry.api.arboriculture.genetics.ITreeFactory;
import forestry.arboriculture.genetics.alleles.AlleleTreeSpecies;
import forestry.arboriculture.models.SpriteProviderLeaves;

public class TreeFactory implements ITreeFactory {

	@Override
	public IAlleleTreeSpeciesBuilder createSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean dominant, IClassification branch, String binomial, String modID, ILeafSpriteProvider leafSpriteProvider, IGermlingModelProvider germlingModelProvider, ITreeGenerator generator, ILeafProvider leafProvider) {
		return new AlleleTreeSpecies(uid, unlocalizedName, authority, unlocalizedDescription, dominant, branch, binomial, modID, leafSpriteProvider, germlingModelProvider, generator, leafProvider);
	}

	@Override
	public ILeafSpriteProvider getLeafIconProvider(EnumLeafType enumLeafType, Color color, Color colorPollinated) {
		return new SpriteProviderLeaves(enumLeafType, color, colorPollinated);
	}
}
