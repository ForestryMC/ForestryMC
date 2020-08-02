package genetics.api.root.components;

public enum DefaultStage implements IStage {
    //Fired at the creation of the root.
    CREATION,
    //Fired after all register events were fired
    REGISTRATION,
    //Fired at the common setup event of forge
    SETUP,
    //Fired at the load complete event of forge
    COMPLETION;

    @Override
    public boolean fireForComponent(IStage componentStage) {
        if (componentStage instanceof DefaultStage) {
            return ((DefaultStage) componentStage).ordinal() >= ordinal();
        }
        return componentStage.fireForComponent(this);
    }
}
