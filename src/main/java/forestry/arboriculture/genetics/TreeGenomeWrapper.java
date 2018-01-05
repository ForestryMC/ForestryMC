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

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITreeGenomeWrapper;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IGenome;
import forestry.core.genetics.GenomeWrapper;

public class TreeGenomeWrapper extends GenomeWrapper<EnumTreeChromosome> implements ITreeGenomeWrapper {
	@Nullable
	private Boolean matchesTemplateCached;

	public TreeGenomeWrapper(IGenome genome) {
		super(genome);
	}

	@Override
	public IAlleleTreeSpecies getPrimary() {
		return getActiveAllele(EnumTreeChromosome.SPECIES, IAlleleTreeSpecies.class);
	}

	@Override
	public IAlleleTreeSpecies getSecondary() {
		return getInactiveAllele(EnumTreeChromosome.SPECIES, IAlleleTreeSpecies.class);
	}


	@Override
	public IFruitProvider getFruitProvider() {
		return getActiveAllele(EnumTreeChromosome.FRUITS, IAlleleFruit.class).getProvider();
	}

	@Override
	public float getHeight() {
		return getActiveAllele(EnumTreeChromosome.HEIGHT, IAlleleFloat.class).getValue();
	}

	@Override
	public float getFertility() {
		return getActiveAllele(EnumTreeChromosome.FERTILITY, IAlleleFloat.class).getValue();
	}

	@Override
	public float getYield() {
		return getActiveAllele(EnumTreeChromosome.YIELD, IAlleleFloat.class).getValue();
	}

	@Override
	public float getSappiness() {
		return getActiveAllele(EnumTreeChromosome.SAPPINESS, IAlleleFloat.class).getValue();
	}

	@Override
	public int getMaturationTime() {
		return getActiveAllele(EnumTreeChromosome.MATURATION, IAlleleInteger.class).getValue();
	}

	@Override
	public int getGirth() {
		return getActiveAllele(EnumTreeChromosome.GIRTH, IAlleleInteger.class).getValue();
	}

	@Override
	public IAlleleLeafEffect getEffect() {
		return getActiveAllele(EnumTreeChromosome.EFFECT, IAlleleLeafEffect.class);
	}
	
	@Override
	public ItemStack getDecorativeLeaves() {
		return getPrimary().getLeafProvider().getDecorativeLeaves();
	}

	@Override
	public boolean matchesTemplateGenome() {
		if (matchesTemplateCached == null) {
			matchesTemplateCached = calculateMatchesTemplateGenome();
		}
		return matchesTemplateCached;
	}

	private boolean calculateMatchesTemplateGenome() {
		IAlleleTreeSpecies primary = getPrimary();
		IAllele[] template = genome.getSpeciesRoot().getTemplate(primary);
		IChromosome[] chromosomes = genome.getChromosomes();
		for (int i = 0; i < chromosomes.length; i++) {
			IChromosome chromosome = chromosomes[i];
			String templateUid = template[i].getUID();
			IAllele primaryAllele = chromosome.getActiveAllele();
			if (!primaryAllele.getUID().equals(templateUid)) {
				return false;
			}
			IAllele secondaryAllele = chromosome.getInactiveAllele();
			if (!secondaryAllele.getUID().equals(templateUid)) {
				return false;
			}
		}
		return true;
	}
}
