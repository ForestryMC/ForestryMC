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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHive;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.CommandBeekeepingMode;
import forestry.apiculture.CommandGiveBee;
import forestry.apiculture.FlowerProviderCacti;
import forestry.apiculture.FlowerProviderEnd;
import forestry.apiculture.FlowerProviderGourd;
import forestry.apiculture.FlowerProviderJungle;
import forestry.apiculture.FlowerProviderMushroom;
import forestry.apiculture.FlowerProviderNetherwart;
import forestry.apiculture.FlowerProviderVanilla;
import forestry.apiculture.FlowerProviderWheat;
import forestry.apiculture.GuiHandlerApiculture;
import forestry.apiculture.PacketHandlerApiculture;
import forestry.apiculture.SaveEventHandlerApiculture;
import forestry.apiculture.VillageHandlerApiculture;
import forestry.apiculture.gadgets.BlockAlveary;
import forestry.apiculture.gadgets.BlockBeehives;
import forestry.apiculture.gadgets.BlockCandle;
import forestry.apiculture.gadgets.BlockStump;
import forestry.apiculture.gadgets.StructureLogicAlveary;
import forestry.apiculture.gadgets.TileAlvearyFan;
import forestry.apiculture.gadgets.TileAlvearyHeater;
import forestry.apiculture.gadgets.TileAlvearyHygroregulator;
import forestry.apiculture.gadgets.TileAlvearyPlain;
import forestry.apiculture.gadgets.TileAlvearySieve;
import forestry.apiculture.gadgets.TileAlvearyStabiliser;
import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.apiculture.gadgets.TileApiaristChest;
import forestry.apiculture.gadgets.TileApiary;
import forestry.apiculture.gadgets.TileBeehouse;
import forestry.apiculture.gadgets.TileCandle;
import forestry.apiculture.gadgets.TileSwarm;
import forestry.apiculture.genetics.AlleleBeeSpecies;
import forestry.apiculture.genetics.AlleleEffectAggressive;
import forestry.apiculture.genetics.AlleleEffectCreeper;
import forestry.apiculture.genetics.AlleleEffectExploration;
import forestry.apiculture.genetics.AlleleEffectGlacial;
import forestry.apiculture.genetics.AlleleEffectHeroic;
import forestry.apiculture.genetics.AlleleEffectIgnition;
import forestry.apiculture.genetics.AlleleEffectMiasmic;
import forestry.apiculture.genetics.AlleleEffectMisanthrope;
import forestry.apiculture.genetics.AlleleEffectNone;
import forestry.apiculture.genetics.AlleleEffectPotion;
import forestry.apiculture.genetics.AlleleEffectRadioactive;
import forestry.apiculture.genetics.AlleleEffectRepulsion;
import forestry.apiculture.genetics.AlleleEffectResurrection;
import forestry.apiculture.genetics.AlleleEffectSnowing;
import forestry.apiculture.genetics.AlleleFlowers;
import forestry.apiculture.genetics.Bee;
import forestry.apiculture.genetics.BeeHelper;
import forestry.apiculture.genetics.BeeMutation;
import forestry.apiculture.genetics.BeeTemplates;
import forestry.apiculture.genetics.BeekeepingMode;
import forestry.apiculture.genetics.BranchBees;
import forestry.apiculture.genetics.HiveDrop;
import forestry.apiculture.genetics.JubilanceNone;
import forestry.apiculture.genetics.JubilanceProviderHermit;
import forestry.apiculture.genetics.MutationTimeLimited;
import forestry.apiculture.items.ItemAlvearyBlock;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemBeealyzer;
import forestry.apiculture.items.ItemCandleBlock;
import forestry.apiculture.items.ItemHiveFrame;
import forestry.apiculture.items.ItemHoneycomb;
import forestry.apiculture.items.ItemImprinter;
import forestry.apiculture.items.ItemWaxCast;
import forestry.apiculture.proxy.ProxyApiculture;
import forestry.apiculture.trigger.TriggerNoFrames;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveEnd;
import forestry.apiculture.worldgen.HiveForest;
import forestry.apiculture.worldgen.HiveJungle;
import forestry.apiculture.worldgen.HiveMeadows;
import forestry.apiculture.worldgen.HiveParched;
import forestry.apiculture.worldgen.HiveSnow;
import forestry.apiculture.worldgen.HiveSwamp;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.gadgets.TileAnalyzer;
import forestry.core.genetics.Allele;
import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.items.ItemCrated;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemOverlay;
import forestry.core.items.ItemOverlay.OverlayInfo;
import forestry.core.items.ItemScoop;
import forestry.core.proxy.Proxies;
import forestry.core.render.EntitySnowFX;
import forestry.core.triggers.Trigger;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.ShapedRecipeCustom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.IIcon;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(pluginID = "Apiculture", name = "Apiculture", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.apiculture.description")
public class PluginApiculture extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.apiculture.proxy.ClientProxyApiculture", serverSide = "forestry.apiculture.proxy.ProxyApiculture")
	public static ProxyApiculture proxy;
	private static final String CONFIG_CATEGORY = "apiculture";
	private Configuration apicultureConfig;
	public static String beekeepingMode = "NORMAL";
	public static int beeCycleTicks = 550;
	public static boolean apiarySideSensitive = false;
	public static boolean fancyRenderedBees = false;
	public static Trigger triggerNoFrames;
	private ArrayList<IHiveDrop> forestDrops;
	private ArrayList<IHiveDrop> meadowsDrops;
	private ArrayList<IHiveDrop> desertDrops;
	private ArrayList<IHiveDrop> jungleDrops;
	private ArrayList<IHiveDrop> endDrops;
	private ArrayList<IHiveDrop> snowDrops;
	private ArrayList<IHiveDrop> swampDrops;
	private ArrayList<IHiveDrop> swarmDrops;
	/**
	 * See {@link IBeeRoot} for details
	 */
	public static IBeeRoot beeInterface;
	public static MachineDefinition definitionApiary;
	public static MachineDefinition definitionChest;
	public static MachineDefinition definitionBeehouse;
	public static MachineDefinition definitionAnalyzer;

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(this);

		createHiveDropArrays();

		ForestryBlock.apiculture.registerBlock(new BlockBase(Material.iron), ItemForestryBlock.class, "apiculture");
		ForestryBlock.apiculture.block().setCreativeTab(Tabs.tabApiculture);

		definitionApiary = ((BlockBase) ForestryBlock.apiculture.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_APIARY_META, "forestry.Apiary", TileApiary.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.apiculture.getItemStack(1, Defaults.DEFINITION_APIARY_META),
						"XXX",
						"#C#",
						"###",
						'X', "slabWood",
						'#', "plankWood",
						'C', ForestryItem.impregnatedCasing))
				.setFaces(0, 1, 2, 2, 4, 4, 0, 7));

		definitionChest = ((BlockBase) ForestryBlock.apiculture.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_APIARISTCHEST_META, "forestry.ApiaristChest", TileApiaristChest.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.apiculture.getItemStack(1, Defaults.DEFINITION_APIARISTCHEST_META),
						" # ",
						"XYX",
						"XXX",
						'#', Blocks.glass,
						'X', "beeComb",
						'Y', Blocks.chest))
				.setFaces(0, 1, 2, 3, 4, 4));

		definitionBeehouse = ((BlockBase) ForestryBlock.apiculture.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_BEEHOUSE_META, "forestry.Beehouse", TileBeehouse.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.apiculture.getItemStack(1, Defaults.DEFINITION_BEEHOUSE_META),
						"XXX",
						"#C#",
						"###",
						'X', "slabWood",
						'#', "plankWood",
						'C', "beeComb"))
				.setFaces(0, 1, 2, 2, 4, 4, 0, 7));

		definitionAnalyzer = ((BlockBase) ForestryBlock.core.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_ANALYZER_META, "forestry.Analyzer", TileAnalyzer.class,
				PluginApiculture.proxy.getRendererAnalyzer(Defaults.TEXTURE_PATH_BLOCKS + "/analyzer_")));

		ForestryBlock.beehives.registerBlock(new BlockBeehives(), ItemForestryBlock.class, "beehives");
		ForestryBlock.beehives.block().setHarvestLevel("pickaxe", 1, 0);
		ForestryBlock.beehives.block().setHarvestLevel("scoop", 0, 1);
		ForestryBlock.beehives.block().setHarvestLevel("scoop", 0, 2);
		ForestryBlock.beehives.block().setHarvestLevel("scoop", 0, 3);
		ForestryBlock.beehives.block().setHarvestLevel("scoop", 0, 4);
		ForestryBlock.beehives.block().setHarvestLevel("scoop", 0, 5);
		ForestryBlock.beehives.block().setHarvestLevel("scoop", 0, 6);
		ForestryBlock.beehives.block().setHarvestLevel("scoop", 0, 7);
		ForestryBlock.beehives.block().setHarvestLevel("scoop", 0, 8);

		createHives();

		// Init bee interface
		AlleleManager.alleleRegistry.registerSpeciesRoot(PluginApiculture.beeInterface = new BeeHelper());
		BeeManager.villageBees = new ArrayList[]{new ArrayList<IBeeGenome>(), new ArrayList<IBeeGenome>()};

		// Candles
		ForestryBlock.candle.registerBlock(new BlockCandle(), ItemCandleBlock.class, "candle");
		ForestryBlock.stump.registerBlock(new BlockStump(), ItemForestryBlock.class, "stump");

		// Alveary and Components
		ForestryBlock.alveary.registerBlock(new BlockAlveary(), ItemAlvearyBlock.class, "alveary");
		ForestryBlock.alveary.block().setHarvestLevel("axe", 0);

		// Add triggers
		triggerNoFrames = new TriggerNoFrames();

		// Register village components with the Structure registry.
		VillageHandlerApiculture.registerVillageComponents();
	}

	@Override
	public void doInit() {
		super.doInit();

		apicultureConfig = new Configuration();

		Property property = apicultureConfig.get("apiary.sidesensitive", CONFIG_CATEGORY, apiarySideSensitive);
		property.Comment = "set to false if apiaries should output all items regardless of side a pipe is attached to";
		apiarySideSensitive = Boolean.parseBoolean(property.Value);

		property = apicultureConfig.get("render.bees.fancy", CONFIG_CATEGORY, fancyRenderedBees);
		property.Comment = "set to true to enable a fancy butterfly-like renderer for bees. (experimental!)";
		fancyRenderedBees = Boolean.parseBoolean(property.Value);

		property = apicultureConfig.get("beekeeping.mode", CONFIG_CATEGORY, "NORMAL");
		property.Comment = "change beekeeping modes here. possible values EASY, NORMAL, HARD, HARDCORE, INSANE. mods may add additional modes.";
		beekeepingMode = property.Value.trim();
		Proxies.log.finer("Beekeeping mode read from config: " + beekeepingMode);

		property = apicultureConfig.get("beekeeping.flowers.custom", CONFIG_CATEGORY, "");
		property.Comment = "add additional flower blocks for apiaries here in the format id:meta. separate blocks using ';'. will be treated like vanilla flowers. not recommended for flowers implemented as tile entities.";
		parseAdditionalFlowers(property.Value, FlowerManager.plainFlowers);

		property = apicultureConfig.get("species.blacklist", CONFIG_CATEGORY, "");
		property.Comment = "add species to blacklist identified by their uid and seperated with ';'.";
		parseBeeBlacklist(property.Value);

		apicultureConfig.save();

		createAlleles();
		createMutations();
		registerBeehiveDrops();

		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.easy);
		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.normal);
		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.hard);
		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.hardcore);
		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.insane);

		// Inducers for swarmer
		BeeManager.inducers.put(ForestryItem.royalJelly.getItemStack(), 10);

		registerTemplates();

		definitionAnalyzer.register();
		definitionApiary.register();
		definitionBeehouse.register();
		definitionChest.register();

		GameRegistry.registerTileEntity(TileAlvearyPlain.class, "forestry.Alveary");
		GameRegistry.registerTileEntity(TileSwarm.class, "forestry.Swarm");
		GameRegistry.registerTileEntity(TileAlvearySwarmer.class, "forestry.AlvearySwarmer");
		GameRegistry.registerTileEntity(TileAlvearyHeater.class, "forestry.AlvearyHeater");
		GameRegistry.registerTileEntity(TileAlvearyFan.class, "forestry.AlvearyFan");
		GameRegistry.registerTileEntity(TileAlvearyHygroregulator.class, "forestry.AlvearyHygro");
		GameRegistry.registerTileEntity(TileAlvearyStabiliser.class, "forestry.AlvearyStabiliser");
		GameRegistry.registerTileEntity(TileAlvearySieve.class, "forestry.AlvearySieve");
		GameRegistry.registerTileEntity(TileCandle.class, "forestry.Candle");

		BeeManager.villageBees[0].add(beeInterface.templateAsGenome(BeeTemplates.getForestTemplate()));
		BeeManager.villageBees[0].add(beeInterface.templateAsGenome(BeeTemplates.getMeadowsTemplate()));
		BeeManager.villageBees[0].add(beeInterface.templateAsGenome(BeeTemplates.getModestTemplate()));
		BeeManager.villageBees[0].add(beeInterface.templateAsGenome(BeeTemplates.getMarshyTemplate()));
		BeeManager.villageBees[0].add(beeInterface.templateAsGenome(BeeTemplates.getWintryTemplate()));
		BeeManager.villageBees[0].add(beeInterface.templateAsGenome(BeeTemplates.getTropicalTemplate()));

		BeeManager.villageBees[1].add(beeInterface.templateAsGenome(BeeTemplates.getForestRainResistTemplate()));
		BeeManager.villageBees[1].add(beeInterface.templateAsGenome(BeeTemplates.getCommonTemplate()));
		BeeManager.villageBees[1].add(beeInterface.templateAsGenome(BeeTemplates.getValiantTemplate()));

		// Register villager stuff
		VillageHandlerApiculture villageHandler = new VillageHandlerApiculture();
		VillagerRegistry.instance().registerVillageCreationHandler(villageHandler);
		VillagerRegistry.instance().registerVillagerId(Defaults.ID_VILLAGER_BEEKEEPER);
		Proxies.render.registerVillagerSkin(Defaults.ID_VILLAGER_BEEKEEPER, Defaults.TEXTURE_SKIN_BEEKPEEPER);
		VillagerRegistry.instance().registerVillageTradeHandler(Defaults.ID_VILLAGER_BEEKEEPER, villageHandler);

		// Register world gen
		if (Config.generateBeehives)
			MinecraftForge.EVENT_BUS.register(HiveDecorator.instance());

		proxy.initializeRendering();
	}

	@Override
	public void postInit() {
		super.postInit();
		registerDungeonLoot();
		updateHiveDrops();
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerApiculture();
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerApiculture();
	}

	@Override
	protected void registerItems() {

		// / BEES
		ForestryItem.beeQueenGE.registerItem((new ItemBeeGE(EnumBeeType.QUEEN)), "beeQueenGE");
		ForestryItem.beeDroneGE.registerItem((new ItemBeeGE(EnumBeeType.DRONE)), "beeDroneGE");
		ForestryItem.beePrincessGE.registerItem((new ItemBeeGE(EnumBeeType.PRINCESS)), "beePrincessGE");
		ForestryItem.beeLarvaeGE.registerItem((new ItemBeeGE(EnumBeeType.LARVAE)), "beeLarvaeGE");

		ForestryItem.beealyzer.registerItem((new ItemBeealyzer()), "beealyzer");
		// Disabling Habitat Locator, because it's b0rked and seems to flubber up other icons.
		/*ForestryItem.biomeFinder = new ItemBiomefinder(Config.getOrCreateItemIdProperty("biomeFinder", Defaults.ID_ITEM_BIOME_FINDER))
		 .setUnlocalizedName("biomeFinder");*/
		ForestryItem.imprinter.registerItem((new ItemImprinter()), "imprinter");

		// / COMB FRAMES
		ForestryItem.frameUntreated.registerItem(new ItemHiveFrame(80, 0.9f), "frameUntreated");
		ForestryItem.frameImpregnated.registerItem(new ItemHiveFrame(240, 0.4f), "frameImpregnated");
		ForestryItem.frameProven.registerItem(new ItemHiveFrame(720, 0.3f), "frameProven");

		// / BEE RESOURCES
		ForestryItem.honeyDrop.registerItem(new ItemOverlay(Tabs.tabApiculture,
				new OverlayInfo("honey", 0xecb42d, 0xe8c814), new OverlayInfo("charged", 0x800505, 0x9c0707).setIsSecret(),
				new OverlayInfo("omega", 0x191919, 0x4a8ca7).setIsSecret()), "honeyDrop");
		OreDictionary.registerOre("dropHoney", ForestryItem.honeyDrop.getItemStack());

		ForestryItem.pollenCluster.registerItem(new ItemOverlay(Tabs.tabApiculture,
				new OverlayInfo("normal", 0xa28a25, 0xa28a25),
				new OverlayInfo("crystalline", 0xffffff, 0xc5feff)),
				"pollen");
		OreDictionary.registerOre("itemPollen", ForestryItem.pollenCluster.getItemStack());

		ForestryItem.propolis.registerItem(new ItemOverlay(Tabs.tabApiculture,
				new OverlayInfo("normal", 0xc5b24e), new OverlayInfo("sticky", 0xc68e57), new OverlayInfo("pulsating", 0x2ccdb1).setIsSecret(),
				new OverlayInfo("silky", 0xddff00)),
				"propolis");

		ForestryItem.honeydew.registerItem(new ItemForestry().setCreativeTab(Tabs.tabApiculture), "honeydew");
		OreDictionary.registerOre("dropHoneydew", ForestryItem.honeydew.getItemStack());

		ForestryItem.royalJelly.registerItem(new ItemForestry().setCreativeTab(Tabs.tabApiculture), "royalJelly");
		OreDictionary.registerOre("dropRoyalJelly", ForestryItem.royalJelly.getItemStack());

		ForestryItem.waxCast.registerItem(new ItemWaxCast().setCreativeTab(Tabs.tabApiculture), "waxCast");

		// / BEE COMBS
		ForestryItem.beeComb.registerItem(new ItemHoneycomb(), "beeCombs");
		OreDictionary.registerOre("beeComb", ForestryItem.beeComb.getItemStack(1, Defaults.WILDCARD));

		// / APIARIST'S CLOTHES
		ForestryItem.apiaristHat.registerItem(new ItemArmorApiarist(0), "apiaristHelmet");
		ForestryItem.apiaristChest.registerItem(new ItemArmorApiarist(1), "apiaristChest");
		ForestryItem.apiaristLegs.registerItem(new ItemArmorApiarist(2), "apiaristLegs");
		ForestryItem.apiaristBoots.registerItem(new ItemArmorApiarist(3), "apiaristBoots");

		// TOOLS
		ForestryItem.scoop.registerItem(new ItemScoop(), "scoop");
		ForestryItem.scoop.item().setHarvestLevel("scoop", 3);
	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerCrates() {
		ForestryItem.cratedBeeswax.registerItem(new ItemCrated(ForestryItem.beeswax.getItemStack()), "cratedBeeswax");
		ForestryItem.cratedPollen.registerItem(new ItemCrated(ForestryItem.pollenCluster.getItemStack()), "cratedPollen");
		ForestryItem.cratedPropolis.registerItem(new ItemCrated(ForestryItem.propolis.getItemStack()), "cratedPropolis");
		ForestryItem.cratedHoneydew.registerItem(new ItemCrated(ForestryItem.honeydew.getItemStack()), "cratedHoneydew");
		ForestryItem.cratedRoyalJelly.registerItem(new ItemCrated(ForestryItem.royalJelly.getItemStack()), "cratedRoyalJelly");

		ForestryItem.cratedHoneycombs.registerItem(new ItemCrated(ForestryItem.beeComb.getItemStack(1, 0)), "cratedHoneycombs");
		ForestryItem.cratedCocoaComb.registerItem(new ItemCrated(ForestryItem.beeComb.getItemStack(1, 1)), "cratedCocoaComb");
		ForestryItem.cratedSimmeringCombs.registerItem(new ItemCrated(ForestryItem.beeComb.getItemStack(1, 2)), "cratedSimmeringCombs");
		ForestryItem.cratedStringyCombs.registerItem(new ItemCrated(ForestryItem.beeComb.getItemStack(1, 3)), "cratedStringyCombs");
		ForestryItem.cratedFrozenCombs.registerItem(new ItemCrated(ForestryItem.beeComb.getItemStack(1, 4)), "cratedFrozenCombs");
		ForestryItem.cratedDrippingCombs.registerItem(new ItemCrated(ForestryItem.beeComb.getItemStack(1, 5)), "cratedDrippingCombs");

		ForestryItem.cratedRefractoryWax.registerItem(new ItemCrated(ForestryItem.refractoryWax.getItemStack()), "cratedRefractoryWax");

	}

	@Override
	protected void registerRecipes() {

		// / APIARIST'S ARMOR
		Proxies.common.addRecipe(ForestryItem.apiaristHat.getItemStack(),
				"###", "# #",
				'#', ForestryItem.craftingMaterial.getItemStack(1, 3));
		Proxies.common.addRecipe(ForestryItem.apiaristChest.getItemStack(),
				"# #", "###", "###",
				'#', ForestryItem.craftingMaterial.getItemStack(1, 3));
		Proxies.common.addRecipe(ForestryItem.apiaristLegs.getItemStack(),
				"###", "# #", "# #",
				'#', ForestryItem.craftingMaterial.getItemStack(1, 3));
		Proxies.common.addRecipe(ForestryItem.apiaristBoots.getItemStack(),
				"# #", "# #",
				'#', ForestryItem.craftingMaterial.getItemStack(1, 3));

		// / HABITAT LOCATOR
		//Proxies.common.addRecipe(ForestryItem.biomeFinder.getItemStack(),
		//		new Object[] { " X ", "X#X", " X ", '#', Items.redstone, 'X', "ingotBronze" });
		Proxies.common.addRecipe(ForestryItem.vialCatalyst.getItemStack(3),
				"###", "YXY",
				'#', ForestryItem.waxCapsule,
				'X', Items.bone,
				'Y', ForestryItem.pollenCluster);
		Proxies.common.addRecipe(ForestryItem.vialCatalyst.getItemStack(3),
				"###", "YXY",
				'#', ForestryItem.canEmpty,
				'X', Items.bone,
				'Y', ForestryItem.pollenCluster);
		Proxies.common.addRecipe(ForestryItem.vialCatalyst.getItemStack(),
				"###", "YXY", "###",
				'#', ForestryItem.honeyDrop,
				'Y', ForestryItem.fertilizerCompound,
				'X', ForestryItem.waxCapsule);
		Proxies.common.addRecipe(ForestryItem.vialCatalyst.getItemStack(),
				"###", "YXY", "###",
				'#', ForestryItem.honeyDrop,
				'Y', ForestryItem.fertilizerCompound,
				'X', ForestryItem.canEmpty);
		Proxies.common.addRecipe(ForestryItem.vialCatalyst.getItemStack(),
				"###", "YXY", "###",
				'#', ForestryItem.honeyDrop,
				'Y', ForestryItem.pollenCluster,
				'X', ForestryItem.waxCapsule);
		Proxies.common.addRecipe(ForestryItem.vialCatalyst.getItemStack(),
				"###", "YXY", "###",
				'#', ForestryItem.honeyDrop,
				'Y', ForestryItem.pollenCluster,
				'X', ForestryItem.canEmpty);

		// Bees
		Proxies.common.addRecipe(ForestryItem.scoop.getItemStack(1),
				"#X#", "###", " # ",
				'#', "stickWood",
				'X', Blocks.wool);
		Proxies.common.addRecipe(new ItemStack(Items.slime_ball),
				"#X#", "#X#", "#X#",
				'#', ForestryItem.propolis,
				'X', ForestryItem.pollenCluster);
		Proxies.common.addRecipe(new ItemStack(Items.speckled_melon),
				"#X#", "#Y#", "#X#",
				'#', ForestryItem.honeyDrop,
				'X', ForestryItem.honeydew,
				'Y', Items.melon);
		Proxies.common.addRecipe(ForestryItem.frameUntreated.getItemStack(),
				"###", "#S#", "###",
				'#', "stickWood",
				'S', Items.string);
		Proxies.common.addRecipe(ForestryItem.frameImpregnated.getItemStack(),
				"###", "#S#", "###",
				'#', ForestryItem.stickImpregnated,
				'S', Items.string);

		// FOOD STUFF
		if (ForestryItem.honeyedSlice.item() != null)
			Proxies.common.addRecipe(ForestryItem.honeyedSlice.getItemStack(4),
					"###", "#X#", "###",
					'#', ForestryItem.honeyDrop,
					'X', Items.bread);
		if (ForestryItem.honeyPot.item() != null)
			Proxies.common.addRecipe(ForestryItem.honeyPot.getItemStack(1),
					"# #", " X ", "# #",
					'#', ForestryItem.honeyDrop,
					'X', ForestryItem.waxCapsule);
		if (ForestryItem.ambrosia.item() != null)
			Proxies.common.addRecipe(ForestryItem.ambrosia.getItemStack(),
					"#Y#", "XXX", "###",
					'#', ForestryItem.honeydew,
					'X', ForestryItem.royalJelly,
					'Y', ForestryItem.waxCapsule);

		// / CAPSULES
		Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.capsule"), "###", '#', ForestryItem.beeswax);
		Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.refractory"), "###", '#', ForestryItem.refractoryWax);

		// / BITUMINOUS PEAT
		Proxies.common.addRecipe(ForestryItem.bituminousPeat.getItemStack(),
				" # ", "XYX", " # ",
				'#', "dustAsh",
				'X', ForestryItem.peat,
				'Y', ForestryItem.propolis);
		// / TORCHES
		Proxies.common.addRecipe(new ItemStack(Blocks.torch, 3),
				" # ", " # ", " Y ",
				'#', ForestryItem.beeswax,
				'Y', "stickWood");
		Proxies.common.addRecipe(ForestryItem.craftingMaterial.getItemStack(1, 1),
				"# #", " # ", "# #",
				'#', ForestryItem.propolis.getItemStack(1, 2));

		// / CANDLES
		RecipeManagers.carpenterManager.addRecipe(30, LiquidHelper.getLiquid(Defaults.LIQUID_WATER, 600), null, ForestryBlock.candle.getItemStack(24),
				" X ",
				"###",
				"###",
				'#', ForestryItem.beeswax,
				'X', Items.string);
		RecipeManagers.carpenterManager.addRecipe(10, LiquidHelper.getLiquid(Defaults.LIQUID_WATER, 200), null, ForestryBlock.candle.getItemStack(6),
				"#X#",
				'#', ForestryItem.beeswax,
				'X', ForestryItem.craftingMaterial.getItemStack(1, 2));
		Proxies.common.addShapelessRecipe(ForestryBlock.candle.getItemStack(), ForestryBlock.candle.getItemStack());
		Proxies.common.addShapelessRecipe(ForestryBlock.candle.getItemStack(1, 1), ForestryBlock.candle.getItemStack(1, 1));

		// / WAX CAST
		Proxies.common.addRecipe(ForestryItem.waxCast.getItemStack(),
				"###",
				"# #",
				"###",
				'#', ForestryItem.beeswax);

		// / ALVEARY
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(),
				"###",
				"#X#",
				"###",
				'X', ForestryItem.impregnatedCasing,
				'#', ForestryItem.craftingMaterial.getItemStack(1, 6));
		// SWARMER
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 2),
				"#G#",
				" X ",
				"#G#",
				'#', ForestryItem.tubes.getItemStack(1, 5),
				'X', ForestryBlock.alveary,
				'G', Items.gold_ingot);
		// FAN
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 3),
				"I I",
				" X ",
				"I#I",
				'#', ForestryItem.tubes.getItemStack(1, 4),
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot);
		// HEATER
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 4),
				"#I#",
				" X ",
				"YYY",
				'#', ForestryItem.tubes.getItemStack(1, 4),
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot, 'Y', Blocks.stone);
		// HYGROREGULATOR
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 5),
				"GIG",
				"GXG",
				"GIG",
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot,
				'G', Blocks.glass);
		// STABILISER
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 6),
				"G G",
				"GXG",
				"G G",
				'X', ForestryBlock.alveary,
				'G', Items.quartz);
		// SIEVE
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, TileAlvearySieve.BLOCK_META),
				"III",
				" X ",
				"WWW",
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot,
				'W', ForestryItem.craftingMaterial.getItemStack(1, 3));

		// / SQUEEZER
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.honeyDrop.getItemStack()}, LiquidHelper.getLiquid(Defaults.LIQUID_HONEY, Defaults.FLUID_PER_HONEY_DROP),
				ForestryItem.propolis.getItemStack(), 5);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.honeydew.getItemStack()}, LiquidHelper.getLiquid(Defaults.LIQUID_HONEY, Defaults.FLUID_PER_HONEY_DROP));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.phosphor.getItemStack(2), new ItemStack(Blocks.sand)}, LiquidHelper.getLiquid(Defaults.LIQUID_LAVA, 2000));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.phosphor.getItemStack(2), new ItemStack(Blocks.dirt)}, LiquidHelper.getLiquid(Defaults.LIQUID_LAVA, 1600));

		// / CARPENTER
		RecipeManagers.carpenterManager.addRecipe(100, LiquidHelper.getLiquid(Defaults.LIQUID_WATER, 2000), null, ForestryItem.beealyzer.getItemStack(),
				"X#X", "X#X", "RDR",
				'#', Blocks.glass_pane,
				'X', "ingotTin",
				'R', Items.redstone,
				'D', Items.diamond);
		RecipeManagers.carpenterManager.addRecipe(50, LiquidHelper.getLiquid(Defaults.LIQUID_HONEY, 500), null, ForestryItem.craftingMaterial.getItemStack(1, 6),
				" J ", "###", "WPW",
				'#', "plankWood",
				'J', ForestryItem.royalJelly,
				'W', ForestryItem.beeswax,
				'P', ForestryItem.pollenCluster);

		// / CENTRIFUGE
		// Honey combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 0), ForestryItem.beeswax.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 90);
		// Cocoa combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 1), ForestryItem.beeswax.getItemStack(), new ItemStack(Items.dye, 1, 3), 50);
		// Simmering combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 2), ForestryItem.refractoryWax.getItemStack(), ForestryItem.phosphor.getItemStack(2), 70);
		// Stringy combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 3), ForestryItem.propolis.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 40);
		// Drippig combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 5), ForestryItem.honeydew.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 40);
		// Frozen combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 4), new ItemStack[]{ForestryItem.beeswax.getItemStack(),
			ForestryItem.honeyDrop.getItemStack(), new ItemStack(Items.snowball), ForestryItem.pollenCluster.getItemStack(1, 1)}, new int[]{80, 70, 40, 20});
		// Silky combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 6), ForestryItem.honeyDrop.getItemStack(), ForestryItem.propolis.getItemStack(1, 3), 80);
		// Parched combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 7), ForestryItem.beeswax.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 90);
		// Mysterious combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 8), new ItemStack[]{ForestryItem.honeyDrop.getItemStack(),
			ForestryItem.propolis.getItemStack(1, 2)}, new int[]{40, 100});
		// Irradiated combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 9), new ItemStack[]{}, new int[]{});
		// Powdery combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 10), new ItemStack[]{ForestryItem.honeyDrop.getItemStack(),
			ForestryItem.beeswax.getItemStack(), new ItemStack(Items.gunpowder)}, new int[]{20, 20, 90});
		// Reddened Combs
		RecipeManagers.centrifugeManager.addRecipe(80, ForestryItem.beeComb.getItemStack(1, 11),
				new ItemStack[]{ForestryItem.honeyDrop.getItemStack(2, 1)}, new int[]{100});
		// Darkened Combs
		RecipeManagers.centrifugeManager.addRecipe(80, ForestryItem.beeComb.getItemStack(1, 12),
				new ItemStack[]{ForestryItem.honeyDrop.getItemStack(1, 1)}, new int[]{100});
		// Omega Combs
		RecipeManagers.centrifugeManager.addRecipe(400, ForestryItem.beeComb.getItemStack(1, 13), new ItemStack[]{ForestryItem.honeyDrop.getItemStack(1, 2)}, new int[]{100});
		// Wheaten Combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 14), new ItemStack[]{ForestryItem.honeyDrop.getItemStack(),
			ForestryItem.beeswax.getItemStack(), new ItemStack(Items.wheat)}, new int[]{20, 20, 80});
		// Mossy Combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 15), ForestryItem.beeswax.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 90);
		// Mellow Combs
		RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 16), new ItemStack[]{ForestryItem.honeydew.getItemStack(),
			ForestryItem.beeswax.getItemStack(), new ItemStack(Items.quartz)}, new int[]{60, 20, 30});

		// Silk
		RecipeManagers.centrifugeManager.addRecipe(5, ForestryItem.propolis.getItemStack(1, 3), new ItemStack[]{
			ForestryItem.craftingMaterial.getItemStack(1, 2), ForestryItem.propolis.getItemStack()}, new int[]{60, 10});

		// / FERMENTER
		RecipeManagers.fermenterManager.addRecipe(ForestryItem.honeydew.getItemStack(), 500, 1.0f, LiquidHelper.getLiquid(Defaults.LIQUID_MEAD, 1),
				LiquidHelper.getLiquid(Defaults.LIQUID_HONEY, 1));

		// ANALYZER
		definitionAnalyzer.recipes = createAlyzerRecipes(ForestryBlock.core.block(), Defaults.DEFINITION_ANALYZER_META);
	}

	public IRecipe[] createAlyzerRecipes(Block block, int meta) {
		ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
		recipes.add(ShapedRecipeCustom.createShapedRecipe(new ItemStack(block, 1, meta), "XTX", " Y ", "X X", 'Y', ForestryItem.sturdyCasing, 'T', ForestryItem.beealyzer, 'X', "ingotBronze"));
		recipes.add(ShapedRecipeCustom.createShapedRecipe(new ItemStack(block, 1, meta), "XTX", " Y ", "X X", 'Y', ForestryItem.sturdyCasing, 'T', ForestryItem.treealyzer, 'X', "ingotBronze"));
		return recipes.toArray(new IRecipe[recipes.size()]);
	}

	private void registerBeehiveDrops() {
		forestDrops.add(new HiveDrop(BeeTemplates.getForestTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 0)}, 80).setIgnobleShare(0.7f));
		forestDrops.add(new HiveDrop(BeeTemplates.getForestRainResistTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 0)}, 8));
		forestDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 0)}, 3));

		meadowsDrops.add(new HiveDrop(BeeTemplates.getMeadowsTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 0)}, 80).setIgnobleShare(0.7f));
		meadowsDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 0)}, 3));

		desertDrops.add(new HiveDrop(BeeTemplates.getModestTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 7)}, 80).setIgnobleShare(0.7f));
		desertDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 7)}, 3));

		jungleDrops.add(new HiveDrop(BeeTemplates.getTropicalTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 6)}, 80).setIgnobleShare(0.7f));
		jungleDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 6)}, 3));

		endDrops.add(new HiveDrop(BeeTemplates.getEnderTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 8)}, 90));

		snowDrops.add(new HiveDrop(BeeTemplates.getWintryTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 4)}, 80).setIgnobleShare(0.5f));
		snowDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 4)}, 3));

		swampDrops.add(new HiveDrop(BeeTemplates.getMarshyTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 15)}, 80).setIgnobleShare(0.4f));
		swampDrops.add(new HiveDrop(BeeTemplates.getValiantTemplate(), new ItemStack[]{ForestryItem.beeComb.getItemStack(1, 15)}, 3));
	}

	private void registerDungeonLoot() {
		int rarity;
		if (Config.dungeonLootRare)
			rarity = 5;
		else
			rarity = 10;

		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(getBeeItemFromTemplate(BeeTemplates.getSteadfastTemplate(), EnumBeeType.DRONE), 1, 1, rarity));

		ItemStack stack = ForestryBlock.candle.getItemStack();
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(BlockCandle.colourTagName, 0xffffff);
		stack.setTagCompound(tag);

		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(stack, 7, 12, 12));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.scoop.getItemStack(), 1, 1, 8));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.propolis.getItemStack(), 2, 4, 6));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.beeComb.getItemStack(), 4, 12, 7));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.beeComb.getItemStack(1, 4), 2, 10, 7));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.beeComb.getItemStack(1, 6), 1, 6, 7));

		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(getBeeItemFromTemplate(BeeTemplates.getForestRainResistTemplate(), EnumBeeType.PRINCESS), 1, 1, 5));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(getBeeItemFromTemplate(BeeTemplates.getCommonTemplate(), EnumBeeType.DRONE), 1, 2, 8));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(getBeeItemFromTemplate(BeeTemplates.getMeadowsTemplate(), EnumBeeType.PRINCESS), 1, 1, 5));
	}

	private ItemStack getBeeItemFromTemplate(IAllele[] template, EnumBeeType beeType) {
		IBee bee = new Bee(PluginApiculture.beeInterface.templateAsGenome(template));
		ItemStack beeItem;
		switch (beeType) {
		default:
		case DRONE:
			beeItem = ForestryItem.beeDroneGE.getItemStack();
			break;
		case PRINCESS:
			beeItem = ForestryItem.beePrincessGE.getItemStack();
			break;
		}
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		bee.writeToNBT(nbtTagCompound);
		beeItem.setTagCompound(nbtTagCompound);

		return beeItem;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Deprecated // deprecated since 3.1. remove when BeeManager.hiveDrops is removed
	private void createHiveDropArrays() {

		BeeManager.hiveDrops = new ArrayList[8];

		forestDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[0] = forestDrops;

		meadowsDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[1] = meadowsDrops;

		desertDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[2] = desertDrops;

		jungleDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[3] = jungleDrops;

		endDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[4] = endDrops;

		snowDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[5] = snowDrops;

		swampDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[6] = swampDrops;

		swarmDrops = new ArrayList<IHiveDrop>();
		BeeManager.hiveDrops[7] = swarmDrops;
	}

	private void createHives() {
		HiveManager.put(HiveManager.forest, new HiveForest(3));
		HiveManager.put(HiveManager.meadows, new HiveMeadows(1));
		HiveManager.put(HiveManager.desert, new HiveParched(1));
		HiveManager.put(HiveManager.jungle, new HiveJungle(4));
		HiveManager.put(HiveManager.end, new HiveEnd(4));
		HiveManager.put(HiveManager.snow, new HiveSnow(2));
		HiveManager.put(HiveManager.swamp, new HiveSwamp(2));
	}

	@Deprecated // deprecated since 3.1. remove when BeeManager.hiveDrops is removed
	private void updateHiveDrops() {
		IHive hive = HiveManager.getForestHive();
		for (IHiveDrop drop : forestDrops)
			hive.addDrop(drop);

		hive = HiveManager.getMeadowsHive();
		for (IHiveDrop drop : meadowsDrops)
			hive.addDrop(drop);

		hive = HiveManager.getDesertHive();
		for (IHiveDrop drop : desertDrops)
			hive.addDrop(drop);

		hive = HiveManager.getJungleHive();
		for (IHiveDrop drop : jungleDrops)
			hive.addDrop(drop);

		hive = HiveManager.getEndHive();
		for (IHiveDrop drop : endDrops)
			hive.addDrop(drop);

		hive = HiveManager.getSnowHive();
		for (IHiveDrop drop : snowDrops)
			hive.addDrop(drop);

		hive = HiveManager.getSwampHive();
		for (IHiveDrop drop : swampDrops)
			hive.addDrop(drop);
	}

	private void createAlleles() {

		IClassification hymnoptera = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "hymnoptera", "Hymnoptera");
		AlleleManager.alleleRegistry.getClassification("class.insecta").addMemberGroup(hymnoptera);

		IClassification apidae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "apidae", "Apidae");
		hymnoptera.addMemberGroup(apidae);

		IClassification honey = new BranchBees("honey", "Apis");
		apidae.addMemberGroup(honey);
		IClassification noble = new BranchBees("noble", "Probapis");
		apidae.addMemberGroup(noble);
		IClassification industrious = new BranchBees("industrious", "Industrapis");
		apidae.addMemberGroup(industrious);
		IClassification heroic = new BranchBees("heroic", "Herapis");
		apidae.addMemberGroup(heroic);
		IClassification infernal = new BranchBees("infernal", "Diapis");
		apidae.addMemberGroup(infernal);
		IClassification austere = new BranchBees("austere", "Modapis");
		apidae.addMemberGroup(austere);
		IClassification end = new BranchBees("end", "Finapis");
		apidae.addMemberGroup(end);
		IClassification vengeful = new BranchBees("vengeful", "Punapis");
		apidae.addMemberGroup(vengeful);
		IClassification tropical = new BranchBees("tropical", "Caldapis");
		apidae.addMemberGroup(tropical);
		IClassification frozen = new BranchBees("frozen", "Coagapis");
		apidae.addMemberGroup(frozen);
		IClassification reddened = new BranchBees("reddened", "Rubapis");
		apidae.addMemberGroup(reddened);
		IClassification festive = new BranchBees("festive", "Festapis");
		apidae.addMemberGroup(festive);
		IClassification agrarian = new BranchBees("agrarian", "Rustapis");
		apidae.addMemberGroup(agrarian);
		IClassification boggy = new BranchBees("boggy", "Paludapis");
		apidae.addMemberGroup(boggy);
		IClassification monastic = new BranchBees("monastic", "Monapis");
		apidae.addMemberGroup(monastic);

		// / BEES // SPECIES
		// Common Branch
		Allele.speciesForest = new AlleleBeeSpecies("speciesForest", true, "bees.species.forest", honey, "nigrocincta", 0x19d0ec, 0xffdc16).addProduct(
				ForestryItem.beeComb.getItemStack(1, 0), 30);
		Allele.speciesMeadows = new AlleleBeeSpecies("speciesMeadows", true, "bees.species.meadows", honey, "florea", 0xef131e, 0xffdc16).addProduct(
				ForestryItem.beeComb.getItemStack(1, 0), 30);
		Allele.speciesCommon = new AlleleBeeSpecies("speciesCommon", true, "bees.species.common", honey, "cerana", 0xb2b2b2, 0xffdc16).addProduct(
				ForestryItem.beeComb.getItemStack(1, 0), 35).setIsSecret();
		Allele.speciesCultivated = new AlleleBeeSpecies("speciesCultivated", true, "bees.species.cultivated", honey, "mellifera", 0x5734ec, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 40).setIsSecret();

		// Noble Branch
		Allele.speciesNoble = new AlleleBeeSpecies("speciesNoble", false, "bees.species.noble", noble, "nobilis", 0xec9a19, 0xffdc16).addProduct(
				ForestryItem.beeComb.getItemStack(1, 5), 20).setIsSecret();
		Allele.speciesMajestic = new AlleleBeeSpecies("speciesMajestic", true, "bees.species.majestic", noble, "regalis", 0x7f0000, 0xffdc16).addProduct(
				ForestryItem.beeComb.getItemStack(1, 5), 30).setIsSecret();
		Allele.speciesImperial = new AlleleBeeSpecies("speciesImperial", false, "bees.species.imperial", noble, "imperatorius", 0xa3e02f, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 5), 20).addProduct(ForestryItem.royalJelly.getItemStack(), 15).setHasEffect().setIsSecret();

		// Industrious Branch
		Allele.speciesDiligent = new AlleleBeeSpecies("speciesDiligent", false, "bees.species.diligent", industrious, "sedulus", 0xc219ec, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 3), 20).setIsSecret();
		Allele.speciesUnweary = new AlleleBeeSpecies("speciesUnweary", true, "bees.species.unweary", industrious, "assiduus", 0x19ec5a, 0xffdc16).addProduct(
				ForestryItem.beeComb.getItemStack(1, 3), 30).setIsSecret();
		Allele.speciesIndustrious = new AlleleBeeSpecies("speciesIndustrious", false, "bees.species.industrious", industrious, "industria", 0xffffff, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 3), 20).addProduct(ForestryItem.pollenCluster.getItemStack(), 15).setHasEffect().setIsSecret();

		// Heroic Branch
		Allele.speciesSteadfast = new AlleleBeeSpecies("speciesSteadfast", false, "bees.species.steadfast", heroic, "legio", 0x4d2b15, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 1), 20).setIsSecret().setHasEffect();
		Allele.speciesValiant = new AlleleBeeSpecies("speciesValiant", true, "bees.species.valiant", heroic, "centurio", 0x626bdd, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 1), 30).addSpecialty(new ItemStack(Items.sugar), 15).setIsSecret();
		Allele.speciesHeroic = new AlleleBeeSpecies("speciesHeroic", false, "bees.species.heroic", heroic, "kraphti", 0xb3d5e4, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 1), 40).setIsSecret().setHasEffect();

		// Infernal Branch
		Allele.speciesSinister = new AlleleBeeSpecies("speciesSinister", false, "bees.species.sinister", infernal, "caecus", 0xb3d5e4, 0x9a2323)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 2), 45).setEntityTexture("sinisterBee").setIsSecret().setTemperature(EnumTemperature.HELLISH).setHumidity(EnumHumidity.ARID);
		Allele.speciesFiendish = new AlleleBeeSpecies("speciesFiendish", true, "bees.species.fiendish", infernal, "diabolus", 0xd7bee5, 0x9a2323)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 2), 55).addProduct(ForestryItem.ash.getItemStack(), 15).setEntityTexture("sinisterBee").setIsSecret()
				.setTemperature(EnumTemperature.HELLISH).setHumidity(EnumHumidity.ARID);
		Allele.speciesDemonic = new AlleleBeeSpecies("speciesDemonic", false, "bees.species.demonic", infernal, "draco", 0xf4e400, 0x9a2323)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 2), 45).addProduct(new ItemStack(Items.glowstone_dust), 15).setEntityTexture("sinisterBee").setHasEffect().setIsSecret()
				.setTemperature(EnumTemperature.HELLISH).setHumidity(EnumHumidity.ARID);

		// Austere Branch
		Allele.speciesModest = new AlleleBeeSpecies("speciesModest", false, "bees.species.modest", austere, "modicus", 0xc5be86, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 7), 20).setTemperature(EnumTemperature.HOT).setHumidity(EnumHumidity.ARID);
		Allele.speciesFrugal = new AlleleBeeSpecies("speciesFrugal", true, "bees.species.frugal", austere, "permodestus", 0xe8dcb1, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 7), 30).setIsSecret().setTemperature(EnumTemperature.HOT).setHumidity(EnumHumidity.ARID);
		Allele.speciesAustere = new AlleleBeeSpecies("speciesAustere", false, "bees.species.austere", austere, "correpere", 0xfffac2, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 7), 20).addSpecialty(ForestryItem.beeComb.getItemStack(1, 10), 50).setHasEffect()
				.setIsSecret().setTemperature(EnumTemperature.HOT).setHumidity(EnumHumidity.ARID);

		// / Tropical Branch
		Allele.speciesTropical = new AlleleBeeSpecies("speciesTropical", false, "bees.species.tropical", tropical, "mendelia", 0x378020, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 6), 20).setEntityTexture("tropicalBee").setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		Allele.speciesExotic = new AlleleBeeSpecies("speciesExotic", true, "bees.species.exotic", tropical, "darwini", 0x304903, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 6), 30).setEntityTexture("tropicalBee").setIsSecret().setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		Allele.speciesEdenic = new AlleleBeeSpecies("speciesEdenic", false, "bees.species.edenic", tropical, "humboldti", 0x393d0d, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 6), 20).setEntityTexture("tropicalBee").setHasEffect().setIsSecret().setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);

		// End Branch
		Allele.speciesEnded = new AlleleBeeSpecies("speciesEnded", false, "bees.species.ender", end, "notchi", 0xe079fa, 0xd9de9e)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 8), 30).setEntityTexture("endBee").setIsSecret().setTemperature(EnumTemperature.COLD);
		Allele.speciesSpectral = new AlleleBeeSpecies("speciesSpectral", true, "bees.species.spectral", end, "idolum", 0xa98bed, 0xd9de9e) // 0xa98bed
				.addProduct(ForestryItem.beeComb.getItemStack(1, 8), 50).setEntityTexture("endBee").setIsSecret().setTemperature(EnumTemperature.COLD);
		Allele.speciesPhantasmal = new AlleleBeeSpecies("speciesPhantasmal", false, "bees.species.phantasmal", end, "lemur", 0xcc00fa, 0xd9de9e) // 0x31023a //
				// 0x8bc3ed
				.addProduct(ForestryItem.beeComb.getItemStack(1, 8), 40).setEntityTexture("endBee").setIsSecret().setHasEffect().setTemperature(EnumTemperature.COLD);

		// Frozen Branch
		Allele.speciesWintry = new AlleleBeeSpecies("speciesWintry", false, "bees.species.wintry", frozen, "brumalis", 0xa0ffc8, 0xdaf5f3).addProduct(
				ForestryItem.beeComb.getItemStack(1, 4), 30).setEntityTexture("icyBee").setTemperature(EnumTemperature.ICY);
		Allele.speciesIcy = new AlleleBeeSpecies("speciesIcy", true, "bees.species.icy", frozen, "coagulis", 0xa0ffff, 0xdaf5f3)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 4), 20).setEntityTexture("icyBee").addProduct(ForestryItem.craftingMaterial.getItemStack(1, 5), 20)
				.setTemperature(EnumTemperature.ICY).setIsSecret();
		Allele.speciesGlacial = new AlleleBeeSpecies("speciesGlacial", false, "bees.species.glacial", frozen, "glacialis", 0xefffff, 0xdaf5f3)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 4), 20).setEntityTexture("icyBee").addProduct(ForestryItem.craftingMaterial.getItemStack(1, 5), 40)
				.setTemperature(EnumTemperature.ICY).setHasEffect().setIsSecret();

		// Vengeful Branch
		Allele.speciesVindictive = new AlleleBeeSpecies("speciesVindictive", false, "bees.species.vindictive", vengeful, "ultio", 0xeafff3, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 9), 25).setIsSecret().setIsNotCounted();
		Allele.speciesVengeful = new AlleleBeeSpecies("speciesVengeful", false, "bees.species.vengeful", vengeful, "punire", 0xc2de00, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 9), 40).setIsSecret().setIsNotCounted();
		Allele.speciesAvenging = new AlleleBeeSpecies("speciesAvenging", false, "bees.species.avenging", vengeful, "hostimentum", 0xddff00, 0xffdc16)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 9), 40).setIsSecret().setHasEffect().setIsNotCounted();

		// Reddened Branch (EE)
		Allele.speciesDarkened = new AlleleBeeSpecies("speciesDarkened", false, "bees.species.darkened", reddened, "pahimas", 0xd7bee5, 0x260f29)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 100).addSpecialty(ForestryItem.beeComb.getItemStack(1, 12), 100)
				.setJubilanceProvider(new JubilanceNone()).setIsSecret().setIsNotCounted();
		AlleleManager.alleleRegistry.blacklistAllele(Allele.speciesDarkened.getUID());
		Allele.speciesReddened = new AlleleBeeSpecies("speciesReddened", false, "bees.species.reddened", reddened, "xenophos", 0xf8c1c1, 0x260f29)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 100).addSpecialty(ForestryItem.beeComb.getItemStack(1, 11), 100)
				.setJubilanceProvider(new JubilanceNone()).setIsSecret().setIsNotCounted();
		AlleleManager.alleleRegistry.blacklistAllele(Allele.speciesReddened.getUID());
		Allele.speciesOmega = new AlleleBeeSpecies("speciesOmega", false, "bees.species.omega", reddened, "slopokis", 0xfeff8f, 0x260f29)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 100).addSpecialty(ForestryItem.beeComb.getItemStack(1, 13), 100)
				.setJubilanceProvider(new JubilanceNone()).setIsSecret().setIsNotCounted();
		AlleleManager.alleleRegistry.blacklistAllele(Allele.speciesOmega.getUID());

		// Festive branch
		Allele.speciesLeporine = new AlleleBeeSpecies("speciesLeporine", false, "bees.species.leporine", festive, "lepus", 0xfeff8f, 0x3cd757)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 6), 30).addProduct(new ItemStack(Items.egg), 10).setIsSecret().setIsNotCounted()
				.setHasEffect();
		Allele.speciesMerry = new AlleleBeeSpecies("speciesMerry", false, "bees.species.merry", festive, "feliciter", 0xffffff, 0xd40000)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 4), 30).addProduct(ForestryItem.craftingMaterial.getItemStack(1, 5), 20)
				.setTemperature(EnumTemperature.ICY).setIsSecret().setIsNotCounted().setHasEffect();
		Allele.speciesTipsy = new AlleleBeeSpecies("speciesTipsy", false, "bees.species.tipsy", festive, "ebrius", 0xffffff, 0xc219ec)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 4), 30).addProduct(ForestryItem.craftingMaterial.getItemStack(1, 5), 20)
				.setTemperature(EnumTemperature.ICY).setIsSecret().setIsNotCounted().setHasEffect();
		// 35 Solstice
		Allele.speciesTricky = new AlleleBeeSpecies("speciesTricky", false, "bees.species.tricky", festive, "libita", 0x49413B, 0xFF6A00)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 40).addProduct(new ItemStack(Items.cookie), 15)
				.addSpecialty(new ItemStack(Items.skull, 1, 0), 2).addSpecialty(new ItemStack(Items.skull, 1, 2), 2)
				.addSpecialty(new ItemStack(Items.skull, 1, 3), 2).addSpecialty(new ItemStack(Items.skull, 1, 4), 2)
				.setIsSecret().setIsNotCounted().setHasEffect();
		// 37 Thanksgiving

		// Agrarian branch
		Allele.speciesRural = new AlleleBeeSpecies("speciesRural", false, "bees.species.rural", agrarian, "rustico", 0xfeff8f, 0xffdc16).addProduct(
				ForestryItem.beeComb.getItemStack(1, 14), 20).setIsSecret();
		// 41 Farmerly
		// 42 Agrarian

		// Boggy branch
		Allele.speciesMarshy = new AlleleBeeSpecies("speciesMarshy", true, "bees.species.marshy", boggy, "adorasti", 0x546626, 0xffdc16).addProduct(
				ForestryItem.beeComb.getItemStack(1, 15), 30).setHumidity(EnumHumidity.DAMP);
		// 44 speciesMiry
		// 45 speciesBoggy

		// Monastic branch
		Allele.speciesMonastic = new AlleleBeeSpecies("speciesMonastic", false, "bees.species.monastic", monastic, "monachus", 0x42371c, 0xfff7b6)
				.addProduct(ForestryItem.beeComb.getItemStack(1, 14), 30).addSpecialty(ForestryItem.beeComb.getItemStack(1, 16), 10)
				.setJubilanceProvider(new JubilanceProviderHermit()).setIsSecret();
		Allele.speciesSecluded = new AlleleBeeSpecies("speciesSecluded", true, "bees.species.secluded", monastic, "contractus", 0x7b6634, 0xfff7b6)
				.addSpecialty(ForestryItem.beeComb.getItemStack(1, 16), 20).setJubilanceProvider(new JubilanceProviderHermit()).setIsSecret();
		Allele.speciesHermitic = new AlleleBeeSpecies("speciesHermitic", false, "bees.species.hermitic", monastic, "anachoreta", 0xffd46c, 0xfff7b6)
				.addSpecialty(ForestryItem.beeComb.getItemStack(1, 16), 20).setJubilanceProvider(new JubilanceProviderHermit()).setHasEffect().setIsSecret();

		// / BEES // FLOWER PROVIDERS 1500 - 1599
		Allele.flowersVanilla = new AlleleFlowers("flowersVanilla", new FlowerProviderVanilla(), true);
		Allele.flowersNether = new AlleleFlowers("flowersNether", new FlowerProviderNetherwart());
		Allele.flowersCacti = new AlleleFlowers("flowersCacti", new FlowerProviderCacti());
		Allele.flowersMushrooms = new AlleleFlowers("flowersMushrooms", new FlowerProviderMushroom());
		Allele.flowersEnd = new AlleleFlowers("flowersEnd", new FlowerProviderEnd());
		Allele.flowersJungle = new AlleleFlowers("flowersJungle", new FlowerProviderJungle());
		Allele.flowersSnow = new AlleleFlowers("flowersSnow", new FlowerProviderVanilla(), true);
		Allele.flowersWheat = new AlleleFlowers("flowersWheat", new FlowerProviderWheat(), true);
		Allele.flowersGourd = new AlleleFlowers("flowersGourd", new FlowerProviderGourd(), true);

		// / BEES // EFFECTS 1800 - 1899
		Allele.effectNone = new AlleleEffectNone("effectNone");
		Allele.effectAggressive = new AlleleEffectAggressive("effectAggressive");
		Allele.effectHeroic = new AlleleEffectHeroic("effectHeroic");
		Allele.effectBeatific = new AlleleEffectPotion("effectBeatific", "beatific", false, Potion.regeneration, 100, true);
		Allele.effectMiasmic = new AlleleEffectMiasmic("effectMiasmic");
		Allele.effectMisanthrope = new AlleleEffectMisanthrope("effectMisanthrope");
		Allele.effectGlacial = new AlleleEffectGlacial("effectGlacial");
		Allele.effectRadioactive = new AlleleEffectRadioactive("effectRadioactive");
		Allele.effectCreeper = new AlleleEffectCreeper("effectCreeper");
		Allele.effectIgnition = new AlleleEffectIgnition("effectIgnition");
		Allele.effectExploration = new AlleleEffectExploration("effectExploration");
		Allele.effectFestiveEaster = new AlleleEffectNone("effectFestiveEaster");
		Allele.effectSnowing = new AlleleEffectSnowing("effectSnowing");
		Allele.effectDrunkard = new AlleleEffectPotion("effectDrunkard", "drunkard", false, Potion.confusion, 100, false);
		Allele.effectReanimation = new AlleleEffectResurrection("effectReanimation", "reanimation", AlleleEffectResurrection.getReanimationList());
		Allele.effectResurrection = new AlleleEffectResurrection("effectResurrection", "resurrection", AlleleEffectResurrection.getResurrectionList());
		Allele.effectRepulsion = new AlleleEffectRepulsion("effectRepulsion");

	}

	private void createMutations() {
		// / MUTATIONS
		BeeTemplates.commonA = new BeeMutation(Allele.speciesForest, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.commonB = new BeeMutation(Allele.speciesModest, Allele.speciesForest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonC = new BeeMutation(Allele.speciesModest, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.commonD = new BeeMutation(Allele.speciesWintry, Allele.speciesForest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonE = new BeeMutation(Allele.speciesWintry, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonF = new BeeMutation(Allele.speciesWintry, Allele.speciesModest, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.commonG = new BeeMutation(Allele.speciesTropical, Allele.speciesForest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonH = new BeeMutation(Allele.speciesTropical, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonI = new BeeMutation(Allele.speciesTropical, Allele.speciesModest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonJ = new BeeMutation(Allele.speciesTropical, Allele.speciesWintry, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.commonK = new BeeMutation(Allele.speciesMarshy, Allele.speciesForest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonL = new BeeMutation(Allele.speciesMarshy, Allele.speciesMeadows, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonM = new BeeMutation(Allele.speciesMarshy, Allele.speciesModest, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonN = new BeeMutation(Allele.speciesMarshy, Allele.speciesWintry, BeeTemplates.getCommonTemplate(), 15);
		BeeTemplates.commonO = new BeeMutation(Allele.speciesMarshy, Allele.speciesTropical, BeeTemplates.getCommonTemplate(), 15);

		BeeTemplates.cultivatedA = new BeeMutation(Allele.speciesCommon, Allele.speciesForest, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedB = new BeeMutation(Allele.speciesCommon, Allele.speciesMeadows, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedC = new BeeMutation(Allele.speciesCommon, Allele.speciesModest, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedD = new BeeMutation(Allele.speciesCommon, Allele.speciesWintry, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedE = new BeeMutation(Allele.speciesCommon, Allele.speciesTropical, BeeTemplates.getCultivatedTemplate(), 12);
		BeeTemplates.cultivatedF = new BeeMutation(Allele.speciesCommon, Allele.speciesMarshy, BeeTemplates.getCultivatedTemplate(), 12);

		BeeTemplates.nobleA = new BeeMutation(Allele.speciesCommon, Allele.speciesCultivated, BeeTemplates.getNobleTemplate(), 10);
		BeeTemplates.majesticA = new BeeMutation(Allele.speciesNoble, Allele.speciesCultivated, BeeTemplates.getMajesticTemplate(), 8);
		BeeTemplates.imperialA = new BeeMutation(Allele.speciesNoble, Allele.speciesMajestic, BeeTemplates.getImperialTemplate(), 8);

		BeeTemplates.diligentA = new BeeMutation(Allele.speciesCommon, Allele.speciesCultivated, BeeTemplates.getDiligentTemplate(), 10);
		BeeTemplates.unwearyA = new BeeMutation(Allele.speciesDiligent, Allele.speciesCultivated, BeeTemplates.getUnwearyTemplate(), 8);
		BeeTemplates.industriousA = new BeeMutation(Allele.speciesDiligent, Allele.speciesUnweary, BeeTemplates.getIndustriousTemplate(), 8);

		BeeTemplates.heroicA = new BeeMutation(Allele.speciesSteadfast, Allele.speciesValiant, BeeTemplates.getHeroicTemplate(), 6)
				.restrictBiomeType(BiomeDictionary.Type.FOREST).enableStrictBiomeCheck();

		BeeTemplates.sinisterA = new BeeMutation(Allele.speciesModest, Allele.speciesCultivated, BeeTemplates.getSinisterTemplate(), 60)
				.restrictBiomeType(BiomeDictionary.Type.NETHER);
		BeeTemplates.sinisterB = new BeeMutation(Allele.speciesTropical, Allele.speciesCultivated, BeeTemplates.getSinisterTemplate(), 60)
				.restrictBiomeType(BiomeDictionary.Type.NETHER);
		BeeTemplates.fiendishA = new BeeMutation(Allele.speciesSinister, Allele.speciesCultivated, BeeTemplates.getFiendishTemplate(), 40)
				.restrictBiomeType(BiomeDictionary.Type.NETHER);
		BeeTemplates.fiendishB = new BeeMutation(Allele.speciesSinister, Allele.speciesModest, BeeTemplates.getFiendishTemplate(), 40)
				.restrictBiomeType(BiomeDictionary.Type.NETHER);
		BeeTemplates.fiendishC = new BeeMutation(Allele.speciesSinister, Allele.speciesTropical, BeeTemplates.getFiendishTemplate(), 40)
				.restrictBiomeType(BiomeDictionary.Type.NETHER);
		BeeTemplates.demonicA = new BeeMutation(Allele.speciesSinister, Allele.speciesFiendish, BeeTemplates.getDemonicTemplate(), 25)
				.restrictBiomeType(BiomeDictionary.Type.NETHER);

		// Austere branch
		BeeTemplates.frugalA = new BeeMutation(Allele.speciesModest, Allele.speciesSinister, BeeTemplates.getFrugalTemplate(), 16).setTemperatureRainfall(1.9f,
				2.0f, 0.0f, 0.1f);
		BeeTemplates.frugalB = new BeeMutation(Allele.speciesModest, Allele.speciesFiendish, BeeTemplates.getFrugalTemplate(), 10).setTemperatureRainfall(1.9f,
				2.0f, 0.0f, 0.1f);
		BeeTemplates.austereA = new BeeMutation(Allele.speciesModest, Allele.speciesFrugal, BeeTemplates.getAustereTemplate(), 8).setTemperatureRainfall(1.9f,
				2.0f, 0.0f, 0.1f);

		// Tropical branch
		BeeTemplates.exoticA = new BeeMutation(Allele.speciesAustere, Allele.speciesTropical, BeeTemplates.getExoticTemplate(), 12);
		BeeTemplates.edenicA = new BeeMutation(Allele.speciesExotic, Allele.speciesTropical, BeeTemplates.getEdenicTemplate(), 8);

		// Wintry branch
		BeeTemplates.icyA = new BeeMutation(Allele.speciesIndustrious, Allele.speciesWintry, BeeTemplates.getIcyTemplate(), 12).setTemperature(0f, 0.15f);
		BeeTemplates.glacialA = new BeeMutation(Allele.speciesIcy, Allele.speciesWintry, BeeTemplates.getGlacialTemplate(), 8).setTemperature(0f, 0.15f);

		// Festive branch
		BeeTemplates.leporineA = new MutationTimeLimited(Allele.speciesMeadows, Allele.speciesForest, BeeTemplates.getLeporineTemplate(), 10,
				new MutationTimeLimited.DayMonth(29, 3), new MutationTimeLimited.DayMonth(15, 4)).setIsSecret();
		BeeTemplates.merryA = new MutationTimeLimited(Allele.speciesWintry, Allele.speciesForest, BeeTemplates.getMerryTemplate(), 10,
				new MutationTimeLimited.DayMonth(21, 12), new MutationTimeLimited.DayMonth(27, 12)).setIsSecret();
		BeeTemplates.tipsyA = new MutationTimeLimited(Allele.speciesWintry, Allele.speciesMeadows, BeeTemplates.getTipsyTemplate(), 10,
				new MutationTimeLimited.DayMonth(27, 12), new MutationTimeLimited.DayMonth(2, 1)).setIsSecret();
		BeeTemplates.trickyA = new MutationTimeLimited(Allele.speciesSinister, Allele.speciesCommon, BeeTemplates.getTrickyTemplate(), 10,
				new MutationTimeLimited.DayMonth(15, 10), new MutationTimeLimited.DayMonth(3, 11)).setIsSecret();

		// Agrarian branch
		BeeTemplates.ruralA = new BeeMutation(Allele.speciesMeadows, Allele.speciesDiligent, BeeTemplates.getRuralTemplate(), 12)
				.restrictBiomeType(BiomeDictionary.Type.PLAINS).enableStrictBiomeCheck();

		// Monastic branch
		BeeTemplates.secludedA = new BeeMutation(Allele.speciesMonastic, Allele.speciesAustere, BeeTemplates.getSecludedTemplate(), 12);
		BeeTemplates.hermiticA = new BeeMutation(Allele.speciesMonastic, Allele.speciesSecluded, BeeTemplates.getHermiticTemplate(), 8);

		// End branch
		BeeTemplates.spectralA = new BeeMutation(Allele.speciesHermitic, Allele.speciesEnded, BeeTemplates.getSpectralTemplate(), 4);
		BeeTemplates.phantasmalA = new BeeMutation(Allele.speciesSpectral, Allele.speciesEnded, BeeTemplates.getPhantasmalTemplate(), 2);

		// Vindictive branch
		BeeTemplates.vindictiveA = new BeeMutation(Allele.speciesMonastic, Allele.speciesDemonic, BeeTemplates.getVindictiveTemplate(), 4).setIsSecret();

		BeeTemplates.vengefulA = new BeeMutation(Allele.speciesDemonic, Allele.speciesVindictive, BeeTemplates.getVengefulTemplate(), 8).setIsSecret();
		BeeTemplates.vengefulB = new BeeMutation(Allele.speciesMonastic, Allele.speciesVindictive, BeeTemplates.getVengefulTemplate(), 8).setIsSecret();
		BeeTemplates.avengingA = new BeeMutation(Allele.speciesVengeful, Allele.speciesVindictive, BeeTemplates.getAvengingTemplate(), 4);
	}

	private void registerTemplates() {
		beeInterface.registerTemplate(BeeTemplates.getForestTemplate());
		beeInterface.registerTemplate(BeeTemplates.getMeadowsTemplate());
		beeInterface.registerTemplate(BeeTemplates.getCommonTemplate());
		beeInterface.registerTemplate(BeeTemplates.getCultivatedTemplate());
		beeInterface.registerTemplate(BeeTemplates.getNobleTemplate());
		beeInterface.registerTemplate(BeeTemplates.getMajesticTemplate());
		beeInterface.registerTemplate(BeeTemplates.getImperialTemplate());
		beeInterface.registerTemplate(BeeTemplates.getDiligentTemplate());
		beeInterface.registerTemplate(BeeTemplates.getUnwearyTemplate());
		beeInterface.registerTemplate(BeeTemplates.getIndustriousTemplate());
		beeInterface.registerTemplate(BeeTemplates.getSteadfastTemplate());
		beeInterface.registerTemplate(BeeTemplates.getValiantTemplate());
		beeInterface.registerTemplate(BeeTemplates.getHeroicTemplate());
		beeInterface.registerTemplate(BeeTemplates.getSinisterTemplate());
		beeInterface.registerTemplate(BeeTemplates.getFiendishTemplate());
		beeInterface.registerTemplate(BeeTemplates.getDemonicTemplate());
		beeInterface.registerTemplate(BeeTemplates.getModestTemplate());
		beeInterface.registerTemplate(BeeTemplates.getFrugalTemplate());
		beeInterface.registerTemplate(BeeTemplates.getAustereTemplate());
		beeInterface.registerTemplate(BeeTemplates.getTropicalTemplate());
		beeInterface.registerTemplate(BeeTemplates.getExoticTemplate());
		beeInterface.registerTemplate(BeeTemplates.getEdenicTemplate());
		beeInterface.registerTemplate(BeeTemplates.getWintryTemplate());
		beeInterface.registerTemplate(BeeTemplates.getIcyTemplate());
		beeInterface.registerTemplate(BeeTemplates.getGlacialTemplate());
		beeInterface.registerTemplate(BeeTemplates.getVindictiveTemplate());
		beeInterface.registerTemplate(BeeTemplates.getVengefulTemplate());
		beeInterface.registerTemplate(BeeTemplates.getAvengingTemplate());
		beeInterface.registerTemplate(BeeTemplates.getDarkenedTemplate());
		beeInterface.registerTemplate(BeeTemplates.getReddenedTemplate());
		beeInterface.registerTemplate(BeeTemplates.getOmegaTemplate());
		beeInterface.registerTemplate(BeeTemplates.getRuralTemplate());
		beeInterface.registerTemplate(BeeTemplates.getLeporineTemplate());
		beeInterface.registerTemplate(BeeTemplates.getMerryTemplate());
		beeInterface.registerTemplate(BeeTemplates.getTipsyTemplate());
		beeInterface.registerTemplate(BeeTemplates.getTrickyTemplate());
		beeInterface.registerTemplate(BeeTemplates.getMarshyTemplate());
		beeInterface.registerTemplate(BeeTemplates.getMonasticTemplate());
		beeInterface.registerTemplate(BeeTemplates.getSecludedTemplate());
		beeInterface.registerTemplate(BeeTemplates.getHermiticTemplate());
		beeInterface.registerTemplate(BeeTemplates.getEnderTemplate());
		beeInterface.registerTemplate(BeeTemplates.getSpectralTemplate());
		beeInterface.registerTemplate(BeeTemplates.getPhantasmalTemplate());
	}

	private void parseAdditionalFlowers(String list, ArrayList<ItemStack> target) {
		String[] parts = list.split("[;]+");

		for (String part : parts) {
			if (part.isEmpty())
				continue;

			String[] ident = part.split("[:]+");

			if (ident.length != 1 && ident.length != 2) {
				Proxies.log.warning("Failed to add flower of (" + part + ") to vanilla flower provider since it isn't formatted properly.");
				continue;
			}

			Item item = GameData.getItemRegistry().getRaw(ident[0]);

			if (item == null) {
				Block block = GameData.getBlockRegistry().getRaw(ident[0]);

				if (block == null || block == Blocks.air || Item.getItemFromBlock(block) == null) {
					Proxies.log.warning("Failed to add flower of (" + part + ") to vanilla flower provider since it couldn't be found.");
					continue;
				}

				item = Item.getItemFromBlock(block);
			}

			int meta = ident.length > 1 ? Integer.parseInt(ident[1]) : 0;

			Proxies.log.finer("Adding flower of (" + part + ") to vanilla flower provider.");
			target.add(new ItemStack(item, 1, meta));
		}
	}

	private void parseBeeBlacklist(String list) {
		String[] items = list.split("[;]+");

		for (String item : items) {
			if (item.isEmpty())
				continue;

			FMLCommonHandler.instance().getFMLLogger().debug("Blacklisting bee species identified by " + item);
			AlleleManager.alleleRegistry.blacklistAllele(item);
		}
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerApiculture();
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[]{new CommandBeekeepingMode(), new CommandGiveBee(EnumBeeType.DRONE), new CommandGiveBee(EnumBeeType.PRINCESS),
			new CommandGiveBee(EnumBeeType.QUEEN),};
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-candle-lighting-id")) {
			ItemStack value = message.getItemStackValue();
			if (value != null)
				((BlockCandle) ForestryBlock.candle.block()).addItemToLightingList(value.getItem());
			else
				Logger.getLogger("Forestry").log(Level.WARNING,
						"Received an invalid 'add-candle-lighting-id' request from mod %s. Please contact the author and report this issue.",
						message.getSender());
			return true;
		} else if (message.key.equals("add-alveary-slab") && message.isStringMessage())
			try {
				Block block = GameData.getBlockRegistry().getRaw(message.getStringValue());

				if (block == null || block == Blocks.air)
					Logger.getLogger("Forestry").log(Level.WARNING,
							"Received an invalid 'add-alveary-slab' request from mod %s. Please contact the author and report this issue.",
							message.getSender());
				else
					StructureLogicAlveary.slabBlocks.add(block);
			} catch (Exception e) {
				Logger.getLogger("Forestry").log(Level.WARNING,
						"Received an invalid 'add-alveary-slab' request from mod %s. Please contact the author and report this issue.",
						message.getSender());
			}
		return super.processIMCMessage(message);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 1) {
			EntitySnowFX.icons = new IIcon[3];
			for (int i = 0; i < EntitySnowFX.icons.length; i++)
				EntitySnowFX.icons[i] = event.map.registerIcon("forestry:particles/snow." + (i + 1));
		}
	}
}
