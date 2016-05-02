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
package forestry.apiculture;

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import forestry.Forestry;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveRegistry.HiveType;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IFlowerAcceptableRule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.blocks.BlockCandle;
import forestry.apiculture.blocks.BlockRegistryApiculture;
import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.blocks.BlockTypeApicultureTesr;
import forestry.apiculture.commands.CommandBee;
import forestry.apiculture.entities.EntityFXBee;
import forestry.apiculture.entities.EntityMinecartApiary;
import forestry.apiculture.entities.EntityMinecartBeehouse;
import forestry.apiculture.flowers.Flower;
import forestry.apiculture.flowers.FlowerRegistry;
import forestry.apiculture.genetics.BeeBranchDefinition;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.BeeFactory;
import forestry.apiculture.genetics.BeeMutationFactory;
import forestry.apiculture.genetics.BeeRoot;
import forestry.apiculture.genetics.BeekeepingMode;
import forestry.apiculture.genetics.HiveDrop;
import forestry.apiculture.genetics.JubilanceFactory;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.apiculture.multiblock.TileAlvearyFan;
import forestry.apiculture.multiblock.TileAlvearyHeater;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearyStabiliser;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.apiculture.network.PacketRegistryApiculture;
import forestry.apiculture.proxy.ProxyApiculture;
import forestry.apiculture.tiles.TileCandle;
import forestry.apiculture.tiles.TileHive;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveDescription;
import forestry.apiculture.worldgen.HiveGenHelper;
import forestry.apiculture.worldgen.HiveRegistry;
import forestry.core.ISaveEventHandler;
import forestry.core.PluginCore;
import forestry.core.PluginFluids;
import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.entities.EntityFXSnow;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Stack;
import forestry.core.utils.Translator;
import forestry.food.PluginFood;
import forestry.food.items.ItemRegistryFood;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.APICULTURE, name = "Apiculture", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.apiculture.description")
public class PluginApiculture extends BlankForestryPlugin {

	@SidedProxy(clientSide = "forestry.apiculture.proxy.ProxyApicultureClient", serverSide = "forestry.apiculture.proxy.ProxyApiculture")
	public static ProxyApiculture proxy;
	private static final String CONFIG_CATEGORY = "apiculture";
	public static String beekeepingMode = "NORMAL";
	private static float secondPrincessChance = 0;
	public static final int ticksPerBeeWorkCycle = 550;
	public static boolean fancyRenderedBees = false;

	public static ItemRegistryApiculture items;
	public static BlockRegistryApiculture blocks;

	public static HiveRegistry hiveRegistry;

	private final Map<String, String[]> defaultAcceptedFlowers = new HashMap<>();
	private final Map<String, String[]> defaultPlantableFlowers = new HashMap<>();

