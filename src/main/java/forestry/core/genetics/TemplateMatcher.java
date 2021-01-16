package forestry.core.genetics;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IGenome;
import genetics.api.individual.IGenomeMatcher;
import genetics.api.root.IIndividualRoot;

public class TemplateMatcher implements IGenomeMatcher {
	private final IGenome genome;
	@Nullable
	private Boolean matches = null;

	public TemplateMatcher(IGenome genome) {
		this.genome = genome;
	}

	@Override
	public IGenome getFirst() {
		return genome;
	}

	@Override
	public IGenome getSecond() {
		return genome.getKaryotype().getDefaultGenome();
	}

	@Override
	public IIndividualRoot getRoot() {
		return GeneticsAPI.apiInstance.getRoot(genome.getKaryotype().getUID()).get();
	}

	@Override
	public boolean matches() {
		if (matches == null) {
			matches = calculateMatches();
		}
		return matches;
	}

	private boolean calculateMatches() {
		IAlleleSpecies primary = genome.getPrimary();
		IAllele[] template = getRoot().getTemplates().getTemplate(primary.getRegistryName().toString());
		IChromosome[] chromosomes = genome.getChromosomes();
		for (int i = 0; i < chromosomes.length; i++) {
			IChromosome chromosome = chromosomes[i];
			ResourceLocation templateUid = template[i].getRegistryName();
			IAllele activeAllele = chromosome.getActiveAllele();
			if (!activeAllele.getRegistryName().equals(templateUid)) {
				return false;
			}
			IAllele inactiveAllele = chromosome.getInactiveAllele();
			if (!inactiveAllele.getRegistryName().equals(templateUid)) {
				return false;
			}
		}
		return true;
	}
}
