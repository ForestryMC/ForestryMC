/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.factory.recipes;

import java.lang.reflect.Method;

import forestry.core.proxy.Proxies;

public class CraftGuideIntegration {

	public static void register() {
		registerRecipeProviders();
	}

	private static void registerRecipeProviders() {

		Proxies.log.info("Registering CraftGuide integration.");
		try {
			Class<?> c = Class.forName("uristqwerty.CraftGuide.ReflectionAPI");
			Method m = c.getMethod("registerAPIObject", Object.class);

			Proxies.log.fine("Adding crafting handler custom recipes.");
			m.invoke(null, new CraftGuideCustomRecipes());
			Proxies.log.fine("Adding crafting handler for the carpenter.");
			m.invoke(null, new CraftGuideCarpenter());
			Proxies.log.fine("Adding crafting handler for the thermionic fabricator.");
			m.invoke(null, new CraftGuideFabricator());
			Proxies.log.fine("Adding crafting handler for the centrifuge.");
			m.invoke(null, new CraftGuideCentrifuge());
			Proxies.log.fine("Adding crafting handler for the squeezer.");
			m.invoke(null, new CraftGuideSqueezer());
			Proxies.log.fine("Adding crafting handler for the fermenter.");
			m.invoke(null, new CraftGuideFermenter());
			Proxies.log.fine("Adding crafting handler for the still.");
			m.invoke(null, new CraftGuideStill());
			Proxies.log.fine("Adding crafting handler for the bottler.");
			m.invoke(null, new CraftGuideBottler());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
