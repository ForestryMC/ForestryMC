package forestry.core.gui.elements.lib.events;

public interface IMouseListener {
    default boolean onButton(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    default boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    default boolean onMouseReleased(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    default boolean onWheel(double mouseX, double mouseY, double dWheel) {
        return false;
    }

    default void onMouseMove(double mouseX, double mouseY) {
    }

    default void onDrag(double mouseX, double mouseY) {

    }
}
