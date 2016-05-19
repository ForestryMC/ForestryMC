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

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.circuits.ChipsetManager;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.multiblock.MultiblockManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.blocks.BlockRegistryCore;
import forestry.core.blocks.BlockSoil;
import forestry.core.blocks.EnumResourceType;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.SolderManager;
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
import forestry.core.loot.functions.SetSpeciesNBT;
import forestry.core.models.ModelManager;
import forestry.core.multiblock.MultiblockLogicFactory;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketRegistryCore;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.RecipeUtil;
import forestry.core.render.TextureManager;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.CORE, name = "Core", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.core.description")
public class PluginCore extends BlankForestryPlugin {
	public static final RootCommand rootCommand = new RootCommand();
	public static ItemRegistryCore items;
	public static BlockRegistryCore blocks;

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	@Nonnull
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

		AlleleHelper.instance = new AlleleHelper();

		LootFunctionManager.registerFunction(new SetSpeciesNBT.Serializer());

		MultiblockManager.logicFactory = new MultiblockLogicFactory();
		ForestryAPI.climateManager = new ClimateManager();
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryCore();
		blocks = new BlockRegistryCore();
	}

	@Override
	public void preInit() {
		super.preInit();
		
		MinecraftForge.EVENT_BUS.register(this);

		rootCommand.addChildCommand(new CommandPlugins());
	}

	@Override
	public void doInit() {
		super.doInit();

		blocks.analyzer.init();
		blocks.escritoire.init();

		ForestryModEnvWarningCallable.register();

		AlleleHelper.instance.init();

		Proxies.render.initRendering();
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerCore();
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		// forestry items
		crateRegistry.registerCrate(items.peat);
		crateRegistry.registerCrate(items.apatite);
		crateRegistry.registerCrate(items.fertilizerCompound);
		crateRegistry.registerCrate(items.mulch);
		crateRegistry.registerCrate(items.phosphor);
		crateRegistry.registerCrate(items.ash);
		crateRegistry.registerCrateUsingOreDict(items.ingotTin);
		crateRegistry.registerCrateUsingOreDict(items.ingotCopper);
		crateRegistry.registerCrateUsingOreDict(items.ingotBronze);

		// forestry blocks
		crateRegistry.registerCrate(blocks.soil.get(BlockSoil.SoilType.HUMUS, 1));
		crateRegistry.registerCrate(blocks.soil.get(BlockSoil.SoilType.BOG_EARTH, 1));

		// vanilla items
		crateRegistry.registerCrate(Items.WHEAT);
		crateRegistry.registerCrate(Items.COOKIE);
		crateRegistry.registerCrate(Items.REDSTONE);
		crateRegistry.registerCrate(new ItemStack(Items.DYE, 1, 4));
		crateRegistry.registerCrate(Items.REEDS);
		crateRegistry.registerCrate(Items.CLAY_BALL);
		crateRegistry.registerCrate(Items.GLOWSTONE_DUST);
		crateRegistry.registerCrate(Items.APPLE);
		crateRegistry.registerCrate(new ItemStack(Items.NETHER_WART));
		crateRegistry.registerCrate(new ItemStack(Items.COAL, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Items.COAL, 1, 0));
		crateRegistry.registerCrate(Items.WHEAT_SEEDS);
		crateRegistry.registerCrate(Items.POTATO);
		crateRegistry.registerCrate(Items.CARROT);

		// vanilla blocks
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 2));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 3));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG2, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG2, 1, 1));
		crateRegistry.registerCrate(Blocks.COBBLESTONE);
		crateRegistry.registerCrate(new ItemStack(Blocks.DIRT, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.DIRT, 1, 2));
		crateRegistry.registerCrate(new ItemStack(Blocks.STONE, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.STONE, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.STONE, 1, 3));
		crateRegistry.registerCrate(new ItemStack(Blocks.STONE, 1, 5));
		crateRegistry.registerCrate(new ItemStack(Blocks.PRISMARINE, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.PRISMARINE, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.PRISMARINE, 1, 2));
		crateRegistry.registerCrate(Blocks.BRICK_BLOCK);
		crateRegistry.registerCrate(Blocks.CACTUS);
		crateRegistry.registerCrate(new ItemStack(Blocks.SAND, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAND, 1, 1));
		crateRegistry.registerCrate(Blocks.OBSIDIAN);
		crateRegistry.registerCrate(Blocks.NETHERRACK);
		crateRegistry.registerCrate(Blocks.SOUL_SAND);
		crateRegistry.registerCrate(Blocks.SANDSTONE);
		crateRegistry.registerCrate(Blocks.NETHER_BRICK);
		crateRegistry.registerCrate(Blocks.MYCELIUM);
		crateRegistry.registerCrate(Blocks.GRAVEL);
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 2));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 3));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 4));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 5));
	}

	@Override
	public void registerRecipes() {

		/* SMELTING RECIPES */
		RecipeUtil.addSmelting(blocks.resources.get(EnumResourceType.APATITE, 1), items.apatite, 0.5f);
		RecipeUtil.addSmelting(blocks.resources.get(EnumResourceType.COPPER, 1), items.ingotCopper, 0.5f);
		RecipeUtil.addSmelting(blocks.resources.get(EnumResourceType.TIN, 1), items.ingotTin, 0.5f);
		RecipeUtil.addSmelting(new ItemStack(items.peat), items.ash, 0.0f);

		/* BRONZE INGOTS */
		if (Config.isCraftingBronzeEnabled()) {
			RecipeUtil.addShapelessRecipe(new ItemStack(items.ingotBronze, 4), "ingotTin", "ingotCopper", "ingotCopper", "ingotCopper");
		}

		/* STURDY MACHINE */
		RecipeUtil.addRecipe(items.sturdyCasing, "###", "# #", "###", '#', "ingotBronze");

		// / EMPTY CANS
		int canAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.can");
		ItemStack canOutput = PluginFluids.items.canEmpty.getItemStack(canAmount);
		RecipeUtil.addRecipe(canOutput, " # ", "# #", '#', "ingotTin");

		// / GEARS
		List<ItemStack> stoneGear = OreDictionary.getOres("gearStone");
		Object gearCenter;
		if (!stoneGear.isEmpty()) {
			gearCenter = "gearStone";
		} else {
			gearCenter = "ingotCopper";
		}
		RecipeUtil.addRecipe(items.gearBronze, " # ", "#X#", " # ", '#', "ingotBronze", 'X', gearCenter);
		RecipeUtil.addRecipe(items.gearCopper, " # ", "#X#", " # ", '#', "ingotCopper", 'X', gearCenter);
		RecipeUtil.addRecipe(items.gearTin, " # ", "#X#", " # ", '#', "ingotTin", 'X', gearCenter);

		// / SURVIVALIST TOOLS
		RecipeUtil.addRecipe(items.bronzePickaxe, " X ", " X ", "###", '#', "ingotBronze", 'X', "stickWood");
		RecipeUtil.addRecipe(items.bronzeShovel, " X ", " X ", " # ", '#', "ingotBronze", 'X', "stickWood");
		RecipeUtil.addShapelessRecipe(items.kitPickaxe, items.bronzePickaxe, items.carton);
		RecipeUtil.addShapelessRecipe(items.kitShovel, items.bronzeShovel, items.carton);

		/* NATURALIST'S ARMOR */
		RecipeUtil.addRecipe(items.spectacles, " X ", "Y Y", 'X', "ingotBronze", 'Y', "paneGlass");

		// / WRENCH
		RecipeUtil.addRecipe(items.wrench, "# #", " # ", " # ", '#', "ingotBronze");
		
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {
			// / CARPENTER
			RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null, PluginCore.items.portableAlyzer.getItemStack(),
					"X#X", "X#X", "RDR",
					'#', "paneGlass",
					'X', "ingotTin",
					'R', "dustRedstone",
					'D', "gemDiamond");
		}else{
			// Portable ANALYZER
			RecipeUtil.addRecipe(PluginCore.items.portableAlyzer.getItemStack(),
					"X#X",
					"X#X",
					"RDR",
					'#', "paneGlass",
					'X', "ingotTin",
					'R', "dustRedstone",
					'D', "gemDiamond");
		}
		
		// ANALYZER
		RecipeUtil.addRecipe(PluginCore.blocks.analyzer,
				"XTX",
				" Y ",
				"X X",
				'Y', PluginCore.items.sturdyCasing,
				'T', PluginCore.items.portableAlyzer,
				'X', "ingotBronze");

		// Manure and Fertilizer
		int compostWheatAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.wheat");
		if (compostWheatAmount > 0) {
			ItemStack compost = items.fertilizerBio.getItemStack(compostWheatAmount);
			RecipeUtil.addRecipe(compost, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', "cropWheat");
		}

		int compostAshAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.ash");
		if (compostAshAmount > 0) {
			ItemStack compost = items.fertilizerBio.getItemStack(compostAshAmount);
			RecipeUtil.addRecipe(compost, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', "dustAsh");
		}

		int fertilizerApatiteAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.fertilizer.apatite");
		if (fertilizerApatiteAmount > 0) {
			ItemStack fertilizer = items.fertilizerCompound.getItemStack(fertilizerApatiteAmount);
			RecipeUtil.addRecipe(fertilizer, " # ", " X ", " # ", '#', "sand", 'X', "gemApatite");
		}

		int fertilizerAshAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.fertilizer.ash");
		if (fertilizerAshAmount > 0) {
			ItemStack fertilizer = items.fertilizerCompound.getItemStack(fertilizerAshAmount);
			RecipeUtil.addRecipe(fertilizer, "###", "#X#", "###", '#', "dustAsh", 'X', "gemApatite");
		}

		// Humus
		int humusCompostAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.humus.compost");
		if (humusCompostAmount > 0) {
			ItemStack humus = blocks.soil.get(BlockSoil.SoilType.HUMUS, humusCompostAmount);
			RecipeUtil.addRecipe(humus, "###", "#X#", "###", '#', Blocks.DIRT, 'X', items.fertilizerBio);
		}

		int humusFertilizerAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.humus.fertilizer");
		if (humusFertilizerAmount > 0) {
			ItemStack humus = blocks.soil.get(BlockSoil.SoilType.HUMUS, humusFertilizerAmount);
			RecipeUtil.addRecipe(humus, "###", "#X#", "###", '#', Blocks.DIRT, 'X', items.fertilizerCompound);
		}

		// Bog earth
		int bogEarthOutputBucket = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.bucket");
		if (bogEarthOutputBucket > 0) {
			ItemStack bogEarth = blocks.soil.get(BlockSoil.SoilType.BOG_EARTH, bogEarthOutputBucket);
			RecipeUtil.addRecipe(bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', Items.WATER_BUCKET, 'Y', "sand");
		}

		int bogEarthOutputCan = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.can");
		if (bogEarthOutputCan > 0) {
			ItemStack bogEarth = blocks.soil.get(BlockSoil.SoilType.BOG_EARTH, bogEarthOutputCan);
			ItemStack canWater = PluginFluids.items.getContainer(EnumContainerType.CAN, Fluids.WATER).getItemStack();
			ItemStack waxCapsuleWater = PluginFluids.items.getContainer(EnumContainerType.CAPSULE, Fluids.WATER).getItemStack();
			ItemStack refractoryWater = PluginFluids.items.getContainer(EnumContainerType.REFRACTORY, Fluids.WATER).getItemStack();
			RecipeUtil.addRecipe(bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', canWater, 'Y', "sand");
			RecipeUtil.addRecipe(bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', waxCapsuleWater, 'Y', "sand");
			RecipeUtil.addRecipe(bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', refractoryWater, 'Y', "sand");
		}

		// Crafting Material
		RecipeUtil.addRecipe(new ItemStack(Items.STRING), "#", "#", "#", '#', items.craftingMaterial.getSilkWisp());

		// / Pipette
		RecipeUtil.addRecipe(items.pipette, "  #", " X ", "X  ", 'X', "paneGlass", '#', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));

		// Storage Blocks
		RecipeUtil.addRecipe(blocks.resourceStorage.get(EnumResourceType.APATITE), "###", "###", "###", '#', "gemApatite");
		RecipeUtil.addShapelessRecipe(new ItemStack(items.apatite, 9), "blockApatite");

		RecipeUtil.addRecipe(blocks.resourceStorage.get(EnumResourceType.COPPER), "###", "###", "###", '#', "ingotCopper");
		RecipeUtil.addShapelessRecipe(new ItemStack(items.ingotCopper, 9), "blockCopper");

		RecipeUtil.addRecipe(blocks.resourceStorage.get(EnumResourceType.TIN), "###", "###", "###", '#', "ingotTin");
		RecipeUtil.addShapelessRecipe(new ItemStack(items.ingotTin, 9), "blockTin");

		RecipeUtil.addRecipe(blocks.resourceStorage.get(EnumResourceType.BRONZE), "###", "###", "###", '#', "ingotBronze");
		RecipeUtil.addShapelessRecipe(new ItemStack(items.ingotBronze, 9), "blockBronze");

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
		return new FuelHandler();
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// research note items are not useful without actually having completed research
		hiddenItems.add(new ItemStack(PluginCore.items.researchNote));
	}

	private static class FuelHandler implements IFuelHandler {

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
	public void onBakeModel(ModelBakeEvent event) {
		ModelManager.getInstance().registerCustomModels(event);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerSprites(TextureStitchEvent.Pre event) {
		TextureManager.getInstance().registerSprites();
	}
}
