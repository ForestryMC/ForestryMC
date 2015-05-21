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
package forestry.core.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;

public abstract class SpeciesRoot implements ISpeciesRoot {
	
	/* RESEARCH */
	private final LinkedHashMap<ItemStack, Float> researchCatalysts = new LinkedHashMap<ItemStack, Float>();
	
	@Override
	public Map<ItemStack, Float> getResearchCatalysts() {
		return Collections.unmodifiableMap(researchCatalysts);
	}

	@Override
	public void setResearchSuitability(ItemStack itemstack, float suitability) {
		researchCatalysts.put(itemstack, suitability);
	}

	/* TEMPLATES */
	public final HashMap<String, IAllele[]> speciesTemplates = new HashMap<String, IAllele[]>();

	@Override
	public Map<String, IAllele[]> getGenomeTemplates() {
		return speciesTemplates;
	}
	
	@Override
	public void registerTemplate(IAllele[] template) {
		registerTemplate(template[0].getUID(), template);
	}

	@Override
	public IAllele[] getRandomTemplate(Random rand) {
		return speciesTemplates.values().toArray(new IAllele[0][])[rand.nextInt(speciesTemplates.values().size())];
	}

	@Override
	public IAllele[] getTemplate(String identifier) {
		IAllele[] template = speciesTemplates.get(identifier);
		if (template == null) {
			return null;
		}
		return Arrays.copyOf(template, template.length);
	}

	/* MUTATIONS */
	@Override
	public Collection<? extends IMutation> getCombinations(IAllele other) {
		ArrayList<IMutation> combinations = new ArrayList<IMutation>();
		for (IMutation mutation : getMutations(false)) {
			if (mutation.isPartner(other)) {
				combinations.add(mutation);
			}
		}

		return combinations;
	}

	@Override
	public Collection<? extends IMutation> getPaths(IAllele result, IChromosomeType chromosomeType) {
		ArrayList<IMutation> paths = new ArrayList<IMutation>();
		for (IMutation mutation : getMutations(false)) {
			if (mutation.getTemplate()[chromosomeType.ordinal()] == result) {
				paths.add(mutation);
			}
		}

		return paths;
	}

	/* GENOME CONVERSIONS */
	@Override
	public IChromosome[] templateAsChromosomes(IAllele[] template) {
		Chromosome[] chromosomes = new Chromosome[template.length];
		for (int i = 0; i < template.length; i++) {
			if (template[i] != null) {
				chromosomes[i] = new Chromosome(template[i]);
			}
		}

		return chromosomes;
	}

	@Override
	public IChromosome[] templateAsChromosomes(IAllele[] templateActive, IAllele[] templateInactive) {
		Chromosome[] chromosomes = new Chromosome[templateActive.length];
		for (int i = 0; i < templateActive.length; i++) {
			if (templateActive[i] != null) {
				chromosomes[i] = new Chromosome(templateActive[i], templateInactive[i]);
			}
		}

		return chromosomes;
	}

}
