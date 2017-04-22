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
package forestry.core;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import forestry.api.circuits.ChipsetManager;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.multiblock.MultiblockManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.blocks.BlockRegistryCore;
import forestry.core.blocks.EnumResourceType;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.SolderManager;
import forestry.core.climate.ClimateManager;
import forestry.core.commands.CommandPlugins;
import forestry.core.commands.RootCommand;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.genetics.alleles.AlleleFactory;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.AlleleRegistry;
import forestry.core.items.EnumContainerType;
import forestry.core.items.ItemRegistryCore;
import forestry.core.items.ItemRegistryFluids;
import forestry.core.loot.SetSpeciesNBT;
import forestry.core.models.ModelCamouflageSprayCan;
import forestry.core.models.ModelEntry;
import forestry.core.models.ModelManager;
import forestry.core.multiblock.MultiblockLogicFactory;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketRegistryCore;
import forestry.core.owner.GameProfileDataSerializer;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.core.utils.OreDictUtil;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

@ForestryPlugin(pluginID = ForestryPluginUids.CORE, name = "Core", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.core.description")
public class PluginCore extends BlankForestryPlugin {
	public static final RootCommand rootCommand = new RootCommand();
	@Nullable
	public static ItemRegistryCore items;
	@Nullable
	private static BlockRegistryCore blocks;

	public static ItemRegistryCore getItems() {
		Preconditions.checkState(items != null);
		return items;
	}

	public static BlockRegistryCore getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	@Override
	public Set<String> getDependencyUids() {
		return Collections.emptySet();
	}

	@Override
	public void setupAPI() {
		ChipsetManager.solderManager = new SolderManager();

		ChipsetManager.circuitRegistry = new CircuitRegistry();

		AlleleRegistry alleleRegistry = new AlleleRegistry();
		AlleleManager.alleleRegistry = alleleRegistry;
		AlleleManager.climateHelper = new ClimateUtil();
		AlleleManager.alleleFactory = new AlleleFactory();
		alleleRegistry.initialize();

		LootFunctionManager.registerFunction(new SetSpeciesNBT.Serializer());

		MultiblockManager.logicFactory = new MultiblockLogicFactory();
		ForestryAPI.climateManager = new ClimateManager();

		CamouflageManager.camouflageAccess = new CamouflageAccess();
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryCore();
		blocks = new BlockRegistryCore();
	}

	@Override
	public void preInit() {
		super.preInit();

		GameProfileDataSerializer.register();

		MinecraftForge.EVENT_BUS.register(this);

		rootCommand.addChildCommand(new CommandPlugins());

		CamouflageManager.camouflageAccess.registerCamouflageItemHandler(new CamouflageHandlerBlock());
		CamouflageManager.camouflageAccess.registerCamouflageItemHandler(new CamouflageHandlerGlass());
		CamouflageManager.camouflageAccess.registerCamouflageItemHandler(new CamouflageHandlerDoor());
	}

	@Override
	public void doInit() {
		super.doInit();

		AlleleHelper alleleHelper = AlleleHelper.getInstance();
		BlockRegistryCore blocks = getBlocks();

		blocks.analyzer.init();
		blocks.escritoire.init();

		ForestryModEnvWarningCallable.register();

		alleleHelper.init();

		Proxies.render.initRendering();

		RecipeSorter.register("forestry:shaped", ShapedRecipeCustom.class, RecipeSorter.Category.SHAPED, "");
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerCore();
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		// forestry items
		ItemRegistryCore items = getItems();
		crateRegistry.registerCrate(items.peat);
		crateRegistry.registerCrate(items.apatite);
		crateRegistry.registerCrate(items.fertilizerCompound);
		crateRegistry.registerCrate(items.mulch);
		crateRegistry.registerCrate(items.phosphor);
		crateRegistry.registerCrate(items.ash);
		crateRegistry.registerCrate(OreDictUtil.INGOT_TIN);
		crateRegistry.registerCrate(OreDictUtil.INGOT_COPPER);
		crateRegistry.registerCrate(OreDictUtil.INGOT_BRONZE);

		// forestry blocks
		BlockRegistryCore blocks = getBlocks();
		crateRegistry.registerCrate(blocks.humus);
		crateRegistry.registerCrate(blocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, 1));

		// vanilla items
		crateRegistry.registerCrate(OreDictUtil.CROP_WHEAT);
		crateRegistry.registerCrate(Items.COOKIE);
		crateRegistry.registerCrate(OreDictUtil.DUST_REDSTONE);
		crateRegistry.registerCrate(new ItemStack(Items.DYE, 1, 4));
		crateRegistry.registerCrate("sugarcane");
		crateRegistry.registerCrate(Items.CLAY_BALL);
		crateRegistry.registerCrate("dustGlowstone");
		crateRegistry.registerCrate(Items.APPLE);
		crateRegistry.registerCrate(new ItemStack(Items.NETHER_WART));
		crateRegistry.registerCrate(new ItemStack(Items.COAL, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Items.COAL, 1, 0));
		crateRegistry.registerCrate(Items.WHEAT_SEEDS);
		crateRegistry.registerCrate("cropPotato");
		crateRegistry.registerCrate("cropCarrot");

		// vanilla blocks
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 2));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 3));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG2, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG2, 1, 1));
		crateRegistry.registerCrate("cobblestone");
		crateRegistry.registerCrate("dirt");
		crateRegistry.registerCrate(new ItemStack(Blocks.DIRT, 1, 2));
		crateRegistry.registerCrate("stone");
		crateRegistry.registerCrate("stoneGranite");
		crateRegistry.registerCrate("stoneDiorite");
		crateRegistry.registerCrate("stoneAndesite");
		crateRegistry.registerCrate("blockPrismarine");
		crateRegistry.registerCrate("blockPrismarineBrick");
		crateRegistry.registerCrate("blockPrismarineDark");
		crateRegistry.registerCrate(Blocks.BRICK_BLOCK);
		crateRegistry.registerCrate("blockCactus");
		crateRegistry.registerCrate(new ItemStack(Blocks.SAND, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAND, 1, 1));
		crateRegistry.registerCrate("obsidian");
		crateRegistry.registerCrate("netherrack");
		crateRegistry.registerCrate(Blocks.SOUL_SAND);
		crateRegistry.registerCrate(Blocks.SANDSTONE);
		crateRegistry.registerCrate(Blocks.NETHER_BRICK);
		crateRegistry.registerCrate(Blocks.MYCELIUM);
		crateRegistry.registerCrate("gravel");
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 2));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 3));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 4));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 5));
	}

	@Override
	public void registerRecipes() {
		BlockRegistryCore blocks = getBlocks();
		ItemRegistryCore items = getItems();
		ItemRegistryFluids fluidItems = PluginFluids.getItems();

		/* SMELTING RECIPES */
		RecipeUtil.addSmelting(blocks.resources.get(EnumResourceType.APATITE, 1), items.apatite, 0.5f);
		RecipeUtil.addSmelting(blocks.resources.get(EnumResourceType.COPPER, 1), items.ingotCopper, 0.5f);
		RecipeUtil.addSmelting(blocks.resources.get(EnumResourceType.TIN, 1), items.ingotTin, 0.5f);
		RecipeUtil.addSmelting(new ItemStack(items.peat), items.ash, 0.0f);

		/* BRONZE INGOTS */
		if (Config.isCraftingBronzeEnabled()) {
			ItemStack ingotBronze = items.ingotBronze.copy();
			ingotBronze.setCount(4);
			RecipeUtil.addShapelessRecipe(ingotBronze, OreDictUtil.INGOT_TIN, OreDictUtil.INGOT_COPPER, OreDictUtil.INGOT_COPPER, OreDictUtil.INGOT_COPPER);
		}

		/* STURDY MACHINE */
		RecipeUtil.addRecipe(items.sturdyCasing, "###", "# #", "###", '#', OreDictUtil.INGOT_BRONZE);

		// / EMPTY CANS
		int canAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.can");
		ItemStack canOutput = fluidItems.canEmpty.getItemStack(canAmount);
		RecipeUtil.addRecipe(canOutput, " # ", "# #", '#', OreDictUtil.INGOT_TIN);

		// / GEARS
		List<ItemStack> stoneGear = OreDictionary.getOres(OreDictUtil.GEAR_STONE);
		Object gearCenter;
		if (!stoneGear.isEmpty()) {
			gearCenter = OreDictUtil.GEAR_STONE;
		} else {
			gearCenter = OreDictUtil.INGOT_COPPER;
		}
		RecipeUtil.addRecipe(items.gearBronze, " # ", "#X#", " # ", '#', OreDictUtil.INGOT_BRONZE, 'X', gearCenter);
		RecipeUtil.addRecipe(items.gearCopper, " # ", "#X#", " # ", '#', OreDictUtil.INGOT_COPPER, 'X', gearCenter);
		RecipeUtil.addRecipe(items.gearTin, " # ", "#X#", " # ", '#', OreDictUtil.INGOT_TIN, 'X', gearCenter);

		// / SURVIVALIST TOOLS
		RecipeUtil.addRecipe(items.bronzePickaxe, " X ", " X ", "###", '#', OreDictUtil.INGOT_BRONZE, 'X', OreDictUtil.STICK_WOOD);
		RecipeUtil.addRecipe(items.bronzeShovel, " X ", " X ", " # ", '#', OreDictUtil.INGOT_BRONZE, 'X', OreDictUtil.STICK_WOOD);
		RecipeUtil.addShapelessRecipe(items.kitPickaxe, items.bronzePickaxe, items.carton);
		RecipeUtil.addShapelessRecipe(items.kitShovel, items.bronzeShovel, items.carton);

		/* NATURALIST'S ARMOR */
		RecipeUtil.addRecipe(items.spectacles, " X ", "Y Y", 'X', OreDictUtil.INGOT_BRONZE, 'Y', OreDictUtil.PANE_GLASS);

		// / WRENCH
		RecipeUtil.addRecipe(items.wrench, "# #", " # ", " # ", '#', OreDictUtil.INGOT_BRONZE);

		// / CAMOUFLAGE SPRAY CAN
		RecipeUtil.addRecipe(items.camouflageSprayCan, "TTT", "TCT", "TCT", 'T', OreDictUtil.INGOT_TIN, 'C', items.craftingMaterial.getCamouflagedPaneling());
		
		// / WEB
		RecipeUtil.addRecipe(new ItemStack(Blocks.WEB, 4), "# #", " # ", "# #", '#', items.craftingMaterial.getSilkWisp());

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {
			// / CARPENTER
			// Portable ANALYZER
			RecipeManagers.carpenterManager.addRecipe(100, new FluidStack(FluidRegistry.WATER, 2000), ItemStack.EMPTY, items.portableAlyzer.getItemStack(),
					"X#X", "X#X", "RDR",
					'#', OreDictUtil.PANE_GLASS,
					'X', OreDictUtil.INGOT_TIN,
					'R', OreDictUtil.DUST_REDSTONE,
					'D', OreDictUtil.GEM_DIAMOND);
			// Camouflaged Paneling
			RecipeManagers.carpenterManager.addRecipe(50, Fluids.BIOMASS.getFluid(500), ItemStack.EMPTY, items.craftingMaterial.getCamouflagedPaneling(8),
					"Y#R", "BPB", "R#Y",
					'#', OreDictUtil.PLANK_WOOD,
					'R', OreDictUtil.DUST_REDSTONE,
					'P', OreDictUtil.PULP_WOOD,
					'R', OreDictUtil.DYE_RED,
					'B', OreDictUtil.DYE_BLUE,
					'Y', OreDictUtil.DYE_YELLOW);
		} else {
			// Portable ANALYZER
			RecipeUtil.addRecipe(items.portableAlyzer.getItemStack(),
					"X#X",
					"X#X",
					"RDR",
					'#', OreDictUtil.PANE_GLASS,
					'X', OreDictUtil.INGOT_TIN,
					'R', OreDictUtil.DUST_REDSTONE,
					'D', OreDictUtil.GEM_DIAMOND);
			// Camouflaged Paneling
			RecipeUtil.addRecipe(items.craftingMaterial.getCamouflagedPaneling(8),
					"WWW",
					"YBR",
					"WWW",
					'W', OreDictUtil.PLANK_WOOD,
					'D', OreDictUtil.DUST_REDSTONE,
					'Y', OreDictUtil.DYE_YELLOW,
					'B', OreDictUtil.DYE_BLUE,
					'R', OreDictUtil.DYE_RED);
		}

		// ANALYZER
		RecipeUtil.addRecipe(blocks.analyzer,
				"XTX",
				" Y ",
				"X X",
				'Y', items.sturdyCasing,
				'T', items.portableAlyzer,
				'X', OreDictUtil.INGOT_BRONZE);

		// Manure and Fertilizer
		int compostWheatAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.wheat");
		if (compostWheatAmount > 0) {
			ItemStack compost = items.fertilizerBio.getItemStack(compostWheatAmount);
			RecipeUtil.addRecipe(compost, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', OreDictUtil.CROP_WHEAT);
		}

		int compostAshAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.ash");
		if (compostAshAmount > 0) {
			ItemStack compost = items.fertilizerBio.getItemStack(compostAshAmount);
			RecipeUtil.addRecipe(compost, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', OreDictUtil.DUST_ASH);
		}

		int fertilizerApatiteAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.fertilizer.apatite");
		if (fertilizerApatiteAmount > 0) {
			ItemStack fertilizer = items.fertilizerCompound.getItemStack(fertilizerApatiteAmount);
			RecipeUtil.addRecipe(fertilizer, " # ", " X ", " # ", '#', OreDictUtil.SAND, 'X', OreDictUtil.GEM_APATITE);
		}

		int fertilizerAshAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.fertilizer.ash");
		if (fertilizerAshAmount > 0) {
			ItemStack fertilizer = items.fertilizerCompound.getItemStack(fertilizerAshAmount);
			RecipeUtil.addRecipe(fertilizer, "###", "#X#", "###", '#', OreDictUtil.DUST_ASH, 'X', OreDictUtil.GEM_APATITE);
		}

		// Humus
		int humusCompostAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.humus.compost");
		if (humusCompostAmount > 0) {
			ItemStack humus = new ItemStack(blocks.humus, humusCompostAmount);
			RecipeUtil.addRecipe(humus, "###", "#X#", "###", '#', Blocks.DIRT, 'X', items.fertilizerBio);
		}

		int humusFertilizerAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.humus.fertilizer");
		if (humusFertilizerAmount > 0) {
			ItemStack humus = new ItemStack(blocks.humus, humusFertilizerAmount);
			RecipeUtil.addRecipe(humus, "###", "#X#", "###", '#', Blocks.DIRT, 'X', items.fertilizerCompound);
		}

		// Bog earth
		int bogEarthOutputBucket = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.bucket");
		if (bogEarthOutputBucket > 0) {
			ItemStack bogEarth = blocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, bogEarthOutputBucket);
			RecipeUtil.addRecipe(bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', Items.WATER_BUCKET, 'Y', OreDictUtil.SAND);
		}

		int bogEarthOutputCan = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.can");
		if (bogEarthOutputCan > 0) {
			ItemStack bogEarth = blocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, bogEarthOutputCan);
			ItemStack canWater = fluidItems.getContainer(EnumContainerType.CAN, FluidRegistry.WATER);
			ItemStack waxCapsuleWater = fluidItems.getContainer(EnumContainerType.CAPSULE, FluidRegistry.WATER);
			ItemStack refractoryWater = fluidItems.getContainer(EnumContainerType.REFRACTORY, FluidRegistry.WATER);
			RecipeUtil.addRecipe(bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', canWater, 'Y', OreDictUtil.SAND);
			RecipeUtil.addRecipe(bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', waxCapsuleWater, 'Y', OreDictUtil.SAND);
			RecipeUtil.addRecipe(bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', refractoryWater, 'Y', OreDictUtil.SAND);
		}

		// Crafting Material
		RecipeUtil.addRecipe(new ItemStack(Items.STRING), "#", "#", "#", '#', items.craftingMaterial.getSilkWisp());

		// / Pipette
		RecipeUtil.addRecipe(items.pipette, "  #", " X ", "X  ", 'X', OreDictUtil.PANE_GLASS, '#', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));

		// Storage Blocks
		{
			RecipeUtil.addRecipe(blocks.resourceStorageApatite, "###", "###", "###", '#', OreDictUtil.GEM_APATITE);

			RecipeUtil.addShapelessRecipe(new ItemStack(items.apatite, 9), OreDictUtil.BLOCK_APATITE);
		}

		{
			RecipeUtil.addRecipe(blocks.resourceStorageCopper, "###", "###", "###", '#', OreDictUtil.INGOT_COPPER);

			ItemStack ingotCopper = items.ingotCopper.copy();
			ingotCopper.setCount(9);
			RecipeUtil.addShapelessRecipe(ingotCopper, OreDictUtil.BLOCK_COPPER);
		}

		{
			RecipeUtil.addRecipe(blocks.resourceStorageTin, "###", "###", "###", '#', OreDictUtil.INGOT_TIN);

			ItemStack ingotTin = items.ingotTin.copy();
			ingotTin.setCount(9);
			RecipeUtil.addShapelessRecipe(ingotTin, OreDictUtil.BLOCK_TIN);
		}

		{
			RecipeUtil.addRecipe(blocks.resourceStorageBronze, "###", "###", "###", '#', OreDictUtil.INGOT_BRONZE);

			ItemStack ingotBronze = items.ingotBronze.copy();
			ingotBronze.setCount(9);
			RecipeUtil.addShapelessRecipe(ingotBronze, OreDictUtil.BLOCK_BRONZE);
		}

		// alternate recipes
		if (!ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			RecipeManagers.centrifugeManager.addRecipe(5, new ItemStack(Items.STRING), ImmutableMap.of(
					items.craftingMaterial.getSilkWisp(), 0.15f
			));
		}
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryCore();
	}

	@Override
	public IPickupHandler getPickupHandler() {
		return new PickupHandlerCore();
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[]{rootCommand};
	}

	@Override
	public IFuelHandler getFuelHandler() {
		return new FuelHandler(getItems());
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// research note items are not useful without actually having completed research
		hiddenItems.add(new ItemStack(getItems().researchNote));
	}

	private static class FuelHandler implements IFuelHandler {
		private final ItemRegistryCore items;

		public FuelHandler(ItemRegistryCore items) {
			this.items = items;
		}

		@Override
		public int getBurnTime(ItemStack fuel) {
			if (fuel != null && fuel.getItem() == items.peat) {
				return 2000;
			}
			if (fuel != null && fuel.getItem() == items.bituminousPeat) {
				return 4200;
			}

			return 0;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onBakeModels(ModelBakeEvent event) {
		ModelResourceLocation modelLocation = new ModelResourceLocation("forestry:camouflage_spray_can", "inventory");
		ModelEntry blockModelIndex = new ModelEntry(modelLocation, new ModelCamouflageSprayCan());
		ModelManager modelManager = ModelManager.getInstance();
		modelManager.registerCustomModel(blockModelIndex);
		modelManager.onBakeModels(event);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerSprites(TextureStitchEvent.Pre event) {
		TextureManagerForestry.getInstance().registerSprites();
	}
}
