/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.storage;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

import forestry.api.genetics.ISpeciesRoot;

/**
 * The Backpack Interface allows you to add items to Forestry backpacks or create your own backpacks.
 * <p>
 * To create your own backpack, create an {@link IBackpackDefinition}.
 * Backpack definitions have a filter, which you can create here with
 * {@link #createBackpackFilter()}
 * or {@link #createNaturalistBackpackFilter(String)}
 * or create your own.
 * <p>
 * After you've registered your backpack definition, create the item with
 * {@link #createBackpack(String, EnumBackpackType)}
 * or {@link #createNaturalistBackpack(String, ISpeciesRoot)}
 * and then register the returned item with {@link GameRegistry#register(IForgeRegistryEntry)} like any other item.
 */
public interface IBackpackInterface {
	/**
	 * Add an accepted item to a Forestry backpack.
	 *
	 * @param backpackUid The unique ID of the Forestry backpack. See {@link BackpackManager} for valid UIDs.
	 * @param itemStack   The itemStack that the backpack should accept.
	 *                    {@link OreDictionary#WILDCARD_VALUE} can be used for meta value.
	 */
	void addItemToForestryBackpack(String backpackUid, ItemStack itemStack);

	/**
	 * Register a backpack definition with a given uid.
	 */
	void registerBackpackDefinition(String backpackUid, IBackpackDefinition definition);

	/**
	 * Get a backpack definition with a given uid.
	 */
	@Nullable
	IBackpackDefinition getBackpackDefinition(String backpackUid);

	/**
	 * Creates a backpack with the given UID and type, returning the item.
	 * The backpack's definition must first be registered with {@link #registerBackpackDefinition(String, IBackpackDefinition)}.
	 *
	 * @param backpackUid The unique ID of the backpack.
	 * @param type        Type of backpack.
	 * @return Created backpack item.
	 */
	Item createBackpack(String backpackUid, EnumBackpackType type);

	/**
	 * Create a backpack that can hold items from a specific {@link ISpeciesRoot}.
	 * The backpack's definition must first be registered with {@link #registerBackpackDefinition(String, IBackpackDefinition)}.
	 *
	 * @param backpackUid The unique ID of the backpack.
	 * @param speciesRoot The species root.
	 * @return Created backpack item.
	 */
	Item createNaturalistBackpack(String backpackUid, ISpeciesRoot speciesRoot);

	/**
	 * Makes a new configurable backpack filter. Useful for implementing {@link IBackpackDefinition}.
	 */
	IBackpackFilterConfigurable createBackpackFilter();

	/**
	 * Makes a new naturalist backpack filter. Only accepts items from a specific {@link ISpeciesRoot}.
	 * Useful for implementing {@link IBackpackDefinition} for naturalist's backpacks.
	 *
	 * @param speciesRootUid The species root's unique ID. See {@link ISpeciesRoot#getUID()}.
	 * @return a new backpack filter for the specified species root
	 * @see #createNaturalistBackpack(String, ISpeciesRoot)
	 */
	Predicate<ItemStack> createNaturalistBackpackFilter(String speciesRootUid);
}
