package genetics.root;

import java.util.Map;
import java.util.Optional;

import net.minecraft.item.ItemStack;

import genetics.api.GeneticHelper;
import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganism;
import genetics.api.root.EmptyRootDefinition;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootHelper;
import genetics.api.root.IRootDefinition;

public enum IndividualRootHelper implements IIndividualRootHelper {
	INSTANCE;

	@Override
	@SuppressWarnings("unchecked")
	public <R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(ItemStack stack) {
		return (IRootDefinition<R>) getSpeciesRoot(stack, IIndividualRoot.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(ItemStack stack, Class<? extends R> rootClass) {
		if (stack.isEmpty()) {
			return EmptyRootDefinition.empty();
		}

		Map<String, IRootDefinition> definitions = GeneticsAPI.apiInstance.getRoots();
		for (IRootDefinition definition : definitions.values()) {
			if (!definition.isRootPresent()) {
				continue;
			}
			IIndividualRoot root = definition.get();
			if (!root.isMember(stack) || rootClass.isInstance(root)) {
				continue;
			}
			return (IRootDefinition<R>) definition;
		}
		return EmptyRootDefinition.empty();
	}

	@Override
	public IRootDefinition getSpeciesRoot(Class<? extends IIndividual> individualClass) {
		return getSpeciesRoot(individualClass, IIndividualRoot.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(Class<? extends IIndividual> individualClass, Class<? extends R> rootClass) {
		Map<String, IRootDefinition> definitions = GeneticsAPI.apiInstance.getRoots();
		for (IRootDefinition rootDefinition : definitions.values()) {
			if (!rootDefinition.isRootPresent()) {
				continue;
			}
			IIndividualRoot<?> root = rootDefinition.get();
			if (!root.getMemberClass().isAssignableFrom(individualClass) || rootClass.isInstance(root)) {
				continue;
			}
			return (IRootDefinition<R>) rootDefinition;
		}
		return EmptyRootDefinition.empty();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(IIndividual individual) {
		return (IRootDefinition<R>) individual.getRoot().getDefinition();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(IIndividual individual, Class<? extends R> rootClass) {
		IIndividualRoot root = individual.getRoot();
		return rootClass.isInstance(root) ? (IRootDefinition<R>) root.getDefinition() : EmptyRootDefinition.empty();
	}

	@Override
	public boolean isIndividual(ItemStack stack) {
		return getSpeciesRoot(stack).isRootPresent();
	}

	@Override
	public Optional<IIndividual> getIndividual(ItemStack stack) {
		IOrganism<IIndividual> organism = GeneticHelper.getOrganism(stack);
		return organism.getIndividual();
	}

	@Override
	public IAlleleTemplateBuilder createTemplate(String uid) {
		GeneticsAPI.apiInstance.getRoot(uid);
		return null;
	}
}
