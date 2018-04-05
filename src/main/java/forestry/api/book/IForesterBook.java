/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.book;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * IForesterBook is the interface that the forestry guide book class implements.
 * <p>
 * You can get the current instance of the guide book from {@link IBookLoader#loadBook()}.
 */
@SideOnly(Side.CLIENT)
public interface IForesterBook {

	/**
	 * Creates a category, add it to this book and returns it.
	 *
	 * @param name A unique identifier for the category.
	 * @return The created category.
	 */
	IBookCategory addCategory(String name);

	/**
	 * @return The category with the given unique name.
	 */
	@Nullable
	IBookCategory getCategory(String name);

	/**
	 * @return All categories of this book.
	 */
	Collection<IBookCategory> getCategories();

	/**
	 * @return A collection that contains all unique names of the categories
	 */
	Collection<String> getCategoryNames();

	/**
	 * @return A collection that contains all entries of the category with the given name.
	 */
	Collection<IBookEntry> getEntries(String category);

	/**
	 * @return the entry with the given unique name.
	 */
	@Nullable
	IBookEntry getEntry(String name);
}
