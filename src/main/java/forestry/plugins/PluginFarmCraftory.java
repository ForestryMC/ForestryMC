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
package forestry.plugins;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import forestry.api.farming.Farmables;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.farming.logic.FarmableFarmCraftory;

@Plugin(pluginID = "FarmCraftory", name = "FarmCraftory", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.farmcraftory.description")
public class PluginFarmCraftory extends ForestryPlugin {

	public static Block blockSingle;
	public static Block blockMulti;

	public static Class<?> classSingle;
	public static Class<?> classMulti;

	public static Method methodGrowthSingle;
	public static Method methodGrowthMulti;

	public static final HashMap<String, ItemStack> vegetableSeeds = new HashMap<String, ItemStack>();
	public static final String[] seedIdentifiers = new String[]{"turnipSeedBag", "cabbageSeedBag", "onionSeedBag", "spinachSeedBag", "leekSeedBag",
			"cucumberSeedBag", "tomatoSeedBag", "eggplantSeedBag", "greenPepperSeedBag", "yamSeedBag", "strawberrySeedBag", "pineappleSeedBag"};
	public static final HashMap<String, ItemStack> vegetableItems = new HashMap<String, ItemStack>();
	public static final String[] vegetableIdentifiers = new String[]{"turnipItem", "cabbageItem", "onionItem", "spinachItem", "leekItem", "cucumberItem",
			"tomatoItem", "eggplantItem", "greenPepperItem", "yamItem", "strawberryItem", "pineappleItem"};

	public static final HashMap<String, ItemStack> cerealSeeds = new HashMap<String, ItemStack>();
	public static final String[] cseedIdentifiers = new String[]{"cornSeedBag"};
	public static final HashMap<String, ItemStack> cerealItems = new HashMap<String, ItemStack>();
	public static final String[] cerealIdentifiers = new String[]{"cornItem"};

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded("FarmCraftory");
	}

	@Override
	public String getFailMessage() {
		return "FarmCraftory not found";
	}

	@Override
	public void doInit() {
		super.doInit();

		try {

			blockSingle = (Block) Class.forName("farmcraftory.FarmCraftory").getField("singleHarvest").get(null);
			blockMulti = (Block) Class.forName("farmcraftory.FarmCraftory").getField("multiHarvest").get(null);

			classSingle = Class.forName("farmcraftory.TileEntitySingleHarvest");
			classMulti = Class.forName("farmcraftory.TileEntityMultiHarvest");

			methodGrowthSingle = classSingle.getMethod("getGrowthStage");
			methodGrowthMulti = classMulti.getMethod("getGrowthStage");

			for (String str : seedIdentifiers) {
				try {
					vegetableSeeds.put(str, new ItemStack((Item) Class.forName("farmcraftory.FarmCraftory").getField(str).get(null)));
				} catch (Exception ex) {
					Proxies.log.info("FarmCraftory item '%s' could not be integrated.", str);
					Proxies.log.info(ex.getMessage());
				}
			}

			for (String str : vegetableIdentifiers) {
				try {
					vegetableItems.put(str, new ItemStack((Item) Class.forName("farmcraftory.FarmCraftory").getField(str).get(null)));
				} catch (Exception ex) {
					Proxies.log.info("FarmCraftory item '%s' could not be integrated.", str);
					Proxies.log.info(ex.getMessage());
				}
			}

			for (String str : cseedIdentifiers) {
				try {
					cerealSeeds.put(str, new ItemStack((Item) Class.forName("farmcraftory.FarmCraftory").getField(str).get(null)));
				} catch (Exception ex) {
					Proxies.log.info("FarmCraftory item '%s' could not be integrated.", str);
					Proxies.log.info(ex.getMessage());
				}
			}

			for (String str : cerealIdentifiers) {
				try {
					cerealItems.put(str, new ItemStack((Item) Class.forName("farmcraftory.FarmCraftory").getField(str).get(null)));
				} catch (Exception ex) {
					Proxies.log.info("FarmCraftory item '%s' could not be integrated.", str);
					Proxies.log.info(ex.getMessage());
				}
			}

			if (blockMulti != null && blockSingle != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableFarmCraftory(cerealSeeds.values(), cerealItems.values()));
				Farmables.farmables.get("farmVegetables").add(new FarmableFarmCraftory(vegetableSeeds.values(), vegetableItems.values()));
			}

		} catch (Exception ex) {
			Proxies.log.info("FarmCraftory plugin unexpectedly failed to load.");
			Proxies.log.info(ex.getMessage());
		}
	}

	public static int getGrowthStage(TileEntity tile) {
		try {
			if (classSingle.isInstance(tile)) {
				return (Integer) methodGrowthSingle.invoke(tile);
			} else if (classMulti.isInstance(tile)) {
				return (Integer) methodGrowthMulti.invoke(tile);
			}
		} catch (Exception ex) {
			return 0;
		}

		return 0;
	}

}
