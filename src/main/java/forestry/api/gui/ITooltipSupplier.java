/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.gui;

import java.util.Collection;

import net.minecraft.util.text.ITextComponent;

@FunctionalInterface
public interface ITooltipSupplier {

	default boolean hasTooltip() {
		return true;
	}

	void addTooltip(Collection<ITextComponent> tooltip, IGuiElement element, int mouseX, int mouseY);
}
