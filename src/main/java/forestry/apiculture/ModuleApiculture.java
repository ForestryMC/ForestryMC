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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.loot.LootTableList;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.Forestry;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveRegistry.HiveType;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IFlowerAcceptableRule;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.blocks.BlockCandle;
import forestry.apiculture.blocks.BlockHoneyComb;
import forestry.apiculture.blocks.BlockRegistryApiculture;
import forestry.apiculture.capabilities.ArmorApiarist;
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
import forestry.apiculture.genetics.alleles.AlleleEffects;
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
import forestry.apiculture.tiles.TileCandle;
import forestry.apiculture.tiles.TileHive;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveDescription;
import forestry.apiculture.worldgen.HiveGenHelper;
import forestry.apiculture.worldgen.HiveRegistry;
import forestry.core.ISaveEventHandler;
import forestry.core.ModuleCore;
import forestry.core.ModuleFluids;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.entities.ParticleSnow;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.items.ItemRegistryFluids;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.Log;
import forestry.core.utils.OreDictUtil;
import forestry.core.utils.VillagerTradeLists;
import forestry.food.ModuleFood;
import forestry.food.items.ItemRegistryFood;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.APICULTURE, name = "Apiculture", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.apiculture.description", lootTable = "apiculture")
public class ModuleApiculture extends BlankForestryModule {
	private static final String CONFIG_CATEGORY = "apiculture";
	private static float secondPrincessChance = 0;

	@SideOnly(Side.CLIENT)
	@Nullable
	private static TextureAtlasSprite beeSprite;
	@Nullable
	private static ItemRegistryApiculture items;
	@Nullable
	private static BlockRegistryApiculture blocks;
	@Nullable
	private static HiveRegistry hiveRegistry;

	public static String beekeepingMode = "NORMAL";

	public static int ticksPerBeeWorkCycle = 550;

	public static boolean hivesDamageOnPeaceful = false;

	public static boolean hivesDamageUnderwater = true;

	public static boolean hivesDamageOnlyPlayers = false;

	public static boolean hiveDamageOnAttack = true;

	public static boolean doSelfPollination = true;

	public static int maxFlowersSpawnedPerHive = 20;
	@Nullable
	public static VillagerRegistry.VillagerProfession villagerApiarist;

	public static ItemRegistryApiculture getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	public static BlockRegistryApiculture getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static HiveRegistry getHiveRegistry() {
		Preconditions.checkNotNull(hiveRegistry);
		return hiveRegistry;
	}

	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getBeeSprite() {
		Preconditions.checkNotNull(beeSprite, "Bee sprite has not been registered");
		return beeSprite;
	}

	@Override
	public void setupAPI() {

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

		// Capabilities
		CapabilityManager.INSTANCE.register(IArmorApiarist.class, new NullStorage<>(), () -> ArmorApiarist.INSTANCE);
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryApiculture();
		blocks = new BlockRegistryApiculture();
	}

	@Override
	public void preInit() {

		BeeDefinition.preInit();

		MinecraftForge.EVENT_BUS.register(this);

		if (Config.enableVillagers) {
			// Register village components with the Structure registry.
			VillageCreationApiculture.registerVillageComponents();
		}

		// Commands
		ModuleCore.rootCommand.addChildCommand(new CommandBee());

		if (ModuleHelper.isEnabled(ForestryModuleUids.SORTING)) {
			ApicultureFilterRuleType.init();
			ApicultureFilterRule.init();
		}
	}

	@Override
	public void registerTriggers() {
		ApicultureTriggers.initialize();
	}

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

		maxFlowersSpawnedPerHive = config.getIntLocalized("beekeeping", "flowers.spawn", 20, 0, 1000);

		String[] blacklist = config.getStringListLocalized("species", "blacklist", Constants.EMPTY_STRINGS);
		parseBeeBlacklist(blacklist);

		ticksPerBeeWorkCycle = config.getIntLocalized("beekeeping", "ticks.work", 550, 250, 850);

		hivesDamageOnPeaceful = config.getBooleanLocalized("beekeeping.hivedamage", "peaceful", hivesDamageOnPeaceful);

		hivesDamageUnderwater = config.getBooleanLocalized("beekeeping.hivedamage", "underwater", hivesDamageUnderwater);

