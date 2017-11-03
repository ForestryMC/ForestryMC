/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.util.Collection;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiElementLayout extends IGuiElement {
	/**
	 * Adds a element to this layout.
	 */
	IGuiElementLayout addElement(IGuiElement element);

	/**
	 * Removes a element from this layout.
	 */
	IGuiElementLayout removeElement(IGuiElement element);

	default IGuiElementLayout addElements(IGuiElement... elements){
		for(IGuiElement element : elements){
			addElement(element);
		}
		return this;
	}

	default IGuiElementLayout removeElements(IGuiElement... elements){
		for(IGuiElement element : elements){
			removeElement(element);
		}
		return this;
	}

	default IGuiElementLayout addElements(Collection<IGuiElement> elements){
		elements.forEach(element -> addElement(element));
		return this;
	}

	default IGuiElementLayout removeElements(Collection<IGuiElement> elements){
		elements.forEach(element -> removeElement(element));
		return this;
	}

	List<IGuiElement> getElements();

	IGuiElementLayout setDistance(int distance);

	void addTooltip(String line);

	/**
	 * @return The tooltip that is displayed if the mouse is over this element and no element of this layout is under it.
	 */
	List<String> getTooltip();

	/**
	 * @return The distance between the different elements of this layout.
	 */
	int getDistance();

	int getSize();
}
