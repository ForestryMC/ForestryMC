/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

public class TreeManager {

	/**
	 * Convenient access to AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees")
	 */
	public static ITreeRoot treeRoot;

	/**
	 * Convenient access to wood items.
	 */
	public static IWoodItemAccess woodItemAccess;

	/**
	 * Used to create new trees.
	 */
	public static ITreeFactory treeFactory;

	/**
	 * Used to create new tree mutations.
	 */
	public static ITreeMutationFactory treeMutationFactory;
}
