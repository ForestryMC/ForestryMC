/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

/**
 * Some miscellaneous lists and settings for bees.
 *
 * @author SirSengir
 */
public class BeeManager {

	/**
	 * Common Village Bees
	 */
	public static List<IBeeGenome> commonVillageBees;
	/**
	 * Uncommon Village Bees (20 % of spawns)
	 */
	public static List<IBeeGenome> uncommonVillageBees;

	/**
	 * List of items that can induce swarming. Integer denotes x in 1000 chance.
	 */
	public static final Map<ItemStack, Integer> inducers = new HashMap<>();

	/**
	 * Convenient access to AlleleManager.alleleRegistry.getSpeciesRoot("rootBees")
	 *
	 * @implNote Only null if the "apiculture" module is not enabled.
	 */
	@Nullable
	public static IBeeRoot beeRoot;

	/**
	 * Used to create new bees.
	 *
	 * @implNote Only null if the "apiculture" module is not enabled.
	 */
	@Nullable
	public static IBeeFactory beeFactory;

	/**
	 * Used to create new bee mutations.
	 *
	 * @implNote Only null if the "apiculture" module is not enabled.
	 */
	@Nullable
	public static IBeeMutationFactory beeMutationFactory;

	/**
	 * Used to get Forestry's jubilance implementations.
	 *
	 * @implNote Only null if the "apiculture" module is not enabled.
	 */
	@Nullable
	public static IJubilanceFactory jubilanceFactory;

	/**
	 * Used to check whether a player is wearing Apiarist Armor.
	 *
	 * @implNote Only null if the "apiculture" module is not enabled.
	 */
	@Nullable
	public static IArmorApiaristHelper armorApiaristHelper;
}
