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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.climate.ClimateType;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.greenhouse.IGreenhouseHelper;
import forestry.core.CreativeTabForestry;
import forestry.core.PluginCore;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.Circuits;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.greenhouse.blocks.BlockClimatiserType;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.BlockRegistryGreenhouse;
import forestry.greenhouse.camouflage.CamouflageAccess;
import forestry.greenhouse.camouflage.CamouflageHandlerBlock;
import forestry.greenhouse.circuits.CircuitClimateSource;
import forestry.greenhouse.climate.modifiers.AltitudeModifier;
import forestry.greenhouse.climate.modifiers.ClimateSourceModifier;
import forestry.greenhouse.climate.modifiers.TimeModifier;
import forestry.greenhouse.climate.modifiers.WeatherModifier;
import forestry.greenhouse.items.ItemRegistryGreenhouse;
import forestry.greenhouse.multiblock.GreenhouseController;
import forestry.greenhouse.multiblock.blocks.ChunkEvents;
import forestry.greenhouse.multiblock.blocks.world.GreenhouseBlockManager;
import forestry.greenhouse.network.PacketRegistryGreenhouse;
import forestry.greenhouse.proxy.ProxyGreenhouse;
import forestry.greenhouse.tiles.TileDehumidifier;
import forestry.greenhouse.tiles.TileFan;
import forestry.greenhouse.tiles.TileGreenhouseControl;
import forestry.greenhouse.tiles.TileGreenhouseGearbox;
import forestry.greenhouse.tiles.TileGreenhousePlain;
import forestry.greenhouse.tiles.TileGreenhouseScreen;
import forestry.greenhouse.tiles.TileGreenhouseWindow;
import forestry.greenhouse.tiles.TileHeater;
import forestry.greenhouse.tiles.TileHumidifier;
import forestry.greenhouse.tiles.TileHygroregulator;
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
	@Nullable
	private static ItemRegistryGreenhouse items;

	public static BlockRegistryGreenhouse getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	public static ItemRegistryGreenhouse getItems() {
		Preconditions.checkArgument(items != null);
		return items;
	}

	public static CreativeTabs getGreenhouseTab() {
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			return Tabs.tabAgriculture;
		}
		return CreativeTabForestry.tabForestry;
	}

	@Override
	public void setupAPI() {
		GreenhouseManager.greenhouseHelper = new GreenhouseHelper();
		GreenhouseManager.greenhouseBlockManager = GreenhouseBlockManager.getInstance();
		CamouflageManager.camouflageAccess = new CamouflageAccess();
	}
	
	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryGreenhouse();
		items = new ItemRegistryGreenhouse();
	}

	@Override
	public void preInit() {
		proxy.preInti();
		MinecraftForge.EVENT_BUS.register(new ChunkEvents());
		CamouflageManager.camouflageAccess.registerItemHandler(new CamouflageHandlerBlock());
		proxy.initializeModels();

		ICircuitLayout layoutManaged = new CircuitLayout("greenhouse.climatiser", CircuitSocketType.GREENHOUSE_CLIMATISER);
		ChipsetManager.circuitRegistry.registerLayout(layoutManaged);
	}
	
	@Override
	public void doInit() {
		super.doInit();
		IGreenhouseHelper helper = GreenhouseManager.greenhouseHelper;

		GameRegistry.registerTileEntity(TileGreenhousePlain.class, "forestry.GreenhousePlain");
		GameRegistry.registerTileEntity(TileFan.class, "forestry.GreenhouseFan");
		GameRegistry.registerTileEntity(TileHeater.class, "forestry.GreenhouseHeater");
		GameRegistry.registerTileEntity(TileDehumidifier.class, "forestry.GreenhouseDryer");
		GameRegistry.registerTileEntity(TileHumidifier.class, "forestry.GreenhouseSprinkler");
		GameRegistry.registerTileEntity(TileHygroregulator.class, "forestry.ClimateSourceHygroregulator");
		GameRegistry.registerTileEntity(TileGreenhouseGearbox.class, "forestry.GreenhouseGearbox");
		GameRegistry.registerTileEntity(TileGreenhouseControl.class, "forestry.GreenhouseController");
		GameRegistry.registerTileEntity(TileGreenhouseWindow.class, "forestry.ClimateSourceWindow");
		GameRegistry.registerTileEntity(TileGreenhouseScreen.class, "forestry.GreenhouseScreen");

		helper.registerWindowGlass("glass", new ItemStack(Blocks.GLASS), "blocks/glass");
		for (EnumDyeColor dye : EnumDyeColor.values()) {
			helper.registerWindowGlass("glass" + dye.getName(), new ItemStack(Blocks.STAINED_GLASS, 1, dye.getMetadata()), "blocks/glass_" + dye.getName());
		}

		helper.registerModifier(new WeatherModifier());
		helper.registerModifier(new TimeModifier());
		helper.registerModifier(new AltitudeModifier());
		helper.registerModifier(new ClimateSourceModifier());

		Circuits.greenhouseClimatiserTemperature1 = new CircuitClimateSource("climatiser.temperature.1", ClimateType.TEMPERATURE, 0.125F, 0.125F);
		Circuits.greenhouseClimatiserTemperature2 = new CircuitClimateSource("climatiser.temperature.2", ClimateType.TEMPERATURE, 0.25F, 0.25F);
		Circuits.greenhouseClimatiserHumidity1 = new CircuitClimateSource("climatiser.humidity.1", ClimateType.HUMIDITY, 0.125F, 0.125F);
		Circuits.greenhouseClimatiserHumidity2 = new CircuitClimateSource("climatiser.humidity.2", ClimateType.HUMIDITY, 0.25F, 0.25F);
		proxy.inti();
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = PluginCore.getItems();
		BlockRegistryGreenhouse blocks = getBlocks();
		ItemRegistryGreenhouse items = getItems();

		// / CAMOUFLAGE SPRAY CAN
		RecipeUtil.addRecipe("camouflage_spray_can", items.camouflageSprayCan, "TTT", "TCT", "TCT", 'T', OreDictUtil.INGOT_TIN, 'C', coreItems.craftingMaterial.getCamouflagedPaneling());

		ItemStack greenhousePlainBlock = new ItemStack(blocks.greenhouseBlock, 2, BlockGreenhouseType.PLAIN.ordinal());
		RecipeUtil.addRecipe("greenhouse_plain", greenhousePlainBlock.copy(),
			"#X#",
			"SIS",
			'I', OreDictUtil.INGOT_IRON,
			'S', OreDictUtil.SLAB_WOOD,
			'X', GreenhouseController.createDefaultCamouflageBlock(),
			'#', coreItems.craftingMaterial.getCamouflagedPaneling());
		greenhousePlainBlock.setCount(1);

		RecipeUtil.addRecipe("greenhouse_control", new ItemStack(blocks.greenhouseBlock, 1, BlockGreenhouseType.CONTROL.ordinal()),
			" X ",
			"#T#",
			'X', greenhousePlainBlock.copy(),
			'#', OreDictUtil.DUST_REDSTONE,
			'T', coreItems.tubes.get(EnumElectronTube.GOLD, 1));

		RecipeUtil.addRecipe("greenhouse_gearbox", new ItemStack(blocks.greenhouseBlock, 1, BlockGreenhouseType.GEARBOX.ordinal()),
			" X ",
			"###",
			'X', greenhousePlainBlock.copy(),
			'#', OreDictUtil.GEAR_TIN);

		RecipeUtil.addRecipe("greenhouse_hygro", new ItemStack(blocks.climatiserBlock, 1, BlockClimatiserType.HYGRO.ordinal()),
			"GIG",
			"GXG",
			"GIG",
			'X', greenhousePlainBlock.copy(),
			'I', OreDictUtil.INGOT_IRON,
			'G', OreDictUtil.BLOCK_GLASS);

		RecipeUtil.addRecipe("greenhouse_heater", new ItemStack(blocks.climatiserBlock, 1, BlockClimatiserType.HEATER.ordinal()),
			"T#T",
			"#X#",
			"T#T",
			'X', greenhousePlainBlock.copy(),
			'#', OreDictUtil.INGOT_TIN,
			'T', coreItems.tubes.get(EnumElectronTube.GOLD, 1));

		RecipeUtil.addRecipe("greenhouse_fan", new ItemStack(blocks.climatiserBlock, 1, BlockClimatiserType.FAN.ordinal()),
			"T#T",
			"#X#",
			"T#T",
			'X', greenhousePlainBlock.copy(),
			'#', OreDictUtil.INGOT_IRON,
			'T', coreItems.tubes.get(EnumElectronTube.TIN, 1));

		RecipeUtil.addRecipe("greenhouse_dehumidifier", new ItemStack(blocks.climatiserBlock, 1, BlockClimatiserType.DEHUMIDIFIER.ordinal()),
			"T#T",
			"#X#",
			"T#T",
			'X', greenhousePlainBlock.copy(),
			'#', OreDictUtil.INGOT_TIN,
			'T', coreItems.tubes.get(EnumElectronTube.BLAZE, 1));

		RecipeUtil.addRecipe("greenhouse_humidifier", new ItemStack(blocks.climatiserBlock, 1, BlockClimatiserType.HUMIDIFIER.ordinal()),
			"T#T",
			"#X#",
			"T#T",
			'X', greenhousePlainBlock.copy(),
			'#', OreDictUtil.INGOT_TIN,
			'T', coreItems.tubes.get(EnumElectronTube.LAPIS, 1));

		for (String glassName : GreenhouseManager.greenhouseHelper.getWindowGlasses()) {
			ItemStack glassItem = GreenhouseManager.greenhouseHelper.getGlassItem(glassName);
			ItemStack window = blocks.window.getItem(glassName);
			ItemStack roodWindow = blocks.roofWindow.getItem(glassName);
			RecipeUtil.addRecipe("greenhouse_window_" + glassName, roodWindow,
				true,
				"SGS",
				"GGG",
				"GGG",
				'G', glassItem,
				'S', OreDictUtil.STICK_WOOD);

			RecipeUtil.addRecipe("greenhouse_window_roof_" + glassName, window,
				true,
				"SGG",
				"GGG",
				"SGG",
				'G', glassItem,
				'S', OreDictUtil.STICK_WOOD);
		}

		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.greenhouse.climatiser");
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.GOLD, 1), Circuits.greenhouseClimatiserTemperature1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.BLAZE, 1), Circuits.greenhouseClimatiserTemperature2);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.LAPIS, 1), Circuits.greenhouseClimatiserHumidity1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.OBSIDIAN, 1), Circuits.greenhouseClimatiserHumidity2);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryGreenhouse();
	}

}
