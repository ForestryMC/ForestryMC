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
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.network.IGuiHandler;

import forestry.api.circuits.ChipsetManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.CreativeTabForestry;
import forestry.core.GameMode;
import forestry.core.PickupHandlerCore;
import forestry.core.SaveEventHandlerCore;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.circuits.ItemSolderingIron;
import forestry.core.commands.CommandPlugins;
import forestry.core.commands.CommandVersion;
import forestry.core.commands.RootCommand;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.BlockResource;
import forestry.core.gadgets.BlockResourceStorageBlock;
import forestry.core.gadgets.BlockSoil;
import forestry.core.gadgets.BlockStainedGlass;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.gadgets.TileEscritoire;
import forestry.core.genetics.ClimateHelper;
import forestry.core.genetics.ItemResearchNote;
import forestry.core.genetics.alleles.Allele;
import forestry.core.genetics.alleles.AlleleFactory;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.AlleleRegistry;
import forestry.core.interfaces.IPickupHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.items.ItemArmorNaturalist;
import forestry.core.items.ItemAssemblyKit;
import forestry.core.items.ItemElectronTube;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemForestryPickaxe;
import forestry.core.items.ItemForestryShovel;
import forestry.core.items.ItemFruit;
import forestry.core.items.ItemMisc;
import forestry.core.items.ItemOverlay.OverlayInfo;
import forestry.core.items.ItemPipette;
import forestry.core.items.ItemTypedBlock;
import forestry.core.items.ItemWrench;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.core.utils.ShapedRecipeCustom;

