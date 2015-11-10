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

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import cpw.mods.fml.common.IFuelHandler;

import forestry.api.circuits.ChipsetManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.multiblock.MultiblockManager;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.CreativeTabForestry;
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
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.circuits.SolderManager;
import forestry.core.commands.CommandPlugins;
import forestry.core.commands.CommandVersion;
import forestry.core.commands.RootCommand;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.ItemResearchNote;
import forestry.core.genetics.alleles.AlleleFactory;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.AlleleRegistry;
import forestry.core.items.ItemArmorNaturalist;
import forestry.core.items.ItemAssemblyKit;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemBlockTyped;
import forestry.core.items.ItemElectronTube;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemForestryBonemeal;
import forestry.core.items.ItemForestryPickaxe;
import forestry.core.items.ItemForestryShovel;
import forestry.core.items.ItemFruit;
import forestry.core.items.ItemMisc;
import forestry.core.items.ItemOverlay.OverlayInfo;
import forestry.core.items.ItemPipette;
import forestry.core.items.ItemWithGui;
import forestry.core.items.ItemWrench;
import forestry.core.multiblock.MultiblockLogicFactory;
import forestry.core.network.GuiId;
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
		// / FERTILIZERS
		ForestryItem.fertilizerBio.registerItem(new ItemForestry(), "fertilizerBio");
		ForestryItem.fertilizerCompound.registerItem(new ItemForestryBonemeal(), "fertilizerCompound");

		// / GEMS
		ForestryItem.apatite.registerItem(new ItemForestry(), "apatite");
		OreDictionary.registerOre("gemApatite", ForestryItem.apatite.getItemStack());

		ForestryItem.researchNote.registerItem(new ItemResearchNote(), "researchNote");

		// / INGOTS
		ForestryItem.ingotCopper.registerItem(new ItemForestry(), "ingotCopper");
		ForestryItem.ingotTin.registerItem(new ItemForestry(), "ingotTin");
		ForestryItem.ingotBronze.registerItem(new ItemForestry(), "ingotBronze");

		OreDictionary.registerOre("ingotCopper", ForestryItem.ingotCopper.getItemStack());
		OreDictionary.registerOre("ingotTin", ForestryItem.ingotTin.getItemStack());
		OreDictionary.registerOre("ingotBronze", ForestryItem.ingotBronze.getItemStack());

		// / TOOLS
		ForestryItem.wrench.registerItem((new ItemWrench()), "wrench");
		ForestryItem.pipette.registerItem(new ItemPipette(), "pipette");

		// / MACHINES
		ForestryItem.sturdyCasing.registerItem((new ItemForestry()), "sturdyMachine");
		ForestryItem.hardenedCasing.registerItem((new ItemForestry()), "hardenedMachine");
		ForestryItem.impregnatedCasing.registerItem((new ItemForestry()), "impregnatedCasing");

		ForestryItem.craftingMaterial.registerItem(new ItemMisc(), "craftingMaterial");

		/* ARMOR */
		ForestryItem.naturalistHat.registerItem(new ItemArmorNaturalist(), "naturalistHelmet");

		// / PEAT PRODUCTION
		ForestryItem.peat.registerItem((new ItemForestry()), "peat");
		OreDictionary.registerOre("brickPeat", ForestryItem.peat.getItemStack());

		ForestryItem.ash.registerItem((new ItemForestry()), "ash");
		OreDictionary.registerOre("dustAsh", ForestryItem.ash.getItemStack());

		RecipeUtil.addSmelting(ForestryItem.peat.getItemStack(), ForestryItem.ash.getItemStack(), 0.0f);
		ForestryItem.bituminousPeat.registerItem(new ItemForestry(), "bituminousPeat");

		// / GEARS
		ForestryItem.gearBronze.registerItem((new ItemForestry()), "gearBronze");
		OreDictionary.registerOre("gearBronze", ForestryItem.gearBronze.getItemStack());
		ForestryItem.gearCopper.registerItem((new ItemForestry()), "gearCopper");
		OreDictionary.registerOre("gearCopper", ForestryItem.gearCopper.getItemStack());
		ForestryItem.gearTin.registerItem((new ItemForestry()), "gearTin");
		OreDictionary.registerOre("gearTin", ForestryItem.gearTin.getItemStack());

		// / CIRCUIT BOARDS
		ForestryItem.circuitboards.registerItem(new ItemCircuitBoard(), "chipsets");
		Item solderingIron = new ItemWithGui(GuiId.SolderingIronGUI).setMaxDamage(5).setFull3D();
		ForestryItem.solderingIron.registerItem(solderingIron, "solderingIron");

		Color tubeCoverNormal = Color.WHITE;
		Color tubeCoverGold = new Color(0xFFF87E);
		Color tubeCoverEnder = new Color(0x255661);

		ForestryItem.tubes.registerItem(new ItemElectronTube(CreativeTabForestry.tabForestry,
				new OverlayInfo("ex-0", tubeCoverNormal, new Color(0xe3b78e)),
				new OverlayInfo("ex-1", tubeCoverNormal, new Color(0xE6F8FF)),
				new OverlayInfo("ex-2", tubeCoverNormal, new Color(0xddc276)),
				new OverlayInfo("ex-3", tubeCoverNormal, new Color(0xCCCCCC)),
				new OverlayInfo("ex-4", tubeCoverNormal, new Color(0xffff8b)),
				new OverlayInfo("ex-5", tubeCoverNormal, new Color(0x8CF5E3)),
				new OverlayInfo("ex-6", tubeCoverNormal, new Color(0x866bc0)),
				new OverlayInfo("ex-7", tubeCoverGold, new Color(0xd96600)),
				new OverlayInfo("ex-8", tubeCoverNormal, new Color(0x444444)),
				new OverlayInfo("ex-9", tubeCoverNormal, new Color(0x00CC41)),
				new OverlayInfo("ex-10", tubeCoverNormal, new Color(0x579CD9)),
				new OverlayInfo("ex-11", tubeCoverNormal, new Color(0x1c57c6)),
				new OverlayInfo("ex-12", tubeCoverEnder, new Color(0x33adad))
		), "thermionicTubes");

		// / CARTONS
		ForestryItem.carton.registerItem((new ItemForestry()), "carton");

		// / CRAFTING CARPENTER
		ForestryItem.stickImpregnated.registerItem((new ItemForestry()), "oakStick");
		ForestryItem.woodPulp.registerItem((new ItemForestry()), "woodPulp");
		OreDictionary.registerOre("pulpWood", ForestryItem.woodPulp.getItemStack());

		// / RECLAMATION
		ForestryItem.brokenBronzePickaxe.registerItem((new ItemForestry()), "brokenBronzePickaxe");
		ForestryItem.brokenBronzeShovel.registerItem((new ItemForestry()), "brokenBronzeShovel");

		// / TOOLS
		ForestryItem.bronzePickaxe.registerItem(new ItemForestryPickaxe(ForestryItem.brokenBronzePickaxe.getItemStack()), "bronzePickaxe");
		ForestryItem.bronzePickaxe.item().setHarvestLevel("pickaxe", 3);
		MinecraftForge.EVENT_BUS.register(ForestryItem.bronzePickaxe.item());
		ForestryItem.bronzeShovel.registerItem(new ItemForestryShovel(ForestryItem.brokenBronzeShovel.getItemStack()), "bronzeShovel");
		ForestryItem.bronzeShovel.item().setHarvestLevel("shovel", 3);
		MinecraftForge.EVENT_BUS.register(ForestryItem.bronzeShovel.item());

		// / ASSEMBLY KITS
		ForestryItem.kitShovel.registerItem(new ItemAssemblyKit(ForestryItem.bronzeShovel.getItemStack()), "kitShovel");
		ForestryItem.kitPickaxe.registerItem(new ItemAssemblyKit(ForestryItem.bronzePickaxe.getItemStack()), "kitPickaxe");

		// / MOISTENER RESOURCES
		ForestryItem.mouldyWheat.registerItem((new ItemForestry()), "mouldyWheat");
		ForestryItem.decayingWheat.registerItem((new ItemForestry()), "decayingWheat");
		ForestryItem.mulch.registerItem((new ItemForestry()), "mulch");

		// / RAINMAKER SUBSTRATES
		ForestryItem.iodineCharge.registerItem((new ItemForestry()), "iodineCapsule");

		ForestryItem.phosphor.registerItem((new ItemForestry()), "phosphor");

		// / BEE RESOURCES
		ForestryItem.beeswax.registerItem(new ItemForestry().setCreativeTab(Tabs.tabApiculture), "beeswax");
		OreDictionary.registerOre("itemBeeswax", ForestryItem.beeswax.getItemStack());

		ForestryItem.refractoryWax.registerItem(new ItemForestry(), "refractoryWax");

		// FRUITS
		ForestryItem.fruits.registerItem(new ItemFruit(), "fruits");
		for (ItemFruit.EnumFruit def : ItemFruit.EnumFruit.values()) {
			OreDictionary.registerOre(def.getOreDict(), def.getStack());
		}

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
		crateRegistry.registerCrate(ForestryBlock.soil.getItemStack(1, 0), "cratedHumus");
		crateRegistry.registerCrate(ForestryBlock.soil.getItemStack(1, 1), "cratedBogearth");
		crateRegistry.registerCrate(Blocks.nether_brick, "cratedNetherbrick");
		crateRegistry.registerCrate(ForestryItem.peat.item(), "cratedPeat");
		crateRegistry.registerCrate(ForestryItem.apatite.item(), "cratedApatite");
		crateRegistry.registerCrate(ForestryItem.fertilizerCompound.item(), "cratedFertilizer");
		crateRegistry.registerCrate(Items.wheat, "cratedWheat");
		crateRegistry.registerCrate(Blocks.mycelium, "cratedMycelium");
		crateRegistry.registerCrate(ForestryItem.mulch.item(), "cratedMulch");
		crateRegistry.registerCrate(Items.cookie, "cratedCookies");
		crateRegistry.registerCrate(Items.redstone, "cratedRedstone");
		crateRegistry.registerCrate(new ItemStack(Items.dye, 1, 4), "cratedLapis");
		crateRegistry.registerCrate(Items.reeds, "cratedReeds");
		crateRegistry.registerCrate(Items.clay_ball, "cratedClay");
		crateRegistry.registerCrate(Items.glowstone_dust, "cratedGlowstone");
		crateRegistry.registerCrate(Items.apple, "cratedApples");
		crateRegistry.registerCrate(new ItemStack(Items.nether_wart), "cratedNetherwart");
		crateRegistry.registerCrate(ForestryItem.phosphor.item(), "cratedPhosphor");
		crateRegistry.registerCrate(ForestryItem.ash.item(), "cratedAsh");
		crateRegistry.registerCrate(new ItemStack(Items.coal, 1, 1), "cratedCharcoal");
		crateRegistry.registerCrate(new ItemStack(Items.coal, 1, 0), "cratedCoal");
		crateRegistry.registerCrate(Blocks.gravel, "cratedGravel");
		crateRegistry.registerCrate(Items.wheat_seeds, "cratedSeeds");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 0), "cratedSaplings");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 1), "cratedSpruceSapling");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 2), "cratedBirchSapling");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 3), "cratedJungleSapling");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 4), "cratedAcaciaSapling");
		crateRegistry.registerCrate(new ItemStack(Blocks.sapling, 1, 5), "cratedDarkOakSapling");
		crateRegistry.registerCrate(Items.potato, "cratedPotatoes");
		crateRegistry.registerCrate(Items.carrot, "cratedCarrots");

		crateRegistry.registerCrateUsingOreDict(ForestryItem.ingotTin.item(), "cratedTin");
		crateRegistry.registerCrateUsingOreDict(ForestryItem.ingotCopper.item(), "cratedCopper");
		crateRegistry.registerCrateUsingOreDict(ForestryItem.ingotBronze.item(), "cratedBronze");
	}

	@Override
	protected void registerRecipes() {

		/* SMELTING RECIPES */
		RecipeUtil.addSmelting(ForestryBlock.resources.getItemStack(1, 0), ForestryItem.apatite.getItemStack(), 0.5f);
		RecipeUtil.addSmelting(ForestryBlock.resources.getItemStack(1, 1), ForestryItem.ingotCopper.getItemStack(), 0.5f);
		RecipeUtil.addSmelting(ForestryBlock.resources.getItemStack(1, 2), ForestryItem.ingotTin.getItemStack(), 0.5f);

		/* BRONZE INGOTS */
		if (Config.isCraftingBronzeEnabled()) {
			RecipeUtil.addShapelessRecipe(ForestryItem.ingotBronze.getItemStack(4), "ingotTin", "ingotCopper", "ingotCopper", "ingotCopper");
		}

		/* STURDY MACHINE */
		RecipeUtil.addRecipe(ForestryItem.sturdyCasing.getItemStack(), "###", "# #", "###", '#', "ingotBronze");

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
		RecipeUtil.addRecipe(ForestryItem.gearBronze.getItemStack(), " # ", "#X#", " # ", '#', "ingotBronze", 'X', gearCenter);
		RecipeUtil.addRecipe(ForestryItem.gearCopper.getItemStack(), " # ", "#X#", " # ", '#', "ingotCopper", 'X', gearCenter);
		RecipeUtil.addRecipe(ForestryItem.gearTin.getItemStack(), " # ", "#X#", " # ", '#', "ingotTin", 'X', gearCenter);

		// / SURVIVALIST TOOLS
		RecipeUtil.addRecipe(ForestryItem.bronzePickaxe.getItemStack(), " X ", " X ", "###", '#', "ingotBronze", 'X', "stickWood");
		RecipeUtil.addRecipe(ForestryItem.bronzeShovel.getItemStack(), " X ", " X ", " # ", '#', "ingotBronze", 'X', "stickWood");
		RecipeUtil.addShapelessRecipe(ForestryItem.kitPickaxe.getItemStack(), ForestryItem.bronzePickaxe, ForestryItem.carton);
		RecipeUtil.addShapelessRecipe(ForestryItem.kitShovel.getItemStack(), ForestryItem.bronzeShovel, ForestryItem.carton);

		/* NATURALIST'S ARMOR */
		RecipeUtil.addRecipe(ForestryItem.naturalistHat.getItemStack(), " X ", "Y Y", 'X', "ingotBronze", 'Y', "paneGlass");

		// / WRENCH
		RecipeUtil.addRecipe(ForestryItem.wrench.getItemStack(), "# #", " # ", " # ", '#', "ingotBronze");

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
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.humus.compost"), "###", "#X#", "###", '#', Blocks.dirt, 'X', ForestryItem.fertilizerBio);
		}
		if (ForestryAPI.activeMode.getStackSetting("recipe.output.humus.fertilizer").stackSize > 0) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.humus.fertilizer"), "###", "#X#", "###", '#', Blocks.dirt, 'X', ForestryItem.fertilizerCompound);
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
		RecipeUtil.addRecipe(new ItemStack(Items.string), "#", "#", "#", '#', ForestryItem.craftingMaterial.getItemStack(1, 2));

		// / Pipette
		RecipeUtil.addRecipe(ForestryItem.pipette.getItemStack(), "  #", " X ", "X  ", 'X', "paneGlass", '#', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE));

		// Storage Blocks
		RecipeUtil.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 0), "###", "###", "###", '#', "gemApatite");
		RecipeUtil.addShapelessRecipe(ForestryItem.apatite.getItemStack(9), "blockApatite");

		RecipeUtil.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 1), "###", "###", "###", '#', "ingotCopper");
		RecipeUtil.addShapelessRecipe(ForestryItem.ingotCopper.getItemStack(9), "blockCopper");

		RecipeUtil.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 2), "###", "###", "###", '#', "ingotTin");
		RecipeUtil.addShapelessRecipe(ForestryItem.ingotTin.getItemStack(9), "blockTin");

		RecipeUtil.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 3), "###", "###", "###", '#', "ingotBronze");
		RecipeUtil.addShapelessRecipe(ForestryItem.ingotBronze.getItemStack(9), "blockBronze");
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
			if (fuel != null && fuel.getItem() == ForestryItem.peat.item()) {
				return 2000;
			}
			if (fuel != null && fuel.getItem() == ForestryItem.bituminousPeat.item()) {
				return 4200;
			}

			return 0;
		}
	}
}
