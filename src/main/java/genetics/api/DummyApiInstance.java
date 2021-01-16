package genetics.api;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import net.minecraftforge.fml.ModLoadingContext;

import genetics.api.alleles.IAlleleHelper;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.individual.IChromosomeList;
import genetics.api.individual.IKaryotype;
import genetics.api.root.EmptyRootDefinition;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootHelper;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.IRootComponentRegistry;

public class DummyApiInstance implements IGeneticApiInstance {
	private static final String ERROR_MESSAGE = "The mod %s tried to access the genetics api without checking if the mod is present. Please report this to the mod author.";

	@Override
	public IClassificationRegistry getClassificationRegistry() {
		throw new IllegalStateException(String.format(ERROR_MESSAGE, ModLoadingContext.get().getActiveContainer()));
	}

	@Override
	public IAlleleRegistry getAlleleRegistry() {
		throw new IllegalStateException(String.format(ERROR_MESSAGE, ModLoadingContext.get().getActiveContainer()));
	}

	@Override
	public IAlleleHelper getAlleleHelper() {
		throw new IllegalStateException(String.format(ERROR_MESSAGE, ModLoadingContext.get().getActiveContainer()));
	}

	@Override
	public IGeneticFactory getGeneticFactory() {
		throw new IllegalStateException(String.format(ERROR_MESSAGE, ModLoadingContext.get().getActiveContainer()));
	}

	@Override
	public IGeneticSaveHandler getSaveHandler() {
		throw new IllegalStateException(String.format(ERROR_MESSAGE, ModLoadingContext.get().getActiveContainer()));
	}

	@Override
	public IIndividualRootHelper getRootHelper() {
		throw new IllegalStateException(String.format(ERROR_MESSAGE, ModLoadingContext.get().getActiveContainer()));
	}

	@Override
	public IRootComponentRegistry getComponentRegistry() {
		throw new IllegalStateException(String.format(ERROR_MESSAGE, ModLoadingContext.get().getActiveContainer()));
	}

	@Override
	public <R extends IIndividualRoot> IRootDefinition<R> getRoot(String rootUID) {
		return EmptyRootDefinition.empty();
	}

	@Override
	public IChromosomeList getChromosomeList(String rootUID) {
		throw new IllegalStateException(String.format(ERROR_MESSAGE, ModLoadingContext.get().getActiveContainer()));
	}

	@Override
	public Optional<IKaryotype> getKaryotype(String rootUID) {
		return Optional.empty();
	}

	@Override
	public Map<String, IRootDefinition> getRoots() {
		return Collections.emptyMap();
	}

	@Override
	public boolean isModPresent() {
		return false;
	}
}
