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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
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
import forestry.apiculture.commands.CommandBee;
import forestry.apiculture.entities.EntityMinecartApiary;
import forestry.apiculture.entities.EntityMinecartBeehouse;
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
import forestry.apiculture.proxy.ProxyApicultureClient;
import forestry.apiculture.tiles.TileCandle;
import forestry.apiculture.tiles.TileHive;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveDescription;
import forestry.apiculture.worldgen.HiveGenHelper;
import forestry.apiculture.worldgen.HiveRegistry;
import forestry.core.ISaveEventHandler;
import forestry.core.PluginCore;
import forestry.core.PluginFluids;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.entities.ParticleSnow;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.Log;
import forestry.core.utils.OreDictUtil;
import forestry.core.utils.VillagerTradeLists;
import forestry.food.PluginFood;
import forestry.food.items.ItemRegistryFood;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ForestryPlugin(pluginID = ForestryPluginUids.APICULTURE, name = "Apiculture", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.apiculture.description")
public class PluginApiculture extends BlankForestryPlugin {

	@SidedProxy(clientSide = "forestry.apiculture.proxy.ProxyApicultureClient", serverSide = "forestry.apiculture.proxy.ProxyApiculture")
	public static ProxyApiculture proxy;
	private static final String CONFIG_CATEGORY = "apiculture";
	public static String beekeepingMode = "NORMAL";
	private static float secondPrincessChance = 0;
	public static final int ticksPerBeeWorkCycle = 550;

	public static ItemRegistryApiculture items;
	public static BlockRegistryApiculture blocks;

	public static HiveRegistry hiveRegistry;

	public static VillagerRegistry.VillagerProfession villagerApiarist;

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

		if (Config.enableVillagers) {
			// Register village components with the Structure registry.
			VillageCreationApiculture.registerVillageComponents();
		}

		// Commands
		PluginCore.rootCommand.addChildCommand(new CommandBee());
	}

	// TODO: Buildcraft for 1.9
//	@Override
//	public void registerTriggers() {
//		ApicultureTriggers.initialize();
//	}

	@Override
	public void doInit() {
		File configFile = new File(Forestry.instance.getConfigFolder(), CONFIG_CATEGORY + ".cfg");

		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "3.0.0");
		if (!Objects.equals(config.getLoadedConfigVersion(), config.getDefinedConfigVersion())) {
			boolean deleted = configFile.delete();
			if (deleted) {
				config = new LocalizedConfiguration(configFile, "3.0.0");
			}
		}

		initFlowerRegistry();

		List<IBeekeepingMode> beekeepingModes = BeeManager.beeRoot.getBeekeepingModes();
		String[] validBeekeepingModeNames = new String[beekeepingModes.size()];
		for (int i = 0; i < beekeepingModes.size(); i++) {
			validBeekeepingModeNames[i] = beekeepingModes.get(i).getName();
		}

		beekeepingMode = config.getStringLocalized("beekeeping", "mode", "NORMAL", validBeekeepingModeNames);
		Log.debug("Beekeeping mode read from config: " + beekeepingMode);

		secondPrincessChance = config.getFloatLocalized("beekeeping", "second.princess", secondPrincessChance, 0.0f, 100.0f);

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

		if (Config.enableVillagers) {
			// Register villager stuff
			VillageCreationApiculture villageHandler = new VillageCreationApiculture();
			VillagerRegistry villagerRegistry = VillagerRegistry.instance();
			villagerRegistry.registerVillageCreationHandler(villageHandler);

			villagerApiarist = new VillagerRegistry.VillagerProfession(Constants.ID_VILLAGER_APIARIST, Constants.TEXTURE_SKIN_BEEKPEEPER, Constants.TEXTURE_SKIN_ZOMBIE_BEEKPEEPER);
			villagerRegistry.register(villagerApiarist);

			ItemStack wildcardPrincess = new ItemStack(items.beePrincessGE, 1);
			ItemStack wildcardDrone = new ItemStack(items.beeDroneGE, 1);
			ItemStack apiary = new ItemStack(blocks.apiary);
			ItemStack provenFrames = items.frameProven.getItemStack();
			ItemStack monasticDrone = BeeDefinition.MONASTIC.getMemberStack(EnumBeeType.DRONE);
			ItemStack endDrone = BeeDefinition.ENDED.getMemberStack(EnumBeeType.DRONE);
			ItemStack propolis = new ItemStack(PluginApiculture.items.propolis,1);

			VillagerRegistry.VillagerCareer apiaristCareer = new VillagerRegistry.VillagerCareer(villagerApiarist, "apiarist");
			apiaristCareer.addTrade(1,
					new VillagerApiaristTrades.GiveRandomCombsForItems(new ItemStack(Items.WHEAT), new EntityVillager.PriceInfo(8, 12), new EntityVillager.PriceInfo(2, 4)),
					new VillagerApiaristTrades.GiveRandomCombsForItems(new ItemStack(Items.CARROT), new EntityVillager.PriceInfo(8, 12), new EntityVillager.PriceInfo(2, 4)),
					new VillagerApiaristTrades.GiveRandomCombsForItems(new ItemStack(Items.POTATO), new EntityVillager.PriceInfo(8, 12), new EntityVillager.PriceInfo(2, 4))
			);
			apiaristCareer.addTrade(2,
					new VillagerTradeLists.GiveItemForEmeralds(new EntityVillager.PriceInfo(1, 4), new ItemStack(items.smoker), null),
					new VillagerTradeLists.GiveItemForLogsAndEmeralds(apiary, new EntityVillager.PriceInfo(1, 1), new EntityVillager.PriceInfo(16, 32), new EntityVillager.PriceInfo(1, 2)),
					new VillagerApiaristTrades.GiveRandomHiveDroneForItems(propolis, null, wildcardDrone, new EntityVillager.PriceInfo(2, 4))
			);
			apiaristCareer.addTrade(3,
					new VillagerTradeLists.GiveEmeraldForItems(wildcardPrincess, null),
					new VillagerTradeLists.GiveItemForEmeralds(new EntityVillager.PriceInfo(1, 2), provenFrames, new EntityVillager.PriceInfo(1, 6))
			);
			apiaristCareer.addTrade(4,
					new VillagerTradeLists.GiveItemForItemAndEmerald(wildcardPrincess, null, new EntityVillager.PriceInfo(10, 64), monasticDrone, null),
					new VillagerTradeLists.GiveItemForTwoItems(wildcardPrincess, null,new ItemStack(Items.ENDER_EYE),new EntityVillager.PriceInfo(12, 16),endDrone, null)
			);
		}

		blocks.apiary.init();
		blocks.beeHouse.init();
		blocks.beeChest.init();
	}

	@Override
	public void postInit() {
		super.postInit();
		registerDungeonLoot();
	}

	private void initFlowerRegistry() {
		FlowerRegistry flowerRegistry = (FlowerRegistry) FlowerManager.flowerRegistry;

		flowerRegistry.registerAcceptableFlowerRule(new EndFlowerAcceptableRule(), FlowerManager.FlowerTypeEnd);

		// Register acceptable plants
		flowerRegistry.registerAcceptableFlower(Blocks.DRAGON_EGG, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.VINE, FlowerManager.FlowerTypeJungle);
		flowerRegistry.registerAcceptableFlower(Blocks.TALLGRASS, FlowerManager.FlowerTypeJungle);
		flowerRegistry.registerAcceptableFlower(Blocks.WHEAT, FlowerManager.FlowerTypeWheat);
		flowerRegistry.registerAcceptableFlower(Blocks.PUMPKIN_STEM, FlowerManager.FlowerTypeGourd);
		flowerRegistry.registerAcceptableFlower(Blocks.MELON_STEM, FlowerManager.FlowerTypeGourd);
		flowerRegistry.registerAcceptableFlower(Blocks.NETHER_WART, FlowerManager.FlowerTypeNether);
		flowerRegistry.registerAcceptableFlower(Blocks.CACTUS, FlowerManager.FlowerTypeCacti);

		// Register plantable plants
		for (BlockFlower.EnumFlowerType flowerType : BlockFlower.EnumFlowerType.values()) {
			IBlockState blockState;
			switch (flowerType.getBlockType()) {
				case RED:
					blockState = Blocks.RED_FLOWER.getDefaultState().withProperty(Blocks.RED_FLOWER.getTypeProperty(), flowerType);
					break;
				case YELLOW:
					blockState = Blocks.YELLOW_FLOWER.getDefaultState().withProperty(Blocks.YELLOW_FLOWER.getTypeProperty(), flowerType);
					break;
				default:
					continue;
			}
			flowerRegistry.registerPlantableFlower(blockState, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		}

		flowerRegistry.registerPlantableFlower(Blocks.BROWN_MUSHROOM.getDefaultState(), 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.RED_MUSHROOM.getDefaultState(), 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.CACTUS.getDefaultState(), 1.0, FlowerManager.FlowerTypeCacti);
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
				'#', OreDictUtil.DUST_REDSTONE, 'X', OreDictUtil.INGOT_BRONZE);

		// Bees
		RecipeUtil.addRecipe(items.scoop,
				"#X#", "###", " # ",
				'#', OreDictUtil.STICK_WOOD,
				'X', Blocks.WOOL);
		RecipeUtil.addRecipe(items.smoker,
				"LS#",
				"LF#",
				"###",
				'#', "ingotTin", 'S', OreDictUtil.STICK_WOOD, 'F', Items.FLINT_AND_STEEL, 'L', OreDictUtil.LEATHER);
		RecipeUtil.addRecipe(new ItemStack(Items.SLIME_BALL),
				"#X#", "#X#", "#X#",
				'#', items.propolis,
				'X', items.pollenCluster.get(EnumPollenCluster.NORMAL, 1));
		RecipeUtil.addRecipe(new ItemStack(Items.SPECKLED_MELON),
				"#X#", "#Y#", "#X#",
				'#', items.honeyDrop,
				'X', items.honeydew,
				'Y', Items.MELON);
		RecipeUtil.addRecipe(items.frameUntreated,
				"###", "#S#", "###",
				'#', OreDictUtil.STICK_WOOD,
				'S', Items.STRING);
		RecipeUtil.addRecipe(items.frameImpregnated,
				"###", "#S#", "###",
				'#', PluginCore.items.stickImpregnated,
				'S', Items.STRING);
		RecipeUtil.addRecipe(items.minecartBeehouse.getBeeHouseMinecart(),
				"B",
				"C",
				'B', new ItemStack(blocks.beeHouse),
				'C', Items.MINECART);
		RecipeUtil.addRecipe(items.minecartBeehouse.getApiaryMinecart(),
				"B",
				"C",
				'B', new ItemStack(blocks.apiary),
				'C', Items.MINECART);

		// FOOD STUFF
		ItemRegistryFood foodItems = PluginFood.items;
		if (foodItems != null) {
			RecipeUtil.addRecipe(new ItemStack(foodItems.honeyedSlice, 4),
					"###", "#X#", "###",
					'#', items.honeyDrop,
					'X', Items.BREAD);

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
				'#', OreDictUtil.DUST_ASH,
				'X', PluginCore.items.peat,
				'Y', items.propolis);

		// / TORCHES
		RecipeUtil.addRecipe(new ItemStack(Blocks.TORCH, 3),
				" # ", " # ", " Y ",
				'#', PluginCore.items.beeswax,
				'Y', OreDictUtil.STICK_WOOD);
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
				'G', OreDictUtil.INGOT_GOLD);
		// FAN
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.FAN),
				"I I",
				" X ",
				"I#I",
				'#', PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1),
				'X', alvearyPlainBlock,
				'I', OreDictUtil.INGOT_IRON);
		// HEATER
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.HEATER),
				"#I#",
				" X ",
				"YYY",
				'#', PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1),
				'X', alvearyPlainBlock,
				'I', OreDictUtil.INGOT_IRON, 'Y', OreDictUtil.STONE);
		// HYGROREGULATOR
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.HYGRO),
				"GIG",
				"GXG",
				"GIG",
				'X', alvearyPlainBlock,
				'I', OreDictUtil.INGOT_IRON,
				'G', OreDictUtil.BLOCK_GLASS);
		// STABILISER
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.STABILISER),
				"G G",
				"GXG",
				"G G",
				'X', alvearyPlainBlock,
				'G', OreDictUtil.GEM_QUARTZ);
		// SIEVE
		RecipeUtil.addRecipe(blocks.getAlvearyBlock(BlockAlvearyType.SIEVE),
				"III",
				" X ",
				"WWW",
				'X', alvearyPlainBlock,
				'I', OreDictUtil.INGOT_IRON,
				'W', PluginCore.items.craftingMaterial.getWovenSilk());

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {
			// / SQUEEZER
			FluidStack honeyDropFluid = Fluids.FOR_HONEY.getFluid(Constants.FLUID_PER_HONEY_DROP);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{items.honeyDrop.getItemStack()}, honeyDropFluid, items.propolis.getItemStack(), 5);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{items.honeydew.getItemStack()}, honeyDropFluid);

			ItemStack phosphor = PluginCore.items.phosphor.getItemStack(2);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{phosphor, new ItemStack(Blocks.SAND)}, new FluidStack(FluidRegistry.LAVA, 2000));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{phosphor, new ItemStack(Blocks.SAND, 1, 1)}, new FluidStack(FluidRegistry.LAVA, 2000));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{phosphor, new ItemStack(Blocks.DIRT)}, new FluidStack(FluidRegistry.LAVA, 1600));

			// / CARPENTER
			RecipeManagers.carpenterManager.addRecipe(50, Fluids.FOR_HONEY.getFluid(500), null, PluginCore.items.craftingMaterial.getScentedPaneling(),
					" J ", "###", "WPW",
					'#', OreDictUtil.PLANK_WOOD,
					'J', items.royalJelly,
					'W', PluginCore.items.beeswax,
					'P', items.pollenCluster.get(EnumPollenCluster.NORMAL, 1));
			
			RecipeManagers.carpenterManager.addRecipe(30, new FluidStack(FluidRegistry.WATER, 600), null, blocks.candle.getUnlitCandle(24),
					" X ",
					"###",
					"###",
					'#', PluginCore.items.beeswax,
					'X', Items.STRING);
			RecipeManagers.carpenterManager.addRecipe(10, new FluidStack(FluidRegistry.WATER, 200), null, blocks.candle.getUnlitCandle(6),
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
					new ItemStack(Items.DYE, 1, 3), 0.5f
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
					new ItemStack(Items.SNOWBALL), 0.4f,
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
					new ItemStack(Items.GUNPOWDER), 0.9f
			));

			// Wheaten Combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.WHEATEN, 1), ImmutableMap.of(
					items.honeyDrop.getItemStack(), 0.2f,
					PluginCore.items.beeswax.getItemStack(), 0.2f,
					new ItemStack(Items.WHEAT), 0.8f
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
					new ItemStack(Items.QUARTZ), 0.3f
			));

			// Silky Propolis
			RecipeManagers.centrifugeManager.addRecipe(5, items.propolis.get(EnumPropolis.SILKY, 1), ImmutableMap.of(
					PluginCore.items.craftingMaterial.getSilkWisp(), 0.6f,
					items.propolis.getItemStack(), 0.1f
			));

			// / FERMENTER
			RecipeManagers.fermenterManager.addRecipe(items.honeydew.getItemStack(), 500, 1.0f, Fluids.SHORT_MEAD.getFluid(1), Fluids.FOR_HONEY.getFluid(1));
		}

		RecipeUtil.addRecipe(blocks.apiary,
				"XXX",
				"#C#",
				"###",
				'X', OreDictUtil.SLAB_WOOD,
				'#', OreDictUtil.PLANK_WOOD,
				'C', PluginCore.items.impregnatedCasing);

		RecipeUtil.addRecipe(blocks.beeChest,
				" # ",
				"XYX",
				"XXX",
				'#', OreDictUtil.BLOCK_GLASS,
				'X', OreDictUtil.BEE_COMB,
				'Y', OreDictUtil.CHEST_WOOD);

		RecipeUtil.addRecipe(blocks.beeHouse,
				"XXX",
				"#C#",
				"###",
				'X', OreDictUtil.SLAB_WOOD,
				'#', OreDictUtil.PLANK_WOOD,
				'C', OreDictUtil.BEE_COMB);
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
		LootTableList.register(Constants.VILLAGE_NATURALIST_LOOT_KEY);
	}

	@Override
	public void addLootPoolNames(Set<String> lootPoolNames) {
		super.addLootPoolNames(lootPoolNames);
		lootPoolNames.add("forestry_apiculture_items");
		lootPoolNames.add("forestry_apiculture_bees");
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

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerApiculture();
	}

	@Override
	public void populateChunk(IChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		if (Config.getBeehivesAmount() > 0.0) {
			HiveDecorator.decorateHives(chunkGenerator, world, rand, chunkX, chunkZ, hasVillageGenerated);
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
		ParticleSnow.sprites = new TextureAtlasSprite[3];
		for (int i = 0; i < ParticleSnow.sprites.length; i++) {
			ParticleSnow.sprites[i] = event.getMap().registerSprite(new ResourceLocation("forestry:entity/particles/snow." + (i + 1)));
		}
		ProxyApicultureClient.beeSprite = event.getMap().registerSprite(new ResourceLocation("forestry:entity/particles/swarm_bee"));
	}

	private static class EndFlowerAcceptableRule implements IFlowerAcceptableRule {
		@Override
		public boolean isAcceptableFlower(IBlockState blockState, World world, BlockPos pos, String flowerType) {
			Biome biomeGenForCoords = world.getBiome(pos);
			return BiomeDictionary.isBiomeOfType(biomeGenForCoords, BiomeDictionary.Type.END);
		}
	}
}
