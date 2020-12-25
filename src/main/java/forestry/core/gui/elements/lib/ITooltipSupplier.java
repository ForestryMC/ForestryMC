/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.core.gui.elements.lib;

import forestry.api.core.tooltips.ToolTip;

@FunctionalInterface
public interface ITooltipSupplier {

    default boolean hasTooltip() {
        return true;
    }

    void addTooltip(ToolTip tooltip, IGuiElement element, int mouseX, int mouseY);
}
