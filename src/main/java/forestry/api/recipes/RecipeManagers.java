/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Contains all available recipe managers for Forestry machines and items.
 *
 * @author SirSengir
 */
public class RecipeManagers {

	/**
	 * Allows you to add recipes to the carpenter. See {@link ICarpenterManager} for details.
	 */
	public static ICarpenterManager carpenterManager;
	/**
	 * Allows you to add recipes to the centrifuge. See {@link ICentrifugeManager} for details.
	 */
	public static ICentrifugeManager centrifugeManager;
	/**
	 * Allows you to add recipes to the fermenter. See {@link IFermenterManager} for details.
	 */
	public static IFermenterManager fermenterManager;
	/**
	 * Allows you to add recipes to the moistener. See {@link IMoistenerManager} for details.
	 */
	public static IMoistenerManager moistenerManager;
	/**
	 * Allows you to add recipes to the squeezer. See {@link ISqueezerManager} for details.
	 */
	public static ISqueezerManager squeezerManager;
	/**
	 * Allows you to add container recipes to the squeezer. See {@link ISqueezerContainerManager} for details.
	 */
	public static ISqueezerContainerManager squeezerContainerManager;
	/**
	 * Allows you to add recipes to the still. See {@link IStillManager} for details.
	 */
	public static IStillManager stillManager;
	/**
	 * Allows you to add recipes to the fabricator. See {@link IFabricatorManager} for details.
	 */
	public static IFabricatorManager fabricatorManager;
	/**
	 * Allows you to add smelting recipes to the fabricator. See {@link IFabricatorSmeltingManager} for details.
	 */
	public static IFabricatorSmeltingManager fabricatorSmeltingManager;
	/**
	 * Allows you to add recipes to the hygroregulator and habitatformer. See {@link IHygroregulatorManager} for details.
	 */
	public static IHygroregulatorManager hygroregulatorManager;

	static <T extends Recipe<?>> RecipeType<T> create(String name) {
		return new RecipeType<T>() {
			@Override
			public String toString() {
				return name;
			}
		};
	}
}
