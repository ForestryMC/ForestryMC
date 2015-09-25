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
package forestry.core.recipes.nei;

import forestry.core.utils.Log;
import forestry.factory.recipes.nei.NEIHandlerBottler;
import forestry.factory.recipes.nei.NEIHandlerCarpenter;
import forestry.factory.recipes.nei.NEIHandlerCentrifuge;
import forestry.factory.recipes.nei.NEIHandlerFabricator;
import forestry.factory.recipes.nei.NEIHandlerFermenter;
import forestry.factory.recipes.nei.NEIHandlerMoistener;
import forestry.factory.recipes.nei.NEIHandlerSqueezer;
import forestry.factory.recipes.nei.NEIHandlerStill;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIIntegrationConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		Log.info("Loading: " + getName());

		registerHandler(new NEIHandlerShapedCustom());
		registerHandler(new NEIHandlerShapelessCustom());
		registerHandler(new NEIHandlerBottler());
		registerHandler(new NEIHandlerCarpenter());
		registerHandler(new NEIHandlerCentrifuge());
		registerHandler(new NEIHandlerFabricator());
		registerHandler(new NEIHandlerFermenter());
		registerHandler(new NEIHandlerMoistener());
		registerHandler(new NEIHandlerSqueezer());
		registerHandler(new NEIHandlerStill());
	}

	protected static void registerHandler(IRecipeHandlerBase handler) {
		handler.prepare();
		API.registerRecipeHandler(handler);
		API.registerUsageHandler(handler);
	}

	@Override
	public String getName() {
		return "Forestry NEI Integration";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

}
