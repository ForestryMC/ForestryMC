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

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import cpw.mods.fml.common.IFuelHandler;

import forestry.api.circuits.ChipsetManager;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.multiblock.MultiblockManager;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.GuiHandlerBase;
import forestry.core.IPickupHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.PickupHandlerCore;
import forestry.core.SaveEventHandlerCore;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.BlockResourceOre;
import forestry.core.blocks.BlockResourceStorage;
import forestry.core.blocks.BlockSoil;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.SolderManager;
import forestry.core.commands.CommandPlugins;
import forestry.core.commands.CommandVersion;
import forestry.core.commands.RootCommand;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.alleles.AlleleFactory;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.AlleleRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemBlockTyped;
import forestry.core.items.ItemRegistryCore;
import forestry.core.multiblock.MultiblockLogicFactory;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketRegistryCore;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.recipes.ShapelessRecipeCustom;
import forestry.core.tiles.MachineDefinition;
import forestry.core.tiles.TileEscritoire;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ForestryModEnvWarningCallable;

@Plugin(pluginID = "Core", name = "Core", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.core.description")
public class PluginCore extends ForestryPlugin {

	private static MachineDefinition definitionEscritoire;

	public static final RootCommand rootCommand = new RootCommand();
	public static ItemRegistryCore items;

	@Override
	protected void setupAPI() {
		super.setupAPI();

		ChipsetManager.solderManager = new SolderManager();

		ChipsetManager.circuitRegistry = new CircuitRegistry();

		AlleleRegistry alleleRegistry = new AlleleRegistry();
		AlleleManager.alleleRegistry = alleleRegistry;
		AlleleManager.climateHelper = new ClimateUtil();
		AlleleManager.alleleFactory = new AlleleFactory();
		alleleRegistry.initialize();

		AlleleHelper.instance = new AlleleHelper();

		MultiblockManager.logicFactory = new MultiblockLogicFactory();
	}

	@Override
	protected void registerItemsAndBlocks() {
		items = new ItemRegistryCore();

		ForestryBlock.core.registerBlock(new BlockBase(Material.iron, true), ItemBlockForestry.class, "core");

		ForestryBlock.soil.registerBlock(new BlockSoil(), ItemBlockTyped.class, "soil");
		ForestryBlock.soil.block().setHarvestLevel("shovel", 0, 0);
		ForestryBlock.soil.block().setHarvestLevel("shovel", 0, 1);

		ForestryBlock.resources.registerBlock(new BlockResourceOre(), ItemBlockForestry.class, "resources");
		ForestryBlock.resources.block().setHarvestLevel("pickaxe", 1);
		OreDictionary.registerOre("oreApatite", ForestryBlock.resources.getItemStack(1, 0));
		OreDictionary.registerOre("oreCopper", ForestryBlock.resources.getItemStack(1, 1));
		OreDictionary.registerOre("oreTin", ForestryBlock.resources.getItemStack(1, 2));

		ForestryBlock.resourceStorage.registerBlock(new BlockResourceStorage(), ItemBlockForestry.class, "resourceStorage");
		ForestryBlock.resourceStorage.block().setHarvestLevel("pickaxe", 0);
		OreDictionary.registerOre("blockApatite", ForestryBlock.resourceStorage.getItemStack(1, 0));
		OreDictionary.registerOre("blockCopper", ForestryBlock.resourceStorage.getItemStack(1, 1));
		OreDictionary.registerOre("blockTin", ForestryBlock.resourceStorage.getItemStack(1, 2));
		OreDictionary.registerOre("blockBronze", ForestryBlock.resourceStorage.getItemStack(1, 3));

		OreDictionary.registerOre("chestWood", Blocks.chest);
		OreDictionary.registerOre("craftingTableWood", Blocks.crafting_table);
	}

	@Override
	public void preInit() {
		super.preInit();

		rootCommand.addChildCommand(new CommandVersion());
		rootCommand.addChildCommand(new CommandPlugins());

		definitionEscritoire = ((BlockBase) ForestryBlock.core.block()).addDefinition(new MachineDefinition(Constants.DEFINITION_ESCRITOIRE_META, "forestry.Escritoire", TileEscritoire.class,
				Proxies.render.getRenderEscritoire()));
	}

	@Override
	public void doInit() {
		super.doInit();

		Proxies.render.init();

		definitionEscritoire.register();
		ForestryModEnvWarningCallable.register();

		AlleleHelper.instance.init();

		RecipeSorter.register("forestry:shapedrecipecustom", ShapedRecipeCustom.class, RecipeSorter.Category.SHAPED, "before:minecraft:shaped");
		RecipeSorter.register("forestry:shapelessrecipecustom", ShapelessRecipeCustom.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shapeless");
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public GuiHandlerBase getGuiHandler() {
		return null;
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerCore();
	}

	@Override
	protected void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		// forestry items
		crateRegistry.registerCrate(items.peat, "cratedPeat");
		crateRegistry.registerCrate(items.apatite, "cratedApatite");
		crateRegistry.registerCrate(items.fertilizerCompound, "cratedFertilizer");
		crateRegistry.registerCrate(items.mulch, "cratedMulch");
		crateRegistry.registerCrate(items.phosphor, "cratedPhosphor");
		crateRegistry.registerCrate(items.ash, "cratedAsh");
		crateRegistry.registerCrateUsingOreDict(items.ingotTin, "cratedTin");
		crateRegistry.registerCrateUsingOreDict(items.ingotCopper, "cratedCopper");
		crateRegistry.registerCrateUsingOreDict(items.ingotBronze, "cratedBronze");

		// forestry blocks
		crateRegistry.registerCrate(ForestryBlock.soil.getItemStack(1, 0), "cratedHumus");
		crateRegistry.registerCrate(ForestryBlock.soil.getItemStack(1, 1), "cratedBogearth");

		// vanilla items
		crateRegistry.registerCrate(Items.wheat, "cratedWheat");
		crateRegistry.registerCrate(Items.cookie, "cratedCookies");
		crateRegistry.registerCrate(Items.redstone, "cratedRedstone");
		crateRegistry.registerCrate(new ItemStack(Items.dye, 1, 4), "cratedLapis");
		crateRegistry.registerCrate(Items.reeds, "cratedReeds");
		crateRegistry.registerCrate(Items.clay_ball, "cratedClay");
		crateRegistry.registerCrate(Items.glowstone_dust, "cratedGlowstone");
		crateRegistry.registerCrate(Items.apple, "cratedApples");
		crateRegistry.registerCrate(new ItemStack(Items.nether_wart), "cratedNetherwart");
		crateRegistry.registerCrate(new ItemStack(Items.coal, 1, 1), "cratedCharcoal");
		crateRegistry.registerCrate(new ItemStack(Items.coal, 1, 0), "cratedCoal");
		crateRegistry.registerCrate(Items.wheat_seeds, "cratedSeeds");
		crateRegistry.registerCrate(Items.potato, "cratedPotatoes");
		crateRegistry.registerCrate(Items.carrot, "cratedCarrots");

		// vanilla blocks
		crateRegistry.registerCrate(new ItemStack(Blocks.log, 1, 0), "cratedWood");
		crateRegistry.registerCrate(new ItemStack(Blocks.log, 1, 1), "cratedSpruceWood");
		crateRegistry.registerCrate(new ItemStack(Blocks.log, 1, 2), "cratedBirchWood");
		crateRegistry.registerCrate(new ItemStack(Blocks.log, 1, 3), "cratedJungleWood");
		crateRegistry.registerCrate(new ItemStack(Blocks.log2, 1, 0), "cratedAcaciaWood");
		crateRegistry.registerCrate(new ItemStack(Blocks.log2, 1, 1), "cratedDarkOakWood");
		crateRegistry.registerCrate(Blocks.cobblestone, "cratedCobblestone");
		crateRegistry.registerCrate(new ItemStack(Blocks.dirt, 1, 0), "cratedDirt");
		crateRegistry.registerCrate(new ItemStack(Blocks.dirt, 1, 2), "cratedPodzol");
		crateRegistry.registerCrate(Blocks.stone, "cratedStone");
		crateRegistry.registerCrate(Blocks.brick_block, "cratedBrick");
		crateRegistry.registerCrate(Blocks.cactus, "cratedCacti");
		crateRegistry.registerCrate(new ItemStack(Blocks.sand, 1, 0), "cratedSand");
		crateRegistry.registerCrate(new ItemStack(Blocks.sand, 1, 1), "cratedRedSand");
		crateRegistry.registerCrate(Blocks.obsidian, "cratedObsidian");
		crateRegistry.registerCrate(Blocks.netherrack, "cratedNetherrack");
		crateRegistry.registerCrate(Blocks.soul_sand, "cratedSoulsand");
		crateRegistry.registerCrate(Blocks.sandstone, "cratedSandstone");
		crateRegistry.registerCrate(Blocks.nether_brick, "cratedNetherbrick");
		crateRegistry.registerCrate(Blocks.mycelium, "cratedMycelium");
		crateRegistry.registerCrate(Blocks.gravel, "cratedGravel");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 0), "cratedSaplings");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 1), "cratedSpruceSapling");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 2), "cratedBirchSapling");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 3), "cratedJungleSapling");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 4), "cratedAcaciaSapling");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 5), "cratedDarkOakSapling");
	}

	@Override
	protected void registerRecipes() {

		/* SMELTING RECIPES */
		RecipeUtil.addSmelting(ForestryBlock.resources.getItemStack(1, 0), items.apatite, 0.5f);
		RecipeUtil.addSmelting(ForestryBlock.resources.getItemStack(1, 1), items.ingotCopper, 0.5f);
		RecipeUtil.addSmelting(ForestryBlock.resources.getItemStack(1, 2), items.ingotTin, 0.5f);
		RecipeUtil.addSmelting(new ItemStack(items.peat), items.ash, 0.0f);

		/* BRONZE INGOTS */
		if (Config.isCraftingBronzeEnabled()) {
			RecipeUtil.addShapelessRecipe(new ItemStack(items.ingotBronze, 4), "ingotTin", "ingotCopper", "ingotCopper", "ingotCopper");
		}

		/* STURDY MACHINE */
		RecipeUtil.addRecipe(items.sturdyCasing, "###", "# #", "###", '#', "ingotBronze");

		// / EMPTY CANS
		RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.can"), " # ", "# #", '#', "ingotTin");

		// / GEARS
		ArrayList<ItemStack> stoneGear = OreDictionary.getOres("gearStone");
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

		// Manure and Fertilizer
		if (ForestryAPI.activeMode.getStackSetting("recipe.output.compost.wheat").stackSize > 0) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.compost.wheat"), " X ", "X#X", " X ", '#', Blocks.dirt, 'X', "cropWheat");
		}
		if (ForestryAPI.activeMode.getStackSetting("recipe.output.compost.ash").stackSize > 0) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.compost.ash"), " X ", "X#X", " X ", '#', Blocks.dirt, 'X', "dustAsh");
		}
		if (ForestryAPI.activeMode.getStackSetting("recipe.output.fertilizer.apatite").stackSize > 0) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.fertilizer.apatite"), " # ", " X ", " # ", '#', "sand", 'X', "gemApatite");
		}
		if (ForestryAPI.activeMode.getStackSetting("recipe.output.fertilizer.ash").stackSize > 0) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.fertilizer.ash"), "###", "#X#", "###", '#', "dustAsh", 'X', "gemApatite");
		}

		// Humus
		if (ForestryAPI.activeMode.getStackSetting("recipe.output.humus.compost").stackSize > 0) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.humus.compost"), "###", "#X#", "###", '#', Blocks.dirt, 'X', items.fertilizerBio);
		}
		if (ForestryAPI.activeMode.getStackSetting("recipe.output.humus.fertilizer").stackSize > 0) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.humus.fertilizer"), "###", "#X#", "###", '#', Blocks.dirt, 'X', items.fertilizerCompound);
		}

		// Bog earth
		if (ForestryAPI.activeMode.getStackSetting("recipe.output.bogearth.bucket").stackSize > 0) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.bogearth.bucket"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', Items.water_bucket, 'Y', "sand");
		}

		if (ForestryAPI.activeMode.getStackSetting("recipe.output.bogearth.can").stackSize > 0) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.bogearth.can"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', ForestryItem.canWater, 'Y', "sand");
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.bogearth.can"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', ForestryItem.waxCapsuleWater, 'Y', "sand");
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.bogearth.can"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', ForestryItem.refractoryWater, 'Y', "sand");
		}

		// Crafting Material
		RecipeUtil.addRecipe(new ItemStack(Items.string), "#", "#", "#", '#', items.craftingMaterial.getSilkWisp());

		// / Pipette
		RecipeUtil.addRecipe(items.pipette, "  #", " X ", "X  ", 'X', "paneGlass", '#', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));

		// Storage Blocks
		RecipeUtil.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 0), "###", "###", "###", '#', "gemApatite");
		RecipeUtil.addShapelessRecipe(new ItemStack(items.apatite, 9), "blockApatite");

		RecipeUtil.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 1), "###", "###", "###", '#', "ingotCopper");
		RecipeUtil.addShapelessRecipe(new ItemStack(items.ingotCopper, 9), "blockCopper");

		RecipeUtil.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 2), "###", "###", "###", '#', "ingotTin");
		RecipeUtil.addShapelessRecipe(new ItemStack(items.ingotTin, 9), "blockTin");

		RecipeUtil.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 3), "###", "###", "###", '#', "ingotBronze");
		RecipeUtil.addShapelessRecipe(new ItemStack(items.ingotBronze, 9), "blockBronze");
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
}