@Plugin(pluginID = "Core", name = "Core", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.core.description")
public class PluginCore extends ForestryPlugin {

	private static MachineDefinition definitionEscritoire;

	public static final RootCommand rootCommand = new RootCommand();

	private AlleleHelper alleleHelper;

	@Override
	protected void setupAPI() {
		super.setupAPI();

		ChipsetManager.solderManager = new ItemSolderingIron.SolderManager();

		ChipsetManager.circuitRegistry = new CircuitRegistry();

		AlleleRegistry alleleRegistry = new AlleleRegistry();
		AlleleManager.alleleRegistry = alleleRegistry;
		AlleleManager.climateHelper = new ClimateHelper();
		AlleleManager.alleleFactory = new AlleleFactory();
		alleleRegistry.initialize();
	}

	@Override
	public void preInit() {
		super.preInit();

		rootCommand.addChildCommand(new CommandVersion());
		rootCommand.addChildCommand(new CommandPlugins());

		Allele.helper = alleleHelper = new AlleleHelper();

		ForestryBlock.core.registerBlock(new BlockBase(Material.iron, true), ItemForestryBlock.class, "core");

		definitionEscritoire = ((BlockBase) ForestryBlock.core.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_ESCRITOIRE_META, "forestry.Escritoire", TileEscritoire.class,
				Proxies.render.getRenderEscritoire()));

		ForestryBlock.soil.registerBlock(new BlockSoil(), ItemTypedBlock.class, "soil");
		ForestryBlock.soil.block().setHarvestLevel("shovel", 0, 0);
		ForestryBlock.soil.block().setHarvestLevel("shovel", 0, 1);

		ForestryBlock.resources.registerBlock(new BlockResource(), ItemForestryBlock.class, "resources");
		ForestryBlock.resources.block().setHarvestLevel("pickaxe", 1);

		OreDictionary.registerOre("oreApatite", ForestryBlock.resources.getItemStack(1, 0));
		OreDictionary.registerOre("oreCopper", ForestryBlock.resources.getItemStack(1, 1));
		OreDictionary.registerOre("oreTin", ForestryBlock.resources.getItemStack(1, 2));

		ForestryBlock.resourceStorage.registerBlock(new BlockResourceStorageBlock(), ItemForestryBlock.class, "resourceStorage");
		ForestryBlock.resourceStorage.block().setHarvestLevel("pickaxe", 0);

		OreDictionary.registerOre("blockApatite", ForestryBlock.resourceStorage.getItemStack(1, 0));
		OreDictionary.registerOre("blockCopper", ForestryBlock.resourceStorage.getItemStack(1, 1));
		OreDictionary.registerOre("blockTin", ForestryBlock.resourceStorage.getItemStack(1, 2));
		OreDictionary.registerOre("blockBronze", ForestryBlock.resourceStorage.getItemStack(1, 3));
		OreDictionary.registerOre("chestWood", Blocks.chest);
		ForestryBlock.glass.registerBlock(new BlockStainedGlass(), ItemForestryBlock.class, "stained");
	}

	@Override
	public void doInit() {
		super.doInit();

		definitionEscritoire.register();
		ForestryModEnvWarningCallable.register();

		alleleHelper.init();

		RecipeSorter.register("forestry:shapedrecipecustom", ShapedRecipeCustom.class, RecipeSorter.Category.SHAPED, "before:minecraft:shaped");
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return null;
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerCore();
	}

	@Override
	protected void registerItems() {
		// / FERTILIZERS
		ForestryItem.fertilizerBio.registerItem((new ItemForestry()), "fertilizerBio");
		ForestryItem.fertilizerCompound.registerItem((new ItemForestry()).setBonemeal(), "fertilizerCompound");

		// / GEMS
		ForestryItem.apatite.registerItem((new ItemForestry()), "apatite");
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

		Proxies.common.addSmelting(ForestryItem.peat.getItemStack(), ForestryItem.ash.getItemStack());
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
		ForestryItem.solderingIron.registerItem(new ItemSolderingIron(), "solderingIron");

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
		Proxies.common.addSmelting(ForestryBlock.resources.getItemStack(1, 0), ForestryItem.apatite.getItemStack(), 0.5f);
		Proxies.common.addSmelting(ForestryBlock.resources.getItemStack(1, 1), ForestryItem.ingotCopper.getItemStack(), 0.5f);
		Proxies.common.addSmelting(ForestryBlock.resources.getItemStack(1, 2), ForestryItem.ingotTin.getItemStack(), 0.5f);

		/* BRONZE INGOTS */
		if (Config.isCraftingBronzeEnabled()) {
			Proxies.common.addShapelessRecipe(ForestryItem.ingotBronze.getItemStack(4), "ingotTin", "ingotCopper", "ingotCopper", "ingotCopper");
		}

		/* STURDY MACHINE */
		Proxies.common.addRecipe(ForestryItem.sturdyCasing.getItemStack(), "###", "# #", "###", '#', "ingotBronze");

		// / EMPTY CANS
		Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.can"), " # ", "# #", '#', "ingotTin");

		// / GEARS
		ArrayList<ItemStack> stoneGear = OreDictionary.getOres("gearStone");
		Object gearCenter;
		if (!stoneGear.isEmpty()) {
			gearCenter = "gearStone";
		} else {
			gearCenter = "ingotCopper";
		}
		Proxies.common.addRecipe(ForestryItem.gearBronze.getItemStack(), " # ", "#X#", " # ", '#', "ingotBronze", 'X', gearCenter);
		Proxies.common.addRecipe(ForestryItem.gearCopper.getItemStack(), " # ", "#X#", " # ", '#', "ingotCopper", 'X', gearCenter);
		Proxies.common.addRecipe(ForestryItem.gearTin.getItemStack(), " # ", "#X#", " # ", '#', "ingotTin", 'X', gearCenter);

		// / SURVIVALIST TOOLS
		Proxies.common.addRecipe(ForestryItem.bronzePickaxe.getItemStack(), " X ", " X ", "###", '#', "ingotBronze", 'X', "stickWood");
		Proxies.common.addRecipe(ForestryItem.bronzeShovel.getItemStack(), " X ", " X ", " # ", '#', "ingotBronze", 'X', "stickWood");
		Proxies.common.addShapelessRecipe(ForestryItem.kitPickaxe.getItemStack(), ForestryItem.bronzePickaxe, ForestryItem.carton);
		Proxies.common.addShapelessRecipe(ForestryItem.kitShovel.getItemStack(), ForestryItem.bronzeShovel, ForestryItem.carton);

		/* NATURALIST'S ARMOR */
		Proxies.common.addRecipe(ForestryItem.naturalistHat.getItemStack(), " X ", "Y Y", 'X', "ingotBronze", 'Y', "paneGlass");

		// / WRENCH
		Proxies.common.addRecipe(ForestryItem.wrench.getItemStack(), "# #", " # ", " # ", '#', "ingotBronze");

		// Manure and Fertilizer
		if (GameMode.getGameMode().getStackSetting("recipe.output.compost.wheat").stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.compost.wheat"), " X ", "X#X", " X ", '#', Blocks.dirt, 'X', "cropWheat");
		}
		if (GameMode.getGameMode().getStackSetting("recipe.output.compost.ash").stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.compost.ash"), " X ", "X#X", " X ", '#', Blocks.dirt, 'X', "dustAsh");
		}
		if (GameMode.getGameMode().getStackSetting("recipe.output.fertilizer.apatite").stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.fertilizer.apatite"), " # ", " X ", " # ", '#', "sand", 'X', "gemApatite");
		}
		if (GameMode.getGameMode().getStackSetting("recipe.output.fertilizer.ash").stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.fertilizer.ash"), "###", "#X#", "###", '#', "dustAsh", 'X', "gemApatite");
		}

		// Humus
		if (GameMode.getGameMode().getStackSetting("recipe.output.humus.compost").stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.humus.compost"), "###", "#X#", "###", '#', Blocks.dirt, 'X', ForestryItem.fertilizerBio);
		}
		if (GameMode.getGameMode().getStackSetting("recipe.output.humus.fertilizer").stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.humus.fertilizer"), "###", "#X#", "###", '#', Blocks.dirt, 'X', ForestryItem.fertilizerCompound);
		}

		// Bog earth
		if (GameMode.getGameMode().getStackSetting("recipe.output.bogearth.bucket").stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.bogearth.bucket"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', Items.water_bucket, 'Y', "sand");
		}

		if (GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can").stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', ForestryItem.canWater, 'Y', "sand");
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', ForestryItem.waxCapsuleWater, 'Y', "sand");
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', ForestryItem.refractoryWater, 'Y', "sand");
		}

		// Crafting Material
		Proxies.common.addRecipe(new ItemStack(Items.string), "#", "#", "#", '#', ForestryItem.craftingMaterial.getItemStack(1, 2));

		// / Pipette
		Proxies.common.addRecipe(ForestryItem.pipette.getItemStack(), "  #", " X ", "X  ", 'X', Blocks.glass_pane, '#', new ItemStack(Blocks.wool, 1, Defaults.WILDCARD));

		// Storage Blocks
		Proxies.common.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 0), "###", "###", "###", '#', "gemApatite");
		Proxies.common.addShapelessRecipe(ForestryItem.apatite.getItemStack(9), "blockApatite");

		Proxies.common.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 1), "###", "###", "###", '#', "ingotCopper");
		Proxies.common.addShapelessRecipe(ForestryItem.ingotCopper.getItemStack(9), "blockCopper");

		Proxies.common.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 2), "###", "###", "###", '#', "ingotTin");
		Proxies.common.addShapelessRecipe(ForestryItem.ingotTin.getItemStack(9), "blockTin");

		Proxies.common.addRecipe(ForestryBlock.resourceStorage.getItemStack(1, 3), "###", "###", "###", '#', "ingotBronze");
		Proxies.common.addShapelessRecipe(ForestryItem.ingotBronze.getItemStack(9), "blockBronze");
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
