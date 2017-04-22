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
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IIndividualTranslator;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import net.minecraft.item.ItemStack;

public abstract class SpeciesRoot implements ISpeciesRoot {
	/* TRANSLATORS */
	private final HashMap<Object, IIndividualTranslator<IIndividual, Object>> translators = new HashMap<>();
	
	/* RESEARCH */
	private final LinkedHashMap<ItemStack, Float> researchCatalysts = new LinkedHashMap<>();

	@Override
	public Map<ItemStack, Float> getResearchCatalysts() {
		return Collections.unmodifiableMap(researchCatalysts);
	}

	@Override
	public void setResearchSuitability(ItemStack itemstack, float suitability) {
		researchCatalysts.put(itemstack, suitability);
	}

	/* TEMPLATES */
	protected final HashMap<String, IAllele[]> speciesTemplates = new HashMap<>();

	@Override
	public Map<String, IAllele[]> getGenomeTemplates() {
		return speciesTemplates;
	}

	@Override
	public void registerTemplate(IAllele[] template) {
		Preconditions.checkNotNull(template, "Tried to register null template");
		Preconditions.checkArgument(template.length > 0, "Tried to register empty template");

		registerTemplate(template[0].getUID(), template);
	}

	@Override
	public IAllele[] getRandomTemplate(Random rand) {
		Collection<IAllele[]> templates = speciesTemplates.values();
		int size = templates.size();
		IAllele[][] templatesArray = templates.toArray(new IAllele[size][]);
		return templatesArray[rand.nextInt(size)];
	}

	@Override
	public IAllele[] getTemplate(String identifier) {
		IAllele[] template = speciesTemplates.get(identifier);
		if (template == null) {
			return null;
		}
		return Arrays.copyOf(template, template.length);
	}

	@Override
	public IAllele[] getTemplate(IAlleleSpecies species) {
		IAllele[] template = getTemplate(species.getUID());
		if (template == null) {
			throw new IllegalStateException("No template found for species " + species.getUID());
		}
		return template;
	}
	
	/* TRANSLATORS */
	@Override
	public <O extends Object, I extends IIndividual> void registerTranslator(Object translatorKey, IIndividualTranslator<I, O> translator) {
		if(!translators.containsKey(translatorKey)){
			translators.put(translatorKey, (IIndividualTranslator<IIndividual, Object>) translator);
		}
	}
	
	@Nullable
	@Override
	public <O extends Object, I extends IIndividual> IIndividualTranslator<I, O> getTranslator(Object translatorKey) {
		return (IIndividualTranslator<I, O>) translators.get(translatorKey);
	}

	/* MUTATIONS */
	@Override
	public List<IMutation> getCombinations(IAllele other) {
		List<IMutation> combinations = new ArrayList<>();
		for (IMutation mutation : getMutations(false)) {
			if (mutation.isPartner(other)) {
				combinations.add(mutation);
			}
		}

		return combinations;
	}

	@Override
	public List<IMutation> getCombinations(IAlleleSpecies parentSpecies0, IAlleleSpecies parentSpecies1, boolean shuffle) {
		List<IMutation> combinations = new ArrayList<>();

		String parentSpecies1UID = parentSpecies1.getUID();
		for (IMutation mutation : getMutations(shuffle)) {
			if (mutation.isPartner(parentSpecies0)) {
				IAllele partner = mutation.getPartner(parentSpecies0);
				if (partner.getUID().equals(parentSpecies1UID)) {
					combinations.add(mutation);
				}
			}
		}

		return combinations;
	}

	@Override
	public Collection<? extends IMutation> getPaths(IAllele result, IChromosomeType chromosomeType) {
		ArrayList<IMutation> paths = new ArrayList<>();
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
