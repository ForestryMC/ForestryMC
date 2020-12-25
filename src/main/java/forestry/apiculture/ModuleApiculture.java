/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.apiculture;

import com.google.common.base.Preconditions;
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
import forestry.apiculture.features.ApicultureFeatures;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.flowers.FlowerRegistry;
import forestry.apiculture.genetics.*;
import forestry.apiculture.gui.*;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.network.PacketRegistryApiculture;
import forestry.apiculture.particles.ApicultureParticles;
import forestry.apiculture.proxy.ProxyApiculture;
import forestry.apiculture.proxy.ProxyApicultureClient;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.apiculture.villagers.RegisterVillager;
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    public static ProxyApiculture proxy;

    public static HiveRegistry getHiveRegistry() {
        Preconditions.checkNotNull(hiveRegistry);
        return hiveRegistry;
    }

    public ModuleApiculture() {
        proxy = DistExecutor.safeRunForDist(() -> ProxyApicultureClient::new, () -> ProxyApiculture::new);
        ForgeUtils.registerSubscriber(this);

        ApicultureParticles.PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        if (Config.enableVillagers) {
            RegisterVillager.Registers.POINTS_OF_INTEREST.register(modEventBus);
            RegisterVillager.Registers.PROFESSIONS.register(modEventBus);
            MinecraftForge.EVENT_BUS.register(new RegisterVillager.Events());
        }

        if (Config.getBeehivesAmount() > 0.0) {
            modEventBus.addGenericListener(Feature.class, ApicultureFeatures::registerFeatures);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ApicultureFeatures::onBiomeLoad);
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
        ScreenManager.registerFactory(
                ApicultureContainers.ALVEARY_HYGROREGULATOR.containerType(),
                GuiAlvearyHygroregulator::new
        );
        ScreenManager.registerFactory(ApicultureContainers.ALVEARY_SIEVE.containerType(), GuiAlvearySieve::new);
        ScreenManager.registerFactory(ApicultureContainers.ALVEARY_SWARMER.containerType(), GuiAlvearySwarmer::new);
        ScreenManager.registerFactory(
                ApicultureContainers.BEE_HOUSING.containerType(),
                GuiBeeHousing<ContainerBeeHousing>::new
        );
        ScreenManager.registerFactory(ApicultureContainers.HABITAT_LOCATOR.containerType(), GuiHabitatLocator::new);
        ScreenManager.registerFactory(ApicultureContainers.IMPRINTER.containerType(), GuiImprinter::new);
        ScreenManager.registerFactory(
                ApicultureContainers.BEEHOUSE_MINECART.containerType(),
                GuiBeeHousing<ContainerMinecartBeehouse>::new
        );
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

        secondPrincessChance = config.getFloatLocalized(
                "beekeeping",
                "second.princess",
                secondPrincessChance,
                0.0f,
                100.0f
        );

        maxFlowersSpawnedPerHive = config.getIntLocalized("beekeeping", "flowers.spawn", 20, 0, 1000);

        String[] blacklist = config.getStringListLocalized("species", "blacklist", Constants.EMPTY_STRINGS);
        parseBeeBlacklist(blacklist);

        ticksPerBeeWorkCycle = config.getIntLocalized("beekeeping", "ticks.work", 550, 250, 850);

        hivesDamageOnPeaceful = config.getBooleanLocalized("beekeeping.hivedamage", "peaceful", hivesDamageOnPeaceful);

        hivesDamageUnderwater = config.getBooleanLocalized(
                "beekeeping.hivedamage",
                "underwater",
                hivesDamageUnderwater
        );

        hivesDamageOnlyPlayers = config.getBooleanLocalized(
                "beekeeping.hivedamage",
                "onlyPlayers",
                hivesDamageOnlyPlayers
        );

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
        flowerRegistry.registerPlantableFlower(
                Blocks.BROWN_MUSHROOM.getDefaultState(),
                1.0,
                FlowerManager.FlowerTypeMushrooms
        );
        flowerRegistry.registerPlantableFlower(
                Blocks.RED_MUSHROOM.getDefaultState(),
                1.0,
                FlowerManager.FlowerTypeMushrooms
        );
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
                PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.HEALING)
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.fromStacks(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.CRYSTALLINE, 1)),
                PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.REGENERATION)
        );
    }

    private static void registerBeehiveDrops() {
        ItemStack honeyComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.HONEY, 1);
        HiveRegistry hiveRegistry = getHiveRegistry();

        hiveRegistry.addDrops(
                HiveType.FOREST.getHiveUid(),
                new HiveDrop(0.80, BeeDefinition.FOREST, honeyComb).setIgnobleShare(0.7),
                new HiveDrop(0.08, BeeDefinition.FOREST.getRainResist(), honeyComb),
                new HiveDrop(0.03, BeeDefinition.VALIANT, honeyComb)
        );

        hiveRegistry.addDrops(
                HiveType.MEADOWS.getHiveUid(),
                new HiveDrop(0.80, BeeDefinition.MEADOWS, honeyComb).setIgnobleShare(0.7),
                new HiveDrop(0.03, BeeDefinition.VALIANT, honeyComb)
        );

        ItemStack parchedComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.PARCHED, 1);
        hiveRegistry.addDrops(
                HiveType.DESERT.getHiveUid(),
                new HiveDrop(0.80, BeeDefinition.MODEST, parchedComb).setIgnobleShare(0.7),
                new HiveDrop(0.03, BeeDefinition.VALIANT, parchedComb)
        );

        ItemStack silkyComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.SILKY, 1);
        hiveRegistry.addDrops(
                HiveType.JUNGLE.getHiveUid(),
                new HiveDrop(0.80, BeeDefinition.TROPICAL, silkyComb).setIgnobleShare(0.7),
                new HiveDrop(0.03, BeeDefinition.VALIANT, silkyComb)
        );

        ItemStack mysteriousComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MYSTERIOUS, 1);
        hiveRegistry.addDrops(
                HiveType.END.getHiveUid(),
                new HiveDrop(0.90, BeeDefinition.ENDED, mysteriousComb)
        );

        ItemStack frozenComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.FROZEN, 1);
        hiveRegistry.addDrops(
                HiveType.SNOW.getHiveUid(),
                new HiveDrop(0.80, BeeDefinition.WINTRY, frozenComb).setIgnobleShare(0.5),
                new HiveDrop(0.03, BeeDefinition.VALIANT, frozenComb)
        );

        ItemStack mossyComb = ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.MOSSY, 1);
        hiveRegistry.addDrops(
                HiveType.SWAMP.getHiveUid(),
                new HiveDrop(0.80, BeeDefinition.MARSHY, mossyComb).setIgnobleShare(0.4),
                new HiveDrop(0.03, BeeDefinition.VALIANT, mossyComb)
        );
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
