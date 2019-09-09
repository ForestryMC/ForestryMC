package genetics.root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;

public class MutationContainer<I extends IIndividual, M extends IMutation> implements IMutationContainer<I, M> {

	private final List<M> mutations = new LinkedList<>();
	private final IIndividualRoot<I> root;

	public MutationContainer(IIndividualRoot<I> root) {
		this.root = root;
	}

	@Override
	public IIndividualRoot<I> getRoot() {
		return root;
	}

	@Override
	public boolean registerMutation(M mutation) {
		IChromosomeType speciesType = root.getKaryotype().getSpeciesType();
		IAlleleRegistry alleleRegistry = GeneticsAPI.apiInstance.getAlleleRegistry();
		IAllele firstParent = mutation.getFirstParent();
		IAllele secondParent = mutation.getSecondParent();
		IAllele resultSpecies = mutation.getTemplate()[speciesType.getIndex()];
		if (alleleRegistry.isBlacklisted(resultSpecies)
			|| alleleRegistry.isBlacklisted(firstParent)
			|| alleleRegistry.isBlacklisted(secondParent)) {
			return false;
		}
		mutations.add(mutation);
		return true;
	}

	@Override
	public List<M> getMutations(boolean shuffle) {
		if (shuffle) {
			Collections.shuffle(mutations);
		}
		return mutations;
	}

	@Override
	public List<M> getCombinations(IAllele other) {
		List<M> combinations = new ArrayList<>();
		for (M mutation : getMutations(false)) {
			if (mutation.isPartner(other)) {
				combinations.add(mutation);
			}
		}

		return combinations;
	}

	@Override
	public List<M> getResultantMutations(IAllele other) {
		IKaryotype karyotype = root.getKaryotype();
		List<M> resultants = new ArrayList<>();
		int speciesIndex = karyotype.getSpeciesType().getIndex();
		for (M mutation : getMutations(false)) {
			IAllele[] template = mutation.getTemplate();
			if (template.length <= speciesIndex) {
				continue;
			}
			IAllele speciesAllele = template[speciesIndex];
			if (speciesAllele == other) {
				resultants.add(mutation);
			}
		}

		return resultants;
	}

	@Override
	public List<M> getCombinations(IAlleleSpecies parentFirst, IAlleleSpecies parentSecond, boolean shuffle) {
		List<M> combinations = new ArrayList<>();

		ResourceLocation parentSpecies = parentSecond.getRegistryName();
		for (M mutation : getMutations(shuffle)) {
			if (mutation.isPartner(parentFirst)) {
				IAllele partner = mutation.getPartner(parentFirst);
				if (partner.getRegistryName().equals(parentSpecies)) {
					combinations.add(mutation);
				}
			}
		}

		return combinations;
	}

	@Override
	public Collection<M> getPaths(IAllele result, IChromosomeType chromosomeType) {
		ArrayList<M> paths = new ArrayList<>();
		for (M mutation : getMutations(false)) {
			IAllele[] template = mutation.getTemplate();
			IAllele mutationResult = template[chromosomeType.getIndex()];
			if (mutationResult == result) {
				paths.add(mutation);
			}
		}

		return paths;
	}

	@Override
	public ComponentKey<IMutationContainer> getKey() {
		return ComponentKeys.MUTATIONS;
	}
}
