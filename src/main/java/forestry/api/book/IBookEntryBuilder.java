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

@SideOnly(Side.CLIENT)
public interface IBookEntryBuilder {

	/**
	 * Sets the content of the entry.
	 */
	IBookEntryBuilder setContent(BookContent[][] content);

	/**
	 * Sets the localized name of entry.
	 */
	IBookEntryBuilder setTitle(String title);

	IBookEntryBuilder setStack(ItemStack stack);

	IBookEntryBuilder setLoader(IBookPageFactory loader);

	/**
	 * Creates a sub entry.
	 * <p>
	 * Important: You not have to call {@link #addToCategory()} at the end of the creation of the sub entry
	 *
	 * @param name The unique name of the sub entry.
	 * @return The entry builder of the sub entry.
	 */
	default IBookEntryBuilder createSubEntry(String name) {
		return createSubEntry(name, ItemStack.EMPTY);
	}

	/**
	 * Creates a sub entry.
	 * <p>
	 * Important: You not have to call {@link #addToCategory()} at the end of the creation of the sub entry
	 *
	 * @param name  The unique name of the sub entry.
	 * @param stack The stack that represents the sub entry.
	 * @return The entry builder of the sub entry.
	 */
	IBookEntryBuilder createSubEntry(String name, ItemStack stack);

	IBookEntry build(@Nullable IBookEntry parent);

	/**
	 * Builds a entry with the current information.
	 */
	IBookEntry build();

	/**
	 * Builds the entry and adds it to the category.
	 */
	IBookCategory addToCategory();
}
