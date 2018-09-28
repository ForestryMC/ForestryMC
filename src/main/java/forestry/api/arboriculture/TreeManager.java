/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TreeManager {

	/**
	 * Convenient access to AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees")
	 *
	 * @implNote Only null if the "arboriculture" module is not enabled.
	 */
	@Nullable
	public static ITreeRoot treeRoot;

	/**
	 * Convenient access to wood items.
	 */
	public static IWoodAccess woodAccess;

	/**
	 * Used to create new trees.
	 *
	 * @implNote Only null if the "arboriculture" module is not enabled.
	 */
	@Nullable
	public static ITreeFactory treeFactory;

	/**
	 * Used to create new tree mutations.
	 *
	 * @implNote Only null if the "arboriculture" module is not enabled.
	 */
	@Nullable
	public static ITreeMutationFactory treeMutationFactory;

	/**
	 * Can be used to add new charcoal pile walls.
	 *
	 * @implNote Only null if the "charcoal" module is not enabled.
	 */
	@Nullable
	public static ICharcoalManager charcoalManager;

	/**
	 * @deprecated Please use {@link ICharcoalManager#registerWall}.
	 *
	 * */
	@Deprecated
	public static List<ICharcoalPileWall> pileWalls = new ArrayList<>();
}
