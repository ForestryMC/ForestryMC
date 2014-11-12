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
import forestry.core.genetics.Allele;
import forestry.core.genetics.AlleleRegistry;
import forestry.core.genetics.ClimateHelper;
import forestry.core.genetics.ItemResearchNote;
import forestry.core.interfaces.IPickupHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.items.ItemArmorNaturalist;
import forestry.core.items.ItemAssemblyKit;
import forestry.core.items.ItemCrated;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemForestryPickaxe;
import forestry.core.items.ItemForestryShovel;
import forestry.core.items.ItemFruit;
import forestry.core.items.ItemLiquidContainer;
import forestry.core.items.ItemLiquidContainer.EnumContainerType;
import forestry.core.items.ItemMisc;
import forestry.core.items.ItemOverlay;
import forestry.core.items.ItemOverlay.OverlayInfo;
import forestry.core.items.ItemPipette;
import forestry.core.items.ItemTypedBlock;
import forestry.core.items.ItemWrench;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.core.utils.ShapedRecipeCustom;

@Plugin(pluginID = "Core", name = "Core", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.core.description")
public class PluginCore extends ForestryPlugin {

	public static MachineDefinition definitionEscritoire;
	// ICrashCallable for highlighting certain mods during crashes.
	public static ForestryModEnvWarningCallable crashCallable;
	public static RootCommand rootCommand = new RootCommand();

	@Override
	public void preInit() {
		super.preInit();

		rootCommand.addChildCommand(new CommandVersion());
		rootCommand.addChildCommand(new CommandPlugins());

		ChipsetManager.solderManager = new ItemSolderingIron.SolderManager();

		CircuitRegistry circuitRegistry = new CircuitRegistry();
		ChipsetManager.circuitRegistry = circuitRegistry;
		circuitRegistry.initialize();

		AlleleRegistry alleleRegistry = new AlleleRegistry();
		AlleleManager.alleleRegistry = alleleRegistry;
		AlleleManager.climateHelper = new ClimateHelper();
		alleleRegistry.initialize();

		Allele.initialize();

		ForestryBlock.core.registerBlock(new BlockBase(Material.iron, true), ItemForestryBlock.class, "core");

		definitionEscritoire = ((BlockBase) ForestryBlock.core.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_ESCRITOIRE_META, "forestry.Escritoire", TileEscritoire.class,
				Proxies.render.getRenderEscritoire()));

		ForestryBlock.soil.registerBlock(new BlockSoil(), ItemTypedBlock.class, "soil");
		ForestryBlock.soil.block().setHarvestLevel("shovel", 0, 0);
		ForestryBlock.soil.block().setHarvestLevel("shovel", 0, 1);

		ForestryBlock.resources.registerBlock(new BlockResource(), ItemForestryBlock.class, "resources");
		ForestryBlock.resources.block().setHarvestLevel("pickaxe", 1, 0);
		ForestryBlock.resources.block().setHarvestLevel("pickaxe", 1, 1);
		ForestryBlock.resources.block().setHarvestLevel("pickaxe", 1, 2);

		OreDictionary.registerOre("oreApatite", ForestryBlock.resources.getItemStack(1, 0));
		OreDictionary.registerOre("oreCopper", ForestryBlock.resources.getItemStack(1, 1));
		OreDictionary.registerOre("oreTin", ForestryBlock.resources.getItemStack(1, 2));

		ForestryBlock.resourceStorage.registerBlock(new BlockResourceStorageBlock(), ItemForestryBlock.class, "resourceStorage");
		ForestryBlock.resourceStorage.block().setHarvestLevel("pickaxe", 0, 0);
		ForestryBlock.resourceStorage.block().setHarvestLevel("pickaxe", 0, 1);
		ForestryBlock.resourceStorage.block().setHarvestLevel("pickaxe", 0, 2);
		ForestryBlock.resourceStorage.block().setHarvestLevel("pickaxe", 0, 3);

		OreDictionary.registerOre("blockApatite", ForestryBlock.resourceStorage.getItemStack(1, 0));
		OreDictionary.registerOre("blockCopper", ForestryBlock.resourceStorage.getItemStack(1, 1));
		OreDictionary.registerOre("blockTin", ForestryBlock.resourceStorage.getItemStack(1, 2));
		OreDictionary.registerOre("blockBronze", ForestryBlock.resourceStorage.getItemStack(1, 3));

		ForestryBlock.glass.registerBlock(new BlockStainedGlass(), ItemForestryBlock.class, "stained");
	}

	@Override
	public void doInit() {
		super.doInit();

		definitionEscritoire.register();
		crashCallable = new ForestryModEnvWarningCallable();

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
		ForestryItem.fertilizerCompound.registerItem((new ItemForestry()).setBonemeal(true), "fertilizerCompound");

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
		ForestryItem.naturalistHat.registerItem(new ItemArmorNaturalist(0), "naturalistHelmet");

		// / DISCONTINUED
		// ForestryItem.vialEmpty = (new
		// ItemForestry(Config.getOrCreateIntProperty("vialEmpty",
		// Config.CATEGORY_ITEM, Defaults.ID_ITEM_VIAL_EMPTY)))
		// .setItemName("vialEmpty").setIconIndex(10);
		ForestryItem.vialCatalyst.registerItem((new ItemForestry()), "vialCatalyst");

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
		ForestryItem.tubes.registerItem(new ItemOverlay(CreativeTabForestry.tabForestry,
				new OverlayInfo("ex-0", 0xffffff, 0xe3b78e), new OverlayInfo("ex-1", 0xffffff, 0xe1eef4),
				new OverlayInfo("ex-2", 0xffffff, 0xddc276), new OverlayInfo("ex-3", 0xffffff, 0xd8d8d8), new OverlayInfo("ex-4", 0xffffff, 0xffff8b),
				new OverlayInfo("ex-5", 0xffffff, 0x7bd1b8), new OverlayInfo("ex-6", 0xffffff, 0x866bc0), new OverlayInfo("ex-7", 0xfff87e, 0xd96600),
				new OverlayInfo("ex-8", 0xffffff, 0x444444), new OverlayInfo("ex-9", 0xffffff, 0xbfffdd), new OverlayInfo("ex-10", 0xffffff, 0x68ccee),
				new OverlayInfo("ex-11", 0xffffff, 0x1c57c6)), "thermionicTubes");

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

		// / EMPTY LIQUID CONTAINERS
		ForestryItem.waxCapsule.registerItem(new ItemLiquidContainer(EnumContainerType.CAPSULE, -1).setMaxStackSize(64), "waxCapsule");
		ForestryItem.canEmpty.registerItem(new ItemLiquidContainer(EnumContainerType.CAN, -1).setMaxStackSize(64), "canEmpty");
		ForestryItem.refractoryEmpty.registerItem(new ItemLiquidContainer(EnumContainerType.REFRACTORY, -1).setMaxStackSize(64), "refractoryEmpty");

		// / BUCKETS
		ForestryItem.bucketBiomass.registerItem(new ItemForestry().setContainerItem(Items.bucket).setMaxStackSize(1), "bucketBiomass");
		ForestryItem.bucketBiofuel.registerItem(new ItemForestry().setContainerItem(Items.bucket).setMaxStackSize(1), "bucketBiofuel");

		// / WAX CAPSULES
		ForestryItem.waxCapsuleWater.registerItem((new ItemLiquidContainer(EnumContainerType.CAPSULE, 0x2432ec)), "waxCapsuleWater");
		ForestryItem.waxCapsuleBiomass.registerItem((new ItemLiquidContainer(EnumContainerType.CAPSULE, 0x83d41c)), "waxCapsuleBiomass");
		ForestryItem.waxCapsuleBiofuel.registerItem((new ItemLiquidContainer(EnumContainerType.CAPSULE, 0xff7909)), "waxCapsuleBiofuel");
		ForestryItem.waxCapsuleOil.registerItem((new ItemLiquidContainer(EnumContainerType.CAPSULE, 0x404040)), "waxCapsuleOil");
		ForestryItem.waxCapsuleFuel.registerItem((new ItemLiquidContainer(EnumContainerType.CAPSULE, 0xffff00)), "waxCapsuleFuel");
		ForestryItem.waxCapsuleSeedOil.registerItem((new ItemLiquidContainer(EnumContainerType.CAPSULE, 0xffffa9)), "waxCapsuleSeedOil");
		ForestryItem.waxCapsuleHoney.registerItem((new ItemLiquidContainer(EnumContainerType.CAPSULE, 0xffda47)).setDrink(Defaults.FOOD_HONEY_HEAL, Defaults.FOOD_HONEY_SATURATION), "waxCapsuleHoney");
		ForestryItem.waxCapsuleJuice.registerItem((new ItemLiquidContainer(EnumContainerType.CAPSULE, 0x99d04e)).setDrink(Defaults.FOOD_JUICE_HEAL, Defaults.FOOD_JUICE_SATURATION), "waxCapsuleJuice");
		ForestryItem.waxCapsuleIce.registerItem((new ItemLiquidContainer(EnumContainerType.CAPSULE, 0xdcffff)), "waxCapsuleIce");

		// / CANS
		ForestryItem.canWater.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0x2432ec)), "waterCan");
		ForestryItem.canBiomass.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0x83d41c)), "biomassCan");
		ForestryItem.canBiofuel.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0xff7909)), "biofuelCan");
		ForestryItem.canOil.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0x404040)), "canOil");
		ForestryItem.canFuel.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0xffff00)), "canFuel");
		ForestryItem.canLava.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0xfd461f)), "canLava");
		ForestryItem.canSeedOil.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0xffffa9)), "canSeedOil");
		ForestryItem.canHoney.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0xffda47)).setDrink(Defaults.FOOD_HONEY_HEAL, Defaults.FOOD_HONEY_SATURATION), "canHoney");
		ForestryItem.canJuice.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0x99d04e)).setDrink(Defaults.FOOD_JUICE_HEAL, Defaults.FOOD_JUICE_SATURATION), "canJuice");
		ForestryItem.canIce.registerItem((new ItemLiquidContainer(EnumContainerType.CAN, 0xdcffff)), "canIce");

		// / REFRACTORY CAPSULES
		ForestryItem.refractoryWater.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0x2432ec)), "refractoryWater");
		ForestryItem.refractoryBiomass.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0x83d41c)), "refractoryBiomass");
		ForestryItem.refractoryBiofuel.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0xff7909)), "refractoryBiofuel");
		ForestryItem.refractoryOil.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0x404040)), "refractoryOil");
		ForestryItem.refractoryFuel.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0xffff00)), "refractoryFuel");
		ForestryItem.refractoryLava.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0xfd461f)), "refractoryLava");
		ForestryItem.refractorySeedOil.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0xffffa9)), "refractorySeedOil");
		ForestryItem.refractoryHoney.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0xffda47)).setDrink(Defaults.FOOD_HONEY_HEAL, Defaults.FOOD_HONEY_SATURATION), "refractoryHoney");
		ForestryItem.refractoryJuice.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0x99d04e)).setDrink(Defaults.FOOD_JUICE_HEAL, Defaults.FOOD_JUICE_SATURATION), "refractoryJuice");
		ForestryItem.refractoryIce.registerItem((new ItemLiquidContainer(EnumContainerType.REFRACTORY, 0xdcffff)), "refractoryIce");

	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerCrates() {
		// / CRATES
		ForestryItem.cratedWood.registerItem(new ItemCrated(new ItemStack(Blocks.log)), "cratedWood");
		ForestryItem.cratedCobblestone.registerItem(new ItemCrated(new ItemStack(Blocks.cobblestone)), "cratedCobblestone");
		ForestryItem.cratedDirt.registerItem(new ItemCrated(new ItemStack(Blocks.dirt)), "cratedDirt");
		ForestryItem.cratedStone.registerItem(new ItemCrated(new ItemStack(Blocks.stone)), "cratedStone");
		ForestryItem.cratedBrick.registerItem(new ItemCrated(new ItemStack(Blocks.brick_block)), "cratedBrick");
		ForestryItem.cratedCacti.registerItem(new ItemCrated(new ItemStack(Blocks.cactus)), "cratedCacti");
		ForestryItem.cratedSand.registerItem(new ItemCrated(new ItemStack(Blocks.sand)), "cratedSand");
		ForestryItem.cratedObsidian.registerItem(new ItemCrated(new ItemStack(Blocks.obsidian)), "cratedObsidian");
		ForestryItem.cratedNetherrack.registerItem(new ItemCrated(new ItemStack(Blocks.netherrack)), "cratedNetherrack");
		ForestryItem.cratedSoulsand.registerItem(new ItemCrated(new ItemStack(Blocks.soul_sand)), "cratedSoulsand");
		ForestryItem.cratedSandstone.registerItem(new ItemCrated(new ItemStack(Blocks.sandstone)), "cratedSandstone");
		ForestryItem.cratedBogearth.registerItem(new ItemCrated(ForestryBlock.soil.getItemStack(1, 1)), "cratedBogearth");
		ForestryItem.cratedHumus.registerItem(new ItemCrated(ForestryBlock.soil.getItemStack(1, 0)), "cratedHumus");
		ForestryItem.cratedNetherbrick.registerItem(new ItemCrated(new ItemStack(Blocks.nether_brick)), "cratedNetherbrick");
		ForestryItem.cratedPeat.registerItem(new ItemCrated(ForestryItem.peat.getItemStack()), "cratedPeat");
		ForestryItem.cratedApatite.registerItem(new ItemCrated(ForestryItem.apatite.getItemStack()), "cratedApatite");
		ForestryItem.cratedFertilizer.registerItem(new ItemCrated(ForestryItem.fertilizerCompound.getItemStack()), "cratedFertilizer");
		ForestryItem.cratedTin.registerItem(new ItemCrated(ForestryItem.ingotTin.getItemStack()), "cratedTin");
		ForestryItem.cratedCopper.registerItem(new ItemCrated(ForestryItem.ingotCopper.getItemStack()), "cratedCopper");
		ForestryItem.cratedBronze.registerItem(new ItemCrated(ForestryItem.ingotBronze.getItemStack()), "cratedBronze");
		ForestryItem.cratedWheat.registerItem(new ItemCrated(new ItemStack(Items.wheat)), "cratedWheat");
		ForestryItem.cratedMycelium.registerItem(new ItemCrated(new ItemStack(Blocks.mycelium)), "cratedMycelium");
		ForestryItem.cratedMulch.registerItem(new ItemCrated(ForestryItem.mulch.getItemStack()), "cratedMulch");
		ForestryItem.cratedCookies.registerItem(new ItemCrated(new ItemStack(Items.cookie)), "cratedCookies");
		ForestryItem.cratedRedstone.registerItem(new ItemCrated(new ItemStack(Items.redstone)), "cratedRedstone");
		ForestryItem.cratedLapis.registerItem(new ItemCrated(new ItemStack(Items.dye, 1, 4)), "cratedLapis");
		ForestryItem.cratedReeds.registerItem(new ItemCrated(new ItemStack(Items.reeds)), "cratedReeds");
		ForestryItem.cratedClay.registerItem(new ItemCrated(new ItemStack(Items.clay_ball)), "cratedClay");
		ForestryItem.cratedGlowstone.registerItem(new ItemCrated(new ItemStack(Items.glowstone_dust)), "cratedGlowstone");
		ForestryItem.cratedApples.registerItem(new ItemCrated(new ItemStack(Items.apple)), "cratedApples");
		ForestryItem.cratedNetherwart.registerItem(new ItemCrated(new ItemStack(Items.nether_wart)), "cratedNetherwart");
		ForestryItem.cratedPhosphor.registerItem(new ItemCrated(ForestryItem.phosphor.getItemStack()), "cratedPhosphor");
		ForestryItem.cratedAsh.registerItem(new ItemCrated(ForestryItem.ash.getItemStack()), "cratedAsh");
		ForestryItem.cratedCharcoal.registerItem(new ItemCrated(new ItemStack(Items.coal, 1, 1)), "cratedCharcoal");
		ForestryItem.cratedGravel.registerItem(new ItemCrated(new ItemStack(Blocks.gravel)), "cratedGravel");
		ForestryItem.cratedCoal.registerItem(new ItemCrated(new ItemStack(Items.coal, 1, 0)), "cratedCoal");
		ForestryItem.cratedSeeds.registerItem(new ItemCrated(new ItemStack(Items.wheat_seeds)), "cratedSeeds");
		ForestryItem.cratedSaplings.registerItem(new ItemCrated(new ItemStack(Blocks.sapling)), "cratedSaplings");
	}

	@Override
	protected void registerRecipes() {

		/* SMELTING RECIPES */
		Proxies.common.addSmelting(ForestryBlock.resources.getItemStack(1, 0), ForestryItem.apatite.getItemStack(), 0.5f);
		Proxies.common.addSmelting(ForestryBlock.resources.getItemStack(1, 1), ForestryItem.ingotCopper.getItemStack(), 0.5f);
		Proxies.common.addSmelting(ForestryBlock.resources.getItemStack(1, 2), ForestryItem.ingotTin.getItemStack(), 0.5f);

		/* BRONZE INGOTS */
		if (Config.getCraftingBronzeEnabled())
			Proxies.common.addShapelessRecipe(ForestryItem.ingotBronze.getItemStack(4), "ingotTin", "ingotCopper", "ingotCopper", "ingotCopper");

		/* STURDY MACHINE */
		Proxies.common.addRecipe(ForestryItem.sturdyCasing.getItemStack(), "###", "# #", "###", '#', "ingotBronze");

		// / EMPTY CANS
		Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.can"), " # ", "# #", '#', "ingotTin");

		// / GEARS
		if (PluginBuildCraft.stoneGear != null) {

			Proxies.common.addRecipe(ForestryItem.gearBronze.getItemStack(), " # ", "#Y#", " # ", '#', "ingotBronze", 'Y', PluginBuildCraft.stoneGear);
			Proxies.common.addRecipe(ForestryItem.gearCopper.getItemStack(), " # ", "#Y#", " # ", '#', "ingotCopper", 'Y', PluginBuildCraft.stoneGear);
			Proxies.common.addRecipe(ForestryItem.gearTin.getItemStack(), " # ", "#Y#", " # ", '#', "ingotTin", 'Y', PluginBuildCraft.stoneGear);

		} else {

			Proxies.common.addRecipe(ForestryItem.gearBronze.getItemStack(), " # ", "#X#", " # ", '#', "ingotBronze", 'X', "ingotCopper");
			Proxies.common.addRecipe(ForestryItem.gearCopper.getItemStack(), " # ", "#X#", " # ", '#', "ingotCopper", 'X', "ingotCopper");
			Proxies.common.addRecipe(ForestryItem.gearTin.getItemStack(), " # ", "#X#", " # ", '#', "ingotTin", 'X', "ingotCopper");
		}

		// / SURVIVALIST TOOLS
		Proxies.common.addRecipe(ForestryItem.bronzePickaxe.getItemStack(), " X ", " X ", "###", '#', "ingotBronze", 'X', "stickWood");
		Proxies.common.addRecipe(ForestryItem.bronzeShovel.getItemStack(), " X ", " X ", " # ", '#', "ingotBronze", 'X', "stickWood");
		Proxies.common.addShapelessRecipe(ForestryItem.kitPickaxe.getItemStack(), ForestryItem.bronzePickaxe, ForestryItem.carton);
		Proxies.common.addShapelessRecipe(ForestryItem.kitShovel.getItemStack(), ForestryItem.bronzeShovel, ForestryItem.carton);

		/* NATURALIST'S ARMOR */
		Proxies.common.addRecipe(ForestryItem.naturalistHat.getItemStack(), " X ", "Y Y", 'X', "ingotBronze", 'Y', Blocks.glass_pane);

		// / WRENCH
		Proxies.common.addRecipe(ForestryItem.wrench.getItemStack(), "# #", " # ", " # ", '#', "ingotBronze");

		// Manure and Fertilizer
		if (GameMode.getGameMode().getStackSetting("recipe.output.compost.wheat").stackSize > 0)
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.compost.wheat"), " X ", "X#X", " X ", '#', Blocks.dirt, 'X', Items.wheat);
		if (GameMode.getGameMode().getStackSetting("recipe.output.compost.ash").stackSize > 0)
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.compost.ash"), " X ", "X#X", " X ", '#', Blocks.dirt, 'X', "dustAsh");
		if (GameMode.getGameMode().getStackSetting("recipe.output.fertilizer.apatite").stackSize > 0)
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.fertilizer.apatite"), " # ", " X ", " # ", '#', Blocks.sand, 'X', "gemApatite");
		if (GameMode.getGameMode().getStackSetting("recipe.output.fertilizer.ash").stackSize > 0)
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.fertilizer.ash"), "###", "#X#", "###", '#', "dustAsh", 'X', "gemApatite");

		// Humus
		if (GameMode.getGameMode().getStackSetting("recipe.output.humus.compost").stackSize > 0)
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.humus.compost"), "###", "#X#", "###", '#', Blocks.dirt, 'X', ForestryItem.fertilizerBio);
		if (GameMode.getGameMode().getStackSetting("recipe.output.humus.fertilizer").stackSize > 0)
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.humus.fertilizer"), "###", "#X#", "###", '#', Blocks.dirt, 'X', ForestryItem.fertilizerCompound);

		// Bog earth
		if (GameMode.getGameMode().getStackSetting("recipe.output.bogearth.bucket").stackSize > 0)
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.bogearth.bucket"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', Items.water_bucket, 'Y', Blocks.sand);

		if (GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can").stackSize > 0) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', ForestryItem.canWater, 'Y', Blocks.sand);
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', ForestryItem.waxCapsuleWater, 'Y', Blocks.sand);
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.bogearth.can"), "#Y#", "YXY", "#Y#", '#', Blocks.dirt, 'X', ForestryItem.refractoryWater, 'Y', Blocks.sand);
		}

		// Vials and catalyst
		Proxies.common.addRecipe(ForestryItem.vialCatalyst.getItemStack(3), "###", "YXY", '#', ForestryItem.waxCapsule.item(), 'X', Items.bone, 'Y', ForestryItem.fertilizerCompound);
		Proxies.common.addRecipe(ForestryItem.vialCatalyst.getItemStack(3), "###", "YXY", '#', ForestryItem.canEmpty.item(), 'X', Items.bone, 'Y', ForestryItem.fertilizerCompound);

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
		return new IFuelHandler() {

			@Override
			public int getBurnTime(ItemStack fuel) {
				if (fuel != null && fuel.getItem() == ForestryItem.peat.item())
					return 2000;
				if (fuel != null && fuel.getItem() == ForestryItem.bituminousPeat.item())
					return 4200;

				return 0;
			}
		};
	}
}
