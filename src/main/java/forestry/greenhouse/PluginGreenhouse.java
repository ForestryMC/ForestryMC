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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.recipes.RecipeManagers;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.BlockRegistryGreenhouse;
import forestry.greenhouse.logics.GreenhouseLogicGreenhouseDoor;
import forestry.greenhouse.logics.GreenhouseLogicGreenhouseEffect;
import forestry.greenhouse.proxy.ProxyGreenhouse;
import forestry.greenhouse.tiles.TileGreenhouseButterflyHatch;
import forestry.greenhouse.tiles.TileGreenhouseClimateControl;
import forestry.greenhouse.tiles.TileGreenhouseControl;
import forestry.greenhouse.tiles.TileGreenhouseDoor;
import forestry.greenhouse.tiles.TileGreenhouseDryer;
import forestry.greenhouse.tiles.TileGreenhouseFan;
import forestry.greenhouse.tiles.TileGreenhouseGearbox;
import forestry.greenhouse.tiles.TileGreenhouseHatch;
import forestry.greenhouse.tiles.TileGreenhouseHeater;
import forestry.greenhouse.tiles.TileGreenhousePlain;
import forestry.greenhouse.tiles.TileGreenhouseSprinkler;
import forestry.greenhouse.tiles.TileGreenhouseValve;
import forestry.greenhouse.tiles.TileGreenhouseWindow;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.GREENHOUSE, name = "Greenhouse", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.plugin.greenhouse.description")
public class PluginGreenhouse extends BlankForestryPlugin {

	@SidedProxy(clientSide = "forestry.greenhouse.proxy.ProxyGreenhouseClient", serverSide = "forestry.greenhouse.proxy.ProxyGreenhouse")
	public static ProxyGreenhouse proxy;
	
	public static BlockRegistryGreenhouse blocks;
	
