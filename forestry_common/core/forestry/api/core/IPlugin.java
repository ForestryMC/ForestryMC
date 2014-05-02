/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.core;

/**
 * Optional way to hook into Forestry.
 * 
 * Plugin classes can reside in any package, their class name however has to start with 'Plugin', i.e. 'PluginMyStuff'.
 * 
 * @author SirSengir
 */
public interface IPlugin {
	
	/**
	 * @return true if the plugin is to be loaded.
	 */
	public boolean isAvailable();

	/**
	 * Called during Forestry's @PreInit.
	 */
	public void preInit();

	/**
	 * Called at the start of Forestry's @PostInit.
	 */
	public void doInit();

	/**
	 * Called at the end of Forestry's @PostInit.
	 */
	public void postInit();

}
