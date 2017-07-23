/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import javax.annotation.Nullable;
import java.util.Set;

import forestry.api.climate.IClimateManager;
import forestry.api.farming.IFarmRegistry;
import forestry.api.farming.ISimpleFarmLogic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Forestry's API is divided into several subcategories to make it easier to understand.
 * <p>
 * If you need to distribute API files, try to only include the parts you are actually
 * using to minimize conflicts due to API changes.
 * <p>
 * .core     - Miscallenous base classes and interfaces as well as some basics for tools, armor, game modes and stuff needed by biome mods.
 * .fuels    - Managers and classes to facilitate adding fuels to various engines and machines.
 * .recipes  - Managers and helpers to facilitate adding new recipes to various machines.
 * .storage  - Managers, events and interfaces for defining new backpacks and handling backpack behaviour.
 * .mail     - Anything related to handling letters and adding new mail carrier systems.
 * .genetics - Shared code for all genetic subclasses.
 * \ .apiculture       - Bees.
 * \ .arboriculture    - Trees.
 * \ .lepidopterology  - Butterflies.
 * <p>
 * Note that if Forestry is not present, all these references will be null.
 */
public class ForestryAPI {

	/**
	 * The main mod instance for Forestry.
	 */
	public static Object instance;

	/**
	 * A {@link ITextureManager} needed for some things in the API.
	 */
	@SideOnly(Side.CLIENT)
	public static ITextureManager textureManager;

	@SideOnly(Side.CLIENT)
	public static IModelManager modelManager;

	public static IClimateManager climateManager;

	/**
	 * A registry for register fertilizers, {@link IFarmabeles}s and {@link ISimpleFarmLogic}s
	 */
	public static IFarmRegistry farmRegistry;
	
	/**
	 * The currently active {@link IGameMode}.
	 */
	public static IGameMode activeMode;

	/**
	 * Provides information on certain Forestry constants (Villager IDs, Chest gen keys, etc)
	 */
	public static IForestryConstants forestryConstants;

	/**
	 * The currently enabled Forestry plugins.
	 * Can be used to check if certain features are available, for example:
	 * ForestryAPI.enabledPlugins.contains("APICULTURE")
	 */
	public static Set<String> enabledPlugins;

	/**
	 * Instance of the errorStateRegistry for registering errors.
	 * Also creates new instances of IErrorLogic.
	 */
	public static IErrorStateRegistry errorStateRegistry;
}
