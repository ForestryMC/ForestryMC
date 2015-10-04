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

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.network.IGuiHandler;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.recipes.RecipeManagers;
import forestry.core.blocks.BlockBase;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitLayout;
import forestry.core.config.Constants;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.GameMode;
import forestry.core.fluids.Fluids;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemBlockNBT;
import forestry.core.network.IPacketHandler;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.recipes.craftguide.CraftGuideIntegration;
import forestry.core.tiles.MachineDefinition;
import forestry.core.utils.Log;
import forestry.factory.DummyManagers;
import forestry.factory.GuiHandlerFactory;
import forestry.factory.circuits.CircuitSpeedUpgrade;
import forestry.factory.network.PacketHandlerFactory;
import forestry.factory.tiles.TileBottler;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileCentrifuge;
import forestry.factory.tiles.TileFabricator;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMillRainmaker;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileRaintank;
import forestry.factory.tiles.TileSqueezer;
import forestry.factory.tiles.TileStill;
import forestry.factory.tiles.TileWorktable;
import forestry.factory.triggers.FactoryTriggers;


@Plugin(pluginID = "Factory", name = "Factory", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.factory.description")
public class PluginFactory extends ForestryPlugin {

	private static MachineDefinition definitionBottler;
	private static MachineDefinition definitionCarpenter;
	private static MachineDefinition definitionCentrifuge;
	private static MachineDefinition definitionFermenter;
	private static MachineDefinition definitionMoistener;
	private static MachineDefinition definitionSqueezer;
	private static MachineDefinition definitionStill;
	private static MachineDefinition definitionRainmaker;
	private static MachineDefinition definitionFabricator;
	private static MachineDefinition definitionRaintank;
	private static MachineDefinition definitionWorktable;

	@Override
	protected void setupAPI() {
		super.setupAPI();

		RecipeManagers.craftingProviders = ImmutableList.of(
				RecipeManagers.carpenterManager = new TileCarpenter.RecipeManager(),
				RecipeManagers.centrifugeManager = new TileCentrifuge.RecipeManager(),
				RecipeManagers.fabricatorManager = new TileFabricator.RecipeManager(),
				RecipeManagers.fermenterManager = new TileFermenter.RecipeManager(),
				RecipeManagers.moistenerManager = new TileMoistener.RecipeManager(),
				RecipeManagers.squeezerManager = new TileSqueezer.RecipeManager(),
				RecipeManagers.stillManager = new TileStill.RecipeManager()
		);
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerFactory();
	}

	@Override
	protected void disabledSetupAPI() {
		super.disabledSetupAPI();

		RecipeManagers.craftingProviders = ImmutableList.of(
				RecipeManagers.carpenterManager = new DummyManagers.CarpenterManager(),
				RecipeManagers.centrifugeManager = new DummyManagers.CentrifugeManager(),
				RecipeManagers.fabricatorManager = new DummyManagers.FabricatorManager(),
				RecipeManagers.fermenterManager = new DummyManagers.FermenterManager(),
				RecipeManagers.moistenerManager = new DummyManagers.MoistenerManager(),
				RecipeManagers.squeezerManager = new DummyManagers.SqueezerManager(),
				RecipeManagers.stillManager = new DummyManagers.StillManager()
		);
	}

	@Override
	public void preInit() {
		super.preInit();

		ForestryBlock.factoryTESR.registerBlock(new BlockBase(Material.iron, true), ItemBlockForestry.class, "factory");

		BlockBase factoryTESR = ((BlockBase) ForestryBlock.factoryTESR.block());

		definitionBottler = factoryTESR.addDefinition(new MachineDefinition(Constants.DEFINITION_BOTTLER_META, "forestry.Bottler", TileBottler.class,
				Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + "/bottler_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.factoryTESR.getItemStack(1, Constants.DEFINITION_BOTTLER_META),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', ForestryItem.canEmpty,
				'Y', ForestryItem.sturdyCasing)));

		definitionCarpenter = factoryTESR.addDefinition(new MachineDefinition(Constants.DEFINITION_CARPENTER_META, "forestry.Carpenter", TileCarpenter.class,
				Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + "/carpenter_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.factoryTESR.getItemStack(1, Constants.DEFINITION_CARPENTER_META),
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotBronze",
				'Y', ForestryItem.sturdyCasing)));

		definitionCentrifuge = factoryTESR.addDefinition(new MachineDefinition(Constants.DEFINITION_CENTRIFUGE_META, "forestry.Centrifuge", TileCentrifuge.class,
				Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + "/centrifuge_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.factoryTESR.getItemStack(1, Constants.DEFINITION_CENTRIFUGE_META),
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotCopper",
				'Y', ForestryItem.sturdyCasing.getItemStack())));

		definitionFermenter = factoryTESR.addDefinition(new MachineDefinition(Constants.DEFINITION_FERMENTER_META, "forestry.Fermenter", TileFermenter.class,
				Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + "/fermenter_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.factoryTESR.getItemStack(1, Constants.DEFINITION_FERMENTER_META),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearBronze",
				'Y', ForestryItem.sturdyCasing)));

		definitionMoistener = factoryTESR.addDefinition(new MachineDefinition(Constants.DEFINITION_MOISTENER_META, "forestry.Moistener", TileMoistener.class,
				Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + "/moistener_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.factoryTESR.getItemStack(1, Constants.DEFINITION_MOISTENER_META),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearCopper",
				'Y', ForestryItem.sturdyCasing)));

		definitionSqueezer = factoryTESR.addDefinition(new MachineDefinition(Constants.DEFINITION_SQUEEZER_META, "forestry.Squeezer", TileSqueezer.class,
				Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + "/squeezer_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.factoryTESR.getItemStack(1, Constants.DEFINITION_SQUEEZER_META),
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotTin",
				'Y', ForestryItem.sturdyCasing.getItemStack())));

		definitionStill = factoryTESR.addDefinition(new MachineDefinition(Constants.DEFINITION_STILL_META, "forestry.Still", TileStill.class,
				Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + "/still_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.factoryTESR.getItemStack(1, Constants.DEFINITION_STILL_META),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "dustRedstone",
				'Y', ForestryItem.sturdyCasing)));

		definitionRainmaker = factoryTESR.addDefinition(new MachineDefinition(Constants.DEFINITION_RAINMAKER_META, "forestry.Rainmaker", TileMillRainmaker.class,
				Proxies.render.getRenderMill(Constants.TEXTURE_PATH_BLOCKS + "/rainmaker_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.factoryTESR.getItemStack(1, Constants.DEFINITION_RAINMAKER_META),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearTin",
				'Y', ForestryItem.hardenedCasing)));

		ForestryBlock.factoryPlain.registerBlock(new BlockBase(Material.iron), ItemBlockNBT.class, "factory2");

		BlockBase factoryPlain = ((BlockBase) ForestryBlock.factoryPlain.block());

		definitionFabricator = factoryPlain.addDefinition(new MachineDefinition(Constants.DEFINITION_FABRICATOR_META, "forestry.Fabricator", TileFabricator.class,
				ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.factoryPlain.getItemStack(1, Constants.DEFINITION_FABRICATOR_META),
						"X#X",
						"#Y#",
						"XZX",
						'#', "blockGlass",
						'X', "ingotGold",
						'Y', ForestryItem.sturdyCasing,
						'Z', "chestWood"))
				.setFaces(0, 1, 2, 3, 4, 4));

		definitionRaintank = factoryPlain.addDefinition(new MachineDefinition(Constants.DEFINITION_RAINTANK_META, "forestry.Raintank", TileRaintank.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.factoryPlain.getItemStack(1, Constants.DEFINITION_RAINTANK_META),
						"X#X",
						"XYX",
						"X#X",
						'#', "blockGlass",
						'X', "ingotIron",
						'Y', ForestryItem.sturdyCasing))
				.setFaces(0, 1, 0, 0, 0, 0));

		definitionWorktable = factoryPlain.addDefinition(new MachineDefinition(Constants.DEFINITION_WORKTABLE_META, "forestry.Worktable", TileWorktable.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.factoryPlain.getItemStack(1, Constants.DEFINITION_WORKTABLE_META),
						"B",
						"W",
						"C",
						'B', Items.book,
						'W', "craftingTableWood",
						'C', "chestWood"))
				.setFaces(0, 1, 2, 3, 4, 4));

		ICircuitLayout layoutMachineUpgrade = new CircuitLayout("machine.upgrade", CircuitSocketType.MACHINE);
		ChipsetManager.circuitRegistry.registerLayout(layoutMachineUpgrade);

	}

	@Override
	protected void registerTriggers() {
		FactoryTriggers.initialize();
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

		Circuit.machineSpeedUpgrade1 = new CircuitSpeedUpgrade("machine.speed.boost.1", 0.125f, 0.05f, 4);
		Circuit.machineSpeedUpgrade2 = new CircuitSpeedUpgrade("machine.speed.boost.2", 0.250f, 0.10f, 4);
		Circuit.machineEfficiencyUpgrade1 = new CircuitSpeedUpgrade("machine.efficiency.1", 0, -0.10f, 2);
	}

	@Override
	public void postInit() {
		super.postInit();

		if (Proxies.common.isModLoaded("craftguide")) {
			CraftGuideIntegration.register();
		} else {
			Log.info("Skipping CraftGuide integration.");
		}
	}

	@Override
	protected void registerRecipes() {

		// / FABRICATOR
		
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 0), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotCopper"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 1), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotTin"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 2), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotBronze"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 3), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotIron"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotGold"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 5), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "gemDiamond"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 6), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', Blocks.obsidian});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 7), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', Items.blaze_powder});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 9), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "gemEmerald"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 10),
				new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "gemApatite"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 11),
				new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', new ItemStack(Items.dye, 1, 4)});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), ForestryItem.tubes.getItemStack(4, 12),
				new Object[]{" X ", "#X#", "XXX", '#', new ItemStack(Items.ender_eye, 1, 0), 'X', new ItemStack(Blocks.end_stone, 1, 0)});

		String[] dyes = {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime",
				"dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			FluidStack liquidGlass = Fluids.GLASS.getFluid(Constants.BUCKET_VOLUME);
			for (int i = 0; i < 16; i++) {
				RecipeManagers.fabricatorManager.addRecipe(ForestryItem.waxCast.getItemStack(1, OreDictionary.WILDCARD_VALUE), liquidGlass, new ItemStack(Blocks.stained_glass, 4, 15 - i), new Object[]{"#", "X", '#', dyes[i],
						'X', ForestryItem.propolis.getItemStack(1, OreDictionary.WILDCARD_VALUE)});
			}
		}

		// / SQUEEZER
		int appleMulchAmount = GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple");
		int appleJuiceAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple");
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.apple)}, Fluids.JUICE.getFluid(appleJuiceAmount),
				ForestryItem.mulch.getItemStack(), appleMulchAmount);

		int seedOilAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedOil = Fluids.SEEDOIL.getFluid(seedOilAmount);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.wheat_seeds)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.pumpkin_seeds)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.melon_seeds)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.phosphor.getItemStack(2), new ItemStack(Blocks.cobblestone)}, Fluids.LAVA.getFluid(1600));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Blocks.cactus)}, Fluids.WATER.getFluid(500));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.snowball), ForestryItem.craftingMaterial.getItemStack(4, 5)}, Fluids.ICE.getFluid(4000));

		// STILL
		RecipeManagers.stillManager.addRecipe(Constants.STILL_DESTILLATION_DURATION, Fluids.BIOMASS.getFluid(Constants.STILL_DESTILLATION_INPUT),
				Fluids.ETHANOL.getFluid(Constants.STILL_DESTILLATION_OUTPUT));

		// convert old honey to new honey
		RecipeManagers.stillManager.addRecipe(1, Fluids.LEGACY_HONEY.getFluid(1000), Fluids.HONEY.getFluid(1000));

		// MOISTENER
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Items.wheat_seeds), new ItemStack(Blocks.mycelium), 5000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.mossy_cobblestone), 20000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 1), 20000);

		// FERMENTER
		for (int i = 0; i < 6; i++) {
			RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.sapling, 1, i), GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		}

		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.cactus), GameMode.getGameMode().getIntegerSetting("fermenter.yield.cactus"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Items.wheat), GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Items.reeds), GameMode.getGameMode().getIntegerSetting("fermenter.yield.cane"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.brown_mushroom), GameMode.getGameMode().getIntegerSetting("fermenter.yield.mushroom"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.red_mushroom), GameMode.getGameMode().getIntegerSetting("fermenter.yield.mushroom"), Fluids.BIOMASS);

		// FABRICATOR

		RecipeManagers.fabricatorManager.addSmelting(new ItemStack(Blocks.glass), Fluids.GLASS.getFluid(1000), 1000);
		RecipeManagers.fabricatorManager.addSmelting(new ItemStack(Blocks.glass_pane), Fluids.GLASS.getFluid(375), 1000);
		RecipeManagers.fabricatorManager.addSmelting(new ItemStack(Blocks.sand), Fluids.GLASS.getFluid(1000), 3000);

		// / CARPENTER
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEEDOIL.getFluid(250), null, ForestryItem.impregnatedCasing.getItemStack(),
				"###",
				"# #",
				"###",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEEDOIL.getFluid(500), null,
				ForestryBlock.core.getItemStack(1, Constants.DEFINITION_ESCRITOIRE_META),
				"#  ",
				"###",
				"# #",
				'#', "plankWood");

		// RESOURCES
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEEDOIL.getFluid(100), null,
				ForestryItem.stickImpregnated.getItemStack(2),
				"#",
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(250), null,
				ForestryItem.woodPulp.getItemStack(4),
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(250), null,
				new ItemStack(Items.paper, 1),
				"#",
				"#",
				'#', "pulpWood");
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null,
				ForestryBlock.soil.getItemStack(9),
				"###",
				"#X#",
				"###",
				'#', Blocks.dirt,
				'X', ForestryItem.mulch);
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null,
				ForestryBlock.soil.getItemStack(8, 1),
				"#X#",
				"XYX", "#X#",
				'#', Blocks.dirt,
				'X', "sand",
				'Y', ForestryItem.mulch);
		RecipeManagers.carpenterManager.addRecipe(75, Fluids.WATER.getFluid(5000), null, ForestryItem.hardenedCasing.getItemStack(),
				"# #",
				" Y ",
				"# #",
				'#', "gemDiamond",
				'Y', ForestryItem.sturdyCasing);

		// / CHIPSETS
		RecipeManagers.carpenterManager.addRecipe(20, Fluids.WATER.getFluid(1000), null, ForestryItem.circuitboards.getItemStack(1, 0),
				"R R", "R#R", "R R", '#', "ingotTin", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(40, Fluids.WATER.getFluid(1000), null, ForestryItem.circuitboards.getItemStack(1, 1),
				"R#R", "R#R", "R#R", '#', "ingotBronze", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(80, Fluids.WATER.getFluid(1000), null, ForestryItem.circuitboards.getItemStack(1, 2),
				"R#R", "R#R", "R#R", '#', "ingotIron", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(80, Fluids.WATER.getFluid(1000), null, ForestryItem.circuitboards.getItemStack(1, 3),
				"R#R", "R#R", "R#R", '#', "ingotGold", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(40, Fluids.WATER.getFluid(1000), null, ForestryItem.solderingIron.getItemStack(),
				" # ", "# #", "  B", '#', "ingotIron", 'B', "ingotBronze");
		// ForestryCore.oreHandler.registerCarpenterRecipe(solderingIron);

		// RAIN SUBSTRATES
		if (PluginManager.Module.APICULTURE.isEnabled()) {
			RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null, ForestryItem.iodineCharge.getItemStack(),
					"Z#Z",
					"#Y#",
					"X#X",
					'#', ForestryItem.pollenCluster,
					'X', Items.gunpowder,
					'Y', ForestryItem.canEmpty,
					'Z', ForestryItem.honeyDrop);
			// ForestryCore.oreHandler.registerCarpenterRecipe(iodineCapsule);
			RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null, ForestryItem.craftingMaterial.getItemStack(1, 4),
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
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.WATER.getFluid(500), null, ForestryItem.craftingMaterial.getItemStack(1, 3),
				"###", "###", "###", '#', ForestryItem.craftingMaterial.getItemStack(1, 2));

		// Boxes
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null, ForestryItem.carton.getItemStack(2),
				" # ", "# #", " # ", '#', "pulpWood");

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

		// Crating and uncrating
		if (PluginManager.Module.STORAGE.isEnabled()) {
			PluginStorage.createCrateRecipes();
		}
		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.machine.upgrade");

		// / Solder Manager
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 9), Circuit.machineSpeedUpgrade1);
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 7), Circuit.machineSpeedUpgrade2);
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 4), Circuit.machineEfficiencyUpgrade1);
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerFactory();
	}

}
