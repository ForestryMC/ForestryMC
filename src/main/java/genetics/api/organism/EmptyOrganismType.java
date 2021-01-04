package genetics.api.organism;

public enum EmptyOrganismType implements IOrganismType {
	INSTANCE;

	@Override
	public String getName() {
		return "empty";
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

}
