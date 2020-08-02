package forestry.core.gui.elements;

import javax.annotation.Nullable;

import forestry.core.gui.elements.lib.IGuiElement;

public interface ICroppedGuiElement extends IGuiElement {

    /**
     * Sets the cropped zone of this element and the element the zone is relative to.
     *
     * @param cropElement The element the zone is relative to.
     * @param cropX       The x start coordinate of the zone.
     * @param cropY       The y start coordinate of the zone.
     * @param cropWidth   The width of the zone.
     * @param cropHeight  The height of the zone.
     */
    void setCroppedZone(@Nullable IGuiElement cropElement, int cropX, int cropY, int cropWidth, int cropHeight);

    /**
     * @return The element the cropped zone is relative to.
     */
    @Nullable
    IGuiElement getCropElement();

    /**
     * @return The x start coordinate of the cropped zone.
     */
    int getCropX();

    /**
     * @return The y start coordinate of the cropped zone.
     */
    int getCropY();

    /**
     * @return The width of the cropped zone.
     */
    int getCropWidth();

    /**
     * @return The height of the cropped zone.
     */
    int getCropHeight();

    /**
     * @return True if {@link #setCroppedZone(IGuiElement, int, int, int, int)} was ever called on this element.
     */
    boolean isCropped();
}
