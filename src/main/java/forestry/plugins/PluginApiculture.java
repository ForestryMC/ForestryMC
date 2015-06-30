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

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.IIcon;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.Forestry;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.apiculture.GuiHandlerApiculture;
import forestry.apiculture.SaveEventHandlerApiculture;
import forestry.apiculture.VillageHandlerApiculture;
import forestry.apiculture.commands.CommandBee;
import forestry.apiculture.flowers.FlowerRegistry;
import forestry.apiculture.gadgets.BlockAlveary;
import forestry.apiculture.gadgets.BlockBeehives;
import forestry.apiculture.gadgets.BlockCandle;
import forestry.apiculture.gadgets.BlockStump;
import forestry.apiculture.gadgets.TileApiaristChest;
import forestry.apiculture.gadgets.TileApiary;
import forestry.apiculture.gadgets.TileBeehouse;
import forestry.apiculture.gadgets.TileCandle;
import forestry.apiculture.gadgets.TileSwarm;
import forestry.apiculture.genetics.AlleleEffectAggressive;
import forestry.apiculture.genetics.AlleleEffectCreeper;
import forestry.apiculture.genetics.AlleleEffectExploration;
import forestry.apiculture.genetics.AlleleEffectFertile;
import forestry.apiculture.genetics.AlleleEffectFungification;
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
import forestry.apiculture.genetics.BeeBranchDefinition;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.BeeFactory;
import forestry.apiculture.genetics.BeeHelper;
import forestry.apiculture.genetics.BeeMutationFactory;
import forestry.apiculture.genetics.BeekeepingMode;
import forestry.apiculture.genetics.HiveDrop;
import forestry.apiculture.genetics.JubilanceFactory;
import forestry.apiculture.items.ItemAlvearyBlock;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemBeealyzer;
import forestry.apiculture.items.ItemCandleBlock;
import forestry.apiculture.items.ItemHabitatLocator;
import forestry.apiculture.items.ItemHiveFrame;
import forestry.apiculture.items.ItemHoneycomb;
import forestry.apiculture.items.ItemImprinter;
import forestry.apiculture.items.ItemWaxCast;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.multiblock.TileAlvearyFan;
import forestry.apiculture.multiblock.TileAlvearyHeater;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearyStabiliser;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.apiculture.network.PacketHandlerApiculture;
import forestry.apiculture.proxy.ProxyApiculture;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveDescription;
import forestry.apiculture.worldgen.HiveGenHelper;
import forestry.apiculture.worldgen.HiveRegistry;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.fluids.Fluids;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.gadgets.TileAnalyzer;
import forestry.core.genetics.alleles.Allele;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemOverlay;
import forestry.core.items.ItemOverlay.OverlayInfo;
import forestry.core.items.ItemScoop;
import forestry.core.network.IPacketHandler;
import forestry.core.proxy.Proxies;
import forestry.core.render.EntitySnowFX;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;

