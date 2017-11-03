/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.modules;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to provide additional information on IForestryModules. This information will be available via the "/forestry module info {@link #moduleID()}" command ingame.
 * Any class annotated by this and implementing {@link IForestryModule} will be loaded by the model manager of Forestry.
 *
 * @author Nedelosk
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ForestryModule {

	/**
	 * @return Unique identifier for the module, no spaces!
	 */
	String moduleID();

	/**
	 * @return The unique identifier of the module container.
	 */
	String containerID();

	/**
	 * @return Nice and readable module name.
	 */
	String name();

	/**
	 * @return ForestryModule author's name.
	 */
	String author() default "";

	/**
	 * @return URL of plugin homepage.
	 */
	String url() default "";

	/**
	 * @return Not used (yet?).
	 */
	String help() default "";

	/**
	 * @return The name of the loot table file, if any.
	 */
	String lootTable() default "";

	/**
	 * @return True if this is a core module.
	 */
	boolean coreModule() default false;

	/**
	 * @return Version of the module, if any.
	 */
	String version() default "";

	/**
	 * @return Localization key for a short description what the module does.
	 */
	String unlocalizedDescription() default "";

}
