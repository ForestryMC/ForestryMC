package forestry.core.gui.elements.lib.events;

public interface IKeyListener {
    default boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    default boolean onCharTyped(int keyCode, int modifiers) {
        return false;
    }
}
