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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;

public abstract class SpeciesRoot<C extends Enum<C> & IChromosomeType<C>> implements ISpeciesRoot<C> {
	
	/* RESEARCH */
	private final LinkedHashMap<ItemStack, Float> researchCatalysts = new LinkedHashMap<>();
	private final ImmutableMap<Byte, C> chromosomeTypeMap;

	public SpeciesRoot(@Nonnull Class<C> chromosomeTypeClass) {
		ImmutableMap.Builder<Byte, C> chromosomeMapBuilder = ImmutableMap.builder();
		for (C chromosomeType : chromosomeTypeClass.getEnumConstants()) {
			chromosomeMapBuilder.put(chromosomeType.getUid(), chromosomeType);
		}
		chromosomeTypeMap = chromosomeMapBuilder.build();
	}

	@Override
	public C getChromosomeTypeForUid(byte uid) {
		return chromosomeTypeMap.get(uid);
	}
	
	@Override
	public Map<ItemStack, Float> getResearchCatalysts() {
		return Collections.unmodifiableMap(researchCatalysts);
	}

	@Override
	public void setResearchSuitability(ItemStack itemstack, float suitability) {
		researchCatalysts.put(itemstack, suitability);
	}

	/* TEMPLATES */
	protected final Map<String, ImmutableMap<C, IAllele>> speciesTemplates = new HashMap<>();

	@Override
	public void registerTemplate(String identifier, ImmutableMap<C, IAllele> template) {
		speciesTemplates.put(identifier, template);
	}

	@Override
	public Map<String, ImmutableMap<C, IAllele>> getGenomeTemplates() {
		return speciesTemplates;
	}
	
	@Override
	public void registerTemplate(ImmutableMap<C, IAllele> template) {
		if (template == null) {
			throw new IllegalArgumentException("Tried to register null template");
		}
		if (template.size() == 0) {
			throw new IllegalArgumentException("Tried to register empty template");
		}
		registerTemplate(template.get(getKaryotypeKey()).getUID(), template);
	}

	@Override
	public ImmutableMap<C, IAllele> getRandomTemplate(Random rand) {
		Collection<ImmutableMap<C, IAllele>> templates = speciesTemplates.values();
		int size = templates.size();
		ImmutableList<ImmutableMap<C, IAllele>> templatesList = ImmutableList.copyOf(speciesTemplates.values());
		return templatesList.get(rand.nextInt(size));
	}

	@Nullable
	@Override
	public ImmutableMap<C, IAllele> getTemplate(String identifier) {
		return speciesTemplates.get(identifier);
	}

	/* MUTATIONS */
	@Override
	public List<IMutation<C>> getCombinations(IAllele other) {
		ArrayList<IMutation<C>> combinations = new ArrayList<>();
		for (IMutation<C> mutation : getMutations(false)) {
			if (mutation.isPartner(other)) {
				combinations.add(mutation);
			}
		}

		return combinations;
	}

	@Override
	public List<IMutation<C>> getCombinations(IAlleleSpecies<C> parentSpecies0, IAlleleSpecies<C> parentSpecies1, boolean shuffle) {
		List<IMutation<C>> combinations = new ArrayList<>();

		String parentSpecies1UID = parentSpecies1.getUID();
		for (IMutation<C> mutation : getMutations(shuffle)) {
			if (mutation.isPartner(parentSpecies0)) {
				IAllele partner = mutation.getPartner(parentSpecies0);
				if (partner != null && partner.getUID().equals(parentSpecies1UID)) {
					combinations.add(mutation);
				}
			}
		}

		return combinations;
	}

	@Override
	public Collection<? extends IMutation<C>> getPaths(IAllele result, C chromosomeType) {
		ArrayList<IMutation<C>> paths = new ArrayList<>();
		for (IMutation<C> mutation : getMutations(false)) {
			if (mutation.getResultTemplate().get(chromosomeType) == result) {
				paths.add(mutation);
			}
		}

		return paths;
	}

	/* GENOME CONVERSIONS */
	@Override
	public ImmutableMap<C, IChromosome> templateAsChromosomes(ImmutableMap<C, IAllele> template) {
		ImmutableMap.Builder<C, IChromosome> templateBuilder = ImmutableMap.builder();
		for (Map.Entry<C, IAllele> entry : template.entrySet()) {
			C chromosomeType = entry.getKey();
			IAllele allele = entry.getValue();
			IChromosome chromosome = new Chromosome(allele);
			templateBuilder.put(chromosomeType, chromosome);
		}
		return templateBuilder.build();
	}

	@Override
	public ImmutableMap<C, IChromosome> templateAsChromosomes(ImmutableMap<C, IAllele> templateActive, ImmutableMap<C, IAllele> templateInactive) {
		ImmutableMap.Builder<C, IChromosome> templateBuilder = ImmutableMap.builder();
		for (Map.Entry<C, IAllele> activeEntry : templateActive.entrySet()) {
			C chromosomeType = activeEntry.getKey();
			IAllele activeAllele = activeEntry.getValue();
			IAllele inactiveAllele = templateInactive.get(chromosomeType);
			IChromosome chromosome = new Chromosome(activeAllele, inactiveAllele);
			templateBuilder.put(chromosomeType, chromosome);
		}
		return templateBuilder.build();
	}
}