	@Override
	public void setupAPI() {
		GreenhouseManager.greenhouseHelper = new GreenhouseHelper();
	}
	
	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryGreenhouse();
	}
	
	@Override
	public void registerRecipes() {
		
		Block greenhousePlainBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN);
		RecipeUtil.addRecipe(greenhousePlainBlock,
				"###",
				"#X#",
				"###",
				'X', Blocks.BRICK_BLOCK,
				'#', PluginCore.items.craftingMaterial.getCamouflagedPaneling());

		Block greenhouseGlassBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.GLASS);
		RecipeUtil.addRecipe(greenhouseGlassBlock,
				"###",
				"#X#",
				"###",
				'X', "blockGlass",
				'#', PluginCore.items.craftingMaterial.getCamouflagedPaneling());

		Block greenhouseHatchInputBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.HATCH_INPUT);
		RecipeUtil.addRecipe(greenhouseHatchInputBlock,
				"TXT",
				"#H#",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'H', OreDictUtil.TRAPDOOR_WOOD,
				'#', "gearTin",
				'T', PluginCore.items.tubes.get(EnumElectronTube.BRONZE, 1));

		Block greenhouseHatchOutputBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.HATCH_OUTPUT);
		RecipeUtil.addRecipe(greenhouseHatchOutputBlock,
				"#H#",
				"TXT",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'H', OreDictUtil.TRAPDOOR_WOOD,
				'#', "gearTin",
				'T', PluginCore.items.tubes.get(EnumElectronTube.BRONZE, 1));

		Block greenhouseControlBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.CONTROL);
		RecipeUtil.addRecipe(greenhouseControlBlock,
				" X ",
				"#T#",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.DUST_REDSTONE,
				'T', PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1));

		Block greenhouseGearBoxBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.GEARBOX);
		RecipeUtil.addRecipe(greenhouseGearBoxBlock,
				" X ",
				"###",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.GEAR_TIN);

		Block greenhouseValveBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.VALVE);
		RecipeUtil.addRecipe(greenhouseValveBlock,
				" X ",
				"#G#",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.BLOCK_GLASS,
				'G', OreDictUtil.GEAR_TIN);

		Block greenhouseHeaterBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.HEATER);
		RecipeUtil.addRecipe(greenhouseHeaterBlock,
				"T#T",
				"#X#",
				"T#T",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.INGOT_TIN,
				'T', PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1));

		Block greenhouseFanlock = blocks.getGreenhouseBlock(BlockGreenhouseType.FAN);
		RecipeUtil.addRecipe(greenhouseFanlock,
				"T#T",
				"#X#",
				"T#T",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.INGOT_IRON,
				'T', PluginCore.items.tubes.get(EnumElectronTube.TIN, 1));

		Block greenhouseDryerBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.DRYER);
		RecipeUtil.addRecipe(greenhouseDryerBlock,
				"T#T",
				"#X#",
				"T#T",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'#', OreDictUtil.INGOT_TIN,
				'T', PluginCore.items.tubes.get(EnumElectronTube.BLAZE, 1));

		Block greenhouseSprinklerBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.SPRINKLER);
		RecipeUtil.addRecipe(greenhouseSprinklerBlock,
				"TXT",
				"GIG",
				" I ",
				'X', blocks.getGreenhouseBlock(BlockGreenhouseType.GLASS),
				'I', OreDictUtil.INGOT_IRON,   
				'G', OreDictUtil.GEAR_TIN,
				'T', PluginCore.items.tubes.get(EnumElectronTube.LAPIS, 1));

		Block greenhouseDoorBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.DOOR);
		RecipeUtil.addRecipe(greenhouseDoorBlock,
				true,
				"GG ",
				"GG ",
				"GG ",
				'G', blocks.getGreenhouseBlock(BlockGreenhouseType.GLASS));
		
		Block greenhouseWindowBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.WINDOW);
		RecipeUtil.addRecipe(greenhouseWindowBlock,
				true,
				"GGS",
				"GGG",
				"GGS",
				'G', blocks.getGreenhouseBlock(BlockGreenhouseType.GLASS),
				'S', OreDictUtil.STICK_WOOD);
		
		Block greenhouseWindowRoofBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.WINDOW_UP);
		RecipeUtil.addRecipe(greenhouseWindowRoofBlock,
				true,
				"SGS",
				"GGG",
				"GGG",
				'G', blocks.getGreenhouseBlock(BlockGreenhouseType.GLASS),
				'S', OreDictUtil.STICK_WOOD);
		
		Block greenhouseClimateControlBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.CLIMATE_CONTROL);
		RecipeUtil.addRecipe(greenhouseClimateControlBlock,
				true,
				"IRG",
				"EBT",
				"GRI",
				'B', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
				'I', OreDictUtil.INGOT_BRONZE,
				'G', OreDictUtil.GEAR_TIN,
				'R', OreDictUtil.DUST_REDSTONE,
				'T', PluginCore.items.tubes.get(EnumElectronTube.LAPIS, 1),
				'E', PluginCore.items.tubes.get(EnumElectronTube.BLAZE, 1));

		if(ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)){
			Block greenhouseButterflyHatchBlock = blocks.getGreenhouseBlock(BlockGreenhouseType.BUTTERFLY_HATCH);
			RecipeUtil.addRecipe(greenhouseButterflyHatchBlock,
					true,
					"IRG",
					"SBS",
					"GRI",
					'R', OreDictUtil.DUST_REDSTONE,
					'I', OreDictUtil.INGOT_IRON,
					'G', OreDictUtil.GEAR_COPPER,
					'B', blocks.getGreenhouseBlock(BlockGreenhouseType.PLAIN),
					'S', PluginCore.items.craftingMaterial.getSilkWisp());
		}
	}
	
	@Override
	public void doInit() {
		super.doInit();
		
		GameRegistry.registerTileEntity(TileGreenhouseFan.class, "forestry.GreenhouseFan");
		GameRegistry.registerTileEntity(TileGreenhouseHeater.class, "forestry.GreenhouseHeater");
		GameRegistry.registerTileEntity(TileGreenhouseDryer.class, "forestry.GreenhouseDryer");
		GameRegistry.registerTileEntity(TileGreenhouseSprinkler.class, "forestry.GreenhouseSprinkler");
		GameRegistry.registerTileEntity(TileGreenhouseValve.class, "forestry.GreenhouseValve");
		GameRegistry.registerTileEntity(TileGreenhouseGearbox.class, "forestry.GreenhouseGearbox");
		GameRegistry.registerTileEntity(TileGreenhouseControl.class, "forestry.GreenhouseController");
		GameRegistry.registerTileEntity(TileGreenhousePlain.class, "forestry.GreenhousePlain");
		GameRegistry.registerTileEntity(TileGreenhouseDoor.class, "forestry.GreenhouseDoor");
		GameRegistry.registerTileEntity(TileGreenhouseHatch.class, "forestry.GreenhouseHatch");
		GameRegistry.registerTileEntity(TileGreenhouseClimateControl.class, "forestry.GreenhouseClimateControl");
		GameRegistry.registerTileEntity(TileGreenhouseWindow.class, "forestry.GreenhouseWindow");
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			GameRegistry.registerTileEntity(TileGreenhouseButterflyHatch.class, "forestry.GreenhouseButterflyHatch");
		}
	}
	
	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(new EventHandlerGreenhouse());
		
		proxy.initializeModels();
		
		GreenhouseManager.greenhouseHelper.addGreenhouseLogic(GreenhouseLogicGreenhouseEffect.class);
		GreenhouseManager.greenhouseHelper.addGreenhouseLogic(GreenhouseLogicGreenhouseDoor.class);
	}
	
}
