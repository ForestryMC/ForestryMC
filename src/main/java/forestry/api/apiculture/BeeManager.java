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

import genetics.api.GeneticsAPI;
import genetics.api.individual.IGenome;
import genetics.api.root.IRootDefinition;

import forestry.api.apiculture.genetics.IBeeFactory;
import forestry.api.apiculture.genetics.IBeeMutationFactory;
import forestry.api.apiculture.genetics.IBeeRoot;

/**
 * Some miscellaneous lists and settings for bees.
 *
 * @author SirSengir
 */
public class BeeManager {

	/**
	 * Common Village Bees
	 */
	public static List<IGenome> commonVillageBees;
	/**
	 * Uncommon Village Bees (20 % of spawns)
	 */
	public static List<IGenome> uncommonVillageBees;

	/**
	 * List of items that can induce swarming. Integer denotes x in 1000 chance.
	 */
	public static final Map<ItemStack, Integer> inducers = new HashMap<>();

	public static final IRootDefinition<IBeeRoot> beeRootDefinition = GeneticsAPI.apiInstance.getRoot("rootBees");

	/**
	 * Convenient access to AlleleManager.alleleRegistry.getSpeciesRoot("rootBees")
	 *
	 * @implNote Only null if the "apiculture" module is not enabled.
	 */
	//TODO: Move most calls to definition (more save)
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
	 * Used to getComb Forestry's jubilance implementations.
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
