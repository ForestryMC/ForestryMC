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

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.Forestry;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveRegistry.HiveType;
import forestry.api.genetics.flowers.IFlowerAcceptableRule;
import forestry.api.modules.ForestryModule;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.apiculture.capabilities.ArmorApiarist;
import forestry.apiculture.commands.CommandBee;
import forestry.apiculture.features.ApicultureContainers;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.flowers.FlowerRegistry;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.BeeFactory;
import forestry.apiculture.genetics.BeeMutationFactory;
import forestry.apiculture.genetics.HiveDrop;
import forestry.apiculture.genetics.JubilanceFactory;
import forestry.apiculture.gui.ContainerBeeHousing;
import forestry.apiculture.gui.ContainerMinecartBeehouse;
import forestry.apiculture.gui.GuiAlveary;
import forestry.apiculture.gui.GuiAlvearyHygroregulator;
import forestry.apiculture.gui.GuiAlvearySieve;
import forestry.apiculture.gui.GuiAlvearySwarmer;
import forestry.apiculture.gui.GuiBeeHousing;
import forestry.apiculture.gui.GuiHabitatLocator;
import forestry.apiculture.gui.GuiImprinter;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.network.PacketRegistryApiculture;
import forestry.apiculture.proxy.ProxyApiculture;
import forestry.apiculture.proxy.ProxyApicultureClient;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.apiculture.villagers.RegisterVillager;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveDescription;
import forestry.apiculture.worldgen.HiveGenHelper;
import forestry.apiculture.worldgen.HiveRegistry;
import forestry.core.ISaveEventHandler;
import forestry.core.ModuleCore;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.features.CoreItems;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.ForgeUtils;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.Log;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
import forestry.modules.ModuleHelper;

import genetics.api.GeneticsAPI;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.APICULTURE, name = "Apiculture", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.apiculture.description", lootTable = "apiculture")
public class ModuleApiculture extends BlankForestryModule {
	private static final String CONFIG_CATEGORY = "apiculture";
	private static float secondPrincessChance = 0;

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
	public static VillagerProfession villagerApiarist;

	public static ProxyApiculture proxy;

	public static HiveRegistry getHiveRegistry() {
		Preconditions.checkNotNull(hiveRegistry);
		return hiveRegistry;
	}

