package genetics.root;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.ITemplateContainer;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;

public class TemplateContainer<I extends IIndividual> implements ITemplateContainer<I> {
	private final IIndividualRoot<I> root;
	private final HashMap<String, IAllele[]> templates = new HashMap<>();

	TemplateContainer(IIndividualRoot<I> root) {
		this.root = root;
	}

	@Override
	public IIndividualRoot<I> getRoot() {
		return root;
	}

	@Override
	public ITemplateContainer registerTemplate(IAllele[] template) {
		Preconditions.checkNotNull(template, "Tried to register null template");
		Preconditions.checkArgument(template.length > 0, "Tried to register empty template");

		IChromosomeType templateType = getKaryotype().getSpeciesType();
		IAllele templateAllele = template[templateType.getIndex()];
		String identifier = templateAllele.getRegistryName().toString();
		templates.put(identifier, template);

		return this;
	}

	@Override
	public ITemplateContainer registerTemplate(IAlleleTemplate template) {
		return registerTemplate(template.alleles());
	}

	@Override
	public Map<String, IAllele[]> getGenomeTemplates() {
		return templates;
	}

	@Override
	public Collection<IAllele[]> getTemplates() {
		return Collections.unmodifiableCollection(templates.values());
	}

	@Override
	public IAllele[] getRandomTemplate(Random rand) {
		Collection<IAllele[]> alleles = this.templates.values();
		int size = alleles.size();
		IAllele[][] templatesArray = alleles.toArray(new IAllele[size][]);
		return templatesArray[rand.nextInt(size)];
	}

	@Override
	public IAllele[] getTemplate(String identifier) {
		IAllele[] alleles = templates.get(identifier);
		if (alleles == null) {
			return new IAllele[0];
		}
		return Arrays.copyOf(alleles, alleles.length);
	}

	@Override
	public ComponentKey<ITemplateContainer> getKey() {
		return ComponentKeys.TEMPLATES;
	}
}
