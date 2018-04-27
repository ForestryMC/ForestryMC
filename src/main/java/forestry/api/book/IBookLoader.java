/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.book;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBookLoader {

	/**
	 * Adds a content type.
	 *
	 * @param name         The name of the content type.
	 * @param contentClass The class for the deserialization of the content.
	 */
	void registerContentType(String name, Class<? extends BookContent> contentClass);

	void registerPageFactory(String name, IBookPageFactory factory);

	IBookPageFactory getPageFactory(String name);

	/**
	 * Loads the book if it was not loaded already or if it was invalidated. Otherwise it returns the last loaded
	 * instance.
	 *
	 * @return The current instance of the forestry guide book.
	 */
	IForesterBook loadBook();

	/**
	 * Invalidates the current instance of the book.
	 * The next time {@link #loadBook()} gets called a new instance of the book will be loaded.
	 */
	void invalidateBook();
}