		hivesDamageOnlyPlayers = config.getBooleanLocalized("beekeeping.hivedamage", "onlyPlayers", hivesDamageOnlyPlayers);

		hiveDamageOnAttack = config.getBooleanLocalized("beekeeping.hivedamage", "onlyAfterAttack", hiveDamageOnAttack);

		doSelfPollination = config.getBooleanLocalized("beekeeping", "self.pollination", false);

		config.save();

		// Genetics
		createAlleles();
		BeeDefinition.initBees();

		// Hives
		createHives();
		registerBeehiveDrops();

		ItemRegistryApiculture items = getItems();
		BlockRegistryApiculture blocks = getBlocks();

		// Inducers for swarmer
		BeeManager.inducers.put(items.royalJelly.getItemStack(), 10);

		TileUtil.registerTile(TileAlvearyPlain.class, "alveary_plain");
		TileUtil.registerTile(TileHive.class, "hive_wild");
		TileUtil.registerTile(TileAlvearySwarmer.class, "alveary_swarmer");
		TileUtil.registerTile(TileAlvearyHeater.class, "alveary_heater");
		TileUtil.registerTile(TileAlvearyFan.class, "alveary_fan");
		TileUtil.registerTile(TileAlvearyHygroregulator.class, "alveary_hygro");
		TileUtil.registerTile(TileAlvearyStabiliser.class, "alveary_stabiliser");
		TileUtil.registerTile(TileAlvearySieve.class, "alveary_sieve");
		TileUtil.registerTile(TileCandle.class, "candle");

		ResourceLocation beeHouseCartResource = new ResourceLocation(Constants.MOD_ID, "cart.beehouse");
		EntityUtil.registerEntity(beeHouseCartResource, EntityMinecartBeehouse.class, "cart.beehouse", 1, 0x000000, 0xffffff, 256, 3, true);
		ResourceLocation apiaryCartResource = new ResourceLocation(Constants.MOD_ID, "cart.apiary");
		EntityUtil.registerEntity(apiaryCartResource, EntityMinecartApiary.class, "cart.apiary", 2, 0x000000, 0xffffff, 256, 3, true);

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
			IForgeRegistry<VillagerRegistry.VillagerProfession> villagerProfessions = ForgeRegistries.VILLAGER_PROFESSIONS;
			villagerProfessions.register(villagerApiarist);

			ItemStack wildcardPrincess = new ItemStack(items.beePrincessGE, 1);
			ItemStack wildcardDrone = new ItemStack(items.beeDroneGE, 1);
			ItemStack apiary = new ItemStack(blocks.apiary);
			ItemStack provenFrames = items.frameProven.getItemStack();
			ItemStack monasticDrone = BeeDefinition.MONASTIC.getMemberStack(EnumBeeType.DRONE);
			ItemStack endDrone = BeeDefinition.ENDED.getMemberStack(EnumBeeType.DRONE);
			ItemStack propolis = new ItemStack(items.propolis, 1);

