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
package forestry.greenhouse;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import forestry.api.core.ForestryAPI;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.BlockRegistryGreenhouse;
import forestry.greenhouse.proxy.ProxyGreenhouse;
import forestry.greenhouse.tiles.TileGreenhouseNursery;
import forestry.greenhouse.tiles.TileGreenhouseClimateControl;
import forestry.greenhouse.tiles.TileGreenhouseControl;
import forestry.greenhouse.tiles.TileGreenhouseDoor;
import forestry.greenhouse.tiles.TileGreenhouseDryer;
import forestry.greenhouse.tiles.TileGreenhouseFan;
import forestry.greenhouse.tiles.TileGreenhouseGearbox;
import forestry.greenhouse.tiles.TileGreenhouseHatch;
import forestry.greenhouse.tiles.TileGreenhouseHeater;
import forestry.greenhouse.tiles.TileGreenhousePlain;
import forestry.greenhouse.tiles.TileGreenhouseHumidifier;
import forestry.greenhouse.tiles.TileGreenhouseValve;
import forestry.greenhouse.tiles.TileGreenhouseWindow;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.GameRegistry;

@ForestryPlugin(pluginID = ForestryPluginUids.GREENHOUSE, name = "Greenhouse", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.plugin.greenhouse.description")
public class PluginGreenhouse extends BlankForestryPlugin {

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.greenhouse.proxy.ProxyGreenhouseClient", serverSide = "forestry.greenhouse.proxy.ProxyGreenhouse")
	public static ProxyGreenhouse proxy;

	@Nullable
	private static BlockRegistryGreenhouse blocks;

	public static BlockRegistryGreenhouse getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public void setupAPI() {
		GreenhouseManager.greenhouseHelper = new GreenhouseHelper();
	}
	
	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryGreenhouse();
	}

	@Override
	public void preInit() {
		proxy.initializeModels();
	}
	
	@Override
	public void doInit() {
		super.doInit();

		GameRegistry.registerTileEntity(TileGreenhouseFan.class, "forestry.GreenhouseFan");
		GameRegistry.registerTileEntity(TileGreenhouseHeater.class, "forestry.GreenhouseHeater");
		GameRegistry.registerTileEntity(TileGreenhouseDryer.class, "forestry.GreenhouseDryer");
		GameRegistry.registerTileEntity(TileGreenhouseHumidifier.class, "forestry.GreenhouseSprinkler");
		GameRegistry.registerTileEntity(TileGreenhouseValve.class, "forestry.GreenhouseValve");
		GameRegistry.registerTileEntity(TileGreenhouseGearbox.class, "forestry.GreenhouseGearbox");
		GameRegistry.registerTileEntity(TileGreenhouseControl.class, "forestry.GreenhouseController");
		GameRegistry.registerTileEntity(TileGreenhousePlain.class, "forestry.GreenhousePlain");
		GameRegistry.registerTileEntity(TileGreenhouseDoor.class, "forestry.GreenhouseDoor");
		GameRegistry.registerTileEntity(TileGreenhouseHatch.class, "forestry.GreenhouseHatch");
		GameRegistry.registerTileEntity(TileGreenhouseClimateControl.class, "forestry.GreenhouseClimateControl");
		GameRegistry.registerTileEntity(TileGreenhouseWindow.class, "forestry.GreenhouseWindow");
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			GameRegistry.registerTileEntity(TileGreenhouseNursery.class, "forestry.GreenhouseButterflyHatch");
		}
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = PluginCore.getItems();
		BlockRegistryGreenhouse blocks = getBlocks();

		Block greenhousePlainBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN);
		RecipeUtil.addRecipe("greenhouse_plain", new ItemStack(greenhousePlainBlock, 2),
				"#X#",
				"SIS",
				'I', OreDictUtil.INGOT_IRON,
				'S', OreDictUtil.SLAB_WOOD,
				'X', Blocks.BRICK_BLOCK,
				'#', coreItems.craftingMaterial.getCamouflagedPaneling());

		Block greenhouseGlassBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.GLASS);
		RecipeUtil.addRecipe("greenhouse_glass", new ItemStack(greenhouseGlassBlock, 2),
				"#X#",
				"PIP",
				'I', OreDictUtil.INGOT_IRON,
				'X', OreDictUtil.BLOCK_GLASS,
				'P', OreDictUtil.PANE_GLASS,
				'#', coreItems.craftingMaterial.getCamouflagedPaneling());

		Block greenhouseHatchInputBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.HATCH_INPUT);
		RecipeUtil.addRecipe("greenhouse_hatch_in", greenhouseHatchInputBlock,
				"TXT",
				"#H#",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'H', OreDictUtil.TRAPDOOR_WOOD,
				'#', OreDictUtil.GEAR_TIN,
				'T', coreItems.tubes.get(EnumElectronTube.BRONZE, 1));

		Block greenhouseHatchOutputBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.HATCH_OUTPUT);
		RecipeUtil.addRecipe("greenhouse_hatch_out", greenhouseHatchOutputBlock,
				"#H#",
				"TXT",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'H', OreDictUtil.TRAPDOOR_WOOD,
				'#', OreDictUtil.GEAR_TIN,
				'T', coreItems.tubes.get(EnumElectronTube.BRONZE, 1));

		Block greenhouseControlBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.CONTROL);
		RecipeUtil.addRecipe("greenhouse_control", greenhouseControlBlock,
				" X ",
				"#T#",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.DUST_REDSTONE,
				'T', coreItems.tubes.get(EnumElectronTube.GOLD, 1));

		Block greenhouseGearBoxBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.GEARBOX);
		RecipeUtil.addRecipe("greenhouse_gearbox", greenhouseGearBoxBlock,
				" X ",
				"###",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.GEAR_TIN);

		Block greenhouseValveBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.VALVE);
		RecipeUtil.addRecipe("greenhouse_valve", greenhouseValveBlock,
				" X ",
				"#G#",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.BLOCK_GLASS,
				'G', OreDictUtil.GEAR_TIN);

		Block greenhouseHeaterBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.HEATER);
		RecipeUtil.addRecipe("greenhouse_heater", greenhouseHeaterBlock,
				"T#T",
				"#X#",
				"T#T",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.INGOT_TIN,
				'T', coreItems.tubes.get(EnumElectronTube.GOLD, 1));

		Block greenhouseFanlock = blocks.getGreenhouseBlock(BlockGreenhouseType.FAN);
		RecipeUtil.addRecipe("greenhouse_fan", greenhouseFanlock,
				"T#T",
				"#X#",
				"T#T",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.INGOT_IRON,
				'T', coreItems.tubes.get(EnumElectronTube.TIN, 1));

		Block greenhouseDryerBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.DEHUMIDIFIER);
		RecipeUtil.addRecipe("greenhouse_dryer", greenhouseDryerBlock,
				"T#T",
				"#X#",
				"T#T",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.INGOT_TIN,
				'T', coreItems.tubes.get(EnumElectronTube.BLAZE, 1));

		Block greenhouseSprinklerBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.HUMIDIFIER);
		RecipeUtil.addRecipe("greenhouse_sprinkler", greenhouseSprinklerBlock,
				"T#T",
				"#X#",
				"T#T",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.INGOT_TIN,
				'T', coreItems.tubes.get(EnumElectronTube.LAPIS, 1));

		Block greenhouseDoorBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.DOOR);
		RecipeUtil.addRecipe("greenhouse_door", greenhouseDoorBlock,
				true,
				"GG ",
				"GG ",
				"GG ",
				'G', blocks.getGreenhouseBlock(BlockGreenhouseType.GLASS));

		Block greenhouseWindowBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.WINDOW);
		RecipeUtil.addRecipe("greenhouse_window", greenhouseWindowBlock,
				true,
				"GGS",
				"GGG",
				"GGS",
				'G', blocks.getGreenhouseBlock(BlockGreenhouseType.GLASS),
				'S', OreDictUtil.STICK_WOOD);

		Block greenhouseWindowRoofBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.WINDOW_UP);
		RecipeUtil.addRecipe("greenhouse_window_roof", greenhouseWindowRoofBlock,
				true,
				"SGS",
				"GGG",
				"GGG",
				'G', blocks.getGreenhouseBlock(BlockGreenhouseType.GLASS),
				'S', OreDictUtil.STICK_WOOD);

		Block greenhouseClimateControlBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.CLIMATE_CONTROL);
		RecipeUtil.addRecipe("greenhouse_climate_control", greenhouseClimateControlBlock,
				true,
				"IRG",
				"EBT",
				"GRI",
				'B', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'I', OreDictUtil.INGOT_BRONZE,
				'G', OreDictUtil.GEAR_TIN,
				'R', OreDictUtil.DUST_REDSTONE,
				'T', coreItems.tubes.get(EnumElectronTube.LAPIS, 1),
				'E', coreItems.tubes.get(EnumElectronTube.BLAZE, 1));

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			Block greenhouseButterflyHatchBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.NURSERY);
			RecipeUtil.addRecipe("greenhouse_butterfly_hatch", greenhouseButterflyHatchBlock,
					true,
					"IRG",
					"SBS",
					"GRI",
					'R', OreDictUtil.DUST_REDSTONE,
					'I', OreDictUtil.INGOT_IRON,
					'G', OreDictUtil.GEAR_COPPER,
					'B', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
					'S', coreItems.craftingMaterial.getSilkWisp());
		}
	}

}
