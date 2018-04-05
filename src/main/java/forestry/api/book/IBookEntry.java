/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.book;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A entry in the {@link IForesterBook}.
 * <p>
 * Forestry crates them with the help of json file, but you can add you own custom ones with
 * {@link IBookCategory#addEntry(IBookEntry)}.
 */
@SideOnly(Side.CLIENT)
public interface IBookEntry {

	/**
	 * @return The stack that is displayed before the entry name.
	 */
	ItemStack getStack();

	/**
	 * @return A unique string identifier for this entry.
	 */
	String getName();

	/**
	 * @return the object that creates the book pages at the moment the bock opens.
	 */
	IBookPageFactory getPageFactory();

	/**
	 * All sub entries of this entry are reachable over the buttons on the left side of the book.
	 */
	IBookEntry[] getSubEntries();

	/**
	 * The parent of the entry if this entry is a sub entry. Null if the entry is no sub entry.
	 */
	@Nullable
	IBookEntry getParent();

	/**
	 * @return the localized title of this entry.
	 */
	String getTitle();

	/**
	 * @return the content that was deserialized from the json file of this entry.
	 */
	BookContent[][] getContent();

}