	@Override
	public void setupAPI() {
		super.setupAPI();

		HiveManager.hiveRegistry = hiveRegistry = new HiveRegistry();
		HiveManager.genHelper = new HiveGenHelper();

		FlowerManager.flowerRegistry = new FlowerRegistry();

		BeeManager.commonVillageBees = new ArrayList<>();
		BeeManager.uncommonVillageBees = new ArrayList<>();

		BeeManager.beeFactory = new BeeFactory();
		BeeManager.beeMutationFactory = new BeeMutationFactory();
		BeeManager.jubilanceFactory = new JubilanceFactory();
		BeeManager.armorApiaristHelper = new ArmorApiaristHelper();

		// Init bee interface
		BeeManager.beeRoot = new BeeRoot();
		AlleleManager.alleleRegistry.registerSpeciesRoot(BeeManager.beeRoot);

		// Modes
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.easy);
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.normal);
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.hard);
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.hardcore);
		BeeManager.beeRoot.registerBeekeepingMode(BeekeepingMode.insane);
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryApiculture();
		blocks = new BlockRegistryApiculture();
	}

	@Override
	public void preInit() {
		super.preInit();

		BeeDefinition.preInit();

		MinecraftForge.EVENT_BUS.register(this);

		blocks.apiculture.addDefinitions(BlockTypeApiculture.VALUES);
		blocks.apicultureChest.addDefinitions(BlockTypeApicultureTesr.APIARIST_CHEST);

		/*if (Config.enableVillagers) {
			// Register village components with the Structure registry.
			VillageHandlerApiculture.registerVillageComponents();
		}*/

		// Commands
		PluginCore.rootCommand.addChildCommand(new CommandBee());
	}

	@Override
	public void registerTriggers() {
		ApicultureTriggers.initialize();
	}

	@Override
	public void doInit() {
		File configFile = new File(Forestry.instance.getConfigFolder(), CONFIG_CATEGORY + ".cfg");
		if (!configFile.exists()) {
			setDefaultsForConfig();
		}

		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "2.0.0");
		if (!Objects.equals(config.getLoadedConfigVersion(), config.getDefinedConfigVersion())) {
			boolean deleted = configFile.delete();
			if (deleted) {
				config = new LocalizedConfiguration(configFile, "2.0.0");
				setDefaultsForConfig();
			}
		}

		List<IBeekeepingMode> beekeepingModes = BeeManager.beeRoot.getBeekeepingModes();
		String[] validBeekeepingModeNames = new String[beekeepingModes.size()];
		for (int i = 0; i < beekeepingModes.size(); i++) {
			validBeekeepingModeNames[i] = beekeepingModes.get(i).getName();
		}

		beekeepingMode = config.getStringLocalized("beekeeping", "mode", "NORMAL", validBeekeepingModeNames);
		Log.debug("Beekeeping mode read from config: " + beekeepingMode);

		secondPrincessChance = config.getFloatLocalized("beekeeping", "second.princess", secondPrincessChance, 0.0f, 100.0f);

		String acceptedFlowerMessage = Translator.translateToLocal("for.config.beekeeping.flowers.accepted.comment");
		String plantableFlowerMessage = Translator.translateToLocal("for.config.beekeeping.flowers.plantable.comment");

		FlowerRegistry flowerRegistry = (FlowerRegistry) FlowerManager.flowerRegistry;

		for (String flowerType : flowerRegistry.getFlowerTypes()) {
			String[] defaultAccepted = defaultAcceptedFlowers.get(flowerType);
			if (defaultAccepted == null) {
				defaultAccepted = Constants.EMPTY_STRINGS;
			}
			Property property = config.get("beekeeping.flowers." + flowerType, "accepted", defaultAccepted);
			property.comment = acceptedFlowerMessage;
			parseAcceptedFlowers(property.getStringList(), flowerType);

			String[] defaultPlantable = defaultPlantableFlowers.get(flowerType);
			if (defaultPlantable == null) {
				defaultPlantable = Constants.EMPTY_STRINGS;
			}
			property = config.get("beekeeping.flowers." + flowerType, "plantable", defaultPlantable);
			property.comment = plantableFlowerMessage;
			parsePlantableFlowers(property, flowerType);

			Set<Flower> acceptableFlowers = flowerRegistry.getAcceptableFlowers(flowerType);
			if (acceptableFlowers == null || acceptableFlowers.isEmpty()) {
				Log.error("Flower type '" + flowerType + "' has no valid flowers set in apiculture.cfg. Add valid flowers or delete the config to set it to default.");
			}
		}

		String[] blacklist = config.getStringListLocalized("species", "blacklist", Constants.EMPTY_STRINGS);
		parseBeeBlacklist(blacklist);

		config.save();

		// Genetics
		createAlleles();
		BeeDefinition.initBees();

		// Hives
		createHives();
		registerBeehiveDrops();

		// Inducers for swarmer
		BeeManager.inducers.put(items.royalJelly.getItemStack(), 10);

		GameRegistry.registerTileEntity(TileAlvearyPlain.class, "forestry.Alveary");
		GameRegistry.registerTileEntity(TileHive.class, "forestry.Swarm");
		GameRegistry.registerTileEntity(TileAlvearySwarmer.class, "forestry.AlvearySwarmer");
		GameRegistry.registerTileEntity(TileAlvearyHeater.class, "forestry.AlvearyHeater");
		GameRegistry.registerTileEntity(TileAlvearyFan.class, "forestry.AlvearyFan");
		GameRegistry.registerTileEntity(TileAlvearyHygroregulator.class, "forestry.AlvearyHygro");
		GameRegistry.registerTileEntity(TileAlvearyStabiliser.class, "forestry.AlvearyStabiliser");
		GameRegistry.registerTileEntity(TileAlvearySieve.class, "forestry.AlvearySieve");
		GameRegistry.registerTileEntity(TileCandle.class, "forestry.Candle");

		EntityUtil.registerEntity(EntityMinecartBeehouse.class, "cart.beehouse", 1, 0x000000, 0xffffff, 256, 3, true);
		EntityUtil.registerEntity(EntityMinecartApiary.class, "cart.apiary", 2, 0x000000, 0xffffff, 256, 3, true);

		BeeManager.commonVillageBees.add(BeeDefinition.FOREST.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.MEADOWS.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.MODEST.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.MARSHY.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.WINTRY.getGenome());
		BeeManager.commonVillageBees.add(BeeDefinition.TROPICAL.getGenome());

		BeeManager.uncommonVillageBees.add(BeeDefinition.FOREST.getRainResist().getGenome());
		BeeManager.uncommonVillageBees.add(BeeDefinition.COMMON.getGenome());
		BeeManager.uncommonVillageBees.add(BeeDefinition.VALIANT.getGenome());

		/*if (Config.enableVillagers) {
			// Register villager stuff
			VillageHandlerApiculture villageHandler = new VillageHandlerApiculture();
			VillagerRegistry.instance().registerVillageCreationHandler(villageHandler);
			VillagerRegistry.instance().registerVillagerId(Constants.ID_VILLAGER_BEEKEEPER);
			Proxies.render.registerVillagerSkin(Constants.ID_VILLAGER_BEEKEEPER, Constants.TEXTURE_SKIN_BEEKPEEPER);
			VillagerRegistry.instance().registerVillageTradeHandler(Constants.ID_VILLAGER_BEEKEEPER, villageHandler);
		}*/

		proxy.initializeRendering();

		blocks.apiculture.init();
		blocks.apicultureChest.init();
	}

	@Override
	public void postInit() {
		super.postInit();
		registerDungeonLoot();
	}

	private void setDefaultsForConfig() {
		
		FlowerRegistry flowerRegistry = (FlowerRegistry) FlowerManager.flowerRegistry;

		flowerRegistry.registerAcceptableFlowerRule(new EndFlowerAcceptableRule(), FlowerManager.FlowerTypeEnd);

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

		for (String flowerType : flowerRegistry.getFlowerTypes()) {
			Set<Flower> flowers = flowerRegistry.getAcceptableFlowers(flowerType);
			List<String> acceptedFlowerNames = new ArrayList<>();
			List<String> plantableFlowerNames = new ArrayList<>();
			if (flowers != null) {
				for (Flower flower : flowers) {
					String name = ItemStackUtil.getBlockNameFromRegistryAsSting(flower.getBlock());
					if (name == null) {
						Log.warning("Could not find name for flower: " + flower + " with type: " + flowerType);
						continue;
					}

					int meta = flower.getMeta();
					if (flower.getMeta() != OreDictionary.WILDCARD_VALUE) {
						name = name + ':' + meta;
					}

					if (flower.isPlantable()) {
						plantableFlowerNames.add(name);
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

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryApiculture();
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		crateRegistry.registerCrate(PluginCore.items.beeswax.getItemStack());
		crateRegistry.registerCrate(items.pollenCluster.get(EnumPollenCluster.NORMAL, 1));
		crateRegistry.registerCrate(items.pollenCluster.get(EnumPollenCluster.CRYSTALLINE, 1));
		crateRegistry.registerCrate(items.propolis.getItemStack());
		crateRegistry.registerCrate(items.honeydew.getItemStack());
		crateRegistry.registerCrate(items.royalJelly.getItemStack());

		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.HONEY, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.COCOA, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.SIMMERING, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.STRINGY, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.FROZEN, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.DRIPPING, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.SILKY, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.PARCHED, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.MYSTERIOUS, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.POWDERY, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.WHEATEN, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.MOSSY, 1));
		crateRegistry.registerCrate(items.beeComb.get(EnumHoneyComb.MELLOW, 1));

		crateRegistry.registerCrate(PluginCore.items.refractoryWax.getItemStack());
	}

	@Override
	public void registerRecipes() {

		// / APIARIST'S ARMOR
		ItemStack wovenSilk = PluginCore.items.craftingMaterial.getWovenSilk();
		RecipeUtil.addRecipe(items.apiaristHat,
				"###", "# #",
				'#', wovenSilk);
		RecipeUtil.addRecipe(items.apiaristChest,
				"# #", "###", "###",
				'#', wovenSilk);
		RecipeUtil.addRecipe(items.apiaristLegs,
				"###", "# #", "# #",
				'#', wovenSilk);
		RecipeUtil.addRecipe(items.apiaristBoots,
				"# #", "# #",
				'#', wovenSilk);

		// / HABITAT LOCATOR
		RecipeUtil.addRecipe(items.habitatLocator,
				" X ",
				"X#X",
				" X ",
				'#', "dustRedstone", 'X', "ingotBronze");

		// Bees
		RecipeUtil.addRecipe(items.scoop,
				"#X#", "###", " # ",
				'#', "stickWood",
				'X', Blocks.wool);
		RecipeUtil.addRecipe(items.smoker,
				"LS#",
				"LF#",
				"###",
				'#', "ingotTin", 'S', "stickWood", 'F', Items.flint_and_steel, 'L', "leather");
		RecipeUtil.addRecipe(new ItemStack(Items.slime_ball),
				"#X#", "#X#", "#X#",
				'#', items.propolis,
				'X', items.pollenCluster.get(EnumPollenCluster.NORMAL, 1));
		RecipeUtil.addRecipe(new ItemStack(Items.speckled_melon),
				"#X#", "#Y#", "#X#",
				'#', items.honeyDrop,
				'X', items.honeydew,
				'Y', Items.melon);
		RecipeUtil.addRecipe(items.frameUntreated,
				"###", "#S#", "###",
				'#', "stickWood",
				'S', Items.string);
		RecipeUtil.addRecipe(items.frameImpregnated,
				"###", "#S#", "###",
				'#', PluginCore.items.stickImpregnated,
				'S', Items.string);
		RecipeUtil.addRecipe(items.minecartBeehouse.getBeeHouseMinecart(),
				"B",
				"C",
				'B', blocks.apiculture.get(BlockTypeApiculture.BEE_HOUSE),
				'C', Items.minecart);
		RecipeUtil.addRecipe(items.minecartBeehouse.getApiaryMinecart(),
				"B",
				"C",
				'B', blocks.apiculture.get(BlockTypeApiculture.APIARY),
				'C', Items.minecart);

		// FOOD STUFF
		ItemRegistryFood foodItems = PluginFood.items;
		if (foodItems != null) {
			RecipeUtil.addRecipe(new ItemStack(foodItems.honeyedSlice, 4),
					"###", "#X#", "###",
					'#', items.honeyDrop,
					'X', Items.bread);

			RecipeUtil.addRecipe(foodItems.honeyPot,
					"# #", " X ", "# #",
					'#', items.honeyDrop,
					'X', PluginFluids.items.waxCapsuleEmpty);

			RecipeUtil.addRecipe(foodItems.ambrosia,
					"#Y#", "XXX", "###",
					'#', items.honeydew,
					'X', items.royalJelly,
					'Y', PluginFluids.items.waxCapsuleEmpty);
		}

		// / CAPSULES
		int outputCapsuleAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.capsule");
		if (outputCapsuleAmount > 0) {
			ItemStack capsule = PluginFluids.items.waxCapsuleEmpty.getItemStack(outputCapsuleAmount);
			RecipeUtil.addRecipe(capsule, "###", '#', PluginCore.items.beeswax);
		}

		int outputRefractoryAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.refractory");
		if (outputRefractoryAmount > 0) {
			ItemStack capsule = PluginFluids.items.refractoryEmpty.getItemStack(outputRefractoryAmount);
			RecipeUtil.addRecipe(capsule, "###", '#', PluginCore.items.refractoryWax);
		}

		// / BITUMINOUS PEAT
		RecipeUtil.addRecipe(PluginCore.items.bituminousPeat.getItemStack(),
				" # ", "XYX", " # ",
				'#', "dustAsh",
				'X', PluginCore.items.peat,
				'Y', items.propolis);

		// / TORCHES
		RecipeUtil.addRecipe(new ItemStack(Blocks.torch, 3),
				" # ", " # ", " Y ",
				'#', PluginCore.items.beeswax,
				'Y', "stickWood");
		RecipeUtil.addRecipe(PluginCore.items.craftingMaterial.getPulsatingMesh(),
				"# #", " # ", "# #",
				'#', items.propolis.get(EnumPropolis.PULSATING, 1));

		// / WAX CAST
		RecipeUtil.addRecipe(items.waxCast,
				"###",
				"# #",
				"###",
				'#', PluginCore.items.beeswax);

		// / ALVEARY
		ItemStack alvearyPlainBlock = blocks.getAlvearyBlock(BlockAlvearyType.PLAIN);
		RecipeUtil.addRecipe(alvearyPlainBlock,
				"###",
				"#X#",
				"###",
				'X', PluginCore.items.impregnatedCasing,
				'#', PluginCore.items.craftingMaterial.getScentedPaneling());
		// SWARMER
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.SWARMER),
				"#G#",
				" X ",
				"#G#",
				'#', PluginCore.items.tubes.get(EnumElectronTube.DIAMOND, 1),
				'X', alvearyPlainBlock,
				'G', "ingotGold");
		// FAN
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.FAN),
				"I I",
				" X ",
				"I#I",
				'#', PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1),
				'X', alvearyPlainBlock,
				'I', "ingotIron");
		// HEATER
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.HEATER),
				"#I#",
				" X ",
				"YYY",
				'#', PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1),
				'X', alvearyPlainBlock,
				'I', "ingotIron", 'Y', "stone");
		// HYGROREGULATOR
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.HYGRO),
				"GIG",
				"GXG",
				"GIG",
				'X', alvearyPlainBlock,
				'I', "ingotIron",
				'G', "blockGlass");
		// STABILISER
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.STABILISER),
				"G G",
				"GXG",
				"G G",
				'X', alvearyPlainBlock,
				'G', "gemQuartz");
		// SIEVE
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.SIEVE),
				"III",
				" X ",
				"WWW",
				'X', alvearyPlainBlock,
				'I', "ingotIron",
				'W', PluginCore.items.craftingMaterial.getWovenSilk());

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {
			// / SQUEEZER
			FluidStack honeyDropFluid = Fluids.FOR_HONEY.getFluid(Constants.FLUID_PER_HONEY_DROP);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{items.honeyDrop.getItemStack()}, honeyDropFluid, items.propolis.getItemStack(), 5);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{items.honeydew.getItemStack()}, honeyDropFluid);

			ItemStack phosphor = PluginCore.items.phosphor.getItemStack(2);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{phosphor, new ItemStack(Blocks.sand)}, Fluids.LAVA.getFluid(2000));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{phosphor, new ItemStack(Blocks.sand, 1, 1)}, Fluids.LAVA.getFluid(2000));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{phosphor, new ItemStack(Blocks.dirt)}, Fluids.LAVA.getFluid(1600));

			// / CARPENTER
			RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null, items.beealyzer.getItemStack(),
					"X#X", "X#X", "RDR",
					'#', "paneGlass",
					'X', "ingotTin",
					'R', "dustRedstone",
					'D', "gemDiamond");
			RecipeManagers.carpenterManager.addRecipe(50, Fluids.FOR_HONEY.getFluid(500), null, PluginCore.items.craftingMaterial.getScentedPaneling(),
					" J ", "###", "WPW",
					'#', "plankWood",
					'J', items.royalJelly,
					'W', PluginCore.items.beeswax,
					'P', items.pollenCluster.get(EnumPollenCluster.NORMAL, 1));

			RecipeManagers.carpenterManager.addRecipe(30, Fluids.WATER.getFluid(600), null, blocks.candle.getUnlitCandle(24),
					" X ",
					"###",
					"###",
					'#', PluginCore.items.beeswax,
					'X', Items.string);
			RecipeManagers.carpenterManager.addRecipe(10, Fluids.WATER.getFluid(200), null, blocks.candle.getUnlitCandle(6),
					"#X#",
					'#', PluginCore.items.beeswax,
					'X', PluginCore.items.craftingMaterial.getSilkWisp());
			RecipeUtil.addShapelessRecipe(blocks.candle.getUnlitCandle(1), blocks.candle.getUnlitCandle(1));
			RecipeUtil.addShapelessRecipe(blocks.candle.getLitCandle(1), blocks.candle.getLitCandle(1));

			// / CENTRIFUGE
			// Honey combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.HONEY, 1), ImmutableMap.of(
					PluginCore.items.beeswax.getItemStack(), 1.0f,
					items.honeyDrop.getItemStack(), 0.9f
			));

			// Cocoa combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.COCOA, 1), ImmutableMap.of(
					PluginCore.items.beeswax.getItemStack(), 1.0f,
					new ItemStack(Items.dye, 1, 3), 0.5f
			));

			// Simmering combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.SIMMERING, 1), ImmutableMap.of(
					PluginCore.items.refractoryWax.getItemStack(), 1.0f,
					PluginCore.items.phosphor.getItemStack(2), 0.7f
			));

			// Stringy combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.STRINGY, 1), ImmutableMap.of(
					items.propolis.getItemStack(), 1.0f,
					items.honeyDrop.getItemStack(), 0.4f
			));

			// Dripping combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.DRIPPING, 1), ImmutableMap.of(
					items.honeydew.getItemStack(), 1.0f,
					items.honeyDrop.getItemStack(), 0.4f
			));

			// Frozen combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.FROZEN, 1), ImmutableMap.of(
					PluginCore.items.beeswax.getItemStack(), 0.8f,
					items.honeyDrop.getItemStack(), 0.7f,
					new ItemStack(Items.snowball), 0.4f,
					items.pollenCluster.get(EnumPollenCluster.CRYSTALLINE, 1), 0.2f
			));

			// Silky combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.SILKY, 1), ImmutableMap.of(
					items.honeyDrop.getItemStack(), 1.0f,
					items.propolis.get(EnumPropolis.SILKY, 1), 0.8f
			));

			// Parched combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.PARCHED, 1), ImmutableMap.of(
					PluginCore.items.beeswax.getItemStack(), 1.0f,
					items.honeyDrop.getItemStack(), 0.9f
			));

			// Mysterious combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.MYSTERIOUS, 1), ImmutableMap.of(
					items.propolis.get(EnumPropolis.PULSATING, 1), 1.0f,
					items.honeyDrop.getItemStack(), 0.4f
			));

			// Irradiated combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.IRRADIATED, 1), ImmutableMap.of(
			));

			// Powdery combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.POWDERY, 1), ImmutableMap.of(
					items.honeyDrop.getItemStack(), 0.2f,
					PluginCore.items.beeswax.getItemStack(), 0.2f,
					new ItemStack(Items.gunpowder), 0.9f
			));

			// Wheaten Combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.WHEATEN, 1), ImmutableMap.of(
					items.honeyDrop.getItemStack(), 0.2f,
					PluginCore.items.beeswax.getItemStack(), 0.2f,
					new ItemStack(Items.wheat), 0.8f
			));

			// Mossy Combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.MOSSY, 1), ImmutableMap.of(
					PluginCore.items.beeswax.getItemStack(), 1.0f,
					items.honeyDrop.getItemStack(), 0.9f
			));

			// Mellow Combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.MELLOW, 1), ImmutableMap.of(
					items.honeydew.getItemStack(), 0.6f,
					PluginCore.items.beeswax.getItemStack(), 0.2f,
					new ItemStack(Items.quartz), 0.3f
			));

			// Silky Propolis
			RecipeManagers.centrifugeManager.addRecipe(5, items.propolis.get(EnumPropolis.SILKY, 1), ImmutableMap.of(
					PluginCore.items.craftingMaterial.getSilkWisp(), 0.6f,
					items.propolis.getItemStack(), 0.1f
			));

			// / FERMENTER
			RecipeManagers.fermenterManager.addRecipe(items.honeydew.getItemStack(), 500, 1.0f, Fluids.SHORT_MEAD.getFluid(1), Fluids.FOR_HONEY.getFluid(1));
		}

		// ANALYZER
		RecipeUtil.addRecipe(PluginCore.blocks.core.get(BlockTypeCoreTesr.ANALYZER),
				"XTX",
				" Y ",
				"X X",
				'Y', PluginCore.items.sturdyCasing,
				'T', items.beealyzer,
				'X', "ingotBronze");

		RecipeUtil.addRecipe(blocks.apiculture.get(BlockTypeApiculture.APIARY),
				"XXX",
				"#C#",
				"###",
				'X', "slabWood",
				'#', "plankWood",
				'C', PluginCore.items.impregnatedCasing);

		RecipeUtil.addRecipe(new ItemStack(blocks.apicultureChest),
				" # ",
				"XYX",
				"XXX",
				'#', "blockGlass",
				'X', "beeComb",
				'Y', "chestWood");

		RecipeUtil.addRecipe(blocks.apiculture.get(BlockTypeApiculture.BEE_HOUSE),
				"XXX",
				"#C#",
				"###",
				'X', "slabWood",
				'#', "plankWood",
				'C', "beeComb");
	}

	private static void registerBeehiveDrops() {
		ItemStack honeyComb = items.beeComb.get(EnumHoneyComb.HONEY, 1);
		hiveRegistry.addDrops(HiveType.FOREST.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.FOREST, honeyComb).setIgnobleShare(0.7),
				new HiveDrop(0.08, BeeDefinition.FOREST.getRainResist(), honeyComb),
				new HiveDrop(0.03, BeeDefinition.VALIANT, honeyComb)
		);

		hiveRegistry.addDrops(HiveType.MEADOWS.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.MEADOWS, honeyComb).setIgnobleShare(0.7),
				new HiveDrop(0.03, BeeDefinition.VALIANT, honeyComb)
		);

		ItemStack parchedComb = items.beeComb.get(EnumHoneyComb.PARCHED, 1);
		hiveRegistry.addDrops(HiveType.DESERT.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.MODEST, parchedComb).setIgnobleShare(0.7),
				new HiveDrop(0.03, BeeDefinition.VALIANT, parchedComb)
		);

		ItemStack silkyComb = items.beeComb.get(EnumHoneyComb.SILKY, 1);
		hiveRegistry.addDrops(HiveType.JUNGLE.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.TROPICAL, silkyComb).setIgnobleShare(0.7),
				new HiveDrop(0.03, BeeDefinition.VALIANT, silkyComb)
		);

		ItemStack mysteriousComb = items.beeComb.get(EnumHoneyComb.MYSTERIOUS, 1);
		hiveRegistry.addDrops(HiveType.END.getHiveUid(),
				new HiveDrop(0.90, BeeDefinition.ENDED, mysteriousComb)
		);

		ItemStack frozenComb = items.beeComb.get(EnumHoneyComb.FROZEN, 1);
		hiveRegistry.addDrops(HiveType.SNOW.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.WINTRY, frozenComb).setIgnobleShare(0.5),
				new HiveDrop(0.03, BeeDefinition.VALIANT, frozenComb)
		);

		ItemStack mossyComb = items.beeComb.get(EnumHoneyComb.MOSSY, 1);
		hiveRegistry.addDrops(HiveType.SWAMP.getHiveUid(),
				new HiveDrop(0.80, BeeDefinition.MARSHY, mossyComb).setIgnobleShare(0.4),
				new HiveDrop(0.03, BeeDefinition.VALIANT, mossyComb)
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

		ItemStack stack = blocks.candle.getUnlitCandle(1);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(BlockCandle.colourTagName, 0xffffff);
		stack.setTagCompound(tag);

		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(stack, 7, 12, 12));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(items.scoop.getItemStack(), 1, 1, 8));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(items.smoker.getItemStack(), 1, 1, 8));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(items.propolis.get(EnumPropolis.NORMAL, 1), 2, 4, 6));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(items.beeComb.get(EnumHoneyComb.HONEY, 1), 4, 12, 7));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(items.beeComb.get(EnumHoneyComb.FROZEN, 1), 2, 10, 7));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(items.beeComb.get(EnumHoneyComb.SILKY, 1), 1, 6, 7));

		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(BeeDefinition.FOREST.getRainResist().getMemberStack(EnumBeeType.PRINCESS), 1, 1, 5));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(BeeDefinition.COMMON.getMemberStack(EnumBeeType.DRONE), 1, 2, 8));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(BeeDefinition.MEADOWS.getMemberStack(EnumBeeType.PRINCESS), 1, 1, 5));
	}

	private static void createHives() {
		hiveRegistry.registerHive(HiveType.FOREST.getHiveUid(), HiveDescription.FOREST);
		hiveRegistry.registerHive(HiveType.MEADOWS.getHiveUid(), HiveDescription.MEADOWS);
		hiveRegistry.registerHive(HiveType.DESERT.getHiveUid(), HiveDescription.DESERT);
		hiveRegistry.registerHive(HiveType.JUNGLE.getHiveUid(), HiveDescription.JUNGLE);
		hiveRegistry.registerHive(HiveType.END.getHiveUid(), HiveDescription.END);
		hiveRegistry.registerHive(HiveType.SNOW.getHiveUid(), HiveDescription.SNOW);
		hiveRegistry.registerHive(HiveType.SWAMP.getHiveUid(), HiveDescription.SWAMP);
	}

	private static void createAlleles() {

		IClassification hymnoptera = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "hymnoptera", "Hymnoptera");
		AlleleManager.alleleRegistry.getClassification("class.insecta").addMemberGroup(hymnoptera);

		IClassification apidae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "apidae", "Apidae");
		hymnoptera.addMemberGroup(apidae);

		for (BeeBranchDefinition beeBranch : BeeBranchDefinition.values()) {
			apidae.addMemberGroup(beeBranch.getBranch());
		}

		AlleleEffect.createAlleles();
	}

	public static double getSecondPrincessChance() {
		return secondPrincessChance;
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

	private static void parseAcceptedFlowers(String[] acceptedFlowers, String flowerType) {
		List<Stack> acceptedFlowerItemStacks = Stack.parseStackStrings(acceptedFlowers, OreDictionary.WILDCARD_VALUE);
		for (Stack acceptedFlower : acceptedFlowerItemStacks) {
			Block acceptedFlowerBlock = acceptedFlower.getBlock();
			int meta = acceptedFlower.getMeta();
			if (acceptedFlowerBlock != null) {
				FlowerManager.flowerRegistry.registerAcceptableFlower(acceptedFlowerBlock, meta, flowerType);
			} else {
				Log.warning("No block found for '" + acceptedFlower + "' in apiculture config for '" + flowerType + "'.");
			}
		}
	}

	private static void parsePlantableFlowers(Property property, String flowerType) {
		for (String string : property.getStringList()) {
			Stack plantableFlower = Stack.parseStackString(string, OreDictionary.WILDCARD_VALUE);
			if (plantableFlower == null) {
				continue;
			}

			Block plantableFlowerBlock = plantableFlower.getBlock();
			int meta = plantableFlower.getMeta();
			if (plantableFlowerBlock != null) {
				FlowerManager.flowerRegistry.registerPlantableFlower(plantableFlowerBlock, meta, 1.0, flowerType);
			} else {
				Log.warning("No block found for '" + string + "' in config '" + property.getName() + "'.");
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
			HiveDecorator.decorateHives(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
		}
	}

	@Override
	public void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		if (Config.getBeehivesAmount() > 0.0) {
			HiveDecorator.decorateHives(world, rand, chunkX, chunkZ);
		}
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-candle-lighting-id")) {
			ItemStack value = message.getItemStackValue();
			if (value != null) {
				BlockCandle.addItemToLightingList(value.getItem());
			} else {
				IMCUtil.logInvalidIMCMessage(message);
			}
			return true;
		} else if (message.key.equals("add-alveary-slab") && message.isStringMessage()) {
			String messageString = String.format("Received a '%s' request from mod '%s'. This IMC message has been replaced with the oreDictionary for 'slabWood'. Please contact the author and report this issue.", message.key, message.getSender());
			Log.warning(messageString);
			return true;
		}

		return super.processIMCMessage(message);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		EntityFXSnow.sprites = new TextureAtlasSprite[3];
		for (int i = 0; i < EntityFXSnow.sprites.length; i++) {
			EntityFXSnow.sprites[i] = event.map.registerSprite(new ResourceLocation("forestry:entity/particles/snow." + (i + 1)));
		}
		EntityFXBee.beeSprite = event.map.registerSprite(new ResourceLocation("forestry:entity/particles/swarm_bee"));
	}

	private static class EndFlowerAcceptableRule implements IFlowerAcceptableRule {
		@Override
		public boolean isAcceptableFlower(String flowerType, World world, BlockPos pos) {
			BiomeGenBase biomeGenForCoords = world.getBiomeGenForCoords(pos);
			return BiomeDictionary.isBiomeOfType(biomeGenForCoords, BiomeDictionary.Type.END);
		}
	}
}