@Plugin(pluginID = "Apiculture", name = "Apiculture", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.apiculture.description")
public class PluginApiculture extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.apiculture.proxy.ClientProxyApiculture", serverSide = "forestry.apiculture.proxy.ProxyApiculture")
	public static ProxyApiculture proxy;
	private static final String CONFIG_CATEGORY = "apiculture";
	public static final String[] EMPTY_STRINGS = new String[0];
	public static String beekeepingMode = "NORMAL";
	private static float secondPrincessChance = 0;
	public static final int ticksPerBeeWorkCycle = 550;
	public static boolean apiarySideSensitive = false;
	public static boolean fancyRenderedBees = false;

	public static HiveRegistry hiveRegistry;

	public static MachineDefinition definitionApiary;
	public static MachineDefinition definitionChest;
	public static MachineDefinition definitionBeehouse;
	public static MachineDefinition definitionAnalyzer;

	private final Map<String, String[]> defaultAcceptedFlowers = new HashMap<String, String[]>();
	private final Map<String, String[]> defaultPlantableFlowers = new HashMap<String, String[]>();

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void setupAPI() {
		super.setupAPI();

		HiveManager.hiveRegistry = hiveRegistry = new HiveRegistry();
		HiveManager.genHelper = new HiveGenHelper();

		FlowerManager.flowerRegistry = new FlowerRegistry();

		BeeManager.villageBees = new ArrayList[]{new ArrayList<IBeeGenome>(), new ArrayList<IBeeGenome>()};

		BeeManager.beeFactory = new BeeFactory();
		BeeManager.beeMutationFactory = new BeeMutationFactory();
		BeeManager.jubilanceFactory = new JubilanceFactory();

		// Init bee interface
		BeeManager.beeRoot = new BeeHelper();
		AlleleManager.alleleRegistry.registerSpeciesRoot(BeeManager.beeRoot);

		// Modes
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.easy);
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.normal);
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.hard);
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.hardcore);
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.insane);
	}

	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(this);

		ForestryBlock.apiculture.registerBlock(new BlockBase(Material.iron), ItemForestryBlock.class, "apiculture");
		ForestryBlock.apiculture.block().setCreativeTab(Tabs.tabApiculture);
		ForestryBlock.apiculture.block().setHarvestLevel("axe", 0);

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

		// Candles
		ForestryBlock.candle.registerBlock(new BlockCandle(), ItemCandleBlock.class, "candle");
		ForestryBlock.stump.registerBlock(new BlockStump(), ItemForestryBlock.class, "stump");

		// Alveary and Components
		ForestryBlock.alveary.registerBlock(new BlockAlveary(), ItemAlvearyBlock.class, "alveary");
		ForestryBlock.alveary.block().setHarvestLevel("axe", 0);

		// Add triggers
		if (PluginManager.Module.BUILDCRAFT_STATEMENTS.isEnabled()) {
			ApicultureTriggers.initialize();
		}

		if (Config.enableVillagers) {
			// Register village components with the Structure registry.
			VillageHandlerApiculture.registerVillageComponents();
		}

		// Commands
		PluginCore.rootCommand.addChildCommand(new CommandBee());
	}

	@Override
	public void doInit() {
		final String oldConfig = CONFIG_CATEGORY + ".conf";
		final String newConfig = CONFIG_CATEGORY + ".cfg";

		File configFile = new File(Forestry.instance.getConfigFolder(), newConfig);
		if (!configFile.exists()) {
			setDefaultsForConfig();
		}

		File oldConfigFile = new File(Forestry.instance.getConfigFolder(), oldConfig);
		if (oldConfigFile.exists()) {
			loadOldConfig();

			final String oldConfigRenamed = CONFIG_CATEGORY + ".conf.old";
			File oldConfigFileRenamed = new File(Forestry.instance.getConfigFolder(), oldConfigRenamed);
			if (oldConfigFile.renameTo(oldConfigFileRenamed)) {
				Proxies.log.info("Migrated " + CONFIG_CATEGORY + " settings to the new file '" + newConfig + "' and renamed '" + oldConfig + "' to '" + oldConfigRenamed + "'.");
			}
		}

		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");

		List<IBeekeepingMode> beekeepingModes = BeeManager.beeRoot.getBeekeepingModes();
		String[] validBeekeepingModeNames = new String[beekeepingModes.size()];
		for (int i = 0; i < beekeepingModes.size(); i++) {
			validBeekeepingModeNames[i] = beekeepingModes.get(i).getName();
		}

		beekeepingMode = config.getStringLocalized("beekeeping", "mode", "NORMAL", validBeekeepingModeNames);
		Proxies.log.finer("Beekeeping mode read from config: " + beekeepingMode);

		secondPrincessChance = config.getFloatLocalized("beekeeping", "second.princess", secondPrincessChance, 0.0f, 100.0f);

		String acceptedFlowerMessage = StringUtil.localize("config.beekeeping.flowers.accepted.comment");
		String plantableFlowerMessage = StringUtil.localize("config.beekeeping.flowers.plantable.comment");

		for (String flowerType : FlowerManager.flowerRegistry.getFlowerTypes()) {
			String[] defaultAccepted = defaultAcceptedFlowers.get(flowerType);
			if (defaultAccepted == null) {
				defaultAccepted = EMPTY_STRINGS;
			}
			Property property = config.get("beekeeping.flowers." + flowerType, "accepted", defaultAccepted);
			property.comment = acceptedFlowerMessage;
			parseAcceptedFlowers(property, flowerType);

			String[] defaultPlantable = defaultPlantableFlowers.get(flowerType);
			if (defaultPlantable == null) {
				defaultPlantable = EMPTY_STRINGS;
			}
			property = config.get("beekeeping.flowers." + flowerType, "plantable", defaultPlantable);
			property.comment = plantableFlowerMessage;
			parsePlantableFlowers(property, flowerType);

			Set<IFlower> acceptableFlowers = FlowerManager.flowerRegistry.getAcceptableFlowers(flowerType);
			if (acceptableFlowers == null || acceptableFlowers.size() == 0) {
				Proxies.log.severe("Flower type '" + flowerType + "' has no valid flowers set in apiculture.cfg. Add valid flowers or delete the config to set it to default.");
			}
		}

		String[] blacklist = config.getStringListLocalized("species", "blacklist", EMPTY_STRINGS);
		parseBeeBlacklist(blacklist);

		config.save();

		// Genetics
		createAlleles();
		BeeDefinition.initBees();

		AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.speciesArgrarian", BeeDefinition.AGRARIAN.getGenome().getPrimary());

		// Hives
		createHives();
		registerBeehiveDrops();

		// Inducers for swarmer
		BeeManager.inducers.put(ForestryItem.royalJelly.getItemStack(), 10);

		GameRegistry.registerTileEntity(TileAlvearyPlain.class, "forestry.Alveary");
		GameRegistry.registerTileEntity(TileSwarm.class, "forestry.Swarm");
		GameRegistry.registerTileEntity(TileAlvearySwarmer.class, "forestry.AlvearySwarmer");
		GameRegistry.registerTileEntity(TileAlvearyHeater.class, "forestry.AlvearyHeater");
		GameRegistry.registerTileEntity(TileAlvearyFan.class, "forestry.AlvearyFan");
		GameRegistry.registerTileEntity(TileAlvearyHygroregulator.class, "forestry.AlvearyHygro");
		GameRegistry.registerTileEntity(TileAlvearyStabiliser.class, "forestry.AlvearyStabiliser");
		GameRegistry.registerTileEntity(TileAlvearySieve.class, "forestry.AlvearySieve");
		GameRegistry.registerTileEntity(TileCandle.class, "forestry.Candle");


		BeeManager.villageBees[0].add(BeeDefinition.FOREST.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.MEADOWS.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.MODEST.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.MARSHY.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.WINTRY.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.TROPICAL.getGenome());

		BeeManager.villageBees[1].add(BeeDefinition.FOREST.getRainResist().getGenome());
		BeeManager.villageBees[1].add(BeeDefinition.COMMON.getGenome());
		BeeManager.villageBees[1].add(BeeDefinition.VALIANT.getGenome());

		if (Config.enableVillagers) {
			// Register villager stuff
			VillageHandlerApiculture villageHandler = new VillageHandlerApiculture();
			VillagerRegistry.instance().registerVillageCreationHandler(villageHandler);
			VillagerRegistry.instance().registerVillagerId(Defaults.ID_VILLAGER_BEEKEEPER);
			Proxies.render.registerVillagerSkin(Defaults.ID_VILLAGER_BEEKEEPER, Defaults.TEXTURE_SKIN_BEEKPEEPER);
			VillagerRegistry.instance().registerVillageTradeHandler(Defaults.ID_VILLAGER_BEEKEEPER, villageHandler);
		}

		proxy.initializeRendering();
	}

	@Override
	public void postInit() {
		super.postInit();
		registerDungeonLoot();
	}

	private void setDefaultsForConfig() {
		
		IFlowerRegistry flowerRegistry = FlowerManager.flowerRegistry;

		// Register acceptable plants
		flowerRegistry.registerAcceptableFlower(Blocks.dragon_egg, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.vine, FlowerManager.FlowerTypeJungle);
		flowerRegistry.registerAcceptableFlower(Blocks.tallgrass, FlowerManager.FlowerTypeJungle);
		flowerRegistry.registerAcceptableFlower(Blocks.wheat, FlowerManager.FlowerTypeWheat);
		flowerRegistry.registerAcceptableFlower(Blocks.pumpkin_stem, FlowerManager.FlowerTypeGourd);
		flowerRegistry.registerAcceptableFlower(Blocks.melon_stem, FlowerManager.FlowerTypeGourd);
		flowerRegistry.registerAcceptableFlower(Blocks.nether_wart, FlowerManager.FlowerTypeNether);
		flowerRegistry.registerAcceptableFlower(Blocks.cactus, FlowerManager.FlowerTypeCacti);
		
		flowerRegistry.registerAcceptableFlower(Blocks.double_plant, 0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		flowerRegistry.registerAcceptableFlower(Blocks.double_plant, 1, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		flowerRegistry.registerAcceptableFlower(Blocks.double_plant, 4, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		flowerRegistry.registerAcceptableFlower(Blocks.double_plant, 5, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		
		// Register plantable plants
		for (int meta = 0; meta <= 8; meta++) {
			flowerRegistry.registerPlantableFlower(Blocks.red_flower, meta, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		}

		flowerRegistry.registerPlantableFlower(Blocks.yellow_flower, 0, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		flowerRegistry.registerPlantableFlower(Blocks.brown_mushroom, 0, 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.red_mushroom, 0, 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.cactus, 0, 1.0, FlowerManager.FlowerTypeCacti);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		DecimalFormat weightDecimalFormat = new DecimalFormat("#.000", decimalFormatSymbols);
		weightDecimalFormat.setGroupingUsed(false);

		for (String flowerType : FlowerManager.flowerRegistry.getFlowerTypes()) {
			Set<IFlower> flowers = FlowerManager.flowerRegistry.getAcceptableFlowers(flowerType);
			List<String> acceptedFlowerNames = new ArrayList<String>();
			List<String> plantableFlowerNames = new ArrayList<String>();
			if (flowers != null) {
				for (IFlower flower : flowers) {
					String name = flower.getBlock().delegate.name();
					int meta = flower.getMeta();
					if (flower.getMeta() != OreDictionary.WILDCARD_VALUE) {
						name = name + ':' + meta;
					}

					if (flower.isPlantable()) {
						double weight = flower.getWeight();
						String weightString = weightDecimalFormat.format(weight);
						plantableFlowerNames.add(weightString + ':' + name);
					} else {
						acceptedFlowerNames.add(name);
					}
				}
			}

			String[] acceptedFlowerNamesArray = acceptedFlowerNames.toArray(new String[acceptedFlowerNames.size()]);
			defaultAcceptedFlowers.put(flowerType, acceptedFlowerNamesArray);

			String[] plantableFlowerNamesArray = plantableFlowerNames.toArray(new String[plantableFlowerNames.size()]);
			defaultPlantableFlowers.put(flowerType, plantableFlowerNamesArray);
		}
	}

	private void loadOldConfig() {
		// Config
		forestry.core.config.deprecated.Configuration apicultureConfig = new forestry.core.config.deprecated.Configuration();

		forestry.core.config.deprecated.Property property = apicultureConfig.get("apiary.sidesensitive", CONFIG_CATEGORY, apiarySideSensitive);
		property.comment = "set to false if apiaries should output all items regardless of side a pipe is attached to";
		apiarySideSensitive = Boolean.parseBoolean(property.value);

		property = apicultureConfig.get("render.bees.fancy", CONFIG_CATEGORY, fancyRenderedBees);
		property.comment = "set to true to enable a fancy butterfly-like renderer for bees. (experimental!)";
		fancyRenderedBees = Boolean.parseBoolean(property.value);

		property = apicultureConfig.get("beekeeping.mode", CONFIG_CATEGORY, "NORMAL");
		property.comment = "change beekeeping modes here. possible values EASY, NORMAL, HARD, HARDCORE, INSANE. mods may add additional modes.";
		beekeepingMode = property.value.trim();
		Proxies.log.finer("Beekeeping mode read from config: " + beekeepingMode);

		property = apicultureConfig.get("beekeeping.secondprincess", CONFIG_CATEGORY, secondPrincessChance);
		property.comment = "percent chance of second princess drop, for limited/skyblock maps. Acceptable values up to 2 decimals.";
		secondPrincessChance = Float.parseFloat(property.value);

		property = apicultureConfig.get("species.blacklist", CONFIG_CATEGORY, "");
		property.comment = "add species to blacklist identified by their uid and seperated with ';'.";
		parseBeeBlacklist(property.value);
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
		ForestryItem.habitatLocator.registerItem(new ItemHabitatLocator(), "habitatLocator");
		ForestryItem.imprinter.registerItem((new ItemImprinter()), "imprinter");

		// / COMB FRAMES
		ForestryItem.frameUntreated.registerItem(new ItemHiveFrame(80, 0.9f), "frameUntreated");
		ForestryItem.frameImpregnated.registerItem(new ItemHiveFrame(240, 0.4f), "frameImpregnated");
		ForestryItem.frameProven.registerItem(new ItemHiveFrame(720, 0.3f), "frameProven");

		// / BEE RESOURCES
		ForestryItem.honeyDrop.registerItem(new ItemOverlay(Tabs.tabApiculture,
				new OverlayInfo("honey", 0xecb42d, 0xe8c814),
				new OverlayInfo("charged", 0x800505, 0x9c0707).setIsSecret(),
				new OverlayInfo("omega", 0x191919, 0x4a8ca7).setIsSecret()), "honeyDrop");
		OreDictionary.registerOre("dropHoney", ForestryItem.honeyDrop.getItemStack());

		ForestryItem.pollenCluster.registerItem(new ItemOverlay(Tabs.tabApiculture,
						new OverlayInfo("normal", 0xa28a25, 0xa28a25),
						new OverlayInfo("crystalline", 0xffffff, 0xc5feff)),
				"pollen");
		OreDictionary.registerOre("itemPollen", ForestryItem.pollenCluster.getItemStack());

		ForestryItem.propolis.registerItem(new ItemOverlay(Tabs.tabApiculture,
						new OverlayInfo("normal", 0xc5b24e),
						new OverlayInfo("sticky", 0xc68e57),
						new OverlayInfo("pulsating", 0x2ccdb1).setIsSecret(),
						new OverlayInfo("silky", 0xddff00)),
				"propolis");

		ForestryItem.honeydew.registerItem(new ItemForestry().setCreativeTab(Tabs.tabApiculture), "honeydew");
		OreDictionary.registerOre("dropHoneydew", ForestryItem.honeydew.getItemStack());

		ForestryItem.royalJelly.registerItem(new ItemForestry().setCreativeTab(Tabs.tabApiculture), "royalJelly");
		OreDictionary.registerOre("dropRoyalJelly", ForestryItem.royalJelly.getItemStack());

		ForestryItem.waxCast.registerItem(new ItemWaxCast().setCreativeTab(Tabs.tabApiculture), "waxCast");

		// / BEE COMBS
		ForestryItem.beeComb.registerItem(new ItemHoneycomb(), "beeCombs");
		OreDictionary.registerOre("beeComb", ForestryItem.beeComb.getWildcard());

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
	protected void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		crateRegistry.registerCrate(ForestryItem.beeswax.getItemStack(), "cratedBeeswax");
		crateRegistry.registerCrate(ForestryItem.pollenCluster.getItemStack(), "cratedPollen");
		crateRegistry.registerCrate(ForestryItem.propolis.getItemStack(), "cratedPropolis");
		crateRegistry.registerCrate(ForestryItem.honeydew.getItemStack(), "cratedHoneydew");
		crateRegistry.registerCrate(ForestryItem.royalJelly.getItemStack(), "cratedRoyalJelly");

		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 0), "cratedHoneycombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 1), "cratedCocoaComb");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 2), "cratedSimmeringCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 3), "cratedStringyCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 4), "cratedFrozenCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 5), "cratedDrippingCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 6), "cratedSilkyCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 7), "cratedParchedCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 8), "cratedMysteriousCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 10), "cratedPowderyCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 14), "cratedWheatenCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 15), "cratedMossyCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 16), "cratedMellowCombs");

		crateRegistry.registerCrate(ForestryItem.refractoryWax.getItemStack(), "cratedRefractoryWax");
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
		Proxies.common.addRecipe(ForestryItem.habitatLocator.getItemStack(),
				" X ",
				"X#X",
				" X ",
				'#', Items.redstone, 'X', "ingotBronze");

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
		if (ForestryItem.honeyedSlice.item() != null) {
			Proxies.common.addRecipe(ForestryItem.honeyedSlice.getItemStack(4),
					"###", "#X#", "###",
					'#', ForestryItem.honeyDrop,
					'X', Items.bread);
		}
		if (ForestryItem.honeyPot.item() != null) {
			Proxies.common.addRecipe(ForestryItem.honeyPot.getItemStack(1),
					"# #", " X ", "# #",
					'#', ForestryItem.honeyDrop,
					'X', ForestryItem.waxCapsule);
		}
		if (ForestryItem.ambrosia.item() != null) {
			Proxies.common.addRecipe(ForestryItem.ambrosia.getItemStack(),
					"#Y#", "XXX", "###",
					'#', ForestryItem.honeydew,
					'X', ForestryItem.royalJelly,
					'Y', ForestryItem.waxCapsule);
		}

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

		// / WAX CAST
		Proxies.common.addRecipe(ForestryItem.waxCast.getItemStack(),
				"###",
				"# #",
				"###",
				'#', ForestryItem.beeswax);

		// / ALVEARY
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, TileAlveary.PLAIN_META),
				"###",
				"#X#",
				"###",
				'X', ForestryItem.impregnatedCasing,
				'#', ForestryItem.craftingMaterial.getItemStack(1, 6));
		// SWARMER
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, TileAlveary.SWARMER_META),
				"#G#",
				" X ",
				"#G#",
				'#', ForestryItem.tubes.getItemStack(1, 5),
				'X', ForestryBlock.alveary,
				'G', Items.gold_ingot);
		// FAN
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, TileAlveary.FAN_META),
				"I I",
				" X ",
				"I#I",
				'#', ForestryItem.tubes.getItemStack(1, 4),
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot);
		// HEATER
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, TileAlveary.HEATER_META),
				"#I#",
				" X ",
				"YYY",
				'#', ForestryItem.tubes.getItemStack(1, 4),
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot, 'Y', Blocks.stone);
		// HYGROREGULATOR
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, TileAlveary.HYGRO_META),
				"GIG",
				"GXG",
				"GIG",
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot,
				'G', Blocks.glass);
		// STABILISER
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, TileAlveary.STABILIZER_META),
				"G G",
				"GXG",
				"G G",
				'X', ForestryBlock.alveary,
				'G', Items.quartz);
		// SIEVE
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, TileAlveary.SIEVE_META),
				"III",
				" X ",
				"WWW",
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot,
				'W', ForestryItem.craftingMaterial.getItemStack(1, 3));

		if (PluginManager.Module.FACTORY.isEnabled()) {
			// / SQUEEZER
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.honeyDrop.getItemStack()}, Fluids.HONEY.getFluid(Defaults.FLUID_PER_HONEY_DROP),
					ForestryItem.propolis.getItemStack(), 5);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.honeydew.getItemStack()}, Fluids.HONEY.getFluid(Defaults.FLUID_PER_HONEY_DROP));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.phosphor.getItemStack(2), new ItemStack(Blocks.sand)}, Fluids.LAVA.getFluid(2000));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.phosphor.getItemStack(2), new ItemStack(Blocks.dirt)}, Fluids.LAVA.getFluid(1600));

			// / CARPENTER
			RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null, ForestryItem.beealyzer.getItemStack(),
					"X#X", "X#X", "RDR",
					'#', Blocks.glass_pane,
					'X', "ingotTin",
					'R', Items.redstone,
					'D', Items.diamond);
			RecipeManagers.carpenterManager.addRecipe(50, Fluids.HONEY.getFluid(500), null, ForestryItem.craftingMaterial.getItemStack(1, 6),
					" J ", "###", "WPW",
					'#', "plankWood",
					'J', ForestryItem.royalJelly,
					'W', ForestryItem.beeswax,
					'P', ForestryItem.pollenCluster);

			RecipeManagers.carpenterManager.addRecipe(30, Fluids.WATER.getFluid(600), null, ForestryBlock.candle.getItemStack(24),
					" X ",
					"###",
					"###",
					'#', ForestryItem.beeswax,
					'X', Items.string);
			RecipeManagers.carpenterManager.addRecipe(10, Fluids.WATER.getFluid(200), null, ForestryBlock.candle.getItemStack(6),
					"#X#",
					'#', ForestryItem.beeswax,
					'X', ForestryItem.craftingMaterial.getItemStack(1, 2));
			Proxies.common.addShapelessRecipe(ForestryBlock.candle.getItemStack(), ForestryBlock.candle.getItemStack());
			Proxies.common.addShapelessRecipe(ForestryBlock.candle.getItemStack(1, 1), ForestryBlock.candle.getItemStack(1, 1));

			// / CENTRIFUGE
			// Honey combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 0), ImmutableMap.of(
					ForestryItem.beeswax.getItemStack(), 1.0f,
					ForestryItem.honeyDrop.getItemStack(), 0.9f
			));

			// Cocoa combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 1), ImmutableMap.of(
					ForestryItem.beeswax.getItemStack(), 1.0f,
					new ItemStack(Items.dye, 1, 3), 0.5f
			));

			// Simmering combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 2), ImmutableMap.of(
					ForestryItem.refractoryWax.getItemStack(), 1.0f,
					ForestryItem.phosphor.getItemStack(2), 0.7f
			));

			// Stringy combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 3), ImmutableMap.of(
					ForestryItem.propolis.getItemStack(), 1.0f,
					ForestryItem.honeyDrop.getItemStack(), 0.4f
			));

			// Dripping combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 5), ImmutableMap.of(
					ForestryItem.honeydew.getItemStack(), 1.0f,
					ForestryItem.honeyDrop.getItemStack(), 0.4f
			));

			// Frozen combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 4), ImmutableMap.of(
					ForestryItem.beeswax.getItemStack(), 0.8f,
					ForestryItem.honeyDrop.getItemStack(), 0.7f,
					new ItemStack(Items.snowball), 0.4f,
					ForestryItem.pollenCluster.getItemStack(1, 1), 0.2f
			));

			// Silky combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 6), ImmutableMap.of(
					ForestryItem.honeyDrop.getItemStack(), 1.0f,
					ForestryItem.propolis.getItemStack(1, 3), 0.8f
			));

			// Parched combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 7), ImmutableMap.of(
					ForestryItem.beeswax.getItemStack(), 1.0f,
					ForestryItem.honeyDrop.getItemStack(), 0.9f
			));

			// Mysterious combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 8), ImmutableMap.of(
					ForestryItem.propolis.getItemStack(1, 2), 1.0f,
					ForestryItem.honeyDrop.getItemStack(), 0.4f
			));

			// Irradiated combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 9), ImmutableMap.<ItemStack, Float>of(
			));

			// Powdery combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 10), ImmutableMap.of(
					ForestryItem.honeyDrop.getItemStack(), 0.2f,
					ForestryItem.beeswax.getItemStack(), 0.2f,
					new ItemStack(Items.gunpowder), 0.9f
			));

			// Wheaten Combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 14), ImmutableMap.of(
					ForestryItem.honeyDrop.getItemStack(), 0.2f,
					ForestryItem.beeswax.getItemStack(), 0.2f,
					new ItemStack(Items.wheat), 0.8f
			));

			// Mossy Combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 15), ImmutableMap.of(
					ForestryItem.beeswax.getItemStack(), 1.0f,
					ForestryItem.honeyDrop.getItemStack(), 0.9f
			));

			// Mellow Combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 16), ImmutableMap.of(
					ForestryItem.honeydew.getItemStack(), 0.6f,
					ForestryItem.beeswax.getItemStack(), 0.2f,
					new ItemStack(Items.quartz), 0.3f
			));

			// Silky Propolis
			RecipeManagers.centrifugeManager.addRecipe(5, ForestryItem.propolis.getItemStack(1, 3), ImmutableMap.of(
					ForestryItem.craftingMaterial.getItemStack(1, 2), 0.6f,
					ForestryItem.propolis.getItemStack(), 0.1f
			));

			// / FERMENTER
			RecipeManagers.fermenterManager.addRecipe(ForestryItem.honeydew.getItemStack(), 500, 1.0f, Fluids.SHORT_MEAD.getFluid(1), Fluids.HONEY.getFluid(1));
		}

		// ANALYZER
		definitionAnalyzer.recipes = createAlyzerRecipes(ForestryBlock.core.block(), Defaults.DEFINITION_ANALYZER_META);

		definitionAnalyzer.register();
		definitionApiary.register();
		definitionBeehouse.register();
		definitionChest.register();
	}

	public static IRecipe[] createAlyzerRecipes(Block block, int meta) {
		ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
		recipes.add(ShapedRecipeCustom.createShapedRecipe(new ItemStack(block, 1, meta), "XTX", " Y ", "X X", 'Y', ForestryItem.sturdyCasing, 'T', ForestryItem.beealyzer, 'X', "ingotBronze"));
		recipes.add(ShapedRecipeCustom.createShapedRecipe(new ItemStack(block, 1, meta), "XTX", " Y ", "X X", 'Y', ForestryItem.sturdyCasing, 'T', ForestryItem.treealyzer, 'X', "ingotBronze"));
		return recipes.toArray(new IRecipe[recipes.size()]);
	}

	private static void registerBeehiveDrops() {
		ItemStack honeyComb = ForestryItem.beeComb.getItemStack(1, 0);
		hiveRegistry.addDrops(HiveRegistry.forest,
				new HiveDrop(80, BeeDefinition.FOREST, honeyComb).setIgnobleShare(0.7f),
				new HiveDrop(8, BeeDefinition.FOREST.getRainResist(), honeyComb),
				new HiveDrop(3, BeeDefinition.VALIANT, honeyComb)
		);

		hiveRegistry.addDrops(HiveRegistry.meadows,
				new HiveDrop(80, BeeDefinition.MEADOWS, honeyComb).setIgnobleShare(0.7f),
				new HiveDrop(3, BeeDefinition.VALIANT, honeyComb)
		);

		ItemStack parchedComb = ForestryItem.beeComb.getItemStack(1, 7);
		hiveRegistry.addDrops(HiveRegistry.desert,
				new HiveDrop(80, BeeDefinition.MODEST, parchedComb).setIgnobleShare(0.7f),
				new HiveDrop(3, BeeDefinition.VALIANT, parchedComb)
		);

		ItemStack silkyComb = ForestryItem.beeComb.getItemStack(1, 6);
		hiveRegistry.addDrops(HiveRegistry.jungle,
				new HiveDrop(80, BeeDefinition.TROPICAL, silkyComb).setIgnobleShare(0.7f),
				new HiveDrop(3, BeeDefinition.VALIANT, silkyComb)
		);

		ItemStack mysteriousComb = ForestryItem.beeComb.getItemStack(1, 8);
		hiveRegistry.addDrops(HiveRegistry.end,
				new HiveDrop(90, BeeDefinition.ENDED, mysteriousComb)
		);

		ItemStack frozenComb = ForestryItem.beeComb.getItemStack(1, 4);
		hiveRegistry.addDrops(HiveRegistry.snow,
				new HiveDrop(80, BeeDefinition.WINTRY, frozenComb).setIgnobleShare(0.5f),
				new HiveDrop(3, BeeDefinition.VALIANT, frozenComb)
		);

		ItemStack mossyComb = ForestryItem.beeComb.getItemStack(1, 15);
		hiveRegistry.addDrops(HiveRegistry.swamp,
				new HiveDrop(80, BeeDefinition.MARSHY, mossyComb).setIgnobleShare(0.4f),
				new HiveDrop(3, BeeDefinition.VALIANT, mossyComb)
		);
	}

	private static void registerDungeonLoot() {
		int rarity;
		if (Config.dungeonLootRare) {
			rarity = 5;
		} else {
			rarity = 10;
		}

		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(BeeDefinition.STEADFAST.getMemberStack(EnumBeeType.DRONE), 1, 1, rarity));

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

		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(BeeDefinition.FOREST.getRainResist().getMemberStack(EnumBeeType.PRINCESS), 1, 1, 5));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(BeeDefinition.COMMON.getMemberStack(EnumBeeType.DRONE), 1, 2, 8));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(BeeDefinition.MEADOWS.getMemberStack(EnumBeeType.PRINCESS), 1, 1, 5));
	}

	private static void createHives() {
		hiveRegistry.registerHive(HiveRegistry.forest, HiveDescription.FOREST);
		hiveRegistry.registerHive(HiveRegistry.meadows, HiveDescription.MEADOWS);
		hiveRegistry.registerHive(HiveRegistry.desert, HiveDescription.DESERT);
		hiveRegistry.registerHive(HiveRegistry.jungle, HiveDescription.JUNGLE);
		hiveRegistry.registerHive(HiveRegistry.end, HiveDescription.END);
		hiveRegistry.registerHive(HiveRegistry.snow, HiveDescription.SNOW);
		hiveRegistry.registerHive(HiveRegistry.swamp, HiveDescription.SWAMP);
	}

	private static void createAlleles() {

		IClassification hymnoptera = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "hymnoptera", "Hymnoptera");
		AlleleManager.alleleRegistry.getClassification("class.insecta").addMemberGroup(hymnoptera);

		IClassification apidae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "apidae", "Apidae");
		hymnoptera.addMemberGroup(apidae);

		for (BeeBranchDefinition beeBranch : BeeBranchDefinition.values()) {
			apidae.addMemberGroup(beeBranch.getBranch());
		}

		// / BEES // EFFECTS 1800 - 1899
		Allele.effectNone = new AlleleEffectNone("none");
		Allele.effectAggressive = new AlleleEffectAggressive();
		Allele.effectHeroic = new AlleleEffectHeroic();
		Allele.effectBeatific = new AlleleEffectPotion("beatific", false, Potion.regeneration, 100, true);
		Allele.effectMiasmic = new AlleleEffectMiasmic();
		Allele.effectMisanthrope = new AlleleEffectMisanthrope();
		Allele.effectGlacial = new AlleleEffectGlacial();
		Allele.effectRadioactive = new AlleleEffectRadioactive();
		Allele.effectCreeper = new AlleleEffectCreeper();
		Allele.effectIgnition = new AlleleEffectIgnition();
		Allele.effectExploration = new AlleleEffectExploration();
		Allele.effectFestiveEaster = new AlleleEffectNone("festiveEaster");
		Allele.effectSnowing = new AlleleEffectSnowing();
		Allele.effectDrunkard = new AlleleEffectPotion("drunkard", false, Potion.confusion, 100, false);
		Allele.effectReanimation = new AlleleEffectResurrection("reanimation", AlleleEffectResurrection.getReanimationList());
		Allele.effectResurrection = new AlleleEffectResurrection("resurrection", AlleleEffectResurrection.getResurrectionList());
		Allele.effectRepulsion = new AlleleEffectRepulsion();
		Allele.effectFertile = new AlleleEffectFertile();
		Allele.effectMycophilic = new AlleleEffectFungification();
	}

	public static double getSecondPrincessChance() {
		return secondPrincessChance;
	}

	private static void parseAdditionalFlowers(String list, ArrayList<ItemStack> target) {
		List<ItemStack> flowers = StackUtils.parseItemStackStrings(list, 0);
		target.addAll(flowers);
	}

	private static void parseBeeBlacklist(String list) {
		String[] items = list.split("[;]+");
		parseBeeBlacklist(items);
	}

	private static void parseBeeBlacklist(String[] items) {
		for (String item : items) {
			if (item.isEmpty()) {
				continue;
			}

			FMLCommonHandler.instance().getFMLLogger().debug("Blacklisting bee species identified by " + item);
			AlleleManager.alleleRegistry.blacklistAllele(item);
		}
	}

	private static void parseAcceptedFlowers(Property property, String flowerType) {
		List<StackUtils.Stack> acceptedFlowerItemStacks = StackUtils.parseStackStrings(property.getStringList(), OreDictionary.WILDCARD_VALUE);
		for (StackUtils.Stack acceptedFlower : acceptedFlowerItemStacks) {
			Block acceptedFlowerBlock = acceptedFlower.getBlock();
			int meta = acceptedFlower.getMeta();
			if (acceptedFlowerBlock != null) {
				FlowerManager.flowerRegistry.registerAcceptableFlower(acceptedFlowerBlock, meta, flowerType);
			} else {
				Proxies.log.warning("No block found for '" + acceptedFlower + "' in config '" + property.getName() + "'.");
			}
		}
	}

	private static void parsePlantableFlowers(Property property, String flowerType) {
		for (String string : property.getStringList()) {
			int idx = string.indexOf(':');

			String weightString = string.substring(0, idx);
			double weight;
			try {
				weight = Double.parseDouble(weightString);
			} catch (NumberFormatException e) {
				Proxies.log.warning("Invalid weight for plantable flower in config. (" + property.getName() + ')');
				continue;
			}

			String itemStackString = string.substring(idx + 1);
			StackUtils.Stack plantableFlower = StackUtils.parseStackString(itemStackString, OreDictionary.WILDCARD_VALUE);
			if (plantableFlower == null) {
				continue;
			}

			Block plantableFlowerBlock = plantableFlower.getBlock();
			int meta = plantableFlower.getMeta();
			if (plantableFlowerBlock != null) {
				FlowerManager.flowerRegistry.registerPlantableFlower(plantableFlowerBlock, meta, weight, flowerType);
			} else {
				Proxies.log.warning("No block found for '" + plantableFlower + "' in config '" + property.getName() + "'.");
			}
		}
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerApiculture();
	}

	@Override
	public void populateChunk(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		if (Config.getBeehivesAmount() > 0.0) {
			HiveDecorator.instance().decorateHives(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
		}
	}

	@Override
	public void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		if (Config.getBeehivesAmount() > 0.0) {
			HiveDecorator.instance().decorateHives(world, rand, chunkX, chunkZ);
		}
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-candle-lighting-id")) {
			ItemStack value = message.getItemStackValue();
			if (value != null) {
				BlockCandle.addItemToLightingList(value.getItem());
			} else {
				logInvalidIMCMessage(message);
			}
			return true;
		} else if (message.key.equals("add-alveary-slab") && message.isStringMessage()) {
			String messageString = String.format("Received a '%s' request from mod '%s'. This IMC message has been replaced with the oreDictionary for 'slabWood'. Please contact the author and report this issue.", message.key, message.getSender());
			Proxies.log.warning(messageString);
			return true;
		}

		return super.processIMCMessage(message);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 1) {
			EntitySnowFX.icons = new IIcon[3];
			for (int i = 0; i < EntitySnowFX.icons.length; i++) {
				EntitySnowFX.icons[i] = event.map.registerIcon("forestry:particles/snow." + (i + 1));
			}
		}
	}
}
