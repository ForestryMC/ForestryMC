package genetics.api.root.components;

public enum DefaultStage implements IStage {
	CREATION,
	SETUP,
	COMPLETION;

	@Override
	public boolean fireForComponent(IStage componentStage) {
		if (componentStage instanceof DefaultStage) {
			return ((DefaultStage) componentStage).ordinal() >= ordinal();
		}
		return componentStage.fireForComponent(this);
	}
}