			VillagerRegistry.VillagerCareer apiaristCareer = new VillagerRegistry.VillagerCareer(villagerApiarist, "apiarist");
			apiaristCareer.addTrade(1,
				new VillagerApiaristTrades.GiveRandomCombsForItems(items.beeComb, new ItemStack(Items.WHEAT), new EntityVillager.PriceInfo(8, 12), new EntityVillager.PriceInfo(2, 4)),
				new VillagerApiaristTrades.GiveRandomCombsForItems(items.beeComb, new ItemStack(Items.CARROT), new EntityVillager.PriceInfo(8, 12), new EntityVillager.PriceInfo(2, 4)),
				new VillagerApiaristTrades.GiveRandomCombsForItems(items.beeComb, new ItemStack(Items.POTATO), new EntityVillager.PriceInfo(8, 12), new EntityVillager.PriceInfo(2, 4))
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
				new VillagerTradeLists.GiveItemForTwoItems(wildcardPrincess, null, new ItemStack(Items.ENDER_EYE), new EntityVillager.PriceInfo(12, 16), endDrone, null)
			);
		}

		blocks.apiary.init();
		blocks.beeHouse.init();
		blocks.beeChest.init();
	}

	@Override
	public void postInit() {
		registerDungeonLoot();
	}

	private void initFlowerRegistry() {
		FlowerRegistry flowerRegistry = (FlowerRegistry) FlowerManager.flowerRegistry;

		flowerRegistry.registerAcceptableFlowerRule(new EndFlowerAcceptableRule(), FlowerManager.FlowerTypeEnd);

		// Register acceptable plants
		flowerRegistry.registerAcceptableFlower(Blocks.DRAGON_EGG, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.CHORUS_PLANT, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.CHORUS_FLOWER, FlowerManager.FlowerTypeEnd);
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

		//Flower Pots
		IBlockState flowerPot = Blocks.FLOWER_POT.getBlockState().getBaseState();
		PropertyEnum<BlockFlowerPot.EnumFlowerType> CONTENTS = BlockFlowerPot.CONTENTS;
		String[] standardTypes = new String[]{FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow};

		for (BlockFlowerPot.EnumFlowerType flowerType : BlockFlowerPot.EnumFlowerType.values()) {
			if (flowerType == BlockFlowerPot.EnumFlowerType.EMPTY ||
				flowerType.getName().contains("sapling") ||
				flowerType == BlockFlowerPot.EnumFlowerType.DEAD_BUSH ||
				flowerType == BlockFlowerPot.EnumFlowerType.FERN) {
				//Don't register these as flowers
			} else if (flowerType == BlockFlowerPot.EnumFlowerType.MUSHROOM_RED ||
				flowerType == BlockFlowerPot.EnumFlowerType.MUSHROOM_BROWN) {
				flowerRegistry.registerAcceptableFlower(flowerPot.withProperty(CONTENTS, flowerType), FlowerManager.FlowerTypeMushrooms);

			} else if (flowerType == BlockFlowerPot.EnumFlowerType.CACTUS) {
				flowerRegistry.registerAcceptableFlower(flowerPot.withProperty(CONTENTS, flowerType), FlowerManager.FlowerTypeCacti);
			} else {
				flowerRegistry.registerAcceptableFlower(flowerPot.withProperty(CONTENTS, flowerType), standardTypes);
			}
		}
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryApiculture();
	}

	@Override
	public void registerCrates() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		ItemRegistryApiculture items = getItems();

		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		crateRegistry.registerCrate(coreItems.beeswax.getItemStack());
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

		crateRegistry.registerCrate(coreItems.refractoryWax.getItemStack());
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		ItemRegistryFluids fluidItems = ModuleFluids.getItems();
		ItemRegistryApiculture items = getItems();
		BlockRegistryApiculture blocks = getBlocks();

		// / APIARIST'S ARMOR

		ItemStack wovenSilk = coreItems.craftingMaterial.getWovenSilk();
		RecipeUtil.addRecipe("apiarist_hat", items.apiaristHat,
			"###", "# #",
			'#', wovenSilk);
		RecipeUtil.addRecipe("apiarist_chest", items.apiaristChest,
			"# #", "###", "###",
			'#', wovenSilk);
		RecipeUtil.addRecipe("apiarist_legs", items.apiaristLegs,
			"###", "# #", "# #",
			'#', wovenSilk);
		RecipeUtil.addRecipe("apiarist_boots", items.apiaristBoots,
			"# #", "# #",
			'#', wovenSilk);

		// / HABITAT LOCATOR
		RecipeUtil.addRecipe("habitat_locator", items.habitatLocator,
			" X ",
			"X#X",
			" X ",
			'#', OreDictUtil.DUST_REDSTONE, 'X', OreDictUtil.INGOT_BRONZE);

		// Bees
		RecipeUtil.addRecipe("scoop", items.scoop,
			"#X#", "###", " # ",
			'#', OreDictUtil.STICK_WOOD,
			'X', OreDictUtil.BLOCK_WOOL);
		RecipeUtil.addRecipe("smoker", items.smoker,
			"LS#",
			"LF#",
			"###",
			'#', "ingotTin", 'S', OreDictUtil.STICK_WOOD, 'F', Items.FLINT_AND_STEEL, 'L', OreDictUtil.LEATHER);
		RecipeUtil.addRecipe("propolis_to_slime", new ItemStack(Items.SLIME_BALL),
			"#X#", "#X#", "#X#",
			'#', items.propolis,
			'X', items.pollenCluster.get(EnumPollenCluster.NORMAL, 1));
		RecipeUtil.addRecipe("honey_melon", new ItemStack(Items.SPECKLED_MELON),
			"#X#", "#Y#", "#X#",
			'#', items.honeyDrop,
			'X', items.honeydew,
			'Y', Items.MELON);
		RecipeUtil.addRecipe("frame_untreated", items.frameUntreated,
			"###", "#S#", "###",
			'#', OreDictUtil.STICK_WOOD,
			'S', Items.STRING);
		RecipeUtil.addRecipe("frame_impregnated", items.frameImpregnated,
			"###", "#S#", "###",
			'#', coreItems.stickImpregnated,
			'S', Items.STRING);
		RecipeUtil.addRecipe("bee_house_minecart", items.minecartBeehouse.getBeeHouseMinecart(),
			"B",
			"C",
			'B', new ItemStack(blocks.beeHouse),
			'C', Items.MINECART);
		RecipeUtil.addRecipe("apiary_minecart", items.minecartBeehouse.getApiaryMinecart(),
			"B",
			"C",
			'B', new ItemStack(blocks.apiary),
			'C', Items.MINECART);
		for (int i = 0; i < EnumHoneyComb.VALUES.length; i++) {
			int remainder = i & 15;
			int quotient = i >> 4;
			BlockHoneyComb block = blocks.beeCombs[quotient];
			RecipeUtil.addRecipe("comb." + i, new ItemStack(block, 1, remainder),
				"###",
				"###",
				"###", '#', items.beeComb.get(EnumHoneyComb.get(i), 1));

		}

		// FOOD STUFF
		if (ModuleHelper.isEnabled(ForestryModuleUids.FOOD)) {
			ItemRegistryFood foodItems = ModuleFood.getItems();
			RecipeUtil.addRecipe("honeyed_slice", new ItemStack(foodItems.honeyedSlice, 4),
				"###", "#X#", "###",
				'#', items.honeyDrop,
				'X', Items.BREAD);

			RecipeUtil.addRecipe("honey_pot", foodItems.honeyPot,
				"# #", " X ", "# #",
				'#', items.honeyDrop,
				'X', fluidItems.waxCapsuleEmpty);

			RecipeUtil.addRecipe("ambrosia", foodItems.ambrosia,
				"#Y#", "XXX", "###",
				'#', items.honeydew,
				'X', items.royalJelly,
				'Y', fluidItems.waxCapsuleEmpty);

		}

		// / BITUMINOUS PEAT
		RecipeUtil.addRecipe("bituminous_peat", coreItems.bituminousPeat.getItemStack(),
			" # ", "XYX", " # ",
			'#', OreDictUtil.DUST_ASH,
			'X', coreItems.peat,
			'Y', items.propolis);

		// / TORCHES
		RecipeUtil.addRecipe("beeswax_worth", new ItemStack(Blocks.TORCH, 3),
			" # ", " # ", " Y ",
			'#', coreItems.beeswax,
			'Y', OreDictUtil.STICK_WOOD);
		RecipeUtil.addRecipe("pulsating_mesh", coreItems.craftingMaterial.getPulsatingMesh(),
			"# #", " # ", "# #",
			'#', items.propolis.get(EnumPropolis.PULSATING, 1));

		// / WAX CAST
		RecipeUtil.addRecipe("wax_cast", items.waxCast,
			"###",
			"# #",
			"###",
			'#', coreItems.beeswax);

		// / ALVEARY
		ItemStack alvearyPlainBlock = blocks.getAlvearyBlockStack(BlockAlvearyType.PLAIN);
		RecipeUtil.addRecipe("alveary_plain", alvearyPlainBlock,
			"###",
			"#X#",
			"###",
			'X', coreItems.impregnatedCasing,
			'#', coreItems.craftingMaterial.getScentedPaneling());
		// SWARMER
		RecipeUtil.addRecipe("alveary_swarmer", blocks.getAlvearyBlockStack(BlockAlvearyType.SWARMER),
			"#G#",
			" X ",
			"#G#",
			'#', coreItems.tubes.get(EnumElectronTube.DIAMOND, 1),
			'X', alvearyPlainBlock,
			'G', OreDictUtil.INGOT_GOLD);
		// FAN
		RecipeUtil.addRecipe("alveary_fan", blocks.getAlvearyBlockStack(BlockAlvearyType.FAN),
			"I I",
			" X ",
			"I#I",
			'#', coreItems.tubes.get(EnumElectronTube.GOLD, 1),
			'X', alvearyPlainBlock,
			'I', OreDictUtil.INGOT_IRON);
		// HEATER
		RecipeUtil.addRecipe("alveary_heater", blocks.getAlvearyBlockStack(BlockAlvearyType.HEATER),
			"#I#",
			" X ",
			"YYY",
			'#', coreItems.tubes.get(EnumElectronTube.GOLD, 1),
			'X', alvearyPlainBlock,
			'I', OreDictUtil.INGOT_IRON, 'Y', OreDictUtil.STONE);
		// HYGROREGULATOR
		RecipeUtil.addRecipe("alveary_hygro", blocks.getAlvearyBlockStack(BlockAlvearyType.HYGRO),
			"GIG",
			"GXG",
			"GIG",
			'X', alvearyPlainBlock,
			'I', OreDictUtil.INGOT_IRON,
			'G', OreDictUtil.BLOCK_GLASS);
		// STABILISER
		RecipeUtil.addRecipe("alveary_stabiliser", blocks.getAlvearyBlockStack(BlockAlvearyType.STABILISER),
			"G G",
			"GXG",
			"G G",
			'X', alvearyPlainBlock,
			'G', OreDictUtil.GEM_QUARTZ);
		// SIEVE
		RecipeUtil.addRecipe("alveary_sieve", blocks.getAlvearyBlockStack(BlockAlvearyType.SIEVE),
			"III",
			" X ",
			"WWW",
			'X', alvearyPlainBlock,
			'I', OreDictUtil.INGOT_IRON,
			'W', coreItems.craftingMaterial.getWovenSilk());

		if (ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
			// / SQUEEZER
			FluidStack honeyDropFluid = Fluids.FOR_HONEY.getFluid(Constants.FLUID_PER_HONEY_DROP);
			if (honeyDropFluid != null) {
				RecipeManagers.squeezerManager.addRecipe(10, items.honeyDrop.getItemStack(), honeyDropFluid, items.propolis.getItemStack(), 5);
				RecipeManagers.squeezerManager.addRecipe(10, items.honeydew.getItemStack(), honeyDropFluid);
			}

			ItemStack phosphor = coreItems.phosphor.getItemStack(2);
			NonNullList<ItemStack> lavaIngredients = NonNullList.create();
			lavaIngredients.add(phosphor);
			lavaIngredients.add(new ItemStack(Blocks.SAND));
			RecipeManagers.squeezerManager.addRecipe(10, lavaIngredients, new FluidStack(FluidRegistry.LAVA, 2000));

			lavaIngredients = NonNullList.create();
			lavaIngredients.add(phosphor);
			lavaIngredients.add(new ItemStack(Blocks.SAND, 1, 1));
			RecipeManagers.squeezerManager.addRecipe(10, lavaIngredients, new FluidStack(FluidRegistry.LAVA, 2000));

			lavaIngredients = NonNullList.create();
			lavaIngredients.add(phosphor);
			lavaIngredients.add(new ItemStack(Blocks.DIRT));
			RecipeManagers.squeezerManager.addRecipe(10, lavaIngredients, new FluidStack(FluidRegistry.LAVA, 1600));

			// / CARPENTER
			RecipeManagers.carpenterManager.addRecipe(50, Fluids.FOR_HONEY.getFluid(500), ItemStack.EMPTY, coreItems.craftingMaterial.getScentedPaneling(),
				" J ", "###", "WPW",
				'#', OreDictUtil.PLANK_WOOD,
				'J', items.royalJelly,
				'W', coreItems.beeswax,
				'P', items.pollenCluster.get(EnumPollenCluster.NORMAL, 1));

			RecipeManagers.carpenterManager.addRecipe(30, new FluidStack(FluidRegistry.WATER, 600), ItemStack.EMPTY, blocks.candle.getUnlitCandle(24),
				" X ",
				"###",
				"###",
				'#', coreItems.beeswax,
				'X', Items.STRING);
			RecipeManagers.carpenterManager.addRecipe(10, new FluidStack(FluidRegistry.WATER, 200), ItemStack.EMPTY, blocks.candle.getUnlitCandle(6),
				"#X#",
				'#', coreItems.beeswax,
				'X', coreItems.craftingMaterial.getSilkWisp());
			RecipeUtil.addShapelessRecipe("candle_unlit_reset", blocks.candle.getUnlitCandle(1), blocks.candle.getUnlitCandle(1));
			RecipeUtil.addShapelessRecipe("candle_lit_reset", blocks.candle.getLitCandle(1), blocks.candle.getLitCandle(1));

			// / CENTRIFUGE
			// Honey combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.HONEY, 1), ImmutableMap.of(
				coreItems.beeswax.getItemStack(), 1.0f,
				items.honeyDrop.getItemStack(), 0.9f
			));

			// Cocoa combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.COCOA, 1), ImmutableMap.of(
				coreItems.beeswax.getItemStack(), 1.0f,
				new ItemStack(Items.DYE, 1, 3), 0.5f
			));

			// Simmering combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.SIMMERING, 1), ImmutableMap.of(
				coreItems.refractoryWax.getItemStack(), 1.0f,
				coreItems.phosphor.getItemStack(2), 0.7f
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
				coreItems.beeswax.getItemStack(), 0.8f,
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
				coreItems.beeswax.getItemStack(), 1.0f,
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
				coreItems.beeswax.getItemStack(), 0.2f,
				new ItemStack(Items.GUNPOWDER), 0.9f
			));

			// Wheaten Combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.WHEATEN, 1), ImmutableMap.of(
				items.honeyDrop.getItemStack(), 0.2f,
				coreItems.beeswax.getItemStack(), 0.2f,
				new ItemStack(Items.WHEAT), 0.8f
			));

			// Mossy Combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.MOSSY, 1), ImmutableMap.of(
				coreItems.beeswax.getItemStack(), 1.0f,
				items.honeyDrop.getItemStack(), 0.9f
			));

			// Mellow Combs
			RecipeManagers.centrifugeManager.addRecipe(20, items.beeComb.get(EnumHoneyComb.MELLOW, 1), ImmutableMap.of(
				items.honeydew.getItemStack(), 0.6f,
				coreItems.beeswax.getItemStack(), 0.2f,
				new ItemStack(Items.QUARTZ), 0.3f
			));

			// Silky Propolis
			RecipeManagers.centrifugeManager.addRecipe(5, items.propolis.get(EnumPropolis.SILKY, 1), ImmutableMap.of(
				coreItems.craftingMaterial.getSilkWisp(), 0.6f,
				items.propolis.getItemStack(), 0.1f
			));

			// / FERMENTER
			FluidStack shortMead = Fluids.SHORT_MEAD.getFluid(1);
			FluidStack honey = Fluids.FOR_HONEY.getFluid(1);
			if (shortMead != null && honey != null) {
				RecipeManagers.fermenterManager.addRecipe(items.honeydew.getItemStack(), 500, 1.0f, shortMead, honey);
			}
		}

		RecipeUtil.addRecipe("apiary", blocks.apiary,
			"XXX",
			"#C#",
			"###",
			'X', OreDictUtil.SLAB_WOOD,
			'#', OreDictUtil.PLANK_WOOD,
			'C', coreItems.impregnatedCasing);

		RecipeUtil.addRecipe("bee_chest", blocks.beeChest,
			" # ",
			"XYX",
			"XXX",
			'#', OreDictUtil.BLOCK_GLASS,
			'X', OreDictUtil.BEE_COMB,
			'Y', OreDictUtil.CHEST_WOOD);

		RecipeUtil.addRecipe("bee_house", blocks.beeHouse,
			"XXX",
			"#C#",
			"###",
			'X', OreDictUtil.SLAB_WOOD,
			'#', OreDictUtil.PLANK_WOOD,
			'C', OreDictUtil.BEE_COMB);

		// BREWING RECIPES
		BrewingRecipeRegistry.addRecipe(
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.AWKWARD),
			items.pollenCluster.get(EnumPollenCluster.NORMAL, 1),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.HEALING));
		BrewingRecipeRegistry.addRecipe(
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.AWKWARD),
			items.pollenCluster.get(EnumPollenCluster.CRYSTALLINE, 1),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.REGENERATION));

	}

	private static void registerBeehiveDrops() {
		ItemRegistryApiculture items = getItems();
		ItemStack honeyComb = items.beeComb.get(EnumHoneyComb.HONEY, 1);
		HiveRegistry hiveRegistry = getHiveRegistry();

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
		lootPoolNames.add("forestry_apiculture_items");
		lootPoolNames.add("forestry_apiculture_bees");
	}

	private static void createHives() {
		HiveRegistry hiveRegistry = getHiveRegistry();
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

		AlleleEffects.registerAlleles();
	}

	public static double getSecondPrincessChance() {
		return secondPrincessChance;
	}

	private static void parseBeeBlacklist(String[] items) {
		for (String item : items) {
			if (item.isEmpty()) {
				continue;
			}

			Log.debug("Blacklisting bee species identified by " + item);
			AlleleManager.alleleRegistry.blacklistAllele(item);
		}
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerApiculture();
	}

	@Override
	public void populateChunk(IChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ,
		boolean hasVillageGenerated) {
		if (!world.provider.getDimensionType().equals(DimensionType.THE_END)) {
			return;
		}
		if (Config.getBeehivesAmount() > 0.0) {
			HiveDecorator.decorateHives(world, rand, chunkX, chunkZ);
		}
	}

	@Override
	public void decorateBiome(World world, Random rand, BlockPos pos) {
		if (Config.getBeehivesAmount() > 0.0) {
			int chunkX = pos.getX() >> 4;
			int chunkZ = pos.getZ() >> 4;
			HiveDecorator.decorateHives(world, rand, chunkX, chunkZ);
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
		} else if (message.key.equals("blacklist-hives-dimension")) {
			int[] dims = message.getNBTValue().getIntArray("dimensions");
			for (int dim : dims) {
				HiveConfig.addBlacklistedDim(dim);
			}
			return true;
		} else if (message.key.equals("add-plantable-flower")) {
			return addPlantableFlower(message);
		} else if (message.key.equals("add-acceptable-flower")) {
			return addAcceptableFlower(message);
		}

		return false;
	}

	private boolean addPlantableFlower(IMCMessage message) {
		try {
			NBTTagCompound tagCompound = message.getNBTValue();
			IBlockState flowerState = NBTUtil.readBlockState(tagCompound);
			double weight = tagCompound.getDouble("weight");
			List<String> flowerTypes = new ArrayList<>();
			for (String key : tagCompound.getKeySet()) {
				if (key.contains("flowertype")) {
					flowerTypes.add(tagCompound.getString("flowertype"));
				}
			}
			FlowerManager.flowerRegistry.registerPlantableFlower(flowerState, weight, flowerTypes.toArray(new String[flowerTypes.size()]));
			return true;
		} catch (Exception e) {
			IMCUtil.logInvalidIMCMessage(message);
			return false;
		}
	}

	private boolean addAcceptableFlower(IMCMessage message) {
		try {
			NBTTagCompound tagCompound = message.getNBTValue();
			IBlockState flowerState = NBTUtil.readBlockState(tagCompound);
			List<String> flowerTypes = new ArrayList<>();
			for (String key : tagCompound.getKeySet()) {
				if (key.contains("flowertype")) {
					flowerTypes.add(tagCompound.getString("flowertype"));
				}
			}
			FlowerManager.flowerRegistry.registerAcceptableFlower(flowerState, flowerTypes.toArray(new String[flowerTypes.size()]));
			return true;
		} catch (Exception e) {
			IMCUtil.logInvalidIMCMessage(message);
			return false;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		for (int i = 0; i < ParticleSnow.sprites.length; i++) {
			ParticleSnow.sprites[i] = event.getMap().registerSprite(new ResourceLocation("forestry:entity/particles/snow." + (i + 1)));
		}
		beeSprite = event.getMap().registerSprite(new ResourceLocation("forestry:entity/particles/swarm_bee"));
	}

	private static class EndFlowerAcceptableRule implements IFlowerAcceptableRule {
		@Override
		public boolean isAcceptableFlower(IBlockState blockState, World world, BlockPos pos, String flowerType) {
			Biome biomeGenForCoords = world.getBiome(pos);
			return BiomeDictionary.hasType(biomeGenForCoords, BiomeDictionary.Type.END);
		}
	}
}
