package forestry.api.core;

/**
 * An helper interface for every type that does something at the start of the setup or at the end.
 */
public interface ISetupListener {
    /**
     * Called at the start of the forestry setup after the registry events.
     */
    default void onStartSetup() {
    }

    /**
     * Called at the end of the setup after forestry has done all the setup stuff.
     */
    default void onFinishSetup() {
    }
}
