/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.recipes.craftguide;

import java.lang.reflect.Method;

import forestry.core.utils.Log;
import forestry.factory.recipes.craftguide.CraftGuideBottler;
import forestry.factory.recipes.craftguide.CraftGuideCarpenter;
import forestry.factory.recipes.craftguide.CraftGuideCentrifuge;
import forestry.factory.recipes.craftguide.CraftGuideFabricator;
import forestry.factory.recipes.craftguide.CraftGuideFermenter;
import forestry.factory.recipes.craftguide.CraftGuideSqueezer;
import forestry.factory.recipes.craftguide.CraftGuideStill;

public class CraftGuideIntegration {

	public static void register() {
		registerRecipeProviders();
	}

	private static void registerRecipeProviders() {

		Log.info("Registering CraftGuide integration.");
		try {
			Class<?> c = Class.forName("uristqwerty.CraftGuide.ReflectionAPI");
			Method m = c.getMethod("registerAPIObject", Object.class);

			Log.fine("Adding crafting handler custom recipes.");
			m.invoke(null, new CraftGuideCustomRecipes());
			Log.fine("Adding crafting handler for the carpenter.");
			m.invoke(null, new CraftGuideCarpenter());
			Log.fine("Adding crafting handler for the thermionic fabricator.");
			m.invoke(null, new CraftGuideFabricator());
			Log.fine("Adding crafting handler for the centrifuge.");
			m.invoke(null, new CraftGuideCentrifuge());
			Log.fine("Adding crafting handler for the squeezer.");
			m.invoke(null, new CraftGuideSqueezer());
			Log.fine("Adding crafting handler for the fermenter.");
			m.invoke(null, new CraftGuideFermenter());
			Log.fine("Adding crafting handler for the still.");
			m.invoke(null, new CraftGuideStill());
			Log.fine("Adding crafting handler for the bottler.");
			m.invoke(null, new CraftGuideBottler());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
