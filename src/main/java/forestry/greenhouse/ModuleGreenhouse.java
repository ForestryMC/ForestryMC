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
import forestry.api.core.Tabs;
import forestry.api.modules.ForestryModule;
import forestry.core.CreativeTabForestry;
import forestry.core.ModuleCore;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.Circuits;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.greenhouse.api.climate.IGreenhouseClimateManager;
import forestry.greenhouse.api.greenhouse.GreenhouseManager;
import forestry.greenhouse.api.greenhouse.IGreenhouseHelper;
import forestry.greenhouse.blocks.BlockClimatiserType;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.BlockRegistryGreenhouse;
import forestry.greenhouse.camouflage.CamouflageAccess;
import forestry.greenhouse.camouflage.CamouflageHandlerBlock;
import forestry.greenhouse.circuits.CircuitClimateSource;
import forestry.greenhouse.climate.GreenhouseClimateManager;
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
import forestry.greenhouse.tiles.TileGreenhouseWindow;
import forestry.greenhouse.tiles.TileHeater;
import forestry.greenhouse.tiles.TileHumidifier;
import forestry.greenhouse.tiles.TileHygroregulator;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.GREENHOUSE, name = "Greenhouse", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.greenhouse.description")
public class ModuleGreenhouse extends BlankForestryModule {

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
		if (ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
			return Tabs.tabAgriculture;
		}
		return CreativeTabForestry.tabForestry;
	}

	@Override
	public void setupAPI() {
		GreenhouseManager.helper = new GreenhouseHelper();
		GreenhouseManager.blockManager = GreenhouseBlockManager.getInstance();
		GreenhouseManager.climateManager = GreenhouseClimateManager.getInstance();
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
		IGreenhouseHelper helper = GreenhouseManager.helper;

		GameRegistry.registerTileEntity(TileGreenhousePlain.class, "forestry.GreenhousePlain");
		GameRegistry.registerTileEntity(TileHygroregulator.class, "forestry.ClimateSourceHygroregulator");
		GameRegistry.registerTileEntity(TileGreenhouseGearbox.class, "forestry.GreenhouseGearbox");
		GameRegistry.registerTileEntity(TileGreenhouseControl.class, "forestry.GreenhouseController");
		GameRegistry.registerTileEntity(TileGreenhouseWindow.class, "forestry.ClimateSourceWindow");
		GameRegistry.registerTileEntity(TileFan.class, "forestry.GreenhouseFan");
		GameRegistry.registerTileEntity(TileHeater.class, "forestry.GreenhouseHeater");
		GameRegistry.registerTileEntity(TileDehumidifier.class, "forestry.GreenhouseDryer");
		GameRegistry.registerTileEntity(TileHumidifier.class, "forestry.GreenhouseSprinkler");

		helper.registerWindowGlass("glass", new ItemStack(Blocks.GLASS), "blocks/glass");
		for (EnumDyeColor dye : EnumDyeColor.values()) {
			helper.registerWindowGlass("glass" + dye.getName(), new ItemStack(Blocks.STAINED_GLASS, 1, dye.getMetadata()), "blocks/glass_" + dye.getName());
		}

		IGreenhouseClimateManager climateSourceManager = GreenhouseClimateManager.getInstance();
		climateSourceManager.registerModifier(new WeatherModifier());
		climateSourceManager.registerModifier(new TimeModifier());
		climateSourceManager.registerModifier(new AltitudeModifier());
		climateSourceManager.registerModifier(new ClimateSourceModifier());

		Circuits.climatiserTemperature1 = new CircuitClimateSource("climatiser.temperature.1", ClimateType.TEMPERATURE, 0.125F, 0.125F, 0.25F);
		Circuits.climatiserTemperature2 = new CircuitClimateSource("climatiser.temperature.2", ClimateType.TEMPERATURE, 0.25F, 0.25F, 0.5F);
		Circuits.climatiserHumidity1 = new CircuitClimateSource("climatiser.humidity.1", ClimateType.HUMIDITY, 0.125F, 0.125F, 0.25F);
		Circuits.climatiserHumidity2 = new CircuitClimateSource("climatiser.humidity.2", ClimateType.HUMIDITY, 0.25F, 0.25F, 0.5F);
		proxy.inti();
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
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

		for (String glassName : GreenhouseManager.helper.getWindowGlasses()) {
			ItemStack glassItem = GreenhouseManager.helper.getGlassItem(glassName);
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
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.GOLD, 1), Circuits.climatiserTemperature1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.BLAZE, 1), Circuits.climatiserTemperature2);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.LAPIS, 1), Circuits.climatiserHumidity1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.OBSIDIAN, 1), Circuits.climatiserHumidity2);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryGreenhouse();
	}

}