	public ModuleApiculture() {
		proxy = DistExecutor.safeRunForDist(() -> ProxyApicultureClient::new, () -> ProxyApiculture::new);
		ForgeUtils.registerSubscriber(this);

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		if (Config.enableVillagers) {
			RegisterVillager.Registers.POINTS_OF_INTEREST.register(modEventBus);
			RegisterVillager.Registers.PROFESSIONS.register(modEventBus);
			MinecraftForge.EVENT_BUS.register(new RegisterVillager.Events());
		}
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
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerGuiFactories() {
		ScreenManager.registerFactory(ApicultureContainers.ALVEARY.containerType(), GuiAlveary::new);
		ScreenManager.registerFactory(ApicultureContainers.ALVEARY_HYGROREGULATOR.containerType(), GuiAlvearyHygroregulator::new);
		ScreenManager.registerFactory(ApicultureContainers.ALVEARY_SIEVE.containerType(), GuiAlvearySieve::new);
		ScreenManager.registerFactory(ApicultureContainers.ALVEARY_SWARMER.containerType(), GuiAlvearySwarmer::new);
		ScreenManager.registerFactory(ApicultureContainers.BEE_HOUSING.containerType(), GuiBeeHousing<ContainerBeeHousing>::new);
		ScreenManager.registerFactory(ApicultureContainers.HABITAT_LOCATOR.containerType(), GuiHabitatLocator::new);
		ScreenManager.registerFactory(ApicultureContainers.IMPRINTER.containerType(), GuiImprinter::new);
		ScreenManager.registerFactory(ApicultureContainers.BEEHOUSE_MINECART.containerType(), GuiBeeHousing<ContainerMinecartBeehouse>::new);
	}

	@Override
	public void preInit() {
		// Capabilities
		CapabilityManager.INSTANCE.register(IArmorApiarist.class, new NullStorage<>(), () -> ArmorApiarist.INSTANCE);

		MinecraftForge.EVENT_BUS.register(this);

		if (Config.enableVillagers) {
			// Register village components with the Structure registry.
			//			VillageCreationApiculture.registerVillageComponents();
			//TODO villages
		}

		// Commands
		//TODO - commands
				ModuleCore.rootCommand.then(CommandBee.register());

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
		BeeDefinition.initBees();

		// Hives
		createHives();
		registerBeehiveDrops();

		// Inducers for swarmer
		BeeManager.inducers.put(ApicultureItems.ROYAL_JELLY.stack(), 10);

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
			VillageCreationApiculture villageHandler = new VillageCreationApiculture();
			VillagerRegistry villagerRegistry = VillagerRegistry.instance();
			villagerRegistry.registerVillageCreationHandler(villageHandler);

			villagerApiarist = new VillagerProfession(Constants.ID_VILLAGER_APIARIST, Constants.TEXTURE_SKIN_BEEKPEEPER, Constants.TEXTURE_SKIN_ZOMBIE_BEEKPEEPER);
			IForgeRegistry<VillagerProfession> villagerProfessions = ForgeRegistries.PROFESSIONS;
			villagerProfessions.register(villagerApiarist);

			ItemStack wildcardPrincess = ApicultureItems.BEE_PRINCESS.stack();
			ItemStack wildcardDrone = ApicultureItems.BEE_DRONE.stack();
			ItemStack apiary = ApicultureBlocks.BASE.stack(BlockTypeApiculture.APIARY);
			ItemStack provenFrames = ApicultureItems.FRAME_PROVEN.stack();
			ItemStack monasticDrone = BeeDefinition.MONASTIC.getMemberStack(EnumBeeType.DRONE);
			ItemStack endDrone = BeeDefinition.ENDED.getMemberStack(EnumBeeType.DRONE);
			ItemStack propolis = ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL);

			VillagerRegistry.VillagerCareer apiaristCareer = new VillagerRegistry.VillagerCareer(villagerApiarist, "apiarist");
			VillagerTrades.VILLAGER_DEFAULT_TRADES.put(villagerApiarist, new Int2ObjectOpenHashMap<>(
				ImmutableMap.of(
					1,
					new VillagerTrades.ITrade[]{
						new VillagerApiaristTrades.GiveRandomCombsForItems(3, new ItemStack(Items.WHEAT, 10), 16, 2),
						new VillagerApiaristTrades.GiveRandomCombsForItems(3, new ItemStack(Items.CARROT, 10), 16, 2),
						new VillagerApiaristTrades.GiveRandomCombsForItems(3, new ItemStack(Items.POTATO, 10), 16, 2)
					},
					2,
					new VillagerTrades.ITrade[]{
						new VillagerTrades.GiveItemForEmeralds(new VillagerEntity.PriceInfo(1, 4), new ItemStack(items.smoker), null),
						new VillagerTrades.GiveItemForLogsAndEmeralds(apiary, new VillagerEntity.PriceInfo(1, 1), new VillagerEntity.PriceInfo(16, 32), new VillagerEntity.PriceInfo(1, 2)),
						new VillagerApiaristTrades.GiveRandomHiveDroneForItems(propolis, null, wildcardDrone, new VillagerEntity.PriceInfo(2, 4))
					},
					3,
					new VillagerTrades.ITrade[]{
						new VillagerTrades.GiveEmeraldForItems(wildcardPrincess, null),
						new VillagerTrades.GiveItemForEmeralds(new VillagerEntity.PriceInfo(1, 2), provenFrames, new VillagerEntity.PriceInfo(1, 6))
					},
					4,
					new VillagerTrades.ITrade[]{
						new VillagerTrades.GiveItemForItemAndEmerald(wildcardPrincess, null, new VillagerEntity.PriceInfo(10, 64), monasticDrone, null),
						new VillagerTrades.GiveItemForTwoItems(wildcardPrincess, null, new ItemStack(Items.ENDER_EYE), new VillagerEntity.PriceInfo(12, 16), endDrone, null)
					}
				)
			));
		}*/
	}

	@Override
	public void postInit() {
		//TODO loottable
		//		registerDungeonLoot();
	}

	private void initFlowerRegistry() {
		FlowerRegistry flowerRegistry = (FlowerRegistry) FlowerManager.flowerRegistry;

		flowerRegistry.registerAcceptableFlowerRule(new EndFlowerAcceptableRule(), FlowerManager.FlowerTypeEnd);

		// Register acceptable plants
		flowerRegistry.registerAcceptableFlower(Blocks.DRAGON_EGG, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.CHORUS_PLANT, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.CHORUS_FLOWER, FlowerManager.FlowerTypeEnd);
		flowerRegistry.registerAcceptableFlower(Blocks.VINE, FlowerManager.FlowerTypeJungle);
		flowerRegistry.registerAcceptableFlower(Blocks.FERN, FlowerManager.FlowerTypeJungle);
		flowerRegistry.registerAcceptableFlower(Blocks.WHEAT, FlowerManager.FlowerTypeWheat);
		flowerRegistry.registerAcceptableFlower(Blocks.PUMPKIN_STEM, FlowerManager.FlowerTypeGourd);
		flowerRegistry.registerAcceptableFlower(Blocks.MELON_STEM, FlowerManager.FlowerTypeGourd);
		flowerRegistry.registerAcceptableFlower(Blocks.NETHER_WART, FlowerManager.FlowerTypeNether);
		flowerRegistry.registerAcceptableFlower(Blocks.CACTUS, FlowerManager.FlowerTypeCacti);

		Block[] standardFlowers = new Block[]{
				Blocks.DANDELION,
				Blocks.POPPY,
				Blocks.BLUE_ORCHID,
			Blocks.ALLIUM,
			Blocks.AZURE_BLUET,
			Blocks.RED_TULIP,
			Blocks.ORANGE_TULIP,
			Blocks.WHITE_TULIP,
			Blocks.PINK_TULIP,
			Blocks.OXEYE_DAISY,
			Blocks.CORNFLOWER,
			Blocks.WITHER_ROSE,
			Blocks.LILY_OF_THE_VALLEY,
		};
		Block[] pottedStandardFlowers = new Block[]{
			Blocks.POTTED_POPPY,
			Blocks.POTTED_BLUE_ORCHID,
			Blocks.POTTED_ALLIUM,
			Blocks.POTTED_AZURE_BLUET,
			Blocks.POTTED_RED_TULIP,
			Blocks.POTTED_ORANGE_TULIP,
			Blocks.POTTED_WHITE_TULIP,
			Blocks.POTTED_PINK_TULIP,
			Blocks.POTTED_OXEYE_DAISY,
			Blocks.POTTED_CORNFLOWER,
			Blocks.POTTED_LILY_OF_THE_VALLEY,
			Blocks.POTTED_WITHER_ROSE,
		};

		// Register plantable plants
		String[] standardTypes = new String[]{FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow};
		for (Block standardFlower : standardFlowers) {
			flowerRegistry.registerPlantableFlower(standardFlower.getDefaultState(), 1.0, standardTypes);
		}
		flowerRegistry.registerPlantableFlower(Blocks.BROWN_MUSHROOM.getDefaultState(), 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.RED_MUSHROOM.getDefaultState(), 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.CACTUS.getDefaultState(), 1.0, FlowerManager.FlowerTypeCacti);

		//Flower Pots
		for (Block standardFlower : pottedStandardFlowers) {
			flowerRegistry.registerAcceptableFlower(standardFlower, standardTypes);
		}

		flowerRegistry.registerAcceptableFlower(Blocks.POTTED_RED_MUSHROOM, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerAcceptableFlower(Blocks.POTTED_BROWN_MUSHROOM, FlowerManager.FlowerTypeMushrooms);

		flowerRegistry.registerAcceptableFlower(Blocks.POTTED_CACTUS, FlowerManager.FlowerTypeCacti);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryApiculture();
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		crateRegistry.registerCrate(CoreItems.BEESWAX.stack());
		crateRegistry.registerCrate(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.NORMAL));
		crateRegistry.registerCrate(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.CRYSTALLINE));
		crateRegistry.registerCrate(ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL));
		crateRegistry.registerCrate(ApicultureItems.HONEYDEW.stack());
		crateRegistry.registerCrate(ApicultureItems.ROYAL_JELLY.stack());

		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.HONEY, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.COCOA, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.SIMMERING, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.STRINGY, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.FROZEN, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.DRIPPING, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.SILKY, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.PARCHED, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MYSTERIOUS, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.POWDERY, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.WHEATEN, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MOSSY, 1));
		crateRegistry.registerCrate(ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MELLOW, 1));

		crateRegistry.registerCrate(CoreItems.REFRACTORY_WAX.stack());
	}

	@Override
	public void registerRecipes() {
		// BREWING RECIPES
		BrewingRecipeRegistry.addRecipe(
			Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.AWKWARD)),
			Ingredient.fromStacks(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.NORMAL, 1)),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.HEALING));
		BrewingRecipeRegistry.addRecipe(
			Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.AWKWARD)),
			Ingredient.fromStacks(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.CRYSTALLINE, 1)),
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.REGENERATION));

	}

	private static void registerBeehiveDrops() {
		ItemStack honeyComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.HONEY, 1);
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

		ItemStack parchedComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.PARCHED, 1);
		hiveRegistry.addDrops(HiveType.DESERT.getHiveUid(),
			new HiveDrop(0.80, BeeDefinition.MODEST, parchedComb).setIgnobleShare(0.7),
			new HiveDrop(0.03, BeeDefinition.VALIANT, parchedComb)
		);

		ItemStack silkyComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.SILKY, 1);
		hiveRegistry.addDrops(HiveType.JUNGLE.getHiveUid(),
			new HiveDrop(0.80, BeeDefinition.TROPICAL, silkyComb).setIgnobleShare(0.7),
			new HiveDrop(0.03, BeeDefinition.VALIANT, silkyComb)
		);

		ItemStack mysteriousComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MYSTERIOUS, 1);
		hiveRegistry.addDrops(HiveType.END.getHiveUid(),
			new HiveDrop(0.90, BeeDefinition.ENDED, mysteriousComb)
		);

		ItemStack frozenComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.FROZEN, 1);
		hiveRegistry.addDrops(HiveType.SNOW.getHiveUid(),
			new HiveDrop(0.80, BeeDefinition.WINTRY, frozenComb).setIgnobleShare(0.5),
			new HiveDrop(0.03, BeeDefinition.VALIANT, frozenComb)
		);

		ItemStack mossyComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MOSSY, 1);
		hiveRegistry.addDrops(HiveType.SWAMP.getHiveUid(),
			new HiveDrop(0.80, BeeDefinition.MARSHY, mossyComb).setIgnobleShare(0.4),
			new HiveDrop(0.03, BeeDefinition.VALIANT, mossyComb)
		);
	}

	//TODO - just done by datapacks now?
	//	private static void registerDungeonLoot() {
	//		LootTables.register(Constants.VILLAGE_NATURALIST_LOOT_KEY);
	//	}

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

	public static double getSecondPrincessChance() {
		return secondPrincessChance;
	}

	private static void parseBeeBlacklist(String[] items) {
		for (String item : items) {
			if (item.isEmpty()) {
				continue;
			}

			Log.debug("Blacklisting bee species identified by " + item);
			GeneticsAPI.apiInstance.getAlleleRegistry().blacklistAllele(new ResourceLocation(item));
		}
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerApiculture();
	}

	@Override
	public void populateChunk(ChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ,
							  boolean hasVillageGenerated) {
		//TODO: Fix if dimensions are more integrated into forge
		/*if (!world.func_230315_m_() != DimensionType..getType().equals(DimensionType.OVERWORLD)) {
			return;
		}*/
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
	public boolean processIMCMessage(InterModComms.IMCMessage message) {
		//		if (message.getMethod().equals("add-candle-lighting-id")) {
		//			ItemStack value = message.getItemStackValue();
		//			if (value != null) {
		//				BlockCandle.addItemToLightingList(value.getItem());
		//			} else {
		//				IMCUtil.logInvalidIMCMessage(message);
		//			}
		//			return true;
		//		} else if (message.getMethod().equals("add-alveary-slab") && message.isStringMessage()) {
		//			String messageString = String.format("Received a '%s' request from mod '%s'. This IMC message has been replaced with the oreDictionary for 'slabWood'. Please contact the author and report this issue.", message.key, message.getSender());
		//			Log.warning(messageString);
		//			return true;
		//		} else if (message.getMethod().equals("blacklist-hives-dimension")) {
		//			int[] dims = message.getNBTValue().getIntArray("dimensions");
		//			for (int dim : dims) {
		//				HiveConfig.addBlacklistedDim(dim);
		//			}
		//			return true;
		//		} else if (message.getMethod().equals("add-plantable-flower")) {
		//			return addPlantableFlower(message);
		//		} else if (message.getMethod().equals("add-acceptable-flower")) {
		//			return addAcceptableFlower(message);
		//		}
		//TODO new imc
		return false;
	}

	private boolean addPlantableFlower(InterModComms.IMCMessage message) {
		try {
			//TODO new imc
			//			CompoundNBT tagCompound = message.getNBTValue();
			//			BlockState flowerState = NBTUtil.readBlockState(tagCompound);
			//			double weight = tagCompound.getDouble("weight");
			//			List<String> flowerTypes = new ArrayList<>();
			//			for (String key : tagCompound.getKeySet()) {
			//				if (key.contains("flowertype")) {
			//					flowerTypes.add(tagCompound.getString("flowertype"));
			//				}
			//			}
			//			FlowerManager.flowerRegistry.registerPlantableFlower(flowerState, weight, flowerTypes.toArray(new String[0]));
			return true;
		} catch (Exception e) {
			IMCUtil.logInvalidIMCMessage(message);
			return false;
		}
	}

	private boolean addAcceptableFlower(InterModComms.IMCMessage message) {
		try {
			//TODO new imc
			//			CompoundNBT tagCompound = message.getNBTValue();
			//			BlockState flowerState = NBTUtil.readBlockState(tagCompound);
			//			List<String> flowerTypes = new ArrayList<>();
			//			for (String key : tagCompound.getKeySet()) {
			//				if (key.contains("flowertype")) {
			//					flowerTypes.add(tagCompound.getString("flowertype"));
			//				}
			//			}
			//			FlowerManager.flowerRegistry.registerAcceptableFlower(flowerState, flowerTypes.toArray(new String[0]));
			return true;
		} catch (Exception e) {
			IMCUtil.logInvalidIMCMessage(message);
			return false;
		}
	}

	@Override
	public ISidedModuleHandler getModuleHandler() {
		return proxy;
	}

	private static class EndFlowerAcceptableRule implements IFlowerAcceptableRule {
		@Override
		public boolean isAcceptableFlower(BlockState blockState, World world, BlockPos pos, String flowerType) {
			Biome biomeGenForCoords = world.getBiome(pos);
			return Biome.Category.THEEND == biomeGenForCoords.getCategory();
		}
	}
}
