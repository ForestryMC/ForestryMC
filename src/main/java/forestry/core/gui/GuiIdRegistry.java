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
package forestry.core.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import forestry.apiculture.entities.EntityMinecartApiary;
import forestry.apiculture.entities.EntityMinecartBeehouse;
import forestry.apiculture.items.ItemBeealyzer;
import forestry.apiculture.items.ItemHabitatLocator;
import forestry.apiculture.items.ItemImprinter;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.tiles.TileApiary;
import forestry.apiculture.tiles.TileBeehouse;
import forestry.arboriculture.items.ItemTreealyzer;
import forestry.core.items.ItemSolderingIron;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileEscritoire;
import forestry.core.tiles.TileNaturalistChest;
import forestry.energy.tiles.TileEngineBiogas;
import forestry.energy.tiles.TileEngineElectric;
import forestry.energy.tiles.TileEnginePeat;
import forestry.energy.tiles.TileGenerator;
import forestry.factory.tiles.TileBottler;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileCentrifuge;
import forestry.factory.tiles.TileFabricator;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileRaintank;
import forestry.factory.tiles.TileSqueezer;
import forestry.factory.tiles.TileStill;
import forestry.factory.tiles.TileWorktable;
import forestry.farming.tiles.TileFarm;
import forestry.food.items.ItemInfuser;
import forestry.lepidopterology.items.ItemFlutterlyzer;
import forestry.mail.items.ItemCatalogue;
import forestry.mail.items.ItemLetter;
import forestry.mail.tiles.TileMailbox;
import forestry.mail.tiles.TileStampCollector;
import forestry.mail.tiles.TileTrader;
import forestry.storage.items.ItemBackpack;
import forestry.storage.items.ItemBackpackNaturalist;

public class GuiIdRegistry {
	private static final Map<Class<? extends IGuiHandlerForestry>, GuiId> classMap = new HashMap<>();
	private static final Map<Integer, GuiId> idMap = new HashMap<>();
	private static int nextId = 0;

	static {
		registerGuiHandlers(GuiType.Tile, Arrays.<Class<? extends IGuiHandlerForestry>>asList(
				TileAlveary.class,
				TileAlvearyHygroregulator.class,
				TileAlvearySieve.class,
				TileApiary.class,
				TileBeehouse.class,

				TileAnalyzer.class,
				TileEscritoire.class,
				TileNaturalistChest.class,
				TileRaintank.class,
				TileWorktable.class,

				TileBottler.class,
				TileCarpenter.class,
				TileCentrifuge.class,
				TileFabricator.class,
				TileFermenter.class,
				TileMoistener.class,
				TileSqueezer.class,
				TileStill.class,

				TileFarm.class,

				TileEngineBiogas.class,
				TileEngineElectric.class,
				TileEnginePeat.class,
				TileGenerator.class,

				TileMailbox.class,
				TileStampCollector.class,
				TileTrader.class
		));

		registerGuiHandlers(GuiType.Item, Arrays.<Class<? extends IGuiHandlerForestry>>asList(
				ItemBackpack.class,
				ItemBackpackNaturalist.class,
				ItemBeealyzer.class,
				ItemCatalogue.class,
				ItemFlutterlyzer.class,
				ItemHabitatLocator.class,
				ItemImprinter.class,
				ItemInfuser.class,
				ItemLetter.class,
				ItemSolderingIron.class,
				ItemTreealyzer.class
		));

		registerGuiHandlers(GuiType.Entity, Arrays.<Class<? extends IGuiHandlerForestry>>asList(
				EntityMinecartApiary.class,
				EntityMinecartBeehouse.class
		));
	}

	private static void registerGuiHandlers(GuiType guiType, List<Class<? extends IGuiHandlerForestry>> guiHandlerClasses) {
		for (Class<? extends IGuiHandlerForestry> tileGuiHandlerClass : guiHandlerClasses) {
			GuiId guiId = new GuiId(nextId++, guiType, tileGuiHandlerClass);
			classMap.put(tileGuiHandlerClass, guiId);
			idMap.put(guiId.getId(), guiId);
		}
	}

	public static GuiId getGuiIdForGuiHandler(IGuiHandlerForestry guiHandler) {
		Class<? extends IGuiHandlerForestry> guiHandlerClass = guiHandler.getClass();
		GuiId guiId = classMap.get(guiHandlerClass);
		if (guiId == null) {
			for (Map.Entry<Class<? extends IGuiHandlerForestry>, GuiId> classGuiIdEntry : classMap.entrySet()) {
				if (classGuiIdEntry.getKey().isAssignableFrom(guiHandlerClass)) {
					guiId = classGuiIdEntry.getValue();
					break;
				}
			}
		}
		if (guiId == null) {
			throw new IllegalStateException("No gui ID for gui handler: " + guiHandler);
		}
		return guiId;
	}

	public static GuiId getGuiId(int id) {
		return idMap.get(id);
	}
}
