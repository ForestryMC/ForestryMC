/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.book;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBookCategory {
	/**
	 * @return the {@link ItemStack} that represents this category at the front page.
	 */
	ItemStack getStack();

	IBookCategory setStack(ItemStack stack);

	/**
	 * Adds an custom entry to the category.
	 */
	IBookCategory addEntry(IBookEntry entry);

	/**
	 * Adds an entry with the given name and the given icon to the category.
	 *
	 * @param name  A unique name for this entry.
	 * @param stack The stack that will be displayed next to the entry title at the category page.
	 */
	IBookCategory addEntry(String name, ItemStack stack);

	/**
	 * Creates a entry builder with the given name.
	 * <p>
	 * At the end of your creation you have to call {@link IBookEntryBuilder#addToCategory()} to add the entry to this
	 * category.
	 *
	 * @param name A unique name for this entry.
	 */
	IBookEntryBuilder createEntry(String name);

	/**
	 * Creates a entry builder with the given name and the given icon.
	 * <p>
	 * At the end of your creation you have to call {@link IBookEntryBuilder#addToCategory()} to add the entry to this
	 * category.
	 *
	 * @param name  A unique name for this entry.
	 * @param stack The stack that will be displayed next to the entry title at the category page.
	 */
	IBookEntryBuilder createEntry(String name, ItemStack stack);

	/**
	 * @return a collection that contains all entries of this category.
	 */
	Collection<IBookEntry> getEntries();

	/**
	 * @return the entry with the given unique name.
	 */
	@Nullable
	IBookEntry getEntry(String name);

	/**
	 * @return the localized name of this category.
	 */
	String getLocalizedName();

	/**
	 * Currently unused
	 */
	String getTooltip();

	/**
	 * @return the unique name of this category
	 */
	String getName();
}
