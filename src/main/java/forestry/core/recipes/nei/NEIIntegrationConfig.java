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

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.core.utils.Log;
import forestry.factory.gui.GuiWorktable;
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

		registerHandler(new NEIHandlerBottler());
		registerHandler(new NEIHandlerCarpenter());
		registerHandler(new NEIHandlerCentrifuge());
		registerHandler(new NEIHandlerFabricator());
		registerHandler(new NEIHandlerFermenter());
		registerHandler(new NEIHandlerMoistener());
		registerHandler(new NEIHandlerSqueezer());
		registerHandler(new NEIHandlerStill());

		CustomOverlayHandler handler = new CustomOverlayHandler(-14, 14, true);
		API.registerGuiOverlayHandler(GuiWorktable.class, handler, "crafting");

		API.addSubset("Forestry.Bees.Princesses", new ItemFilterGenetic(BeeManager.beeRoot, EnumBeeType.PRINCESS.ordinal(), true));
		API.addSubset("Forestry.Bees.Drones", new ItemFilterGenetic(BeeManager.beeRoot, EnumBeeType.DRONE.ordinal(), true));
		API.addSubset("Forestry.Bees.Larvae", new ItemFilterGenetic(BeeManager.beeRoot, EnumBeeType.LARVAE.ordinal(), true));

		API.addSubset("Forestry.Trees.Saplings", new ItemFilterGenetic(TreeManager.treeRoot, EnumGermlingType.SAPLING.ordinal(), true));
		API.addSubset("Forestry.Trees.Pollen", new ItemFilterGenetic(TreeManager.treeRoot, EnumGermlingType.POLLEN.ordinal(), true));

		API.addSubset("Forestry.Butterflies.Butterflies", new ItemFilterGenetic(ButterflyManager.butterflyRoot, EnumFlutterType.BUTTERFLY.ordinal(), true));
		API.addSubset("Forestry.Butterflies.Caterpillars", new ItemFilterGenetic(ButterflyManager.butterflyRoot, EnumFlutterType.CATERPILLAR.ordinal(), true));
		API.addSubset("Forestry.Butterflies.Serum", new ItemFilterGenetic(ButterflyManager.butterflyRoot, EnumFlutterType.SERUM.ordinal(), true));

		API.addSubset("Forestry.Bees.Combs", OreDictionary.getOres("beeComb"));
		API.addSubset("Forestry.Blocks", new ItemFilterOther(false));
		API.addSubset("Forestry.Items", new ItemFilterOther(true));
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
		return "1.1.0";
	}

}
