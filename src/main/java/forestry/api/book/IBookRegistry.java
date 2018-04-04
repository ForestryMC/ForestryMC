/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.book;

public interface IBookRegistry {

	/**
	 * Adds a content type.
	 *
	 * @param name The name of the content type.
	 * @param contentClass The class for the deserialization of the content.
	 */
	void registerContentType(String name, Class<? extends BookContent> contentClass);

	IForesterBook loadBook();

	void invalidateBook();
}
