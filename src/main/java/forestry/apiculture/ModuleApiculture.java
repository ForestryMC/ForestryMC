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
import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveRegistry.HiveType;
import forestry.api.genetics.flowers.IFlowerAcceptableRule;
import forestry.api.modules.ForestryModule;
import forestry.apiculture.commands.CommandBee;
import forestry.apiculture.features.ApicultureContainers;
import forestry.apiculture.features.ApicultureFeatures;
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
import forestry.apiculture.items.HabitatLocatorLogic;
import forestry.apiculture.network.PacketRegistryApiculture;
import forestry.apiculture.particles.ApicultureParticles;
import forestry.apiculture.proxy.ProxyApiculture;
import forestry.apiculture.proxy.ProxyApicultureClient;
import forestry.apiculture.villagers.RegisterVillager;
import forestry.apiculture.worldgen.HiveDescription;
import forestry.apiculture.worldgen.HiveGenHelper;
import forestry.apiculture.worldgen.HiveRegistry;
import forestry.core.ISaveEventHandler;
import forestry.core.ModuleCore;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.ForgeUtils;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.Log;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;

import genetics.api.GeneticsAPI;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.APICULTURE, name = "Apiculture", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.apiculture.description", lootTable = "apiculture")
public class ModuleApiculture extends BlankForestryModule {

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

	public static ProxyApiculture proxy;

	public static HiveRegistry getHiveRegistry() {
		Preconditions.checkNotNull(hiveRegistry);
		return hiveRegistry;
	}

	public ModuleApiculture() {
		proxy = DistExecutor.safeRunForDist(() -> ProxyApicultureClient::new, () -> ProxyApiculture::new);
		ForgeUtils.registerSubscriber(this);
		MinecraftForge.EVENT_BUS.register(HabitatLocatorLogic.class);

		ApicultureParticles.PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		if (Config.enableVillagers) {
			RegisterVillager.Registers.POINTS_OF_INTEREST.register(modEventBus);
			RegisterVillager.Registers.PROFESSIONS.register(modEventBus);
			MinecraftForge.EVENT_BUS.register(new RegisterVillager.Events());
		}

		modEventBus.addGenericListener(Feature.class, ApicultureFeatures::registerFeatures);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ApicultureFeatures::onBiomeLoad);
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
		MenuScreens.register(ApicultureContainers.ALVEARY.containerType(), GuiAlveary::new);
		MenuScreens.register(ApicultureContainers.ALVEARY_HYGROREGULATOR.containerType(), GuiAlvearyHygroregulator::new);
		MenuScreens.register(ApicultureContainers.ALVEARY_SIEVE.containerType(), GuiAlvearySieve::new);
		MenuScreens.register(ApicultureContainers.ALVEARY_SWARMER.containerType(), GuiAlvearySwarmer::new);
		MenuScreens.register(ApicultureContainers.BEE_HOUSING.containerType(), GuiBeeHousing<ContainerBeeHousing>::new);
		MenuScreens.register(ApicultureContainers.HABITAT_LOCATOR.containerType(), GuiHabitatLocator::new);
		MenuScreens.register(ApicultureContainers.IMPRINTER.containerType(), GuiImprinter::new);
		MenuScreens.register(ApicultureContainers.BEEHOUSE_MINECART.containerType(), GuiBeeHousing<ContainerMinecartBeehouse>::new);
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);

		// Commands
		ModuleCore.rootCommand.then(CommandBee.register());

		ApicultureFilterRuleType.init();
		ApicultureFilterRule.init();
	}

	@Override
	public void registerCapabilities(Consumer<Class<?>> consumer) {
		consumer.accept(IArmorApiarist.class);
	}

	@Override
	public void doInit() {
		initFlowerRegistry();

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
			flowerRegistry.registerPlantableFlower(standardFlower.defaultBlockState(), 1.0, standardTypes);
		}
		flowerRegistry.registerPlantableFlower(Blocks.BROWN_MUSHROOM.defaultBlockState(), 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.RED_MUSHROOM.defaultBlockState(), 1.0, FlowerManager.FlowerTypeMushrooms);
		flowerRegistry.registerPlantableFlower(Blocks.CACTUS.defaultBlockState(), 1.0, FlowerManager.FlowerTypeCacti);

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
	public void registerRecipes() {
		// BREWING RECIPES
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
				Ingredient.of(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.NORMAL, 1)),
				PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HEALING));
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
				Ingredient.of(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.CRYSTALLINE, 1)),
				PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.REGENERATION));
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
		float secondPrincessChance = 0;
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
		public boolean isAcceptableFlower(BlockState blockState, Level world, BlockPos pos, String flowerType) {
			Biome biomeGenForCoords = world.getBiome(pos).value();
			return Biome.BiomeCategory.THEEND == biomeGenForCoords.getBiomeCategory();
		}
	}
}
