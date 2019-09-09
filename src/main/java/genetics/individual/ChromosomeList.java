package genetics.individual;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IChromosomeList;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IChromosomeTypeBuilder;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

public class ChromosomeList implements IChromosomeList {
	private final String rootUID;
	private final IRootDefinition definition;
	private final List<IChromosomeType> types = new LinkedList<>();

	public ChromosomeList(String rootUID) {
		this.rootUID = rootUID;
		this.definition = GeneticsAPI.apiInstance.getRoot(rootUID);
	}

	@Override
	public IChromosomeTypeBuilder builder() {
		return new ChromosomeTypeBuilder(this);
	}

	@Override
	public Collection<IChromosomeType> types() {
		return types;
	}

	@Override
	public IChromosomeType[] typesArray() {
		return types.toArray(new IChromosomeType[0]);
	}

	@Override
	public int size() {
		return types.size();
	}

	@Override
	public String getUID() {
		return rootUID;
	}

	public <T extends IChromosomeType> T add(T type) {
		types.add(type);
		return type;
	}

	@Override
	public Iterator<IChromosomeType> iterator() {
		return types.iterator();
	}

	@Override
	public IIndividualRoot getRoot() {
		return definition.get();
	}

	@Override
	public IRootDefinition getDefinition() {
		return definition;
	}
}
