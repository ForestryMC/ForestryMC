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

import java.util.Arrays;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.network.IGuiHandler;

import forestry.api.recipes.RecipeManagers;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.gadgets.MachineNBTDefinition;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemNBTTile;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.factory.GuiHandlerFactory;
import forestry.factory.gadgets.MachineBottler;
import forestry.factory.gadgets.MachineCarpenter;
import forestry.factory.gadgets.MachineCentrifuge;
import forestry.factory.gadgets.MachineFabricator;
import forestry.factory.gadgets.MachineFermenter;
import forestry.factory.gadgets.MachineMoistener;
import forestry.factory.gadgets.MachineRaintank;
import forestry.factory.gadgets.MachineSqueezer;
import forestry.factory.gadgets.MachineStill;
import forestry.factory.gadgets.MillRainmaker;
import forestry.factory.gadgets.TileWorktable;
import forestry.factory.recipes.CraftGuideIntegration;
import forestry.factory.triggers.FactoryTriggers;


@Plugin(pluginID = "Factory", name = "Factory", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.factory.description")
public class PluginFactory extends ForestryPlugin {

	public static MachineDefinition definitionBottler;
	public static MachineDefinition definitionCarpenter;
	public static MachineDefinition definitionCentrifuge;
	public static MachineDefinition definitionFermenter;
	public static MachineDefinition definitionMoistener;
	public static MachineDefinition definitionSqueezer;
	public static MachineDefinition definitionStill;
	public static MachineDefinition definitionRainmaker;
	public static MachineDefinition definitionFabricator;
	public static MachineDefinition definitionRaintank;
	public static MachineDefinition definitionWorktable;

	@Override
	public void preInit() {
		super.preInit();

		RecipeManagers.craftingProviders = Arrays.asList(
			RecipeManagers.carpenterManager = new MachineCarpenter.RecipeManager(),
			RecipeManagers.centrifugeManager = new MachineCentrifuge.RecipeManager(),
			RecipeManagers.fabricatorManager = new MachineFabricator.RecipeManager(),
			RecipeManagers.fermenterManager = new MachineFermenter.RecipeManager(),
			RecipeManagers.moistenerManager = new MachineMoistener.RecipeManager(),
			RecipeManagers.squeezerManager = new MachineSqueezer.RecipeManager(),
			RecipeManagers.stillManager = new MachineStill.RecipeManager()
		);

		ForestryBlock.factoryTESR.registerBlock(new BlockBase(Material.iron, true), ItemForestryBlock.class, "factory");

		BlockBase factoryTESR = ((BlockBase) ForestryBlock.factoryTESR.block());

		definitionBottler = factoryTESR.addDefinition(new MachineDefinition(Defaults.DEFINITION_BOTTLER_META, "forestry.Bottler", MachineBottler.class,
				Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/bottler_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_BOTTLER_META),
						"X#X",
						"#Y#",
						"X#X",
						'#', Blocks.glass,
						'X', ForestryItem.canEmpty,
						'Y', ForestryItem.sturdyCasing)));

		definitionCarpenter = factoryTESR.addDefinition(new MachineDefinition(Defaults.DEFINITION_CARPENTER_META, "forestry.Carpenter", MachineCarpenter.class,
				Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/carpenter_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_CARPENTER_META),
						"X#X",
						"XYX",
						"X#X",
						'#', Blocks.glass,
						'X', "ingotBronze",
						'Y', ForestryItem.sturdyCasing)));

		definitionCentrifuge = factoryTESR.addDefinition(new MachineDefinition(Defaults.DEFINITION_CENTRIFUGE_META, "forestry.Centrifuge", MachineCentrifuge.class,
				Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/centrifuge_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_CENTRIFUGE_META),
						"X#X",
						"XYX",
						"X#X",
						'#', Blocks.glass,
						'X', "ingotCopper",
						'Y', ForestryItem.sturdyCasing.getItemStack())));

		definitionFermenter = factoryTESR.addDefinition(new MachineDefinition(Defaults.DEFINITION_FERMENTER_META, "forestry.Fermenter", MachineFermenter.class,
				Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/fermenter_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_FERMENTER_META),
						"X#X",
						"#Y#",
						"X#X",
						'#', Blocks.glass,
						'X', "gearBronze",
						'Y', ForestryItem.sturdyCasing)));

		definitionMoistener = factoryTESR.addDefinition(new MachineDefinition(Defaults.DEFINITION_MOISTENER_META, "forestry.Moistener", MachineMoistener.class,
				Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/moistener_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_MOISTENER_META),
						"X#X",
						"#Y#",
						"X#X",
						'#', Blocks.glass,
						'X', "gearCopper",
						'Y', ForestryItem.sturdyCasing)));

		definitionSqueezer = factoryTESR.addDefinition(new MachineDefinition(Defaults.DEFINITION_SQUEEZER_META, "forestry.Squeezer", MachineSqueezer.class,
				Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/squeezer_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_SQUEEZER_META),
						"X#X",
						"XYX",
						"X#X",
						'#', Blocks.glass,
						'X', "ingotTin",
						'Y', ForestryItem.sturdyCasing.getItemStack())));

		definitionStill = factoryTESR.addDefinition(new MachineDefinition(Defaults.DEFINITION_STILL_META, "forestry.Still", MachineStill.class,
				Proxies.render.getRenderDefaultMachine(Defaults.TEXTURE_PATH_BLOCKS + "/still_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_STILL_META),
						"X#X",
						"#Y#",
						"X#X",
						'#', Blocks.glass,
						'X', Items.redstone,
						'Y', ForestryItem.sturdyCasing)));

		definitionRainmaker = factoryTESR.addDefinition(new MachineDefinition(Defaults.DEFINITION_RAINMAKER_META, "forestry.Rainmaker", MillRainmaker.class,
				Proxies.render.getRenderMill(Defaults.TEXTURE_PATH_BLOCKS + "/rainmaker_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryTESR.getItemStack(1, Defaults.DEFINITION_RAINMAKER_META),
						"X#X",
						"#Y#",
						"X#X",
						'#', Blocks.glass,
						'X', "gearTin",
						'Y', ForestryItem.hardenedCasing)));

		ForestryBlock.factoryPlain.registerBlock(new BlockBase(Material.iron), ItemNBTTile.class, "factory2");

		BlockBase factoryPlain = ((BlockBase) ForestryBlock.factoryPlain.block());

		definitionFabricator = factoryPlain.addDefinition(new MachineDefinition(Defaults.DEFINITION_FABRICATOR_META, "forestry.Fabricator", MachineFabricator.class,
				ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryPlain.getItemStack(1, Defaults.DEFINITION_FABRICATOR_META),
						"X#X",
						"#Y#",
						"XZX",
						'#', Blocks.glass,
						'X', Items.gold_ingot,
						'Y', ForestryItem.sturdyCasing,
						'Z', Blocks.chest))
				.setFaces(0, 1, 2, 3, 4, 4));

		definitionRaintank = factoryPlain.addDefinition(new MachineDefinition(Defaults.DEFINITION_RAINTANK_META, "forestry.Raintank", MachineRaintank.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.factoryPlain.getItemStack(1, Defaults.DEFINITION_RAINTANK_META),
						"X#X",
						"XYX",
						"X#X",
						'#', Blocks.glass,
						'X', Items.iron_ingot,
						'Y', ForestryItem.sturdyCasing))
				.setFaces(0, 1, 0, 0, 0, 0));

		definitionWorktable = factoryPlain.addDefinition(new MachineNBTDefinition(Defaults.DEFINITION_WORKTABLE_META, "forestry.Worktable", TileWorktable.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.factoryPlain.getItemStack(1, Defaults.DEFINITION_WORKTABLE_META),
						"B",
						"W",
						"C",
						'B', Items.book,
						'W', Blocks.crafting_table,
						'C', Blocks.chest))
				.setFaces(0, 1, 2, 3, 4, 4));

		// Triggers
		if (PluginManager.Module.BUILDCRAFT_STATEMENTS.isEnabled()) {
			FactoryTriggers.initialize();
		}
	}

	@Override
	public void doInit() {
		super.doInit();

		definitionBottler.register();
		definitionCarpenter.register();
		definitionCentrifuge.register();
		definitionFabricator.register();
		definitionFermenter.register();
		definitionMoistener.register();
		definitionRaintank.register();
		definitionSqueezer.register();
		definitionStill.register();
		definitionRainmaker.register();
		definitionWorktable.register();

	}

	@Override
	public void postInit() {
		super.postInit();

		if (Proxies.common.isModLoaded("craftguide"))
			CraftGuideIntegration.register();
		else
			Proxies.log.info("Skipping CraftGuide integration.");
	}

	@Override
	protected void registerItems() {
	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerRecipes() {

		// / FABRICATOR
		
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 0), new Object[]{
			" X ", "#X#", "XXX", '#', Items.redstone, 'X', "ingotCopper"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 1), new Object[]{
			" X ", "#X#", "XXX", '#', Items.redstone, 'X', "ingotTin"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 2), new Object[]{
			" X ", "#X#", "XXX", '#', Items.redstone, 'X', "ingotBronze"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 3), new Object[]{
			" X ", "#X#", "XXX", '#', Items.redstone, 'X', Items.iron_ingot});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 4), new Object[]{
			" X ", "#X#", "XXX", '#', Items.redstone, 'X', Items.gold_ingot});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 5), new Object[]{
			" X ", "#X#", "XXX", '#', Items.redstone, 'X', Items.diamond});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 6), new Object[]{
			" X ", "#X#", "XXX", '#', Items.redstone, 'X', Blocks.obsidian});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 7), new Object[]{
			" X ", "#X#", "XXX", '#', Items.redstone, 'X', Items.blaze_powder});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 9), new Object[]{
			" X ", "#X#", "XXX", '#', Items.redstone, 'X', Items.emerald});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 10),
				new Object[]{" X ", "#X#", "XXX", '#', Items.redstone, 'X', "gemApatite"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.get(500), ForestryItem.tubes.getItemStack(4, 11),
				new Object[]{" X ", "#X#", "XXX", '#', Items.redstone, 'X', new ItemStack(Items.dye, 1, 4)});

		String[] dyes = {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime",
			"dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			FluidStack liquidGlass = Fluids.GLASS.get(Defaults.BUCKET_VOLUME);
			for (int i = 0; i < 16; i++)
				RecipeManagers.fabricatorManager.addRecipe(ForestryItem.waxCast.getItemStack(1, Defaults.WILDCARD), liquidGlass, ForestryBlock.glass.getItemStack(1, 15 - i), new Object[]{"#", "X", '#', dyes[i],
						'X', ForestryItem.propolis.getItemStack(1, Defaults.WILDCARD)});
		}

		// / SQUEEZER
		int appleMulchAmount = GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple");
		int appleJuiceAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple");
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.apple)}, Fluids.JUICE.get(appleJuiceAmount),
				ForestryItem.mulch.getItemStack(), appleMulchAmount);

		int seedOilAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedOil = Fluids.SEEDOIL.get(seedOilAmount);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.wheat_seeds)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.pumpkin_seeds)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.melon_seeds)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.phosphor.getItemStack(2), new ItemStack(Blocks.cobblestone)}, Fluids.LAVA.get(1600));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Blocks.cactus)}, Fluids.WATER.get(500));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.snowball), ForestryItem.craftingMaterial.getItemStack(4, 5)}, Fluids.ICE.get(4000));

		// STILL
		RecipeManagers.stillManager.addRecipe(Defaults.STILL_DESTILLATION_DURATION, Fluids.BIOMASS.get(Defaults.STILL_DESTILLATION_INPUT),
				Fluids.BIOETHANOL.get(Defaults.STILL_DESTILLATION_OUTPUT));

		// MOISTENER
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Items.wheat_seeds), new ItemStack(Blocks.mycelium), 5000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.mossy_cobblestone), 20000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 1), 20000);

		// FERMENTER
		for (int i = 0; i < 4; i++)
			RecipeUtil.injectLeveledRecipe(new ItemStack(Blocks.sapling, 1, i), GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);

		RecipeUtil.injectLeveledRecipe(new ItemStack(Blocks.cactus), GameMode.getGameMode().getIntegerSetting("fermenter.yield.cactus"), Fluids.BIOMASS);
		RecipeUtil.injectLeveledRecipe(new ItemStack(Items.wheat), GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		RecipeUtil.injectLeveledRecipe(new ItemStack(Items.reeds), GameMode.getGameMode().getIntegerSetting("fermenter.yield.cane"), Fluids.BIOMASS);
		RecipeUtil.injectLeveledRecipe(new ItemStack(Blocks.brown_mushroom), GameMode.getGameMode().getIntegerSetting("fermenter.yield.mushroom"), Fluids.BIOMASS);
		RecipeUtil.injectLeveledRecipe(new ItemStack(Blocks.red_mushroom), GameMode.getGameMode().getIntegerSetting("fermenter.yield.mushroom"), Fluids.BIOMASS);

		// FABRICATOR

		RecipeManagers.fabricatorManager.addSmelting(new ItemStack(Blocks.glass), Fluids.GLASS.get(1000), 1000);
		RecipeManagers.fabricatorManager.addSmelting(new ItemStack(Blocks.glass_pane), Fluids.GLASS.get(375), 1000);
		RecipeManagers.fabricatorManager.addSmelting(new ItemStack(Blocks.sand), Fluids.GLASS.get(1000), 3000);

		// / CARPENTER
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEEDOIL.get(250), null, ForestryItem.impregnatedCasing.getItemStack(),
				"###",
				"# #",
				"###",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEEDOIL.get(500), null,
				ForestryBlock.core.getItemStack(1, Defaults.DEFINITION_ESCRITOIRE_META),
				"#  ",
				"###",
				"# #",
				'#', "plankWood");

		// RESOURCES
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEEDOIL.get(100), null,
				ForestryItem.stickImpregnated.getItemStack(2),
				"#",
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.get(250), null,
				ForestryItem.woodPulp.getItemStack(4),
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.get(250), null,
				new ItemStack(Items.paper, 1),
				"#",
				"#",
				'#', "pulpWood");
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.get(1000), null,
				ForestryBlock.soil.getItemStack(9),
				"###",
				"#X#",
				"###",
				'#', Blocks.dirt,
				'X', ForestryItem.mulch);
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.get(1000), null,
				ForestryBlock.soil.getItemStack(8, 1),
				"#X#",
				"XYX", "#X#",
				'#', Blocks.dirt,
				'X', Blocks.sand,
				'Y', ForestryItem.mulch);
		RecipeManagers.carpenterManager.addRecipe(75, Fluids.WATER.get(5000), null, ForestryItem.hardenedCasing.getItemStack(),
				"# #",
				" Y ",
				"# #",
				'#', Items.diamond,
				'Y', ForestryItem.sturdyCasing);

		// / CHIPSETS
		RecipeManagers.carpenterManager.addRecipe(20, Fluids.WATER.get(1000), null, ForestryItem.circuitboards.getItemStack(1, 0),
				"R R", "R#R", "R R", '#', "ingotTin", 'R', Items.redstone);
		RecipeManagers.carpenterManager.addRecipe(40, Fluids.WATER.get(1000), null, ForestryItem.circuitboards.getItemStack(1, 1),
				"R#R", "R#R", "R#R", '#', "ingotBronze", 'R', Items.redstone);
		RecipeManagers.carpenterManager.addRecipe(80, Fluids.WATER.get(1000), null, ForestryItem.circuitboards.getItemStack(1, 2),
				"R#R", "R#R", "R#R", '#', Items.iron_ingot, 'R', Items.redstone);
		RecipeManagers.carpenterManager.addRecipe(80, Fluids.WATER.get(1000), null, ForestryItem.circuitboards.getItemStack(1, 3),
				"R#R", "R#R", "R#R", '#', Items.gold_ingot, 'R', Items.redstone);
		RecipeManagers.carpenterManager.addRecipe(40, Fluids.WATER.get(1000), null, ForestryItem.solderingIron.getItemStack(),
				" # ", "# #", "  B", '#', Items.iron_ingot, 'B', "ingotBronze");
		// ForestryCore.oreHandler.registerCarpenterRecipe(solderingIron);

		// RAIN SUBSTRATES
		if (PluginManager.Module.APICULTURE.isEnabled()) {
			RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.get(1000), null, ForestryItem.iodineCharge.getItemStack(),
					"Z#Z",
					"#Y#",
					"X#X",
					'#', ForestryItem.pollenCluster,
					'X', Items.gunpowder,
					'Y', ForestryItem.canEmpty,
					'Z', ForestryItem.honeyDrop);
			// ForestryCore.oreHandler.registerCarpenterRecipe(iodineCapsule);
			RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.get(1000), null, ForestryItem.craftingMaterial.getItemStack(1, 4),
					"Z#Z",
					"#Y#",
					"X#X",
					'#', ForestryItem.royalJelly,
					'X', Items.gunpowder,
					'Y', ForestryItem.canEmpty,
					'Z', ForestryItem.honeydew);
			// ForestryCore.oreHandler.registerCarpenterRecipe(dissipationCharge);
		}

		// Ender pearl
		RecipeManagers.carpenterManager.addRecipe(100, null, new ItemStack(Items.ender_pearl, 1), " # ", "###", " # ", '#',
				ForestryItem.craftingMaterial.getItemStack(1, 1));
		// Woven Silk
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.WATER.get(500), null, ForestryItem.craftingMaterial.getItemStack(1, 3),
				"###", "###", "###", '#', ForestryItem.craftingMaterial.getItemStack(1, 2));

		// Boxes
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.get(1000), null, ForestryItem.carton.getItemStack(2),
				" # ", "# #", " # ", '#', "pulpWood");
		RecipeManagers.carpenterManager.addRecipe(20, Fluids.WATER.get(1000), null, ForestryItem.crate.getItemStack(24),
				" # ", "# #", " # ", '#', "logWood");

		// Assembly Kits
		RecipeManagers.carpenterManager.addRecipe(20, null, ForestryItem.carton.getItemStack(), ForestryItem.kitPickaxe.getItemStack(), new Object[]{
			"###",
			" X ",
			" X ",
			'#', "ingotBronze",
			'X', "stickWood"});

		RecipeManagers.carpenterManager.addRecipe(20, null, ForestryItem.carton.getItemStack(), ForestryItem.kitShovel.getItemStack(),
				new Object[]{" # ", " X ", " X ", '#', "ingotBronze", 'X', "stickWood"});

		// Reclamation
		RecipeManagers.carpenterManager.addRecipe(null, ForestryItem.ingotBronze.getItemStack(2, ForestryItem.ingotBronze.item().getMaxDamage()),
				"#", '#', ForestryItem.brokenBronzePickaxe);
		RecipeManagers.carpenterManager.addRecipe(null, ForestryItem.ingotBronze.getItemStack(), "#", '#', ForestryItem.brokenBronzeShovel);

		// Crating and uncrating condensed
		if (PluginManager.Module.STORAGE.isEnabled()) {
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedWood.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedCobblestone.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedDirt.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedStone.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedBrick.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedCacti.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedSand.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedObsidian.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedNetherrack.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedSoulsand.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedSandstone.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedBogearth.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedHumus.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedNetherbrick.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedPeat.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedApatite.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedFertilizer.getItemStack());
			RecipeManagers.carpenterManager.addCrating("ingotTin", ForestryItem.ingotTin.getItemStack(), ForestryItem.cratedTin.getItemStack());
			RecipeManagers.carpenterManager.addCrating("ingotCopper", ForestryItem.ingotCopper.getItemStack(), ForestryItem.cratedCopper.getItemStack());
			RecipeManagers.carpenterManager.addCrating("ingotBronze", ForestryItem.ingotBronze.getItemStack(), ForestryItem.cratedBronze.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedWheat.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedMycelium.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedMulch.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedCookies.getItemStack());

			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedRedstone.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedLapis.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedReeds.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedClay.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedGlowstone.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedApples.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedNetherwart.getItemStack());

			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedPhosphor.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedAsh.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedCharcoal.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedGravel.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedCoal.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedSeeds.getItemStack());
			RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedSaplings.getItemStack());

			if (PluginManager.Module.APICULTURE.isEnabled()) {
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedHoneycombs.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedBeeswax.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedPollen.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedPropolis.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedHoneydew.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedRoyalJelly.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedCocoaComb.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedSimmeringCombs.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedStringyCombs.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedFrozenCombs.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedDrippingCombs.getItemStack());
				RecipeManagers.carpenterManager.addCrating(ForestryItem.cratedRefractoryWax.getItemStack());
			}
		}
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerFactory();
	}

}
