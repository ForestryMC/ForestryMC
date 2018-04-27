/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.gui;

import java.util.Collection;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IElementLayoutHelper {
	/**
	 * @return Only false if the helper has no space to add this element.
	 */
	boolean add(IGuiElement element);

	/**
	 * Removes all layouts and resets all variables of this helper.
	 */
	void clear();

	/**
	 * Adds the layouts to the parent of the {@link IElementGroup}. And calls {@link #clear()}
	 */
	void finish(boolean centerX);

	default void finish() {
		finish(false);
	}

	/**
	 * @return All layouts that were created with the help of this helper since the last {@link #clear()} or {@link #finish()}.
	 */
	Collection<IElementLayout> layouts();

	interface LayoutFactory {
		/**
		 * A factory method to create new layouts if the last layout is full.
		 */
		IElementLayout createLayout(int xOffset, int yOffset);
	}
}
