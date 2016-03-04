/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;

public interface IBackpackInterface {
	/**
	 * Get a backpack with a given uid so you can add items to it or get information about it.
	 * returns null if there is no backpack for the given uid.
	 */
	@Nullable
	IBackpackDefinition getBackpack(@Nonnull String uid);

	/**
	 * Register a backpack with a given uid
	 */
	void registerBackpack(@Nonnull String uid, @Nonnull IBackpackDefinition definition);

	/**
	 * Adds a backpack with the given definition and type, returning the item.
	 *
	 * @param definition
	 *            Definition of backpack behaviour.
	 * @param type
	 *            Type of backpack.
	 * @return Created backpack item.
	 */
	@Nonnull
	Item createBackpack(@Nonnull IBackpackDefinition definition, @Nonnull EnumBackpackType type);

}
